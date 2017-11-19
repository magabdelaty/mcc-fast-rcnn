package com.simplicity.maged.mccobjectdetection.components;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfilerDatabaseHelper extends SQLiteOpenHelper {

	public static final String name = "SimplicityProfiler.db";
	public static final CursorFactory factory = null;
	public static final int version = 1;

	public static final String TABLE_NAME = "profile_store";
	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ " (_ID INTEGER PRIMARY KEY, provider_uuid TEXT, service_name TEXT , total_exec_cost INTEGER , exec_cost INTEGER ,"
			+ " up_data_size INTEGER, down_data_size INTEGER, data_provider INTEGER,"
			+ " mem_required REAL, batt_consumed REAL, batt_consum_unit TEXT )";

	private static final String SQL_DROP = "DROP TABLE IS EXISTS " + TABLE_NAME;

	public static final String BATT_TABLE_NAME = "batt_profile_store";
	private static final String BATT_SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ BATT_TABLE_NAME
			+ " (_ID INTEGER PRIMARY KEY, uid TEXT, currentPower TEXT, "
			+ "totalEnergy TEXT, runtime TEXT, timestamp INTEGER, key TEXT, "
			+ "percentage TEXT, prefix TEXT, unit TEXT )";

	private static final String BATT_SQL_DROP = "DROP TABLE IS EXISTS "
			+ BATT_TABLE_NAME;

	public ProfilerDatabaseHelper(Context context) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		db.execSQL(BATT_SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(BATT_SQL_DROP);
		onCreate(db);
	}

}
