package com.socar.map;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Map;

public class TileFile {
	public static String USER_AGENT = "OsmAnd~";
	private static final String URL="http://localhost:8787/SocarMap/wms?bbox=%s&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:900913&width=256&height=256&layers=SocarAll&map=&styles=";
	private static final int BUFFER_SIZE = 1024;

	public static String getFileForImage(int x, int y, int zoom, String ext) {
		return zoom + "/" + (x) + "/" + y + ext + ".tile";
	}

	private int zoom;
	private int x;
	private int y;
	private boolean alreadyDownloaded;

	private File newRequestedFile;

	private String filePath;

	public TileFile(int zoom, int x, int y, String ext) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
		filePath = getFileForImage(x, y, zoom, ext);
		alreadyDownloaded = false;
	}

	public String code() {
		return createUnique(zoom, x, y);
	}

	private static String createUnique(int zoom, int x, int y) {
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

	public boolean isAlreadyDownloaded() {
		return alreadyDownloaded;
	}

	public File getNewRequestedFile() {
		return newRequestedFile;
	}

	public static void addIfNotExists(Map<String, TileFile> map, int zoom,
			int x, int y, String ext) {
		String unique = createUnique(zoom, x, y);
		if (map.containsKey(unique))
			return;
		map.put(unique, new TileFile(zoom, x, y, ext));
	}
	
	private static DecimalFormat df2 = new DecimalFormat(
			"#######################0.00000000");
	private static String bbox_format = "%s,%s,%s,%s";
	
	private static String get_tile_bbox(int x, int y, int z) {
		int my = (int)(Math.pow(2, z)-y-1);
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
	
	
	public static void main(String[] args) {
		int zoom=8;
		int x=158;
		int y=97;
		for (int i = 157; i < 162; i++) {
			for (int j = 94; j < 96; j++) {
				String mTile=String.format(URL, get_tile_bbox(i, j, zoom));
				//mTile="http://whoots.mapwarper.net/tms/"+zoom+"/"+zoom+"/"+zoom+"/SocarAll/http://localhost:8787/SocarMap/wms";
				try {
					System.out.println(mTile);
//					downloadFile(i+"_"+j+".png", mTile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	private static void downloadFile(String file, String urls) throws Exception{
		URL url = new URL(urls);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$
		connection.setConnectTimeout(35000);
		BufferedInputStream inputStream = new BufferedInputStream(
				connection.getInputStream(), 8 * BUFFER_SIZE);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(new File(file));
			streamCopy(inputStream, stream);
			stream.flush();
		} finally {
			closeStream(inputStream);
			closeStream(stream);
		}
	}
	
	public boolean download(boolean force, File parent, String mTile)
			throws Exception {
		if (alreadyDownloaded)
			return false;
		File newRequestedFile = new File(parent, filePath);
		if (!force && newRequestedFile.exists())
			return false;

		File newRequestedFileDir = newRequestedFile.getParentFile();
		boolean createdDir = true;
		if (!newRequestedFileDir.exists())
			createdDir = newRequestedFileDir.mkdirs();
		if (!createdDir)
			throw new Exception("Cannot create path:"
					+ newRequestedFileDir.getAbsolutePath());
		mTile=String.format(URL, get_tile_bbox(getX(), getY(), getZoom()));
//		System.out.println(mTile);
		URL url = new URL(mTile);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$
		connection.setConnectTimeout(35000);
		BufferedInputStream inputStream = new BufferedInputStream(
				connection.getInputStream(), 8 * BUFFER_SIZE);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(newRequestedFile);
			streamCopy(inputStream, stream);
			stream.flush();
		} finally {
			closeStream(inputStream);
			closeStream(stream);
		}
		alreadyDownloaded = true;
		this.newRequestedFile = newRequestedFile;
		return true;
		
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

}
