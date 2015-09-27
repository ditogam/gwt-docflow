package com.docflow.and.impl.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;

import jsqlite.JDBC2z.JDBCConnection;

import com.docflow.and.impl.SQLCustomFuntion;
import com.docflowdroid.DocFlow;
import com.docflowdroid.R;

public class DBConnectionAnd {

	public static Connection getExportedDB() throws Exception {
		return getConnection("exported_db");
	}

	public static Connection getDocFlowDB() throws Exception {
		return getConnection("docflow_db");
	}

	public static ArrayList<SQLCustomFuntion> customFuntions = null;

	public static Connection getConnection(String db) throws Exception {
		File dir = new File(DocFlow.activity.getString(R.string.docflow_dir)
				+ "db");
		File fl = new File(dir, DocFlow.activity.getString(DocFlow
				.getResourceId("string." + db)));
		JDBCConnection conn = new JDBCConnection("jdbc:jsqlite:"
				+ fl.getAbsolutePath(), "UTF8", null, null, null);
		return conn;
	}

	public static <T> ArrayList<T> getObjectsFromDB(
			ADBResultObjectExecutor<T> executor, String sql) throws Exception {
		return getObjectsFromDB(executor, sql, false, false);
	}

	public static <T> T getObjectFromDB(ADBResultObjectExecutor<T> executor,
			String sql) throws Exception {
		ArrayList<T> list = getObjectsFromDB(executor, sql, true, false);
		if (list.size() == 1)
			return list.get(0);
		else
			return null;
	}

	public static <T> void execute(ADBResultObjectExecutor<T> executor,
			String sql) throws Exception {
		getObjectsFromDB(executor, sql, true, true);
	}

	private static <T> ArrayList<T> getObjectsFromDB(
			ADBResultObjectExecutor<T> executor, String sql, boolean single,
			boolean execute) throws Exception {

		Connection localConn = null;
		Connection execConn = executor.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<T> result = new ArrayList<T>();
		try {
			localConn = execConn == null ? getConnection(executor
					.getConnectionName()) : execConn;
			sql = sql.replaceAll("feeder.", "");
			if (customFuntions != null && localConn instanceof JDBCConnection) {
				for (SQLCustomFuntion cf : customFuntions) {
					sql = cf.addDatabaseFunction(
							((JDBCConnection) localConn).getSQLiteDatabase(),
							sql);
				}
			}

			ps = localConn.prepareStatement(sql);

			DbParam[] params = executor.getParams();
			if (params != null && sql.contains("?"))
				for (int i = 0; i < params.length; i++) {
					DbParam po = params[i];
					if (po == null)
						continue;
					Object val = po.getValue();
					if (val == null)
						ps.setNull(i + 1, po.getType());
					else
						ps.setObject(i + 1, val, po.getType());
				}
			if (execute)
				ps.execute();
			else {
				rs = ps.executeQuery();
				while (rs.next()) {
					T val = executor.getResult(rs);
					if (val != null)
						result.add(val);
					if (single)
						break;
				}
			}
		} finally {
			try {
				closeAll(execConn == null ? localConn : null, rs, ps);
			} catch (Exception e) {

			}
		}

		return result;
	}

	public static void closeAll(Wrapper... closables) {
		for (Wrapper wrapper : closables) {
			if (wrapper == null)
				continue;
			try {
				if (wrapper instanceof Connection) {
					if (!((Connection) wrapper).getAutoCommit())
						((Connection) wrapper).rollback();
					((Connection) wrapper).close();
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (wrapper instanceof Statement) {
					((Statement) wrapper).close();
					continue;
				}
				if (wrapper instanceof ResultSet) {
					((ResultSet) wrapper).close();
					continue;
				}

			} catch (Throwable e) {

			}
		}
	}

}
