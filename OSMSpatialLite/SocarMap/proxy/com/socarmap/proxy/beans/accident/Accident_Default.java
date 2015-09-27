package com.socarmap.proxy.beans.accident;

import java.io.Serializable;
import java.sql.Timestamp;

public class Accident_Default implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085170480488257233L;

	private int id;
	private Timestamp reg_time;
	private Timestamp event_time;
	private int reason_type;
	private String reason_text;
	private int current_status;
	private int cus_id;
	private int creator_user_id;
	private String description;
	private int ppcity_id;
	private int pcity_id;
	private int city_id;
	private int street_id;
	private String address;

	public Accident_Default() {
		// TODO Auto-generated constructor stub
	}

	public String getAddress() {
		return address;
	}

	public int getCity_id() {
		return city_id;
	}

	public int getCreator_user_id() {
		return creator_user_id;
	}

	public int getCurrent_status() {
		return current_status;
	}

	public int getCus_id() {
		return cus_id;
	}

	public String getDescription() {
		return description;
	}

	public Timestamp getEvent_time() {
		return event_time;
	}

	public int getId() {
		return id;
	}

	public int getPcity_id() {
		return pcity_id;
	}

	public int getPpcity_id() {
		return ppcity_id;
	}

	public String getReason_text() {
		return reason_text;
	}

	public int getReason_type() {
		return reason_type;
	}

	public Timestamp getReg_time() {
		return reg_time;
	}

	public int getStreet_id() {
		return street_id;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public void setCreator_user_id(int creator_user_id) {
		this.creator_user_id = creator_user_id;
	}

	public void setCurrent_status(int current_status) {
		this.current_status = current_status;
	}

	public void setCus_id(int cus_id) {
		this.cus_id = cus_id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEvent_time(Timestamp event_time) {
		this.event_time = event_time;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPcity_id(int pcity_id) {
		this.pcity_id = pcity_id;
	}

	public void setPpcity_id(int ppcity_id) {
		this.ppcity_id = ppcity_id;
	}

	public void setReason_text(String reason_text) {
		this.reason_text = reason_text;
	}

	public void setReason_type(int reason_type) {
		this.reason_type = reason_type;
	}

	public void setReg_time(Timestamp reg_time) {
		this.reg_time = reg_time;
	}

	public void setStreet_id(int street_id) {
		this.street_id = street_id;
	}

}
