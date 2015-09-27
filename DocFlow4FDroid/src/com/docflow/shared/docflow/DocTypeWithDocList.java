package com.docflow.shared.docflow;

import java.util.ArrayList;

import com.docflow.shared.ListSizes;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DocTypeWithDocList extends ListSizes implements IsSerializable {

	/**
	 * 
	 */

	private DocType docType;
	private ArrayList<DocumentShort> docList;

	public ArrayList<DocumentShort> getDocList() {
		return docList;
	}

	public DocType getDocType() {
		return docType;
	}

	public void setDocList(ArrayList<DocumentShort> docList) {
		this.docList = docList;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

}
