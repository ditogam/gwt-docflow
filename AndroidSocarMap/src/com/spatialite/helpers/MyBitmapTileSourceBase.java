package com.spatialite.helpers;

import java.io.InputStream;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;

import android.graphics.drawable.Drawable;

import com.spatialite.utilities.ActivityHelper;

public class MyBitmapTileSourceBase extends BitmapTileSourceBase {

	public MyBitmapTileSourceBase(String offlineSource, string aResourceId,
			int aZoomMinLevel, int aZoomMaxLevel, int aTileSizePixels,
			String aImageFilenameEnding) {
		super(offlineSource, aResourceId, aZoomMinLevel, aZoomMaxLevel,
				aTileSizePixels, aImageFilenameEnding);

	}

	@Override
	public Drawable getDrawable(final String aFilePath) {
		Drawable dr = super.getDrawable(aFilePath);
		ActivityHelper.showAlert(null, "KK" + aFilePath);
		return dr;
	}

	@Override
	public Drawable getDrawable(final InputStream aFileInputStream) {
		Drawable dr = null;
		try {
			dr = super.getDrawable(aFileInputStream);
		} catch (LowMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ActivityHelper.showAlert(null, "KK"
				+ aFileInputStream.getClass().getName());
		return dr;
	}

	@Override
	public String getTileRelativeFilenameString(final MapTile tile) {
		String ss = super.getTileRelativeFilenameString(tile);
		ActivityHelper.showAlert(null, "BM" + ss);
		return ss;
	}

}
