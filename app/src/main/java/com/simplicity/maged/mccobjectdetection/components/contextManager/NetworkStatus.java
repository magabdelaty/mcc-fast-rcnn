package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.io.File;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import javax.annotation.Nonnull;

import com.simplicity.maged.mccobjectdetection.components.DownloadManager;
import com.simplicity.maged.mccobjectdetection.components.UploadManager;

public class NetworkStatus {

	Boolean connected;
	Boolean cloudConnected;
	Boolean mobileInternet;
	Boolean wifi;
	int signal_strength; // 99 = is not connected
	int wifi_quality; // -100 dbm = is not connected
	int wifi_quality_percent; // 0 = 0% quality
	double wifi_link_speed;
	double upBw;
	double downBw;
	Boolean mBwTesting;
	long mBwStartValid;
	static ConnectivityManager cm;
	static NetworkInfo activeNetwork;
	static TelephonyManager telephonyManager;
	static WifiManager wifiManager;

	protected Boolean getConnected() {
		return connected;
	}

	protected double getWifi_link_speed() {
		return wifi_link_speed;
	}

	protected Boolean getCloudConnected() {
		return cloudConnected;
	}

	protected Boolean getMobileInternet() {
		return mobileInternet;
	}

	protected Boolean getWifi() {
		return wifi;
	}

	protected int getSignal_strength() {
		return signal_strength;
	}

	protected int getWifi_quality() {
		return wifi_quality;
	}

	protected int getWifi_quality_percent() {
		return wifi_quality_percent;
	}

	protected double getUpBw() {
		return upBw;
	}

	protected double getDownBw() {
		return downBw;
	}

	protected void stopPhoneStateListener(Context context) {
		if (telephonyManager != null) {
			telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
		}
	}

	// Singleton.
	private static class NetworkStatusHolder {
		public static final NetworkStatus instance = new NetworkStatus();
	}

	@Nonnull
	protected static NetworkStatus getInstance() {
		return NetworkStatusHolder.instance;
	}

	private NetworkStatus() {
		connected = false;
		cloudConnected = false;
		mobileInternet = false;
		wifi = false;
		mBwTesting = false;
	}

	protected synchronized void UpdateNetworkStatus(Context context,
			String downUrl, String upUrl) {
		if (cm == null) {
			cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		if (wifiManager == null) {
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}
		if (telephonyManager == null) {
			telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(new PhoneStateListener() {
				@Override
				public void onSignalStrengthsChanged(
						SignalStrength signalStrength) {
					super.onSignalStrengthsChanged(signalStrength);
					if (signalStrength.getGsmSignalStrength() != 99)
						signal_strength = signalStrength.getGsmSignalStrength() * 2 - 113;
					else
						signal_strength = signalStrength.getGsmSignalStrength();
				}
			}, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		activeNetwork = cm.getActiveNetworkInfo();
		connected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();

		if (connected) {
			wifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
			mobileInternet = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
		} else {
			wifi = false;
			mobileInternet = false;
		}
		if (wifi) {
			wifi_quality = wifiManager.getConnectionInfo().getRssi();
			// The range is between -100 to 0 dBm, closer to 0 is higher
			// strength and vice-versa.
			wifi_quality_percent = WifiManager.calculateSignalLevel(
					wifi_quality, 100);
			// wifi_link_speed KB/sec
			wifi_link_speed = wifiManager.getConnectionInfo().getLinkSpeed() / 8 * 1024;
		} else {
			wifi_quality = -100;
			wifi_quality_percent = 0;
			wifi_link_speed = 0;
		}
		if (mBwTesting == null) {
			mBwTesting = false;
		}
		// Ignore Cloud Connectivity Test, We assume the cloud will be up 24/7.
		//
		if (connected) {
			// bandwidth will be valid for 600,000 millisecond = 2 * 5 minutes
			if (mBwStartValid + 30000000 < System.currentTimeMillis()
					&& !mBwTesting || (!mBwTesting && upBw == 0 && downBw == 0)) {
				File outputDir = context.getCacheDir(); // context being the
				// Activity pointer
				try {
					File outputFile = File.createTempFile("netProfileImage",
							".jpg", outputDir);
					new DownloadUploadImage().execute(downUrl,
							outputFile.getAbsolutePath(), upUrl);

				} catch (Exception e) {
					Log.e("simplicity", e.toString());
				}
			}
		} else {
			downBw = 0.0;
			ContextEngine.setDownBW(0);
			mBwTesting = false;
			upBw = 0.0;
			ContextEngine.setUpBW(0);
			mBwStartValid = 0;
			cloudConnected = false;
		}

	}

	private class DownloadUploadImage extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			mBwTesting = true;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				long start = System.nanoTime();
				if (!DownloadManager.Download(params[0], params[1])) {
					downBw = 0.0;
					ContextEngine.setDownBW(0);
					upBw = 0.0;
					File f = new File(params[1]);
					ContextEngine.setUpBW(0);
					f.delete();
					return null;
				}
				long end = System.nanoTime();
				File f = new File(params[1]);
				downBw = (f.length() / 1024.0) / ((end - start) / 1000000000.0);
				ContextEngine.setDownBW(downBw);
				start = System.nanoTime();
				UploadManager.Upload(params[1], params[2], "image/jpeg");
				end = System.nanoTime();
				upBw = (f.length() / 1024.0) / ((end - start) / 1000000000.0);
				ContextEngine.setUpBW(upBw);
				f.delete();
			} catch (Exception e) {
				downBw = 0.0;
				ContextEngine.setDownBW(0);
				mBwTesting = false;
				upBw = 0.0;
				ContextEngine.setUpBW(0);
				mBwStartValid = 0;
				cloudConnected = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (downBw == 0.0 || upBw == 0.0) {
				mBwTesting = false;
				mBwStartValid = 0;
				cloudConnected = false;
			} else {
				mBwTesting = false;
				mBwStartValid = System.currentTimeMillis();
				cloudConnected = true;
			}
		}
	}

}
