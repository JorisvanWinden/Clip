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
public class ServerDataBase extends SQLiteOpenHelper {

	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "_name";
	public static final String COLUMN_IP = "_ip";
	public static final String COLUMN_PORT = "_port";
	public static final String[] COLUMNS = new String[]{COLUMN_NAME, COLUMN_IP, COLUMN_PORT};
	public static final String DATABASE_CREATE = "create table " + TABLE_SERVERS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_IP + " text, " + COLUMN_PORT + " integer);";
	public static final String DATABASE_NAME = "servers.db";
	public static final int DATABASE_VERSION = 1;

	public ServerDataBase(Context context) {
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
		values.put(ServerDataBase.COLUMN_NAME, item.getName());
		values.put(ServerDataBase.COLUMN_IP, item.getIp());
		values.put(ServerDataBase.COLUMN_PORT, item.getPort());
		db.insert(ServerDataBase.TABLE_SERVERS, null, values);
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
				COLUMNS,
				COLUMN_ID + "=?",
				new String[]{String.valueOf(position)},
				null, null, null, null);
		Server server = new Server(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
		db.close();
		cursor.close();
		return server;
	}

	public List<Server> getAll() {
		List<Server> servers = new ArrayList<Server>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_SERVERS,
				COLUMNS,
				null,
				null,
				null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				servers.add(new Server(cursor.getString(0), cursor.getString(1), cursor.getInt(2)));
			} while (cursor.moveToNext());
		}
		db.close();
		cursor.close();
		return servers;
	}
}
