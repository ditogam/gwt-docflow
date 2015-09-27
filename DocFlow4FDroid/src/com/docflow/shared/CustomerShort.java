package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CustomerShort implements IsSerializable {

	/**
	 * 
	 */

	private int cusid;
	private String cusname;
	private String region;
	private String raion;
	private long zone;
	private String cityname;
	private String streetname;
	private String home;
	private String flat;
	private String scopename;
	private String loan;

	private int subregionid;
	private int regionid;
	private int streetid;
	private int cityid;
	private String phone;

	public String getCityname() {
		return cityname;
	}

	public int getCusid() {
		return cusid;
	}

	public String getCusname() {
		return cusname;
	}

	public String getFlat() {
		return flat;
	}

	public String getHome() {
		return home;
	}

	public String getLoan() {
		return loan;
	}

	public String getRaion() {
		return raion;
	}

	public String getRegion() {
		return region;
	}

	public int getRegionid() {
		return regionid;
	}

	public String getScopename() {
		return scopename;
	}

	public int getStreetid() {
		return streetid;
	}

	public String getStreetname() {
		return streetname;
	}

	public int getSubregionid() {
		return subregionid;
	}

	public long getZone() {
		return zone;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public void setCusid(int cusid) {
		this.cusid = cusid;
	}

	public void setCusname(String cusname) {
		this.cusname = cusname;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public void setLoan(String loan) {
		this.loan = loan;
	}

	public void setRaion(String raion) {
		this.raion = raion;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setRegionid(int regionid) {
		this.regionid = regionid;
	}

	public void setScopename(String scopename) {
		this.scopename = scopename;
	}

	public void setStreetid(int streetid) {
		this.streetid = streetid;
	}

	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}

	public void setSubregionid(int subregionid) {
		this.subregionid = subregionid;
	}

	public void setZone(long zone) {
		this.zone = zone;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

	public int getCityid() {
		return cityid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
