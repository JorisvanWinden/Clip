package com.jvw.clip;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joris on 21-4-14.
 */
public class ServerHelper extends SQLiteOpenHelper {

	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_NAME = "_name";
	public static final String COLUMN_IP = "_ip";
	public static final String COLUMN_PORT = "_port";
	public static final String DATABASE_CREATE = "create table " + TABLE_SERVERS + "(" + COLUMN_NAME + " text not null, " + COLUMN_IP + " text primary key, " + COLUMN_PORT + " integer);";
	public static final String DATABASE_NAME = "servers.db";
	public static final int DATABASE_VERSION = 1;

	public ServerHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
