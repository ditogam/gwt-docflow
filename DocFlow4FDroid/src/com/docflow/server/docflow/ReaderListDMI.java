package com.docflow.server.docflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.common.db.DBConnection;
import com.docflow.server.DMIUtils;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class ReaderListDMI {
	public DSResponse update(DSRequest req) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = req.getValueSets();
		@SuppressWarnings("unchecked")
		List<Map<?, ?>> listold = req.getOldValueSets();
		Object user_id = null;
		for (Map<?, ?> map : listold) {
			user_id = map.get("userid");
			if (user_id != null)
				break;
		}

		DSResponse resp = new DSResponse();
		resp.setStatus(DSResponse.STATUS_SUCCESS);
		Connection conn = null;
		try {
			conn = DBConnection.getConnection("Gass");

			for (Map<String, Object> mp : list) {
				// executeSelect(
				// conn,
				// "select feeder.insertreaderlistifnotexists("
				// + mp.get("id") + ")");

				Object id = mp.get("id");

				if (user_id == null)
					continue;
				mp.remove("id");
				mp.remove("userid");
				if (mp.isEmpty())
					continue;
				@SuppressWarnings("unchecked")
				Map<String, Object> oldmp = (Map<String, Object>) DMIUtils
						.findRecordById(req.getDataSourceName(), null, id, "id");

				if (!mp.containsKey("m3k")
						&& (mp.containsKey("oldval")
								|| mp.containsKey("newval") || mp
									.containsKey("temp_k"))) {

					Double oldval = getValue(mp.get("oldval"));
					Double newval = getValue(mp.get("newval"));
					Double temp_k = getValue(mp.get("temp_k"));
					if (oldval == null)
						oldval = getValue(oldmp.get("oldval"));
					if (newval == null)
						newval = getValue(oldmp.get("newval"));
					if (temp_k == null)
						temp_k = getValue(oldmp.get("temp_k"));
					if (oldval == null || newval == null || temp_k == null) {
					} else {
						Double m3 = (newval - oldval) * temp_k;
						DecimalFormat df2 = new DecimalFormat("#.##");
						m3 = df2.parse(df2.format(m3)).doubleValue();
						mp.put("m3k", m3);
					}
				}

				String sql = "update readerlist set readerdate=now()";
				String ubd = "";
				for (Object key : mp.keySet()) {
					ubd += ",";
					ubd += " " + key + "=" + mp.get(key) + "\n";
				}
				ubd += ", userid=" + user_id;
				sql += ubd + " where id=" + id;
				executeUpdate(conn, sql);

				ResultSet rs = conn.createStatement()
						.executeQuery(
								"select oldval,newval from readerlist r where id="
										+ id);

				oldmp.clear();
				if (rs.next()) {
					oldmp.put("oldval", rs.getDouble("oldval"));
					oldmp.put("newval", rs.getDouble("newval"));
				}
				rs.getStatement().close();
				rs.close();
				Double oldval = getValue(oldmp.get("oldval"));
				Double newval = getValue(oldmp.get("newval"));
				if (oldval != null && newval != null) {
					sql = "update readerlist set chdate=now(),m3="
							+ (newval - oldval) + " where id=" + id;
					executeUpdate(conn, sql);
				}
				// mp.clear();
				for (Object key : oldmp.keySet()) {
					if (!mp.containsKey(key))
						mp.put(key.toString(), oldmp.get(key));
				}
			}
			conn.commit();

		} catch (Exception e) {
			resp.setStatus(DSResponse.STATUS_FAILURE);
			try {
				conn.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw e;
		} finally {

			try {
				DBConnection.freeConnection(conn);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return resp;
	}

	private Double getValue(Object fieldVal) {
		try {
			return Double.parseDouble(fieldVal.toString().trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private void executeUpdate(Connection conn, String sql) throws Exception {
		PreparedStatement ps = null;
		ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();

	}

	// private void executeSelect(Connection conn, String sql) throws Exception
	// {
	// PreparedStatement ps = null;
	// ps = conn.prepareStatement(sql);
	// ps.executeQuery().close();
	// ps.close();
	// }
}
