package com.simplicity.maged.mccobjectdetection.components;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

public class RESTClient {

	private static RequestQueue mRequestQueue;
	private static final String REQUEST_TAG = "RESTClient-RequestTag";

	public static String GetStringRequest(Context context, String url,
			final int timeout) {
		final RequestFuture<String> futureRequest = RequestFuture.newFuture();
		mRequestQueue = SingletonVolleyRequestQueue.getInstance(
				context.getApplicationContext()).getRequestQueue();
		final StringRequest stringRequest = new StringRequest(
				Request.Method.GET, url, futureRequest, futureRequest);
		stringRequest.setTag(REQUEST_TAG);
		stringRequest.setShouldCache(false);
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(timeout * 1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);
		try {
			return futureRequest.get(timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof ServerError) {
				ServerError error = (ServerError) cause;
				String json = null;
				NetworkResponse response = error.networkResponse;
				if (response != null && response.data != null) {
					json = new String(response.data);
					json = trimMessage(json, "description");
					Log.e("simplicity", "Server error: "
							+ json);
					return "Error: " + json;
				}
			}
			Log.e("simplicity", e.toString());
			return "Error: " + e.toString();
		}
	}

	static String trimMessage(String json, String key) {
		String trimmedString = null;

		try {
			JSONObject obj = new JSONObject(json);
			trimmedString = obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return trimmedString;
	}

	public static JSONObject GetJsonRequest(Context context, String url,
			final int timeout) {
		final RequestFuture<JSONObject> futureRequest = RequestFuture
				.newFuture();
		mRequestQueue = SingletonVolleyRequestQueue.getInstance(
				context.getApplicationContext()).getRequestQueue();
		final JsonObjectRequest jsObjRequest = new JsonObjectRequest(url, null,
				futureRequest, futureRequest);
		jsObjRequest.setTag(REQUEST_TAG);
		jsObjRequest.setShouldCache(false);
		jsObjRequest
				.setRetryPolicy(new DefaultRetryPolicy(timeout * 1000, 0, 0));
		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		mRequestQueue.add(jsObjRequest);
		try {
			return futureRequest.get(timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			Log.e("simplicity", e.toString());
		}
		return null;
	}

	public void CancelAllReq() {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(REQUEST_TAG);
		}
	}

	public static String PostRequest(Context context, String url,
			final String filePath, final String contentType, long timeout) {
		final RequestFuture<String> futureRequest = RequestFuture.newFuture();
		mRequestQueue = SingletonVolleyRequestQueue.getInstance(
				context.getApplicationContext()).getRequestQueue();
		final StringRequest stringRequest = new StringRequest(
				Request.Method.POST, url, futureRequest, futureRequest) {
			@Override
			public byte[] getBody() throws AuthFailureError {
				try {
					return readFile(new File(filePath));
				} catch (IOException e) {
					Log.e("simplicity", e.toString());
				}
				return super.getBody();
			}

			@Override
			public String getBodyContentType() {

				return contentType;
			}
		};
		stringRequest.setTag(REQUEST_TAG);
		mRequestQueue.add(stringRequest);
		try {
			return futureRequest.get(timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			Log.e("simplicity", e.toString());
		}
		return null;
	}

	private static byte[] readFile(File file) throws IOException {
		// Open file
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			// Get and check length
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength)
				throw new IOException("File size >= 2 GB");
			// Read file and return data
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		} finally {
			f.close();
		}
	}

}
