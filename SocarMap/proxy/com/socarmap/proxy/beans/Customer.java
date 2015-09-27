package com.socarmap.proxy.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4070165003443761334L;
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
	private String cusstatusname;
	private String custypename;

	private ArrayList<Balance> balances;
	private ArrayList<Meter> meters;

	public String getAddress() {
		return streetname + " " + home + " " + flat;

	}

	public ArrayList<Balance> getBalances() {
		return balances;
	}

	public String getCityname() {
		return cityname;
	}

	public int getCusid() {
		return cusid;
	}

	public String getCusname() {
		return cusname;
	}

	public String getCusstatusname() {
		return cusstatusname;
	}

	public String getCustypename() {
		return custypename;
	}

	public String getFlat() {
		return flat;
	}

	public String getHome() {
		return home;
	}

	public ArrayList<Meter> getMeters() {
		return meters;
	}

	public String getRaion() {
		return raion;
	}

	public String getRegion() {
		return region;
	}

	public String getScopename() {
		return scopename;
	}

	public String getStreetname() {
		return streetname;
	}

	public long getZone() {
		return zone;
	}

	public void setBalances(ArrayList<Balance> balances) {
		this.balances = balances;
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

	public void setCusstatusname(String cusstatusname) {
		this.cusstatusname = cusstatusname;
	}

	public void setCustypename(String custypename) {
		this.custypename = custypename;
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public void setMeters(ArrayList<Meter> meters) {
		this.meters = meters;
	}

	public void setRaion(String raion) {
		this.raion = raion;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setScopename(String scopename) {
		this.scopename = scopename;
	}

	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}

	public void setZone(long zone) {
		this.zone = zone;
	}

}
