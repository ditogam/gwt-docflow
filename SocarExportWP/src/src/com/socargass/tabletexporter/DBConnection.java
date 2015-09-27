package src.com.socargass.tabletexporter;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConnection {
	public static Connection getConnection(String name) throws Exception {
		InitialContext cxt = new InitialContext();
		DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/" + name);
		if (ds == null) {
			throw new Exception("Data source not found!");
		}
		Connection con = ds.getConnection();
		con.setAutoCommit(false);
		return con;
	}
}
