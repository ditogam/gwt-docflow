package com.socarmap.proxy.beans;

import java.io.Serializable;

public class BuildingUpdate implements Serializable {

	public static final int TRANSFER_MAX_SIZE = 512 * 1024;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6072518393858656771L;
	private int buid;
	private int[] cus_ids;
	private int max_transfer_size = TRANSFER_MAX_SIZE;

	public int getBuid() {
		return buid;
	}

	public int[] getCus_ids() {
		return cus_ids;
	}

	public int getMax_transfer_size() {
		return max_transfer_size;
	}

	public void setBuid(int buid) {
		this.buid = buid;
	}

	public void setCus_ids(int[] cus_ids) {
		this.cus_ids = cus_ids;
	}

	public void setMax_transfer_size(int max_transfer_size) {
		this.max_transfer_size = max_transfer_size;
	}

}
