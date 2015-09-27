package com.docflow.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Meter implements IsSerializable {

	/**
	 * 
	 */
	private int cusid;
	private int mtypeid;
	private long start_index;
	private long meterid;
	private String metserial;
	private long regdate;
	private short mstatusid;
	private double mettervalue;
	private ArrayList<MeterPlombs> meterPlombs;

	public int getCusid() {
		return cusid;
	}

	public long getMeterid() {
		return meterid;
	}

	public ArrayList<MeterPlombs> getMeterPlombs() {
		return meterPlombs;
	}

	public String getMetserial() {
		return metserial;
	}

	public double getMettervalue() {
		return mettervalue;
	}

	public short getMstatusid() {
		return mstatusid;
	}

	public int getMtypeid() {
		return mtypeid;
	}

	public long getRegdate() {
		return regdate;
	}

	public long getStart_index() {
		return start_index;
	}

	public void setCusid(int cusid) {
		this.cusid = cusid;
	}

	public void setMeterid(long meterid) {
		this.meterid = meterid;
	}

	public void setMeterPlombs(ArrayList<MeterPlombs> meterPlombs) {
		this.meterPlombs = meterPlombs;
	}

	public void setMetserial(String metserial) {
		this.metserial = metserial;
	}

	public void setMettervalue(double mettervalue) {
		this.mettervalue = mettervalue;
	}

	public void setMstatusid(short mstatusid) {
		this.mstatusid = mstatusid;
	}

	public void setMtypeid(int mtypeid) {
		this.mtypeid = mtypeid;
	}

	public void setRegdate(long regdate) {
		this.regdate = regdate;
	}

	public void setStart_index(long start_index) {
		this.start_index = start_index;
	}

}
