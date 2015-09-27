package com.socarmap.utils;

import java.io.File;

public class ZX1X2 {
	private int zoom;
	private int x1;
	private int x2;
	private File file;

	public ZX1X2(int zoom, int x1, int x2, File file) {
		this.zoom = zoom;
		this.x1 = x1;
		this.x2 = x2;
		this.file = file;

	}

	public boolean acept(int zoom, int x) {
		return this.zoom == zoom && x >= x1 && x <= x2;
	}

	public File getFile() {
		return file;
	}

	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getZoom() {
		return zoom;
	}

}
