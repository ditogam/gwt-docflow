package com.socarmap.proxy.beans;

import java.io.Serializable;

public class ZXYData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7142192455733138501L;
	private int zoom;
	private int x;
	private int y;
	private String unique_id;
	private byte[] data;

	public ZXYData() {
	}

	public ZXYData(int zoom, int x, int y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	public byte[] getData() {
		return data;
	}

	public String getUnique_id() {
		return unique_id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZoom() {
		return zoom;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setUnique_id(String unique_id) {
		this.unique_id = unique_id;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	@Override
	public String toString() {
		return "zoom=" + zoom + " x=" + x + " y=" + y;
	}

}
