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
package org.oscim.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A BoundingBox represents an immutable set of two latitude and two longitude
 * coordinates.
 */
public class BoundingBox implements Parcelable {
	/**
	 * Conversion factor from degrees to microdegrees.
	 */
	private static final double CONVERSION_FACTOR = 1000000d;

	public static BoundingBox fromGeoPoints(
			final List<? extends GeoPoint> partialPolyLine) {
		int minLat = Integer.MAX_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int maxLon = Integer.MIN_VALUE;
		for (final GeoPoint gp : partialPolyLine) {

			minLat = Math.min(minLat, gp.latitudeE6);
			minLon = Math.min(minLon, gp.longitudeE6);
			maxLat = Math.max(maxLat, gp.latitudeE6);
			maxLon = Math.max(maxLon, gp.longitudeE6);
		}

		return new BoundingBox(minLat, minLon, maxLat, maxLon);
	}

	public static boolean isBetween(int number, int min, int max) {
		return min <= number && number <= max;
	}

	/**
	 * The maximum latitude value of this BoundingBox in microdegrees (degrees *
	 * 10^6).
	 */
	public final int maxLatitudeE6;

	/**
	 * The maximum longitude value of this BoundingBox in microdegrees (degrees
	 * * 10^6).
	 */
	public final int maxLongitudeE6;

	/**
	 * The minimum latitude value of this BoundingBox in microdegrees (degrees *
	 * 10^6).
	 */
	public final int minLatitudeE6;

	/**
	 * The minimum longitude value of this BoundingBox in microdegrees (degrees
	 * * 10^6).
	 */
	public final int minLongitudeE6;

	/**
	 * The hash code of this object.
	 */
	private transient int hashCodeValue;

	public static final Parcelable.Creator<BoundingBox> CREATOR = new Parcelable.Creator<BoundingBox>() {
		@Override
		public BoundingBox createFromParcel(final Parcel in) {
			return new BoundingBox(in.readInt(), in.readInt(), in.readInt(),
					in.readInt());
		}

		@Override
		public BoundingBox[] newArray(final int size) {
			return new BoundingBox[size];
		}
	};

	public BoundingBox(double minLatitude, double minLongitude,
			double maxLatitude, double maxLongitude) {
		this.minLatitudeE6 = (int) (minLatitude * 1E6);
		this.minLongitudeE6 = (int) (minLongitude * 1E6);
		this.maxLatitudeE6 = (int) (maxLatitude * 1E6);
		this.maxLongitudeE6 = (int) (maxLongitude * 1E6);
		this.hashCodeValue = calculateHashCode();
	}

	/**
	 * @param minLatitudeE6
	 *            the minimum latitude in microdegrees (degrees * 10^6).
	 * @param minLongitudeE6
	 *            the minimum longitude in microdegrees (degrees * 10^6).
	 * @param maxLatitudeE6
	 *            the maximum latitude in microdegrees (degrees * 10^6).
	 * @param maxLongitudeE6
	 *            the maximum longitude in microdegrees (degrees * 10^6).
	 */
	public BoundingBox(int minLatitudeE6, int minLongitudeE6,
			int maxLatitudeE6, int maxLongitudeE6) {
		this.minLatitudeE6 = minLatitudeE6;
		this.minLongitudeE6 = minLongitudeE6;
		this.maxLatitudeE6 = maxLatitudeE6;
		this.maxLongitudeE6 = maxLongitudeE6;
		this.hashCodeValue = calculateHashCode();
	}

	/**
	 * @return the hash code of this object.
	 */
	private int calculateHashCode() {
		int result = 7;
		result = 31 * result + maxLatitudeE6;
		result = 31 * result + maxLongitudeE6;
		result = 31 * result + minLatitudeE6;
		result = 31 * result + minLongitudeE6;
		return result;
	}

	/**
	 * @param geoPoint
	 *            the point whose coordinates should be checked.
	 * @return true if this BoundingBox contains the given GeoPoint, false
	 *         otherwise.
	 */
	public boolean contains(GeoPoint geoPoint) {
		return geoPoint.latitudeE6 <= maxLatitudeE6
				&& geoPoint.latitudeE6 >= minLatitudeE6
				&& geoPoint.longitudeE6 <= maxLongitudeE6
				&& geoPoint.longitudeE6 >= minLongitudeE6;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof BoundingBox)) {
			return false;
		}
		BoundingBox other = (BoundingBox) obj;
		if (maxLatitudeE6 != other.maxLatitudeE6) {
			return false;
		} else if (maxLongitudeE6 != other.maxLongitudeE6) {
			return false;
		} else if (minLatitudeE6 != other.minLatitudeE6) {
			return false;
		} else if (minLongitudeE6 != other.minLongitudeE6) {
			return false;
		}
		return true;
	}

	/**
	 * @return the GeoPoint at the horizontal and vertical center of this
	 *         BoundingBox.
	 */
	public GeoPoint getCenterPoint() {
		int latitudeOffset = (maxLatitudeE6 - minLatitudeE6) / 2;
		int longitudeOffset = (maxLongitudeE6 - minLongitudeE6) / 2;
		return new GeoPoint(minLatitudeE6 + latitudeOffset, minLongitudeE6
				+ longitudeOffset);
	}

	/**
	 * @return the maximum latitude value of this BoundingBox in degrees.
	 */
	public double getMaxLatitude() {
		return maxLatitudeE6 / CONVERSION_FACTOR;
	}

	/**
	 * @return the maximum longitude value of this BoundingBox in degrees.
	 */
	public double getMaxLongitude() {
		return maxLongitudeE6 / CONVERSION_FACTOR;
	}

	/**
	 * @return the minimum latitude value of this BoundingBox in degrees.
	 */
	public double getMinLatitude() {
		return minLatitudeE6 / CONVERSION_FACTOR;
	}

	/**
	 * @return the minimum longitude value of this BoundingBox in degrees.
	 */
	public double getMinLongitude() {
		return minLongitudeE6 / CONVERSION_FACTOR;
	}

	/* code below is from osdmroid, @author Nicolas Gramlich */

	@Override
	public int hashCode() {
		return hashCodeValue;
	}

	private void readObject(ObjectInputStream objectInputStream)
			throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		this.hashCodeValue = calculateHashCode();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("BoundingBox [minLat=");
		stringBuilder.append(minLatitudeE6);
		stringBuilder.append(", minLon=");
		stringBuilder.append(minLongitudeE6);
		stringBuilder.append(", maxLat=");
		stringBuilder.append(maxLatitudeE6);
		stringBuilder.append(", maxLon=");
		stringBuilder.append(maxLongitudeE6);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(minLatitudeE6);
		dest.writeInt(minLongitudeE6);
		dest.writeInt(maxLatitudeE6);
		dest.writeInt(maxLongitudeE6);
	}

	// public BoundingBox(final double north, final double east, final double
	// south,
	// final double west) {
	// this((int) (north * 1E6), (int) (east * 1E6), (int) (south * 1E6), (int)
	// (west * 1E6));
	// // this.mLatNorthE6 = (int) (north * 1E6);
	// // this.mLonEastE6 = (int) (east * 1E6);
	// // this.mLatSouthE6 = (int) (south * 1E6);
	// // this.mLonWestE6 = (int) (west * 1E6);
	// }

	// public int getLatNorthE6() {
	// return this.maxLatitudeE6;
	// }
	//
	// public int getLatSouthE6() {
	// return this.minLatitudeE6;
	// }
	//
	// public int getLonEastE6() {
	// return this.maxLongitudeE6;
	// }
	//
	// public int getLonWestE6() {
	// return this.minLongitudeE6;
	// }

}
