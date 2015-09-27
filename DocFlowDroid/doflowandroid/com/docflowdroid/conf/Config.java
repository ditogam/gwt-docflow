package com.docflowdroid.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;

import com.docflowdroid.helper.AssetHelper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("config")
public class Config implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6754692772645869861L;
	@XStreamAsAttribute()
	private String username;

	@XStreamAsAttribute
	private String pwd;

	@XStreamAsAttribute
	private int systemid;

	@XStreamAsAttribute
	private int languageid;

	private ArrayList<Server> servers;

	private static XStream xstream;
	public static final String ns = null;

	private Config() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public ArrayList<Server> getServers() {
		if (servers == null)
			servers = new ArrayList<Server>();
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		if (servers == null)
			servers = new ArrayList<Server>();
		this.servers = servers;
	}

	public Server addServer(String servername, String serverurl)
			throws Exception {
		return addServer(servername, serverurl, false);
	}

	public Server addServer(String servername, String serverurl,
			boolean activate) throws Exception {
		return addServer(servername, serverurl, activate, false);
	}

	public Server addServer(String servername, String serverurl,
			boolean activate, boolean save) throws Exception {
		getServers();
		servername = servername.trim();
		serverurl = serverurl.trim();
		for (Server server : servers) {
			if (server.getServername().trim().equals(servername))
				throw new Exception("Server named " + servername
						+ " already exists!!!");

		}
		Server server = new Server(servername, serverurl);
		server.setActive(activate);

		Server activeserver = null;

		if (activate)
			for (Server s : servers)
				if (s.isActive())
					activeserver = s;
		servers.add(server);
		if (save)
			try {
				save();
			} catch (Exception e) {
				servers.remove(server);
				if (activeserver != null)
					activeserver.setActive(true);
				throw e;
			}
		return server;
	}

	private static Config instance = null;
	private static File config_file;

	public static Config getInstance() {
		return instance;
	}

	public static void save() throws Exception {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(config_file);
			xstream.toXML(instance, fos);
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {

			}
		}

	}

	public Server getActiveServer() {
		try {
			return getActiveServer(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public Server getActiveServer(boolean saveIfNotfound) throws Exception {
		getServers();
		for (Server server : servers) {
			if (server.isActive())
				return server;
		}
		if (!servers.isEmpty()) {
			Server server = servers.get(0);
			server.setActive(true);
			if (saveIfNotfound)
				save();
			return server;
		}
		return null;
	}

	public static Config readConfig(Context context, int docflow_dir,
			int docflow_settings) throws Exception {
		xstream = new XStream();
		xstream.alias("config", Config.class);
		xstream.alias("server", Server.class);
		xstream.autodetectAnnotations(true);

		File dir = new File(context.getString(docflow_dir));
		if (!dir.exists())
			dir.mkdirs();
		config_file = new File(dir, context.getString(docflow_settings));
		if (!config_file.exists()) {
			AssetHelper.CopyAsset(context, dir, config_file.getName());
		}
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(config_file);
			instance = (Config) xstream.fromXML(fis);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return instance;
	}

	public int getSystemid() {
		return systemid;
	}

	public void setSystemid(int systemid) {
		this.systemid = systemid;
	}

	public int getLanguageid() {
		return languageid;
	}

	public void setLanguageid(int languageid) {
		this.languageid = languageid;
	}

}
