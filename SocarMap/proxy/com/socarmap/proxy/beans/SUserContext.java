package com.socarmap.proxy.beans;

import java.io.Serializable;

public class SUserContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6898348056161952150L;
	private int user_id;
	private String user_name;
	private int region_id;
	private int subregion_id;
	private int language_id;

	public SUserContext() {

	}

	public int getLanguage_id() {
		return language_id;
	}

	public int getRegion_id() {
		return region_id;
	}

	public int getSubregion_id() {
		return subregion_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setLanguage_id(int language_id) {
		this.language_id = language_id;
	}

	public void setRegion_id(int region_id) {
		this.region_id = region_id;
	}

	public void setSubregion_id(int subregion_id) {
		this.subregion_id = subregion_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

}
