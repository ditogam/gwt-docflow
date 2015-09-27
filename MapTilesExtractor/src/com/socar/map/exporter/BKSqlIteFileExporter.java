package com.socar.map.exporter;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import com.socar.map.downloader.Timer;

public class BKSqlIteFileExporter implements Runnable {

	private static final int BUFFER_SIZE = 1024;
	public static String USER_AGENT = "OsmAnd~";
	private static final String FULL_TIME = "FULL_TIME";
	private static final String SELECT_TIME = "SELECT_TIME";
	private static final String DOWNLOAD_TIME = "DOWNLOAD_TIME";
	private static final String SAVE_TIME = "SAVE_TIME";
	private static final String COMMIT_TIME = "COMMIT_TIME";
	private static final String COPY_TIME = "COPY_TIME";
	private static final String ONE_THREAD_TIME = "ONE_THREAD_TIME";
	private static final String EXCEPTION_COUNT = "EXCEPTION_COUNT";

	private String connectionString;
	private String user;
	private String password;
	private int rcn;
	private Thread thread;
	private int batch_size;
	private Map<Integer, SqlLiteConnection> cons;

	public BKSqlIteFileExporter(String connectionString, String user,
			String password, int rcn, int batch_size,
			Map<Integer, SqlLiteConnection> cons) throws Exception {
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
		this.cons = cons;
		this.rcn = rcn;
		this.batch_size = batch_size;
		thread = new Thread(this, "MyName is" + System.nanoTime());
		thread.start();

	}

	public Thread getThread() {
		return thread;
	}

	public static void closeStream(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {

		}
	}

	public static void streamCopy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	@Override
	public void run() {

		Connection conn = null;
		try {
			Timer.start(ONE_THREAD_TIME);

			Class.forName("org.postgresql.Driver");

			// prop.setProperty("shared_cache", "true");

			conn = DriverManager
					.getConnection(connectionString, user, password);
			conn.setAutoCommit(false);

			PreparedStatement psSave = conn
					.prepareStatement("update maps.rcntbl set exported=true where rcnid=?");

			int count = 0;

			StringBuffer var1 = new StringBuffer();
			var1.append("SELECT m.zoom, ");
			var1.append("       x, ");
			var1.append("       y ");
			// var1.append("       ,file_data ");
			// var1.append("       ,(SELECT Array_to_string(Array_agg(subregion_id), ',') ");
			// var1.append("        FROM   maps.subregionboundzxy12 s ");
			// var1.append("        WHERE  m.zoom = s.zoom ");
			// var1.append("               AND m.x BETWEEN s.x1 AND s.x2 ");
			// var1.append("               AND m.y BETWEEN s.y1 AND s.y2) subregions ");
			var1.append("FROM   maps.mapfiledatazxy m ");
			var1.append("WHERE  rcn = ? ");

			// PreparedStatement psSelect = conn
			// .prepareStatement("select zoom,x, y,file_data from maps.mapfiledatazxy where rcn=? and file_data is not null");
			PreparedStatement psSelect = conn.prepareStatement(var1.toString());
			PreparedStatement psSelectBytes = conn
					.prepareStatement("select file_data from maps.mapfiledatazxy m where zoom=? and x=? and y=?");
			psSelect.setInt(1, rcn);
			Timer.start(SELECT_TIME);
			ResultSet rs = psSelect.executeQuery();
			Timer.step(SELECT_TIME);
			if (count % 100 == 0) {
				System.out.println("RCN =" + rcn);
				Timer.printall();
			}
			while (rs.next()) {
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				Timer.start(FULL_TIME);
				int zoom = rs.getInt("zoom");
				// String subregions = rs.getString("subregions");
				Timer.start(COPY_TIME);
				psSelectBytes.setInt(1, zoom);
				psSelectBytes.setInt(2, x);
				psSelectBytes.setInt(3, y);
				ResultSet rs1 = psSelectBytes.executeQuery();
				rs1.next();
				InputStream is = rs1.getBinaryStream("file_data");
				rs1.close();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				streamCopy(is, bos);
				Timer.step(COPY_TIME);

				Timer.start(SAVE_TIME);
				execute(zoom, x, y, bos.toByteArray(), zoom);
				// psInsert.close();
				Timer.step(SAVE_TIME);
				Timer.step(FULL_TIME);
				closeStream(is);
				closeStream(bos);
				count++;
				if (count % 100 == 0) {
					System.out.println("RCN =" + rcn);
					Timer.printall();
				}
				if (count % batch_size == 0) {
					Timer.start(COMMIT_TIME);
					commit();
					Timer.step(COMMIT_TIME);
				}

			}

			Timer.start(COMMIT_TIME);
			 psSave.setInt(1, rcn);
			 psSave.executeUpdate();
			conn.commit();
			commit();
			Timer.step(COMMIT_TIME);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		} finally {
			Timer.step(ONE_THREAD_TIME);
			Timer.printall();
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			// try {
			// close();
			// } catch (Exception e2) {
			// // TODO: handle exception
			// }
		}

	}

	private void close() {
		for (SqlLiteConnection con : cons.values()) {
			try {
				con.getConn().close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private void rollback() {
		for (SqlLiteConnection con : cons.values()) {
			try {
				con.getConn().rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void commit() {
		for (SqlLiteConnection con : cons.values()) {
			try {
				con.getConn().commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void execute(int zoom, int x, int y, byte[] data, int rcn) {
		ArrayList<Integer> spl = SqliteExporter.getSubregions(zoom, x, y);
		for (Integer id : spl) {
			try {
				SqlLiteConnection con = cons.get(id);
				con.execute(zoom, x, y, data, rcn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public PreparedStatement prepareInsert(Connection connLite)
			throws SQLException {
		PreparedStatement psInsert;
		psInsert = connLite
				.prepareStatement("insert into mapfiledatazxy(zoom,x,y,file_data) values(?,?,?,?)");
		return psInsert;
	}

}
