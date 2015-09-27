package com.spatialite.beans;

public class IDValue {
	private long id;
	private String value;
	private boolean selected;

	public IDValue(long id, String value) {
		this.id = id;
		this.value = value;
		selected=false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	@Override
	public String toString() {
		return value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void toggleChecked() {
		selected=!selected;
		
	}
}
