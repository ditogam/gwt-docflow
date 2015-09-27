package com.docflow.shared.common;

import com.google.gwt.xml.client.Node;

public class XMLParceserHelper {

	public static String getAttribute(String attrName, Node el) {
		return getAttribute(attrName, el, "");
	}

	public static String getAttribute(String attrName, Node el, String def) {

		Object str = null;
		try {
			str = el.getAttributes().getNamedItem(attrName).getNodeValue();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (str != null) {
			return str.toString();
		}
		return def;
	}

	public static int getIntValue(String attrName, Node el) {
		try {
			return Integer.parseInt(getAttribute(attrName, el, "0"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public static boolean getBoolean(String attrName, Node el) {
		try {
			return Integer.parseInt(getAttribute(attrName, el, "0")) == 1 ? true
					: false;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	public static double getDoubleValue(String attrName, Node el,
			double defaultVal) {
		try {
			return Double.parseDouble(getAttribute(attrName, el, defaultVal
					+ ""));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return defaultVal;
	}

	public static double getDoubleValue(String attrName, Node el) {

		return getDoubleValue(attrName, el, 0);
	}
}
