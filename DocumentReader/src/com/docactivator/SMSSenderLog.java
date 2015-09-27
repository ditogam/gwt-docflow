package com.docactivator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.common.db.DBConnection;

public class SMSSenderLog {

	public static void operate() {
		Connection conDocFlow = null;
		Connection conGass = null;
		PreparedStatement psSelectDocFlow = null;
		ResultSet rsSelectDocFlow = null;
		PreparedStatement psLogDocFlow = null;
		PreparedStatement psLogSMSGass = null;
		try {
			conDocFlow = DBConnection.getConnection("DocFlow");
			conGass = DBConnection.getConnection("Gass");

			if (conDocFlow.getAutoCommit())
				conDocFlow.setAutoCommit(false);

			if (conGass.getAutoCommit())
				conGass.setAutoCommit(false);

			psSelectDocFlow = conDocFlow
					.prepareStatement("select doc_id,status_id,sending_direction_id,sending_direction_type,sms_template_id,sms_template from doc_sms_sending where lock_id =?");
			psLogDocFlow = conDocFlow
					.prepareStatement("insert into doc_sms_sending_log(lock_id,doc_id,status_id) values(?,?,?)");

			psLogSMSGass = conGass.prepareStatement("select smsc.send_sms(?,?,?,?,?) smsid");

			Integer lock_id = getLockeID(conDocFlow);
			if (lock_id == null || lock_id.intValue() == 0)
				return;
			psSelectDocFlow.setInt(1, lock_id);

			rsSelectDocFlow = psSelectDocFlow.executeQuery();
			while (rsSelectDocFlow.next()) {
				try {
					psLogDocFlow.setInt(1, lock_id);
					psLogDocFlow.setLong(2, rsSelectDocFlow.getLong("doc_id"));
					psLogDocFlow.setInt(3, rsSelectDocFlow.getInt("status_id"));
					psLogDocFlow.executeUpdate();

					ResultSet rs = null;
					try {
						psLogSMSGass.setLong(1, rsSelectDocFlow.getLong("sending_direction_id"));
						psLogSMSGass.setInt(2, rsSelectDocFlow.getInt("sending_direction_type"));
						psLogSMSGass.setInt(3, rsSelectDocFlow.getInt("sms_template_id"));
						String sms_template = rsSelectDocFlow.getString("sms_template");
						if (rsSelectDocFlow.wasNull() || sms_template == null)
							sms_template = "NULL";

						psLogSMSGass.setString(4, sms_template);
						psLogSMSGass.setLong(5, rsSelectDocFlow.getLong("doc_id"));
						rs = psLogSMSGass.executeQuery();
						int tmp = -111;
						if (rs.next())
							tmp = rs.getInt("smsid");
						System.out.println(tmp);
					} finally {
						try {
							rs.close();
						} catch (Exception e) {

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			conDocFlow.commit();
			conGass.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conDocFlow.rollback();
			} catch (Exception e2) {
			}
			try {
				conGass.rollback();
			} catch (Exception e2) {
			}
		} finally {
			try {
				conDocFlow.close();
			} catch (Exception e2) {
			}
			try {
				conGass.close();
			} catch (Exception e2) {
			}
		}

	}

	private static void doLogDocFlow(long doc_id, int status_id, Connection conDocFlow, PreparedStatement psLogDocFlow)
			throws Exception {
		// TODO Auto-generated method stub

	}

	private static Integer getLockeID(Connection conDocFlow) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conDocFlow.createStatement();
			rs = stmt.executeQuery("select lock_sms_sending() lock_id");
			if (rs.next())
				return rs.getInt("lock_id");
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
			}
			try {
				stmt.close();
			} catch (Exception e2) {
			}

		}
		return null;
	}

}
