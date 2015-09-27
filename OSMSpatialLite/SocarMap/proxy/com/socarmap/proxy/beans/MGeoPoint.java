package com.socarmap.proxy.beans;

import java.io.Serializable;

public class MGeoPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 538410612592038837L;
	public int mLongitudeE6;
	public int mLatitudeE6;
	public int mAltitude;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MGeoPoint(final double aLatitude, final double aLongitude) {
		this.mLatitudeE6 = (int) (aLatitude * 1E6);
		this.mLongitudeE6 = (int) (aLongitude * 1E6);
	}

	public MGeoPoint(final double aLatitude, final double aLongitude,
			final double aAltitude) {
		this.mLatitudeE6 = (int) (aLatitude * 1E6);
		this.mLongitudeE6 = (int) (aLongitude * 1E6);
		this.mAltitude = (int) aAltitude;
	}

	public MGeoPoint(final int aLatitudeE6, final int aLongitudeE6) {
		this.mLatitudeE6 = aLatitudeE6;
		this.mLongitudeE6 = aLongitudeE6;
	}

	public MGeoPoint(final int aLatitudeE6, final int aLongitudeE6,
			final int aAltitude) {
		this.mLatitudeE6 = aLatitudeE6;
		this.mLongitudeE6 = aLongitudeE6;
		this.mAltitude = aAltitude;
	}

	public MGeoPoint(final MGeoPoint aGeopoint) {
		this.mLatitudeE6 = aGeopoint.mLatitudeE6;
		this.mLongitudeE6 = aGeopoint.mLongitudeE6;
		this.mAltitude = aGeopoint.mAltitude;
	}

	public int getAltitude() {
		return this.mAltitude;
	}

	public int getLatitudeE6() {
		return this.mLatitudeE6;
	}

	public int getLongitudeE6() {
		return this.mLongitudeE6;
	}

	public void setAltitude(int aAltitude) {
		this.mAltitude = aAltitude;
	}

	public void setCoordsE6(final int aLatitudeE6, final int aLongitudeE6) {
		this.mLatitudeE6 = aLatitudeE6;
		this.mLongitudeE6 = aLongitudeE6;
	}

	public void setLatitudeE6(final int aLatitudeE6) {
		this.mLatitudeE6 = aLatitudeE6;
	}

	public void setLongitudeE6(final int aLongitudeE6) {
		this.mLongitudeE6 = aLongitudeE6;
	}
}
