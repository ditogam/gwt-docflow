package com.socarmap.utils;

import java.text.DecimalFormat;

import org.oscim.core.BoundingBox;
import org.oscim.renderer.MapTile;

public class SpacialMerchantor {
	public static double tileSize = 256;
	public static double initialResolution = 2 * Math.PI * 6378137 / tileSize;
	public static double originShift = 2 * Math.PI * 6378137 / 2.0;

	private static String bbox_format = "%s,%s,%s,%s";

	private static DecimalFormat df2 = new DecimalFormat(
			"#######################0.00000000");

	private static double[] get_merc_coords(int x, double y, int z) {
		double resolution = (2 * Math.PI * 6378137 / 256) / Math.pow(2, z);
		double merc_x = (x * resolution - 2 * Math.PI * 6378137 / 2.0);
		double merc_y = (y * resolution - 2 * Math.PI * 6378137 / 2.0);
		return new double[] { merc_x, merc_y };
	}

	private static double[] get_tile_bbox(int z, int x, int y) {
		int my = (int) (Math.pow(2, z) - y - 1);
		double[] min = get_merc_coords(x * 256, my * 256, z);
		double[] max = get_merc_coords((x + 1) * 256, (my + 1) * 256, z);
		String ret = String.format(bbox_format, df2.format(min[0]),
				df2.format(min[1]), df2.format(max[0]), df2.format(max[1]));
		System.out.println(ret);
		return new double[] { min[0], min[1], max[0], max[1] };
	}

	public static BoundingBox getBoundingBox(MapTile pTile) {
		// double[] area = get_tile_bbox(pTile.getZoomLevel(), pTile.getX(),
		// pTile.getY());
		double[] area = get_tile_bbox(18, 161393, 97654);
		double[] min = MetersToLatLon(area[0], area[1]);
		double[] max = MetersToLatLon(area[2], area[3]);
		area = new double[] { min[0], min[1], max[0], max[1] };
		BoundingBox bbox = new BoundingBox(area[0], area[1], area[2], area[3]);
		return bbox;
	}

	public static void main(String[] args) {
		System.out.println(get_tile_bbox(158, 114, 8));
	}

	private static double[] MetersToLatLon(double mx, double my) {
		double lon = (mx / originShift) * 180.0;
		double lat = (my / originShift) * 180.0;

		lat = 180
				/ Math.PI
				* (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new double[] { lat, lon };
	}

	public static double[] PixelsToMeters(double px, double py, int zoom) {
		double res = Resolution(zoom);
		double mx = px * res - originShift;
		double my = py * res - originShift;
		return new double[] { mx, my };
	}

	public static double Resolution(int zoom) {
		// Resolution (meters/pixel) for given zoom level (measured at Equator)"

		// return (2 * math.pi * 6378137) / (tileSize * 2**zoom)
		double p = (Math.pow(2, zoom));
		return initialResolution / p;
	}

	public static double[] TileBounds(int zoom, double tx, double ty) {
		double[] min = PixelsToMeters(tx * tileSize, ty * tileSize, zoom);
		double[] max = PixelsToMeters((tx + 1) * tileSize, (ty + 1) * tileSize,
				zoom);
		return new double[] { min[0], min[1], max[0], max[1] };
	}

}
