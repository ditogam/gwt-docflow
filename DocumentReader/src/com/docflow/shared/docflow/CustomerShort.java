package com.docflow.shared.docflow;

import java.io.Serializable;

public class CustomerShort implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3621503159928871497L;

	private int cusid;
	private String cusname;
	private String region;
	private String raion;
	private long zone;
	private String cityname;
	private String streetname;
	private String home;
	private String flat;

	private int subregionid;
	private int regionid;
	private int streetid;
	private int cityid;

	public int getCusid() {
		return cusid;
	}

	public void setCusid(int cusid) {
		this.cusid = cusid;
	}

	public String getCusname() {
		return cusname;
	}

	public void setCusname(String cusname) {
		this.cusname = cusname;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRaion() {
		return raion;
	}

	public void setRaion(String raion) {
		this.raion = raion;
	}

	public long getZone() {
		return zone;
	}

	public void setZone(long zone) {
		this.zone = zone;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public String getStreetname() {
		return streetname;
	}

	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getFlat() {
		return flat;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}



	public int getSubregionid() {
		return subregionid;
	}

	public void setSubregionid(int subregionid) {
		this.subregionid = subregionid;
	}

	public int getRegionid() {
		return regionid;
	}

	public void setRegionid(int regionid) {
		this.regionid = regionid;
	}

	public int getStreetid() {
		return streetid;
	}

	public void setStreetid(int streetid) {
		this.streetid = streetid;
	}

	public int getCityid() {
		return cityid;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

}
