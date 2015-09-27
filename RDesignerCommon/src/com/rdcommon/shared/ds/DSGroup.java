package com.rdcommon.shared.ds;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSGroup extends DSComponent implements IsSerializable {

	private Boolean vertical;
	private DSComponent dynamicFieldProps;
	private ArrayList<DSGroup> dsGroups;

	public Boolean getVertical() {
		return vertical;
	}

	public void setVertical(Boolean vertical) {
		this.vertical = vertical;
	}

	public ArrayList<DSGroup> getDsGroups() {
		return dsGroups;
	}

	public void setDsGroups(ArrayList<DSGroup> dsGroups) {
		this.dsGroups = dsGroups;
	}

	public DSComponent getDynamicFieldProps() {
		return dynamicFieldProps;
	}

	public void setDynamicFieldProps(DSComponent dynamicFieldProps) {
		this.dynamicFieldProps = dynamicFieldProps;
	}

}
