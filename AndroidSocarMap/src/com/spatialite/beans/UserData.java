package com.spatialite.beans;

import org.osmdroid.util.GeoPoint;

public class UserData {
	private String username;
	private int userid;
	private int pcity;
	private int ppcity;
	private GeoPoint boundsTopLeftCorner;
	private GeoPoint boundsBottomLeftCorner;
	private GeoPoint center;
	
	
	public UserData() {
		// TODO Auto-generated constructor stub
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public int getUserid() {
		return userid;
	}


	public void setUserid(int userid) {
		this.userid = userid;
	}


	public int getPcity() {
		return pcity;
	}


	public void setPcity(int pcity) {
		this.pcity = pcity;
	}


	public int getPpcity() {
		return ppcity;
	}


	public void setPpcity(int ppcity) {
		this.ppcity = ppcity;
	}


	public GeoPoint getBoundsTopLeftCorner() {
		return boundsTopLeftCorner;
	}


	public void setBoundsTopLeftCorner(GeoPoint boundsTopLeftCorner) {
		this.boundsTopLeftCorner = boundsTopLeftCorner;
	}


	public GeoPoint getBoundsBottomLeftCorner() {
		return boundsBottomLeftCorner;
	}


	public void setBoundsBottomLeftCorner(GeoPoint boundsBottomLeftCorner) {
		this.boundsBottomLeftCorner = boundsBottomLeftCorner;
	}


	public GeoPoint getCenter() {
		return center;
	}


	public void setCenter(GeoPoint center) {
		this.center = center;
	}
	
	
}
