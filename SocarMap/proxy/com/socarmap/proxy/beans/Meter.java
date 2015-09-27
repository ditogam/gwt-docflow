package com.socarmap.proxy.beans;

import java.io.Serializable;

public class Meter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8274393468573424772L;
	private int cusid;
	private int meterid;
	private String mtypename;
	private String metserial;
	private String start_index;
	private String mstatus;
	private double last_value;

	public Meter() {
		// TODO Auto-generated constructor stub
	}

	public Meter(int meterid, int cusid, String mtypename, String metserial,
			String start_index, String mstatus, double last_value) {
		this.cusid = cusid;
		this.meterid = meterid;
		this.mtypename = mtypename;
		this.metserial = metserial;
		this.start_index = start_index;
		this.mstatus = mstatus;
		this.last_value = last_value;
	}

	public int getCusid() {
		return cusid;
	}

	public double getLast_value() {
		return last_value;
	}

	public int getMeterid() {
		return meterid;
	}

	public String getMetserial() {
		return metserial;
	}

	public String getMstatus() {
		return mstatus;
	}

	public String getMtypename() {
		return mtypename;
	}

	public String getStart_index() {
		return start_index;
	}

	public void setCusid(int cusid) {
		this.cusid = cusid;
	}

	public void setLast_value(double last_value) {
		this.last_value = last_value;
	}

	public void setMeterid(int meterid) {
		this.meterid = meterid;
	}

	public void setMetserial(String metserial) {
		this.metserial = metserial;
	}

	public void setMstatus(String mstatus) {
		this.mstatus = mstatus;
	}

	public void setMtypename(String mtypename) {
		this.mtypename = mtypename;
	}

	public void setStart_index(String start_index) {
		this.start_index = start_index;
	}

	@Override
	public String toString() {
		return mtypename + " " + metserial + " " + start_index + " (" + mstatus
				+ ")" + " " + last_value;
	}

}
