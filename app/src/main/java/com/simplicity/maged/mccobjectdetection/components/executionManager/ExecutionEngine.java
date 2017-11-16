package com.simplicity.maged.mccobjectdetection.components.executionManager;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.DownloadManager;
import com.simplicity.maged.mccobjectdetection.components.Profiler;
import com.simplicity.maged.mccobjectdetection.components.ProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.RESTClient;
import com.simplicity.maged.mccobjectdetection.components.UploadManager;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngine;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextManagerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.contextManager.EnvironmentContext;
import com.simplicity.maged.mccobjectdetection.components.contextManager.LocalContext;
import com.simplicity.maged.mccobjectdetection.components.contextManager.LocalResources;
import com.simplicity.maged.mccobjectdetection.components.contextManager.Provider;
import com.simplicity.maged.mccobjectdetection.components.contextManager.Providers;
import com.simplicity.maged.mccobjectdetection.components.contextManager.UserPreferences;
import com.simplicity.maged.mccobjectdetection.components.decisionModel.AppService;
import com.simplicity.maged.mccobjectdetection.components.decisionModel.OffloadingDecision;
import com.simplicity.maged.mccobjectdetection.components.decisionModel.SelectedPlan;
import com.simplicity.maged.mccobjectdetection.components.executionPlans.Plans;
import com.simplicity.maged.mccobjectdetection.components.logger.SimplicityLogger;

public class ExecutionEngine {
    static ExecutionResult execResult;
    static Boolean flagRetryBW;
    static Profiler profiler;
    static String TAG = "ExecutionEngine";

    public static ExecutionResult executeService(Context context,
                                                 String localServiceRequest, Boolean lackOfResources,
                                                 String[] serviceRequestParams, String[] inputFiles,
                                                 Boolean dataProvider, String app_directory,
                                                 Boolean serviceProvMode, long dataProvFileLength) {
        profiler = new Profiler();
        profiler.execute(context, inputFiles, dataProvider);
        //new Profiler().execute(context, inputFiles, dataProvider);
        ArrayList<String> servicesLocal = new ArrayList<String>();
        String cloudServiceRequest = "";
        ArrayList<String> servicesCloud = new ArrayList<String>();
        double upDataSize = 0;
        flagRetryBW = false;
        execResult = new ExecutionResult();
        String resultstring;
        SimplicityLogger.initLog();
        long startTime = System.nanoTime();
        try {
            LocalResources lcl = ContextEngine.GetLocalResources(context);
            // ============
            Log.i(TAG, "ExecutionEngineService: Profiler profiler;");
            for (int i = 0; i < inputFiles.length; i++) {
                Log.i(TAG, String.valueOf(inputFiles[i]));
                File f = new File(String.valueOf(inputFiles[i]));
                upDataSize += f.length() / 1024;
            }
            if (dataProvider) {
                upDataSize = dataProvFileLength / 1024;
            }
            AppService myService = AppService.getAppService(context,
                    localServiceRequest);
            if (myService.leafs.size() > 0) {
                for (String ss : myService.leafs) {
                    servicesLocal.add(ss);
                    Cursor c = context
                            .getContentResolver()
                            .query(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
                                    ContextManagerContentProvider.SERVICE_DISCOVERY_COLUMNS,
                                    ContextManagerContentProvider.SERVICE_DISCOVERY_ANDROID_SERVICE
                                            + "=?", new String[]{ss}, null);
                    if (c.moveToFirst()) {
                        Log.i(TAG,
                                "ExecutionEngineService: c.moveToFirst()");
                        servicesCloud
                                .add(c.getString(c
                                        .getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_CLOUD_SERVICE)));
                    } else {
                        servicesCloud.add("");
                    }
                    c.close();
                }

                for (int i = 0; i < servicesLocal.size(); i++) {
                    if (i == 0) {
                        resultstring = ExecutionEngine.decisionAndProcessing(
                                context, servicesLocal.get(i), lackOfResources,
                                servicesCloud.get(i), serviceRequestParams,
                                inputFiles, dataProvider, upDataSize,
                                execResult.resultFilePath, lcl, app_directory,
                                serviceProvMode);
                        execResult.resultFilePath = resultstring.split("&&")[0];
                        execResult.responseTime = Long.parseLong(resultstring
                                .split("&&").length > 1 ? resultstring
                                .split("&&")[1] : "0");
                    } else {
                        resultstring = ExecutionEngine.decisionAndProcessing(
                                context, servicesLocal.get(i), lackOfResources,
                                servicesCloud.get(i), serviceRequestParams,
                                new String[]{execResult.resultFilePath},
                                dataProvider, upDataSize,
                                execResult.resultFilePath, lcl, app_directory,
                                serviceProvMode);
                        long endTime = System.nanoTime();
                        execResult.resultFilePath = resultstring;
                        execResult.responseTime = (endTime - startTime)
                                / (1000 * 1000);
                    }
                }

            } else {
                // myService.leafs.size() = 0

                Cursor c = context
                        .getContentResolver()
                        .query(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
                                ContextManagerContentProvider.SERVICE_DISCOVERY_COLUMNS,
                                ContextManagerContentProvider.SERVICE_DISCOVERY_ANDROID_SERVICE
                                        + "=?",
                                new String[]{localServiceRequest}, null);
                if (c.moveToFirst()) {
                    Log.i(TAG,
                            "ExecutionEngineService: c.moveToFirst()");
                    cloudServiceRequest = c
                            .getString(c
                                    .getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_CLOUD_SERVICE));
                }
                c.close();
                resultstring = decisionAndProcessing(context,
                        localServiceRequest, lackOfResources,
                        cloudServiceRequest, serviceRequestParams, inputFiles,
                        dataProvider, upDataSize, execResult.resultFilePath,
                        lcl, app_directory, serviceProvMode);
                long endTime = System.nanoTime();
                execResult.resultFilePath = resultstring;
                execResult.responseTime = (endTime - startTime) / (1000 * 1000);
                profiler.upDataSize = upDataSize;
                profiler.totalCost = execResult.responseTime
                        - execResult.commCost;
                profiler.startLocalCost = startTime;
                profiler.endLocalCost = endTime;
                profiler.cancel(false);
                while (profiler.cancelled != null
                        && profiler.cancelled == false) {

                }
            }
            Log.i(TAG, "execResult.responseTime: "
                    + execResult.responseTime);
            // the most important logger line
            SimplicityLogger.appendLine(","
                    + execResult.commCost + ","
                    + execResult.costCloudExecution
                    + ","
                    + execResult.costMobileExecution
                    + "," + execResult.downDataSize
                    + "," + execResult.responseTime
                    + ","
                    + String.format("%.4f", execResult.monetaryCost));
            StringBuilder logFilePath = new StringBuilder(
                    app_directory
                            + "/Log/"
                            + localServiceRequest.substring(localServiceRequest
                            .lastIndexOf(".") + 1)
                            + "_"
                            + (UserPreferences.getSimple_Decision(context) ? "SimpleDecision"
                            : "SelectPlan")
                            + "_"
                            + (ContextEngine.getMobileInternet() ? "3G"
                            : ContextEngine.getWifi() ? "WiFi"
                            : "NoConnection"));
            for (String inputfile : inputFiles) {
                logFilePath
                        .append("_"
                                + inputfile.substring(inputfile
                                .contains("dropbox") ? (inputfile
                                .lastIndexOf("%2F") + 3) : (inputfile
                                .lastIndexOf("/") + 1)));
            }
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            logFilePath.append("_" + df.format(cal.getTime()));
            logFilePath.append(".txt");
            SimplicityLogger.writeLog(logFilePath.toString());
            return execResult;
        } catch (Exception e) {
            profiler.costMobile = 0;
            profiler.downDataSize = 0;
            profiler.cancel(false);
            Log.e(TAG, e.toString());
            execResult.resultFilePath = "Error: " + e.toString();
            return execResult;
        }
    }

    private static String decisionAndProcessing(Context context,
                                                String localServiceRequest, Boolean lackOfResources,
                                                String cloudServiceRequest, String[] serviceRequestParams,
                                                String[] inputFiles, Boolean dataProvider, double upDataSize,
                                                String resultFilePath, LocalResources lcl, String app_directory,
                                                Boolean serviceProvMode) throws InterruptedException {

        ServiceCost costLocal_profiled = ExecutionEngine.findServiceCost(
                context, upDataSize, localServiceRequest, dataProvider, null)[0];
        double downDataLocal_profiled = costLocal_profiled.downData;
        Log.i(TAG, "ExecutionEngine: costLocal_profiled: "
                + costLocal_profiled.execCost);
        double costResume = 1000; // 1000 milliseconds based on CloneCloud
        double costSuspend = 1000; // 1000 milliseconds based on CloneCloud
        Boolean autOffloading = UserPreferences
                .getAutomatic_offloading(context);
        Boolean forceLocal = UserPreferences.getForce_local(context);
        Boolean forceOffloading = UserPreferences.getForce_offloading(context);
        SelectedPlan sPlan = new SelectedPlan();
        ArrayList<Provider> providers = Providers.getProviders();
        Provider sProvider = new Provider();
        // ==========
        // profiler.startLocalCost = System.nanoTime();
        // ===========
        if (ContextEngine.getInternetConnected()) {
            // Connected to the internet
            Log.i(TAG, "ExecutionEngine:"
                    + " Connected to the internet");

            Boolean offloadingDecision = false;

            if (!cloudServiceRequest.trim().isEmpty()) {
                ServiceCost[] serviceCloudProfile = ExecutionEngine
                        .findServiceCost(context, upDataSize,
                                cloudServiceRequest, dataProvider,
                                providers);
                double[] costCloud_profiled = new double[providers.size()];
                double downDataCloud_profiled = 0;
                for (int i = 0; i < costCloud_profiled.length; i++) {
                    costCloud_profiled[i] = serviceCloudProfile[i].execCost / 1000;
                    downDataCloud_profiled += serviceCloudProfile[i].downData;
                }
                downDataCloud_profiled /= serviceCloudProfile.length;
                downDataCloud_profiled = downDataCloud_profiled > 0 ? downDataCloud_profiled
                        : costLocal_profiled.downData;

                LocalContext lContext = ContextEngine
                        .getLocalContext(context);
                EnvironmentContext[] eContext = ContextEngine
                        .getEnvironmentContext(context);
                //==========
                sProvider = providers.get(0);
                if (ContextEngine.getMobileInternet()) {
                    offloadingDecision = OffloadingDecision.getObjDet3GDecision(
                            costLocal_profiled.totalCost / 1000, lContext.pCompute, upDataSize, downDataCloud_profiled,
                            eContext[0].upBW3, lContext.pTransmit, lContext.pIdeal,
                            lContext.computeSpeed, eContext[0].computeSpeed);
                } else if (ContextEngine.getWifi()) {
                    offloadingDecision = OffloadingDecision.getObjDetWiFiDecision(
                            costLocal_profiled.totalCost / 1000, lContext.pCompute, upDataSize, downDataCloud_profiled,
                            eContext[0].upBW3, eContext[0].downBW3, lContext.pTransmit, lContext.pIdeal,
                            lContext.computeSpeed, eContext[0].computeSpeed);
                }

            }

            if (offloadingDecision && !forceLocal) {

                // Remote Execution
                Log.i(TAG,
                        "ExecutionEngine: Remote Execution//"
                                + " // Offloading is Feasible// Automatic Offloading AND Connected to Internet");
                profiler.serviceName = cloudServiceRequest;
                profiler.provider_uuid = sProvider.id;
                try {
                    resultFilePath = ExecutionEngine.remoteExecute(context,
                            inputFiles, cloudServiceRequest,
                            serviceRequestParams, dataProvider, sProvider,
                            costLocal_profiled.execCost, costResume,
                            costSuspend, forceOffloading, app_directory,
                            upDataSize);
                    profiler.downDataSize = execResult.downDataSize;
                    profiler.costCloud = execResult.costCloudExecution;
                    // profiler.endLocalCost = System.nanoTime();
                    if (resultFilePath.startsWith("Error: ")
                            || resultFilePath.equals("")) {
                        if (flagRetryBW) {
                            // Remote Execution Failed due to small BW and
                            // There's Local
                            // Resources, then: Retry Processing
                            // xx This shouldn't happen when data provider??
                            // xx &&!dataProvider ??
                            ExecutionEngine.decisionAndProcessing(context,
                                    localServiceRequest, lackOfResources,
                                    cloudServiceRequest, serviceRequestParams,
                                    inputFiles, dataProvider, upDataSize,
                                    resultFilePath, lcl, app_directory,
                                    serviceProvMode);
                        }
                    } else {
                        execResult.result = Activity.RESULT_OK;
                    }
                } catch (Exception e) {
                    profiler.cancel(false);
                    Log.e(TAG, e.toString());
                }

            } else if (!lackOfResources) {

                // Offloading is not feasible then Local Execution
                Log.i(TAG, "ExecutionEngineService: Offloading is not feasible So Local Execution//"
                        + " Automatic Offloading AND Connected to Internet");
                profiler.serviceName = localServiceRequest;
                profiler.provider_uuid = null;
                try {
                    resultFilePath = ExecutionEngine.localExecute(context,
                            localServiceRequest, inputFiles,
                            serviceRequestParams, resultFilePath, dataProvider,
                            app_directory);
                    if (resultFilePath.startsWith("Error: ")) {
                        profiler.costMobile = 0;
                        profiler.downDataSize = 0;
                    } else {
                        execResult.result = Activity.RESULT_OK;
                        profiler.costMobile = execResult.costMobileExecution;
                        profiler.downDataSize = execResult.downDataSize;
                    }
                    // profiler.endLocalCost = System.nanoTime();
                } catch (Exception e) {
                    profiler.costMobile = 0;
                    profiler.downDataSize = 0;
                    Log.e(TAG, e.toString());
                }
            } else {
                resultFilePath = "Error: Can't Offload and No local resources!!";
            }
        } else if (!lackOfResources
                && (UserPreferences.getForce_local(context) || !ContextEngine
                .getInternetConnected())) {

            // Local resources are enough AND Force Local Execution OR Not Connected to Cloud

            Log.i(TAG,
                    "ExecutionEngineService: Local resources are enough AND Force Local Execution OR Not "
                            + "Connected to Internet");
            profiler.serviceName = localServiceRequest;
            profiler.provider_uuid = null;
            try {
                resultFilePath = ExecutionEngine.localExecute(context,
                        localServiceRequest, inputFiles, serviceRequestParams,
                        resultFilePath, dataProvider, app_directory);
                if (resultFilePath.startsWith("Error: ")) {
                    profiler.costMobile = 0;
                    profiler.downDataSize = 0;
                } else {
                    execResult.result = Activity.RESULT_OK;
                    profiler.costMobile = execResult.costMobileExecution;
                    profiler.downDataSize = execResult.downDataSize;
                }
                // profiler.endLocalCost = System.nanoTime();
            } catch (Exception e) {
                profiler.costMobile = 0;
                profiler.downDataSize = 0;
                profiler.cancel(false);
                Log.e(TAG, e.toString());
            }
        } else if (lackOfResources) {
            resultFilePath = "Error: Not Connected to the cloud & Local resources is not enough!!";
            profiler.costMobile = 0;
            profiler.downDataSize = 0;
            profiler.cancel(false);
        }

        return resultFilePath;
    }

    private static String localExecute(Context context,
                                       String localServiceRequest, String[] inputFiles,
                                       String[] serviceRequestParams, String resultFilePath,
                                       Boolean dataProvider, String app_directory) {
        try {
            Class<?> localClass = Class.forName(localServiceRequest);
            Method[] validMethods = localClass.getMethods();
            for (Method method : validMethods) {
                if (method.getName().equals("Execute")) {
                    Object[] o;
                    o = (Object[]) method.invoke(null, new Object[]{
                            inputFiles[0], context});
                    resultFilePath = String.valueOf(o[0]);
                    if (resultFilePath.startsWith("Error: ")) {
                        throw new Exception(resultFilePath.replace(
                                "Error: ", ""));
                    }
                    execResult.costMobileExecution = Double.valueOf(String
                            .valueOf(o[1]));
                    Log.i(TAG, "ExecutionEngineService: "
                            + resultFilePath);
                    break;
                }
            }
            execResult.downDataSize = (new File(resultFilePath)).length() / 1024.0;
            return resultFilePath;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "Error: " + e.toString();
        }
    }

    private static String remoteExecute(Context context, String[] inputPaths,
                                        String cloudServiceName, String[] cloudServiceParams,
                                        Boolean dataProvider, Provider provider, double costLocal,
                                        double costResume, double costSuspend, Boolean forceOffloading,
                                        String app_directory, double upDataSize) {
        try {

            List<String> cloudFileNames = new ArrayList<>();
            long commCost = System.nanoTime();
            execResult.commCost = 0;
            execResult.monetaryCost = 0;
            double costPerGB = Double.parseDouble(context
                    .getString(R.string.costPerGB));
            for (int i = 0; i < inputPaths.length; i++) {
                if (dataProvider) {
                    cloudFileNames.add(UploadManager.UploadDropBox(provider.url
                            + "files_dropbox", inputPaths[i]));
                    Log.i(TAG, "Data Provider cloudFileNames: "
                            + cloudFileNames.get(i));
                } else {
                    String contentType = ExecutionEngine.getMimeType(context,
                            inputPaths[i]);
                    cloudFileNames.add(UploadManager.Upload(inputPaths[i],
                            provider.url + "files", contentType));
                    Log.i(TAG, "Local Data cloudFileNames: "
                            + cloudFileNames.get(i));
                }

            }
            execResult.commCost += (System.nanoTime() - commCost) / 1000000;
            if (!dataProvider) {
                ContextEngine.setUpBW((upDataSize)
                        / (execResult.commCost / 1000.0));
                execResult.monetaryCost += costPerGB
                        * (upDataSize / 1024.0 / 1024.0);
            }
            String retString = null;
            long costCloudStart = System.nanoTime();
            if (!cloudFileNames.isEmpty()) {
                String serviceUrl = provider.url + cloudServiceName;
                for (int i = 0; i < cloudFileNames.size(); i++) {
                    serviceUrl += cloudFileNames.get(i);
                    if (i != cloudFileNames.size() - 1) {
                        serviceUrl += "&";
                    }
                }
                if (cloudServiceParams.length > 0) {
                    for (String param : cloudServiceParams) {
                        serviceUrl += "&" + param;
                    }
                }
                // 5 * 60 seconds
                retString = RESTClient.GetStringRequest(context, serviceUrl,
                        5 * 60);
            }
            long costCloudEnd = System.nanoTime();
            String cloudResultFileName = null;
            if (!retString.startsWith("Error: ")) {
                Log.i(TAG, "cloudResultFileName: " + retString);
                cloudResultFileName = retString.split("&")[0];
                // ============================
                execResult.downDataSize = Double.parseDouble(retString
                        .split("&")[1]); // KB
                /*
                 * execResult.costCloudExecution = Double.parseDouble(retString
				 * .split("&")[2]); // milliseconds
				 */
                execResult.costCloudExecution = (costCloudEnd - costCloudStart) / 1000000; // ms
                execResult.monetaryCost += Double.parseDouble(retString
                        .split("&")[3]);
                SimplicityLogger.appendLine(","
                        + retString.split("&")[3]);
                double downBW = ContextEngine.getDownBw();
                if (downBW == 0) {
                    execResult.commCost = 0;
                    return "Error: " + "Cannot measure download bandwidth";
                }
                double costContinue = costResume + execResult.downDataSize
                        / downBW;
                double costTerminate = costResume + costLocal;
                // costLocal == 0 means No profiler record
                if (!forceOffloading && costLocal > 0
                        && (costContinue > costTerminate)) {
                    execResult.commCost = 0;
                    flagRetryBW = true;
                    return "Error: " + "Download Bandwidth is very small.";
                }
                // =============================
                String fResult = app_directory + "/" + cloudResultFileName;
                commCost = System.nanoTime();
                if (DownloadManager.Download(provider.url + "files/"
                        + cloudResultFileName, fResult)) {
                    commCost = (System.nanoTime() - commCost) / 1000000;
                    long resultFileLength = (new File(fResult)).length();
                    ContextEngine.setDownBW((resultFileLength / 1024.0)
                            / (commCost / 1000.0));
                    execResult.commCost += commCost;
                    execResult.monetaryCost += costPerGB
                            * (resultFileLength / 1024.0 / 1024.0 / 1024.0);
                    return fResult;
                }
            } else {
                execResult.commCost = 0;
                return retString;
            }
        } catch (Exception e) {
            execResult.commCost = 0;
            Log.e(TAG, e.toString());
            return "Error: " + e.toString();
        }
        return "";
    }

    private static String getMimeType(Context context, String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url)
                .toLowerCase();
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    extension);
        }
        if (type == null && extension.equalsIgnoreCase("ksh")) {
            type = "text/plain";
        }
        return type;
    }

    private static ServiceCost[] findServiceCost(Context context,
                                                 double upDataSize, String ServiceName, Boolean dataProvider,
                                                 ArrayList<Provider> providers) {

        ServiceCost[] sCost = new ServiceCost[providers == null ? 1 : providers
                .size()];

        for (int i = 0; i < sCost.length; i++) {
            Cursor c1 = context.getContentResolver().query(
                    ProfilerContentProvider.CONTENT_URI,
                    ProfilerContentProvider.COLUMNS,
                    ProfilerContentProvider.ServiceName + " ==? AND "
                            + ProfilerContentProvider.UPDataSize + " ==? "
                            + " AND " + ProfilerContentProvider.DataProvider
                            + " ==? AND "
                            + ProfilerContentProvider.PROVIDERUUID + " ==?",
                    new String[]{
                            ServiceName,
                            String.valueOf(upDataSize),
                            String.valueOf(dataProvider ? 1 : 0),
                            providers == null ? "" : providers.get(i).id
                                    .toString()},
                    ProfilerContentProvider.Col_ID + " DESC");

            sCost[i] = new ServiceCost();
            sCost[i].execCost = 0;
            sCost[i].downData = 0;
            sCost[i].provider_uuid = providers == null ? null : providers
                    .get(i).id;
            if (c1.moveToFirst()) {
                Log.i(TAG, "ProfilerContentProvider: c.moveToFirst()");
                sCost[i].execCost = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.ExecCost));
                sCost[i].totalCost = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.TotalExecCost));
                sCost[i].downData = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.DownDataSize));
                sCost[i].costMemory = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.MemRequired));

            }
            c1.close();
        }

        return sCost;
    }

    private static ServiceCost[] findServiceCost_(Context context,
                                                  double upDataSize, String ServiceName, Boolean dataProvider,
                                                  ArrayList<Provider> providers) {

        ServiceCost[] sCost = new ServiceCost[providers == null ? 1 : providers
                .size()];

        for (int i = 0; i < sCost.length; i++) {
            Cursor c1;
            if (upDataSize <= 500) {
                c1 = context.getContentResolver().query(
                        ProfilerContentProvider.CONTENT_URI,
                        ProfilerContentProvider.COLUMNS,
                        ProfilerContentProvider.ServiceName + "==? AND "
                                + ProfilerContentProvider.UPDataSize
                                + " <= 500 AND "
                                + ProfilerContentProvider.DataProvider
                                + "==?  AND "
                                + ProfilerContentProvider.PROVIDERUUID + "==?",
                        new String[]{
                                ServiceName,
                                String.valueOf(dataProvider ? 1 : 0),
                                providers == null ? "" : providers.get(i).id
                                        .toString()},
                        ProfilerContentProvider.Col_ID + " DESC");
            } else if (500 < upDataSize && upDataSize < 1024) {
                c1 = context.getContentResolver().query(
                        ProfilerContentProvider.CONTENT_URI,
                        ProfilerContentProvider.COLUMNS,
                        ProfilerContentProvider.ServiceName + "==? AND "
                                + ProfilerContentProvider.UPDataSize
                                + " > 500 AND "
                                + ProfilerContentProvider.UPDataSize
                                + " < 1024 AND "
                                + ProfilerContentProvider.DataProvider
                                + "==?  AND "
                                + ProfilerContentProvider.PROVIDERUUID + "==?",
                        new String[]{
                                ServiceName,
                                String.valueOf(dataProvider ? 1 : 0),
                                providers == null ? "" : providers.get(i).id
                                        .toString()},
                        ProfilerContentProvider.Col_ID + " DESC");
            } else {
                c1 = context.getContentResolver().query(
                        ProfilerContentProvider.CONTENT_URI,
                        ProfilerContentProvider.COLUMNS,
                        ProfilerContentProvider.ServiceName + "==? AND "
                                + ProfilerContentProvider.UPDataSize
                                + " >= 1024 AND "
                                + ProfilerContentProvider.DataProvider
                                + "==?  AND "
                                + ProfilerContentProvider.PROVIDERUUID + "==?",
                        new String[]{
                                ServiceName,
                                String.valueOf(dataProvider ? 1 : 0),
                                providers == null ? "" : providers.get(i).id
                                        .toString()},
                        ProfilerContentProvider.Col_ID + " DESC");
            }
            sCost[i] = new ServiceCost();
            sCost[i].execCost = 0;
            sCost[i].downData = 0;
            sCost[i].provider_uuid = providers == null ? null : providers
                    .get(i).id;
            if (c1.moveToFirst()) {
                Log.i(TAG, "ProfilerContentProvider: c.moveToFirst()");
                sCost[i].execCost = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.ExecCost));
                sCost[i].downData = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.DownDataSize));
                sCost[i].costMemory = c1.getDouble(c1
                        .getColumnIndex(ProfilerContentProvider.MemRequired));
            }
            c1.close();
        }

        return sCost;
    }
}
