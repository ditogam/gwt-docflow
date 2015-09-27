package com.docflow.shared.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User_Group implements IsSerializable {

	/**
	 * 
	 */
	private int group_id;
	private String gname;
	private boolean applyed;

	public String getGname() {
		return gname;
	}

	public int getGroup_id() {
		return group_id;
	}

	public boolean isApplyed() {
		return applyed;
	}

	public void setApplyed(boolean applyed) {
		this.applyed = applyed;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

}
