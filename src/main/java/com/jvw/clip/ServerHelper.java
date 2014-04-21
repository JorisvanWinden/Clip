package com.jvw.clip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joris on 21-4-14.
 */
public class ServerHelper extends SQLiteOpenHelper {

	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "_name";
	public static final String COLUMN_IP = "_ip";
	public static final String COLUMN_PORT = "_port";
	public static final String DATABASE_CREATE = "create table " + TABLE_SERVERS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_IP + " text, " + COLUMN_PORT + " integer);";
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

	public void addServer(Server item) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ServerHelper.COLUMN_NAME, item.getName());
		values.put(ServerHelper.COLUMN_IP, item.getIp());
		values.put(ServerHelper.COLUMN_PORT, item.getPort());
		db.insert(ServerHelper.TABLE_SERVERS, null, values);
		db.close();
	}

	public void removeServer(Server item) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(
				TABLE_SERVERS,
				COLUMN_NAME + "=? AND " + COLUMN_IP + "=? AND " + COLUMN_PORT + "=?",
				new String[]{item.getName(), item.getIp(), String.valueOf(item.getPort())});
		db.close();
	}

	public Server getServer(int position) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(
				TABLE_SERVERS,
				new String[]{COLUMN_NAME, COLUMN_IP, COLUMN_PORT},
				COLUMN_ID + "=?",
				new String[]{String.valueOf(position)},
				null, null, null, null);
		Server server = new Server(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
		db.close();
		return server;
	}

	public List<Server> getAll() {
		List<Server> servers = new ArrayList<Server>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_SERVERS,
				new String[]{COLUMN_NAME, COLUMN_IP, COLUMN_PORT},
				null,
				null,
				null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				servers.add(new Server(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3))));
			} while (cursor.moveToNext());
		}
		db.close();
		return servers;
	}
}
