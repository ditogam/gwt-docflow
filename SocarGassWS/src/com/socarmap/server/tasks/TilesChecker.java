package com.socarmap.server.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TimerTask;

import com.socarmap.server.Constants;
import com.socarmap.server.db.DBOperations;

public class TilesChecker extends TimerTask {

	public TilesChecker() {
		// System.out.println("Task created");
		// last_time = System.currentTimeMillis();
	}

	long last_time;

	@Override
	public void run() {
		// System.out.println("Task started"
		// + (System.currentTimeMillis() - last_time));
		// last_time = System.currentTimeMillis();
		// generateRCNS();
		// downloadTiles();
		// SqliteExporterOne.checkNewTiles();
	}

	@SuppressWarnings("unused")
	private void downloadTiles() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select rcn_id rcn from maps.rcn_tbl where not proceeded order by 1 limit ?";
			conn = DBOperations.getConnection(Constants.DBN_MAP);
			conn.setAutoCommit(true);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 10);
			rs = stmt.executeQuery();
			ArrayList<TileCreatorThread> threads = new ArrayList<TileCreatorThread>();
			while (rs.next()) {
				int rcn = rs.getInt("rcn");
				threads.add(new TileCreatorThread(rcn));
			}
			for (TileCreatorThread tileCreatorThread : threads) {
				tileCreatorThread.createThread();
			}
			for (TileCreatorThread tileCreatorThread : threads) {
				tileCreatorThread.getThread().join();
			}
			// conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}

	}

	@SuppressWarnings("unused")
	private void generateRCNS() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select maps.creatercnnew(?) rcn from generate_series(1, ?)";
			conn = DBOperations.getConnection(Constants.DBN_MAP);
			conn.setAutoCommit(true);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 3000);
			stmt.setInt(2, 5);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int rcn = rs.getInt("rcn");
				if (rcn < 0)
					return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}
	}

}
