package com.socar.map.downloader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FromFile {
	FileFilter ff;
	int batch_size;
	public static int TILE_DOWNLOAD_SECONDS_TO_WORK = 25;
	ThreadPoolExecutor threadPoolExecutor;

	private String connectionString;
	private String user;
	private String password;
	private int processors;

	public FromFile(String connectionString, String user, String password,
			int processors, int batch_size, String dir) throws Exception {
		this.batch_size = batch_size;
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
		this.processors = processors;
		threadPoolExecutor = new ThreadPoolExecutor(processors, processors,
				TILE_DOWNLOAD_SECONDS_TO_WORK, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		ff = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isDirectory()
						|| pathname.getName().endsWith(".tile");
			}
		};
		File pDir = new File(dir);
		ArrayList<File> files = new ArrayList<File>();

		for (File file : pDir.listFiles()) {
			if (!file.isDirectory())
				continue;
			try {
				Integer i = Integer.parseInt(file.getName());
				if (i >= 8 && i <= 20)
					addDir(file, files);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}

	private void addDir(File file, ArrayList<File> files) throws Exception {

		for (File f : file.listFiles(ff)) {
			if (f.isDirectory()) {
				addDir(f, files);
			} else {

				files.add(f);
				if (files.size() > batch_size) {
					ArrayList<File> fls = new ArrayList<File>(files);
					files.clear();
					startProccessing(fls);
				}
			}
		}

	}

	private void startProccessing(ArrayList<File> fls) throws Exception {
		while (processors < (threadPoolExecutor.getTaskCount() - threadPoolExecutor
				.getCompletedTaskCount())) {
//			System.err.println("count=" + threadPoolExecutor.getTaskCount()
//					+ "," + threadPoolExecutor.getCompletedTaskCount());
			Thread.sleep(100);
		}
		threadPoolExecutor.execute(new MapDownloader(connectionString, user,
				password, fls));

	}

	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		new FromFile(props.getProperty("connection"),
				props.getProperty("user"), props.getProperty("pwd"),
				getIntValue(props.getProperty("processors")),
				getIntValue(props.getProperty("batch_size")),
				props.getProperty("dir"));
	}

	public static int getIntValue(String val) {
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return 0;
		}
	}

}
