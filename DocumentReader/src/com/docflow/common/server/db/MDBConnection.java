package com.docflow.common.server.db;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import com.common.db.DBConnection;
import com.docflow.shared.docflow.CustomerShort;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLog;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflow.shared.docflow.SMSSender;

public class MDBConnection {

	private final static String DOCUMENTLOG_FIELDS = "d.doc_date, d.doc_status_id, d.docstatus,d.user_id,d.user_name,d.delaystatus,"
			+ "d.controller_id,d.controller_name,d.doc_date_start,d.transaction_date,d.doc_date_status_change,d.version_id,d.content_xml";
	private final static String DOCUMENTSHORT_FIELDS = "d.id docid, d.doc_type_id,d.doctype,d.doc_flow_num,"
			+ DOCUMENTLOG_FIELDS
			+ ",d.cust_id, d.street_id, d.streenname, d.cityid, d.cityname, d.subregionid, d.subregionname, d.regionid, d.regionname, d.czona"
			+ ",cancelary_nom ";
	private final static String DOCUMENTLONG_FIELDS = DOCUMENTSHORT_FIELDS + ",d.content_xml, d.replic";

	public static ArrayList<DocType> getDocTypes(int languageId) throws Exception {

		ArrayList<DocType> result = new ArrayList<DocType>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select id, doc_type_name_id, group_id, typelang, doctypevalue, doctypegroupvalue,realdoctypeid from doc_type_v "
				+ "where typelang=" + languageId + " order by 3,1";
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result.add(DBMapping.getDocType(rs, true, false));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static TreeMap<String, String> getValueMap(String fieldNames[], String sql, int custid, String dbName)
			throws Exception {
		TreeMap<String, String> result = new TreeMap<String, String>();

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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static DocType getDocType(int doc_type_id, int languageId) throws Exception {
		DocType result = null;

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select id,realdoctypeid, doc_type_name_id, group_id, typelang, doctypevalue, doctypegroupvalue,doc_template,cust_sql,cust_selectfields,applied_customer from doc_type_v "
				+ "where typelang=" + languageId + " and id=" + doc_type_id + " order by group_id,id";
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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
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

	public static ArrayList<DocumentShort> getDocList(int doc_type, long dateStart, long dateEnd, int languageId,
			int userid) throws Exception {
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTSHORT_FIELDS + " from document_v d"
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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static ArrayList<SMSSender> getDocFSmsList(int count) throws Exception {
		ArrayList<SMSSender> result = new ArrayList<SMSSender>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select d.id docid,doc_type_id,cust_id,cancelary_nom,delaystatus,content_xml,applied_status,error_status from documents d  "
				+ " inner join doc_systems ds on ds.id=d.system_id and d.doc_status_id =ds.approved_status "
				+ " order by case when doc_type_id=30 then d.id*1000 else d.id end  limit " + count;
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			System.out.println(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
//				result.add(DBMapping.getDocumentLong(rs));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static ArrayList<DocumentLong> getApprovedDocList(int count) throws Exception {
		ArrayList<DocumentLong> result = new ArrayList<DocumentLong>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select d.id docid,doc_type_id,cust_id,cancelary_nom,delaystatus,content_xml,applied_status,error_status from documents d  "
				+ " inner join doc_systems ds on ds.id=d.system_id and d.doc_status_id =ds.approved_status "
				+ " order by case when doc_type_id=30 then d.id*1000 else d.id end  limit " + count;
		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			System.out.println(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getDocumentLong(rs));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static ArrayList<DocumentShort> getDocList(long startdate, long enddate, int languageId,
			ArrayList<String> criterias) throws Exception {
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTSHORT_FIELDS
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
				result.add(DBMapping.getDocumentShort(rs, null));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static DocumentShort getDoc(int docid, int languageId, Connection connection) throws Exception {
		DocumentShort result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTSHORT_FIELDS + " from document_v d" + " where languageid=? and d.id=?";
		try {
			con = connection == null ? DBConnection.getConnection("DocFlow") : connection;
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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if (connection == null)
					con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static DocumentLong getDocLong(int docid, int languageId, Connection connect) throws Exception {
		DocumentLong result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTLONG_FIELDS + " from document_v d" + " where languageid=? and d.id=?";
		try {
			con = connect == null ? DBConnection.getConnection("DocFlow") : connect;
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setInt(2, docid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = DBMapping.getDocumentLong(rs);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if (connect == null)
					con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static ArrayList<DocumentLog> getDocLogList(int docid, int languageId, Connection connect) throws Exception {
		ArrayList<DocumentLog> result = new ArrayList<DocumentLog>();

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select " + DOCUMENTLOG_FIELDS + " from document_log_v d"
				+ " where d.id=? and d.languageid=? order by d.version_id desc";
		try {
			con = connect == null ? DBConnection.getConnection("DocFlow") : connect;
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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if (connect == null)
					con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static CustomerShort getCustomerShort(int cusid) throws Exception {
		CustomerShort result = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select cusid,cusname,region,raion,zone,cityname,streetname,home,flat,subregionid,regionid,streetid,cityid from customerall3 c where c.cusid=? ";
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
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {

				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return result;
	}

	public static void main(String[] args) {
		String sql = "select " + "id docid,doc_type_id,cust_id,cancelary_nom,delaystatus,content_xml"
				+ " from documents d"
				+ " where d.doc_status_id=4 order by case when doc_type_id=30 then d.id*1000 else d.id end  limit "
				+ 500;
		System.out.println(sql);
	}
}
