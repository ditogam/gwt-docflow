package com.docflow.server.db;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.common.db.DBConnection;
import com.common.shared.ClSelectionItem;
import com.common.shared.DSFieldOptions;
import com.docflow.server.ImageData;
import com.docflow.shared.AndroidStatusCounts;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.DocStatusCount;
import com.docflow.shared.ListSizes;
import com.docflow.shared.MDBConnectionCommon;
import com.docflow.shared.Meter;
import com.docflow.shared.MeterPlombs;
import com.docflow.shared.StatusObject;
import com.docflow.shared.UserObject;
import com.docflow.shared.User_Data;
import com.docflow.shared.common.BFUMObject;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.PermitionItem;
import com.docflow.shared.common.User_Group;
import com.docflow.shared.common.ZoneChangeConfiguration;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentLog;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflow.shared.hr.Captions;

public class MDBConnection implements MDBConnectionCommon {

	static {
		DBConnection.mapJni.put("Gass", "jdbc/callsql");
		DBConnection.mapJni.put("DocFlow", "jdbc/docflow");
		DBConnection.mapJni.put("MAP", "jdbc/map");
		DBConnection.mapJni.put("HR", "jdbc/hr");
	}

	public static void bOperationsActive(int operid, int user_id,
			String user_name) throws Exception {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select feeder.boperationsactive(?,?,?)";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			stmt.setLong(1, operid);
			stmt.setInt(2, user_id);
			stmt.setString(3, user_name);

			rs = stmt.executeQuery();
			if (rs.next()) {

			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static void changeZoneToCustomers(int[] customerIds, long zone)
			throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "update customer set zone=? where cusid=?";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			for (int cusId : customerIds) {
				stmt.setLong(1, zone);
				stmt.setInt(2, cusId);
				stmt.addBatch();
			}

			stmt.executeBatch();
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static Integer closeBankByDay(int bankid, Date bankDate, int pCity)
			throws Exception {
		Integer result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select closebankday" + (pCity < 0 ? "p" : "") + "(?,?,?)";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bankid);
			stmt.setDate(2, new java.sql.Date(bankDate.getTime()));
			stmt.setInt(3, Math.abs(pCity));
			rs = stmt.executeQuery();
			if (rs.next()) {
				String s = rs.getString(1);
				try {
					result = new Integer(s);
				} catch (Exception e) {

				}
				System.out.println(s);
			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}
		return result;

	}

	public static void devicedelete(int id, int deviceid) throws Exception {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select devicedelete(?,?)";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			stmt.setLong(1, id);
			stmt.setLong(2, deviceid);

			rs = stmt.executeQuery();
			if (rs.next()) {

			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static DocumentShort documentChangeState(int doc, String replica,
			int doc_state, int user_id, int languageId, long time,
			boolean anyway) throws Exception {
		Connection connection = null;
		try {
			connection = DBConnection.getConnection("DocFlow");
			documentChangeState(doc, replica, doc_state, user_id, time,
					languageId, anyway, connection);

			DocumentShort result = getDoc(doc, languageId, connection);
			connection.commit();
			return result;
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(connection);

			} catch (Exception e2) {

			}
		}
	}

	public static void bashConfiremed(String replica, int user_id, int count)
			throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		String sql = "insert into bash_confirms (user_id,bcount,replica) values (?,?,?)";

		try {
			connection = DBConnection.getConnection("DocFlow");
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, user_id);
			stmt.setInt(2, count);
			stmt.setString(3, replica);
			stmt.execute();
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				stmt.close();
				;

			} catch (Exception e2) {

			}

			try {
				DBConnection.freeConnection(connection);

			} catch (Exception e2) {

			}
		}
	}

	public static Integer documentChangeState(int doc, String replica,
			int doc_state, int user_id, long time, int languageId,
			boolean anyway, Connection connection) throws Exception {
		Integer result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select changeDocState( ?,?,?,?,?,?,?)";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, doc);
			stmt.setInt(2, doc_state);
			stmt.setString(3, replica);
			stmt.setInt(4, user_id);
			stmt.setTimestamp(5, new Timestamp(time));
			stmt.setInt(6, languageId);
			stmt.setBoolean(7, anyway);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static DocumentShort documentCorrection(int doc, String content_xml,
			int user_id, long transaction_date, long doc_date,
			ArrayList<DocumentFile> files, int languageId, int docdelay)
			throws Exception {
		Connection connection = null;
		try {
			connection = DBConnection.getConnection("DocFlow");
			documentCorrection(doc, content_xml, user_id, transaction_date,
					doc_date, docdelay, connection);
			if (files != null && files.size() > 0) {
				saveFiles(doc, files, connection);
			}
			DocumentShort result = getDoc(doc, languageId, connection);
			connection.commit();
			return result;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(connection);
			} catch (Exception e2) {

			}
		}
	}

	public static Integer documentCorrection(int doc, String content_xml,
			int user_id, long transaction_date, long doc_date, int docdelay,
			Connection connection) throws Exception {
		Integer result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select documentcorrection( ?,?,?,?,?,?)";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, doc);
			stmt.setString(2, content_xml);
			stmt.setTimestamp(3, new Timestamp(doc_date));
			stmt.setInt(4, user_id);
			stmt.setTimestamp(5, new Timestamp(transaction_date));
			stmt.setInt(6, docdelay);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static CustomerShort getCustomerShort(int cusid) throws Exception {
		CustomerShort result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select * from v_customer_full c where c.cusid=? ";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, cusid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getCustomerShort(rs);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {

				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ClSelectionItem getDocumentStateValue(Long id)
			throws Exception {
		ClSelectionItem result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select doc_status_id,replic from documents where id=? ";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, id.intValue());
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new ClSelectionItem();
				result.setId(rs.getInt("doc_status_id"));
				result.setValue(rs.getString("replic"));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {

				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static DocumentShort getDoc(int docid, int languageId,
			Connection connection) throws Exception {
		DocumentShort result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTSHORT_FIELDS + " from document_v d"
				+ " where languageid=? and d.id=?";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setInt(2, docid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getDocumentShort(rs, null);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<DocumentShort> getDocList(int doc_type,
			long dateStart, long dateEnd, int languageId, int userid)
			throws Exception {
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select "
				+ DOCUMENTSHORT_FIELDS
				+ " from document_v d"
				+ " where d.languageid=?  and (?>0 or d.user_id=?) and "
				+ (doc_type < 0 ? "d.group_id" : "d.doc_type_id")
				+ "=?  and d.doc_date>=? and d.doc_date<=? order by d.doc_date desc";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setInt(2, userid);
			stmt.setInt(3, userid);
			stmt.setInt(4, Math.abs(doc_type));
			stmt.setTimestamp(5, new Timestamp(trim(dateStart)));
			stmt.setTimestamp(6, new Timestamp(trim(dateEnd)));
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getDocumentShort(rs, null));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static DocTypeWithDocList getDocListForType(int doctypeid,
			long startdate, long enddate, int languageId,
			ArrayList<String> criterias, boolean print, boolean very_short,
			boolean xml, ListSizes sizes) throws Exception {
		DocTypeWithDocList r = new DocTypeWithDocList();
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();
		Connection con = null;
		String sql_pattern = "select %s from document_v d where d.languageid=?  and d.doc_date>=? and d.doc_date<=? and 1=1 and ";

		String criteria = "";
		for (String key : criterias) {
			if (criteria.length() > 0)
				criteria += " and ";
			criteria += key;
		}
		Timestamp ts_startdate = new Timestamp(trim(startdate));
		Timestamp ts_enddate = new Timestamp(trim(enddate));
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = DBConnection.getConnection("DocFlow");
			if (sizes != null) {
				r.setStart_row(sizes.getStart_row());
				r.setEnd_row(sizes.getEnd_row());
			}
			if (sizes != null && sizes.isGenerate_sizes()) {
				String sql = String.format(sql_pattern, "count(1) cnt");
				sql += criteria;
				try {
					stmt = con.prepareStatement(sql);
					stmt.setInt(1, languageId);
					stmt.setTimestamp(2, ts_startdate);
					stmt.setTimestamp(3, ts_enddate);
					rs = stmt.executeQuery();
					if (rs.next())
						r.setTotal_count(rs.getInt("cnt"));

					r.setStart_row(r.getStart_row() > r.getTotal_count() ? r
							.getTotal_count() : r.getStart_row());
					r.setEnd_row(r.getEnd_row() > r.getTotal_count() ? r
							.getTotal_count() : r.getEnd_row());

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
			}
			if (sizes != null) {
				r.setStart_row(sizes.getStart_row());
				r.setEnd_row(sizes.getEnd_row());
			}

			String fields = xml ? DOCUMENTSHORT_WITH_DOC_TYPE_FIELDS
					: DOCUMENTSHORT_FIELDS;

			String sql = String.format(sql_pattern, fields);
			sql += criteria;
			try {

				sql += "  order by d.doc_date desc ";
				if (sizes != null) {
					sql += " OFFSET " + r.getStart_row() + " limit "
							+ (r.getEnd_row() - r.getStart_row());
				}
				stmt = con.prepareStatement(sql);
				stmt.setInt(1, languageId);
				stmt.setTimestamp(2, ts_startdate);
				stmt.setTimestamp(3, ts_enddate);
				rs = stmt.executeQuery();
				while (rs.next()) {
					result.add(DBMapping.getDocumentShort(rs, null, very_short,
							xml));
				}
				r.setDocList(result);
				return r;
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return null;
	}

	public static ArrayList<DocumentShort> getDocList(long startdate,
			long enddate, int languageId, ArrayList<String> criterias)
			throws Exception {
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select getcontent_for_doc_type(doc_values_from_xml,content_xml) content_for_doc_type,"
				+ DOCUMENTSHORT_FIELDS
				+ " from document_v d where d.languageid=?  and d.doc_date>=? and d.doc_date<=? and 1=1 and ";

		String criteria = "";
		for (String key : criterias) {
			if (criteria.length() > 0)
				criteria += " and ";
			criteria += key;
		}
		// +
		sql += criteria + "  order by d.doc_date desc";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setTimestamp(2, new Timestamp(trim(startdate)));
			stmt.setTimestamp(3, new Timestamp(trim(enddate)));
			// stmt.setTimestamp(5, date);
			rs = stmt.executeQuery();
			while (rs.next()) {
				DocumentShort ds = DBMapping.getDocumentShort(rs, null, true,
						false);
				ds.setDoc_template(rs.getString("content_for_doc_type"));
				ds.setDoc_template(ds.getDoc_template() == null ? "" : ds
						.getDoc_template());
				result.add(ds);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<DocumentLog> getDocLogList(int docid,
			int languageId, Connection connect) throws Exception {
		ArrayList<DocumentLog> result = new ArrayList<DocumentLog>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTLOG_FIELDS + " from document_log_v d"
				+ " where d.id=? and d.languageid=? order by d.version_id desc";
		try {
			con = connect == null ? DBConnection.getConnection("DocFlow")
					: connect;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, docid);
			stmt.setInt(2, languageId);
			// stmt.setTimestamp(5, date);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getDocumentLog(rs, null, true));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connect == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static DocumentLong getDocLong(int docid, int languageId)
			throws Exception {
		Connection connection = null;
		DocumentLong result = null;
		try {
			connection = DBConnection.getConnection("DocFlow");

			result = getDocLong(docid, languageId, connection);
			result.setHistory(getDocLogList(docid, languageId, connection));
			connection.commit();

		} catch (Exception e) {

			try {
				connection.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(connection);

			} catch (Exception e2) {

			}
		}
		return result;
	}

	public static DocumentLong getDocLong(int docid, int languageId,
			Connection connect) throws Exception {
		DocumentLong result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTLONG_FIELDS + " from document_v d"
				+ " where languageid=? and d.id=?";
		try {
			con = connect == null ? DBConnection.getConnection("DocFlow")
					: connect;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setInt(2, docid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getDocumentLong(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connect == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<DocStatusCount> getDocStatusCount(int languageId)
			throws Exception {
		ArrayList<DocStatusCount> result = new ArrayList<DocStatusCount>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select * from docstatuscount_v where typelang=?";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getDocStatusCount(rs));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static DocType getDocType(int doc_type_id, int languageId)
			throws Exception {
		DocType result = null;

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select id, doc_type_name_id, group_id, typelang, doctypevalue, doctypegroupvalue,doc_template,cust_sql,cust_selectfields,applied_customer,realdoctypeid,datefield,delayinterval,system_id from doc_type_v "
				+ "where hidden=false and typelang="
				+ languageId
				+ " and id="
				+ doc_type_id + " order by group_id,id";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result = DBMapping.getDocType(rs, true, true);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<DocType> getDocTypes(int languageId, int user_id,
			int system_id, Connection connect) throws Exception {

		ArrayList<DocType> result = new ArrayList<DocType>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select id, doc_type_name_id, group_id, typelang, doctypevalue, doctypegroupvalue,datefield,delayinterval,system_id,applied_customer,cust_sql from doc_type_v "
				+ "where ";
		if (user_id >= 0) {
			sql += " hasDocTypePermition(id," + user_id + ") and ";
		}
		if (system_id >= 0) {
			sql += " system_id=" + system_id + " and ";
		}
		sql += " hidden=false and typelang=" + languageId
				+ " order by sort_order,dt_sort_order";
		try {
			con = connect == null ? DBConnection.getConnection("DocFlow")
					: connect;
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				DocType dt = DBMapping.getDocType(rs, true, false);
				dt.setApplied_customer(rs.getBoolean("applied_customer"));
				result.add(dt);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connect == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<DocumentFile> getFilesForDocument(int docId)
			throws Exception {
		ArrayList<DocumentFile> result = new ArrayList<DocumentFile>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select id,document_id,image_id,filename from document_files "
				+ "where document_id=" + docId;
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(DBMapping.getDocumentFile(rs));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ImageData getImageData(ResultSet rs) throws Exception {
		ImageData result = new ImageData();
		result.setId(rs.getInt("id"));
		result.setImgData(rs.getBytes("imgData"));
		result.setImagename(rs.getString("imgname"));
		result.setContenttype(rs.getString("contenttype"));
		return result;
	}

	public static ImageData getImage(int id, Connection connection)
			throws Exception {
		ImageData result = null;
		String sql = "select id,imgdata,imgname,contenttype  from tblimages where id="
				+ id;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;

			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = (getImageData(rs));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ImageData getImageHr(int id) throws Exception {
		ImageData result = null;
		String sql = "select person_id id,person_picture imgdata,person_picture_filename imgname, '' contenttype  from person where person_id="
				+ id;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection("HR");
			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = (getImageData(rs));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<MeterPlombs> getMetterPlombs(int metterId,
			Connection con) throws Exception {
		ArrayList<MeterPlombs> result = new ArrayList<MeterPlombs>();

		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from v_plomb " + "where meterid=" + metterId;
		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(DBMapping.getMetterPlombs(rs));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
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

		return result;
	}

	public static Meter getMetterValue(int metterid, boolean withplombs)
			throws Exception {

		Meter result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select m.cusid,mtypeid,start_index,meterid,metserial,regdate,mstatusid,(select d.newval  from docmeter d where d.lastindex = 1 and d.meterid=m.meterid) mettervalue from meter m where m.meterid=?";

		Connection con = null;
		try {
			con = DBConnection.getConnection("Gass");

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, metterid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getMetter(rs);
				if (withplombs) {
					result.setMeterPlombs(getMetterPlombs(metterid, con));
				}
			}

			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<PermitionItem> getPermitionItems(boolean user,
			int user_or_group_id) throws Exception {
		ArrayList<PermitionItem> result = new ArrayList<PermitionItem>();
		String sql = "select p.id permition_id, permission_name permitionName,(pt.permition_id is not null) applyed\n"
				+ ",(pt.permition_id is not null and not pt.accepted) denied from spermitions p\n"
				+ "left join permition_table pt on p.id=pt.permition_id and pt.userperm=? and pt.user_or_group_id=?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setBoolean(1, user);
			stmt.setInt(2, user_or_group_id);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getPermitionItem(rs));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static Long getServerTime(Connection conn) throws Exception {
		Long result = null;
		String sql = "select now()";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;
			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getTimestamp(1).getTime();

			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<User_Group> getUser_Groups(int user_id)
			throws Exception {
		ArrayList<User_Group> result = new ArrayList<User_Group>();
		String sql = "select g.id group_id,group_name gname, (ug.group_id is not null) applyed from sgroups g\n"
				+ " left join user_groups ug on ug.group_id=g.id and ug.user_id=?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, user_id);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getUser_Group(rs));
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static int[] getUserAddress(int userid, Connection conn)
			throws Exception {
		int[] result = new int[] { -1, -1 };
		String sql = "select regionid,subregionid from susers where id="
				+ userid;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;
			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result[0] = rs.getInt("regionid");
				result[1] = rs.getInt("subregionid");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static HashMap<String, ArrayList<ClSelectionItem>> getListTypes(
			HashMap<String, ArrayList<DSFieldOptions>> listSqls)
			throws Exception {
		HashMap<String, ArrayList<ClSelectionItem>> result = new HashMap<String, ArrayList<ClSelectionItem>>();
		Set<String> keys = listSqls.keySet();
		for (String key : keys) {
			Connection conn = null;
			try {
				conn = DBConnection.getConnection(key);
				ArrayList<DSFieldOptions> fItems = listSqls.get(key);
				for (DSFieldOptions f : fItems) {
					ArrayList<ClSelectionItem> items = new ArrayList<ClSelectionItem>();
					result.put(f.getFieldName(), items);
					ResultSet rs = null;
					try {
						rs = conn.prepareStatement(f.getFieldTitle())
								.executeQuery();
						while (rs.next()) {
							ClSelectionItem item = new ClSelectionItem();
							item.setId(rs.getLong(1));
							item.setValue(rs.getString(2));
							items.add(item);
						}
					} catch (Exception e) {

					} finally {
						try {
							Statement stmt = rs.getStatement();
							try {
								rs.close();
							} catch (Exception e2) {
							}
							try {
								stmt.close();
							} catch (Exception e2) {
							}
						} catch (Exception e2) {
						}
					}
				}

			} catch (Exception e) {

			} finally {
				try {
					DBConnection.freeConnection(conn);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		return result;
	}

	public static HashMap<String, ArrayList<ClSelectionItem>> getValueList(
			HashMap<String, String> listSql, int custid, String dbName)
			throws Exception {
		HashMap<String, ArrayList<ClSelectionItem>> result = new HashMap<String, ArrayList<ClSelectionItem>>();

		Connection con = null;

		try {
			con = DBConnection.getConnection(dbName);

			Set<String> kesSet = listSql.keySet();
			for (String key : kesSet) {
				String sql = listSql.get(key);
				PreparedStatement stmt = null;
				ResultSet rs = null;
				try {
					stmt = con.prepareStatement(sql);
					if (sql.contains("?"))
						stmt.setInt(1, custid);
					ArrayList<ClSelectionItem> items = new ArrayList<ClSelectionItem>();
					rs = stmt.executeQuery();
					while (rs.next()) {
						ClSelectionItem item = new ClSelectionItem();
						item.setId(rs.getLong(1));
						item.setValue(rs.getString(2));
						items.add(item);
					}
					result.put(key, items);

				} catch (Exception e) {
					e.printStackTrace();
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

			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {

			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static HashMap<String, String> getValueMap(String fieldNames[],
			String sql, int custid, String dbName) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = DBConnection.getConnection(dbName);
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, custid);

			rs = stmt.executeQuery();
			if (rs.next()) {
				for (String key : fieldNames) {
					String value = rs.getString(key);
					result.put(key, value);
				}
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static void saveDocType(DocType dt) {
		Connection con = null;
		PreparedStatement stmt = null;
		String sql = "update doc_type set doc_template=?,cust_sql=?,cust_selectfields=?,realdoctypeid=? where id=?";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setString(1, dt.getDoc_template());
			stmt.setString(2, dt.getCust_sql());
			stmt.setString(3, dt.getCust_selectfields());
			stmt.setString(4, dt.getRealdoctypeid());
			stmt.setInt(5, dt.getId());
			stmt.executeUpdate();
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static DocumentShort saveDocument(DocumentLong doc,
			ArrayList<DocumentFile> files, int languageid) throws Exception {
		Connection connection = null;
		try {
			connection = DBConnection.getConnection("DocFlow");
			Integer id = saveDocument(doc, connection);
			if (files != null && files.size() > 0) {
				saveFiles(id, files, connection);
			}
			DocumentShort result = getDoc(id.intValue(), languageid, connection);
			connection.commit();
			return result;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(connection);
			} catch (Exception e2) {

			}
		}
	}

	public static ArrayList<AndroidStatusCounts> getAndroidStatusStats(
			Connection connection, int system_id) throws Exception {

		ArrayList<AndroidStatusCounts> result = new ArrayList<AndroidStatusCounts>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select subregionid,system_id,cnt,min_doc_date,max_doc_date from android_doc_stats_v where system_id=?";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, system_id);

			rs = stmt.executeQuery();
			if (rs.next()) {
				AndroidStatusCounts r = new AndroidStatusCounts(
						rs.getInt("subregionid"), rs.getInt("system_id"),
						rs.getInt("cnt"), rs.getTimestamp("min_doc_date")
								.getTime(), rs.getTimestamp("max_doc_date")
								.getTime());
				result.add(r);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static StatusObject getStatuses(Connection connection, int system,
			StatusObject result) throws Exception {
		if (result == null)
			result = new StatusObject();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select initial_status,approved_status,applied_status,error_status,next_status, case when next_status is null then false else true end check_for_statuses from doc_systems where id=?";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, system);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result.setInitial_status(rs.getInt("initial_status"));
				result.setApproved_status(rs.getInt("approved_status"));
				result.setApplied_status(rs.getInt("applied_status"));
				result.setError_status(rs.getInt("error_status"));
				result.setCheck_for_statuses(rs
						.getBoolean("check_for_statuses"));
				result.setNext_status(rs.getInt("next_status"));
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static ArrayList<Integer> getAndroidStatusesSystems(
			Connection connection) throws Exception {

		ArrayList<Integer> result = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select system_id from android_doc_statuses_v ";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result.add(rs.getInt("initial_status"));
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static Integer saveDocument(DocumentLong doc, Connection connection)
			throws Exception {
		Integer result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (doc.getCustomer_name() == null) {
			doc.setCustomer_name("");
		}
		String sql = "select addeditdocument( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, doc.getId());
			stmt.setString(2, doc.getContent_xml());
			stmt.setTimestamp(3, new Timestamp(doc.getDoc_date()));
			stmt.setInt(4, doc.getDoc_status_id());
			stmt.setInt(5, doc.getDoc_type_id());
			stmt.setString(6, doc.getDoc_flow_num());
			stmt.setInt(7, doc.getUser_id());
			stmt.setInt(8, doc.getCust_id());
			stmt.setInt(9, doc.getStreet_id());
			stmt.setString(10, doc.getStreenname());
			stmt.setInt(11, doc.getCityid());
			stmt.setString(12, doc.getCityname());
			stmt.setInt(13, doc.getSubregionid());
			stmt.setString(14, doc.getSubregionname());
			stmt.setInt(15, doc.getRegionid());
			stmt.setString(16, doc.getRegionname());
			stmt.setLong(17, doc.getCzona());
			stmt.setInt(18, doc.getController_id());
			stmt.setTimestamp(19, new Timestamp(doc.getDoc_date_start()));
			stmt.setTimestamp(20, new Timestamp(doc.getTransaction_date()));
			stmt.setTimestamp(21,
					new Timestamp(doc.getDoc_date_status_change()));
			stmt.setLong(22, doc.getVersion_id());
			stmt.setString(23, doc.getCancelary_nom());
			stmt.setString(24, doc.getCustomer_name());
			stmt.setInt(25, doc.getDelaystatus());
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static void saveFiles(Integer id, ArrayList<DocumentFile> files,
			Connection connection) throws Exception {
		PreparedStatement psInsert = null;
		PreparedStatement psDelete = null;
		Connection con = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;
			psInsert = con
					.prepareStatement("insert into document_files(document_id,image_id,filename) values (?,?,?)");
			psDelete = con
					.prepareStatement("delete from document_files where id=?");
			for (DocumentFile df : files) {
				if (df.getId() < 0) {
					psDelete.setInt(1, Math.abs(df.getId()));
					psDelete.executeUpdate();
				} else {
					if (df.getId() == 0) {
						psInsert.setInt(1, id);
						psInsert.setInt(2, df.getImage_id());
						psInsert.setString(3, df.getFilename());
						psInsert.executeUpdate();
					}
				}
			}
			if (connection == null)
				con.commit();
		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {

				psInsert.close();
			} catch (Exception e2) {

			}
			try {

				psDelete.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static Integer saveImageData(ImageData imgData, Connection connection)
			throws Exception {
		Integer result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select addImage(?,?,?)";

		Connection con = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;

			stmt = con.prepareStatement(sql);
			stmt.setBytes(1, new byte[0]);
			stmt.setString(2, imgData.getImagename());
			stmt.setString(3, imgData.getContenttype());
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static String getImageStoreFolder(Connection connection)
			throws Exception {
		String result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select images_locations from configurations";

		Connection con = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;

			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static String getImageStoreFolderTmp(Connection connection)
			throws Exception {
		String result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select images_tmp_locations from configurations";

		Connection con = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;

			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static SimpleFTPClient getImageFtp(Connection connection)
			throws Exception {
		SimpleFTPClient result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select ftp_host,ftp_port,ftp_user,ftp_pwd,images_locations,ftp_connect_timeout from configurations";

		Connection con = null;
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow")
					: connection;

			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				String server, username, password, location;
				int port;
				server = rs.getString("ftp_host");
				port = rs.getInt("ftp_port");
				username = rs.getString("ftp_user");
				password = rs.getString("ftp_pwd");
				location = rs.getString("images_locations");
				int ftp_connect_timeout = rs.getInt("ftp_connect_timeout");
				result = new SimpleFTPClient();
				result.connect(server, port);
				result.login(username, password);
				result.setFtp_connect_timeout(ftp_connect_timeout);

				if (location != null && location.trim().length() > 0)
					result.changeWorkingDirectory(location);
			}
			if (connection == null)
				con.commit();

		} catch (Exception e) {
			try {
				if (connection == null)
					con.rollback();
			} catch (Exception e2) {

			}
			e.printStackTrace();
			throw e;

		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (connection == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static void reloadClSelections(Connection conn) {

		Connection con = null;
		ArrayList<ClSelection> selections = new ArrayList<ClSelection>();
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;

			String sql = "select * from clselection";
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					selections.add(DBMapping.getClSelection(rs));

				}

			} catch (Exception e) {

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

		} catch (Exception e) {
		} finally {

			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		ClSelection.SELECTIONS = selections.toArray(new ClSelection[] {});
		ClSelection.reloaded = true;
	}

	public static HashMap<String, String> getCustomDatasources(boolean dev,
			Connection conn) {

		Connection con = null;
		HashMap<String, String> selections = new HashMap<String, String>();
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;

			String sql = "select * from custom_ds";
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					selections.put(
							rs.getString("dsname"),
							!dev ? rs.getString("xml_text") : rs
									.getString("debug_xml"));
				}

			} catch (Exception e) {

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

		} catch (Exception e) {
		} finally {

			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return selections;
	}

	public static void getDS_AND_JS(UserObject uo) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select android_check_status_interval,android_map_renderer,p_debug_js and debug_js debug_js,p_debug_ds and debug_ds debug_ds,\n"
				+ "(select array_agg(case when debug_js and  p_debug_js  then  methode_body_debug else methode_body end ORDER BY m.id ) from android_methodes m)methode_bodys,\n"
				+ "case when debug_and and  p_debug_js  then  android_imports_debug else android_imports end android_imports,\n"
				+ "case when debug_js and  p_debug_js  then  javascript_debug else javascript end javascript from configurations,\n"
				+ "(select hasPermition('DEBUG_JS'::character varying,?) p_debug_js,hasPermition('DEBUG_DS'::character varying,?) p_debug_ds) p";

		Connection con = null;
		try {
			con = DBConnection.getConnection("DocFlow");

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, uo.getUser().getUser_id());
			stmt.setInt(2, uo.getUser().getUser_id());
			rs = stmt.executeQuery();
			if (rs.next()) {
				String javascript = rs.getString("javascript");
				javascript = (javascript == null ? "" : javascript).trim();
				uo.setJavascript(javascript);
				uo.setDebug_ds(rs.getBoolean("debug_ds"));
				uo.setDebug_js(rs.getBoolean("debug_js"));
				uo.setImports(rs.getString("android_imports"));
				uo.setAndroid_map_renderer(rs.getString("android_map_renderer"));
				uo.setAndroid_check_status_interval(rs
						.getInt("android_check_status_interval"));
				Array qArray = rs.getArray("methode_bodys");
				String[] arr = (String[]) qArray.getArray();

				uo.setMethodes(new ArrayList<String>());
				for (String string : arr) {
					uo.getMethodes().add(string);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw e;

		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {

				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static ZoneChangeConfiguration getZoConfiguration(Connection conn)
			throws Exception {
		ZoneChangeConfiguration result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select zone_change_charcount,zone_change_restricted_edits,coef_editable_columns,boper_acctype_default from configurations";

		Connection con = null;
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;

			stmt = con.prepareStatement(sql);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new ZoneChangeConfiguration();
				result.setCharcount(rs.getInt("zone_change_charcount"));
				result.setRestricted_edits_Str(rs
						.getString("zone_change_restricted_edits"));
				result.setCoef_editable_columns_Str(rs
						.getString("coef_editable_columns"));
				result.setBoper_acctype_default(rs
						.getInt("boper_acctype_default"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static User_Data getUser_Data(int user_id, Connection conn)
			throws Exception {
		User_Data result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select user_id,pwith,pheight,pborder,showtimeout,html from user_data where user_id=? and date_trunc('day',showdate)=date_trunc('day',now())";

		Connection con = null;
		try {
			con = conn == null ? DBConnection.getConnection("DocFlow") : conn;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, user_id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getUserData(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				if (conn == null)
					DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static Integer saveUsermanagerObject(BFUMObject umObject,
			int regionid, int subregionid) throws Exception {
		Integer result = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select saveusermanagerobject(?,?,?,?,?,?,?,?)";

		Connection con = null;
		try {
			con = DBConnection.getConnection("DocFlow");

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, umObject.getType());
			stmt.setInt(2, (int) umObject.getIdVal());
			stmt.setString(3, umObject.getTextVal());
			stmt.setBoolean(4, umObject.isPwdApplyed());
			stmt.setString(5, umObject.getPwd());
			stmt.setInt(6, (int) umObject.getCaption_id());
			stmt.setInt(7, regionid);
			stmt.setInt(8, subregionid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static void setPermitionItems(boolean user, int user_or_group_id,
			String permition_ids, String group_ids) throws Exception {

		String sql = "select setpermitions(?,?,?,?)";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (group_ids == null)
			group_ids = "";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setBoolean(2, user);
			stmt.setInt(1, user_or_group_id);
			stmt.setString(3, permition_ids);
			stmt.setString(4, group_ids);
			rs = stmt.executeQuery();
			if (rs.next()) {

			}
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	private static int[] getDocAndStatus(int customer_id, Connection con)
			throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int[] result = null;
		String sql;
		try {

			sql = "select id,doc_status_id from documents where doc_date::date=now()::date and cust_id=? and doc_type_id=30";

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, customer_id);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new int[2];
				result[0] = rs.getInt("id");
				result[1] = rs.getInt("doc_status_id");
			}
			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
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
	}

	private static String greateCoefContext(double coef) {
		return "<DocDef><Val key=\"coef\" value=\"" + coef + "\"/></DocDef>";
	}

	public static Integer[] saveCoefToCustomers(int[] customerIds, double coef,
			long date, int user_id, String cancelary) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			ArrayList<Integer> result = new ArrayList<Integer>();
			con = DBConnection.getConnection("DocFlow");
			ps = con.prepareStatement("select saveCoef(?,?,?,?,?) doc_id");

			ps.setInt(2, user_id);
			ps.setString(3, cancelary);
			ps.setString(4, greateCoefContext(coef));
			ps.setTimestamp(5, new Timestamp(date));
			int i = 0;
			long fullTime = System.currentTimeMillis();
			for (int cus_id : customerIds) {
				ResultSet rs = null;
				i++;
				long time = System.currentTimeMillis();
				try {
					ps.setInt(1, cus_id);
					rs = ps.executeQuery();
					if (rs.next()) {
						int doc_id = rs.getInt("doc_id");
						if (doc_id < 1)
							result.add(cus_id);
					} else
						result.add(cus_id);
					/*
					 * int[] v = getDocAndStatus(cus_id, con); if (v != null) {
					 * if (v[1] > 3) { result.add(cus_id); continue; } int
					 * doc_id = v[0]; updateDocumentCoef(doc_id, coef, user_id,
					 * date, cancelary, con); continue; }
					 * createDocumentCoef(cus_id, coef, user_id, date,
					 * cancelary, con);
					 */
				} catch (Exception e) {
					result.add(cus_id);
				} finally {
					try {
						rs.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
				time = System.currentTimeMillis() - time;
				time = time;
			}
			con.commit();
			fullTime = System.currentTimeMillis() - fullTime;
			double oneAvgTime = (double) fullTime / (double) customerIds.length;
			System.out.println("Size=" + customerIds.length + " fullTime="
					+ fullTime + " oneAvgTime=" + oneAvgTime);
			return result.toArray(new Integer[] {});
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

	}

	public static long trim(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
		System.out.println(cal.getTime());
		return cal.getTimeInMillis();
	}

	public static HashMap<Integer, Captions> getCaptions(Long id)
			throws Exception {
		if (id == null)
			return null;
		HashMap<Integer, Captions> result = new HashMap<Integer, Captions>();

		Connection con = null;

		try {
			con = DBConnection.getConnection("HR");

			String sql = "select id,language_id,cvalue from captions where id=?";
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.prepareStatement(sql);
				stmt.setLong(1, id);
				rs = stmt.executeQuery();
				while (rs.next()) {
					Captions c = DBMapping.getCaptions(rs);
					result.put(c.getLanguage_id(), c);
				}

			} catch (Exception e) {

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

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {

			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	public static Long saveCaptions(Captions[] captions) throws Exception {
		String sql = "select nextval('captions_id_seq'::regclass)";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Long result = null;
		boolean insert = false;
		for (Captions c : captions) {
			if (c.getId() > 0) {
				result = c.getId();
				break;
			}
		}
		insert = result == null;
		try {
			con = DBConnection.getConnection("HR");
			if (insert) {
				stmt = con.prepareStatement(sql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					result = rs.getLong(1);
				}

			}
			sql = "select addeditCaption(?,?,?)";

			stmt = con.prepareStatement(sql);
			stmt.setLong(1, result);

			for (Captions c : captions) {
				stmt.setInt(2, c.getLanguage_id());
				stmt.setString(3, c.getCvalue());
				rs = stmt.executeQuery();
				if (rs.next())
					;
				rs.close();
			}

			con.commit();
			return result;
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}
	}

	public static Integer closeBankByDayNew(int bankid, Date bankDate,
			int pCity, int acc_id, int user_id, String user_name)
			throws Exception {
		Integer result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select feeder.closebankday" + (pCity <= 0 ? "p" : "")
				+ "_acc(?,?,?,?,?,?)";
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bankid);
			stmt.setDate(2, new java.sql.Date(bankDate.getTime()));
			stmt.setInt(3, Math.abs(pCity));
			stmt.setInt(4, acc_id);
			stmt.setInt(5, user_id);
			stmt.setString(6, user_name);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String s = rs.getString(1);
				try {
					result = new Integer(s);
				} catch (Exception e) {

				}
				System.out.println(s);
			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}
		return result;
	}

	public static void saveDocTypePermitions(int user_or_group_id,
			boolean user, ArrayList<Integer> docTypes) throws Exception {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select saveDocTypePermition(?,?,?)";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, user_or_group_id);
			stmt.setBoolean(2, user);
			if (docTypes == null)
				docTypes = new ArrayList<Integer>();
			String sDocTypes = "";
			for (Integer id : docTypes) {
				if (sDocTypes.length() > 0)
					sDocTypes += ",";
				sDocTypes += id;
			}
			stmt.setString(3, sDocTypes);

			rs = stmt.executeQuery();
			if (rs.next()) {
			}
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}
	}

	public static ArrayList<Integer> getDocTypeRestrictions(
			int user_or_group_id, boolean user) throws Exception {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select doc_types from user_restricted_doc_types where user_perm=? and user_or_group_id=?";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setBoolean(1, user);
			stmt.setInt(2, user_or_group_id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String s = rs.getString("doc_types").trim();
				for (String id : s.split(",")) {
					try {
						result.add(Integer.valueOf(id));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {
				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}
		return result;
	}

	public static String checkForNewDocumentsAndroid(Long p_session_id,
			int p_user_id, int p_subregion_id, String p_system_ids,
			String p_android_device_id) throws Exception {
		String result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select android.create_android_session(?,?,?,?,?) result";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			int index = 1;
			stmt.setLong(index++, p_session_id == null ? 0 : p_session_id);
			stmt.setInt(index++, p_user_id);
			stmt.setInt(index++, p_subregion_id);
			stmt.setString(index++, p_system_ids);
			stmt.setString(index++, p_android_device_id);

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString("result");
			}
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {

			}
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {

			}
			try {
				stmt.close();
			} catch (Exception e2) {

			}
			try {

				DBConnection.freeConnection(con);
			} catch (Exception e2) {

			}
		}

		return result;
	}

	private static String[] args;

	static double scpr() {
		double curnt;
		double cloan = Double.parseDouble(args[0]);
		int inindex = Integer.parseInt(args[1]);
		if ((cloan > 600) || (inindex > 24 || inindex < 1))
			return 1000000;
		curnt = inindex * 25;
		if (curnt > cloan) {
			if (inindex == 1)
				return cloan;
			else {
				curnt = cloan - ((inindex - 1) * 25);
				if (curnt < 0)
					curnt = 0;
				return curnt;
			}
		}
		return 25;
	}

	public static void main(String[] args1) {
		String sql = "select getcontent_for_doc_type(doc_values_from_xml,content_xml) content_for_doc_type,"
				+ DOCUMENTSHORT_FIELDS
				+ " from document_v d where d.languageid=?  and d.doc_date>=? and d.doc_date<=? and 1=1 and ";
		System.out.println(sql);
	}

}
