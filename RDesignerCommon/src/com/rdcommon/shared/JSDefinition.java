package com.rdcommon.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class JSDefinition implements IsSerializable {
	private int id;
	private String js_text;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJs_text() {
		return js_text;
	}

	public void setJs_text(String js_text) {
		this.js_text = js_text;
	}

}
