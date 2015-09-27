package com.socar.map.exporter;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import com.socar.map.downloader.Proceeder;
import com.socar.map.downloader.Timer;

public class CopyTable {
	private static int FinishedCount = 1;
	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";

	public CopyTable(String connectionString, String user, String password,
			int processors, int batch_size) {
		processors = 1;
		Connection conn = null;
		try {

			Timer.start(FULL_GET_TIME);

			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection(connectionString, user, password);
			// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			// processors, processors, TILE_DOWNLOAD_SECONDS_TO_WORK,
			// TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			conn.setAutoCommit(true);

			for (int i = 0; i < processors; i++) {
				try {
					Timer.start(GET_RCN_TIME);
					getRcn(conn, batch_size);
					Timer.step(GET_RCN_TIME);
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(100);
				}
			}
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			Timer.start(FULL_GET_TIME);
			Timer.step(FULL_GET_TIME);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Timer.step(FULL_GET_TIME);
		System.out.println("FINISSEEEEEEEEEEEEDDD      " + FinishedCount++);
		Timer.printall();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new CopyTable(connectionString, user, password, processors, batch_size);
	}

	private int getRcn(Connection conn, int batch_size) throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select maps.create_tile_table_by_rcn_cnt("
						+ batch_size + ") rcn");
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
		new CopyTable(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"),
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("batch_size")));
	}

	public static int getIntValue(String val) {
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
