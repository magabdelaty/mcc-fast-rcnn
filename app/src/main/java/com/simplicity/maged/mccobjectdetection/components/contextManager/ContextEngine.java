package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import com.simplicity.maged.mccobjectdetection.R;
import com.simplicity.maged.mccobjectdetection.components.logger.SimplicityLogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ContextEngine {

	static NetworkStatus mNS;
	static Providers providers;
	static double userUpBw;
	static double userDownBw;
	static double upBW;
	static double downBW;

	public static void setUpBW(double upBW) {
		ContextEngine.upBW = upBW;
	}

	public static void setDownBW(double downBW) {
		ContextEngine.downBW = downBW;
	}

	static double getUserUpBw() {
		return userUpBw;
	}

	public static void setUserUpBw(double userUpBw) {
		ContextEngine.userUpBw = userUpBw;
	}

	static double getUserDownBw() {
		return userDownBw;
	}

	public static void setUserDownBw(double userDownBw) {
		ContextEngine.userDownBw = userDownBw;
	}

	public static Boolean getInternetConnected() {
		return mNS.connected;
	}

	public static Boolean getCloudConnected() {
		return mNS.cloudConnected;
	}

	public static Boolean getMobileInternet() {
		return mNS.mobileInternet;
	}

	public static Boolean getWifi() {
		return mNS.wifi;
	}

	public static int getSignal_strength() {
		return mNS.signal_strength;
	}

	public static int getWifi_quality() {
		return mNS.wifi_quality;
	}

	public static int getWifi_quality_percent() {
		return mNS.wifi_quality_percent;
	}

	public static double getWifiLinkSpeed() {
		return mNS.wifi_link_speed;
	}

	public static double getUpBw() {
		if (ContextEngine.upBW == 0 && mNS.upBw > 0) {
			return mNS.upBw;
		}
		return ContextEngine.upBW;
	}

	public static double getDownBw() {
		if (ContextEngine.downBW == 0 && mNS.downBw > 0) {
			return mNS.downBw;
		}
		return ContextEngine.downBW;
	}

	public static LocalResources GetLocalResources(Context context) {
		LocalResources lcl = new LocalResources(context);
		return lcl;
	}

	public static void UpdateNetworkStatus(Context context, String downUrl,
			String upUrl) {
		if (mNS == null) {
			mNS = NetworkStatus.getInstance();
		}
		mNS.UpdateNetworkStatus(context, downUrl, upUrl);
	}

	public static void InitializeProvidersList(Context context) {
		if (providers == null) {
			providers = Providers.getInstance();
			Provider provider = new Provider();
			String s2 = context.getString(R.string.cloudProviderUUID).replace(
					"-", "");
			UUID uuid = new UUID(
					new BigInteger(s2.substring(0, 16), 16).longValue(),
					new BigInteger(s2.substring(16), 16).longValue());
			provider.id = uuid;
			provider.url = context.getString(R.string.serverURL);
			provider.permanentFlag = true;
			providers.addCloudProvider(provider);
		}
	}

	public static LocalContext getLocalContext(Context context) {
		LocalContext lContext = new LocalContext();
		Cursor c = context.getContentResolver().query(
				ContextManagerContentProvider.DEV_SPECS_CONTENT_URI,
				ContextManagerContentProvider.DEV_SPECS_COLUMNS, null, null,
				null);
		// equivalent to select * from table.
		if (c.moveToFirst()) {
			lContext.computeSpeed = c
					.getInt(c
							.getColumnIndex(ContextManagerContentProvider.DEV_SPECS_CPU));
		}
		c.close();
		lContext.upBW1 = getUserUpBw() > 0 ? getUserUpBw() : getWifiLinkSpeed();
		lContext.upBW2 = getUpBw();
		lContext.downBW1 = getUserDownBw() > 0 ? getUserDownBw()
				: getWifiLinkSpeed();
		lContext.downBW2 = getDownBw();
		LocalResources lcl = GetLocalResources(context);
		/*
		 * voltage of 3.8 volts * 1900 mAh == 7220 mA-volt-hours == 7220
		 * milliwatt-hours == 7.22 watt-hours == 7.22 watt * 60 min * 60 sec ==
		 * 25992 watt-seconds or Joules
		 */
		lContext.energyAvail = (lcl.batt_volt * 1900 / 1000.0) * 3600;
		lContext.battLevel = lcl.batt_level;
		lContext.memoryAvail = lcl.avial_memory;
		lContext.pCompute = 0.4445;
		lContext.pIdeal = 0.1887;
		lContext.pTransmit = 0.2103;
		Log.i("simplicity", "ContextEngine.getLocalContext: upBW1: "
				+ lContext.upBW1);
		Log.i("simplicity", "ContextEngine.getLocalContext: downBW1: "
				+ lContext.downBW1);
		Log.i("simplicity", "ContextEngine.getLocalContext: upBW2: "
				+ lContext.upBW2);
		Log.i("simplicity", "ContextEngine.getLocalContext: downBW2: "
				+ lContext.downBW2);


		return lContext;
	}

	public static EnvironmentContext[] getEnvironmentContext(Context context) {
		int length = Providers.getProviders() != null ? Providers
				.getProviders().size() : 0;
		EnvironmentContext[] eContext = new EnvironmentContext[length];
		for (int i = 0; i < length; i++) {
			Provider provider = Providers.getProviders().get(i);
			if (provider.permanentFlag == true) {
				// main cloud provider
				EnvironmentContext eC = new EnvironmentContext();
				Cursor c = context
						.getContentResolver()
						.query(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
								ContextManagerContentProvider.SERVICE_DISCOVERY_COLUMNS,
								ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID
										+ " = ?",
								new String[] { provider.id.toString() }, null);
				if (c.moveToFirst()) {
					eC.upBW3 = getUpBw();
					eC.upBW4 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW4));
					eC.upBW5 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW5));
					eC.downBW3 = getDownBw();
					eC.downBW4 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW4));
					eC.downBW5 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW5));
					eC.computeSpeed = c
							.getLong(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED));
					eC.memoryAvail = c
							.getLong(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY));
				}
				c.close();
				eC.energyAvail = 100; // cloud is 100 % powered
				eC.providerUUID = provider.id;
				eContext[i] = eC;
			}

			else {
				// mobile service provider
				EnvironmentContext eC = new EnvironmentContext();
				Cursor c = context
						.getContentResolver()
						.query(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
								ContextManagerContentProvider.SERVICE_DISCOVERY_COLUMNS,
								ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID
										+ " = ?",
								new String[] { provider.id.toString() }, null);
				if (c.moveToFirst()) {
					eC.computeSpeed = c
							.getLong(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED));
					eC.upBW3 = getUserUpBw() > 0 ? getUserUpBw()
							: getWifiLinkSpeed();
					eC.upBW4 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW4));
					eC.upBW5 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW5));
					eC.downBW3 = getUserDownBw() > 0 ? getUserDownBw()
							: getWifiLinkSpeed();
					eC.downBW4 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW4));
					eC.downBW5 = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW5));
					eC.energyAvail = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILENERGY));
					eC.memoryAvail = c
							.getDouble(c
									.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY));
				}
				c.close();
				eC.providerUUID = provider.id;
				eContext[i] = eC;
			}
			Log.i("simplicity", "ContextEngine.getEnvironmentContext: upBW3: "
					+ eContext[i].upBW3);
			Log.i("simplicity",
					"ContextEngine.getEnvironmentContext: downBW3: "
							+ eContext[i].downBW3);
			Log.i("simplicity", "ContextEngine.getEnvironmentContext: upBW4: "
					+ eContext[i].upBW4);
			Log.i("simplicity",
					"ContextEngine.getEnvironmentContext: downBW4: "
							+ eContext[i].downBW4);
			Log.i("simplicity", "ContextEngine.getEnvironmentContext: upBW5: "
					+ eContext[i].upBW5);
			Log.i("simplicity",
					"ContextEngine.getEnvironmentContext: downBW5: "
							+ eContext[i].downBW5);

			SimplicityLogger
					.appendLine(","
							+ eContext[i].upBW3);
			SimplicityLogger
					.appendLine(","
							+ eContext[i].downBW3);

		}

		return eContext;
	}

	public static void UpdateDeviceSpecs(Context context) {

		long sysBootTime = System.currentTimeMillis()
				- SystemClock.elapsedRealtime();
		long dbSysBootTime = 0;
		Cursor c = context.getContentResolver().query(
				ContextManagerContentProvider.DEV_SPECS_CONTENT_URI,
				ContextManagerContentProvider.DEV_SPECS_COLUMNS, null, null,
				null);
		// equivalent to select * from table.
		if (c.moveToFirst()) {
			dbSysBootTime = c
					.getLong(c
							.getColumnIndex(ContextManagerContentProvider.DEV_SPECS_LAST_BOOT));
		}
		c.close();
		if (!(dbSysBootTime - 2 <= sysBootTime && sysBootTime <= dbSysBootTime + 2)) {
			// disp_size_x // disp_size_y // cpu // ram // sdk // mob_internet
			// wifi // last_boot
			// Reboot has occured
			long cpu = DeviceInfo.getCPUMaxFreqKHz() * 1000; // KHz * 1000 = Hz
			Log.d("simplicity","cpu: " + String.valueOf(cpu));
			long ram = DeviceInfo.getTotalMemory(context);
			Log.d("simplicity","ram: " + String.valueOf(ram));
			int sdk = android.os.Build.VERSION.SDK_INT;
			Log.d("simplicity", String.valueOf(sdk));
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int disp_size_x = size.x;
			int disp_size_y = size.y;
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo m3G = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			int wifi = mWifi != null ? 1 : 0;
			int mob_internet = m3G != null ? 1 : 0;
			// Sensors type name
			SensorManager mgr = (SensorManager) context
					.getSystemService(Context.SENSOR_SERVICE);
			List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
			// ================
			context.getContentResolver().delete(
					ContextManagerContentProvider.DEV_SPECS_CONTENT_URI, null,
					null); // delete * from table
			ContentValues values = new ContentValues();
			values.put(ContextManagerContentProvider.DEV_SPECS_3G, mob_internet);
			values.put(ContextManagerContentProvider.DEV_SPECS_CPU, cpu);
			values.put(ContextManagerContentProvider.DEV_SPECS_DISP_SIZE_X,
					disp_size_x);
			values.put(ContextManagerContentProvider.DEV_SPECS_DISP_SIZE_Y,
					disp_size_y);
			values.put(ContextManagerContentProvider.DEV_SPECS_LAST_BOOT,
					sysBootTime);
			values.put(ContextManagerContentProvider.DEV_SPECS_RAM, ram);
			values.put(ContextManagerContentProvider.DEV_SPECS_SDK, sdk);
			values.put(ContextManagerContentProvider.DEV_SPECS_WIFI, wifi);
			Uri result = context
					.getApplicationContext()
					.getContentResolver()
					.insert(ContextManagerContentProvider.DEV_SPECS_CONTENT_URI,
							values);
			// ====================
			context.getContentResolver().delete(
					ContextManagerContentProvider.DEV_SENSORS_CONTENT_URI,
					null, null); // delete * from table
			for (Sensor sensor : sensors) {
				values.clear();
				values.put(ContextManagerContentProvider.DEV_SENSORS_LAST_BOOT,
						sysBootTime);
				values.put(ContextManagerContentProvider.DEV_SENSORS_NAME,
						sensor.getName());
				values.put(ContextManagerContentProvider.DEV_SENSORS_TYPE,
						sensor.getType());
				result = context
						.getApplicationContext()
						.getContentResolver()
						.insert(ContextManagerContentProvider.DEV_SENSORS_CONTENT_URI,
								values);
			}

		}
	}

	public static void UpdateAvailableResources(Context context,
			String discoverServiceUrl, Boolean forceUpdate) {
		long sysBootTime = System.currentTimeMillis()
				- SystemClock.elapsedRealtime();
		long dbSysBootTime = 0;
		Cursor c = context.getContentResolver().query(
				ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
				ContextManagerContentProvider.SERVICE_DISCOVERY_COLUMNS, null,
				null, null);
		// equivalent to select * from table.
		if (c.moveToFirst()) {
			dbSysBootTime = c
					.getLong(c
							.getColumnIndex(ContextManagerContentProvider.SERVICE_DISCOVERY_LAST_BOOT));
		}
		c.close();
		if (forceUpdate
				|| !(dbSysBootTime - 2 <= sysBootTime && sysBootTime <= dbSysBootTime + 2)) {
			ResourceDiscovery rs = ResourceDiscovery.getInstance();
			rs.DoServiceDiscovery(context, discoverServiceUrl, sysBootTime);
		}
	}

}
