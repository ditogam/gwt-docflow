package com.socar.map;

import java.io.File;

public class DownloadMapWorker implements Runnable,
		Comparable<DownloadMapWorker> {
	static int fullcount = 0;
	static int downloadedcount = 0;
	static int errorcount = 0;
	static int fullsize;
	static long fulltime;
	private TileFile request;
	
	private boolean force;
	
	File parent;
	String mTile;

	public DownloadMapWorker(boolean force, File parent, String mTile,
			TileFile request) {
		this.request = request;
		this.force = force;
		this.parent = parent;
		this.mTile = mTile;
	}

	@Override
	public void run() {
		try {
			long time = System.currentTimeMillis();
			long fulltime = System.currentTimeMillis();
			fullcount++;
			if(request.download(force, parent, mTile))
				downloadedcount++;
			time = System.currentTimeMillis() - time;
			System.out.println("zoom=" + request.getZoom() + " fullsize= "
					+ fullsize + " Count= " + fullcount + " downloaded="
					+ downloadedcount + " in " + time + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public int compareTo(DownloadMapWorker o) {
		return 0; // (int) (time - o.time);
	}

}