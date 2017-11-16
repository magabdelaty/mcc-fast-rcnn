package com.simplicity.maged.mccobjectdetection.components.executionManager;

import java.io.File;

import com.simplicity.maged.mccobjectdetection.R;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ExecutionEngineService extends IntentService {

    public static final String INPUTFILES = "inputfiles";
    public static final String LOCALSERVICEREQUEST = "localservicerequest";
    public static final String DATAPROVIDER = "dataprovider";
    public static final String LACKOFRESOURCES = "lackofresources";
    public static final String SERVICEREQUESTPARAMS = "localservicerequestparams";
    public static final String RESULTFILE = "resultfile";
    public static final String RESPONSETIME = "responsetime";
    public static final String RESULT = "result";
    public static final String COMMCOST = "communicationcost";
    public static final String NOTIFICATION = "com.simplicity.maged.mccobjectdetection.components.executionManager.ExecutionEngineService";
    static String TAG = "ExecutionEngineService";

    public ExecutionEngineService() {
        super("ExecutionEngineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String localServiceRequest = bundle.getString(LOCALSERVICEREQUEST, "");
        Boolean lackOfResources = bundle.getBoolean(LACKOFRESOURCES, false);
        String[] serviceRequestParams = bundle
                .getStringArray(SERVICEREQUESTPARAMS);
        String[] inputFiles = bundle.getStringArray(INPUTFILES);
        Boolean dataProvider = bundle.getBoolean(DATAPROVIDER);
        String app_directory = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/"
                + getApplicationContext().getString(R.string.app_dir);
        if (!(new File(app_directory)).mkdir()) {
            Log.i(TAG,
                    "ExecutionEngineService: app_directory already existed");
        }
        //** remember to change data provider case and get upDataSize
        ExecutionResult execResult = ExecutionEngine.executeService(
                getApplicationContext(), localServiceRequest, lackOfResources,
                serviceRequestParams, inputFiles, dataProvider, app_directory,
                false, 0);
        publishResults(execResult, lackOfResources);
    }

    private void publishResults(ExecutionResult execResult,
                                Boolean lackOfResources) {
        Log.i(TAG, "ExecutionEngineService: publishResults");
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(LACKOFRESOURCES, lackOfResources);
        intent.putExtra(RESULTFILE, execResult.resultFilePath);
        intent.putExtra(COMMCOST, execResult.commCost);
        intent.putExtra(RESPONSETIME, execResult.responseTime);
        intent.putExtra(RESULT, execResult.result);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }
}
