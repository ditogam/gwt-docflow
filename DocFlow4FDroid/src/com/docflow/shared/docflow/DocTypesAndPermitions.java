package com.docflow.shared.docflow;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocTypesAndPermitions implements IsSerializable {
	private ArrayList<DocType> docTypes;
	private ArrayList<Integer> restrictions;

	public DocTypesAndPermitions() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<DocType> getDocTypes() {
		return docTypes;
	}

	public void setDocTypes(ArrayList<DocType> docTypes) {
		this.docTypes = docTypes;
	}

	public ArrayList<Integer> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(ArrayList<Integer> restrictions) {
		this.restrictions = restrictions;
	}

}
