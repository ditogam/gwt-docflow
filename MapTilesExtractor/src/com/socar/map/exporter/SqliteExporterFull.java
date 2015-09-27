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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.socar.map.ZX1X2Y1Y2;
import com.socar.map.downloader.Timer;

public class SqliteExporterFull {
	private static int FinishedCount = 1;
	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";
	private static final String VACUUM_PG = "VACUUM_PG";
	private static final String SUBREGION_ZOOM_COUNT = "SUBREGION_ZOOM_COUNT";
	public Connection conn = null;

	public SqliteExporterFull(Connection conn, int processors, int batch_size,
			ArrayList<SqlLiteConnectionNew> cons, ArrayList<Integer> rcns) {
		processors = 1;
		this.conn = conn;
		try {

			Timer.start(FULL_GET_TIME);

			// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			// processors, processors, TILE_DOWNLOAD_SECONDS_TO_WORK,
			// TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			ArrayList<SqlIteFileExporterFull> threads = new ArrayList<SqlIteFileExporterFull>();

			for (int i = 0; i < processors; i++) {
				try {

					if (rcns.size() < 1) {
						for (SqlLiteConnectionNew con : cons) {
							try {
								con.commit();
								con.getConn().close();
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						this.conn.commit();
						this.conn.close();
						Timer.printall();
						Thread.sleep(5000);
						System.exit(0);
					}

					Integer rcn = rcns.get(0);
					rcns.remove(0);
					SqlIteFileExporterFull t = new SqlIteFileExporterFull(this,
							conn, rcn, batch_size, cons);

					threads.add(t);

				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(100);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Timer.start(FULL_GET_TIME);
			Timer.step(FULL_GET_TIME);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//
		// for (SqlLiteConnectionNew con : cons) {
		// try {
		// con.commit();
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		Timer.step(FULL_GET_TIME);
		System.out.println("FINISSEEEEEEEEEEEEDDD      " + FinishedCount++);
		Timer.printall();
		new SqliteExporterFull(this.conn, processors, batch_size, cons, rcns);
	}

	private static ArrayList<SqlLiteConnectionNew> prepareDBS(
			String connectionString, String user, String password)
			throws Exception {
		Connection conn = null;
		ArrayList<SqlLiteConnectionNew> cons = new ArrayList<SqlLiteConnectionNew>();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection(connectionString, user, password);
			PreparedStatement ps = conn
					.prepareStatement("select subregion_id from subregions order by subregion_id");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ZX1X2Y1Y2 z = new ZX1X2Y1Y2(0, 0, 0, 0, 0, rs.getInt("subregion_id"));
				SqlLiteConnectionNew c = new SqlLiteConnectionNew(
						z.getSubregion_id(), z.getZoom(), z.getX1(), z.getX2());
				createConnection(c);
				cons.add(c);
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
					.prepareStatement("select * from maps.subregionboundzx12");
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

	private static ArrayList<SqlLiteVacuumer> vacuumers = new ArrayList<SqlLiteVacuumer>();

	private static Connection createConnection(SqlLiteConnectionNew n)
			throws Exception {

		File dir = new File("exported");
		if (!dir.exists())
			dir.mkdirs();
		File fl = new File(dir, n.getSubregionid() + ".sqlite");
		if (!fl.exists()) {
			File empty = new File("TilesDBEmpty.sqlite");
			copyFile(empty, fl);
		}
		Class.forName("org.sqlite.JDBC");
		Properties prop = new Properties();
		// prop.setProperty("shared_cache", "true");
		Connection connLite = DriverManager.getConnection(
				"jdbc:sqlite:" + fl.getAbsolutePath(), prop);

		// connLite.setAutoCommit(false);
		n.setConn(connLite);
		File f = new File(dir, fl.getName() + "-journal");
		if (f.exists()) {
			FileChannel channel = new FileInputStream(fl).getChannel();
			long size = channel.size();
			channel.close();
			System.out.println(fl.getAbsolutePath() + " File size is:"
					+ ((double) size / 1024.0 / 1024.0) + " mb");
			vacuumers.add(new SqlLiteVacuumer(n));
		}
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

	private static ArrayList<Integer> getRcn(Connection conn, int batch_size)
			throws Exception {
		ArrayList<Integer> rcns = new ArrayList<Integer>();
		PreparedStatement ps = conn
				.prepareStatement("select rcn_id from maps.rcn_tbl where not exported_full and proceeded order by 1  ");
		ResultSet rs = ps.executeQuery();
		while (rs.next())
			rcns.add(rs.getInt("rcn_id"));
		rs.close();
		ps.close();
		return rcns;
	}

	private static ArrayList<Integer> getRcn(String connectionString,
			String user, String password, int batch_size) throws Exception {
		Connection conn = null;

		Timer.start(FULL_GET_TIME);

		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection(connectionString, user, password);
		ArrayList<Integer> rcns = getRcn(conn, batch_size);
		conn.close();

		return rcns;
	}

	private static void vacuumRcn(String connectionString, String user,
			String password, ArrayList<Integer> rcns) throws Exception {
		// Connection conn = null;
		//
		// Timer.start(FULL_GET_TIME);
		//
		// Class.forName("org.postgresql.Driver");
		// conn = DriverManager.getConnection(connectionString, user, password);
		//
		// int i = 0;
		// for (Integer rcn : rcns) {
		// Timer.start(VACUUM_PG);
		// // PreparedStatement psVacuum = conn
		// // .prepareStatement("vacuum analyze tiles_by_rcn.mapfiledatazxy_"
		// // + rcn);
		// // psVacuum.execute();
		// // psVacuum.close();
		// System.out.println("vacuuming " + i++ + " out of :" + rcns.size());
		// Timer.step(VACUUM_PG);
		// Timer.printall();
		// }
		//
		// conn.close();

	}

	private static ArrayList<ZX1X2Y1Y2> zooms;
	private static ArrayList<SqlLiteConnectionNew> cons;

	public static Connection createNewConnection() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection(
				props.getProperty("connection"), props.getProperty("user"),
				props.getProperty("pwd"));
		conn.setAutoCommit(false);
		// String s =
		// "SELECT status, content_type, content FROM http_get('http://homepc.homelinux.org:55557/SocarExportWP/getmap.jsp?zoom=8&x=157&y=94')";
		// ResultSet rs = conn.createStatement().executeQuery(s);
		// if (rs.next()) {
		// byte[] bt = rs.getBytes("content");
		// System.out.println(bt);
		// }
		return conn;
	}

	public static void main(String[] args) throws Exception {
		createNewConnection();

		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		cons = prepareDBS(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"));
		// for (SqlLiteVacuumer v : vacuumers) {
		// v.getThread().join();
		// }
		// if (true)
		// System.exit(0);
		zooms = getSubregionZooms(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"));
		ArrayList<Integer> rcns = getRcn(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"), 10000000);
		vacuumRcn(props.getProperty("connection"), props.getProperty("user"),
				props.getProperty("pwd"), rcns);
		// System.out.println(getSubregions(20, 649049, 388680));
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection(
				props.getProperty("connection"), props.getProperty("user"),
				props.getProperty("pwd"));
		conn.setAutoCommit(false);
		new SqliteExporterFull(conn,
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("xcount")), cons, rcns);
		conn.close();
	}

	public static ArrayList<SqlLiteConnectionNew> getConnections(int zoom,
			int x, int y) {
		Timer.start(SUBREGION_ZOOM_COUNT);
		ArrayList<SqlLiteConnectionNew> ret = new ArrayList<SqlLiteConnectionNew>();
		ArrayList<Integer> subregions = new ArrayList<Integer>();
		for (ZX1X2Y1Y2 zz : zooms) {
			if (zz.getZoom() != zoom)
				continue;
			if (x >= zz.getX1() && x <= zz.getX2() && y >= zz.getY1()
					&& y <= zz.getY2())
				subregions.add(zz.getSubregion_id());
		}
		for (SqlLiteConnectionNew con : cons) {
			boolean exists = false;
			for (Integer subregion : subregions) {
				if (subregion == con.getSubregionid()) {
					exists = true;
					break;
				}
			}
			if (!exists)
				continue;
//			if (x >= con.getX1() && x <= con.getX2())
				ret.add(con);
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
