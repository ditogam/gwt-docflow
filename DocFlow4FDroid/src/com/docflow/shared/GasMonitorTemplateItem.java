package com.docflow.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GasMonitorTemplateItem implements IsSerializable {
	private int type;
	private int id;

	public GasMonitorTemplateItem() {
	}

	public GasMonitorTemplateItem(String value) throws Exception {
		String[] vals = value.split(",");
		setType(Integer.parseInt(vals[0].trim()));
		setId(Integer.parseInt(vals[1].trim()));
	}

	public static ArrayList<GasMonitorTemplateItem> createFromString(
			String value) throws Exception {
		if (value == null)
			value = "";
		ArrayList<GasMonitorTemplateItem> ret = new ArrayList<GasMonitorTemplateItem>();
		value = value.trim();
		if (value.length() == 0)
			return ret;
		String[] vals = value.split(";");
		for (String v : vals) {
			ret.add(new GasMonitorTemplateItem(v));
		}
		return ret;
	}

	public GasMonitorTemplateItem(int type, int id) {
		this.type = type;
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return type + "," + id;
	}
}
