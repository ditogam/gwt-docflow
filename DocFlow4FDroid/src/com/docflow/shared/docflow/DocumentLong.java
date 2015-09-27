package com.docflow.shared.docflow;

import java.util.ArrayList;

import com.docflow.shared.CustomerShort;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentLong extends DocumentShort implements IsSerializable {

	/**
	 * 
	 */
	private String content_xml;
	private String replic;
	private ArrayList<DocumentLog> history;
	private CustomerShort customerShort;

	public String getContent_xml() {
		return content_xml;
	}

	public CustomerShort getCustomerShort() {
		return customerShort;
	}

	public ArrayList<DocumentLog> getHistory() {
		return history;
	}

	public String getReplic() {
		return replic;
	}

	public void setContent_xml(String content_xml) {
		this.content_xml = content_xml;
	}

	public void setCustomerShort(CustomerShort customerShort) {
		this.customerShort = customerShort;
	}

	public void setHistory(ArrayList<DocumentLog> history) {
		this.history = history;
	}

	public void setReplic(String replic) {
		this.replic = replic;
	}

}
