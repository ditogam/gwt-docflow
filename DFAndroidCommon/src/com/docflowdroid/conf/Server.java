package com.docflowdroid.conf;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("server")
public class Server implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4870898837002216358L;
	@XStreamAsAttribute
	private boolean active;

	@XStreamAsAttribute
	private String servername;

	@XStreamAsAttribute
	private String serverurl;

	public Server() {
	}

	public Server(String servername, String serverurl) {
		super();
		this.servername = servername;
		this.serverurl = serverurl;
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public String getServerurl() {
		return serverurl;
	}

	public void setServerurl(String serverurl) {
		this.serverurl = serverurl;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}