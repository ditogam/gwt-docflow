package com.socargass.tabletexporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import com.socarmap.server.DBOperations;

public class GetMap {
	private static final String WMS_URL = "http://localhost:8787/SocarMap/wms?format=image/png&palette=socarall&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:900913&width=256&height=256&layers=SocarAll&map=&styles=";
	public static String USER_AGENT = "OsmAnd~";
	private static final int BUFFER_SIZE = 1024;

	public static void getmap(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		String zoom = request.getParameter("zoom");
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		OutputStream outputStream = response.getOutputStream();
		response.setContentType("image/png");
		if (DBOperations.getMapData(zoom, x, y, outputStream)) {
			System.out.println("from db Zoom=" + zoom + " x=" + x + " y=" + y);
			closeStream(outputStream);
			return;
		}
		byte[] bt = getBytes(zoom, x, y);

		writeImage(bt, outputStream);
	}

	public static void streamCopy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	public static void closeStream(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {

		}
	}

	public static void writeImage(byte[] bytes, OutputStream stream)
			throws Exception {
		stream.write(bytes);
		closeStream(stream);
	}

	public static byte[] getBytes(String zoom, String x, String y)
			throws Exception {
		// System.out.println("from mappp Zoom=" + zoom + " x=" + x + " y=" +
		// y);
		String bbox = get_tile_bbox(Integer.valueOf(x.trim()),
				Integer.valueOf(y.trim()), Integer.valueOf(zoom.trim()));
		byte[] bt = new byte[0];
		String u = WMS_URL + "&bbox=" + bbox;
		URL url = new URL(u);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$
		connection.setConnectTimeout(35000);
		BufferedInputStream inputStream = new BufferedInputStream(
				connection.getInputStream(), 8 * BUFFER_SIZE);
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			streamCopy(inputStream, stream);
			stream.flush();
			bt = stream.toByteArray();
		} finally {
			closeStream(inputStream);
			closeStream(stream);
		}
		return bt;
	}

	private static DecimalFormat df2 = new DecimalFormat(
			"#######################0.00000000");
	private static String bbox_format = "%s,%s,%s,%s";

	private static String get_tile_bbox(int x, int y, int z) {
		int my = (int) (Math.pow(2, z) - y - 1);
		double[] min = get_merc_coords(x * 256, my * 256, z);
		double[] max = get_merc_coords((x + 1) * 256, (my + 1) * 256, z);
		String ret = String.format(bbox_format, df2.format(min[0]),
				df2.format(min[1]), df2.format(max[0]), df2.format(max[1]));
		return ret;
	}

	private static double[] get_merc_coords(int x, double y, int z) {
		double resolution = (2 * Math.PI * 6378137 / 256) / Math.pow(2, z);
		double merc_x = (x * resolution - 2 * Math.PI * 6378137 / 2.0);
		double merc_y = (y * resolution - 2 * Math.PI * 6378137 / 2.0);
		return new double[] { merc_x, merc_y };
	}

	public static void main(String[] args) throws Exception {
		double[] d = get_merc_coords(158, 114, 8);
		System.out.println(df2.format(d[0]) + ", " + df2.format(d[1]));
		System.out.println(get_tile_bbox(5043, 3052, 13));
	}
}
