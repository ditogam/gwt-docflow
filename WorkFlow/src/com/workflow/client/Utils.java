package com.workflow.client;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

public class Utils {

	public static Map jsToMap(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Map)
			return (Map) obj;
		if (obj instanceof JavaScriptObject)
			return JSOHelper.convertToMap((JavaScriptObject) obj);
		return null;
	}

	
	public static String getClassSimpleName(Class<?> clazz) {
		String name = clazz.getName();
		return getClassSimpleName(name);
	}
	
	public static String getClassSimpleName(String name) {
		name = name.substring(name.lastIndexOf(".") + 1);
		return name;
	}
	
	public static final String getCapitalise(String str) {
		String firstChar = (str.charAt(0) + "").toUpperCase();
		str = firstChar + str.substring(1);
		return str;
	}

	public static String split_by_Capitals(String str) {
		String result = "";
		for (char c : str.toCharArray()) {
			if (!(c + "").toLowerCase().equals("" + c))
				result += " ";
			result += c;
		}
		return result.trim();
	}

	public static native String getPropertyNames(JavaScriptObject object) /*-{
		var ret = "";
		for ( var i in object) {
			if (ret == "") {
				ret = ret + i
			} else {
				ret = ret + "," + i;
			}
		}
		return ret;
	}-*/;
}
