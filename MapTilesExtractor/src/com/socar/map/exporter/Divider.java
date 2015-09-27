package com.socar.map.exporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.socar.map.downloader.Timer;

public class Divider {
	private static final int BUFFER_SIZE = 1024;
	public static String USER_AGENT = "OsmAnd~";
	private static final String FULL_TIME = "FULL_TIME";
	private static final String SELECT_TIME = "SELECT_TIME";
	private static final String DOWNLOAD_TIME = "DOWNLOAD_TIME";
	private static final String SAVE_TIME = "SAVE_TIME";
	private static final String COMMIT_TIME = "COMMIT_TIME";
	private static final String COMMIT_TIMEDEST = "COMMIT_TIMEDEST";
	private static final String COPY_TIME = "COPY_TIME";
	private static final String ONE_THREAD_TIME = "ONE_THREAD_TIME";
	private static final String EXCEPTION_COUNT = "EXCEPTION_COUNT";

	public Divider() throws Exception {
		// Timer.start(ONE_THREAD_TIME);
		Class.forName("org.sqlite.JDBC");
		Properties prop = new Properties();
		// prop.setProperty("shared_cache", "true");
		Connection connLiteSource = null;
		Connection connLiteDest = null;

		String sql = "select m.zoom, m.x,m.y,m.file_data from mapfiledatazxy m\n"
				+ "inner join subregionboundzxy12 s on m.zoom=s.zoom\n"
				+ "and m.x between s.x1 and s.x2\n"
				+ "and m.y between s.y1 and s.y2\n"
				+ "where proceeded =0 and s.subregion_id=24 limit 150000";
		PreparedStatement psSource = null;
		PreparedStatement psDest = null;

		try {
			connLiteSource = DriverManager.getConnection("jdbc:sqlite:"
					+ "TilesDB.sqlite", prop);
			connLiteDest = DriverManager.getConnection("jdbc:sqlite:"
					+ "test3.sqlite", prop);
			connLiteSource.setAutoCommit(false);
			connLiteDest.setAutoCommit(false);
			psSource = connLiteSource
					.prepareStatement("update mapfiledatazxy set proceeded=3 where zoom=? and x=? and y=?");
			psDest = connLiteDest
					.prepareStatement("insert into mapfiledatazxy(zoom,x,y,file_data) values(?,?,?,?)");
			int count = 10000;
			while (count > 0) {
				Statement stmt = connLiteSource.createStatement();
				Timer.start(SELECT_TIME);
				ResultSet rs = stmt.executeQuery(sql);
				Timer.step(SELECT_TIME);
				Timer.printall();
				count = 0;
				Timer.start(ONE_THREAD_TIME);
				while (rs.next()) {
					Timer.start(COPY_TIME);
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					int zoom = rs.getInt("zoom");
					byte[] bt = rs.getBytes("file_data");
					psDest.setInt(1, zoom);
					psDest.setInt(2, x);
					psDest.setInt(3, y);
					psDest.setBytes(4, bt);
					psDest.executeUpdate();
					Timer.step(COPY_TIME);
					Timer.start(SAVE_TIME);
					psSource.setInt(1, zoom);
					psSource.setInt(2, x);
					psSource.setInt(3, y);
					psSource.executeUpdate();
					Timer.step(SAVE_TIME);
					count++;
					if (count % 100 == 0) {
						Timer.printall();
					}
					if (count % 10000 == 0) {
						commit(connLiteSource, connLiteDest);
					}
				}
				Timer.step(ONE_THREAD_TIME);

				commit(connLiteSource, connLiteDest);

			}
			commit(connLiteSource, connLiteDest);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				connLiteSource.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connLiteDest.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		} finally {
			try {
				connLiteSource.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connLiteDest.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public void commit(Connection connLiteSource, Connection connLiteDest)
			throws SQLException {
		Timer.start(COMMIT_TIME);
		connLiteSource.commit();
		Timer.step(COMMIT_TIME);
		Timer.start(COMMIT_TIMEDEST);
		connLiteDest.commit();
		Timer.step(COMMIT_TIMEDEST);
		Timer.printall();
	}

	public static void main(String[] args) {
		try {
			new Divider();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
