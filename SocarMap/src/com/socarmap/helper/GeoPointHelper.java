package com.socarmap.helper;

import org.oscim.core.GeoPoint;

import com.socarmap.proxy.beans.MGeoPoint;

public class GeoPointHelper {
	public static GeoPoint toGeoPoint(MGeoPoint point) {
		if (point == null)
			return null;
		return new GeoPoint(point.mLatitudeE6, point.mLongitudeE6);
	}

	public static MGeoPoint toMGeoPoint(GeoPoint point) {
		if (point == null)
			return null;
		return new MGeoPoint(point.latitudeE6, point.longitudeE6);
	}

	public static MGeoPoint toMGeoPoint(int p_x, int p_y) {
		return new MGeoPoint(p_y, p_x);
	}
}
