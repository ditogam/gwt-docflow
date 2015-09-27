package com.socarmap.proxy.beans;

import java.io.Serializable;

public class BuildingInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2726794465918894848L;
	private Long building_id = null;
	private Long region_id = null;
	private Long subregion_id = null;
	private String building;

	public String getBuilding() {
		return building;
	}

	public Long getBuilding_id() {
		return building_id;
	}

	public Long getRegion_id() {
		return region_id;
	}

	public Long getSubregion_id() {
		return subregion_id;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public void setBuilding_id(Long building_id) {
		this.building_id = building_id;
	}

	public void setRegion_id(Long region_id) {
		this.region_id = region_id;
	}

	public void setSubregion_id(Long subregion_id) {
		this.subregion_id = subregion_id;
	}

}
