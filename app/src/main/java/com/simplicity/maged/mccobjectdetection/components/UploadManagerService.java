package com.simplicity.maged.mccobjectdetection.components;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UploadManagerService extends IntentService {

    public static final String RESULT = "result";
    public static final String URL = "url"; // link to the upload page on the
    // server
    public static final String CONTENTTYPE = "bodycontenttype";// standard data
    // type
    public static final String UPLOADFILE = "uploadfile";// path of file to
    // upload
    public static final String SERVERPATH = "serverpath";// path in server after
    // upload
    public static final String NOTIFICATION = "com.simplicity.maged.mccobjectdetection.components.UploadManagerService";
    private int result = Activity.RESULT_CANCELED;

    public UploadManagerService() {
        super("UploadManagerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlString = intent.getStringExtra(UploadManagerService.URL);
        String filePath = intent
                .getStringExtra(UploadManagerService.UPLOADFILE);
        String contentType = intent
                .getStringExtra(UploadManagerService.CONTENTTYPE);
        URL connectURL;
        FileInputStream fileInputStream;
        String response = "";
        String lineEnd = "\r\n";
        String Tag = "simplicity";
        try {
            fileInputStream = new FileInputStream(filePath);
            connectURL = new URL(urlString);
            Log.i(Tag, "Starting Http File Sending to URL");

            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) connectURL
                    .openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", contentType);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);

            // close streams
            fileInputStream.close();

            dos.flush();

            Log.i(Tag,
                    "File Sent, Response code : "
                            + String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            response = b.toString();
            Log.i(Tag, "Response = " + response);
            dos.close();
            result = Activity.RESULT_OK;
        } catch (Exception e) {
            Log.e(Tag, e.toString());
        } finally {
            publishResults(response, result);
        }
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(SERVERPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

}
