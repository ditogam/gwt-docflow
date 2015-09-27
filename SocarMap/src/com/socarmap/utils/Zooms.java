package com.socarmap.utils;

import java.util.ArrayList;

public class Zooms {
	private int zoom;
	private ArrayList<ZX1X2> zx1x2s;

	public Zooms(int zoom) {
		this.setZoom(zoom);
		zx1x2s = new ArrayList<ZX1X2>();
	}

	public void addXs(ZX1X2 zx1x2) {
		zx1x2s.add(zx1x2);
	}

	public int getZoom() {
		return zoom;
	}

	public ArrayList<ZX1X2> getZx1x2s() {
		return zx1x2s;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

}
