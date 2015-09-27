package com.rdcommon.shared.props;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PropertyNames implements IsSerializable {

	public static final int PT_DATASOURCE = 1;
	public static final int PT_DSFIELD = 2;
	public static final int PT_GRID = 3;
	public static final int PT_GRID_GROUP = 4;
	public static final int PT_WINDOWFORM = 5;
	public static final int PT_PANLE_GROUP = 6;
	public static final int PT_DYNAMICFORM = 7;
	public static final int PT_FORMITEM = 8;
	public static final int PT_GRID_FIELD = 9;

	private String propertyName;
	private ArrayList<PropertyTypes> propertyTypes;

	public static PropertyNames createProperty(String propertyName,
			String values) {
		if (propertyName == null || values == null)
			return null;
		String types[] = values.split(";");
		if (types.length == 0
				|| (types.length == 1 && types[0].trim().isEmpty()))
			return null;
		PropertyNames p = new PropertyNames();
		p.propertyName = propertyName;
		p.propertyTypes = new ArrayList<PropertyTypes>();
		for (String val : types) {
			PropertyTypes pt = PropertyTypes.createType(val);
			if (pt == null)
				continue;
			p.propertyTypes.add(pt);
		}
		return p;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public ArrayList<PropertyTypes> getPropertyTypes() {
		return propertyTypes;
	}

	public void setPropertyTypes(ArrayList<PropertyTypes> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}
}
