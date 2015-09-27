package com.socarmap.server;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class TileDBCopyProperty implements ObjectFactory {

	private boolean scp;
	private boolean debug;
	private String remote_dir;
	private String host_name;
	private int port;
	private String user_name;
	private String password;
	private int batch_size;
	private int processors;

	public static TileDBCopyProperty load() throws Exception {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			Object obj = envContext.lookup("tilecopy/props");
			return (TileDBCopyProperty) obj;
		} catch (Exception e) {
			throw e;
		}
	}

	public TileDBCopyProperty() {

	}

	public boolean isScp() {
		return scp;
	}

	public void setScp(boolean scp) {
		this.scp = scp;
	}

	public String getRemote_dir() {
		if (!remote_dir.endsWith("/"))
			remote_dir += "/";
		return remote_dir;
	}

	public void setRemote_dir(String remote_dir) {
		this.remote_dir = remote_dir;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Object getObjectInstance(Object obj, Name jndiName, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {
		Reference ref = (Reference) obj;
		Enumeration<?> addrs = ref.getAll();
		TileDBCopyProperty ret = new TileDBCopyProperty();
		while (addrs.hasMoreElements()) {
			RefAddr addr = (RefAddr) addrs.nextElement();
			String name = addr.getType();
			Object value = addr.getContent();
			if (value == null)
				continue;
			if (name.equals("scp"))
				ret.setScp(Boolean.valueOf(value.toString().trim()));
			if (name.equals("debug"))
				ret.setDebug(Boolean.valueOf(value.toString().trim()));
			if (name.equals("port"))
				ret.setPort(Integer.valueOf(value.toString().trim()));
			if (name.equals("host_name"))
				ret.setHost_name(value.toString().trim());
			if (name.equals("remote_dir"))
				ret.setRemote_dir(value.toString().trim());
			if (name.equals("user_name"))
				ret.setUser_name(value.toString().trim());
			if (name.equals("password"))
				ret.setPassword(value.toString().trim());
			if (name.equals("batch_size"))
				ret.setBatch_size(Integer.valueOf(value.toString().trim()));
			if (name.equals("processors"))
				ret.setProcessors(Integer.valueOf(value.toString().trim()));
			System.out.println("name=" + name + " value=" + value);
		}
		return ret;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getBatch_size() {
		return batch_size;
	}

	public void setBatch_size(int batch_size) {
		this.batch_size = batch_size;
	}

	public int getProcessors() {
		return processors;
	}

	public void setProcessors(int processors) {
		this.processors = processors;
	}

}
