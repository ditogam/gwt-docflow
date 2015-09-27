package com.docflow.shared.docflow;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class DocTypeGroup implements Serializable, Comparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6220003844747473046L;
	private int group_id;
	private String doctypegroupvalue;

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getDoctypegroupvalue() {
		return doctypegroupvalue;
	}

	public void setDoctypegroupvalue(String doctypegroupvalue) {
		this.doctypegroupvalue = doctypegroupvalue;
	}

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

}
