package com.socar.map.exporter.one;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.socar.map.downloader.Timer;
import com.socar.map.exporter.SqliteExporterNew;

public class SqlIteFileExporterOne implements Runnable {
	private static int cnt = 0;
	private static final int BUFFER_SIZE = 1024;
	public static String USER_AGENT = "OsmAnd~";
	private static final String FULL_TIME = "FULL_TIME";
	private static final String SELECT_TIME = "SELECT_TIME";
	private static final String COMPRESS_TIME = "COMPRESS_TIME";
	private static final String SAVE_TIME = "SAVE_TIME";
	private static final String COMMIT_TIME = "COMMIT_TIME";
	private static final String COMMIT_LITE_TIME = "COMMIT_LITE_TIME";
	private static final String COPY_TIME = "COPY_TIME";
	private static final String ONE_THREAD_TIME = "ONE_THREAD_TIME";
	private static final String CREATE_IMAGE_TIME = "CREATE_IMAGE_TIME";

	private String connectionString;
	private String user;
	private String password;
	private int rcn;
	private int batch_size;
	private ArrayList<SqlLiteConnectionOne> cons;
	private Connection conn;
	private SqliteExporterOne sqliteExporterNew;
	private Thread thread;

	public SqlIteFileExporterOne(SqliteExporterOne sqliteExporterNew,
			Connection conn, int rcn, int batch_size,
			ArrayList<SqlLiteConnectionOne> cons) throws Exception {

		this.cons = cons;
		this.conn = conn;
		this.sqliteExporterNew = sqliteExporterNew;
		this.rcn = rcn;
		this.batch_size = batch_size;
		run();

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

	// private static final

	public static byte[] compress(byte bt[]) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(bt);
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
		// PngEncoder e = new PngEncoder();
		// e.setCompression(PngEncoder.BEST_COMPRESSION);
		// Timer.start(CREATE_IMAGE_TIME);
		// Image bi = Toolkit.getDefaultToolkit().createImage(bt);
		// Timer.step(CREATE_IMAGE_TIME);
		// Timer.start(COMPRESS_TIME);
		// e.encode(bi, os);
		// Timer.step(COMPRESS_TIME);
		// byte[] compressed = os.toByteArray();
		// os.close();
		// return compressed;
	}

	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream("getmap.png");
		byte[] bt = new byte[fis.available()];
		fis.read(bt);
		fis.close();
		System.out.println("was" + bt.length);
		bt = compress(bt);
		System.out.println("is" + bt.length);
		FileOutputStream fos = new FileOutputStream("getimage1.png");
		fos.write(bt);
		fos.flush();
		fos.close();
	}

	public static byte[] decompress(byte[] compressed) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		byte[] data = new byte[BUFFER_SIZE];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			os.write(data, 0, bytesRead);
		}
		data = os.toByteArray();
		gis.close();
		is.close();
		return data;
	}

	@Override
	public void run() {

		try {
			Timer.start(ONE_THREAD_TIME);

			PreparedStatement psSave = conn
					.prepareStatement("update maps.rcn_tbl set exported_one=true where rcn_id=?");

			int count = 0;

			StringBuffer var1 = new StringBuffer();
			var1.append("SELECT m.zoom, ");
			var1.append("       x, ");
			var1.append("       y ");
			var1.append("       ,img_data,rec_time ");

			var1.append("FROM   maps.zoom_xy m " + "" + " where rcn_id= " + rcn
					+ " and img_data is not null  ");
			// PreparedStatement psSelect = conn
			// .prepareStatement("select zoom,x, y,file_data from maps.mapfiledatazxy where rcn=? and file_data is not null");
			PreparedStatement psSelect = conn.prepareStatement(var1.toString());
			// PreparedStatement psSelectBytes = conn
			// .prepareStatement("select file_data from maps.mapfiledatazxy m where zoom=? and x=? and y=?");
			// psSelect.setInt(1, rcn);
			Timer.start(SELECT_TIME);
			ResultSet rs = psSelect.executeQuery();
			Timer.step(SELECT_TIME);
			if (count % 100 == 0) {
				System.out.println("RCN =" + rcn);
				Timer.printall();
			}
			int bytecount = 0;
			int bytecountaftercompression = 0;
			while (rs.next()) {
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				long date = rs.getTimestamp("rec_time").getTime();
				Timer.start(FULL_TIME);
				int zoom = rs.getInt("zoom");
				Timer.start(COPY_TIME);
				Timer.step(COPY_TIME);
				byte[] bytes = rs.getBytes("img_data");
				bytecount += bytes.length;
				bytes = compress(bytes);
				bytecountaftercompression += bytes.length;
				Timer.start(SAVE_TIME);
				execute(zoom, x, y, bytes, rcn, date);
				Timer.step(SAVE_TIME);
				Timer.step(FULL_TIME);
				count++;

			}
			try {
				System.gc();
			} catch (Exception e) {
				// TODO: handle exception
			}
			psSave.setInt(1, rcn);
			psSave.executeUpdate();
			System.out.println("COUNTTTT=" + count + " BYTECOUNT=" + bytecount
					+ " COMPBYTECOUNT=" + bytecountaftercompression + " DIFF="
					+ (bytecount - bytecountaftercompression));
			cnt++;

			if (cnt == batch_size) {

				commitLite();
				Timer.start(COMMIT_TIME);
				conn.commit();
				conn.close();
				sqliteExporterNew.conn = SqliteExporterNew
						.createNewConnection();
				Timer.step(COMMIT_TIME);
				cnt = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			// try {
			// conn.rollback();
			// } catch (Exception e2) {
			// // TODO: handle exception
			// }
			// try {
			// rollback();
			// } catch (Exception e2) {
			// // TODO: handle exception
			// }
		} finally {
			Timer.step(ONE_THREAD_TIME);
			Timer.printall();

			// try {
			// close();
			// } catch (Exception e2) {
			// // TODO: handle exception
			// }
		}

	}

	public void commitLite() {
		Timer.start(COMMIT_LITE_TIME);
		commit();
		Timer.step(COMMIT_LITE_TIME);
	}

	private void close() {
		for (SqlLiteConnectionOne con : cons) {
			try {
				con.getConn().close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private void rollback() {
		for (SqlLiteConnectionOne con : cons) {
			try {
				con.getConn().rollback();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void commit() {
		ArrayList<SqlLiteCommiterOne> threads = new ArrayList<SqlLiteCommiterOne>();

		for (SqlLiteConnectionOne con : cons) {
			threads.add(new SqlLiteCommiterOne(con));
		}
		for (SqlLiteCommiterOne sqlLiteCommiter : threads) {
			try {
				sqlLiteCommiter.getThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void execute(int zoom, int x, int y, byte[] data, int rcn, long date) {
		ArrayList<SqlLiteConnectionOne> spl = SqliteExporterOne.getConnections(
				zoom, x, y);
		for (SqlLiteConnectionOne con : spl) {
			try {
				con.execute(zoom, x, y, data, rcn, date);
				// System.out.println(con.getConn().getAutoCommit());
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

	public Thread getThread() {
		// TODO Auto-generated method stub
		return thread;
	}

}
