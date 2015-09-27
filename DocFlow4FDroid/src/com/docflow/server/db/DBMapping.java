package com.docflow.server.db;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.DocStatusCount;
import com.docflow.shared.Meter;
import com.docflow.shared.MeterPlombs;
import com.docflow.shared.User_Data;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.PermitionItem;
import com.docflow.shared.common.User_Group;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeGroup;
import com.docflow.shared.docflow.DocumentLog;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflow.shared.hr.Captions;
import com.docflow.shared.hr.Item_type;
import com.docflow.shared.hr.Job_position;
import com.docflow.shared.hr.Person;
import com.docflow.shared.hr.Person_education;
import com.docflow.shared.hr.Responcibilities;
import com.docflow.shared.hr.Responcibility_types;
import com.docflow.shared.hr.Salary;
import com.docflow.shared.hr.Salary_type;
import com.docflow.shared.hr.Structure_item;

public class DBMapping {

	public static CustomerShort getCustomerShort(ResultSet rs) throws Exception {
		CustomerShort result = new CustomerShort();
		result.setCusid(rs.getInt("cusid"));
		result.setCusname(rs.getString("cusname"));
		result.setRegion(rs.getString("region"));
		result.setRaion(rs.getString("raion"));
		result.setZone(rs.getLong("zone"));
		result.setCityname(rs.getString("cityname"));
		result.setStreetname(rs.getString("streetname"));

		result.setFlat(rs.getString("flat"));

		result.setSubregionid(rs.getInt("subregionid"));
		result.setRegionid(rs.getInt("regionid"));

		result.setStreetid(rs.getInt("streetid"));
		result.setCityid(rs.getInt("cityid"));
		try {
			result.setPhone(rs.getString("phone"));
		} catch (Exception e) {
		}

		try {
			result.setHome(rs.getString("home"));
		} catch (Exception e) {
		}

		try {
			result.setLoan(rs.getString("loan"));
		} catch (Exception e) {
		}
		try {
			result.setScopename(rs.getString("scopename"));
		} catch (Exception e) {
		}

		return result;
	}

	public static User_Data getUserData(ResultSet rs) throws Exception {
		User_Data result = new User_Data();
		result.setUser_id(rs.getInt("user_id"));
		result.setPborder(rs.getString("pborder"));
		result.setPheight(rs.getString("pheight"));
		result.setPwith(rs.getString("pwith"));
		result.setShowtimeout(rs.getInt("showtimeout"));
		result.setHtml(rs.getString("html"));
		return result;
	}

	private static long getDateLong(ResultSet rs, String fieldName)
			throws Exception {
		Timestamp tm = rs.getTimestamp(fieldName);
		if (tm != null)
			return tm.getTime();
		return 0;
	}

	public static DocStatusCount getDocStatusCount(ResultSet rs)
			throws Exception {
		DocStatusCount result = new DocStatusCount();
		result.setCountofdocs(rs.getInt("countofdocs"));
		result.setDoc_status_id(rs.getInt("doc_status_id"));
		result.setSystem_id(rs.getInt("system_id"));
		result.setDocstatuscaptionvalue(rs.getString("docstatuscaptionvalue"));
		result.setDoctypegroupvalue(rs.getString("doctypegroupvalue"));
		result.setGroup_id(rs.getInt("group_id"));
		return result;
	}

	public static DocType getDocType(ResultSet rs) throws Exception {
		return getDocType(rs, true, true);
	}

	public static DocType getDocType(ResultSet rs, boolean withTitle,
			boolean withSQL) throws Exception {
		DocType result = new DocType();
		result.setId(rs.getInt("id"));
		result.setDoc_type_name_id(rs.getInt("doc_type_name_id"));
		getDocTypeGroup(rs, result, withTitle);
		result.setDoctypevalue(rs.getString("doctypevalue"));
		result.setTypelang(rs.getInt("typelang"));
		try {
			result.setCust_sql(rs.getString("cust_sql"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (withSQL) {
			result.setCust_selectfields(rs.getString("cust_selectfields"));
			
			result.setDoc_template(rs.getString("doc_template"));
			result.setApplied_customer(rs.getBoolean("applied_customer"));
			try {
				result.setRealdoctypeid(rs.getString("realdoctypeid"));
			} catch (Exception e) {
			}
			result.setDatefield(rs.getString("datefield"));
			result.setDelayinterval(rs.getString("delayinterval"));
		}
		return result;
	}

	public static DocTypeGroup getDocTypeGroup(ResultSet rs, DocTypeGroup gt,
			boolean withTitle) throws Exception {
		if (gt == null)
			gt = new DocTypeGroup();
		gt.setGroup_id(rs.getInt("group_id"));
		try {
			gt.setSystem_id(rs.getInt("system_id"));
		} catch (Exception e) {
		}
		if (withTitle)
			gt.setDoctypegroupvalue(rs.getString("doctypegroupvalue"));
		return gt;
	}

	public static DocumentFile getDocumentFile(ResultSet rs) throws Exception {
		DocumentFile result = new DocumentFile();
		result.setId(rs.getInt("id"));
		result.setDocument_id(rs.getInt("document_id"));
		result.setImage_id(rs.getInt("image_id"));
		result.setFilename(rs.getString("filename"));
		return result;
	}

	public static DocumentLog getDocumentLog(ResultSet rs, DocumentLog doc,
			boolean withxml) throws Exception {

		return getDocumentLog(rs, doc, withxml, false);
	}

	public static DocumentLog getDocumentLog(ResultSet rs, DocumentLog doc,
			boolean withxml, boolean very_short) throws Exception {
		if (doc == null)
			doc = new DocumentLog();
		DocumentLog result = doc;
		if (!very_short)
			result.setDoc_date(getDateLong(rs, "doc_date"));
		if (!very_short)
			result.setDoc_status_id(rs.getInt("doc_status_id"));
		result.setDocstatus(rs.getString("docstatus"));
		if (!very_short)
			result.setUser_id(rs.getInt("user_id"));
		result.setUser_name(rs.getString("user_name"));
		if (!very_short)
			result.setController_id(rs.getInt("controller_id"));
		result.setController_name(rs.getString("controller_name"));
		if (!very_short)
			result.setDoc_date_start(getDateLong(rs, "doc_date_start"));
		result.setTransaction_date(getDateLong(rs, "transaction_date"));
		if (!very_short)
			result.setDoc_date_status_change(getDateLong(rs,
					"doc_date_status_change"));
		result.setVersion_id(rs.getInt("version_id"));
		if (withxml && !very_short)
			result.setXml(rs.getString("content_xml"));
		return doc;
	}

	public static DocumentLong getDocumentLong(ResultSet rs) throws Exception {
		DocumentLong result = new DocumentLong();
		getDocumentShort(rs, result);
		result.setContent_xml(rs.getString("content_xml"));
		result.setReplic(rs.getString("replic"));
		return result;
	}

	public static DocumentShort getDocumentShort(ResultSet rs, DocumentShort doc)
			throws Exception {

		return getDocumentShort(rs, doc, false, false);
	}

	public static DocumentShort getDocumentShort(ResultSet rs,
			DocumentShort doc, boolean very_short, boolean doctempxml)
			throws Exception {
		if (doc == null)
			doc = new DocumentShort();
		DocumentShort result = doc;
		getDocumentLog(rs, result, false);
		result.setId(rs.getInt("docid"));
		if (!very_short)
			result.setDoc_flow_num(rs.getString("doc_flow_num"));
		if (!very_short)
			result.setDoc_type_id(rs.getInt("doc_type_id"));
		result.setDoctype(rs.getString("doctype"));
		result.setCust_id(rs.getInt("cust_id"));
		if (!very_short)
			result.setStreet_id(rs.getInt("street_id"));
		if (!very_short)
			result.setStreenname(rs.getString("streenname"));
		if (!very_short)
			result.setCityid(rs.getInt("cityid"));
		if (!very_short)
			result.setCityname(rs.getString("cityname"));
		if (!very_short)
			result.setSubregionid(rs.getInt("subregionid"));
		result.setSubregionname(rs.getString("subregionname"));
		result.setRegionname(rs.getString("regionname"));
		if (!very_short)
			result.setRegionid(rs.getInt("regionid"));
		result.setCzona(rs.getLong("czona"));
		if (!very_short)
			result.setCancelary_nom(rs.getString("cancelary_nom"));
		if (!very_short)
			result.setShxml(rs.getString("shxml"));
		result.setCustomer_name(rs.getString("customer_name"));
		if (!very_short)
			result.setError(rs.getString("error"));
		result.setDelaystatus(rs.getInt("delaystatus"));
		result.setTdocdelaystatus(rs.getString("tdocdelaystatus"));
		try {
			result.setFilecount(rs.getInt("filecount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			result.setThread_id(rs.getInt("thread_id"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (doctempxml)
			try {
				result.setDoc_template(rs.getString("doc_template"));
			} catch (Exception e) {
				// TODO: handle exception
			}

		return doc;
	}

	public static Meter getMetter(ResultSet rs) throws Exception {
		Meter result = new Meter();
		result.setCusid(rs.getInt("cusid"));
		result.setMeterid(rs.getLong("meterid"));
		result.setMetserial(rs.getString("metserial"));
		result.setMettervalue(rs.getDouble("mettervalue"));
		result.setMstatusid(rs.getShort("mstatusid"));
		result.setMtypeid(rs.getInt("mtypeid"));
		try {
			result.setRegdate(rs.getTimestamp("regdate").getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		result.setStart_index(rs.getLong("start_index"));
		return result;
	}

	public static MeterPlombs getMetterPlombs(ResultSet rs) throws Exception {
		MeterPlombs result = new MeterPlombs();
		result.setPlombid(rs.getInt("plombid"));
		result.setPlombname(rs.getString("plombname"));
		try {
			result.setStartdate(rs.getTimestamp("startdate").getTime());
		} catch (Exception e) {
			// TODO: handle exception
		}
		result.setStatus(rs.getString("status"));
		result.setPlace(rs.getString("place"));
		return result;
	}

	public static PermitionItem getPermitionItem(ResultSet rs) throws Exception {
		PermitionItem result = new PermitionItem();
		result.setPermition_id(rs.getInt("permition_id"));
		result.setPermitionName(rs.getString("permitionName"));
		result.setApplyed(rs.getBoolean("applyed"));
		result.setDenied(rs.getBoolean("denied"));
		return result;
	}

	public static User_Group getUser_Group(ResultSet rs) throws Exception {
		User_Group result = new User_Group();
		result.setGroup_id(rs.getInt("group_id"));
		result.setGname(rs.getString("gname"));
		result.setApplyed(rs.getBoolean("applyed"));
		return result;
	}

	public static Item_type getItem_type(ResultSet rs) throws Exception {
		Item_type result = new Item_type();
		result.setItem_type_id(rs.getInt("item_type_id"));
		result.setItem_type_name(rs.getString("item_type_name"));
		return result;
	}

	public static Job_position getJob_position(ResultSet rs) throws Exception {
		Job_position result = new Job_position();
		result.setId(rs.getInt("id"));
		result.setPosition_name(rs.getString("position_name"));
		result.setStructure_id(rs.getInt("structure_id"));
		result.setPosition_description(rs.getString("position_description"));
		result.setPerson_id(rs.getInt("person_id"));
		return result;
	}

	public static Person_education getPerson_education(ResultSet rs)
			throws Exception {
		Person_education result = new Person_education();
		result.setPerson_id(rs.getInt("person_id"));
		result.setEducation_name_and_place(rs
				.getString("education_name_and_place"));
		result.setFaculty(rs.getString("faculty"));
		result.setEnter_year(rs.getInt("enter_year"));
		result.setGreduate_year(rs.getInt("greduate_year"));
		result.setLeave_grade(rs.getInt("leave_grade"));
		result.setGreduate_degree_certificate_num(rs
				.getString("greduate_degree_certificate_num"));
		result.setId(rs.getInt("id"));
		return result;
	}

	public static Person getPerson(ResultSet rs) throws Exception {
		Person result = new Person();
		result.setPerson_id(rs.getInt("person_id"));
		result.setPerson_last_name(rs.getString("person_last_name"));
		result.setPerson_first_name(rs.getString("person_first_name"));
		result.setPerson_picture_filename(rs
				.getString("person_picture_filename"));
		result.setPerson_picture_filesize(rs.getInt("person_picture_filesize"));
		result.setPerson_picture_date_created(rs
				.getDate("person_picture_date_created"));
		result.setPerson_middle_name(rs.getString("person_middle_name"));
		result.setPerson_birth_date(rs.getTimestamp("person_birth_date"));
		result.setPerson_identity_no(rs.getString("person_identity_no"));
		result.setPerson_address(rs.getString("person_address"));
		result.setPerson_family(rs.getString("person_family"));
		result.setPerson_nationality_other(rs
				.getString("person_nationality_other"));
		result.setPerson_sex(rs.getInt("person_sex"));
		result.setPerson_nationality(rs.getInt("person_nationality"));
		result.setPerson_merige_statuse(rs.getInt("person_merige_statuse"));
		return result;
	}

	public static Responcibilities getResponcibilities(ResultSet rs)
			throws Exception {
		Responcibilities result = new Responcibilities();
		result.setId(rs.getInt("id"));
		result.setItem_id(rs.getInt("item_id"));
		result.setItem_type_id(rs.getShort("item_type_id"));
		result.setResp_type_id(rs.getInt("resp_type_id"));
		result.setDescription(rs.getString("description"));
		return result;
	}

	public static Responcibility_types getResponcibility_types(ResultSet rs)
			throws Exception {
		Responcibility_types result = new Responcibility_types();
		result.setId(rs.getInt("id"));
		result.setResp_type_name(rs.getString("resp_type_name"));
		return result;
	}

	public static Salary_type getSalary_type(ResultSet rs) throws Exception {
		Salary_type result = new Salary_type();
		result.setId(rs.getInt("id"));
		result.setDescription(rs.getString("description"));
		return result;
	}

	public static Salary getSalary(ResultSet rs) throws Exception {
		Salary result = new Salary();
		result.setPosition_id(rs.getInt("position_id"));
		result.setSalary_type_id(rs.getInt("salary_type_id"));
		result.setSalary_value(rs.getDouble("salary_value"));
		result.setDescription(rs.getString("description"));
		return result;
	}

	public static Structure_item getStructure_item(ResultSet rs)
			throws Exception {
		Structure_item result = new Structure_item();
		result.setItem_id(rs.getInt("item_id"));
		result.setItem_parent_id(rs.getInt("item_parent_id"));
		result.setItem_type_id(rs.getInt("item_type_id"));
		result.setItem_class_id(rs.getInt("item_class_id"));
		result.setItem_scope_id(rs.getInt("item_scope_id"));
		result.setItem_name(rs.getString("item_name"));
		result.setOpened(rs.getBoolean("opened"));
		result.setObject_id(rs.getInt("object_id"));
		return result;
	}

	public static Captions getCaptions(ResultSet rs) throws Exception {
		Captions result = new Captions();
		result.setId(rs.getLong("id"));
		result.setLanguage_id(rs.getInt("language_id"));
		result.setCvalue(rs.getString("cvalue"));
		return result;
	}

	public static ClSelection getClSelection(ResultSet rs) throws Exception {
		ClSelection result = new ClSelection(rs.getInt("type"),
				rs.getString("sql"), rs.getInt("parenttype"),
				rs.getString("dbname"));

		return result;
	}

}
