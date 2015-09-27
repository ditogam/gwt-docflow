package com.docflow.shared.docflow;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NewDocuments implements IsSerializable {
	private long session_id;
	private long full_count;
	private HashMap<Integer, Integer> systems;

	public NewDocuments() {
	}

	public long getSession_id() {
		return session_id;
	}

	public void setSession_id(long session_id) {
		this.session_id = session_id;
	}

	public long getFull_count() {
		return full_count;
	}

	public void setFull_count(long full_count) {
		this.full_count = full_count;
	}

	public HashMap<Integer, Integer> getSystems() {
		return systems;
	}

	public void setSystems(HashMap<Integer, Integer> systems) {
		this.systems = systems;
	}

}
