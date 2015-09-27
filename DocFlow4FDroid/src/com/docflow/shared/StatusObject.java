package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StatusObject implements IsSerializable {

	private int initial_status;
	private int approved_status;
	private int applied_status;
	private int error_status;
	private boolean check_for_statuses;
	private int next_status;

	public int getInitial_status() {
		return initial_status;
	}

	public void setInitial_status(int initial_status) {
		this.initial_status = initial_status;
	}

	public int getApproved_status() {
		return approved_status;
	}

	public void setApproved_status(int approved_status) {
		this.approved_status = approved_status;
	}

	public int getApplied_status() {
		return applied_status;
	}

	public void setApplied_status(int applied_status) {
		this.applied_status = applied_status;
	}

	public int getError_status() {
		return error_status;
	}

	public void setError_status(int error_status) {
		this.error_status = error_status;
	}

	public boolean isCheck_for_statuses() {
		return check_for_statuses;
	}

	public void setCheck_for_statuses(boolean check_for_statuses) {
		this.check_for_statuses = check_for_statuses;
	}

	public int getNext_status() {
		return next_status;
	}

	public void setNext_status(int next_status) {
		this.next_status = next_status;
	}

}
