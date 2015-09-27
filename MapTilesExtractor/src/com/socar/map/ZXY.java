package com.socar.map;

public class ZXY {
	private int zoom;
	private int x;
	private int y;

	public ZXY(int zoom, int x, int y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	public static String createUnique(int zoom, int x, int y) {
		return zoom + "_" + x + "_" + y;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return zoom + "_" + x + "_" + y;
	}

	public int getZoom() {
		return zoom;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
