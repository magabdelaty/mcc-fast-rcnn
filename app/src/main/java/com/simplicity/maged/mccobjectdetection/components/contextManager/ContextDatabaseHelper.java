package com.simplicity.maged.mccobjectdetection.components.contextManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContextDatabaseHelper extends SQLiteOpenHelper {

	public static final String name = "SimplicityContext.db";
	public static final CursorFactory factory = null;
	public static final int version = 1;

	public static final String DEV_SPECS_TABLE_NAME = "device_specs_store";
	private static final String DEV_SPECS_SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ DEV_SPECS_TABLE_NAME
			+ " (_ID INTEGER PRIMARY KEY, disp_size_x INTEGER, disp_size_y INTEGER,"
			+ " cpu REAL, ram INTEGER, sdk INTEGER, mob_internet INTEGER, wifi INTEGER,"
			+ " last_boot INTEGER )";

	private static final String DEV_SPECS_SQL_DROP = "DROP TABLE IS EXISTS "
			+ DEV_SPECS_TABLE_NAME;

	public static final String DEV_SENSORS_TABLE_NAME = "device_sensors_store";
	private static final String DEV_SENSORS_SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ DEV_SENSORS_TABLE_NAME
			+ " (_ID INTEGER PRIMARY KEY, type INTEGER, name TEXT, last_boot INTEGER )";

	private static final String DEV_SENSORS_SQL_DROP = "DROP TABLE IS EXISTS "
			+ DEV_SENSORS_TABLE_NAME;

	public static final String SERVICE_DISCOVERY_TABLE_NAME = "service_discovery_store";
	private static final String SERVICE_DISCOVERY_SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SERVICE_DISCOVERY_TABLE_NAME
			+ " (_ID INTEGER PRIMARY KEY, provider_uuid TEXT, provider_compute_speed TEXT,"
			+ " provider_avail_energy TEXT, provider_avail_memory TEXT, provider_upBW4 TEXT,"
			+ " provider_downBW4 TEXT, provider_upBW5 TEXT, provider_downBW5 TEXT,"
			+ " android_service TEXT, cloud_service TEXT, last_boot INTEGER )";

	private static final String SERVICE_DISCOVERY_SQL_DROP = "DROP TABLE IS EXISTS "
			+ SERVICE_DISCOVERY_TABLE_NAME;

	public ContextDatabaseHelper(Context context) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DEV_SPECS_SQL_CREATE);
		db.execSQL(DEV_SENSORS_SQL_CREATE);
		db.execSQL(SERVICE_DISCOVERY_SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DEV_SPECS_SQL_DROP);
		db.execSQL(DEV_SENSORS_SQL_DROP);
		db.execSQL(SERVICE_DISCOVERY_SQL_DROP);
		onCreate(db);
	}

}
