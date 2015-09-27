package com.docflow.server.export;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.docflow.server.DocFlowServiceImpl;
import com.docflow.shared.DBData;
import com.docflow.shared.DbExpoResult;

public class LocalConnectionHelper implements IConnectionHelper {

	private static Map<String, String> db_urls = new HashMap<String, String>();

	private static Map<String, Properties> db_props = new HashMap<String, Properties>();

	private static void addUserNameAndPassword(String dbname, String username,
			String password) {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		db_props.put(dbname, props);
	}

	private static void createDbConnectionsProps() throws Exception {
		db_urls.put("DocFlow", "jdbc:postgresql://localhost:54325/docflow");
		db_urls.put("Gass", "jdbc:postgresql://localhost:9996/gass");
		db_urls.put("MAP", "jdbc:postgresql://localhost:54325/socargass");
		addUserNameAndPassword("DocFlow", "gass", "gilelumimani");
		addUserNameAndPassword("Gass", "gass", "gilelumimani");
		addUserNameAndPassword("MAP", "bfuser", "bfuser");
		Class.forName("org.postgresql.Driver");
	}

	static {
		try {
			createDbConnectionsProps();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection createConnection(String dbname) throws Exception {
		return DriverManager.getConnection(db_urls.get(dbname),
				db_props.get(dbname));
	}

	@Override
	public void closeConnection(Connection conn) throws Exception {
		conn.close();

	}

	private static Map<Integer, String> getStatus(String tablename,
			DbExpoResult result) {
		Integer index = 0;
		int ind = 0;
		String caption = "";
		for (DBData iterable_element : result.getDbDatas()) {
			ind++;
			if (iterable_element.getTbl_name().equals(tablename)) {
				caption = iterable_element.getTbl_caption();
				index = ind;
			}
		}
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(index, caption);
		return map;
	}

	public static void main(String[] args) throws Exception {

		long time = System.currentTimeMillis();

		DocFlowServiceImpl.dsDir = new File(System.getProperty("user.dir")
				+ "/war/ds").getAbsolutePath();
		LocalConnectionHelper helper = new LocalConnectionHelper();

		DbExpoResult result = Exporter.createExporterSession(24, helper);
		if (result.getException() != null) {
			System.err.println(result.getException().getDetailed());
			return;
		}
		DbExpoResult last_result = null;
		while (true) {
			Thread.sleep(1000);
			DbExpoResult status = Exporter.getExporterCurrentStatus(result
					.getSession_id());
			last_result = status;
			Map<Integer, String> map = getStatus(status.getTableName(), result);
			int index = map.keySet().iterator().next();
			System.out.println("Tablename=" + status.getTableName() + " index="
					+ index + " caption=" + map.get(index));
			if ((status.getDone() != null && status.getDone().booleanValue())
					|| status.getException() != null)
				break;
		}

		if (last_result.getException() != null) {
			System.err.println(result.getException().getDetailed());
			return;
		}

		System.out.println("Full export time="
				+ (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("test.gz");
		Exporter.writeBytes(result.getSession_id(), fos);
		fos.flush();
		fos.close();
		System.out.println("Full save time="
				+ (System.currentTimeMillis() - time));

	}
}
