package com.simplicity.maged.mccobjectdetection.components;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {

    public static boolean Download(String urlToDownload, String filePath) {
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            connection.setConnectTimeout(60 * 1000);
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
            Log.i("simplicity", "File Downloaded");
            return true;
        } catch (Exception e) {
            Log.e("simplicity", e.toString());
            return false;
        }
    }

}
