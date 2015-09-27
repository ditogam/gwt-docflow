package com.rdcommon.shared.props;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PropertyTypes implements IsSerializable {
	private String type;
	private ArrayList<String> enumDef;

	public static PropertyTypes createType(String values) {
		if (values == null)
			return null;
		String spl[] = values.split(":");
		if (spl.length == 0 || (spl.length == 1 && spl[0].trim().isEmpty()))
			return null;
		PropertyTypes p = new PropertyTypes();
		p.type = spl[0];
		if (spl.length == 1)
			return p;

		String ens[] = spl[1].split(",");
		if (ens.length == 0 || (ens.length == 1 && ens[0].trim().isEmpty()))
			return null;
		p.enumDef = new ArrayList<String>();
		for (String val : ens) {
			p.enumDef.add(val);
		}
		return p;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<String> getEnumDef() {
		return enumDef;
	}

	public void setEnumDef(ArrayList<String> enumDef) {
		this.enumDef = enumDef;
	}
}
