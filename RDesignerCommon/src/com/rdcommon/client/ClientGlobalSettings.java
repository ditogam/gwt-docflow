package com.rdcommon.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.rdcommon.shared.GlobalValues;
import com.rdcommon.shared.props.PropertyNames;
import com.rdcommon.shared.props.PropertyTypes;

public class ClientGlobalSettings {

	public static GlobalValues globalValues;

	public static final DateTimeFormat dateFormatter = DateTimeFormat
			.getFormat("dd/MM/yyyy");

	public static PropertyNames detectIfPropertyIsStandart(String name, int type) {
		if (globalValues == null)
			return null;
		TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames = globalValues
				.getPropertyNames();
		if (propertyNames == null)
			return null;
		TreeMap<String, PropertyNames> props = propertyNames.get(type);
		if (props == null)
			return null;
		PropertyNames prop = props.get(name);
		if (prop != null)
			return prop;
		for (String key : props.keySet()) {
			if (key.equalsIgnoreCase(name))
				return props.get(key);
		}
		return null;
	}

	public static String getTypeName(int type, String name, String value) {
		TreeMap<String, PropertyNames> propertyNames = getNames(type);
		if (propertyNames == null)
			return null;
		PropertyNames p = propertyNames.get(name);
		if (p == null)
			return null;
		ArrayList<PropertyTypes> propertyTypes = p.getPropertyTypes();
		if (propertyTypes == null)
			return null;

		for (PropertyTypes pt : propertyTypes) {
			if (pt.getEnumDef() == null)
				continue;
			for (String key : pt.getEnumDef()) {
				if (key.equals(value))
					return pt.getType();
			}
		}
		return null;
	}

	public static LinkedHashMap<String, String> getEnum(int type, String name,
			String valType) {
		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
		TreeMap<String, PropertyNames> propertyNames = getNames(type);
		if (propertyNames == null)
			return ret;
		PropertyNames p = propertyNames.get(name);
		if (p == null)
			return ret;
		ArrayList<PropertyTypes> propertyTypes = p.getPropertyTypes();
		if (propertyTypes == null)
			return ret;
		PropertyTypes pts = null;
		for (PropertyTypes pt : propertyTypes) {
			if (pt.getType().equals(valType)) {
				pts = pt;
				break;
			}
		}
		if (pts == null)
			return ret;
		for (String key : pts.getEnumDef()) {
			ret.put(key, key);
		}
		return ret;
	}

	public static TreeMap<String, PropertyNames> getNames(int type) {
		if (globalValues == null)
			return new TreeMap<String, PropertyNames>();
		TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames = globalValues
				.getPropertyNames();
		if (propertyNames == null)
			return new TreeMap<String, PropertyNames>();
		return propertyNames.containsKey(type) ? propertyNames.get(type)
				: new TreeMap<String, PropertyNames>();
	}

}
