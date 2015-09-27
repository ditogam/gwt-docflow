package com.socarmap.server.tasks;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.TimerTask;

import com.socargass.tabletexporter.GetMap;
import com.socarmap.server.Constants;
import com.socarmap.server.db.DBOperations;

public class TilesCheckerWithoutThreads extends TimerTask {

	public TilesCheckerWithoutThreads() {
		System.out.println("Task created");
		last_time = System.currentTimeMillis();
	}

	long last_time;

	@Override
	public void run() {
		System.out.println("Task started"
				+ (System.currentTimeMillis() - last_time));
		last_time = System.currentTimeMillis();
		generateRCNS();
		downloadTiles();
	}

	private void downloadTiles() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select rcn_id rcn from maps.rcn_tbl where not proceeded order by 1 limit ?";
			conn = DBOperations.getConnection(Constants.DBN_MAP);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 2);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int rcn = rs.getInt("rcn");
				proceedRcn(rcn, conn);
			}
			conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}

	}

	private void proceedRcn(int rcn, Connection conn) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement psSave = null;
		CallableStatement psSaveMapData = null;
		try {
			String sql = "select m.zoom,x,y from maps.zoom_xy m where rcn_id= ? and not created";
			psSave = conn
					.prepareStatement("update maps.rcn_tbl set proceeded=true where  rcn_id=? ");
			psSaveMapData = conn
					.prepareCall("{ ? = call  maps.savemapdata(?,?,?,?) }");
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, rcn);
			rs = stmt.executeQuery();
			int cnt = 0;
			int logic_err = 0;
			int ph_err = 0;
			while (rs.next()) {
				cnt++;
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int zoom = rs.getInt("zoom");
				byte[] bt = null;
				try {

					bt = GetMap.getBytes(zoom + "", x + "", y + "");
					if (new String(bt).contains("xception")) {
						logic_err++;
						continue;
					}
				} catch (Exception e) {
					ph_err++;
					e.printStackTrace();
					continue;
				}
				System.out.println("start saving Zoom=" + zoom + " x=" + x
						+ " y=" + y + " RCN=" + rcn + " btsize=" + bt.length);
				psSaveMapData.registerOutParameter(1, Types.DOUBLE);
				psSaveMapData.setInt(2, zoom);
				psSaveMapData.setInt(3, x);
				psSaveMapData.setInt(4, y);
				psSaveMapData.setBytes(5, bt);
				psSaveMapData.executeUpdate();
				System.out.println("end Zoom=" + zoom + " x=" + x + " y=" + y
						+ " RCN=" + rcn + " btsize=" + bt.length);
			}
			System.out.println("RCN=" + rcn + " cnt=" + cnt + " logic_err="
					+ logic_err + " ph_err=" + ph_err);
			// psSaveMapData.executeBatch();
			psSave.setInt(1, rcn);
			psSave.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, psSave, psSaveMapData);
		}

	}

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
