package com.docflow.server;

import java.util.Properties;
import java.util.Set;

public class Sysout {
	public static void ddd() {
		Properties pr = System.getProperties();
		Set<Object> keys = pr.keySet();
		for (Object key : keys)
			System.out.println(key + " = " + pr.getProperty(key.toString()));
	}
}
