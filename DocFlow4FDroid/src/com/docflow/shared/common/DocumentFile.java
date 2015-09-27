package com.docflow.shared.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentFile implements IsSerializable {

	/**
	 * 
	 */

	private int id;
	private int document_id;
	private int image_id;
	private String filename;
	private transient String uId;

	public int getDocument_id() {
		return document_id;
	}

	public String getFilename() {
		return filename;
	}

	public int getId() {
		return id;
	}

	public int getImage_id() {
		return image_id;
	}

	public String getuId() {
		return uId;
	}

	public void setDocument_id(int document_id) {
		this.document_id = document_id;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImage_id(int image_id) {
		this.image_id = image_id;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

}
