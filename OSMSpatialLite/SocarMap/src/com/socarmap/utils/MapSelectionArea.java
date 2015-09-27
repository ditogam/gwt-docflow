package com.socarmap.utils;

import java.text.DecimalFormat;

public class MapSelectionArea {

	private double lat1;
	private double lon1;
	private double lat2;
	private double lon2;

	public MapSelectionArea(double lon1, double lat1, double lon2, double lat2) {
		this.lat1 = lat1;
		this.lon1 = lon1;
		this.lat2 = lat2;
		this.lon2 = lon2;
	}

	public double getLat1() {
		return lat1;
	}

	public double getLat2() {
		return lat2;
	}

	public double getLon1() {
		return lon1;
	}

	public double getLon2() {
		return lon2;
	}

	@Override
	public String toString() {
		DecimalFormat df2 = new DecimalFormat(
				"#######################0.0#######################");
		return df2.format(lon1) + "," + df2.format(lat1) + ","
				+ df2.format(lon2) + "," + df2.format(lat2);
	}

}
