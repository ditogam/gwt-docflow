package com.socargass.tabletexporter;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;

public class CreatePng {
	public static void main(String[] args) throws Exception {
		String fileName = "getmap.png";
		String fileNameOut = "getmap1.png";
		FileInputStream fis = new FileInputStream(fileName);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = fis.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}

		fis.close();
		buffer = bos.toByteArray();
		bos.flush();
		bos.close();
		System.out.println("original size=" + buffer.length);
		// ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		PngImage img = new PngImage(fileName);
		PngOptimizer optimizer = new PngOptimizer("debug");
		optimizer.optimize(img, fileNameOut, 9);
		buffer = img.getImageData();
		System.out.println("compressed size=" + buffer.length);
		FileOutputStream fos = new FileOutputStream(fileNameOut);
		fos.write(buffer);
		fos.flush();
		fos.close();

		Rectangle r = new Rectangle();
		r.intersects(null);
	}
}
