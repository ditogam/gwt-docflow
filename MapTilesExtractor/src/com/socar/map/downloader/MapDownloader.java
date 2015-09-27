package com.socar.map.downloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapDownloader implements Runnable {
	private static final int BUFFER_SIZE = 1024;
	public static String USER_AGENT = "OsmAnd~";
	private static final String FULL_TIME = "FULL_TIME";
	private static final String SELECT_TIME = "SELECT_TIME";
	private static final String DOWNLOAD_TIME = "DOWNLOAD_TIME";
	private static final String SAVE_TIME = "SAVE_TIME";
	private static final String COMMIT_TIME = "COMMIT_TIME";
	private static final String BBOX_TIME = "BBOX_TIME";
	private static final String ONE_THREAD_TIME = "ONE_THREAD_TIME";
	private static final String EXCEPTION_COUNT = "EXCEPTION_COUNT";

	private String connectionString;
	private String user;
	private String password;
	private int rcn;
	private String map_url;
	private ArrayList<File> files;
	private int batch_size;

	private Thread thread;

	public MapDownloader(String connectionString, String user, String password,
			ArrayList<File> files) throws Exception {
		this(connectionString, user, password);
		this.files = files;
		createThread();
	}

	private void createThread() {
		thread = new Thread(this, "MyName is" + System.nanoTime());
		thread.start();
	}

	public Thread getThread() {
		return thread;
	}

	public MapDownloader(String connectionString, String user, String password,
			int rcn, String map_url, int batch_size) throws Exception {
		this(connectionString, user, password);
		this.rcn = rcn;
		this.map_url = map_url;
		this.batch_size = batch_size;
		createThread();
	}

	public MapDownloader(String connectionString, String user, String password)
			throws Exception {
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;

	}

	private byte[] downloadFile(String urlStr, String bbox) throws Exception {
		String u = urlStr + "&bbox=" + bbox;
		URL url = new URL(u);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT); //$NON-NLS-1$
		connection.setConnectTimeout(35000);
		BufferedInputStream inputStream = new BufferedInputStream(
				connection.getInputStream(), 8 * BUFFER_SIZE);
		byte[] bytes = new byte[0];
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			streamCopy(inputStream, stream);
			stream.flush();
			bytes = stream.toByteArray();
		} finally {
			closeStream(inputStream);
			closeStream(stream);
		}
		return bytes;
	}

	private byte[] downloadFile(File file) throws Exception {
		FileInputStream inputStream = new FileInputStream(file);
		byte[] bytes = new byte[0];
		ByteArrayOutputStream stream = null;
		try {
			stream = new ByteArrayOutputStream();
			streamCopy(inputStream, stream);
			stream.flush();
			bytes = stream.toByteArray();
		} finally {
			closeStream(inputStream);
			closeStream(stream);
		}
		return bytes;
	}

	public static void closeStream(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {

		}
	}

	public static void streamCopy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	private static DecimalFormat df2 = new DecimalFormat(
			"#######################0.00000000");
	private static String bbox_format = "%s,%s,%s,%s";

	public static void main(String[] args) {
		System.out.println(get_tile_bbox(5110, 3045, 13));
	}
	
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

	@Override
	public void run() {
		Connection conn = null;
		try {
			Timer.start(ONE_THREAD_TIME);

			Class.forName("org.postgresql.Driver");
			conn = DriverManager
					.getConnection(connectionString, user, password);
			conn.setAutoCommit(false);

			String procname = "savemapdata";
			if (files != null)
				procname = "savemapdatafromfile";
			PreparedStatement psSave = conn.prepareStatement("select maps."
					+ procname + "(?,?,?,?) ");
			int count = 0;
			if (files == null) {
				PreparedStatement psSelect = conn
						.prepareStatement("select zoom,x, y from maps.zoom_xy where rcn_id=? and not created");
				psSelect.setInt(1, rcn);
				Timer.start(SELECT_TIME);
				ResultSet rs = psSelect.executeQuery();
				Timer.step(SELECT_TIME);
				if (count % 100 == 0) {
					System.out.println("RCN =" + rcn);
					Timer.printall();
				}
				while (rs.next()) {
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					Timer.start(FULL_TIME);
					int zoom = rs.getInt("zoom");
					Timer.start(BBOX_TIME);
					String bbox = get_tile_bbox(x, y, zoom);
					Timer.step(BBOX_TIME);
					byte[] bt = null;
					try {
						Timer.start(DOWNLOAD_TIME);
						bt = downloadFile(map_url, bbox);
						Timer.step(DOWNLOAD_TIME);
						if (new String(bt).contains("xception")) {
							Timer.start(EXCEPTION_COUNT);
							Timer.step(EXCEPTION_COUNT);
							continue;
						}
					} catch (Exception e) {
						Timer.step(DOWNLOAD_TIME);
						continue;
					}

					psSave.setInt(1, zoom);
					psSave.setInt(2, x);
					psSave.setInt(3, y);
					psSave.setBytes(4, bt);
					Timer.start(SAVE_TIME);
					psSave.executeQuery().close();
					Timer.step(SAVE_TIME);
					Timer.step(FULL_TIME);

					count++;
					if (count % 100 == 0) {
						System.out.println("RCN =" + rcn);
						Timer.printall();
					}
					if (count % batch_size == 0) {
						Timer.start(COMMIT_TIME);
						conn.commit();
						Timer.step(COMMIT_TIME);
					}

				}
			} else {
				for (File file : files) {
					if (!file.exists())
						continue;
					String fileName = file.getName();
					String extract = ".png.tile";
					fileName = fileName.substring(0, fileName.indexOf(extract));
					try {
						int y = Integer.parseInt(fileName);
						int x = Integer
								.parseInt(file.getParentFile().getName());
						int zoom = Integer.parseInt(file.getParentFile()
								.getParentFile().getName());
						Timer.start(FULL_TIME);
						Timer.start(DOWNLOAD_TIME);
						byte[] bt = null;
						try {
							bt = downloadFile(file);
						} catch (Exception e) {
							continue;
						}
						Timer.step(DOWNLOAD_TIME);
						psSave.setInt(1, zoom);
						psSave.setInt(2, x);
						psSave.setInt(3, y);
						psSave.setBytes(4, bt);
						Timer.start(SAVE_TIME);
						psSave.executeQuery().close();
						Timer.step(SAVE_TIME);
						file.renameTo(new File(file.getAbsolutePath() + ".tmp"));
						Timer.step(FULL_TIME);
						count++;
						if (count % 1000 == 0) {
							Timer.printall();
							System.out.println(zoom + "/" + x + "/" + y);
						}
						if (count % 100 == 0) {
							System.out.println(zoom + "/" + x + "/" + y);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}

			PreparedStatement stmt = conn
					.prepareStatement("update maps.rcn_tbl set proceeded=true where rcn_id=?");
			stmt.setInt(1, rcn);
			stmt.executeUpdate();

			Timer.start(COMMIT_TIME);
			conn.commit();
			Timer.step(COMMIT_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Timer.step(ONE_THREAD_TIME);
			Timer.printall();
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}
}
