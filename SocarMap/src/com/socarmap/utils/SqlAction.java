package com.socarmap.utils;

public class SqlAction {

	private String name;
	private String sql;

	public SqlAction(String name, String sql) {
		this.name = name;
		this.sql = sql;
	}

	public String getName() {
		return name;
	}

	public String getSql() {
		return sql;
	}
}
