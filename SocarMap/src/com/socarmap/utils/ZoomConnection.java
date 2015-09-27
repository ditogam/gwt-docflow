package com.socarmap.utils;

public interface ZoomConnection {

	public void close();

	public byte[] getImageData(ZX1X2 zx1x2, int zoom, int x, int y,
			boolean doNotClose) throws Exception;

	public void updateImageData(ZX1X2 zx1x2, int zoom, int x, int y, byte[] data)
			throws Exception;

}
