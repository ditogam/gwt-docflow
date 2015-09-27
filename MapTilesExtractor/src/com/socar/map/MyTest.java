package com.socar.map;

import java.io.FileOutputStream;
import java.text.DecimalFormat;

public class MyTest {
	private static DecimalFormat df2 = new DecimalFormat(
			"#######################0.00000000");

	public MyTest(int zoom, int val1, int val2, boolean x, FileOutputStream fos)
			throws Exception {
		val2 += 2;
		for (int i = val1; i < val2; i++) {
			String v = "insert into maps.coords(" + zoom + "," + i + ","
					+ (x ? 1 : 0) + ","
					+ df2.format(new XYCoordLonLat(zoom, i, x).getRadiant())
					+ ");\n";
			System.out.println(v.trim());
			fos.write(v.getBytes());
		}
	}
}
