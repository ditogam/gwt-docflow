package com.docflow.and.impl.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public abstract class ADBResultObjectExecutor<T> {
	private Connection conn = null;
	private String connectionName;
	private DbParam[] params;

	public ADBResultObjectExecutor(ExecutorConstructor constructor) {
		conn = constructor.getConn();
		connectionName = constructor.getConnectionName();
		params = constructor.getParams();
	}

	public Connection getConnection() {
		return conn;
	}

	public ADBResultObjectExecutor<T> setConnection(Connection conn) {
		this.conn = conn;
		return this;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public DbParam[] getParams() {
		return params;
	}

	public ADBResultObjectExecutor<T> setParams(DbParam... params) {
		this.params = params;
		return this;
	}

	public void closeConnection() throws Throwable {
		conn.close();
		conn = null;
	}

	public abstract T getResult(ResultSet rs) throws Exception;

	public ArrayList<T> getObjectsFromDB(String sql) throws Exception {
		return DBConnectionAnd.getObjectsFromDB(this, sql);
	}

	public T getObjectFromDB(String sql) throws Exception {
		return DBConnectionAnd.getObjectFromDB(this, sql);
	}

}
