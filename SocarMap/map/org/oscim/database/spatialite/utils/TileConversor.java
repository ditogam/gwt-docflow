/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.database.spatialite.utils;

public class TileConversor {

	public static int pixelsPerTile = Tags.DEFAULT_TILE_SIZE;
	public static final int numZoomLevels = Tags.GOOGLE_MAX_ZOOM_LEVEL;
	public static final double PI_x_2 = 6.283185307179586476925286766559;
	public static final double PI_x_180_inv = 0.0017683882565766148418764862596946;
	private static final double originShift = 20037508.3427892430765884088807;
	private static final double PI_div_360 = 0.0087266462599716478846184538424431;
	private static final double PI_div_180 = 0.017453292519943295769236907684886;
	private static final double originShift_div_180 = 111319.49079327357264771338267056;
	public static final double PI = 3.1415926535897932384626433832795;
	public static double originX = 0;
	public static double originY = 0;

	/**
	 * Makes the transformation from all supported coordinates systems into a
	 * tile XY number
	 * 
	 * @param lon
	 *            The coordinate longitude
	 * @param lat
	 *            The coordinate latitude
	 * @param proj
	 *            A Projection instance
	 * @param type
	 *            The type of the server TYPE_TILE_SERVER = 0; //OSM tile server
	 *            TYPE_YAHOO_TILE_SERVER = 1; TYPE_MICROSOFT_TILE_SERVER = 2;
	 *            TYPE_WMS_CACHE_CAPABILITIES_SERVER = 3;
	 *            TYPE_WMS_CACHE_TMS_SERVER = 4; TYPE_WMS_SERVER = 5;
	 * @param zoomLevel
	 *            The zoom level
	 * @param resolution
	 *            The resolution
	 * @param origin
	 *            The lower left corner of the max extent of the map
	 * @param profile
	 *            The lower left corner of the max extent of the map
	 */
	public static Pixel coordinatesToTile(double lon, double lat,
			Projection proj, int type, final int zoomLevel,
			final double resolution, Point origin, String profile) {
		Point p = null;
		Pixel tileN = null;
		String unit = "";
		if (type == Tags.TYPE_YAHOO_TILE_SERVER) {
			p = TileConversor.mercatorToLatLon(lon, lat);
			// p = new Point(lon, lat);
			tileN = TileConversor.getYahooTileNumber(p.getX(), p.getY(),
					zoomLevel);
		} else if (type == Tags.TYPE_MICROSOFT_TILE_SERVER) {
			p = TileConversor.mercatorToLatLon(lon, lat);
			// p = new Point(lon, lat);
			tileN = TileConversor.getTileNumber(p.getX(), p.getY(), zoomLevel);
		} else if (type == Tags.TYPE_TILE_SERVER) {
			p = TileConversor.mercatorToLatLon(lon, lat);
			// p = new Point(lon, lat);
			tileN = TileConversor.getTileNumber(p.getX(), p.getY(), zoomLevel);
		} else if (type == Tags.TYPE_WMS_CACHE_CAPABILITIES_SERVER) {
			unit = proj.getUnitsAbbrev();
			if (unit.compareTo("g") == 0) {
				tileN = TileConversor.latLonToTileG(lon, lat, zoomLevel);
			} else if (unit.compareTo("m") == 0) {
				if (GeoUtils.isMercatorProjection(proj.getAbrev())) {
					tileN = TileConversor.mercatorToTile(lon, lat, resolution);
				} else {
					tileN = TileConversor.metersToTile(lon, lat, resolution,
							-origin.getX(), -origin.getY());
				}
			}
		} else if (type == Tags.TYPE_WMS_CACHE_TMS_SERVER) {
			if (profile.startsWith("global-g")) {
				tileN = TileConversor.latLonToTileG(lon, lat, zoomLevel);
			} else if (profile.startsWith("global-m")) {
				tileN = TileConversor.mercatorToTile(lon, lat, zoomLevel);
			} else {
				tileN = TileConversor.metersToTile(lon, lat, resolution,
						-origin.getX(), -origin.getY());
			}
		} else {
			tileN = TileConversor.metersToTile(lon, lat, resolution,
					-origin.getX(), -origin.getY());
		}
		// System.out.println(tileN.toString());
		return tileN;
	}

	/**
	 * Calculates the tile XY position given a lon-lat position and a zoom
	 * level. This method is used in coordinate tile system of OSM, Yahoo,
	 * Microsoft, Google, etc.
	 * 
	 * @param lon
	 *            Longitude
	 * @param lat
	 *            Latitude
	 * @param resolution
	 * @return A Point with the XY coordinates of the tile that contains the
	 *         lon-lat position at given zoom level
	 */
	public static Pixel getTileNumber(final double lon, final double lat,
			final double resolution) {
		int xtile = (int) Math.floor((lon + 180) / 360 * (resolution));
		int ytile = (int) Math.floor((1 - Float11.log(Math.tan(lat * Math.PI
				/ 180)
				+ 1 / Math.cos(lat * Math.PI / 180))
				/ Math.PI)
				/ 2 * (resolution));
		return new Pixel(xtile, ytile);
	}

	/**
	 * Calculates the tile XY position given a lon-lat position and a zoom
	 * level. This method is used in coordinate tile system of OSM, Yahoo,
	 * Microsoft, Google, etc.
	 * 
	 * @param lon
	 *            Longitude
	 * @param lat
	 *            Latitude
	 * @param zoom
	 *            Zoom level
	 * @return A Point with the XY coordinates of the tile that contains the
	 *         lon-lat position at given zoom level
	 */
	public static Pixel getTileNumber(final double lon, final double lat,
			final int zoom) {
		int xtile = (int) Math.floor((lon + 180.0) / 360.0 * (1 << zoom));
		int ytile = (int) Math.floor((1 - Float11.log(Math.tan(lat * Math.PI
				/ 180.0)
				+ 1 / Math.cos(lat * Math.PI / 180.0))
				/ Math.PI)
				/ 2 * (1 << zoom));
		return new Pixel(xtile, ytile);
	}

	public static Pixel getYahooTileNumber(final double lon, final double lat,
			final int zoom) {
		int xtile = (int) Math.floor((lon + 180.0) / 360.0 * (1 << zoom));
		int ytile = (int) Math.floor((1 - Float11.log(Math.tan(lat * Math.PI
				/ 180.0)
				+ 1 / Math.cos(lat * Math.PI / 180.0))
				/ Math.PI)
				/ 2 * (1 << zoom));

		ytile = (((1 << (zoom)) >> 1) - 1 - ytile);
		return new Pixel(xtile, ytile);
	}

	public static Pixel GoogleTile(final int tx, final int ty, final int zoom) {

		return new Pixel(tx, ((1 << zoom) - 1) - ty);
	}

	/**
	 * Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator
	 * EPSG:900913
	 * 
	 * @param lat
	 *            lattitude
	 * @param lon
	 *            longitude
	 * @return a geom.Point into EPSG:900913
	 */
	public static Point latLonToMercator(final double lon, final double lat) {
		double mx = lon * originShift / 180.0;
		double my = Float11.log(Math.tan((90 + lat) * PI_div_360)) / PI_div_180;

		my = my * originShift_div_180;
		return new Point(mx, my);
	}

	/**
	 * Global Geodetic Profile Converts lat/lon to pixel coordinates in given
	 * zoom of the EPSG:4326 pyramid
	 * 
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @return
	 */
	private static Pixel latLonToPixelsG(final double lon, final double lat,
			final double resolution) {

		double res = resolution;
		double px = (180 + lon) / res;
		double py = (90 + lat) / res;
		return new Pixel((int) px, (int) py);
	}

	// public static Extent tileMeterBounds(final int tx, final int ty,
	// final double resolution, final double originX, final double originY) {
	// Point leftBottom = pixelsToMeters(tx * pixelsPerTile, ty
	// * pixelsPerTile, resolution, -originX, originY);
	// Point rightTop = new Point(leftBottom.getX()
	// + (pixelsPerTile * resolution), leftBottom.getY()
	// + (pixelsPerTile * resolution));
	//
	// // Point rightTop = pixelsToMeters((tx + 1) * pixelsPerTile, (ty + 1)
	// // * pixelsPerTile, resolution, originX, originY);
	// return new Extent(leftBottom, rightTop);
	// }

	/**
	 * Global Geodetic Profile Converts lat/lon to pixel coordinates in given
	 * zoom of the EPSG:4326 pyramid
	 * 
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @return
	 */
	private static Pixel latLonToPixelsG(final double lon, final double lat,
			final int zoom) {

		final double tileSize = pixelsPerTile;
		double res = 180 / tileSize / (1 << zoom);
		double px = (180 + lon) / res;
		double py = (90 + lat) / res;
		return new Pixel((int) px, (int) py);
	}

	/**
	 * This method converts Global Geodetic Coordinates (EPSG:4326) into Tile
	 * number according to Global Geodetic Profile and TMS notation (bottom-left
	 * tile is the first one) This method can be used to convert coordinates
	 * from any WMS-c server that conforms the OSGEO's Tile Map Service
	 * specification with EPSG:4326 and the global geodetic profile.
	 * 
	 * @param lon
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static Pixel latLonToTileG(final double lon, final double lat,
			final double resolution) {
		Pixel p = latLonToPixelsG(lon, lat, resolution);
		return pixelsToTileG(p.getX(), p.getY());
	}

	/**
	 * This method converts Global Geodetic Coordinates (EPSG:4326) into Tile
	 * number according to Global Geodetic Profile and TMS notation (bottom-left
	 * tile is the first one) This method can be used to convert coordinates
	 * from any WMS-c server that conforms the OSGEO's Tile Map Service
	 * specification with EPSG:4326 and the global geodetic profile.
	 * 
	 * @param lon
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static Pixel latLonToTileG(final double lon, final double lat,
			final int zoom) {
		Pixel p = latLonToPixelsG(lon, lat, zoom);
		return pixelsToTileG(p.getX(), p.getY());
	}

	/**
	 * This method converts Global Geodetic Coordinates (EPSG:4326) into Tile
	 * number according to Global Mercator Profile and TMS notation (bottom-left
	 * tile is the first one) This method can be used to convert coordinates
	 * from any WMS-c server that conforms the OSGEO's Tile Map Service
	 * specification with EPSG:4326 and the mercator profile.
	 * 
	 * @param lon
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static Pixel latLonToTileM(final double lon, final double lat,
			final double resolution) {
		Point p = latLonToMercator(lon, lat);
		Pixel pix = mercatorToPixels(p.getX(), p.getY(), resolution);
		return pixelsToTile(pix.getX(), pix.getY());
	}

	/**
	 * This method converts Global Geodetic Coordinates (EPSG:4326) into Tile
	 * number according to Global Mercator Profile and TMS notation (bottom-left
	 * tile is the first one) This method can be used to convert coordinates
	 * from any WMS-c server that conforms the OSGEO's Tile Map Service
	 * specification with EPSG:4326 and the mercator profile.
	 * 
	 * @param lon
	 *            Longitude coordinate in EPSG:4326
	 * @param lat
	 *            Latitude coordinate in EPSG:4326
	 * @param zoom
	 *            Zoom level where 0 is the zoom level that covers the whole
	 *            earth
	 * @return
	 */
	public static Pixel latLonToTileM(final double lon, final double lat,
			final int zoom) {
		Point p = latLonToMercator(lon, lat);
		Pixel pix = mercatorToPixels(p.getX(), p.getY(), zoom);
		return pixelsToTile(pix.getX(), pix.getY());
	}

	public static Pixel latLonToTileOSM(double lon, double lat, int zoom) {
		Point merc = latLonToMercator(lon, lat);
		Pixel px = mercatorToPixelsOSM(merc.getX(), merc.getY(), zoom);
		return pixelsToTile(px.getX(), px.getY());
	}

	/**
	 * Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84
	 * Datum
	 * 
	 * @param pixelX
	 * @param pixelY
	 * @return
	 */
	public static Point mercatorToLatLon(final double px, final double py) {

		double lon = (px / originShift) * 180.0;
		double lat = (py / originShift) * 180.0;

		lat = 180
				/ Math.PI
				* (2 * Float11.atan(Float11.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new Point(lon, lat);
	}

	/**
	 * Converts EPSG:900913 to pyramid pixel coordinates in given zoom level
	 * 
	 * @param mx
	 * @param my
	 * @param resolution
	 * @return
	 */
	private static Pixel mercatorToPixels(final double mx, final double my,
			final double resolution) {

		int px = (int) ((mx + originShift) / resolution);
		int py = (int) ((my + originShift) / resolution);
		return new Pixel(px, py);
	}

	/**
	 * Converts EPSG:900913 to pyramid pixel coordinates in given zoom level
	 * 
	 * @param mx
	 * @param my
	 * @param resolution
	 * @return
	 */
	public static Pixel mercatorToPixels(final double mx, final double my,
			final int zoom) {
		double resolution = resolution(zoom);
		int px = (int) ((mx + originShift) / resolution);
		int py = (int) ((my + originShift) / resolution);
		return new Pixel(px, py);
	}

	/**
	 * OSM Converts coordinates in mercator proj to pixel coordinates in given
	 * zoom level
	 * 
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @return
	 */
	private static Pixel mercatorToPixelsOSM(final double mx, final double my,
			final int zoom) {

		double resolution = resolution(zoom);
		int px = (int) ((mx + originShift) / resolution);
		int py = (int) ((my - originShift) / resolution);
		return new Pixel(px, -py);
	}

	/**
	 * Converts coordinates from Spherical Mercator EPSG:900913 to Tile number
	 * according to TMS notation (bottom-left tile is the first one). This
	 * method is similar to TileConversor.metersToTile but the default origin XY
	 * coordinates correspond to 20037508.3427892430765884088807;
	 * 
	 * @param mx
	 * @param my
	 * @param zoom
	 * @return
	 */
	public static Pixel mercatorToTile(final double mx, final double my,
			final double resolution) {
		Pixel p = mercatorToPixels(mx, my, resolution);
		return pixelsToTile(p.getX(), p.getY());
	}

	/**
	 * Converts coordinates from Spherical Mercator EPSG:900913 to Tile number
	 * according to TMS notation (bottom-left tile is the first one). This
	 * method is similar to TileConversor.metersToTile but the default origin XY
	 * coordinates correspond to 20037508.3427892430765884088807;
	 * 
	 * @param mx
	 * @param my
	 * @param zoom
	 * @return
	 */
	public static Pixel mercatorToTile(final double mx, final double my,
			final int zoom) {
		Pixel p = mercatorToPixels(mx, my, zoom);
		return pixelsToTile(p.getX(), p.getY());
	}

	public static Pixel mercatorToTileOSM(double mx, double my, int zoom) {
		Pixel px = mercatorToPixelsOSM(mx, my, zoom);
		return pixelsToTile(px.getX(), px.getY());
	}

	private static Pixel metersToPixels(final double mx, final double my,
			final double resolution) {
		int px = (int) ((mx + originX) / resolution);
		int py = (int) ((my + originY) / resolution);
		return new Pixel(px, py);
	}

	/**
	 * Converts coordinates in meters into tile number according to the TMS
	 * notation. (bottom-left tile is the first one) This method can be used as
	 * a default method to convert coordinates in any coordinate system in
	 * meters (local profile) into tile numbers.
	 * 
	 * @param mx
	 *            The x coordinate in meters
	 * @param my
	 *            The y coordinate in meters
	 * @param resolution
	 *            The meters per pixel resolution
	 * @param origX
	 *            The lower left corner x coordinate of the max extent of the
	 *            layer
	 * @param origY
	 *            The lower left corner y coordinate of the max extent of the
	 *            layer
	 * @return
	 */
	public static Pixel metersToTile(final double mx, final double my,
			final double resolution, final double origX, final double origY) {
		originX = origX;
		originY = origY;
		Pixel p = metersToPixels(mx, my, resolution);
		return pixelsToTile(p.getX(), p.getY());
	}

	/**
	 * Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
	 * 
	 * @param px
	 * @param py
	 * @param resolution
	 * @return
	 */
	private static Point pixelsToMercatorM(final int px, final int py,
			final double resolution) {

		double mx = px * resolution - originShift;
		double my = py * resolution - originShift;
		return new Point(mx, my);
	}

	/**
	 * Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
	 * 
	 * @param px
	 * @param py
	 * @param resolution
	 * @return
	 */
	private static Point pixelsToMercatorM(final int px, final int py,
			final int zoom) {
		double resolution = resolution(zoom);
		double mx = px * resolution - originShift;
		double my = py * resolution - originShift;
		return new Point(mx, my);
	}

	public static Point pixelsToMercatorOSM(final int px, final int py,
			final int zoom) {
		double resolution = resolution(zoom);
		double mx = px * resolution - originShift;
		double my = py * resolution - originShift;
		return new Point(mx, -my);
	}

	private static Point pixelsToMeters(final int px, final int py,
			final double resolution) {
		double mx = px * resolution - originX;
		double my = py * resolution - originY;
		return new Point(mx, my);
	}

	private static Point pixelsToMeters(final int px, final int py,
			final double resolution, final double originX, final double originY) {
		double mx = (px * resolution - originX);
		double my = py * resolution - originY;
		return new Point(mx, my);
	}

	/**
	 * Returns a tile covering region in given pixel coordinates
	 * 
	 * @param px
	 * @param py
	 * @return
	 */
	private static Pixel pixelsToTile(final int px, final int py) {
		// int tx = (int) (Math.ceil(px / (pixelsPerTile)) - 1);
		// int ty = (int) (Math.ceil(py / (pixelsPerTile)) - 1);
		// if (px < 0 || py < 0) {
		// return null;
		// }
		int tx = (int) (Math.ceil(px / (pixelsPerTile)));
		int ty = (int) (Math.ceil(py / (pixelsPerTile)));
		return new Pixel(tx, ty);
	}

	/**
	 * Global Geodetic Profile Returns coordinates of the tile covering region
	 * in pixel coordinates
	 * 
	 * @param px
	 * @param py
	 * @return
	 */
	private static Pixel pixelsToTileG(final int px, final int py) {

		int tx = (int) (Math.ceil(px / (float) (pixelsPerTile)) - 1);
		int ty = (int) (Math.ceil(py / (float) (pixelsPerTile)) - 1);
		return new Pixel(tx, ty);
	}

	public static StringBuffer quadKeyToDirectory(StringBuffer quadKey) {
		StringBuffer dir = new StringBuffer();
		int size = quadKey.length();
		for (int i = 0; i < size; i++) {
			dir.append(quadKey.charAt(i));
			dir.append("/");
		}
		return dir;
	}

	/**
	 * Resolution (meters/pixel) for given zoom level (measured at Equator)
	 * 
	 * @param zoom
	 * @return
	 */
	private static double resolution(final int zoom) {
		return Tags.RESOLUTIONS[zoom];
	}

	// public static Point getTileCoordintate(double lon, double lat, int zoom)
	// {
	// return Tile.getTileCoordinate(lon, lat, zoom);
	// }
	/**
	 * Global Geodetic Profile Resolution (arc/pixel) for given zoom level
	 * (measured at Equator)
	 * 
	 * @param zoom
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double resolutionG(final int zoom) {

		return 180 / (double) pixelsPerTile / Float11.pow(2, zoom);
	}

	/**
	 * Resolution (meters/pixel) for given zoom level (measured at Equator)
	 * 
	 * @param zoom
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double resolutionMercator(final int zoom) {
		return (2 * Math.PI * 6378137.0)
				/ ((double) pixelsPerTile * (double) (1 << zoom));
	}

	/**
	 * TMS
	 * 
	 * @param y
	 * @param z
	 * @return
	 */
	private static double tile2lat(final int y, final double resolution) {
		double n = Math.PI - ((2.0 * Math.PI * y) / (resolution));
		return 180.0 / Math.PI
				* Float11.atan(0.5 * (Float11.exp(n) - Float11.exp(-n)));
	}

	/**
	 * TMS
	 * 
	 * @param y
	 * @param z
	 * @return
	 */
	private static double tile2lat(final int y, final int z) {
		double n = Math.PI - ((2.0 * Math.PI * y) / Float11.pow(2.0, z));
		// Real r = new Real(String.valueOf(n));
		// r.exp();
		// double expN = Double.valueOf(r.toString()).doubleValue();
		//
		// r.assign(String.valueOf(-n));
		// r.exp();
		// double expmN = Double.valueOf(r.toString()).doubleValue();
		//
		// double atan = 0.5 * (expN - expmN);
		// r.assign(String.valueOf(atan));
		// r.atan();
		// double atanRes = Double.valueOf(r.toString()).doubleValue();
		// System.out.println(n);
		// System.out.println(Float11.exp(n));
		// System.out.println(Float11.exp(-n));
		// System.out.println(Float11.atan(0.5 * (Float11.exp(n) -
		// Float11.exp(-n))));
		return 180.0 / Math.PI
				* Float11.atan(0.5 * (Float11.exp(n) - Float11.exp(-n)));
		// return 180.0 / Math.PI * atanRes;
	}

	/**
	 * TMS
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	private static double tile2lon(final int x, final double resolution) {
		return (x / resolution * 360.0) - 180;
	}

	/**
	 * TMS
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	private static double tile2lon(final int x, final int z) {
		float zoom = 1 << z;
		return (x / zoom * 360.0) - 180;
	}

	/**
	 * Global Geodetic Profile Returns bounds of the given tile
	 * 
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public static Extent tileBoundsG(final int tx, final int ty,
			final double resolution) {

		final double tileSize = pixelsPerTile;
		double res = resolution;
		return new Extent(tx * tileSize * res - 180, ty * tileSize * res - 90,
				(tx + 1) * tileSize * res - 180, (ty + 1) * tileSize * res - 90);
	}

	/**
	 * Global Geodetic Profile Returns bounds of the given tile
	 * 
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public static Extent tileBoundsG(final int tx, final int ty, final int zoom) {

		final double tileSize = pixelsPerTile;
		double res = 180 / tileSize / Float11.pow(2, zoom);
		return new Extent(tx * tileSize * res - 180, ty * tileSize * res - 90,
				(tx + 1) * tileSize * res - 180, (ty + 1) * tileSize * res - 90);
	}

	public static Extent tileBoundsM(final int tx, final int ty,
			final double resolution) {
		Point leftBottom = pixelsToMercatorM(tx * pixelsPerTile, ty
				* pixelsPerTile, resolution);

		Point rightTop = pixelsToMercatorM((tx + 1) * pixelsPerTile, (ty + 1)
				* pixelsPerTile, resolution);
		return new Extent(leftBottom.getX(), leftBottom.getY(),
				rightTop.getX(), rightTop.getY());
	}

	public static Extent tileBoundsM(final int tx, final int ty, final int zoom) {
		Point leftBottom = pixelsToMercatorM(tx * pixelsPerTile, ty
				* pixelsPerTile, zoom);

		Point rightTop = pixelsToMercatorM((tx + 1) * pixelsPerTile, (ty + 1)
				* pixelsPerTile, zoom);
		return new Extent(leftBottom.getX(), leftBottom.getY(),
				rightTop.getX(), rightTop.getY());
	}

	public static Extent tileMeterBounds(final int tx, final int ty,
			final double resolution) {
		Point leftBottom = pixelsToMeters(tx * pixelsPerTile, ty
				* pixelsPerTile, resolution);

		Point rightTop = pixelsToMeters((tx + 1) * pixelsPerTile, (ty + 1)
				* pixelsPerTile, resolution);
		return new Extent(leftBottom, rightTop);
	}

	public static Extent tileMeterBounds(final int tx, final int ty,
			final double resolution, final double originX, final double originY) {
		Point leftBottom = pixelsToMeters(tx * pixelsPerTile, ty
				* pixelsPerTile, resolution, -originX, -originY);

		Point rightTop = pixelsToMeters((tx + 1) * pixelsPerTile, (ty + 1)
				* pixelsPerTile, resolution, -originX, -originY);
		return new Extent(leftBottom, rightTop);
	}

	/**
	 * Calculates the lon lat extent of a given tile and its zoom level. Use
	 * this method when querying OSM, Yahoo, Microsoft, Google, etc.
	 * 
	 * @param x
	 *            The x coordinate of the tile
	 * @param y
	 *            The y coordinate of the tile
	 * @param resolution
	 *            tile
	 * @return The Extent of the tile
	 */
	public static Extent tileOSMGeodeticBounds(final int x, final int y,
			final double resolution) {
		Extent bb = new Extent(tile2lon(x, resolution), tile2lat(y + 1,
				resolution), tile2lon(x + 1, resolution), tile2lat(y,
				resolution));
		return bb;
	}

	/**
	 * Calculates the lon lat extent of a given tile and its zoom level. Use
	 * this method when querying OSM, Yahoo, Microsoft, Google, etc.
	 * 
	 * @param x
	 *            The x coordinate of the tile
	 * @param y
	 *            The y coordinate of the tile
	 * @param zoom
	 *            The zoom level. Zoom level 0 is that in which the whole world
	 *            fills a single tile
	 * @return The Extent of the tile
	 */
	public static Extent tileOSMGeodeticBounds(final int x, final int y,
			final int zoom) {
		Extent bb = new Extent(tile2lon(x, zoom), tile2lat(y + 1, zoom),
				tile2lon(x + 1, zoom), tile2lat(y, zoom));
		return bb;
	}

	/**
	 * Returns bounds of the given tile in latutude/longitude using WGS84 datum
	 * 
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @deprecated use TileConversor.tileOSMGeodeticBounds instead
	 */
	@Deprecated
	public static Extent tileOSMLatLonBounds(final int tx, final int ty,
			final int zoom) {
		Extent bounds = tileOSMMercatorBounds(tx, ty, zoom);
		Point leftBottom = mercatorToLatLon(bounds.getMinX(), bounds.getMinY());
		Point rightTop = mercatorToLatLon(bounds.getMaxX(), bounds.getMaxY());

		return new Extent(leftBottom, rightTop);
	}

	/**
	 * Returns bounds of the given tile in EPSG:900913 coordinates. This method
	 * can be used to retrieve the tile extent from Google, OSM, Yahoo,
	 * Microsoft, etc.
	 * 
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public static Extent tileOSMMercatorBounds(final int tx, final int ty,
			final int zoom) {
		Extent extent = tileOSMGeodeticBounds(tx, ty, zoom);
		Point leftBottom = latLonToMercator(extent.getMinX(), extent.getMinY());
		Point rightTop = latLonToMercator(extent.getMaxX(), extent.getMaxY());
		extent.setLeftBottomCoordinate(leftBottom);
		extent.setRightTopCoordinate(rightTop);
		return extent;
		// Point leftBottom = pixelsToMercator(tx * pixelsPerTile, ty *
		// pixelsPerTile, zoom);
		//
		// Point rightTop = pixelsToMercator((tx + 1) * pixelsPerTile, (ty + 1)
		// * pixelsPerTile, zoom);
		// return new Extent(leftBottom.getX(), -rightTop.getY(),
		// rightTop.getX(), -leftBottom.getY());
	}

	/**
	 * Calculates the Microsoft's quadkey string of a tile.
	 * 
	 * @param tileX
	 *            The x coordinate of the tile
	 * @param tileY
	 *            The y coordinate of the tile
	 * @param levelOfDetail
	 *            The zoom level (starting at 0)
	 * @return The quadkey of the tile
	 */
	public static StringBuffer tileXYToQuadKey(int tileX, int tileY,
			int levelOfDetail) {
		StringBuffer quadKey = new StringBuffer();
		for (int i = levelOfDetail; i > 0; i--) {
			char digit = '0';
			int mask = 1 << (i - 1);
			if ((tileX & mask) != 0) {
				digit++;
			}
			if ((tileY & mask) != 0) {
				digit++;
				digit++;
			}
			quadKey.append(digit);
		}
		return quadKey;
	}
}
