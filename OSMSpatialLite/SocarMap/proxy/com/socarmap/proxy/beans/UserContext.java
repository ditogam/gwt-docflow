package com.socarmap.proxy.beans;

import java.io.Serializable;
import java.util.List;

public class UserContext extends SUserContext implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 4729681023636009917L;

	private UserData userData;
	private List<String> permitions;
	private Classifiers classifiers;
	private transient SUserContext short_value;

	public static final String INVALID_USER_NAME_AND_PWD = "Invalid user name and(or) password";

	public UserContext() {
		// TODO Auto-generated constructor stub
	}

	public Classifiers getClassifiers() {
		return classifiers;
	}

	public List<String> getPermitions() {
		return permitions;
	}

	public SUserContext getShort_value() {
		if (short_value == null) {
			short_value = new SUserContext();
			short_value.setLanguage_id(getLanguage_id());
			short_value.setRegion_id(getRegion_id());
			short_value.setSubregion_id(getSubregion_id());
			short_value.setUser_id(getUser_id());
			short_value.setUser_name(getUser_name());
		}
		return short_value;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setClassifiers(Classifiers classifiers) {
		this.classifiers = classifiers;
	}

	public void setPermitions(List<String> permitions) {
		this.permitions = permitions;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}
