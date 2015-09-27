package com.socar.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MapDownloader {

	public static File parent = new File("/Users/dito/osmand/tiles/SocarMap");
	public static String tile = "http://localhost:8787/SocarMap/wms?bbox=%s&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:900913&width=256&height=256&layers=SocarAll&map=&styles=";

	public static String tileS = "http://whoots.mapwarper.net/tms/%s/%s/%s/SocarAll/http://localhost:8787/SocarMap/wms";

	private static boolean getBoolean(String value) {
		return value != null
				&& (value.trim().equals("1") || value.trim().toLowerCase()
						.equals("true"));
	}

	private static int getInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return defaultValue;
		}

	}

	public static void main1(String[] args) throws Exception {
		// String sql =
		// "select XMIN, YMAX,XMAX, YMIN from v_updated_buildings where lasttime < updated";
		// sql =
		// "select XMIN, YMAX,XMAX, YMIN from maps.v_subregion_bounds where subregion_id is not null";
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		String sql = props.getProperty("sql");
		boolean forceDownload = getBoolean(props.getProperty("forceDownload"));
		final int startZoom = getInt(props.getProperty("startZoom"), 8);
		final int endZoom = getInt(props.getProperty("endZoom"), 11);
		String tileFormat = ".png";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<ZXY> zxies = new ArrayList<ZXY>();

		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(props.getProperty("connection"),
					props.getProperty("user"), props.getProperty("pwd"));
			stmt = con.prepareStatement(sql);
			// stmt.setInt(1, 24);
			rs = stmt.executeQuery();
			int count = 0;
			Map<String, ZXY> map = new TreeMap<String, ZXY>();
			while (rs.next()) {

				// int x1 = rs.getInt("x1");
				// int x2 = rs.getInt("x2");
				// int y1 = rs.getInt("y1");
				// int y2 = rs.getInt("y2");
				// int zoom = rs.getInt("zoom");
				// makeTiles(map, tileFormat, zoom, x1, x2, y1, y2);
				MapSelectionArea selectionArea = new MapSelectionArea(
						rs.getDouble("xmin"), rs.getDouble("ymax"),
						rs.getDouble("xmax"), rs.getDouble("YMIN"));
				ArrayList<ZX1X2Y1Y2> zx1x2y1y2s = makeTiles(selectionArea,
						startZoom, endZoom, rs.getInt("subregion_id"));
				// getTileList(map, selectionArea, parent, tile, forceDownload,
				// startZoom, endZoom, tileFormat);
				// // downloadTiles(map, parent, tileFormat, forceDownload);
				// System.err.println((++count) + " has been proceeded with "
				// + zxies.size() + " for " + rs.getInt("subregion_id"));
				for (ZX1X2Y1Y2 zx1x2y1y2 : zx1x2y1y2s) {
					// System.out.println(" "+zx1x2y1y2);
				}
			}

			stmt.close();
			// con.setAutoCommit(false);
			// stmt =
			// con.prepareStatement("delete from maps.lastdownloadedpcity");
			// stmt.executeUpdate();
			// stmt.close();
			// stmt = con
			// .prepareStatement(" insert into maps.lastdownloadedpcity select subregion_id,now() from subregions where subregion_id is not null");
			//
			// stmt.executeUpdate();
			// stmt.close();
			// con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		try {
			// MapSelectionArea selectionArea = new MapSelectionArea(
			// 39.89846187591553, 43.61017918885324, 46.76491695404053,
			// 41.04409437259262);
			// makeTiles(map, selectionArea, startZoom, endZoom, tileFormat);

			// downloadTiles(map, parent, tile, forceDownload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static Map<String, TileFile> getTileList(
	// MapSelectionArea selectionArea, File parent, String tile,
	// boolean forceDownload, final int startZoom, final int endZoom,
	// String tileFormat) {
	// return getTileList(null, selectionArea, parent, tile, forceDownload,
	// startZoom, endZoom, tileFormat);
	// }
	//
	// public static Map<String, TileFile> getTileList(Map<String, TileFile>
	// map,
	// MapSelectionArea selectionArea, File parent, String tile,
	// boolean forceDownload, final int startZoom, final int endZoom,
	// String tileFormat) {
	// if (map == null)
	// map = new TreeMap<String, TileFile>();
	//
	// makeTiles(map, selectionArea, startZoom, endZoom, tileFormat);
	// downloadTiles(map, parent, tile, forceDownload);
	// return map;
	// }
	public static void main(String[] args) {
		ArrayList<ZXY> list = new ArrayList<ZXY>();
		makeTiles(list, new MapSelectionArea(45.5562589734207,
				42.1324377230937, 46.1232102589639, 41.8001052651163), 8, 10);
		for (ZXY zxy : list) {
			System.out.println(zxy);
		}
	}

	public static void makeTiles(ArrayList<ZXY> zxies,
			MapSelectionArea selectionArea, final int startZoom,
			final int endZoom) {
		int numberTiles = 0;
		for (int zoom = startZoom; zoom <= endZoom; zoom++) {
			int x1 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon1());
			int x2 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon2());
			int y1 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat1());
			int y2 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat2());
			int tmp = x1;
			if (x1 > x2) {
				tmp = x1;
				x1 = x2;
				x2 = tmp;
			}
			if (y1 > y2) {
				tmp = y1;
				y1 = y2;
				y2 = tmp;
			}
			numberTiles += (x2 - x1 + 1) * (y2 - y1 + 1);
			makeTiles(zxies, zoom, x1, x2, y1, y2);
		}
		// System.out.println(numberTiles);
	}

	public static ArrayList<ZX1X2Y1Y2> makeTiles(
			MapSelectionArea selectionArea, final int startZoom,
			final int endZoom, int subregion_id) {
		ArrayList<ZX1X2Y1Y2> result = new ArrayList<ZX1X2Y1Y2>();
		int numberTiles = 0;
		for (int zoom = startZoom; zoom <= endZoom; zoom++) {
			int x1 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon1());
			int x2 = (int) MapUtils.getTileNumberX(zoom,
					selectionArea.getLon2());
			int y1 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat1());
			int y2 = (int) MapUtils.getTileNumberY(zoom,
					selectionArea.getLat2());
			int tmp = x1;
			if (x1 > x2) {
				tmp = x1;
				x1 = x2;
				x2 = tmp;
			}
			if (y1 > y2) {
				tmp = y1;
				y1 = y2;
				y2 = tmp;
			}
			numberTiles += (x2 - x1 + 1) * (y2 - y1 + 1);
			result.add(new ZX1X2Y1Y2(zoom, x1, y1, x2, y2, subregion_id));
			for (int x = x1; x <= x2; x++) {
				for (int y = y1; y <= y2; y++) {
					System.out.println(zoom + " " + x + " " + y);
				}
			}
			// makeTiles(zxies, zoom, x1, x2, y1, y2);
		}

		// System.out.println(numberTiles);
		return result;
	}

	public static void makeTiles(ArrayList<ZXY> zxies, int zoom, int x1,
			int x2, int y1, int y2) {

		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				zxies.add(new ZXY(zoom, x, y));
			}
		}
	}

	public static void downloadTiles(Map<String, TileFile> map, File parent,
			String tile, boolean forceDownload) {
		ThreadPoolExecutor threadPoolExecutor;
		threadPoolExecutor = new ThreadPoolExecutor(2, 2, 25, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		DownloadMapWorker.fullsize = map.size();
		DownloadMapWorker.fulltime = System.currentTimeMillis();
		for (TileFile tf : map.values()) {
			String mTile = String.format(tileS, tf.getZoom() + "", tf.getX()
					+ "", tf.getY() + "");
			// System.out.println(mTile);
			try {

				// threadPoolExecutor.execute(new
				// DownloadMapWorker(forceDownload,
				// parent, mTile, tf));
				tf.download(forceDownload, parent, mTile);

			} catch (Exception e) {
				e.printStackTrace();
				DownloadMapWorker.errorcount++;
			}

		}
		DownloadMapWorker.fulltime = System.currentTimeMillis()
				- DownloadMapWorker.fulltime;
		System.out.println("completed in " + DownloadMapWorker.fulltime
				+ " ms errorcount=" + DownloadMapWorker.errorcount);
	}

}
