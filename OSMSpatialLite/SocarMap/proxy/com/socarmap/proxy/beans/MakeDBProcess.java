package com.socarmap.proxy.beans;

import java.io.Serializable;

public class MakeDBProcess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7463336951019474985L;
	private String sessionID;
	private String[] operations;
	private boolean shouldCopyTiles;
	private int aproximatesize;
	private int count;
	private int filesize;

	public int getAproximatesize() {
		return aproximatesize;
	}

	public int getCount() {
		return count;
	}

	public int getFilesize() {
		return filesize;
	}

	public String[] getOperations() {
		return operations;
	}

	public String getSessionID() {
		return sessionID;
	}

	public boolean isShouldCopyTiles() {
		return shouldCopyTiles;
	}

	public void setAproximatesize(int aproximatesize) {
		this.aproximatesize = aproximatesize;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public void setOperations(String[] operations) {
		this.operations = operations;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public void setShouldCopyTiles(boolean shouldCopyTiles) {
		this.shouldCopyTiles = shouldCopyTiles;
	}

}
