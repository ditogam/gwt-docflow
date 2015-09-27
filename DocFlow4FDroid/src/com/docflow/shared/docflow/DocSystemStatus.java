package com.docflow.shared.docflow;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocSystemStatus implements IsSerializable {

	/**
	 * 
	 */

	private long id;
	private long doc_status_name_id;
	private int statuslang;
	private String docstatuscaptionvalue;
	private boolean hidden;
	private int system_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDoc_status_name_id() {
		return doc_status_name_id;
	}

	public void setDoc_status_name_id(long doc_status_name_id) {
		this.doc_status_name_id = doc_status_name_id;
	}

	public int getStatuslang() {
		return statuslang;
	}

	public void setStatuslang(int statuslang) {
		this.statuslang = statuslang;
	}

	public String getDocstatuscaptionvalue() {
		return docstatuscaptionvalue;
	}

	public void setDocstatuscaptionvalue(String docstatuscaptionvalue) {
		this.docstatuscaptionvalue = docstatuscaptionvalue;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getSystem_id() {
		return system_id;
	}

	public void setSystem_id(int system_id) {
		this.system_id = system_id;
	}

}
