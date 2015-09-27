package com.socar.map.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.socar.map.ZX1X2Y1Y2;
import com.socar.map.downloader.Timer;

public class SqliteExporter {
	private static int FinishedCount = 1;
	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";
	private static final String SUBREGION_ZOOM_COUNT = "SUBREGION_ZOOM_COUNT";

	public SqliteExporter(String connectionString, String user,
			String password, int processors, int batch_size,
			Map<Integer, SqlLiteConnection> cons) {
		processors = 10;
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
			ArrayList<SqlIteFileExporter> threads = new ArrayList<SqlIteFileExporter>();

			for (int i = 0; i < processors; i++) {
				try {
					Timer.start(GET_RCN_TIME);
					ArrayList<Integer> rcns = getRcn(conn, 1);
					if (rcns.size() < 1) {
						for (SqlLiteConnection con : cons.values()) {
							try {
								con.getConn().close();
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						Timer.printall();
						System.exit(0);
					}
					Timer.step(GET_RCN_TIME);
					for (Integer rcn : rcns) {
						SqlIteFileExporter t = new SqlIteFileExporter(
								connectionString, user, password, rcn,
								batch_size, cons);

						threads.add(t);
					}
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
			for (SqlIteFileExporter thread : threads) {
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
		for (SqlLiteConnection con : cons.values()) {
			try {
				con.getConn().commit();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new SqliteExporter(connectionString, user, password, processors,
				batch_size, cons);
	}

	private static Map<Integer, SqlLiteConnection> prepareDBS(
			String connectionString, String user, String password)
			throws Exception {
		Connection conn = null;
		Map<Integer, SqlLiteConnection> cons = new TreeMap<Integer, SqlLiteConnection>();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection(connectionString, user, password);
			PreparedStatement ps = conn
					.prepareStatement("select distinct subregion_id from maps.subregionboundzxy12");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int subregionid = rs.getInt("subregion_id");
				cons.put(subregionid, new SqlLiteConnection(subregionid,
						createConnection(subregionid)));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		try {
			conn.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
		return cons;
	}

	private static ArrayList<ZX1X2Y1Y2> getSubregionZooms(
			String connectionString, String user, String password)
			throws Exception {
		Connection conn = null;
		ArrayList<ZX1X2Y1Y2> zooms = new ArrayList<ZX1X2Y1Y2>();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection(connectionString, user, password);
			PreparedStatement ps = conn
					.prepareStatement("select * from maps.subregionboundzxy12");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				zooms.add(new ZX1X2Y1Y2(rs.getInt("zoom"), rs.getInt("x1"), rs
						.getInt("y1"), rs.getInt("x2"), rs.getInt("y2"), rs
						.getInt("subregion_id")));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		try {
			conn.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
		return zooms;
	}

	private static Connection createConnection(int subregionid)
			throws Exception {

		File dir = new File("exported");
		if (!dir.exists())
			dir.mkdirs();
		File subregionFile = new File(dir, "TilesDB_" + subregionid + ".sqlite");
		if (!subregionFile.exists()) {
			File empty = new File("TilesDBEmpty.sqlite");
			copyFile(empty, subregionFile);
		}
		Class.forName("org.sqlite.JDBC");
		Properties prop = new Properties();
		// prop.setProperty("shared_cache", "true");
		Connection connLite = DriverManager.getConnection("jdbc:sqlite:"
				+ subregionFile.getAbsolutePath(), prop);
		PreparedStatement psSave = connLite
				.prepareStatement("delete from user_data");
		psSave.executeUpdate();
		psSave.close();
		psSave = connLite
				.prepareStatement("insert into user_data(subregionid) values(?)");

		psSave.setInt(1, subregionid);
		psSave.executeUpdate();
		psSave.close();
		connLite.setAutoCommit(false);
		return connLite;
	}

	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();

			// previous code: destination.transferFrom(source, 0,
			// source.size());
			// to avoid infinite loops, should be:
			long count = 0;
			long size = source.size();
			while ((count += destination.transferFrom(source, count, size
					- count)) < size)
				;
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	private ArrayList<Integer> getRcn(Connection conn, int batch_size)
			throws Exception {
		ArrayList<Integer> rcns = new ArrayList<Integer>();
		PreparedStatement ps = conn
				.prepareStatement("select rcnid rcn from maps.rcntbl where not tbl_vacuumed limit "
						+ batch_size);
		ResultSet rs = ps.executeQuery();
		int rcn = 0;
		while (rs.next())
			rcns.add(rs.getInt("rcn"));
		rs.close();
		ps.close();
		return rcns;
	}

	private static ArrayList<ZX1X2Y1Y2> zooms;

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		Map<Integer, SqlLiteConnection> cons = prepareDBS(
				props.getProperty("connection"), props.getProperty("user"),
				props.getProperty("pwd"));
		zooms = getSubregionZooms(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"));
		System.out.println(getSubregions(20, 649049, 388680));
		 new SqliteExporter(props.getProperty("connection"),
		 props.getProperty("user"), props.getProperty("pwd"),
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("batch_size")), cons);
	}

	public static ArrayList<Integer> getSubregions(int zoom, int x, int y) {
		Timer.start(SUBREGION_ZOOM_COUNT);
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (ZX1X2Y1Y2 zz : zooms) {
			if (zz.getZoom() != zoom)
				continue;
			if (x >= zz.getX1() && x <= zz.getX2() && y >= zz.getY1()
					&& y <= zz.getY2())
				ret.add(zz.getSubregion_id());
		}
		Timer.step(SUBREGION_ZOOM_COUNT);
		return ret;
	}

	public static int getIntValue(String val) {
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
