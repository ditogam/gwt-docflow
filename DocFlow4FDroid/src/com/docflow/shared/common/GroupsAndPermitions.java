package com.docflow.shared.common;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupsAndPermitions implements IsSerializable {

	/**
	 * 
	 */
	private ArrayList<User_Group> user_Groups;
	private ArrayList<PermitionItem> permitionItems;

	public ArrayList<PermitionItem> getPermitionItems() {
		return permitionItems;
	}

	public ArrayList<User_Group> getUser_Groups() {
		return user_Groups;
	}

	public void setPermitionItems(ArrayList<PermitionItem> permitionItems) {
		this.permitionItems = permitionItems;
	}

	public void setUser_Groups(ArrayList<User_Group> user_Groups) {
		this.user_Groups = user_Groups;
	}

}
