package com.simplicity.maged.mccobjectdetection.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.DownloadManager;
import com.simplicity.maged.mccobjectdetection.components.RESTClient;
import com.simplicity.maged.mccobjectdetection.components.UploadManager;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class RemoteExecutionService extends IntentService {

    public static final String INPUTFILES = "inputfiles";
    public static final String RESULTFILE = "resultfile";
    public static final String CLOUDSERVICENAME = "cloudservicename";
    public static final String CLOUDSERVICEPARAMS = "cloudserviceparams";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.simplicity.maged.mccobjectdetection.services.RemoteExecutionService";
    private int result = Activity.RESULT_CANCELED;

    public RemoteExecutionService() {
        super("TestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] inputPaths = intent.getStringArrayExtra(INPUTFILES);
        String cloudServiceName = intent.getStringExtra(CLOUDSERVICENAME);
        String cloudServiceParams = intent.getStringExtra(CLOUDSERVICEPARAMS);
        String fResult = inputPaths[0] + "_Cloud_Result"
                + inputPaths[0].substring(inputPaths[0].lastIndexOf("."));
        try {
            String contentType = getMimeType(Uri.fromFile(new File(
                    inputPaths[0])));
            List<String> cloudFileNames = new ArrayList<>();
            for (int i = 0; i < inputPaths.length; i++) {
                cloudFileNames.add(UploadManager.Upload(inputPaths[i],
                        this.getString(R.string.serverURL) + "files",
                        contentType));
                Log.i("simplicity", "cloudFileNames: " + cloudFileNames.get(i));
            }
            String cloudResultFileName = null;

            if (!cloudFileNames.isEmpty()) {
                String serviceUrl = this.getString(R.string.serverURL)
                        + cloudServiceName;
                for (int i = 0; i < cloudFileNames.size(); i++) {
                    serviceUrl += cloudFileNames.get(i);
                    if (i != cloudFileNames.size() - 1) {
                        serviceUrl += "&";
                    }
                }
                if (!cloudServiceParams.equals("")) {
                    serviceUrl += cloudServiceParams;
                }
                cloudResultFileName = RESTClient.GetStringRequest(
                        getApplicationContext(), serviceUrl, 60);
            }
            if (cloudResultFileName != null) {
                Log.i("simplicity", "cloudResultFileName: "
                        + cloudResultFileName);
                if (DownloadManager.Download(this.getString(R.string.serverURL)
                        + "files/" + cloudResultFileName, fResult)) {
                    result = Activity.RESULT_OK;
                }
            }
        } catch (Exception e) {
            Log.e("simplicity", e.toString());
        } finally {
            publishResults(fResult, result);
        }
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULTFILE, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    private String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}
