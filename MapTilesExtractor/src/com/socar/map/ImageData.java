package com.socar.map;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageData {
	private String zxy;
	private byte[] data;

	public ImageData() {
		// TODO Auto-generated constructor stub
	}

	public String getZxy() {
		return zxy;
	}

	public void setZxy(String zxy) {
		this.zxy = zxy;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Image saveToFile() throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		Iterator<?> readers = ImageIO.getImageReadersByFormatName("png");
		// ImageIO is a class containing static convenience methods for locating
		// ImageReaders
		// and ImageWriters, and performing simple encoding and decoding.

		ImageReader reader = (ImageReader) readers.next();
		Object source = bis; // File or InputStream, it seems file is OK

		ImageInputStream iis = ImageIO.createImageInputStream(source);
		// Returns an ImageInputStream that will take its input from the given
		// Object

		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();

		Image image = reader.read(0, param);
		// got an image file

		
		return image;

	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return zxy;
	}

}
