package com.socarmap.proxy.beans.accident;

import java.io.Serializable;

public class Step extends Accident_Default implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 122743408181679774L;

	private int case_id;

	public Step() {
	}

	public int getCase_id() {
		return case_id;
	}

	public void setCase_id(int case_id) {
		this.case_id = case_id;
	}

}
