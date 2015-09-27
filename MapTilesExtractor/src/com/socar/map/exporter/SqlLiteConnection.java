package com.socar.map.exporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlLiteConnection {
	private Connection conn;
	private int subregionid;

	private PreparedStatement psInsert;

	public SqlLiteConnection(int subregionid, Connection conn) {
		this.conn = conn;
		this.subregionid = subregionid;
	}

	public Connection getConn() {
		return conn;
	}

	public int getSubregionid() {
		return subregionid;
	}

	public void execute(int zoom, int x, int y, byte[] data, int rcn)
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
		
		psInsert.setInt(6, zoom);
		psInsert.setInt(7, x);
		psInsert.setInt(8, y);
		
		try {
			psInsert.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(rcn);
			psInsert.close();
			recreateStatement();
		}

	}

	public void recreateStatement() throws SQLException {
		try {
			if (psInsert != null)
				psInsert.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		psInsert = conn
				.prepareStatement("insert into mapfiledatazxy(zoom,x,y,file_data, rcn) select ?,?,?,?,? where 1 not in (select 1 from mapfiledatazxy where zoom=? and x=? and y=?)");
	}
}
