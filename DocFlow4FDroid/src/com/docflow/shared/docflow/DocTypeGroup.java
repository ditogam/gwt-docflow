package com.docflow.shared.docflow;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("rawtypes")
public class DocTypeGroup implements IsSerializable, Comparable {

	/**
	 * 
	 */
	private int group_id;
	private String doctypegroupvalue;
	private int system_id;

	@Override
	public int compareTo(Object o) {
		if (o == null)
			return -1;
		if (o instanceof DocTypeGroup)
			if (((DocTypeGroup) o).getGroup_id() == getGroup_id()) {
				return 0;
			} else {
				return 1;
			}

		return 0;
	}

	public String getDoctypegroupvalue() {
		return doctypegroupvalue;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setDoctypegroupvalue(String doctypegroupvalue) {
		this.doctypegroupvalue = doctypegroupvalue;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getSystem_id() {
		return system_id;
	}

	public void setSystem_id(int system_id) {
		this.system_id = system_id;
	}

}
