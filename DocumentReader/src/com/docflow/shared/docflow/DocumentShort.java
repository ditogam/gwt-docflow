package com.docflow.shared.docflow;

import java.io.Serializable;

public class DocumentShort extends DocumentLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 555152723963774234L;

	private int id;
	private int doc_type_id;
	private int cust_id;
	private String cancelary_nom;
	private int delaystatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDoc_type_id() {
		return doc_type_id;
	}

	public void setDoc_type_id(int doc_type_id) {
		this.doc_type_id = doc_type_id;
	}

	public int getCust_id() {
		return cust_id;
	}

	public void setCust_id(int cust_id) {
		this.cust_id = cust_id;
	}

	public String getCancelary_nom() {
		if (cancelary_nom == null)
			return "";
		return cancelary_nom;
	}

	public void setCancelary_nom(String cancelary_nom) {
		this.cancelary_nom = cancelary_nom;
	}

	public void setDelaystatus(int delaystatus) {
		this.delaystatus = delaystatus;
	}

	public int getDelaystatus() {
		return delaystatus;
	}

}
