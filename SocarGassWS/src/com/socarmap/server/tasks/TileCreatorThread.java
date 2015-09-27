package com.socarmap.server.tasks;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.socargass.tabletexporter.GetMap;
import com.socarmap.server.Constants;
import com.socarmap.server.db.DBOperations;

public class TileCreatorThread implements Runnable {
	private int rcn;

	public TileCreatorThread(int rcn) {
		this.rcn = rcn;
	}

	private Thread thread;

	@Override
	public void run() {
		proceedRcn(rcn);
	}

	public Thread createThread() {
		thread = new Thread(this, "MyName is" + System.nanoTime());
		thread.start();
		return thread;
	}

	public Thread getThread() {
		return thread;
	}

	private void proceedRcn(int rcn) {
		System.out
				.println("Started thread " + thread.getName() + " RCN=" + rcn);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement psSave = null;
		CallableStatement psSaveMapData = null;
		Connection conn = null;
		try {
			String sql = "select m.zoom,x,y from maps.zoom_xy m where rcn_id= ? and not created";
			conn = DBOperations.getConnection(Constants.DBN_MAP);
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
				psSaveMapData.registerOutParameter(1, Types.DOUBLE);
				psSaveMapData.setInt(2, zoom);
				psSaveMapData.setInt(3, x);
				psSaveMapData.setInt(4, y);
				psSaveMapData.setBytes(5, bt);
				psSaveMapData.executeUpdate();
				// if (cnt % 15 == 0)
				// System.out.println("proceeding Zoom=" + zoom + " x=" + x
				// + " y=" + y + " RCN=" + rcn);

			}
			System.out.println("RCN=" + rcn + " cnt=" + cnt + " logic_err="
					+ logic_err + " ph_err=" + ph_err + " thread "
					+ thread.getName());
			// psSaveMapData.executeBatch();
			psSave.setInt(1, rcn);
			psSave.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBOperations.closeAll(rs, stmt, psSave, psSaveMapData, conn);
		}

	}
}
