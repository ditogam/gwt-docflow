package com.spatialite.utilities;

import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class MyExtendedOverlayItem extends ExtendedOverlayItem {

	public MyExtendedOverlayItem(String aTitle, String aDescription,
			GeoPoint aGeoPoint, Context context) {
		super(aTitle, aDescription, aGeoPoint, context);
	}

}
