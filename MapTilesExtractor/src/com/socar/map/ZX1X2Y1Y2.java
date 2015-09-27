package com.socar.map;

import java.util.ArrayList;

public class ZX1X2Y1Y2 {
	private int zoom;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private int subregion_id;

	public ZX1X2Y1Y2(int zoom, int x1, int y1, int x2, int y2, int subregion_id) {
		if(x1>x2)
		{
			int tmp=x1;
			x1=x2;
			x2=tmp;
		}
		if(y1>y2)
		{
			int tmp=y1;
			y1=y2;
			y2=tmp;
		}
		this.zoom = zoom;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.subregion_id = subregion_id;
	}

	public int getSubregion_id() {
		return subregion_id;
	}

	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getY1() {
		return y1;
	}

	public int getY2() {
		return y2;
	}

	public int getZoom() {
		return zoom;
	}

	public ArrayList<ZX1X2Y1Y2> createList(int batchSize) {
		ArrayList<ZX1X2Y1Y2> ret = new ArrayList<ZX1X2Y1Y2>();
		ZX1X2Y1Y2 add = new ZX1X2Y1Y2(zoom, x1, 0, x2, 0, subregion_id);
		if (x2 - x1 < batchSize) {
			ret.add(add);
			return ret;
		}
		int index = 0;
		for (int i = x1; i < x2 + 1; i++) {
			if (index == batchSize+1) {
				index = 0;
				ret.add(add);
				add = new ZX1X2Y1Y2(zoom, i, 0, i, 0, subregion_id);
				if (x2 - i < batchSize) {
					add.x2 = x2;
					ret.add(add);
					return ret;
				}
			}
			add.x2 = i;
			index++;
		}
		ret.add(add);
		return ret;
	}
	
	public String toStringX() {
		// TODO Auto-generated method stub
		return " insert into maps.subregionboundzx12 values (" + subregion_id
				+ "," + zoom + "," + x1 + "," + x2 +");";
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " insert into maps.subregionboundzxy12 values (" + subregion_id
				+ "," + zoom + "," + x1 + "," + x2 + "," + y1 + "," + y2 + ");";
	}
}
