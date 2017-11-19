package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

public class LocalResources {

	public double avial_memory; // KB
	public int cpu_util_ratio;
	public int batt_level;
	public double batt_volt;
	public long local_stor_free;
	public long sd_stor_free;

	public LocalResources(Context context) {
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		long totalMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();
		avial_memory = (int) Math
				.round((freeMemory + (maxMemory - totalMemory)) / 1024);
		// ================
		cpu_util_ratio = ReadCPUinfo();
		// =================
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);

		batt_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		batt_volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000.0;
		// =================
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		sd_stor_free = ((long) statFs.getAvailableBlocks())
				* statFs.getBlockSize() / 1048576;

		statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
		local_stor_free = ((long) statFs.getAvailableBlocks())
				* statFs.getBlockSize() / 1048576;

	}



	public static void logHeap() {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize())
				/ new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.i("simplicity", "debug. =================================");
		Log.i("simplicity",
				"debug.heap native: allocated " + df.format(allocated)
						+ "MB of " + df.format(available) + "MB ("
						+ df.format(free) + "MB free)");
		Log.i("simplicity",
				"debug.memory: allocated: "
						+ df.format(new Double(Runtime.getRuntime()
								.totalMemory() / 1048576))
						+ "MB of "
						+ df.format(new Double(
								Runtime.getRuntime().maxMemory() / 1048576))
						+ "MB ("
						+ df.format(new Double(Runtime.getRuntime()
								.freeMemory() / 1048576)) + "MB free)");
	}

	private int ReadCPUinfo() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("cpu")) {

					final String[] tokens = line.split(" +");
					Long idle = Long.parseLong(tokens[4]);
					Long total = Long.parseLong(tokens[1])
							+ Long.parseLong(tokens[2])
							+ Long.parseLong(tokens[3]) + idle
							+ Long.parseLong(tokens[5])
							+ Long.parseLong(tokens[6])
							+ Long.parseLong(tokens[7]);
					return (int) (100 - idle * 100 / total);
				}
				reader.close();
			}

		} catch (Exception e) {
			Log.e("simplicity", e.toString());
		}
		return 0;
	}
}
