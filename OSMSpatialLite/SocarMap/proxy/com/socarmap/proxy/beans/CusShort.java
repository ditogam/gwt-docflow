package com.socarmap.proxy.beans;

import java.io.Serializable;

public class CusShort implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4675760088514122776L;
	private Long cus_id;
	private String cusname;
	private Long building_id;
	private Long zone;

	public CusShort() {

	}

	public CusShort(Long cus_id, String cusname, Long building_id, Long zone) {
		if (building_id < 0)
			building_id = null;
		this.cus_id = cus_id;
		this.cusname = cusname;
		this.building_id = building_id;
		this.zone = zone;
	}

	public Long getBuilding_id() {
		return building_id;
	}

	public Long getCus_id() {
		return cus_id;
	}

	public String getCusname() {
		return cusname;
	}

	public Long getZone() {
		return zone;
	}

	@Override
	public String toString() {
		return cus_id + " - " + cusname;
	}
}
