package com.docflow.shared.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DocTypeMapping implements IsSerializable {

	/**
	 * 
	 */

	private DocType docType;
	private DocumentLong document;
	private HashMap<String, String> values;
	private HashMap<String, String> displayValues;
	private CustomerShort customerShort;
	private HashMap<String, ArrayList<ClSelectionItem>> selections;
	private ArrayList<DocumentFile> files;

	public CustomerShort getCustomerShort() {
		return customerShort;
	}

	public DocType getDocType() {
		return docType;
	}

	public DocumentLong getDocument() {
		return document;
	}

	public HashMap<String, String> getValues() {
		return values;
	}

	public void setCustomerShort(CustomerShort customerShort) {
		this.customerShort = customerShort;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public void setDocument(DocumentLong document) {
		this.document = document;
	}

	public void setValues(HashMap<String, String> values) {
		this.values = values;
	}

	public HashMap<String, ArrayList<ClSelectionItem>> getSelections() {
		return selections;
	}

	public void setSelections(
			HashMap<String, ArrayList<ClSelectionItem>> selections) {
		this.selections = selections;
	}

	public ArrayList<DocumentFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<DocumentFile> files) {
		this.files = files;
	}

	public HashMap<String, String> getDisplayValues() {
		return displayValues;
	}

	public void setDisplayValues(HashMap<String, String> displayValues) {
		this.displayValues = displayValues;
	}

}
