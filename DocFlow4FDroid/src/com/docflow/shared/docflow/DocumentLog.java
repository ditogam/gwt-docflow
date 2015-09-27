package com.docflow.shared.docflow;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentLog implements IsSerializable {

	/**
	 * 
	 */

	private long doc_date;
	private int doc_status_id;
	private String docstatus;
	private int user_id;
	private String user_name;
	private int controller_id;
	private String controller_name;
	private long doc_date_start;
	private long transaction_date;
	private long doc_date_status_change;
	private int version_id;

	private String xml;

	public int getController_id() {
		return controller_id;
	}

	public String getController_name() {
		if (controller_name == null)
			return "";
		return controller_name;
	}

	public long getDoc_date() {
		return doc_date;
	}

	public long getDoc_date_start() {
		return doc_date_start;
	}

	public long getDoc_date_status_change() {
		return doc_date_status_change;
	}

	public int getDoc_status_id() {
		return doc_status_id;
	}

	public String getDocstatus() {
		if (docstatus == null)
			return "";
		return docstatus;
	}

	public long getTransaction_date() {
		return transaction_date;
	}

	public int getUser_id() {
		return user_id;
	}

	public String getUser_name() {
		if (user_name == null)
			return "";
		return user_name;
	}

	public int getVersion_id() {
		return version_id;
	}

	public String getXml() {
		return xml;
	}

	public void setController_id(int controller_id) {
		this.controller_id = controller_id;
	}

	public void setController_name(String controller_name) {
		this.controller_name = controller_name;
	}

	public void setDoc_date(long doc_date) {
		this.doc_date = doc_date;
	}

	public void setDoc_date_start(long doc_date_start) {
		this.doc_date_start = doc_date_start;
	}

	public void setDoc_date_status_change(long doc_date_status_change) {
		this.doc_date_status_change = doc_date_status_change;
	}

	public void setDoc_status_id(int doc_status_id) {
		this.doc_status_id = doc_status_id;
	}

	public void setDocstatus(String docstatus) {

		this.docstatus = docstatus;
	}

	public void setTransaction_date(long transaction_date) {
		this.transaction_date = transaction_date;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public void setVersion_id(int version_id) {
		this.version_id = version_id;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}
