package com.docflow.shared.docflow;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocType extends DocTypeGroup implements IsSerializable {
	public static final String JAVASCRIPT_ELEMENT = "JAVASCRIPT_ELEMENT";

	/**
	 * 
	 */

	private int id;
	private int doc_type_name_id;
	private int typelang;
	private String doctypevalue;
	private String doc_template;
	private String cust_sql;
	private String cust_selectfields;
	private boolean applied_customer;
	private String realdoctypeid;
	private String datefield;
	private String delayinterval;

	public String getCust_selectfields() {
		return cust_selectfields;
	}

	public String getCust_sql() {
		return cust_sql;
	}

	public String getDatefield() {
		return datefield;
	}

	public String getDelayinterval() {
		return delayinterval;
	}

	public String getDoc_template() {
		return doc_template;
	}

	public int getDoc_type_name_id() {
		return doc_type_name_id;
	}

	public String getDoctypevalue() {
		return doctypevalue;
	}

	public int getId() {
		return id;
	}

	public String getRealdoctypeid() {
		return realdoctypeid;
	}

	public int getTypelang() {
		return typelang;
	}

	public boolean isApplied_customer() {
		return applied_customer;
	}

	public void setApplied_customer(boolean applied_customer) {
		this.applied_customer = applied_customer;
	}

	public void setCust_selectfields(String cust_selectfields) {
		this.cust_selectfields = cust_selectfields;
	}

	public void setCust_sql(String cust_sql) {
		this.cust_sql = cust_sql;
	}

	public void setDatefield(String datefield) {
		this.datefield = datefield;
	}

	public void setDelayinterval(String delayinterval) {
		this.delayinterval = delayinterval;
	}

	public void setDoc_template(String doc_template) {
		this.doc_template = doc_template;
	}

	public void setDoc_type_name_id(int doc_type_name_id) {
		this.doc_type_name_id = doc_type_name_id;
	}

	public void setDoctypevalue(String doctypevalue) {
		this.doctypevalue = doctypevalue;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRealdoctypeid(String realdoctypeid) {
		this.realdoctypeid = realdoctypeid;
	}

	public void setTypelang(int typelang) {
		this.typelang = typelang;
	}

	public boolean isJSType() {
		try {
			return getCust_sql().trim().toLowerCase().equals(JAVASCRIPT_ELEMENT.toLowerCase());
		} catch (Exception e) {
			return false;
		}
	}
}
