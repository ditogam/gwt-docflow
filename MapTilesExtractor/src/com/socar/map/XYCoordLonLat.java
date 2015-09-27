package com.socar.map;

import java.io.FileOutputStream;

public class XYCoordLonLat {
	private double radiant;

	public XYCoordLonLat(int zoom, int val, boolean x) {
		if (!x)
			val = (int) (Math.pow(2, zoom) - val - 1);
		double resolution = (2 * Math.PI * 6378137 / 256) / Math.pow(2, zoom);
		val *= 256;
		radiant = (val * resolution - 2 * Math.PI * 6378137 / 2.0);
	}

	public double getRadiant() {
		return radiant;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new XYCoordLonLat(8, 95, false).radiant);
//		FileOutputStream fos = new FileOutputStream("aa.sql");
//		new MyTest(8, 157, 161, true,fos);
//		new MyTest(9, 315, 322, true,fos);
//		new MyTest(10, 630, 644, true,fos);
//		new MyTest(11, 1260, 1289, true,fos);
//		new MyTest(12, 2520, 2579, true,fos);
//		new MyTest(13, 5041, 5159, true,fos);
//		new MyTest(14, 10082, 10319, true,fos);
//		new MyTest(15, 20164, 20638, true,fos);
//		new MyTest(16, 40329, 41277, true,fos);
//		new MyTest(17, 80659, 82554, true,fos);
//		new MyTest(18, 161318, 165108, true,fos);
//		new MyTest(19, 322637, 330217, true,fos);
//		new MyTest(20, 645275, 660435, true,fos);
//		new MyTest(8, 94, 95, false,fos);
//		new MyTest(9, 188, 191, false,fos);
//		new MyTest(10, 377, 383, false,fos);
//		new MyTest(11, 754, 767, false,fos);
//		new MyTest(12, 1508, 1534, false,fos);
//		new MyTest(13, 3017, 3069, false,fos);
//		new MyTest(14, 6034, 6139, false,fos);
//		new MyTest(15, 12069, 12279, false,fos);
//		new MyTest(16, 24138, 24558, false,fos);
//		new MyTest(17, 48277, 49117, false,fos);
//		new MyTest(18, 96555, 98234, false,fos);
//		new MyTest(19, 193111, 196469, false,fos);
//		new MyTest(20, 386223, 392938, false,fos);

	}
}
