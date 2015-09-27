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

import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class GeoUtils {

	// ---------------------------------------------------
	/**
	 * Useful constants
	 */
	public static final double SQ_METERS_LIMIT = 1000000;
	/**
	 * Useful constants
	 */
	public static double HalfPi = 1.5707963267948966192313;
	/**
	 * Useful constants
	 */
	public static double Degree = 57.295779513082320876798; /*
															 * degrees per
															 * radian
															 */

	/**
	 * Useful constants
	 */
	public static double SqMi = 273218.4; /* Square mi per spherical degree. */

	/**
	 * Useful constants
	 */
	public static double SqKm = 707632.4; /* Square km per spherical degree. */

	/**
	 * Useful constants
	 */
	public static double SqM = 707632400000.0; /*
												 * Square M per spherical
												 * degree.
												 */

	// @SuppressWarnings("unused")
	// private static Elipsoide getEllipsoid(String srs) {
	// if ((srs.toUpperCase(Locale.getDefault()).indexOf("EPSG:230") == 0)
	// && (srs.length() == 10)) {
	// return Elipsoide.getED50();
	// }
	// return Elipsoide.getWGS84();
	// }

	/**
	 * Known projection IDs:
	 * 
	 * Geo WGS84, Geo ED50 + UTM - ED50 - zone 27N - 39N + UTM - WGS84 - zone 1N
	 * - 60N + UTM - WGS84 - zone 1S - 60S
	 * 
	 */
	public static String[] CRS_CODE_KNOWN = null;

	static {

		CRS_CODE_KNOWN = new String[135];
		CRS_CODE_KNOWN[0] = "EPSG:4326";
		CRS_CODE_KNOWN[1] = "EPSG:4230";
		for (int i = 27; i <= 39; i++) {
			CRS_CODE_KNOWN[i - 25] = "EPSG:230" + inTwoDigits(i);
		}
		for (int i = 1; i <= 60; i++) {
			CRS_CODE_KNOWN[i + 14] = "EPSG:326" + inTwoDigits(i);
		}
		for (int i = 1; i <= 60; i++) {
			CRS_CODE_KNOWN[i + 74] = "EPSG:327" + inTwoDigits(i);
		}
	}

	/**
	 * Gets the Euclidean distance between points A and B.
	 * 
	 * @param x1
	 *            x coordinate of point A
	 * @param y1
	 *            y coordinate of point A
	 * @param x2
	 *            x coordinate of point B
	 * @param y2
	 *            y coordinate of point B
	 * @return the Euclidean distance between points A and B.
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static boolean equals(final String projFrom, final String projTo) {
		if (projFrom.compareTo("EPSG:4326") == 0
				&& projTo.compareTo("EPSG:4258") == 0) {
			return true;
		}

		if (projTo.compareTo("EPSG:4326") == 0
				&& projFrom.compareTo("EPSG:4258") == 0) {
			return true;
		}

		if (projTo.compareTo(projFrom) == 0) {
			return true;
		}

		return false;
	}

	/**
	 * This method computes the scale of a map.
	 * 
	 * @param p
	 *            the projection of the map
	 * @param distance_in_proj_units
	 *            sample distance in projection units
	 * @param distance_in_map_in_meters
	 *            true distance of the sample in meters
	 * @return
	 */
	public static double getScale(Projection p, double distance_in_proj_units,
			double distance_in_map_in_meters) {

		double meters_per_proj_unit = p.getMetersPerProjUnit();
		return (distance_in_proj_units * meters_per_proj_unit)
				/ distance_in_map_in_meters;
	}

	/**
	 * Utility method to get the zone of a UTM projection.
	 * 
	 * @param p
	 *            The projection of interest
	 * @return the zone of the UTM projection, or -1 if it's not a known UTM
	 *         projection
	 */
	public static int getZone(Projection p) {
		if (isInED50Meters(p) || isInWGS84Meters(p)) {
			String srs = p.getAbrev();
			try {
				String zone_2 = srs.substring(8, 9);
				String zone_3 = srs.substring(9, 10);
				int izone_2 = Integer.parseInt(zone_2);
				int izone_3 = Integer.parseInt(zone_3);
				return 10 * izone_2 + izone_3;
			} catch (final Exception ex) {
				ex.printStackTrace();
				System.out
						.println("Unexpected Abrev format! Returned zone = -1");
				return -1;
			}
		} else {
			System.out.println("Not a EPSG:23XXX SRS! Returned zone = -1");
			return -1;
		}
	}

	@SuppressWarnings("unused")
	private static double hav(double X) {
		return (1.0 - Math.cos(X)) / 2.0;
	}

	public static String inTwoDigits(int n) {
		return "" + ((n / 10) % 10) + "" + (n % 10);
	}

	/**
	 * Utility method to find out if the given projection is in meters and in
	 * ED50.
	 * 
	 * @param p
	 *            the projection of interest
	 * @return whether the given projection is in meters and in ED50.
	 */
	public static boolean isInED50Meters(Projection p) {

		String srs = p.getAbrev();
		if ((srs.toUpperCase(Locale.getDefault()).indexOf("EPSG:230") == 0)
				&& (srs.length() == 10)) {
			return true;
		}
		return false;
	}

	/**
	 * Utility method to find out if the given projection is in meters and in
	 * ED50.
	 * 
	 * @param p
	 *            the projection of interest
	 * @return whether the given projection is in meters and in ED50.
	 */
	public static boolean isInWGS84Meters(Projection p) {
		String srs = p.getAbrev();
		if ((srs.toUpperCase().indexOf("EPSG:326") == 0)
				&& (srs.length() == 10)) {
			return true;
		}
		if ((srs.toUpperCase().indexOf("EPSG:327") == 0)
				&& (srs.length() == 10)) {
			return true;
		}
		return false;
	}

	public static boolean isLatLonProjection(String abbrev) {

		if ((abbrev.indexOf("EPSG:4") == 0) && (abbrev.length() == 9)) {
			return true;
		}
		return false;

	}

	public static boolean isMercatorProjection(String projName) {
		try {
			if (projName == null || projName.trim().compareTo("") == 0) {
				return false;
			}

			if (projName.indexOf("EPSG:3785") == 0
					|| projName.indexOf("EPSG:900913") == 0
					|| projName.indexOf("OSGEO:41001") == 0) {
				return true;
			}
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Utility method to find out if a given projection is a UTM projectiion in
	 * meters or not
	 * 
	 * @param srs
	 *            the projection abbreviation
	 * @return if a given projection is in meters or not
	 */
	@SuppressWarnings("unused")
	private static boolean srsInMeters(String srs) {

		if ((srs.toUpperCase().indexOf("EPSG:23") == 0) && (srs.length() == 10)) {
			return true;
		}
		if ((srs.toUpperCase().indexOf("EPSG:32") == 0) && (srs.length() == 10)) {
			return true;
		}
		return false;
	}
}
