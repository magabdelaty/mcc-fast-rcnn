/*
Copyright (C) 2011 The University of Michigan

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Please send inquiries to powertutor@umich.edu
 */

package edu.umich.PowerTutor.ui;

import edu.umich.PowerTutor.service.ICounterService;
import edu.umich.PowerTutor.service.PowerEstimator;
import edu.umich.PowerTutor.service.UMLoggerService;
import edu.umich.PowerTutor.service.UidInfo;
import edu.umich.PowerTutor.util.Counter;
import edu.umich.PowerTutor.util.SystemInfo;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Arrays;

public class PowerProfiler extends Service implements Runnable {
	private static final String TAG = "simplicityEnergy";
	private static final double HIDE_UID_THRESHOLD = 0.1;

	public static final int KEY_CURRENT_POWER = 0;
	public static final int KEY_AVERAGE_POWER = 1;
	public static final int KEY_TOTAL_ENERGY = 2;
	private static final CharSequence[] KEY_NAMES = { "Current power",
			"Average power", "Energy usage" };

	private SharedPreferences prefs;
	private int noUidMask;

	public static final String PROFILEDUID = "profileduid";
	public static final String PROFILERCONTPROVURI = "profilercontentprovideruri";
	private int uid;
	private Uri powerProfContUri;

	private Intent serviceIntent;
	private CounterServiceConnection conn;
	private ICounterService counterService;
	private Handler handler;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		serviceIntent = new Intent(this, UMLoggerService.class);
		getApplicationContext().startService(serviceIntent);
		conn = new CounterServiceConnection();
		Log.i("simplicitEnergy", "PowerProfiler==onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler = new Handler();
		handler.postDelayed(this, 100);
		getApplicationContext().bindService(serviceIntent, conn, 0);
		Log.i("simplicitEnergy", "PowerProfiler==onStartCommand");
		Bundle bundle = intent.getExtras();
		uid = bundle.getInt(PROFILEDUID);
		powerProfContUri = Uri.parse(bundle.getString(PROFILERCONTPROVURI));
		getContentResolver().delete(powerProfContUri, null, null); // delete * from table
		Log.i("simplicitEnergy", "uid = " + String.valueOf(uid));
		// refreshView();
		// If we get killed, after returning from here, restart
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		getApplicationContext().stopService(serviceIntent);
		handler.removeCallbacks(this);
		handler = null;
		Log.i("simplicitEnergy", "PowerProfiler==onDestroy");
	}

	private void refreshView() {
		Log.i("simplicitEnergy", "PowerProfiler==refreshView");
		int keyId = prefs.getInt("topKeyId", KEY_TOTAL_ENERGY);
		try {
			byte[] rawUidInfo = counterService.getUidInfo(Counter.WINDOW_TOTAL,
					noUidMask | 0);
			if (rawUidInfo != null) {
				UidInfo[] uidInfos = (UidInfo[]) new ObjectInputStream(
						new ByteArrayInputStream(rawUidInfo)).readObject();
				double total = 0;
				for (UidInfo uidInfo : uidInfos) {
					if (uidInfo.uid == SystemInfo.AID_ALL)
						continue;
					switch (keyId) {
					case KEY_CURRENT_POWER:
						uidInfo.key = uidInfo.currentPower;
						uidInfo.unit = "W";
						break;
					case KEY_AVERAGE_POWER:
						uidInfo.key = uidInfo.totalEnergy
								/ (uidInfo.runtime == 0 ? 1 : uidInfo.runtime);
						uidInfo.unit = "W";
						break;
					case KEY_TOTAL_ENERGY:
						uidInfo.key = uidInfo.totalEnergy;
						uidInfo.unit = "J";
						break;
					default:
						uidInfo.key = uidInfo.currentPower;
						uidInfo.unit = "W";
					}
					total += uidInfo.key;
				}
				if (total == 0)
					total = 1;
				for (UidInfo uidInfo : uidInfos) {
					uidInfo.percentage = 100.0 * uidInfo.key / total;
				}
				Arrays.sort(uidInfos);

				int sz = 0;
				for (int i = 0; i < uidInfos.length; i++) {
					if (uidInfos[i].uid == SystemInfo.AID_ALL
							|| uidInfos[i].percentage < HIDE_UID_THRESHOLD) {
						continue;
					}
					sz++;
					if (uidInfos[i].uid == uid) {
						String prefix;
						if (uidInfos[i].key > 1e12) {
							prefix = "G";
							uidInfos[i].key /= 1e12;
						} else if (uidInfos[i].key > 1e9) {
							prefix = "M";
							uidInfos[i].key /= 1e9;
						} else if (uidInfos[i].key > 1e6) {
							prefix = "k";
							uidInfos[i].key /= 1e6;
						} else if (uidInfos[i].key > 1e3) {
							prefix = "";
							uidInfos[i].key /= 1e3;
						} else {
							prefix = "m";
						}
						ContentValues values = new ContentValues();
						values.put(PowerProfile.currentPower,
								uidInfos[i].currentPower);
						Log.i("simplicitEnergy","currentPower = " + String.valueOf(uidInfos[i].currentPower));
						values.put(PowerProfile.key, uidInfos[i].key);
						Log.i("simplicitEnergy","key = " + String.valueOf(uidInfos[i].key));
						values.put(PowerProfile.percentage,
								uidInfos[i].percentage);
						values.put(PowerProfile.runtime,
								uidInfos[i].runtime);
						values.put(PowerProfile.timestamp,
								System.nanoTime());
						values.put(PowerProfile.totalEnergy,
								uidInfos[i].totalEnergy);
						values.put(PowerProfile.uid, uidInfos[i].uid);
						values.put(PowerProfile.unit, prefix + uidInfos[i].unit);
						Log.i("simplicitEnergy", "unit = " + prefix
								+ uidInfos[i].unit);
						Uri result = getContentResolver().insert(powerProfContUri,
								values);
						long id = Long.parseLong(result.getLastPathSegment());
						Log.i("simplicitEnergy", "PowerProfiler_record_ID = " + String.valueOf(id));
					}
				}
			}
		} catch (IOException e) {
		} catch (RemoteException e) {
		} catch (ClassNotFoundException e) {
		} catch (ClassCastException e) {
		}

	}

	public void run() {
		Log.i("simplicitEnergy", "PowerProfiler==run");
		if (counterService != null) {
			refreshView();
		}

		if (handler != null) {
			handler.postDelayed(this, 1 * PowerEstimator.ITERATION_INTERVAL); // 2
		}
	}

	private class CounterServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className,
				IBinder boundService) {
			counterService = ICounterService.Stub
					.asInterface((IBinder) boundService);
			Log.i("simplicitEnergy", "PowerProfiler===counterService====1");
			try {
				noUidMask = counterService.getNoUidMask();
				Log.i("simplicitEnergy", "PowerProfiler===counterService====2");

			} catch (RemoteException e) {
				counterService = null;
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.i("simplicitEnergy", "PowerProfiler===onServiceDisconnected");
			counterService = null;
			getApplicationContext().unbindService(conn);
			getApplicationContext().bindService(serviceIntent, conn, 0);
			Log.w(TAG, "Unexpectedly lost connection to service");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
