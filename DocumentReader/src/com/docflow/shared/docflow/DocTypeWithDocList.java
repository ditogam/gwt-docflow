package com.docflow.shared.docflow;

import java.io.Serializable;
import java.util.ArrayList;

public class DocTypeWithDocList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5580314613980919770L;
	private DocType docType;
	private ArrayList<DocumentShort> docList;

	public DocType getDocType() {
		return docType;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public ArrayList<DocumentShort> getDocList() {
		return docList;
	}

	public void setDocList(ArrayList<DocumentShort> docList) {
		this.docList = docList;
	}

}
