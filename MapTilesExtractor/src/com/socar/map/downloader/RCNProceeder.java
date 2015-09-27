package com.socar.map.downloader;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RCNProceeder {
	public static int TILE_DOWNLOAD_SECONDS_TO_WORK = 25;

	private static final String GET_RCN_TIME = "GET_RCN_TIME";

	public RCNProceeder(String connectionString, String user, String password,
			int processors, int batch_size, String map_url) throws Exception {
		Connection conn = null;
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection(connectionString, user, password);
		
		conn.setAutoCommit(true);
		int[] rcn = new int[processors];
		for (int i = 0; i < processors; i++) {
			try {
				Timer.start(GET_RCN_TIME);
				rcn[i] = getRcn(conn, batch_size);
				Timer.step(GET_RCN_TIME);
				Timer.printall();

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		conn.close();
	}

	private int getRcn(Connection conn, int batch_size) throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select maps.creatercnTbl(?) rcn");
		ps.setInt(1, batch_size);
		ResultSet rs = ps.executeQuery();
		int rcn = 0;
		if (rs.next())
			rcn = rs.getInt("rcn");
		rs.close();
		ps.close();
		return rcn;
	}

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		new RCNProceeder(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"),
				getIntValue(props.getProperty("loopcount")),
				getIntValue(props.getProperty("rcncount")),
				props.getProperty("map_url"));
	}

	public static int getIntValue(String val) {
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
