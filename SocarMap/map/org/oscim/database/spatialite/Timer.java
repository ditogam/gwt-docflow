package org.oscim.database.spatialite;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

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

	public static void printall() {
		try {
			System.err.println("---------------");
			for (String key : times.keySet()) {
				System.err.println("Key=" + key + " " + times.get(key));
			}
			System.err.println("---------------");
		} catch (Exception e) {
		}
	}

	public static void start(String name) {
		synchronized (times) {
			Timer timer = getTimer(name);
			timer.start();
		}
	}

	public static void step(String name) {
		synchronized (times) {
			getTimer(name).step();
		}

	}

	private long time;
	private long current;
	private long min;

	private long max;

	private long fullTime = 0;

	private long fullcount;

	private double avg = 0.0;

	private static DecimalFormat df2 = new DecimalFormat(
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
			avg = df2.parse(df2.format(avg)).doubleValue();
		} catch (ParseException e) {
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
