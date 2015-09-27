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
import java.util.Properties;

import com.socar.map.downloader.Timer;

public class FilesExporter implements Runnable {

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

	public FilesExporter(String connectionString, String user, String password,
			int rcn, int batch_size) throws Exception {
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
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
		Connection connLite = null;
		try {
			Timer.start(ONE_THREAD_TIME);

			Class.forName("org.postgresql.Driver");
			Class.forName("org.sqlite.JDBC");
			Properties prop = new Properties();
//			prop.setProperty("shared_cache", "true");
			connLite = DriverManager.getConnection("jdbc:sqlite:"
					+ "TilesDB.sqlite", prop);
			connLite.setAutoCommit(false);
			conn = DriverManager
					.getConnection(connectionString, user, password);
			conn.setAutoCommit(false);

			PreparedStatement psSave = conn
					.prepareStatement("update maps.rcntbl set exported=true where rcnid=?");

			PreparedStatement psInsert = prepareInsert(connLite);
			int count = 0;

			PreparedStatement psSelect = conn
					.prepareStatement("select zoom,x, y,file_data from maps.mapfiledatazxy where rcn=? and file_data is not null");
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
				Timer.start(COPY_TIME);
				InputStream is = rs.getBinaryStream("file_data");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				streamCopy(is, bos);
				Timer.step(COPY_TIME);

				Timer.start(SAVE_TIME);
				psInsert.setInt(1, zoom);
				psInsert.setInt(2, x);
				psInsert.setInt(3, y);
				psInsert.setBytes(4, bos.toByteArray());
				try {
					psInsert.executeUpdate();
				} catch (Exception e) {
					psInsert.close();
					psInsert = prepareInsert(connLite);
				}
//				psInsert.close();
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
					connLite.commit();
					Timer.step(COMMIT_TIME);
				}

			}

			Timer.start(COMMIT_TIME);
			psSave.setInt(1, rcn);
			psSave.executeUpdate();
			conn.commit();
			connLite.commit();
			Timer.step(COMMIT_TIME);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connLite.rollback();
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
			try {
				connLite.close();
			} catch (Exception e2) {
				// TODO: handle exception
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
