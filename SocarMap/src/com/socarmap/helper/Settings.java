package com.socarmap.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import android.app.Activity;

import com.socarmap.R;

public class Settings {
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	private static Date defaultDate;
	static {
		try {
			defaultDate = dateFormat.parse("2013-02-25");
		} catch (ParseException e) {
			defaultDate = new Date();
		}
	}
	private String url;
	private String secondary_url;
	private String locale;
	private String user;
	private String pwd;
	private Date lastdowloaded;

	private static String fileName = "settings.properties";
	private static File dir = null;

	public static File tmp_Apk = null;

	private static Settings instance;

	public static Settings getInstance(Activity ctx) {
		if (dir == null)
			dir = new File(ctx.getString(R.string.socar_db)).getParentFile();
		if (!dir.exists())
			dir.mkdir();
		tmp_Apk = new File(dir, "tmp.apk");
		if (instance == null) {

			File flSettings = new File(dir, fileName);
			if (!flSettings.exists()) {
				try {
					AssetHelper.CopyAsset(ctx, dir, fileName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Properties pr = new Properties();
			try {
				pr.load(new FileReader(flSettings));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			instance = new Settings();
			instance.locale = pr.getProperty("locale");
			instance.url = pr.getProperty("server_url");
			instance.secondary_url = pr.getProperty("server_secondary_url");
			instance.user = pr.getProperty("user");
			instance.pwd = pr.getProperty("pwd");
			instance.setDate(pr.getProperty("lastdowloaded"));
			instance.locale = instance.locale == null ? "" : instance.locale;
			instance.url = instance.url == null ? "http://10.90.200.241:8787/SocarGassWS/"
					: instance.url;
			instance.secondary_url = instance.secondary_url == null ? "http://127.0.0.1:8787/SocarGassWS/"
					: instance.secondary_url;
		}
		return instance;
	}

	public Date getLastdowloaded() {
		return lastdowloaded;
	}

	public String getLocale() {
		return locale;
	}

	public String getPwd() {
		if (pwd == null)
			pwd = "";
		return pwd;
	}

	public String getSecondary_url() {
		if (!secondary_url.endsWith("/"))
			secondary_url += "/";
		return secondary_url;
	}

	public String getUrl() {
		if (!url.endsWith("/"))
			url += "/";
		return url;
	}

	public String getUser() {
		if (user == null)
			user = "";
		return user;
	}

	public void saveData() {
		File flSettings = new File(dir, fileName);
		if (!dir.exists())
			dir.mkdir();
		Properties pr = new Properties();
		try {
			pr.load(new FileReader(flSettings));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pr.setProperty("locale", getLocale());
		pr.setProperty("server_url", getUrl());
		pr.setProperty("server_secondary_url", getSecondary_url());
		pr.setProperty("user", getUser());
		pr.setProperty("lastdowloaded", dateFormat.format(getLastdowloaded()));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(flSettings);
			pr.store(fos, "");
			fos.flush();
		} catch (Throwable e) {
		} finally {
			try {
				fos.close();
			} catch (Throwable e2) {
			}
		}

	}

	private void setDate(String date) {
		try {
			lastdowloaded = dateFormat.parse(date);
		} catch (Throwable e) {
		}
		if (lastdowloaded == null) {
			lastdowloaded = defaultDate;
		}
		if (lastdowloaded.getTime() < defaultDate.getTime())
			lastdowloaded = defaultDate;
	}

	public void setLastdowloaded(Date lastdowloaded) {
		this.lastdowloaded = lastdowloaded;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setSecondary_url(String secondary_url) {
		this.secondary_url = secondary_url;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
