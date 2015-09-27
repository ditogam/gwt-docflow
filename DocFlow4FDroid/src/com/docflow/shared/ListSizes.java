package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ListSizes implements IsSerializable {
	/**
	 * 
	 */
	private long last_id;
	private int total_count;
	private int start_row;
	private int end_row;
	boolean generate_sizes;

	public ListSizes() {
		// TODO Auto-generated constructor stub
	}

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}

	public int getStart_row() {
		return start_row;
	}

	public void setStart_row(int start_row) {
		this.start_row = start_row;
	}

	public int getEnd_row() {
		return end_row;
	}

	public void setEnd_row(int end_row) {
		this.end_row = end_row;
	}

	public long getLast_id() {
		return last_id;
	}

	public void setLast_id(long last_id) {
		this.last_id = last_id;
	}

	public boolean isGenerate_sizes() {
		return generate_sizes;
	}

	public void setGenerate_sizes(boolean generate_sizes) {
		this.generate_sizes = generate_sizes;
	}

}
