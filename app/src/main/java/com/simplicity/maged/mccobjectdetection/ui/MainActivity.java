package com.simplicity.maged.mccobjectdetection.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.R.id;
import com.simplicity.maged.mccobjectdetection.components.PowerProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.ProfilerContentProvider;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngine;
import com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngineService;
import com.simplicity.maged.mccobjectdetection.components.contextManager.UserPreferences;
import com.simplicity.maged.mccobjectdetection.components.logger.SimplicityLogger;

import edu.umich.PowerTutor.ui.PowerProfiler;

public class MainActivity extends AppCompatActivity {

    private Button mBtnObjectRecognition;
    private Button mBtnSceneRecognition;
    private Button mBtnUpdateCloudRes;
    private Button mBtnShowProfilerData;
    private RadioButton mChkForceLocal;
    private RadioButton mChkAutoRemote;
    private RadioButton mChkForceRemote;
    private RadioGroup mRdBtn_offloading;
    private Intent mConEngineService;
    private Intent battt_profiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnObjectRecognition = (Button) findViewById(id.btn_Object_Recognition);
        mBtnObjectRecognition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_ObjectRecognition(v);
            }
        });

        /*mBtnSceneRecognition = (Button) findViewById(id.btn_scene_Recognition);
        mBtnSceneRecognition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_SceneRecognition(v);
            }
        });*/

        mRdBtn_offloading = (RadioGroup) findViewById(id.rdBtn_offloading);
        mChkForceLocal = (RadioButton) findViewById(id.chk_force_local);
        mChkForceLocal.setChecked(UserPreferences
                .getForce_local(getApplicationContext()));
        mChkAutoRemote = (RadioButton) findViewById(id.chk_automatic_offloading);
        mChkAutoRemote.setChecked(UserPreferences
                .getAutomatic_offloading(getApplicationContext()));
        mChkForceRemote = (RadioButton) findViewById(id.chk_force_offloading);
        mChkForceRemote.setChecked(UserPreferences
                .getForce_offloading(getApplicationContext()));
        mRdBtn_offloading
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.chk_force_local) {
                            UserPreferences.setForce_offloading(false,
                                    getApplicationContext());
                            UserPreferences.setAutomatic_offloading(false,
                                    getApplicationContext());
                            UserPreferences.setForce_local(true,
                                    getApplicationContext());
                        } else if (checkedId == R.id.chk_force_offloading) {
                            UserPreferences.setForce_offloading(true,
                                    getApplicationContext());
                            UserPreferences.setAutomatic_offloading(false,
                                    getApplicationContext());
                            UserPreferences.setForce_local(false,
                                    getApplicationContext());
                        } else if (checkedId == R.id.chk_automatic_offloading) {
                            UserPreferences.setForce_offloading(false,
                                    getApplicationContext());
                            UserPreferences.setAutomatic_offloading(true,
                                    getApplicationContext());
                            UserPreferences.setForce_local(false,
                                    getApplicationContext());
                        }
                    }

                });

        if (!mChkAutoRemote.isChecked() && !mChkForceLocal.isChecked()
                && !mChkForceRemote.isChecked()) {
            mChkAutoRemote.setChecked(true);
            UserPreferences.setAutomatic_offloading(true,
                    getApplicationContext());
        }

        mBtnUpdateCloudRes = (Button) findViewById(R.id.btn_update_cloud_resources);
        mBtnUpdateCloudRes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_update_cloud_res(v);
            }
        });
        mBtnShowProfilerData = (Button) findViewById(id.btn_show_profiler_data);
        mBtnShowProfilerData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClick_show_profiler_data(v);
            }
        });
        // =============================
        ContextEngine.UpdateDeviceSpecs(getApplicationContext());
        mConEngineService = new Intent(getApplicationContext(),
                ContextEngineService.class);
        mConEngineService
                .setAction(com.simplicity.maged.mccobjectdetection.components.contextManager.Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(mConEngineService);
        // ===============================
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = activityManager
                .getRunningAppProcesses();
        int appUID = 0;
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase("com.simplicity.maged.mccobjectdetection")) {
                appUID = info.uid;
            }
        }
        battt_profiler = new Intent(getApplicationContext(),
                PowerProfiler.class);
        battt_profiler.putExtra(PowerProfiler.PROFILEDUID, appUID);
        battt_profiler.putExtra(PowerProfiler.PROFILERCONTPROVURI,
                PowerProfilerContentProvider.CONTENT_URI.toString());

    }

    private void onClick_show_profiler_data(View v) {
        Cursor c = getApplicationContext().getContentResolver().query(
                ProfilerContentProvider.CONTENT_URI,
                ProfilerContentProvider.COLUMNS, null, null, null);
        // equivalent to select * from table.
        SimplicityLogger.initLog();
        while (c.moveToNext()) {
            SimplicityLogger.appendLine("ID: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.Col_ID))
                    + "|ServiceName: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.ServiceName))
                    + "|BattConsumed: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.BattConsumed))
                    + "|BattConsumUnit: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.BattConsumUnit))
                    + "|ExecCost: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.ExecCost))
                    + "|UPDataSize: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.UPDataSize))
                    + "|DownDataSize: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.DownDataSize))
                    + "|Data Provider: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.DataProvider))
                    + "|MemRequired: "
                    + c.getString(c
                    .getColumnIndex(ProfilerContentProvider.MemRequired))
                    + "\n");
        }
        c.close();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        SimplicityLogger.writeLog("/sdcard/simplicity/Log/"
                + "profiler_data_" + df.format(cal.getTime()) + ".txt");
        Toast.makeText(this, "Profiler Data Retrieve Done!", Toast.LENGTH_SHORT).show();
    }

    private void onClick_SceneRecognition(View v) {
        startService(battt_profiler);
        Intent myIntent = new Intent(MainActivity.this,
                SceneRecognitionActivity.class);
        startActivity(myIntent);
    }

    private void onClick_ObjectRecognition(View v) {
        startService(battt_profiler);
        Intent myIntent = new Intent(MainActivity.this,
                ObjectDetectionActivity.class);
        startActivity(myIntent);
    }

    protected void onClick_update_cloud_res(View v) {
        ContextEngine.UpdateAvailableResources(getApplicationContext(),
                getString(R.string.serverURL) + "service_discovery", true);
    }

    public boolean isNetworkProfilerServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(
                    "NetworkProfilerService")) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        startService(mConEngineService);
        stopService(battt_profiler);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(mConEngineService
                .setAction(com.simplicity.maged.mccobjectdetection.components.contextManager.Constants.ACTION.STOPFOREGROUND_ACTION));
        stopService(battt_profiler);
    }


}
