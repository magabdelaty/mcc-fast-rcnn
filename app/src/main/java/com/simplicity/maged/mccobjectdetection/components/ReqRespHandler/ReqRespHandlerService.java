package com.simplicity.maged.mccobjectdetection.components.ReqRespHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.PowerProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.ProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngine;
import com.simplicity.maged.mccobjectdetection.components.contextManager.DeviceInfo;
import com.simplicity.maged.mccobjectdetection.components.contextManager.LocalResources;
import com.simplicity.maged.mccobjectdetection.components.executionManager.ExecutionEngine;
import com.simplicity.maged.mccobjectdetection.components.executionManager.ExecutionResult;

import edu.umich.PowerTutor.ui.PowerProfiler;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class ReqRespHandlerService extends Service {
    static String app_directory;
    static Context context;
    static long startTime;
    static long endTime;
    Server webServer;
    private Intent battt_profiler;

    static void sendNotFound(HttpServletResponse response, String path)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html");
        response.getWriter().println("URL [" + path + "] doesn't exist");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("simplicity",
                "ReqRespHandlerService: Received Start Foreground Intent");

        if (context == null) {
            context = getApplicationContext();
        }
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = activityManager
                .getRunningAppProcesses();
        int appUID = 0;
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase("com.simplicity.maged.mccobjectdetection")) {
                appUID = info.uid;
            }
        }
        battt_profiler = new Intent(context, PowerProfiler.class);
        battt_profiler.putExtra(PowerProfiler.PROFILEDUID, appUID);
        battt_profiler.putExtra(PowerProfiler.PROFILERCONTPROVURI,
                PowerProfilerContentProvider.CONTENT_URI.toString());
        context.startService(battt_profiler);

        // =========jetty=========
        if (app_directory == null) {
            app_directory = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/"
                    + getApplicationContext().getString(R.string.app_dir);
            if (!(new File(app_directory)).mkdir()) {
                Log.i("simplicity",
                        "ReqRespHandlerService: dir already existed");
            }
        }

        if (webServer == null) {
            webServer = new Server();
            final Connector connector = new SelectChannelConnector();
            connector.setPort(8080);
            connector.setMaxIdleTime(1000 * 60 * 60);
            // connector.setSoLingerTime(-1);
            webServer.setConnectors(new Connector[]{connector});
        }
        try {
            if (webServer.isStarted()) {
                Log.i("simplicity", "ReqRespHandlerService: isStarted");
            } else {
                ServletHandler handler = new ServletHandler();
                // Set the servlet to run.
                handler.addServletWithMapping(SendReceiveData.class, "/files/*");
                handler.addServletWithMapping(Discover.class,
                        "/service_discovery");
                handler.addServletWithMapping(Blur.class,
                        "/image_process/blur/*");
                handler.addServletWithMapping(Blend.class,
                        "/image_process/blend/*");
                handler.addServletWithMapping(MatrixMultiply.class,
                        "/arithmetic/matrix_multiply/*");
                handler.addServletWithMapping(PrimeFactorization.class,
                        "/arithmetic/prime_factors/*");
                handler.addServletWithMapping(BubbleSort.class,
                        "/arithmetic/bubble_sort/*");
                handler.addServletWithMapping(SortPrime.class,
                        "/arithmetic/sort_prime/*");
                handler.addServletWithMapping(NetProfile.class, "/netprofile/*");
                webServer.setHandler(handler);
                webServer.start();
                Log.i("simplicity", "ReqRespHandlerService: started");
            }
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                    new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    Log.i("simplicity",
                                            "ReqRespHandlerService: OpenCV loaded successfully");
                                }
                                break;
                                default: {
                                    super.onManagerConnected(status);
                                }
                                break;
                            }
                        }
                    });

        } catch (Exception e) {
            Log.e("simplicity",
                    "ReqRespHandlerService: unexpected exception starting Web server: "
                            + e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("simplicity", "ReqRespHandlerService: onDestroy");
        if (webServer != null && webServer.isStarted()) {
            try {
                webServer.stop();
                context.stopService(battt_profiler);
            } catch (Exception e) {
                Log.e("simplicity", "ReqRespHandlerService: onDestroy: " + e);
            }
        }
    }

    String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    String getFileExtenstion(String mimeType) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("serial")
    public static class Discover extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            try {
                InputStream is = context.getAssets().open(
                        "service_discovery.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");
                JSONObject obj = new JSONObject(json);
                LocalResources lcl = new LocalResources(context);
                int cpu = DeviceInfo.getCPUMaxFreqKHz() / 1000;
                obj.put("memoryAvail", lcl.avial_memory);
                obj.put("energyAvail", lcl.batt_level);
                obj.put("computeSpeed", cpu);
                buffer = obj.toString().getBytes("UTF-8");
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                OutputStream output = resp.getOutputStream();
                output.write(buffer);
                output.flush();
                output.close();
                Log.i("simplicity", "ReqRespHandlerService: Discover");
            } catch (Exception e) {
                Log.e("simplicity", "ReqRespHandlerService: " + e);
            }

        }
    }

    @SuppressWarnings("serial")
    public static class SendReceiveData extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String file_id = UUID.randomUUID().toString();
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                    req.getContentType());
            String file_name = file_id + "." + ext;
            String file_path = app_directory + "/" + file_name;
            File myFile = new File(file_path);
            myFile.createNewFile();
            // download the file
            InputStream input = new BufferedInputStream(req.getInputStream());
            OutputStream output = new FileOutputStream(myFile);
            byte data[] = new byte[1024];
            int count;
            startTime = System.nanoTime();
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            endTime = System.nanoTime();
            // ContextEngine.setUserUpBw((myFile.length() / 1024.0)
            // / ((endTime - startTime) / (1000 * 1000 * 1000)));
            Log.i("simplicity", "ReqRespHandlerService: upload done "
                    + file_name);
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().println(file_name);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String reqPath = req.getPathInfo();
            if (reqPath != null) {
                String file_path = app_directory + reqPath;
                File myFile = new File(file_path);
                if (myFile.exists()) {
                    String mime = MimeTypeMap
                            .getSingleton()
                            .getMimeTypeFromExtension(
                                    reqPath.substring(reqPath.lastIndexOf(".") + 1));
                    resp.setContentType(mime);
                    resp.setContentLength(Integer.parseInt(Long
                            .toString((myFile.length()))));
                    startTime = System.nanoTime();
                    InputStream input = new FileInputStream(myFile);
                    OutputStream output = resp.getOutputStream();
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    endTime = System.nanoTime();
                    // ContextEngine.setUserDownBw((myFile.length() / 1024.0)
                    // / ((endTime - startTime) / (1000 * 1000 * 1000)));
                    Log.i("simplicity", "ReqRespHandlerService: download done "
                            + reqPath);
                } else {
                    sendNotFound(resp, reqPath);
                }
            } else {
                sendNotFound(resp, reqPath);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class Blur extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    Log.i("simplicity",
                            "ReqRespHandlerService: Blur && Dropbox");
                    String file_path = reqPath[0].substring(1);
                    Log.i("simplicity",
                            "ReqRespHandlerService: Blur && Dropbox && "
                                    + reqPath[0]);
                    Log.i("simplicity",
                            "ReqRespHandlerService: Blur && Dropbox && "
                                    + file_path);
                    Log.i("simplicity",
                            "ReqRespHandlerService: Blur && Dropbox && "
                                    + Long.parseLong(reqPath[3]));
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.ImageBlurringService",
                                    false, new String[]{reqPath[1],
                                            reqPath[2]},
                                    new String[]{file_path}, true,
                                    app_directory, true,
                                    Long.parseLong(reqPath[3]));
                    endTime = System.nanoTime();
                } else {
                    String file_path = app_directory + reqPath[0];
                    File myFile = new File(file_path);
                    if (myFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file_path, options);

                        double totalFreeHeap = ContextEngine
                                .GetLocalResources(context).avial_memory * 1024;
                        // three channels
                        int matSize = options.outWidth * options.outHeight * 3;
                        Boolean lackOfResources = false;
                        if (totalFreeHeap < matSize * 2) {
                            // two images is saved in memory ??
                            lackOfResources = true;
                        }
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.ImageBlurringService",
                                        lackOfResources, new String[]{
                                                reqPath[1], reqPath[2]},
                                        new String[]{file_path}, false,
                                        app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms"
                            + "& ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==================================
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + " & "
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost = "
                            + Long.toString(duration) + " ms" + "& " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class Blend extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    Log.i("simplicity",
                            "ReqRespHandlerService: Blend && Dropbox");
                    String file_src_path = reqPath[0].substring(1);
                    String file_dst_path = reqPath[1];
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.ImageBlendingService",
                                    false,
                                    new String[]{reqPath[2], reqPath[3]},
                                    new String[]{file_src_path, file_dst_path},
                                    true,
                                    app_directory,
                                    true,
                                    Long.parseLong(reqPath[4]));
                    endTime = System.nanoTime();
                } else {
                    String file_src_path = app_directory + reqPath[0];
                    String file_dst_path = app_directory + "/" + reqPath[1];
                    File srcFile = new File(file_src_path);
                    File dstFile = new File(file_dst_path);
                    if (srcFile.exists() && dstFile.exists()) {
                        int x = Integer.parseInt(reqPath[2]);
                        int y = Integer.parseInt(reqPath[3]);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file_dst_path, options);

                        double totalFreeHeap = ContextEngine
                                .GetLocalResources(context).avial_memory * 1024;
                        // three channels
                        int matSize = options.outWidth * options.outHeight * 3;
                        Boolean lackOfResources = false;
                        if (totalFreeHeap < matSize * 4) {
                            lackOfResources = true;
                        }
                        if (x < 300 || y < 100) {
                            resp.setContentType("text/html");
                            resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                            resp.getWriter()
                                    .println(
                                            "Error: x & y are not fitting with the first image");
                            return;
                        }
                        if (options.outHeight < (y + 50)
                                || options.outWidth < (x + 150)) {
                            resp.setContentType("text/html");
                            resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                            resp.getWriter()
                                    .println(
                                            "Error: x & y are outside the second image boundaries");
                            return;
                        }
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.ImageBlendingService",
                                        lackOfResources, new String[]{
                                                reqPath[2], reqPath[3]},
                                        new String[]{file_src_path,
                                                file_dst_path}, false,
                                        app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms"
                            + " & ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // =========
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + " & "
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost= "
                            + Long.toString(duration) + " ms" + "& " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class MatrixMultiply extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    Log.i("simplicity",
                            "ReqRespHandlerService: MatrixMultiply && Dropbox");
                    String matrix1_path = reqPath[0].substring(1);
                    String matrix2_path = reqPath[1];
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.MatrixMultiplyService",
                                    false, new String[]{}, new String[]{
                                            matrix1_path, matrix2_path}, true,
                                    app_directory, true,
                                    Long.parseLong(reqPath[2]));
                    endTime = System.nanoTime();
                } else {
                    String matrix1_path = app_directory + reqPath[0];
                    String matrix2_path = app_directory + "/" + reqPath[1];
                    File matrix1File = new File(matrix1_path);
                    File matrix2File = new File(matrix2_path);
                    if (matrix1File.exists() && matrix2File.exists()) {
                        Log.i("simplicity",
                                "ReqRespHandlerService: MatrixMultiply");
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.MatrixMultiplyService",
                                        false, new String[]{}, new String[]{
                                                matrix1_path, matrix2_path},
                                        false, app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0] + "" + reqPath[1]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms"
                            + " & ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==================================
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + "&"
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost= "
                            + Long.toString(duration) + " ms" + "& " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class PrimeFactorization extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    Log.i("simplicity",
                            "ReqRespHandlerService: PrimeFactorization && Dropbox");
                    String file_path = reqPath[0].substring(1);
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.PrimeFactorsService",
                                    false, new String[]{},
                                    new String[]{file_path}, true,
                                    app_directory, true, Long.parseLong(reqPath[1]));
                    endTime = System.nanoTime();
                } else {
                    String file_path = app_directory + reqPath[0];
                    File myFile = new File(file_path);
                    if (myFile.exists()) {
                        Log.i("simplicity",
                                "ReqRespHandlerService: PrimeFactorization");
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.PrimeFactorsService",
                                        false, new String[]{},
                                        new String[]{file_path}, false,
                                        app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0] + "" + reqPath[1]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms "
                            + "& ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==================================
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + "&"
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost= "
                            + Long.toString(duration) + " ms " + "& " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class BubbleSort extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    Log.i("simplicity",
                            "ReqRespHandlerService: BubbleSort && Dropbox");
                    String file_path = reqPath[0].substring(1);
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.BubbleSortService",
                                    false, new String[]{},
                                    new String[]{file_path}, true,
                                    app_directory, true, Long.parseLong(reqPath[1]));
                    endTime = System.nanoTime();
                } else {
                    String file_path = app_directory + reqPath[0];
                    File myFile = new File(file_path);
                    if (myFile.exists()) {
                        Log.i("simplicity", "ReqRespHandlerService: BubbleSort");
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.BubbleSortService",
                                        false, new String[]{},
                                        new String[]{file_path}, false,
                                        app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0] + "" + reqPath[1]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms"
                            + " & ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==================================
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + "&"
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost= "
                            + Long.toString(duration) + " ms" + " & " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class SortPrime extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String[] reqPath = req.getPathInfo().split("&");
            if (reqPath != null) {
                ExecutionResult execResult = null;
                if (reqPath[0].contains("dropboxusercontent.com")) {
                    String file_path = reqPath[0].substring(1);
                    Log.i("simplicity",
                            "ReqRespHandlerService: SortPrime && Dropbox");
                    startTime = endTime = System.nanoTime();
                    execResult = ExecutionEngine
                            .executeService(
                                    context,
                                    "com.simplicity.maged.mccobjectdetection.services.SortPrimeService",
                                    false, new String[]{},
                                    new String[]{file_path}, true,
                                    app_directory, true, Long.parseLong(reqPath[1]));
                    endTime = System.nanoTime();
                } else {
                    String file_path = app_directory + reqPath[0];
                    File myFile = new File(file_path);
                    if (myFile.exists()) {
                        Log.i("simplicity", "ReqRespHandlerService: SortPrime");
                        startTime = endTime = System.nanoTime();
                        execResult = ExecutionEngine
                                .executeService(
                                        context,
                                        "com.simplicity.maged.mccobjectdetection.services.SortPrimeService",
                                        false, new String[]{},
                                        new String[]{file_path}, false,
                                        app_directory, true, 0);
                        endTime = System.nanoTime();
                    } else {
                        sendNotFound(resp, reqPath[0] + "" + reqPath[1]);
                    }
                }
                if (execResult != null
                        && execResult.result == Activity.RESULT_OK) {
                    long duration = (endTime - startTime) / 1000000;
                    // ==================
                    Cursor c = context.getContentResolver().query(
                            ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.COLUMNS, null, null, null);
                    while (!c.moveToLast()) {
                        c.close();
                        c = context.getContentResolver().query(
                                ProfilerContentProvider.CONTENT_URI,
                                ProfilerContentProvider.COLUMNS, null, null,
                                null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    String ss = c
                            .getString(c
                                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                            + "& commCost= "
                            + execResult.commCost
                            + " ms"
                            + " & ComputationCost= "
                            + c.getString(c
                            .getColumnIndex(ProfilerContentProvider.ExecCost))
                            + " ms";
                    // ==================================
                    String result = execResult.resultFilePath
                            .substring(execResult.resultFilePath
                                    .lastIndexOf("/") + 1)
                            + " & "
                            + (new File(execResult.resultFilePath)).length()
                            / 1024
                            + " KB& totalCost= "
                            + Long.toString(duration) + " ms " + "& " + ss;
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(result);
                } else if (execResult != null
                        && execResult.resultFilePath.startsWith("Error: ")) {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println(execResult.resultFilePath);
                } else {
                    resp.setContentType("text/html");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().println("Bad Request");
                }
            } else {
                sendNotFound(resp, null);
            }
        }
    }

    @SuppressWarnings("serial")
    public static class NetProfile extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String reqPath = req.getPathInfo();
            if (reqPath != null) {
                if (reqPath.equalsIgnoreCase("/ssmountain500.jpg")) {
                    String mime = MimeTypeMap
                            .getSingleton()
                            .getMimeTypeFromExtension(
                                    reqPath.substring(reqPath.lastIndexOf(".") + 1));
                    resp.setContentType(mime);
                    InputStream input = context.getAssets().open(
                            "ssmountain500.jpg");
                    OutputStream output = resp.getOutputStream();
                    byte data[] = new byte[1024];
                    int count, file_length = 0;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        file_length += count;
                    }
                    resp.setContentLength(file_length);
                    output.flush();
                    output.close();
                    input.close();

                    Log.i("simplicity", "ReqRespHandlerService: download done "
                            + reqPath);
                } else if (reqPath.contains("&")) {
                    try {
                        ContextEngine.setUserUpBw(Double.parseDouble(reqPath
                                .substring(reqPath.lastIndexOf("/") + 1).split(
                                        "&")[0]));
                        Log.i("simplicity",
                                "ReqRespHandlerService: ContextEngine.setUserUpBw: "
                                        + Double.parseDouble(reqPath.substring(
                                        reqPath.lastIndexOf("/") + 1)
                                        .split("&")[0]));
                        ContextEngine.setUserDownBw(Double.parseDouble(reqPath
                                .substring(reqPath.lastIndexOf("/") + 1).split(
                                        "&")[1]));
                        Log.i("simplicity",
                                "ReqRespHandlerService: ContextEngine.setUserDownBw: "
                                        + Double.parseDouble(reqPath.substring(
                                        reqPath.lastIndexOf("/") + 1)
                                        .split("&")[1]));
                    } catch (Exception e) {
                        Log.e("simplicity",
                                "ReqRespHandlerService: NetProfile: " + e);
                    }
                } else {

                    sendNotFound(resp, reqPath);
                }
            } else {
                sendNotFound(resp, reqPath);
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            // download the file
            InputStream input = new BufferedInputStream(req.getInputStream());
            byte data[] = new byte[1024];
            while (input.read(data) != -1) {
            }
            input.close();
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

}
