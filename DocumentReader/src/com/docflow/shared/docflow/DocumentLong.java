package com.docflow.shared.docflow;

import java.io.Serializable;

public class DocumentLong extends DocumentShort implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5210934197772120834L;
	private String content_xml;
	private int applied_status;
	private int error_status;

	public String getContent_xml() {
		return content_xml;
	}

	public void setContent_xml(String content_xml) {
		this.content_xml = content_xml;
	}

	public int getApplied_status() {
		return applied_status;
	}

	public void setApplied_status(int applied_status) {
		this.applied_status = applied_status;
	}

	public int getError_status() {
		return error_status;
	}

	public void setError_status(int error_status) {
		this.error_status = error_status;
	}

}
