package com.docflowdroid.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;

import com.docflowdroid.conf.Config;

public class DrowableDownloader {
	private static final HashMap<String, Drawable> cachedImages = new HashMap<String, Drawable>();

	public static Drawable drawableFromUrl(String url, boolean from_cach)
			throws IOException {
		if (from_cach && cachedImages.containsKey(url))
			return cachedImages.get(url);
		InputStream is = (InputStream) new URL(url).getContent();
		Drawable d = Drawable.createFromStream(is, "src name");
		if (from_cach)
			cachedImages.put(url, d);
		return d;
	}

	public static Drawable drawableFromUrl(String url) throws IOException {
		return drawableFromUrl(url, true);
	}

	public static String getMainURL() {
		String mainUrl = Config.getInstance().getActiveServer().getServerurl();
		int ind = mainUrl.lastIndexOf("/");
		mainUrl = mainUrl.substring(0, ind + 1);
		return mainUrl;
	}

	public static String getMapUrl() {
		String mainUrl = getMainURL();
		mainUrl += "wms?format=image/png&palette=socarall&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:900913&width=256&height=256&layers=SocarAll&map=&styles=";
		return mainUrl;
	}
}
