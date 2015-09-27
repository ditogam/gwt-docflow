package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Report implements IsSerializable {

	/**
	 * 
	 */
	public static final int RT_EXCELL = 1;
	public static final int RT_PDF = 2;
	public static final int RT_XML = 3;

	private ReportHeaderItem[] headers;
	private int type;
	private String[][] data;

	public String[][] getData() {
		return data;
	}

	public ReportHeaderItem[] getHeaders() {
		return headers;
	}

	public int getType() {
		return type;
	}

	public void setData(String[][] data) {
		this.data = data;
	}

	public void setHeaders(ReportHeaderItem[] headers) {
		this.headers = headers;
	}

	public void setType(int type) {
		this.type = type;
	}

}
