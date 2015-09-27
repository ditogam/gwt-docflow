package com.docflow.shared;

import java.util.HashMap;

public class SystemNames {
	public static String getSystemName(int system) {
		if (systemNames == null) {
			systemNames = new HashMap<Integer, String>();
			systemNames.put(SCSystem.S_DOCFLOW, "DocFlow");
			systemNames.put(SCSystem.S_CALL_CENTER, "Call Center");
			systemNames.put(SCSystem.S_ECCIDENT_CONTROLL, "Accident Control");
		}
		return systemNames.get(system);
	}

	private static HashMap<Integer, String> systemNames = null;
}
