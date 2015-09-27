package com.docflow.shared.map;

import java.io.Serializable;

public class MakeDBProcess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7463336951019474985L;
	private String sessionID;
	private String[] operations;

	public String[] getOperations() {
		return operations;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setOperations(String[] operations) {
		this.operations = operations;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
