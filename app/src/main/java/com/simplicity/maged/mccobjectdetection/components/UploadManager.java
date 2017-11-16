package com.simplicity.maged.mccobjectdetection.components;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class UploadManager {

    public static String Upload(String filePath, String url,
                                String contentType) {
        URL connectURL;
        FileInputStream fileInputStream;
        String response = "";
        String lineEnd = "\r\n";
        String Tag = "simplicity";
        try {
            fileInputStream = new FileInputStream(filePath);
            connectURL = new URL(url);
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
            return response;
        } catch (Exception e) {
            Log.e(Tag, e.toString());
            return null;
        }
    }

    public static String UploadDropBox(String uploadURL,
                                       String fileURL) {
        URL connectURL;
        String response = "";
        String lineEnd = "\r\n";
        String Tag = "simplicity";
        try {
            connectURL = new URL(uploadURL);
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
            conn.setRequestProperty("DBX-Uri", fileURL);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
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
            return response;
        } catch (Exception e) {
            Log.e(Tag, e.toString());
            return null;
        }
    }


}
