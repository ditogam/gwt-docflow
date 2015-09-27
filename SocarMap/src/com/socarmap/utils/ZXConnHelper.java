package com.socarmap.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

public class ZXConnHelper {
	public static void main(String[] args) {
		// new ZXConnHelper(new File(
		// "/Users/dito/Documents/android1/MapTilesExtractor/exported"),
		// null);
	}
	private Map<Integer, Zooms> zooms = new TreeMap<Integer, Zooms>();
	private ZoomConnection zoomConnection;
	private ZX1X2 lastZx1x2 = null;

	@SuppressWarnings("unused")
	private Context context;

	public ZXConnHelper(File parrent_path, ZoomConnection zoomConnection,
			Context context) {
		this.context = context;
		this.zoomConnection = zoomConnection;
		File[] subregions = parrent_path.listFiles();
		for (File file : subregions) {
			if (!file.isDirectory())
				continue;
			generateZooms(file);
		}
		// ActivityHelper.showAlert(context, this.zooms.size() + "");
	}

	public ArrayList<ZX1X2> findZooms(int zoom, int x, int y) {
		Zooms z = zooms.get(zoom);
		ArrayList<ZX1X2> ret = new ArrayList<ZX1X2>();
		if (z == null)
			return ret;
		ArrayList<ZX1X2> zx1x2s = z.getZx1x2s();
		if (zx1x2s == null || zx1x2s.isEmpty())
			return ret;

		for (ZX1X2 zx1x2 : zx1x2s) {
			if (zx1x2.acept(zoom, x))
				ret.add(zx1x2);
		}

		return ret;
	}

	private void generateZooms(File file) {
		File[] zooms = file.listFiles();
		for (File fzoom : zooms) {
			int zoom = Integer.parseInt(fzoom.getName());
			Zooms z = this.zooms.get(zoom);
			if (z == null) {
				z = new Zooms(zoom);
				this.zooms.put(zoom, z);
			}
			FilenameFilter fnf = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {

					return name.endsWith(".sqlite");
				}
			};

			String[] fileNames = fzoom.list(fnf);
			final String sufix = ".sqlite";
			final int length = sufix.length();
			for (String fn : fileNames) {
				String s = fn.substring(0, fn.length() - length);
				String ss[] = s.split("_");
				int x1 = Integer.parseInt(ss[0]);
				int x2 = Integer.parseInt(ss[1]);
				ZX1X2 zx1x2 = new ZX1X2(zoom, x1, x2, new File(fzoom, fn));
				z.addXs(zx1x2);
				// System.out.println(s);
			}

		}

	}

	public byte[] getImage(int zoom, int x, int y) throws Exception {

		byte[] data = null;
		if (lastZx1x2 != null && lastZx1x2.acept(zoom, x)) {
			data = zoomConnection.getImageData(lastZx1x2, zoom, x, y, true);
		}
		if (data == null) {
			Zooms z = zooms.get(zoom);
			if (z == null)
				return null;
			ArrayList<ZX1X2> zx1x2s = z.getZx1x2s();
			if (zx1x2s == null || zx1x2s.isEmpty())
				return null;
			for (ZX1X2 zx1x2 : zx1x2s) {
				if (lastZx1x2 != null && lastZx1x2.equals(zx1x2))
					continue;
				if (zx1x2.acept(zoom, x)) {
					data = zoomConnection
							.getImageData(zx1x2, zoom, x, y, false);
					if (data != null) {
						lastZx1x2 = zx1x2;
						return data;
					}
				}
			}
			lastZx1x2 = null;
		}
		return data;
	}

	public ZoomConnection getZoomConnection() {
		return zoomConnection;
	}

}
