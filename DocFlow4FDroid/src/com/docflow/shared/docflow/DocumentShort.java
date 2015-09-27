package com.docflow.shared.docflow;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentShort extends DocumentLog implements IsSerializable {
	/**
	 * 
	 */

	private int id;
	private int doc_type_id;
	private String doctype;
	private String doc_flow_num;
	private int cust_id;
	private int street_id;
	private String streenname;
	private int cityid;
	private String cityname;
	private int subregionid;
	private String subregionname;
	private int regionid;
	private String regionname;
	private long czona;
	private String cancelary_nom;
	private String shxml;
	private String customer_name;
	private String error;
	private int filecount;
	private int delaystatus;
	private String tdocdelaystatus;
	private int thread_id;
	private String doc_template;

	public String getCancelary_nom() {
		if (cancelary_nom == null)
			return "";
		return cancelary_nom;
	}

	public int getCityid() {
		return cityid;
	}

	public String getCityname() {
		if (cityname == null)
			return "";
		return cityname;
	}

	public int getCust_id() {
		return cust_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public long getCzona() {
		return czona;
	}

	public int getDelaystatus() {
		return delaystatus;
	}

	public String getDoc_flow_num() {
		if (doc_flow_num == null)
			return "";
		return doc_flow_num;
	}

	public int getDoc_type_id() {
		return doc_type_id;
	}

	public String getDoctype() {
		if (doctype == null)
			return "";
		return doctype;
	}

	public String getError() {
		return error;
	}

	public int getFilecount() {
		return filecount;
	}

	public int getId() {
		return id;
	}

	public int getRegionid() {
		return regionid;
	}

	public String getRegionname() {
		if (regionname == null)
			return "";
		return regionname;
	}

	public String getShxml() {
		return shxml;
	}

	public String getStreenname() {
		if (streenname == null)
			return "";
		return streenname;
	}

	public int getStreet_id() {
		return street_id;
	}

	public int getSubregionid() {
		return subregionid;
	}

	public String getSubregionname() {
		if (subregionname == null)
			return "";
		return subregionname;
	}

	public String getTdocdelaystatus() {
		return tdocdelaystatus;
	}

	public void setCancelary_nom(String cancelary_nom) {
		this.cancelary_nom = cancelary_nom;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public void setCust_id(int cust_id) {
		this.cust_id = cust_id;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public void setCzona(long czona) {
		this.czona = czona;
	}

	public void setDelaystatus(int delaystatus) {
		this.delaystatus = delaystatus;
	}

	public void setDoc_flow_num(String doc_flow_num) {
		this.doc_flow_num = doc_flow_num;
	}

	public void setDoc_type_id(int doc_type_id) {
		this.doc_type_id = doc_type_id;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setFilecount(int filecount) {
		this.filecount = filecount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRegionid(int regionid) {
		this.regionid = regionid;
	}

	public void setRegionname(String regionname) {
		this.regionname = regionname;
	}

	public void setShxml(String shxml) {
		this.shxml = shxml;
	}

	public void setStreenname(String streenname) {
		this.streenname = streenname;
	}

	public void setStreet_id(int street_id) {
		this.street_id = street_id;
	}

	public void setSubregionid(int subregionid) {
		this.subregionid = subregionid;
	}

	public void setSubregionname(String subregionname) {
		this.subregionname = subregionname;
	}

	public void setTdocdelaystatus(String tdocdelaystatus) {
		this.tdocdelaystatus = tdocdelaystatus;
	}

	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	public String getDoc_template() {
		return doc_template;
	}

	public void setDoc_template(String doc_template) {
		this.doc_template = doc_template;
	}

}
