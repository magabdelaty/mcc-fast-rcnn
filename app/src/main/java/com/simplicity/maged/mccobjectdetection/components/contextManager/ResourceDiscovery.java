package com.simplicity.maged.mccobjectdetection.components.contextManager;

import javax.annotation.Nonnull;

import org.json.JSONArray;
import org.json.JSONObject;

import com.simplicity.maged.mccobjectdetection.components.RESTClient;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ResourceDiscovery {
	private static class ResourceDiscoveryHolder {
		public static final ResourceDiscovery instance = new ResourceDiscovery();
	}

	@Nonnull
	protected static ResourceDiscovery getInstance() {
		return ResourceDiscoveryHolder.instance;
	}

	private ResourceDiscovery() {

	}

	protected synchronized void DoServiceDiscovery(Context context,
			String discoverServiceUrl, long sysBootTime) {
		new CloudServiceDiscovery().execute(context, discoverServiceUrl,
				sysBootTime);
	}

	private class CloudServiceDiscovery extends
			AsyncTask<Object, Context, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			try {
				Log.i("simplicity", "CloudServiceDiscovery_doInBackground");
				JSONObject json = RESTClient.GetJsonRequest(
						(Context) params[0], String.valueOf(params[1]), 100);
				Log.i("simplicity", json.toString());
				JSONArray services = json.getJSONArray("avail_services");
				JSONArray speed_tests = json.getJSONArray("speed_test");
				String provider_uuid = json.getString("provider_uuid");
				String provider_cpu_speed = json.getString("cpu_speed"); // hz
				String provider_cpu_core = json.getString("cpu_core");
                String provider_ram_avail = json.getString("ram_avail"); // byte
				double upBW4 = 0; // Cloud<-->Data_Provider
				double downBW4 = 0;
				double upBW5 = 0; // Cloud<-->User_App
				double downBW5 = 0;
				if (speed_tests.length() >= 1) {
					JSONObject s = speed_tests.getJSONObject(0);
					upBW4 = s.getDouble("upload"); // Mbit/sec
					upBW4 = upBW4 / 8 * 1024; // KB/s
					downBW4 = s.getDouble("download");// Mbit/sec
					downBW4 = downBW4 / 8 * 1024;
				}
				if (speed_tests.length() >= 2) {
					JSONObject s = speed_tests.getJSONObject(1);
					upBW5 = s.getDouble("upload");// Mbit/sec
					upBW5 = upBW5 / 8 * 1024;
					downBW5 = s.getDouble("download");// Mbit/sec
					downBW5 = downBW5 / 8 * 1024;
				}
				Log.i("simplicity", "Discovery: upBW4: " + upBW4);
				Log.i("simplicity", "Discovery: provider_uuid: "
						+ provider_uuid);
				if (services.length() > 0) {
					((Context) params[0])
							.getContentResolver()
							.delete(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
									ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID
											+ " = ?",
									new String[] { provider_uuid });
					// delete * from table where provider_uuid=xyz
				}
				Log.i("simplicity", "services.length: " + services.length());
				for (int i = 0; i < services.length(); i++) {
					JSONObject s = services.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UUID,
							provider_uuid);
                    values.put(
                            ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED,
                            Long.valueOf(provider_cpu_core) * Long.valueOf(provider_cpu_speed));
                    values.put(
                            ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY,
                            provider_ram_avail );
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW4,
							upBW4);
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW4,
							downBW4);
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_UPBW5,
							upBW5);
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_PROVIDER_DOWNBW5,
							downBW5);
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_LAST_BOOT,
							(long) params[2]);
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_ANDROID_SERVICE,
							s.getString("android_service"));
					values.put(
							ContextManagerContentProvider.SERVICE_DISCOVERY_CLOUD_SERVICE,
							s.getString("cloud_service"));
					Uri result = ((Context) params[0])
							.getApplicationContext()
							.getContentResolver()
							.insert(ContextManagerContentProvider.SERVICE_DISCOVERY_CONTENT_URI,
									values);
					Log.i("simplicity",
							"SERVICE_DISCOVERY_CONTENT_URI Inserted: "
									+ result.getLastPathSegment());
				}
				publishProgress((Context) params[0]);
			} catch (Exception e) {
				Log.e("simplicity", e.toString());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Context... values) {
			Toast.makeText(values[0], "Resource Discovery Done.",
					Toast.LENGTH_LONG).show();
		}

	}

}
