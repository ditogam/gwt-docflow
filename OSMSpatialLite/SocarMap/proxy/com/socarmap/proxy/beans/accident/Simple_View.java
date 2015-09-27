package com.socarmap.proxy.beans.accident;

import java.io.Serializable;

public class Simple_View extends Accident_Default implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801166858729257072L;

	private String current_status_text;
	private String cus_name;

	public Simple_View() {
	}

	public String getCurrent_status_text() {
		return current_status_text;
	}

	public String getCus_name() {
		return cus_name;
	}

	public void setCurrent_status_text(String current_status_text) {
		this.current_status_text = current_status_text;
	}

	public void setCus_name(String cus_name) {
		this.cus_name = cus_name;
	}

}
