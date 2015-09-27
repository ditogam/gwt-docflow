package com.docflow.server;

import java.util.ArrayList;

import com.docflow.shared.DocStatusCount;

public class DocStatusCounts {
	public long creatitonTime = System.currentTimeMillis() + 10000;
	private ArrayList<DocStatusCount> docStatusCounts = null;

	public ArrayList<DocStatusCount> getDocStatusCounts() {
		return docStatusCounts;
	}

	public void setDocStatusCounts(ArrayList<DocStatusCount> docStatusCounts) {
		this.docStatusCounts = docStatusCounts;
	}
}
