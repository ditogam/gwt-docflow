package com.socarmap.proxy.beans;

import java.io.Serializable;
import java.util.TreeMap;

public class MyTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6765930850257168039L;
	private TreeMap<Long, String> mp;

	public MyTree() {
		// TODO Auto-generated constructor stub
	}

	public TreeMap<Long, String> getMp() {
		return mp;
	}

	public void setMp(TreeMap<Long, String> mp) {
		this.mp = mp;
	}
}
