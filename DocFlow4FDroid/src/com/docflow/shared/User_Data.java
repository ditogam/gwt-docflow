package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User_Data implements IsSerializable {

	private int user_id;
	private String pwith;
	private String pheight;
	private String pborder;
	private int showtimeout;
	private String html;

	public User_Data() {

	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getPwith() {
		return pwith;
	}

	public void setPwith(String pwith) {
		this.pwith = pwith;
	}

	public String getPheight() {
		return pheight;
	}

	public void setPheight(String pheight) {
		this.pheight = pheight;
	}

	public String getPborder() {
		return pborder;
	}

	public void setPborder(String pborder) {
		this.pborder = pborder;
	}

	public int getShowtimeout() {
		return showtimeout;
	}

	public void setShowtimeout(int showtimeout) {
		this.showtimeout = showtimeout;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
