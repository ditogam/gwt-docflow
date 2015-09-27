package com.docflow.client.components.map;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.i18n.client.NumberFormat;

public class Timer {
	private static Map<String, Timer> times = new TreeMap<String, Timer>();

	public static Timer getTimer(String name) {
		Timer timer = times.get(name);
		if (timer == null) {
			timer = new Timer();
			times.put(name, timer);
		}
		return timer;
	}

	public static String printall() {
		String s = "";
		try {

			for (String key : times.keySet()) {
				s += ("<br>Key=" + key + " " + times.get(key));
			}
		} catch (Exception e) {
		}
		return s;
	}

	public static void start(String name) {
		synchronized (times) {
			Timer timer = getTimer(name);
			timer.start();
		}
	}

	public static void start(String prevname, String newname) {
		step(prevname);
		synchronized (times) {
			Timer timer = getTimer(newname);
			timer.start();
		}
	}

	public static void step(String name) {
		synchronized (times) {
			getTimer(name).step();
		}
	}

	public static void reset() {
		times.clear();
	}

	private long time;
	private long current;
	private long min;

	private long max;

	private long fullTime = 0;

	private long fullcount;

	private double avg = 0.0;

	private static NumberFormat df2 = NumberFormat.getFormat(
			"#######################0.###");

	private Timer() {
		min = 1000000000000000L;
		max = 0;
	}

	private void start() {
		time = System.currentTimeMillis();
	}

	private void step() {
		time = System.currentTimeMillis() - time;
		current = time;
		min = Math.min(current, min);
		max = Math.max(current, max);
		fullcount++;
		fullTime += current;
		avg = (double) fullTime / (double) fullcount;
		try {
			avg = df2.parse(df2.format(avg));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
	}

	@Override
	public String toString() {
		return "Count=" + fullcount + " current=" + current + " avg=" + avg
				+ " min=" + min + " max=" + max;
	}
}
