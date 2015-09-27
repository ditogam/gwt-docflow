package com.docflow.common.server.db;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.docflow.shared.docflow.CustomerShort;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeGroup;
import com.docflow.shared.docflow.DocumentLog;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;

public class DBMapping {

	public static DocTypeGroup getDocTypeGroup(ResultSet rs, DocTypeGroup gt,
			boolean withTitle) throws Exception {
		if (gt == null)
			gt = new DocTypeGroup();
		gt.setGroup_id(rs.getInt("group_id"));
		if (withTitle)
			gt.setDoctypegroupvalue(rs.getString("doctypegroupvalue"));
		return gt;
	}

	public static DocType getDocType(ResultSet rs, boolean withTitle,
			boolean withSQL) throws Exception {
		DocType result = new DocType();
		result.setId(rs.getInt("id"));
		result.setDoc_type_name_id(rs.getInt("doc_type_name_id"));
		getDocTypeGroup(rs, result, withTitle);
		result.setDoctypevalue(rs.getString("doctypevalue"));
		result.setTypelang(rs.getInt("typelang"));
		result.setRealdoctypeid(rs.getString("realdoctypeid"));
		if (withSQL) {
			result.setCust_selectfields(rs.getString("cust_selectfields"));
			result.setCust_sql(rs.getString("cust_sql"));
			result.setDoc_template(rs.getString("doc_template"));
			result.setApplied_customer(rs.getBoolean("applied_customer"));
		}
		return result;
	}

	public static DocumentLog getDocumentLog(ResultSet rs, DocumentLog doc,
			boolean withxml) throws Exception {
		if (doc == null)
			doc = new DocumentLog();
		DocumentLog result = doc;
		result.setDoc_date(getDateLong(rs, "doc_date"));
		result.setDoc_status_id(rs.getInt("doc_status_id"));
		result.setDocstatus(rs.getString("docstatus"));
		result.setUser_id(rs.getInt("user_id"));
		result.setUser_name(rs.getString("user_name"));
		result.setController_id(rs.getInt("controller_id"));
		result.setController_name(rs.getString("controller_name"));
		result.setDoc_date_start(getDateLong(rs, "doc_date_start"));
		result.setTransaction_date(getDateLong(rs, "transaction_date"));
		result.setDoc_date_status_change(getDateLong(rs,
				"doc_date_status_change"));
		result.setVersion_id(rs.getInt("version_id"));
		if (withxml)
			result.setXml(rs.getString("content_xml"));
		return doc;
	}

	public static DocumentShort getDocumentShort(ResultSet rs, DocumentShort doc)
			throws Exception {
		if (doc == null)
			doc = new DocumentShort();
		DocumentShort result = doc;
		getDocumentLog(rs, result, false);
		result.setId(rs.getInt("docid"));
		result.setDoc_type_id(rs.getInt("doc_type_id"));
		result.setCust_id(rs.getInt("cust_id"));
		result.setCancelary_nom(rs.getString("cancelary_nom"));
		result.setDelaystatus(rs.getInt("delaystatus"));
		return doc;
	}

	private static long getDateLong(ResultSet rs, String fieldName)
			throws Exception {
		Timestamp tm = rs.getTimestamp(fieldName);
		if (tm != null)
			return tm.getTime();
		return 0;
	}

	public static DocumentLong getDocumentLong(ResultSet rs) throws Exception {
		DocumentLong result = new DocumentLong();
		getDocumentShort(rs, result);
		result.setContent_xml(rs.getString("content_xml"));
		result.setApplied_status(rs.getInt("applied_status"));
		result.setError_status(rs.getInt("error_status"));
		return result;
	}

	public static CustomerShort getCustomerShort(ResultSet rs) throws Exception {
		CustomerShort result = new CustomerShort();
		result.setCusid(rs.getInt("cusid"));
		result.setCusname(rs.getString("cusname"));
		result.setRegion(rs.getString("region"));
		result.setRaion(rs.getString("raion"));
		result.setZone(rs.getLong("zone"));
		result.setCityname(rs.getString("cityname"));
		result.setStreetname(rs.getString("streetname"));
		result.setHome(rs.getString("home"));
		result.setFlat(rs.getString("flat"));
		result.setSubregionid(rs.getInt("subregionid"));
		result.setRegionid(rs.getInt("regionid"));
		result.setStreetid(rs.getInt("streetid"));
		result.setCityid(rs.getInt("cityid"));
		return result;
	}

}
