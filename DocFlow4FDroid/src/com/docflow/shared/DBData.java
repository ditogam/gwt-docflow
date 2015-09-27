package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DBData implements IsSerializable {

	protected String tbl_name;
	protected String tbl_caption;
	protected String field_names;
	protected String primary_keys;

	public DBData() {
	}

	public String getTbl_name() {
		return tbl_name;
	}

	public void setTbl_name(String tbl_name) {
		this.tbl_name = tbl_name;
	}

	public String getTbl_caption() {
		return tbl_caption;
	}

	public void setTbl_caption(String tbl_caption) {
		this.tbl_caption = tbl_caption;
	}

	public String getField_names() {
		return field_names;
	}

	public void setField_names(String field_names) {
		this.field_names = field_names;
	}

	public String getPrimary_keys() {
		return primary_keys;
	}

	public void setPrimary_keys(String primary_keys) {
		this.primary_keys = primary_keys;
	}

}
