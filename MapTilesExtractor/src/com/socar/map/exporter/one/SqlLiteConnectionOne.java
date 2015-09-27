package com.socar.map.exporter.one;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.socar.map.downloader.Timer;

public class SqlLiteConnectionOne {
	private static final String COMMIT_LITE_ONE_TIME = "COMMIT_LITE_ONE_TIME";
	private Connection conn;

	private PreparedStatement psInsert;
	private PreparedStatement psDelete;
	boolean executed = false;
	private File file;

	public SqlLiteConnectionOne(File file) {
		this.file = file;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}

	public void commit() throws Exception {
		if (executed) {
			Timer.start(COMMIT_LITE_ONE_TIME);
			conn.commit();
			Timer.step(COMMIT_LITE_ONE_TIME);
		}
		executed = false;
	}

	public void execute(int zoom, int x, int y, byte[] data, int rcn, long date)
			throws Exception {
		if (psInsert == null)
			recreateStatement();
		try {
			psInsert.setInt(1, zoom);
		} catch (Exception e) {
			recreateStatement();
		}
		psInsert.setInt(1, zoom);
		psInsert.setInt(2, x);
		psInsert.setInt(3, y);
		psInsert.setBytes(4, data);
		psInsert.setInt(5, rcn);
		psInsert.setLong(6, date);

		// psInsert.setInt(6, zoom);
		// psInsert.setInt(7, x);
		// psInsert.setInt(8, y);

		try {
			psInsert.executeUpdate();
		} catch (Exception e) {

			if (e.getMessage().toLowerCase().indexOf("unique") >= 0) {
				// System.out
				// .println("AVOEEEEEE");
				psDelete.setInt(1, zoom);
				psDelete.setInt(2, x);
				psDelete.setInt(3, y);
				psDelete.executeUpdate();
			} else {
				e.printStackTrace();
				System.out.println(rcn);
				psInsert.close();
				recreateStatement();
				psInsert.executeUpdate();
			}
		}
		executed = true;
	}

	public void recreateStatement() throws SQLException {
		try {
			if (psInsert != null)
				psInsert.close();
			if (psDelete != null)
				psDelete.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (conn.getAutoCommit())
			conn.setAutoCommit(false);
		psInsert = conn
				.prepareStatement("insert into mapfiledatazxy(zoom,x,y,file_data, rcn,created_on) values (?,?,?,?,?,?)");
		psDelete = conn
				.prepareStatement("delete from mapfiledatazxy where zoom=? and x=? and y=?");
	}

	public void done() throws SQLException {
		String[] statements = new String[] {
				"drop table zooms",
				"CREATE TABLE zooms (zoom INTEGER, minx INTEGER, maxx INTEGER, PRIMARY KEY(zoom, minx, maxx))",
				"insert into zooms  select zoom,min(x) minx,max(x) maxx from mapfiledatazxy group by zoom" };
		for (String stmt : statements) {
			try {
				Statement st = conn.createStatement();
				st.executeUpdate(stmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		conn.commit();
		conn.close();
	}

	public File getFile() {
		return file;
	}
}
