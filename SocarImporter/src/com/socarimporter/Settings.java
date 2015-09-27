package com.socarimporter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import nanoxml.XMLElement;
import android.content.Context;

public class Settings {
	private String server_url;

	public String getServer_url() {
		return server_url;
	}

	public void setServer_url(String server_url) {
		this.server_url = server_url;
	}

	@SuppressWarnings("unchecked")
	public static Settings load(Context ctx) throws Exception {
		XMLElement el = new XMLElement();
		File file = checkExistence(ctx);
		el.parseFromReader(new FileReader(file));
		Vector<XMLElement> v = (Vector<XMLElement>) el.getChildren();
		Settings settings = new Settings();
		for (XMLElement xmlElement : v) {
			if (xmlElement.getName().trim().equals("server_url"))
				settings.setServer_url(xmlElement.getContent().trim());
		}
		return settings;
	}

	public static File checkExistence(Context ctx) throws IOException {
		String fileName = ctx.getString(R.string.socar_db);
		File dir = new File(fileName);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(dir, "settings.xml");
		if (!file.exists())
			AssetHelper.CopyAsset(ctx, dir, "settings.xml");
		return file;
	}

	public void saveFile(Context ctx) throws IOException {
		File file = checkExistence(ctx);
		XMLElement el = new XMLElement();
		el.setName("settings");
		XMLElement settings = new XMLElement();
		settings.setName("server_url");
		settings.setContent(getServer_url());
		el.addChild(settings);
		FileWriter fw = new FileWriter(file);
		fw.write(el.toString());
		fw.flush();
		fw.close();
	}
}
