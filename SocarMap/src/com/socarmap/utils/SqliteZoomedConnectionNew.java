package com.socarmap.utils;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.socarmap.db.SQLiteConnection;

public class SqliteZoomedConnectionNew implements ZoomConnection {
	public static final ArrayList<ZX1X2> getminiMax(Context context, File file) {

		SQLiteConnection connectionHelper = null;
		SQLiteDatabase database = null;
		ArrayList<ZX1X2> list = new ArrayList<ZX1X2>();
		try {
			connectionHelper = new SQLiteConnection(context, file);
			database = connectionHelper.getReadableDatabase();
			Cursor cursor = database.query("zooms",
					"zoom,minx,maxx".split(","), null, null, null, null, null);

			try {

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					list.add(new ZX1X2(cursor.getInt(0), cursor.getInt(1),
							cursor.getInt(2), file));
					cursor.moveToNext();
				}
				// Make sure to close the cursor
				cursor.close();

			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				try {
					database.close();
				} catch (Throwable e2) {

				}
			}

		} catch (Throwable e) {

		} finally {
			try {
				connectionHelper.close();
			} catch (Throwable e2) {

			}
		}
		return list;
	}

	private Context context;

	private SQLiteDatabase lastConnection;
	private SQLiteConnection lastConnectionHelper;

	public SqliteZoomedConnectionNew(Context context) {
		this.context = context;
	}

	@Override
	public void close() {
		try {
			if (lastConnection != null)
				lastConnection.close();
		} catch (Throwable e) {

		}
		try {
			if (lastConnectionHelper != null)
				lastConnectionHelper.close();
		} catch (Throwable e) {

		}
		lastConnection = null;
		lastConnectionHelper = null;
	}

	// @Override
	// public void updateImageData(ZX1X2 zx1x2, int zoom, int x, int y, byte[]
	// data)
	// throws Exception {
	// jsqlite.Database db = null;
	// try {
	// db = new jsqlite.Database();
	// db.open(zx1x2.getFile().getAbsolutePath(),
	// jsqlite.Constants.SQLITE_OPEN_READWRITE);
	// Stmt stmt = db
	// .prepare("delete from mapfiledatazxy where zoom=? and x=? and y=?");
	// stmt.bind(1, zoom);
	// stmt.bind(2, x);
	// stmt.bind(3, y);
	// stmt.step();
	// stmt.close();
	// stmt = db
	// .prepare("insert into mapfiledatazxy(zoom,x,y,file_data) values (?,?,?,?)");
	// stmt.bind(1, zoom);
	// stmt.bind(2, x);
	// stmt.bind(3, y);
	// stmt.bind(4, data);
	// stmt.step();
	//
	// } catch (Throwable e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// db.close();
	// } catch (Throwable e2) {
	// n
	// }
	// }
	//
	// }

	@Override
	public byte[] getImageData(ZX1X2 zx1x2, int zoom, int x, int y,
			boolean doNotClose) throws Exception {
		if (!doNotClose && lastConnection != null) {
			lastConnectionHelper.close();
			lastConnection.close();
			lastConnection = null;
		}
		if (lastConnection == null) {
			lastConnectionHelper = new SQLiteConnection(context,
					zx1x2.getFile());

			lastConnection = lastConnectionHelper.getReadableDatabase();
		}
		Cursor cursor = null;
		try {
			cursor = lastConnection
					.rawQuery(
							"select file_data from mapfiledatazxy where zoom=? and x=? and y=?",
							new String[] { zoom + "", x + "", y + "" });
			try {

				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					return Utils.decompress(cursor.getBlob(0));
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				cursor.close();
			} catch (Throwable e2) {

			}
		}

		return null;
	}

	@Override
	public void updateImageData(ZX1X2 zx1x2, int zoom, int x, int y, byte[] data)
			throws Exception {
		SQLiteConnection connectionHelper = null;
		SQLiteDatabase database = null;
		try {
			connectionHelper = new SQLiteConnection(context, zx1x2.getFile());
			database = connectionHelper.getReadableDatabase();
			database.delete("mapfiledatazxy", "zoom=? and x=? and y=?",
					new String[] { "" + zoom, x + "", y + "" });
			ContentValues insertValues = new ContentValues();
			insertValues.put("zoom", zoom);
			insertValues.put("x", x);
			insertValues.put("y", y);
			insertValues.put("file_data", data);
			long rowId = database.insert("mapfiledatazxy", null, insertValues);
			System.out.println(rowId);
		} catch (Throwable e) {
			throw new Exception(e);
		} finally {
			try {
				database.close();
			} catch (Throwable e2) {

			}
			try {
				connectionHelper.close();
			} catch (Throwable e2) {

			}
		}

	}
}
