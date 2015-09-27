package com.docflow.shared.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PermitionItem implements IsSerializable {

	/**
	 * 
	 */

	private int permition_id;
	private String permitionName;
	private boolean applyed;
	private boolean denied;

	public int getPermition_id() {
		return permition_id;
	}

	public String getPermitionName() {
		return permitionName;
	}

	public boolean isApplyed() {
		return applyed;
	}

	public boolean isDenied() {
		return denied;
	}

	public void setApplyed(boolean applyed) {
		this.applyed = applyed;
	}

	public void setDenied(boolean denied) {
		this.denied = denied;
	}

	public void setPermition_id(int permition_id) {
		this.permition_id = permition_id;
	}

	public void setPermitionName(String permitionName) {
		this.permitionName = permitionName;
	}

}
