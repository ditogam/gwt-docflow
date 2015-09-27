package com.docflow.server.export;

import java.sql.Connection;

import com.common.db.DBConnection;

public class WebConnectionHelper implements IConnectionHelper {

	@Override
	public Connection createConnection(String dbname) throws Exception {
		return DBConnection.getConnection(dbname);
	}

	@Override
	public void closeConnection(Connection conn) throws Exception {
		DBConnection.freeConnection(conn);

	}

}
