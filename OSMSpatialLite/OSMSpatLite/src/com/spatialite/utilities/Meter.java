package com.spatialite.utilities;

public class Meter {
	private int cusid;
	private int meterid;
	private String mtypename;
	private String metserial;
	private String start_index;
	private String mstatus;

	public Meter(int meterid, int cusid, String mtypename, String metserial,
			String start_index, String mstatus) {
		this.cusid = cusid;
		this.meterid = meterid;
		this.mtypename = mtypename;
		this.metserial = metserial;
		this.start_index = start_index;
		this.mstatus = mstatus;
	}

	public int getCusid() {
		return cusid;
	}

	public int getMeterid() {
		return meterid;
	}

	public String getMtypename() {
		return mtypename;
	}

	public String getMetserial() {
		return metserial;
	}

	public String getStart_index() {
		return start_index;
	}

	public String getMstatus() {
		return mstatus;
	}

	@Override
	public String toString() {
		return mtypename + " " + metserial + " " + start_index + " (" + mstatus
				+ ")";
	}

}
