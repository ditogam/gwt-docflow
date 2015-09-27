package com.docflow.server.export;

import java.sql.Connection;

public interface IConnectionHelper {
	Connection createConnection(String dbname) throws Exception;

	void closeConnection(Connection conn) throws Exception;
}
