package com.socarmap.proxy.beans;

import java.io.Serializable;

public class UserData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -172836892739359565L;
	public static UserData compare(UserData main, UserData helper) {
		int last_center_x = helper.getCenter().getLongitudeE6();
		int last_center_y = helper.getCenter().getLatitudeE6();
		MGeoPoint boundsTopLeftCorner = main.boundsTopLeftCorner;
		MGeoPoint boundsBottomLeftCorner = main.boundsBottomLeftCorner;

		if (last_center_x >= boundsTopLeftCorner.getLongitudeE6()
				&& last_center_x <= boundsBottomLeftCorner.getLongitudeE6()
				&& last_center_y >= boundsTopLeftCorner.getLatitudeE6()
				&& last_center_y <= boundsBottomLeftCorner.getLatitudeE6()) {
			main.setCenter(helper.getCenter());
			main.zoom = helper.zoom;
		}

		return main;
	}
	private String username;
	private int userid;
	private int pcity;
	private int ppcity;
	private int zoom;
	private MGeoPoint boundsTopLeftCorner;
	private MGeoPoint boundsBottomLeftCorner;

	private MGeoPoint center;

	public UserData() {
		// TODO Auto-generated constructor stub
	}

	public MGeoPoint getBoundsBottomLeftCorner() {
		return boundsBottomLeftCorner;
	}

	public MGeoPoint getBoundsTopLeftCorner() {
		return boundsTopLeftCorner;
	}

	public MGeoPoint getCenter() {
		return center;
	}

	public int getPcity() {
		return pcity;
	}

	public int getPpcity() {
		return ppcity;
	}

	public int getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public int getZoom() {
		return zoom;
	}

	public void setBoundsBottomLeftCorner(MGeoPoint boundsBottomLeftCorner) {
		this.boundsBottomLeftCorner = boundsBottomLeftCorner;
	}

	public void setBoundsTopLeftCorner(MGeoPoint boundsTopLeftCorner) {
		this.boundsTopLeftCorner = boundsTopLeftCorner;
	}

	public void setCenter(MGeoPoint center) {
		this.center = center;
	}

	public void setPcity(int pcity) {
		this.pcity = pcity;
	}

	public void setPpcity(int ppcity) {
		this.ppcity = ppcity;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

}
