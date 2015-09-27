package com.docflow.shared;

public interface MDBConnectionCommon {
	public final static String DOCUMENTLOG_FIELDS = "d.doc_date, d.doc_status_id, d.docstatus,d.user_id,d.user_name,d.content_xml as shxml, d.customer_name, d.error,d.delaystatus,d.tdocdelaystatus,"
			+ "d.controller_id,d.controller_name,d.doc_date_start,d.transaction_date,d.doc_date_status_change,d.version_id,d.content_xml,d.filecount";
	public final static String DOCUMENTSHORT_FIELDS = "d.id docid, d.doc_type_id,d.doctype,d.doc_flow_num,"
			+ DOCUMENTLOG_FIELDS
			+ ",d.cust_id, d.street_id, d.streenname, d.cityid, d.cityname, d.subregionid, d.subregionname, d.regionid, d.regionname, d.czona"
			+ ",cancelary_nom,thread_id ";
	public final static String DOCUMENTSHORT_WITH_DOC_TYPE_FIELDS = DOCUMENTSHORT_FIELDS
			+ ",doc_template";
	public final static String DOCUMENTLONG_FIELDS = DOCUMENTSHORT_FIELDS
			+ ",d.content_xml, d.replic";
}
