package com.socarmap.helper;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {

	public static byte[] resizeBitmapAndEncode(String imgPath, int targetWidth,
			int targetHeight) {
		Bitmap pic = resizePicture(imgPath, targetWidth, targetHeight);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	public static Bitmap resizePicture(byte[] bytes, int targetWidth,
			int targetHeight) {
		Bitmap pic;
		// create bitmap options to calculate and use sample size
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();

		// first decode image dimensions only - not the image bitmap
		// itself
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmpOptions);

		// work out what the sample size should be

		// image width and height before sampling
		int currHeight = bmpOptions.outHeight;
		int currWidth = bmpOptions.outWidth;

		// variable to store new sample size
		int sampleSize = 1;

		// calculate the sample size if the existing size is larger
		// than target size
		if (currHeight > targetHeight || currWidth > targetWidth) {
			// use either width or height
			if (currWidth > currHeight)
				sampleSize = Math.round((float) currHeight
						/ (float) targetHeight);
			else
				sampleSize = Math
						.round((float) currWidth / (float) targetWidth);
		}
		// use the new sample size
		bmpOptions.inSampleSize = sampleSize;

		// now decode the bitmap using sample options
		bmpOptions.inJustDecodeBounds = false;

		// get the file as a bitmap
		pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmpOptions);
		return pic;
	}

	public static Bitmap resizePicture(String imgPath, int targetWidth,
			int targetHeight) {
		Bitmap pic;
		// create bitmap options to calculate and use sample size
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();

		// first decode image dimensions only - not the image bitmap
		// itself
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, bmpOptions);

		// work out what the sample size should be

		// image width and height before sampling
		int currHeight = bmpOptions.outHeight;
		int currWidth = bmpOptions.outWidth;

		// variable to store new sample size
		int sampleSize = 1;

		// calculate the sample size if the existing size is larger
		// than target size
		if (currHeight > targetHeight || currWidth > targetWidth) {
			// use either width or height
			if (currWidth > currHeight)
				sampleSize = Math.round((float) currHeight
						/ (float) targetHeight);
			else
				sampleSize = Math
						.round((float) currWidth / (float) targetWidth);
		}
		// use the new sample size
		bmpOptions.inSampleSize = sampleSize;

		// now decode the bitmap using sample options
		bmpOptions.inJustDecodeBounds = false;

		// get the file as a bitmap
		pic = BitmapFactory.decodeFile(imgPath, bmpOptions);
		return pic;
	}
}
