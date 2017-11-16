package com.simplicity.maged.mccobjectdetection.components;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.simplicity.maged.mccobjectdetection.components.logger.SimplicityLogger;

import edu.umich.PowerTutor.ui.PowerProfiler;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug.MemoryInfo;
import android.util.Log;

public class Profiler extends AsyncTask<Object, Integer, Void> {

    public double downDataSize;
    public double upDataSize;
    public double costCloud;
    public double costMobile;
    public double totalCost;
    public Boolean cancelled;
    public String serviceName;
    public UUID provider_uuid;
    public long startLocalCost;
    public long endLocalCost;

	/*
     * static long mStartPrivateDirty; static long mMaxPrivateDirty; static long
	 * mStartLocalCost; static long mEndLocalCost; static double mUpDataSize;
	 * static Cursor mCursor; static double mBattConsumed; static String
	 * mBattConsumUnit; static ContentValues mValues; static Object[] mParams;
	 */

    @Override
    protected Void doInBackground(Object... params) {
        try {
            cancelled = false;
            Log.i("simplicity", "profiler start");

            ActivityManager activityManager = (ActivityManager) ((Context) params[0])
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> pids = activityManager
                    .getRunningAppProcesses();
            int processid = 0;
            int appUID = 0;
            for (int i = 0; i < pids.size(); i++) {
                ActivityManager.RunningAppProcessInfo info = pids.get(i);
                if (info.processName
                        .equalsIgnoreCase("com.simplicity.maged.mccobjectdetection")) {
                    processid = info.pid;
                    appUID = info.uid;
                }
            }
            downDataSize = 0;
            costCloud = 0;
            Boolean dataProvider = (Boolean) params[2];
            MemoryInfo memoryInfo = activityManager
                    .getProcessMemoryInfo(new int[]{processid})[0];
            long startPrivateDirty = memoryInfo.getTotalPrivateDirty();
            long maxPrivateDirty = 0;
            while (!isCancelled()) {
                // Perform some repeating action.
                memoryInfo = activityManager
                        .getProcessMemoryInfo(new int[]{processid})[0];
                long privateDirty = memoryInfo.getTotalPrivateDirty();
                if (privateDirty > maxPrivateDirty) {
                    maxPrivateDirty = privateDirty;
                    Log.i("simplicity", "maxPrivateDirty: " + maxPrivateDirty);
                }
            }
            Log.i("simplicity", "profiler finish");
            Cursor c;
            c = ((Context) params[0]).getContentResolver().query(
                    PowerProfilerContentProvider.CONTENT_URI,
                    PowerProfilerContentProvider.COLUMNS,
                    " timeStamp >= ? AND timeStamp <= ?",
                    new String[]{String.valueOf(startLocalCost),
                            String.valueOf(endLocalCost)}, "timeStamp DESC");
            double battConsumed = 0;
            String battConsumUnit = "";
            if (c.moveToFirst()) {
                battConsumed = c
                        .getString(
                                c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("mJ") ? c.getDouble(c
                        .getColumnIndex(PowerProfilerContentProvider.key)) / 1000
                        : c.getString(
                        c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("kJ") ? c
                        .getDouble(c
                                .getColumnIndex(PowerProfilerContentProvider.key)) * 1000
                        : c.getString(
                        c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("MJ") ? c
                        .getDouble(c
                                .getColumnIndex(PowerProfilerContentProvider.key)) * 1000 * 1000
                        : c.getDouble(c
                        .getColumnIndex(PowerProfilerContentProvider.key));
                battConsumUnit = "J";
                Log.i("simplicity", "Profiler: battConsumed: " + battConsumed);
            }
            if (c.moveToLast()) {
                double battConsumedLast = c
                        .getString(
                                c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("mJ") ? c.getDouble(c
                        .getColumnIndex(PowerProfilerContentProvider.key)) / 1000
                        : c.getString(
                        c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("kJ") ? c
                        .getDouble(c
                                .getColumnIndex(PowerProfilerContentProvider.key)) * 1000
                        : c.getString(
                        c.getColumnIndex(PowerProfilerContentProvider.unit))
                        .trim().equals("MJ") ? c
                        .getDouble(c
                                .getColumnIndex(PowerProfilerContentProvider.key)) * 1000 * 1000
                        : c.getDouble(c
                        .getColumnIndex(PowerProfilerContentProvider.key));
                battConsumed -= battConsumedLast;
                Log.i("simplicity", "Profiler: battConsumedLast: "
                        + battConsumedLast);
            }
            c.close();
            // =======Note: Remember this condition!!!!
            if (downDataSize == 0) {
                Log.i("simplicity", "Opration failed, Download Data Size = 0 ");
                cancelled = true;
                return null;
            }
            Log.i("simplicity", "Down data size: " + downDataSize);
            Log.i("simplicity", "Cloud execution cost: " + costCloud);
            // =======

            int row_affected = ((Context) params[0]).getContentResolver()
                    .delete(ProfilerContentProvider.CONTENT_URI,
                            ProfilerContentProvider.ServiceName + " ==? AND "
                                    + ProfilerContentProvider.UPDataSize
                                    + " ==? " + " AND "
                                    + ProfilerContentProvider.DataProvider
                                    + " ==? AND "
                                    + ProfilerContentProvider.PROVIDERUUID
                                    + " ==?",
                            new String[]{
                                    serviceName,
                                    String.valueOf(upDataSize),
                                    String.valueOf(dataProvider ? 1 : 0),
                                    provider_uuid == null ? "" : provider_uuid
                                            .toString()});

            Log.i("simplicity", "delete old profile row: row_affected: "
                    + row_affected);

            ContentValues values = new ContentValues();

            values.clear();
            values.put(ProfilerContentProvider.BattConsumed, battConsumed);
            Log.i("simplicity", "BattConsumed = " + battConsumed);
            SimplicityLogger.appendLine("," + battConsumed);
            values.put(ProfilerContentProvider.BattConsumUnit, battConsumUnit);
            Log.i("simplicity", "BattConsumedUnit = " + battConsumUnit);
            SimplicityLogger.appendLine("," + battConsumUnit);
            values.put(ProfilerContentProvider.UPDataSize, upDataSize);
            values.put(ProfilerContentProvider.DownDataSize, downDataSize);
            if (costCloud == 0) {
                values.put(ProfilerContentProvider.ExecCost, costMobile);
            } else {
                values.put(ProfilerContentProvider.ExecCost, costCloud);
            }
            values.put(ProfilerContentProvider.TotalExecCost, totalCost);
            values.put(ProfilerContentProvider.DataProvider, dataProvider ? 1
                    : 0);
            values.put(ProfilerContentProvider.PROVIDERUUID,
                    provider_uuid == null ? "" : provider_uuid.toString());
            values.put(ProfilerContentProvider.MemRequired, maxPrivateDirty
                    - startPrivateDirty);
            values.put(ProfilerContentProvider.ServiceName, serviceName);
            Uri result = ((Context) params[0]).getContentResolver().insert(
                    ProfilerContentProvider.CONTENT_URI, values);
            long id = Long.parseLong(result.getLastPathSegment());
            Log.i("simplicity", "Profiler_record_ID = " + String.valueOf(id));
            cancelled = true;
            return null;
        } catch (Exception e) {
            Log.e("simplicity", e.toString());
            cancelled = true;
            return null;
        }
    }

    private void updateORInsert(long startPrivateDirty, long maxPrivateDirty,
                                double LocalCost, double upDataSize, double downDataSize,
                                double costCloud, Boolean dataProvider, UUID provider_uuid,
                                Cursor c, double battConsumed, String battConsumUnit,
                                ContentValues values, Object... params) {
        if (c.moveToFirst()) {
            values.clear();
            values.put(
                    ProfilerContentProvider.BattConsumed,
                    (battConsumed + c.getDouble(c
                            .getColumnIndex(ProfilerContentProvider.BattConsumed))) / 2);
            values.put(ProfilerContentProvider.BattConsumUnit, battConsumUnit);
            values.put(
                    ProfilerContentProvider.UPDataSize,
                    (upDataSize + c.getDouble(c
                            .getColumnIndex(ProfilerContentProvider.UPDataSize))) / 2);
            values.put(
                    ProfilerContentProvider.DownDataSize,
                    (downDataSize + c.getDouble(c
                            .getColumnIndex(ProfilerContentProvider.DownDataSize))) / 2);
            if (costCloud == 0) {
                values.put(
                        ProfilerContentProvider.ExecCost,
                        (LocalCost + c.getDouble(c
                                .getColumnIndex(ProfilerContentProvider.ExecCost))) / 2);
            } else {
                values.put(
                        ProfilerContentProvider.ExecCost,
                        (costCloud + c.getDouble(c
                                .getColumnIndex(ProfilerContentProvider.ExecCost))) / 2);
            }
            values.put(ProfilerContentProvider.DataProvider, dataProvider ? 1
                    : 0);
            values.put(ProfilerContentProvider.PROVIDERUUID,
                    provider_uuid == null ? "" : provider_uuid.toString());
            values.put(ProfilerContentProvider.MemRequired, (maxPrivateDirty
                    - startPrivateDirty + c.getDouble(c
                    .getColumnIndex(ProfilerContentProvider.MemRequired))) / 2);
            values.put(ProfilerContentProvider.ServiceName,
                    String.valueOf(params[1]));

            int affected = ((Context) params[0])
                    .getContentResolver()
                    .update(ProfilerContentProvider.CONTENT_URI,
                            values,
                            ProfilerContentProvider.ServiceName + "=? AND "
                                    + ProfilerContentProvider.Col_ID + "=?",
                            new String[]{
                                    String.valueOf(params[1]),
                                    String.valueOf(c.getDouble(c
                                            .getColumnIndex(ProfilerContentProvider.Col_ID)))});
            Log.i("simplicity", "No of affected rows= " + affected);

        } else {
            values.clear();
            values.put(ProfilerContentProvider.BattConsumed, battConsumed);
            Log.i("simplicity", "BattConsumed = " + battConsumed);
            values.put(ProfilerContentProvider.BattConsumUnit, battConsumUnit);
            Log.i("simplicity", "BattConsumed = " + battConsumUnit);
            values.put(ProfilerContentProvider.UPDataSize, upDataSize);
            values.put(ProfilerContentProvider.DownDataSize, downDataSize);
            if (costCloud == 0) {
                values.put(ProfilerContentProvider.ExecCost, LocalCost);
            } else {
                values.put(ProfilerContentProvider.ExecCost, costCloud);
            }
            values.put(ProfilerContentProvider.DataProvider, dataProvider ? 1
                    : 0);
            values.put(ProfilerContentProvider.PROVIDERUUID,
                    provider_uuid == null ? "" : provider_uuid.toString());
            values.put(ProfilerContentProvider.MemRequired, maxPrivateDirty
                    - startPrivateDirty);
            values.put(ProfilerContentProvider.ServiceName,
                    String.valueOf(params[1]));
            Uri result = ((Context) params[0]).getContentResolver().insert(
                    ProfilerContentProvider.CONTENT_URI, values);
            long id = Long.parseLong(result.getLastPathSegment());
            Log.i("simplicity", "Profiler_record_ID = " + String.valueOf(id));
        }
    }

    private double relativeSigma(double value, double mean) {
        return 100 * (Math.sqrt(Math.pow(value - mean, 2))) / mean;
    }
}
