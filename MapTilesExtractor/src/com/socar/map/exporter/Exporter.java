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

public class Exporter {
	private static int FinishedCount = 1;
	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";

	public Exporter(String connectionString, String user, String password,
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
			ArrayList<FilesExporter> threads = new ArrayList<FilesExporter>();

			int[] rcn = new int[processors];
			for (int i = 0; i < processors; i++) {
				try {
					Timer.start(GET_RCN_TIME);
					rcn[i] = getRcn(conn, batch_size);
					if (rcn[i] < 1)
						System.exit(0);
					Timer.step(GET_RCN_TIME);
					FilesExporter t = new FilesExporter(connectionString, user,
							password, rcn[i], batch_size);

					threads.add(t);
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
			for (FilesExporter thread : threads) {
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
		new Exporter(connectionString, user, password, processors, batch_size);
	}

	private int getRcn(Connection conn, int batch_size) throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("select rcnid rcn from maps.rcntbl where not exported and newid is not null order by 1 limit 1");
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
		new Exporter(props.getProperty("connection"),
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
