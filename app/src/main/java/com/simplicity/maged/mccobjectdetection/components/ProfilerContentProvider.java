package com.simplicity.maged.mccobjectdetection.components;

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

public class ProfilerContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.simplicity.maged.mccobjectdetection.profiler.provider";
	public static final String BASE_PATH_PROFILE_STORE = "profiles";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH_PROFILE_STORE);
	private ProfilerDatabaseHelper dbHelper;

	// vnd: indicates vendor-specific MIME types
	static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.simplicity.maged.mccobjectdetection.profiler.provider.Profiles";
	static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.com.simplicity.maged.mccobjectdetection.profiler.provider.Profiles";

	private static final int PROFILE = 100;
	private static final int PROFILES = 101;
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH_PROFILE_STORE, PROFILES);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH_PROFILE_STORE + "/#", PROFILE);

	}

	public static final String Col_ID = "_ID";
	public static final String PROVIDERUUID = "provider_uuid";
	public static final String ServiceName = "service_name";
	public static final String ExecCost = "exec_cost";
	public static final String TotalExecCost = "total_exec_cost";
	public static final String UPDataSize = "up_data_size";
	public static final String DownDataSize = "down_data_size";
	public static final String DataProvider = "data_provider";
	public static final String MemRequired = "mem_required";
	public static final String BattConsumed = "batt_consumed";
	public static final String BattConsumUnit = "batt_consum_unit";
	public static final String[] COLUMNS = { Col_ID, PROVIDERUUID, ServiceName,
			ExecCost, TotalExecCost, UPDataSize, DownDataSize, DataProvider,
			MemRequired, BattConsumed, BattConsumUnit };

	private void checkColumns(String[] projection) {
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
		dbHelper = new ProfilerDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ProfilerDatabaseHelper.TABLE_NAME);
		checkColumns(projection);
		int type = URI_MATCHER.match(uri);
		switch (type) {
		case PROFILES:
			// there not to do if the query is for the table
			break;
		case PROFILE:
			queryBuilder.appendWhere(Col_ID + " = " + uri.getLastPathSegment());
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
		case PROFILES:
			return MULTIPLE_RECORDS_MIME_TYPE;
		case PROFILE:
			return SINGLE_RECORD_MIME_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int type = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Long id;
		switch (type) {
		case PROFILES:
			id = db.insert(ProfilerDatabaseHelper.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH_PROFILE_STORE + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int type = URI_MATCHER.match(uri);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int affectedRows;
		switch (type) {
		case PROFILES:
			affectedRows = db.delete(ProfilerDatabaseHelper.TABLE_NAME,
					selection, selectionArgs);
			break;

		case PROFILE:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.delete(ProfilerDatabaseHelper.TABLE_NAME,
						Col_ID + "=" + id, null);
			} else {
				affectedRows = db.delete(ProfilerDatabaseHelper.TABLE_NAME,
						Col_ID + "=" + id + " and " + selection, selectionArgs);
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
		switch (type) {
		case PROFILES:
			affectedRows = db.update(ProfilerDatabaseHelper.TABLE_NAME, values,
					selection, selectionArgs);
			break;

		case PROFILE:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				affectedRows = db.update(ProfilerDatabaseHelper.TABLE_NAME,
						values, Col_ID + "=" + id, null);
			} else {
				affectedRows = db.update(ProfilerDatabaseHelper.TABLE_NAME,
						values, Col_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affectedRows;
	}

}
