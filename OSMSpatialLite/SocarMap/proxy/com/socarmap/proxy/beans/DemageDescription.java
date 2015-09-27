package com.socarmap.proxy.beans;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DemageDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9219029667157974053L;
	private Integer id;
	private int demage_type;
	private long time;
	private String description;
	private String images;
	private double px;
	private double py;
	private List<File> files;
	private ArrayList<byte[]> bytes;

	public DemageDescription() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<byte[]> getBytes() {
		return bytes;
	}

	public int getDemage_type() {
		return demage_type;
	}

	public String getDescription() {
		return description;
	}

	public List<File> getFiles() {
		return files;
	}

	public Integer getId() {
		return id;
	}

	public String getImages() {
		return images;
	}

	public double getPx() {
		return px;
	}

	public double getPy() {
		return py;
	}

	public long getTime() {
		return time;
	}

	public void setBytes(ArrayList<byte[]> bytes) {
		this.bytes = bytes;
	}

	public void setDemage_type(int demage_type) {
		this.demage_type = demage_type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public void setPx(double px) {
		this.px = px;
	}

	public void setPy(double py) {
		this.py = py;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
