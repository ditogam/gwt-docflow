package com.socarmap.proxy.beans;

import java.io.Serializable;

public class NewBuilding implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8157079967115713238L;
	private String building_add_id;
	private MGeoPoint location;
	private int[] cus_ids;
	private String scus_ids;
	private int ppcityid;
	private int pcityid;
	private boolean newBuilding = false;

	public NewBuilding() {
		// TODO Auto-generated constructor stub
	}

	public NewBuilding(String building_add_id, MGeoPoint location,
			int[] cus_ids, String scus_ids, int ppcityid, int pcityid) {
		super();
		this.building_add_id = building_add_id;
		this.location = location;
		this.cus_ids = cus_ids;
		this.scus_ids = scus_ids;
		this.ppcityid = ppcityid;
		this.setPcityid(pcityid);
	}

	public String getBuilding_add_id() {
		return building_add_id;
	}

	public int[] getCus_ids() {
		return cus_ids;
	}

	public MGeoPoint getLocation() {
		return location;
	}

	public int getPcityid() {
		return pcityid;
	}

	public int getPpcityid() {
		return ppcityid;
	}

	public String getScus_ids() {
		return scus_ids;
	}

	public boolean isNewBuilding() {
		return newBuilding;
	}

	public void setBuilding_add_id(String building_add_id) {
		this.building_add_id = building_add_id;
	}

	public void setCus_ids(int[] cus_ids) {
		this.cus_ids = cus_ids;
	}

	public void setLocation(MGeoPoint location) {
		this.location = location;
	}

	public void setNewBuilding(boolean newBuilding) {
		this.newBuilding = newBuilding;
	}

	public void setPcityid(int pcityid) {
		this.pcityid = pcityid;
	}

	public void setPpcityid(int ppcityid) {
		this.ppcityid = ppcityid;
	}

	public void setScus_ids(String scus_ids) {
		this.scus_ids = scus_ids;
	}

}
