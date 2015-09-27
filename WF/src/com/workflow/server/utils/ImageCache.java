package com.workflow.server.utils;


public class ImageCache extends LruCache<String, byte[]> {
	private static ImageCache instance;

	public ImageCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, byte[] value) {
		return value.length;
	}

	public static synchronized ImageCache getInstance() {
		if (instance == null) {
			instance = new ImageCache(5000);
		}
		return instance;
	}
}