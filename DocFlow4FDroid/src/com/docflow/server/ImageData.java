package com.docflow.server;

import java.io.InputStream;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ImageData implements IsSerializable {

	/**
	 * 
	 */
	private int id;
	private byte[] imgData;
	private InputStream imageInputStream;
	private String imagename;
	private String contenttype;

	public String getContenttype() {
		return contenttype;
	}

	public int getId() {
		return id;
	}

	public InputStream getImageInputStream() {
		return imageInputStream;
	}

	public byte[] getImgData() {
		return imgData;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImageInputStream(InputStream imageInputStream) {
		this.imageInputStream = imageInputStream;
	}

	public void setImgData(byte[] imgData) {
		this.imgData = imgData;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

}
