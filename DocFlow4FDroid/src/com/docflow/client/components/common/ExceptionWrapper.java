package com.docflow.client.components.common;

import java.io.Serializable;

public class ExceptionWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7099230072365388266L;
	private String message;
	private String detail;
	private String trace;

	public ExceptionWrapper() {
		// TODO Auto-generated constructor stub
	}

	public ExceptionWrapper(String message, String detail, String trace) {
		super();
		this.message = message;
		this.detail = detail;
		this.trace = trace;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

}
