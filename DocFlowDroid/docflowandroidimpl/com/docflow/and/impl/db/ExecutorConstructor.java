package com.docflow.and.impl.db;

import java.sql.Connection;

public class ExecutorConstructor {
	private Connection conn = null;
	private String connectionName;
	private DbParam[] params;
	public int startRow;
	public int endRow;

	public ExecutorConstructor(DbParam... params) {
		this("exported_db", params);

	}

	public ExecutorConstructor(String connectionName, DbParam... params) {
		this.connectionName = connectionName;
		setParams(params);

	}

	public ExecutorConstructor(Connection conn, DbParam... params) {
		this(params);
		this.conn = conn;
	}

	public ExecutorConstructor setConn(Connection conn) {
		this.conn = conn;
		return this;
	}

	public ExecutorConstructor setConnectionName(String connectionName) {
		this.connectionName = connectionName;
		return this;
	}

	public ExecutorConstructor setParams(DbParam... params) {
		this.params = params;
		return this;
	}

	public Connection getConn() {
		return conn;
	}

	public DbParam[] getParams() {
		return params;
	}

	public String getConnectionName() {
		return connectionName;
	}

}
