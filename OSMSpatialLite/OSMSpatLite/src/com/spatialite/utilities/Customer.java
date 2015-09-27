package com.spatialite.utilities;

public class Customer {

	private Long cus_id;
	private String cusname;
	private Long building_id;

	public Customer(Long cus_id, String cusname, Long building_id) {
		if (building_id < 0)
			building_id = null;
		this.cus_id = cus_id;
		this.cusname = cusname;
		this.building_id = building_id;
	}

	public Long getCus_id() {
		return cus_id;
	}

	public String getCusname() {
		return cusname;
	}

	public Long getBuilding_id() {
		return building_id;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return cus_id + "- " + cusname;
	}
}
