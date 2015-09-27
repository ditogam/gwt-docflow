package com.socarmap.db;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConnection extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	public SQLiteConnection(Context context, File file) {
		super(context, file.getAbsolutePath(), null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
