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
public class Utilities {

	private static int groupID = -10;

	// /**
	// * Checks if the Location API is supported into the current phone
	// *
	// * @return True if it is supported
	// */
	// public static boolean isLocationApiSupported() {
	// try {
	// Class.forName("javax.microedition.location.LocationProvider");
	// Class.forName("javax.microedition.location.Criteria");
	// return true;
	// } catch (final java.lang.NoClassDefFoundError c) {
	// return false;
	// } catch (Exception e) {
	// return false;
	// }
	// }
	//
	// /**
	// * Checks if the Location API is supported into the current phone
	// *
	// * @return True if it is supported
	// */
	// public static boolean isWebServiceApiSupported() {
	// try {
	// Class.forName("javax.microedition.xml.rpc.Operation");
	// return true;
	// } catch (final java.lang.NoClassDefFoundError c) {
	// return false;
	// } catch (final Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// /**
	// * Checks if the FileConnection API is supported into the current phone
	// *
	// * @return True if it is supported
	// */
	// public static boolean isFileConnectionApiSupported() {
	// try {
	// Class.forName("javax.microedition.io.file.FileConnection");
	// return true;
	// } catch (final java.lang.NoClassDefFoundError c) {
	// return false;
	// } catch (final Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// }

	public static String capitalizeFirstLetters(String s) {
		if (s == null)
			return "";

		for (int i = 0; i < s.length(); i++) {

			if (i == 0) {
				// Capitalize the first letter of the string.
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}

			// Is this character a non-letter or non-digit? If so
			// then this is probably a word boundary so let's capitalize
			// the next character in the sequence.
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}

		}

		return s;

	}

	/**
	 * @param ex
	 * @param srs
	 * @return false if the extent is null or its area is equals to 0
	 */
	public static boolean checkValidExtent(final Extent ex, final String srs) {

		if (ex == null) {
			return false;
		} else {
			// if(CRSFactory.getCRS(srs).getUnitsAbbrev().compareTo("u") == 0) {
			// return false;
			// }

			// if (srs.indexOf("EPSG:4") == 0 && srs.length() == 9) {
			// if (ex.getMinX() < -180 || ex.getMinX() > 180) {
			// return false;
			// }
			// if (ex.getMinY() < -90 || ex.getMinY() > 90) {
			// return false;
			// }
			// if (ex.getMaxX() < -180 || ex.getMaxX() > 180) {
			// return false;
			// }
			// if (ex.getMaxY() < -90 || ex.getMaxY() > 90) {
			// return false;
			// }
			// }

			if (ex.area() <= 0) {
				return false;
			}
		}
		return true;
	}

	// exchange a[i] and a[j]
	private static void exch(final Point[] a, final int i, final int j) {
		Point swap = a[i];

		a[i] = a[j];
		a[j] = swap;
	}

	/**
	 * Resturns a unique cancellable ID
	 * 
	 * @return
	 */
	@SuppressLint("UseValueOf")
	public static synchronized Integer getCancellableID() {
		return new Integer(groupID--);
	}

	/**
	 * time in seconds to hh::mm:ss
	 * 
	 * @param time
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getTimeHoursMinutesSecondsString(long time) {
		long elapsedTime = time;
		String formatted = String.format(Locale.getDefault(), "%%0%dd", 2);
		String seconds = String.format(formatted, elapsedTime % 60);
		String minutes = String.format(formatted, (elapsedTime % 3600) / 60);
		String hours = String.format(formatted, elapsedTime / 3600);
		return hours + ":" + minutes + ":" + seconds;
	}

	/**
	 * @param string
	 * @return True if the string is empty or null
	 */
	public final static boolean isEmpty(final String string) {
		return (string == null || string.trim().compareTo("") == 0);

	}

	// is x < y ?
	private static boolean less(final double x, final double y) {
		return (x < y);
	}

	/**
	 * Parses the description of a named instance of a Name Finder response. The
	 * description contains tags such as "<strong>" that this method deletes
	 * 
	 * @param description
	 *            The description
	 * @return A clean description to show on the screen
	 */
	public static String parseNamedDescription(final String description) {

		StringBuffer res = null;
		try {
			if (!Utilities.isEmpty(description)) {
				final StringBuffer s = new StringBuffer(description);
				final int size = s.length();
				int ch;
				boolean skip = false;
				res = new StringBuffer();
				for (int i = 0; i < size; i++) {
					ch = s.charAt(i);

					if (ch == 60 || ch == 91) {
						skip = true;
					}
					if (!skip) {
						res.append(s.charAt(i));
					}
					if (ch == 62 || ch == 93) {
						skip = false;
					}
				}
			}
			return res.toString();
		} catch (final Exception e) {
			return null;
		}
	}

	// partition a[left] to a[right], assumes left < right
	private static int partition(final Point[] a, final int left,
			final int right, final Point center) {
		int i = left - 1;
		int j = right;
		while (true) {

			while (less(a[++i].distance(center), a[right].distance(center)))
				// find item on left to swap
				; // a[right] acts as sentinel
			while (less(a[right].distance(center), a[right].distance(center)))
				// find item on right to swap
				if (j == left)
					break; // don't go out-of-bounds
			if (i >= j)
				break; // check if pointers cross
			exch(a, i, j); // swap two elements into place
		}
		exch(a, i, right); // swap with partition element
		return i;
	}

	// quicksort a[left] to a[right]
	private static void quicksort(final Point[] a, final int left,
			final int right, final Point center) {
		if (right <= left)
			return;
		int i = partition(a, left, right, center);
		quicksort(a, left, i - 1, center);
		quicksort(a, i + 1, right, center);
	}

	/**
	 * Orders an array of points with the quicksort algorithm
	 * 
	 * @param a
	 *            The array of points to be ordered
	 * @param center
	 *            The point to compare with. The array will be sorted from
	 *            nearest to the center to furthest
	 */
	public static void quicksort(final Point[] a, final Point center) {
		quicksort(a, 0, a.length - 1, center);
	}

	public static String trimDecimals(final String number,
			final int numberDecimals) {
		try {
			final int length = number.length();
			for (int i = 0; i < length; i++) {
				if (String.valueOf(number.charAt(i)).compareTo(".") == 0) {
					return number.substring(0, i + numberDecimals + 1);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return number;
	}
}
