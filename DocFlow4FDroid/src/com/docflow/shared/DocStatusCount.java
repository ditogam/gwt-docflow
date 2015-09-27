package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocStatusCount implements IsSerializable {

	/**
	 * 
	 */

	private int doc_status_id;
	private String docstatuscaptionvalue;
	private int group_id;
	private String doctypegroupvalue;
	private int countofdocs;
	private int system_id;

	public int getCountofdocs() {
		return countofdocs;
	}

	public int getDoc_status_id() {
		return doc_status_id;
	}

	public String getDocstatuscaptionvalue() {
		return docstatuscaptionvalue;
	}

	public String getDoctypegroupvalue() {
		return doctypegroupvalue;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setCountofdocs(int countofdocs) {
		this.countofdocs = countofdocs;
	}

	public void setDoc_status_id(int doc_status_id) {
		this.doc_status_id = doc_status_id;
	}

	public void setDocstatuscaptionvalue(String docstatuscaptionvalue) {
		this.docstatuscaptionvalue = docstatuscaptionvalue;
	}

	public void setDoctypegroupvalue(String doctypegroupvalue) {
		this.doctypegroupvalue = doctypegroupvalue;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getSystem_id() {
		return system_id;
	}

	public void setSystem_id(int system_id) {
		this.system_id = system_id;
	}

}
