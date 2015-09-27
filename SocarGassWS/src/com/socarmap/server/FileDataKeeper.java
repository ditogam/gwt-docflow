package com.socarmap.server;

import java.util.ArrayList;

public class FileDataKeeper {

	public static final int DELAY_TIMEOUT = 5;// in minutes

	private long time;
	private ArrayList<byte[]> files;

	public FileDataKeeper(byte[] file) {
		time = System.currentTimeMillis();
		files = new ArrayList<byte[]>();
		addFile(file);
	}

	public void addFile(byte[] file) {
		files.add(file);
	}

	public boolean checkForTimeout() {
		boolean timeout = ((int) (System.currentTimeMillis() - time) / 1000) > DELAY_TIMEOUT;
		if (timeout)
			clearData();
		return timeout;
	}

	public void clearData() {
		files.clear();
	}

	public ArrayList<byte[]> getFiles() {
		return files;
	};
}
