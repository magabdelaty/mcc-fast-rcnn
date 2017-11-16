package com.simplicity.maged.mccobjectdetection.components;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManagerService extends IntentService {

    public static final String RESULT = "result";
    public static final String URL = "url";
    public static final String RESULTFILE = "resultfile";
    public static final String NOTIFICATION = "com.simplicity.maged.mccobjectdetection.components.DownloadManagerService";
    private int result = Activity.RESULT_CANCELED;

    public DownloadManagerService() {
        super("DownloadManagerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent
                .getStringExtra(DownloadManagerService.URL);
        String filePath = intent
                .getStringExtra(DownloadManagerService.RESULTFILE);
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // download the file
            InputStream input = new BufferedInputStream(
                    connection.getInputStream());
            OutputStream output = new FileOutputStream(filePath);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            result = Activity.RESULT_OK;
        } catch (Exception e) {
            Log.e("simplicity", e.toString());
        } finally {
            publishResults(filePath, result);
        }
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULTFILE, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
