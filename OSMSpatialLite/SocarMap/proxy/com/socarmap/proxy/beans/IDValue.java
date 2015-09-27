package com.socarmap.proxy.beans;

import java.io.Serializable;

public class IDValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6545356385421582964L;
	private long id;
	private String value;
	private boolean selected;

	public IDValue() {
		// TODO Auto-generated constructor stub
	}

	public IDValue(long id, String value) {
		this.id = id;
		this.value = value;
		selected = false;
	}

	public long getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void toggleChecked() {
		selected = !selected;

	}

	@Override
	public String toString() {
		return value;
	}
}
