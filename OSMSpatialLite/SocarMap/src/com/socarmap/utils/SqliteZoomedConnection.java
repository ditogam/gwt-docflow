package com.socarmap.utils;

import jsqlite.Stmt;
import android.content.Context;

public class SqliteZoomedConnection implements ZoomConnection {
	@SuppressWarnings("unused")
	private Context context;

	jsqlite.Database lastConnection;

	public SqliteZoomedConnection(Context context) {
		this.context = context;
	}

	@Override
	public void close() {

	}

	@Override
	public byte[] getImageData(ZX1X2 zx1x2, int zoom, int x, int y,
			boolean doNotClose) throws Exception {
		if (!doNotClose && lastConnection != null) {
			lastConnection.close();
			lastConnection = null;
		}
		if (lastConnection == null) {
			lastConnection = new jsqlite.Database();

			lastConnection.open(zx1x2.getFile().getAbsolutePath(),
					jsqlite.Constants.SQLITE_OPEN_READONLY);
		}
		Stmt stmt = null;
		try {
			stmt = lastConnection
					.prepare("select file_data from mapfiledatazxy where zoom=? and x=? and y=?");
			try {
				stmt.bind(1, zoom);
				stmt.bind(2, x);
				stmt.bind(3, y);
				if (stmt.step())
					return Utils.decompress(stmt.column_bytes(0));

			} catch (Exception e) {

			}
		} catch (Exception e) {

		} finally {
			stmt.close();
		}

		return null;
	}

	@Override
	public void updateImageData(ZX1X2 zx1x2, int zoom, int x, int y, byte[] data)
			throws Exception {
		jsqlite.Database db = null;
		try {
			db = new jsqlite.Database();
			db.open(zx1x2.getFile().getAbsolutePath(),
					jsqlite.Constants.SQLITE_OPEN_READWRITE);
			Stmt stmt = db
					.prepare("delete from mapfiledatazxy where zoom=? and x=? and y=?");
			stmt.bind(1, zoom);
			stmt.bind(2, x);
			stmt.bind(3, y);
			stmt.step();
			stmt.close();
			stmt = db
					.prepare("insert into mapfiledatazxy(zoom,x,y,file_data) values (?,?,?,?)");
			stmt.bind(1, zoom);
			stmt.bind(2, x);
			stmt.bind(3, y);
			stmt.bind(4, data);
			stmt.step();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				db.close();
			} catch (Exception e2) {

			}
		}

	}
}
