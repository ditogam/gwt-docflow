package com.docflow.server.test;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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

	private File config_file;

	public static final String ns = null;

	public Config() {
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}

	public void addserver(Server server) {
		if (servers == null)
			servers = new ArrayList<Server>();
		servers.add(server);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private static Config instance = null;

}
