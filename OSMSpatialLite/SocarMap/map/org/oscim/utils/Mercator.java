package org.oscim.utils;

import org.oscim.core.GeoPoint;

/**
 * http://wiki.openstreetmap.org/index.php/Mercator
 * http://developers.cloudmade.com
 * /projects/tiles/examples/convert-coordinates-to-tile-numbers
 * 
 * @author Nicolas Gramlich
 * 
 */
public class Mercator {
	// ===========================================================
	// Constants
	// ===========================================================

	final static double DEG2RAD = Math.PI / 180;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * This is a utility class with only static members.
	 */
	private Mercator() {
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Mercator projection of GeoPoint at given zoom level
	 * 
	 * @param aLat
	 *            latitude in degrees [-89000000 to 89000000]
	 * @param aLon
	 *            longitude in degrees [-180000000 to 180000000]
	 * @param zoom
	 *            zoom level
	 * @param aUseAsReturnValue
	 * @return Point with x,y in the range [-2^(zoom-1) to 2^(zoom-1)]
	 */
	public static int[] projectGeoPoint(final int aLatE6, final int aLonE6,
			final int aZoom, final int[] reuse) {
		return projectGeoPoint(aLatE6 * 1E-6, aLonE6 * 1E-6, aZoom, reuse);
	}

	public static final int MAPTILE_LATITUDE_INDEX = 0;
	public static final int MAPTILE_LONGITUDE_INDEX = 1;

	public static final int OpenSpaceUpperBoundArray[] = { 2, 5, 10, 25, 50,
			100, 200, 500, 1000, 2000, 4000 };

	/**
	 * Mercator projection of GeoPoint at given zoom level
	 * 
	 * @param aLat
	 *            latitude in degrees [-89 to 89]
	 * @param aLon
	 *            longitude in degrees [-180 to 180]
	 * @param zoom
	 *            zoom level
	 * @param aUseAsReturnValue
	 * @return Point with x,y in the range [-2^(zoom-1) to 2^(zoom-1)]
	 */
	public static int[] projectGeoPoint(final double aLat, final double aLon,
			final int aZoom, final int[] aUseAsReturnValue) {
		final int[] out = (aUseAsReturnValue != null) ? aUseAsReturnValue
				: new int[2];

		out[MAPTILE_LONGITUDE_INDEX] = (int) Math.floor((aLon + 180) / 360
				* (1 << aZoom));
		out[MAPTILE_LATITUDE_INDEX] = (int) Math.floor((1 - Math.log(Math
				.tan(aLat * DEG2RAD) + 1 / Math.cos(aLat * DEG2RAD))
				/ Math.PI)
				/ 2 * (1 << aZoom));

		return out;
	}

	/**
	 * Reverse Mercator projection of Point at given zoom level
	 * 
	 */
	public static GeoPoint projectPoint(int x, int y, int aZoom) {
		return new GeoPoint((int) (tile2lat(y, aZoom) * 1E6), (int) (tile2lon(
				x, aZoom) * 1E6));
	}

	public static double tile2lon(int x, int aZoom) {
		return ((double) x / (1 << aZoom) * 360.0) - 180;
	}

	public static double tile2lat(int y, int aZoom) {
		final double n = Math.PI - ((2.0 * Math.PI * y) / (1 << aZoom));
		return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}