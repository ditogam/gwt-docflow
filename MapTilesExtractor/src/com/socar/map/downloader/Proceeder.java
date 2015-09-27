package com.socar.map.downloader;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

public class Proceeder {

	private static int FinishedCount = 1;
	public static int TILE_DOWNLOAD_SECONDS_TO_WORK = 25;

	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";

	public Proceeder(String connectionString, String user, String password,
			int processors, int batch_size, String map_url) throws Exception {
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
			ArrayList<MapDownloader> threads = new ArrayList<MapDownloader>();

			ArrayList<Integer> rcn = getRcn(conn, processors);
			for (int i = 0; i < rcn.size(); i++) {
				try {
					Timer.start(GET_RCN_TIME);

					Timer.step(GET_RCN_TIME);
					MapDownloader t = new MapDownloader(connectionString, user,
							password, rcn.get(i), map_url, batch_size);

					threads.add(t);
				} catch (Exception e) {
					Thread.sleep(100);
				}
			}
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			for (MapDownloader thread : threads) {
				thread.getThread().join();
			}

		} catch (Exception e) {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			Timer.start(FULL_GET_TIME);
			Timer.step(FULL_GET_TIME);
			Thread.sleep(100);
		}
		Timer.step(FULL_GET_TIME);
		System.out.println("FINISSEEEEEEEEEEEEDDD      " + FinishedCount++);
		Timer.printall();
		Thread.sleep(10);
		new Proceeder(connectionString, user, password, processors, batch_size,
				map_url);

	}

	// private int getRcn(Connection conn, int batch_size) throws Exception {
	// PreparedStatement ps = conn
	// .prepareStatement("select maps.creatercn(?) rcn");
	// ps.setInt(1, batch_size);
	// ResultSet rs = ps.executeQuery();
	// int rcn = 0;
	// if (rs.next())
	// rcn = rs.getInt("rcn");
	// rs.close();
	// ps.close();
	// return rcn;
	// }

	private ArrayList<Integer> getRcn(Connection conn, int batch_size)
			throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select rcn_id rcn from maps.rcn_tbl where not proceeded limit "
						+ batch_size);
//		ps.setInt(1, batch_size);
		ResultSet rs = ps.executeQuery();
		ArrayList<Integer> rcn = new ArrayList<Integer>();
		while (rs.next())
			rcn.add(rs.getInt("rcn"));
		rs.close();
		ps.close();
		return rcn;
	}

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		new Proceeder(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"),
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("batch_size")),
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
