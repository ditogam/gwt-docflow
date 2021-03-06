package com.socar.map.exporter.one;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.socar.map.downloader.Timer;

public class SqliteExporterOne {
	private static int FinishedCount = 1;
	private static final String GET_RCN_TIME = "GET_RCN_TIME";
	private static final String FULL_GET_TIME = "FULL_GET_TIME";
	private static final String ERROR_COUNT = "ERROR_COUNT";
	private static final String VACUUM_PG = "VACUUM_PG";
	private static final String SUBREGION_ZOOM_COUNT = "SUBREGION_ZOOM_COUNT";
	public Connection conn = null;

	public SqliteExporterOne(Connection conn, int processors, int batch_size,
			ArrayList<SqlLiteConnectionOne> cons, ArrayList<Integer> rcns) {
		processors = 1;
		this.conn = conn;
		try {

			Timer.start(FULL_GET_TIME);

			// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			// processors, processors, TILE_DOWNLOAD_SECONDS_TO_WORK,
			// TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			ArrayList<SqlIteFileExporterOne> threads = new ArrayList<SqlIteFileExporterOne>();

			for (int i = 0; i < processors; i++) {
				try {

					if (rcns.size() < 1) {
						for (SqlLiteConnectionOne con : cons) {
							try {
								con.done();
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
					SqlIteFileExporterOne t = new SqlIteFileExporterOne(this,
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
		new SqliteExporterOne(this.conn, processors, batch_size, cons, rcns);
	}

	private static ArrayList<SqlLiteConnectionOne> prepareDBS()
			throws Exception {

		ArrayList<SqlLiteConnectionOne> cons = new ArrayList<SqlLiteConnectionOne>();
		File dir = new File("exported");
		dir.mkdirs();
		File fl = new File(dir, "full.sqlite");
		SqlLiteConnectionOne con = new SqlLiteConnectionOne(fl);
		File bydate = new File(dir, "bydate");
		bydate.mkdirs();
		cons.add(con);
		File[] paths = bydate.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if (str.equals(".sqlite")) {
						return true;
					}
				}
				return false;
			}
		});
		createConnection(con);
		String currentTime = new SimpleDateFormat("yyMMdd").format(new Date())
				+ ".sqlite";
		boolean alreadyExists = false;
		for (File file : paths) {
			if (file.getName().equalsIgnoreCase(currentTime))
				alreadyExists = true;
			SqlLiteConnectionOne con1 = new SqlLiteConnectionOne(file);
			cons.add(con1);
			createConnection(con1);
		}
		if (!alreadyExists) {
			File file = new File(bydate, currentTime);
			SqlLiteConnectionOne con1 = new SqlLiteConnectionOne(file);
			cons.add(con1);
			createConnection(con1);
		}
		return cons;
	}

	private static ArrayList<SqlLiteVacuumerOne> vacuumers = new ArrayList<SqlLiteVacuumerOne>();

	@SuppressWarnings("resource")
	private static Connection createConnection(SqlLiteConnectionOne n)
			throws Exception {

		File fl = n.getFile();
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
		File f = new File(fl.getParentFile(), fl.getName() + "-journal");
		if (f.exists()) {
			FileChannel channel = new FileInputStream(fl).getChannel();
			long size = channel.size();
			channel.close();
			System.out.println(fl.getAbsolutePath() + " File size is:"
					+ ((double) size / 1024.0 / 1024.0) + " mb");
			vacuumers.add(new SqlLiteVacuumerOne(n));
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
				.prepareStatement("select rcn_id from maps.rcn_tbl where not exported_one and proceeded order by 1  ");
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

	private static ArrayList<SqlLiteConnectionOne> cons = null;

	public static void main(String[] args) throws Exception {
		createNewConnection();

		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));

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
		cons = prepareDBS();
		new SqliteExporterOne(conn,
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("xcount")), cons, rcns);
		conn.close();
	}

	public static ArrayList<SqlLiteConnectionOne> getConnections(int zoom,
			int x, int y) {
		return cons;
	}

	public static int getIntValue(String val) {
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
