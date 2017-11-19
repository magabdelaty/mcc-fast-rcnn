package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ContextManagerContentProvider extends ContentProvider {
	public static final String AUTHORITY = "com.simplicity.maged.mccobjectdetection.context.provider";
	public static final String DEV_SPECS_BASE_PATH = "device_specs";
	public static final Uri DEV_SPECS_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + DEV_SPECS_BASE_PATH);
	public static final String DEV_SENSORS_BASE_PATH = "device_sensors";
	public static final Uri DEV_SENSORS_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + DEV_SENSORS_BASE_PATH);
	public static final String SERVICE_DISCOVERY_BASE_PATH = "service_discovery";
	public static final Uri SERVICE_DISCOVERY_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + SERVICE_DISCOVERY_BASE_PATH);
	private ContextDatabaseHelper dbHelper;

	// vnd: indicates vendor-specific MIME types
	static final String DEV_SPECS_SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.simplicity.maged.mccobjectdetection.context.provider.device_specs";
	static final String DEV_SPECS_MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.com.simplicity.maged.mccobjectdetection.context.provider.device_specs";

	static final String DEV_SENSORS_SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.simplicity.maged.mccobjectdetection.context.provider.device_sensors";
	static final String DEV_SENSORS_MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.com.simplicity.maged.mccobjectdetection.context.provider.device_sensors";

	static final String SERVICE_DISCOVERY_SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.simplicity.maged.mccobjectdetection.context.provider.service_discovery";
	static final String SERVICE_DISCOVERY_MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.com.simplicity.maged.mccobjectdetection.context.provider.service_discovery";

	private static final int DEV_SPEC = 100;
	private static final int DEV_SPECS = 101;
	private static final int DEV_SENSOR = 110;
	private static final int DEV_SENSORS = 111;
	private static final int SERVICE_DISCOVERY = 120;
	private static final int SERVICE_DISCOVERIES = 121;

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, DEV_SPECS_BASE_PATH, DEV_SPECS);
		URI_MATCHER.addURI(AUTHORITY, DEV_SPECS_BASE_PATH + "/#", DEV_SPEC);
		URI_MATCHER.addURI(AUTHORITY, DEV_SENSORS_BASE_PATH, DEV_SENSORS);
		URI_MATCHER.addURI(AUTHORITY, DEV_SENSORS_BASE_PATH + "/#", DEV_SENSOR);
		URI_MATCHER.addURI(AUTHORITY, SERVICE_DISCOVERY_BASE_PATH,
				SERVICE_DISCOVERIES);
		URI_MATCHER.addURI(AUTHORITY, SERVICE_DISCOVERY_BASE_PATH + "/#",
				SERVICE_DISCOVERY);
	}

	public static final String DEV_SPECS_Col_ID = "_ID";
	public static final String DEV_SPECS_DISP_SIZE_X = "disp_size_x";
	public static final String DEV_SPECS_DISP_SIZE_Y = "disp_size_y";
	public static final String DEV_SPECS_CPU = "cpu";
	public static final String DEV_SPECS_RAM = "ram";
	public static final String DEV_SPECS_SDK = "sdk";
	public static final String DEV_SPECS_3G = "mob_internet";
	public static final String DEV_SPECS_WIFI = "wifi";
	public static final String DEV_SPECS_LAST_BOOT = "last_boot";
	public static final String[] DEV_SPECS_COLUMNS = { DEV_SPECS_Col_ID,
			DEV_SPECS_DISP_SIZE_X, DEV_SPECS_DISP_SIZE_Y, DEV_SPECS_CPU,
			DEV_SPECS_RAM, DEV_SPECS_SDK, DEV_SPECS_3G, DEV_SPECS_WIFI,
			DEV_SPECS_LAST_BOOT };

	public static final String DEV_SENSORS_Col_ID = "_ID";
	public static final String DEV_SENSORS_TYPE = "type";
	public static final String DEV_SENSORS_NAME = "name";
	public static final String DEV_SENSORS_LAST_BOOT = "last_boot";
	public static final String[] DEV_SENSORS_COLUMNS = { DEV_SENSORS_Col_ID,
			DEV_SENSORS_TYPE, DEV_SENSORS_NAME, DEV_SENSORS_LAST_BOOT };

	public static final String SERVICE_DISCOVERY_Col_ID = "_ID";
	public static final String SERVICE_DISCOVERY_PROVIDER_UUID = "provider_uuid";
	public static final String SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED = "provider_compute_speed";
	public static final String SERVICE_DISCOVERY_PROVIDER_AVAILENERGY = "provider_avail_energy";
	public static final String SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY = "provider_avail_memory";
	public static final String SERVICE_DISCOVERY_PROVIDER_UPBW4 = "provider_upBW4";
	public static final String SERVICE_DISCOVERY_PROVIDER_DOWNBW4 = "provider_downBW4";
	public static final String SERVICE_DISCOVERY_PROVIDER_UPBW5 = "provider_upBW5";
	public static final String SERVICE_DISCOVERY_PROVIDER_DOWNBW5 = "provider_downBW5";
	public static final String SERVICE_DISCOVERY_ANDROID_SERVICE = "android_service";
	public static final String SERVICE_DISCOVERY_CLOUD_SERVICE = "cloud_service";
	public static final String SERVICE_DISCOVERY_LAST_BOOT = "last_boot";
	public static final String[] SERVICE_DISCOVERY_COLUMNS = {
			SERVICE_DISCOVERY_Col_ID, SERVICE_DISCOVERY_PROVIDER_UUID,
			SERVICE_DISCOVERY_PROVIDER_COMPUTESPEED,
			SERVICE_DISCOVERY_PROVIDER_AVAILENERGY,
			SERVICE_DISCOVERY_PROVIDER_AVAILMEMORY,
			SERVICE_DISCOVERY_PROVIDER_UPBW4,
			SERVICE_DISCOVERY_PROVIDER_DOWNBW4,
			SERVICE_DISCOVERY_PROVIDER_UPBW5,
			SERVICE_DISCOVERY_PROVIDER_DOWNBW5,
			SERVICE_DISCOVERY_ANDROID_SERVICE, SERVICE_DISCOVERY_CLOUD_SERVICE,
			SERVICE_DISCOVERY_LAST_BOOT };

	private void checkColumns(String[] projection, String[] COLUMNS) {
		if (projection != null) {
			HashSet<String> request = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> available = new HashSet<String>(
					Arrays.asList(COLUMNS));
			if (!available.containsAll(request)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = new ContextDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int type = URI_MATCHER.match(uri);

		switch (type) {
		case DEV_SPECS:
			// there not to do if the query is for the table
			queryBuilder.setTables(ContextDatabaseHelper.DEV_SPECS_TABLE_NAME);
			checkColumns(projection, DEV_SPECS_COLUMNS);
			break;
		case DEV_SPEC:
			queryBuilder.setTables(ContextDatabaseHelper.DEV_SPECS_TABLE_NAME);
			checkColumns(projection, DEV_SPECS_COLUMNS);
			queryBuilder.appendWhere(DEV_SPECS_Col_ID + " = "
					+ uri.getLastPathSegment());
			break;
		case DEV_SENSORS:
			// there not to do if the query is for the table
			queryBuilder
					.setTables(ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME);
			checkColumns(projection, DEV_SENSORS_COLUMNS);
			break;
		case DEV_SENSOR:
			queryBuilder
					.setTables(ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME);
			checkColumns(projection, DEV_SENSORS_COLUMNS);
			queryBuilder.appendWhere(DEV_SENSORS_Col_ID + " = "
					+ uri.getLastPathSegment());
			break;
		case SERVICE_DISCOVERIES:
			// there not to do if the query is for the table
			queryBuilder
					.setTables(ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME);
			checkColumns(projection, SERVICE_DISCOVERY_COLUMNS);
			break;
		case SERVICE_DISCOVERY:
			queryBuilder
					.setTables(ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME);
			checkColumns(projection, SERVICE_DISCOVERY_COLUMNS);
			queryBuilder.appendWhere(SERVICE_DISCOVERY_Col_ID + " = "
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case DEV_SPECS:
			return DEV_SPECS_MULTIPLE_RECORDS_MIME_TYPE;
		case DEV_SPEC:
			return DEV_SPECS_SINGLE_RECORD_MIME_TYPE;
		case DEV_SENSORS:
			return DEV_SENSORS_MULTIPLE_RECORDS_MIME_TYPE;
		case DEV_SENSOR:
			return DEV_SENSORS_SINGLE_RECORD_MIME_TYPE;
		case SERVICE_DISCOVERIES:
			return SERVICE_DISCOVERY_MULTIPLE_RECORDS_MIME_TYPE;
		case SERVICE_DISCOVERY:
			return SERVICE_DISCOVERY_SINGLE_RECORD_MIME_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int type = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Long id;
		String BASE_PATH;
		switch (type) {
		case DEV_SPECS:
			id = db.insert(ContextDatabaseHelper.DEV_SPECS_TABLE_NAME, null,
					values);
			BASE_PATH = DEV_SPECS_BASE_PATH;
			break;
		case DEV_SENSORS:
			id = db.insert(ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME, null,
					values);
			BASE_PATH = DEV_SENSORS_BASE_PATH;
			break;
		case SERVICE_DISCOVERIES:
			id = db.insert(ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
					null, values);
			BASE_PATH = SERVICE_DISCOVERY_BASE_PATH;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int type = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows;
		String id;
		switch (type) {
		case DEV_SPECS:
			affectedRows = db.delete(
					ContextDatabaseHelper.DEV_SPECS_TABLE_NAME, selection,
					selectionArgs);
			break;

		case DEV_SPEC:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.delete(
						ContextDatabaseHelper.DEV_SPECS_TABLE_NAME,
						DEV_SPECS_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.delete(
						ContextDatabaseHelper.DEV_SPECS_TABLE_NAME,
						DEV_SPECS_Col_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		case DEV_SENSORS:
			affectedRows = db.delete(
					ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME, selection,
					selectionArgs);
			break;

		case DEV_SENSOR:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.delete(
						ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME,
						DEV_SENSORS_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.delete(
						ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME,
						DEV_SENSORS_Col_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		case SERVICE_DISCOVERIES:
			affectedRows = db.delete(
					ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
					selection, selectionArgs);
			break;

		case SERVICE_DISCOVERY:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.delete(
						ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
						SERVICE_DISCOVERY_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.delete(
						ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
						SERVICE_DISCOVERY_Col_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affectedRows;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int type = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows;
		String id;
		switch (type) {
		case DEV_SPECS:
			affectedRows = db.update(
					ContextDatabaseHelper.DEV_SPECS_TABLE_NAME, values,
					selection, selectionArgs);
			break;

		case DEV_SPEC:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.update(
						ContextDatabaseHelper.DEV_SPECS_TABLE_NAME, values,
						DEV_SPECS_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.update(
						ContextDatabaseHelper.DEV_SPECS_TABLE_NAME, values,
						DEV_SPECS_Col_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		case DEV_SENSORS:
			affectedRows = db.update(
					ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME, values,
					selection, selectionArgs);
			break;

		case DEV_SENSOR:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.update(
						ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME, values,
						DEV_SENSORS_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.update(
						ContextDatabaseHelper.DEV_SENSORS_TABLE_NAME, values,
						DEV_SENSORS_Col_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		case SERVICE_DISCOVERIES:
			affectedRows = db.update(
					ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME, values,
					selection, selectionArgs);
			break;

		case SERVICE_DISCOVERY:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.update(
						ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
						values, SERVICE_DISCOVERY_Col_ID + "=" + id, null);
			} else {
				affectedRows = db.update(
						ContextDatabaseHelper.SERVICE_DISCOVERY_TABLE_NAME,
						values, SERVICE_DISCOVERY_Col_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affectedRows;
	}

}
