package com.docflowdroid.common.ds;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DsField implements IsSerializable {
	private String name;
	private String type;
	private String title;
	private Long length;
	private boolean required;

	public DsField() {
	}

	public DsField(String name, String type) {
		setName(name);
		setType(type);

	}

	public DsField(String name, String type, String title) {
		setName(name);
		setType(type);
		setTitle(title);

	}

	public DsField(String name, String type, String title, Long length) {
		setName(name);
		setType(type);
		setTitle(title);
		setLength(length);

	}

	public DsField(String name, String type, String title, Long length,
			boolean required) {
		setName(name);
		setType(type);
		setTitle(title);
		setLength(length);
		setRequired(required);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}
