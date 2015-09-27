package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AndroidStatusCounts implements IsSerializable {

	private int subregion_id;
	private int system_id;
	private int count;
	private long min_date;
	private long max_date;

	public AndroidStatusCounts() {
	}

	public AndroidStatusCounts(int subregion_id, int system_id, int count,
			long min_date, long max_date) {
		super();
		this.subregion_id = subregion_id;
		this.system_id = system_id;
		this.count = count;
		this.min_date = min_date;
		this.max_date = max_date;
	}

	public int getSubregion_id() {
		return subregion_id;
	}

	public void setSubregion_id(int subregion_id) {
		this.subregion_id = subregion_id;
	}

	public int getSystem_id() {
		return system_id;
	}

	public void setSystem_id(int system_id) {
		this.system_id = system_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getMin_date() {
		return min_date;
	}

	public void setMin_date(long min_date) {
		this.min_date = min_date;
	}

	public long getMax_date() {
		return max_date;
	}

	public void setMax_date(long max_date) {
		this.max_date = max_date;
	}

}
