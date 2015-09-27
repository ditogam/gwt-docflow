package src.com.socargass.tabletexporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

public class GetMap {
	private static final String WMS_URL = "http://whoots.mapwarper.net/tms/%s/%s/%s/SocarAll/http://localhost:8787/SocarMap/wms";
	public static String USER_AGENT = "OsmAnd~";
	private static final int BUFFER_SIZE = 1024;
	public static File parent = new File("/Users/dito/osmand/tiles/SocarMap");

	public static void getmap(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		String zoom = request.getParameter("zoom");
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		byte[] bt = getBytes(zoom, x, y);
		response.setContentType("image/png");
		OutputStream outputStream = response.getOutputStream();
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

	private static void writeImage(byte[] bytes, OutputStream stream)
			throws Exception {
		stream.write(bytes);
		closeStream(stream);
	}

	private static byte[] getBytes(String zoom, String x, String y)
			throws Exception {

		byte[] bt = new byte[0];
		File file = new File(parent, zoom);
		file = new File(file, x);
		file = new File(file, y + ".png.tile");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (file.exists()) {
			ByteArrayOutputStream stream = null;
			FileInputStream inputStream = null;
			try {
				stream = new ByteArrayOutputStream();
				inputStream = new FileInputStream(file);
				streamCopy(inputStream, stream);
				bt = stream.toByteArray();
				return bt;
			} finally {
				closeStream(inputStream);
				closeStream(stream);
			}
		}

		String myURL = String.format(WMS_URL, zoom, x, y);
		URL url = new URL(myURL);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$
		connection.setConnectTimeout(35000);
		BufferedInputStream inputStream = new BufferedInputStream(
				connection.getInputStream(), 8 * BUFFER_SIZE);
		ByteArrayOutputStream stream = null;
		FileOutputStream fos = null;
		try {
			stream = new ByteArrayOutputStream();
			streamCopy(inputStream, stream);
			stream.flush();
			bt = stream.toByteArray();
			fos = new FileOutputStream(file);
			fos.write(bt);
		} finally {
			closeStream(inputStream);
			closeStream(stream);
			closeStream(fos);
		}
		return bt;
	}

	public static void main(String[] args) throws Exception {
		byte[] bt = getBytes(8 + "", 157 + "", 94 + "");
		writeImage(bt, new FileOutputStream("aa.png"));
	}
}
