package com.simplicity.maged.mccobjectdetection.components.contextManager;

import com.simplicity.maged.mccobjectdetection.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ContextEngineService extends Service {
	Runnable runnable;
	Handler handler;
	int counter;
	static Boolean flagAvailServices;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
			try {
				Log.i("simplicity", "Received Start Foreground Intent");
				Intent stopIntent = new Intent(this, ContextEngineService.class);
				stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
				PendingIntent pstopIntent = PendingIntent.getService(this, 0,
						stopIntent, 0);

				Notification notification = new Notification.Builder(this)
						.setSmallIcon(R.drawable.execengine)
						.setTicker(getString(R.string.ticker_text))
						.addAction(android.R.drawable.ic_delete, "Stop",
								pstopIntent)
						.setContentText(
								getString(R.string.notification_content_text))
						.setOngoing(true).setWhen(System.currentTimeMillis())
						.build();
				startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
						notification);
				counter = 0;
				flagAvailServices = UserPreferences
						.getAvailable_services(getApplicationContext());
				if (handler == null) {
					handler = new Handler();
				}
				runnable = new Runnable() {

					@Override
					public void run() {
						ContextEngine.UpdateNetworkStatus(
								getApplicationContext(),
								getString(R.string.serverURL)
										+ "netprofile/SSMountain500.jpg",
								getString(R.string.serverURL) + "netprofile");
						ContextEngine
								.InitializeProvidersList(getApplicationContext());
						if (!flagAvailServices
								&& ContextEngine.getInternetConnected()) {
							// && ContextEngine.getCloudConnected()
							Log.i("simplicity",
									"Available service is being updated.");
							ContextEngine.UpdateAvailableResources(
									getApplicationContext(),
									getString(R.string.serverURL)
											+ "service_discovery", false);
							flagAvailServices = true;
							UserPreferences.setgetAvailable_services(true,
									getApplicationContext());
						}
						if (!ContextEngine.getInternetConnected())
							counter++;
						if (!ContextEngine.getInternetConnected()
								&& counter > 1000) {
							// 1000 milli = 1 second
							if (handler != null) {
								handler.removeCallbacks(runnable);
							}
							stopForeground(true);
							stopSelf();
						} else if (UserPreferences
								.getSimple_Decision(getApplicationContext())) {
							// simple decision && decrease overhead
							// stop service
							if (handler != null) {
								handler.removeCallbacks(runnable);
							}
							stopForeground(true);
							stopSelf();
						} else {
							// Note 10 milliseconds ???
							handler.postDelayed(this, 10);
						}
					}
				};
				handler.postDelayed(runnable, 0); // now
			} catch (Exception e) {
				Log.e("simplicity", e.toString());
			}

		} else if (intent.getAction().equals(
				Constants.ACTION.STOPFOREGROUND_ACTION)) {
			Log.i("simplicity", "Received Stop Foreground Intent");
			if (handler != null) {
				handler.removeCallbacks(runnable);
			}
			stopForeground(true);
			stopSelf();
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
