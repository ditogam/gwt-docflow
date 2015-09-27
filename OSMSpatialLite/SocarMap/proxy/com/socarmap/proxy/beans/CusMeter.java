package com.socarmap.proxy.beans;

import java.io.Serializable;

public class CusMeter extends Meter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5810351898942382798L;
	private String cusname;
	private MGeoPoint location;
	private int type;

	public static final int ACTION_ADD = 1;
	public static final int ACTION_UPDATE = 2;
	public static final int ACTION_DELETE = 3;

	public static final int METTER = 1;
	public static final int CUSTOMER = 2;
	public static final int DEMAGE = 3;

	public CusMeter() {
		super();
	}

	public CusMeter(int meterid, int cusid, String mtypename, String metserial,
			String start_index, String mstatus, double last_value,
			String cusname, MGeoPoint location, int type) {
		super(meterid, cusid, mtypename, metserial, start_index, mstatus,
				last_value);
		this.cusname = cusname;
		this.location = location;
		this.type = type;
	}

	public String getCusname() {
		return cusname;
	}

	public MGeoPoint getLocation() {
		return location;
	}

	public String getSuperToString() {
		return toString() + "\n" + super.toString();
	}

	public int getType() {
		return type;
	}

	public void setCusname(String cusname) {
		this.cusname = cusname;
	}

	public void setLocation(MGeoPoint location) {
		this.location = location;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return cusname;
	}

}
