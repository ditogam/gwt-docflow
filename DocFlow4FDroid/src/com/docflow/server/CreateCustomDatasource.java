package com.docflow.server;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.docflow.server.db.MDBConnection;
import com.docflow.shared.ClSelection;
import com.docflow.shared.UserObject;

public class CreateCustomDatasource {
	// private static HashMap<String, DataSource> dsMap = new HashMap<String,
	// DataSource>();
	// private static HashMap<String, DataSource> dsDebugMap = new
	// HashMap<String, DataSource>();
	public static String dsNames = null;

	public static String createDatasources(Connection conn, String path) {

		if (dsNames != null)
			return dsNames;
		String ret = "";
		if (!ClSelection.reloaded)
			MDBConnection.reloadClSelections(conn);
		// dsMap.clear();

		File dir = new File(path);

		File[] delFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().startsWith(ClSelection.DS_PREFIX);
			}
		});

		for (File file : delFiles) {
			file.delete();
		}

		for (final ClSelection clSelection : ClSelection.SELECTIONS) {

			String id = clSelection.getID();

			try {
				FileWriter fw = new FileWriter(new File(dir, id + ".ds.xml"));
				fw.write(clSelection.createXML());
				fw.flush();
				fw.close();
				// dsMap.put(id, DataSource.fromXML(clSelection.createXML()));
				if (ret.length() > 0)
					ret += ",";
				ret += id;
			} catch (Exception e) {

			}
		}

		HashMap<String, String> custom = MDBConnection.getCustomDatasources(false, conn);
		for (String key : custom.keySet()) {
			try {
				FileWriter fw = new FileWriter(new File(dir, key + ".ds.xml"));
				fw.write(custom.get(key));
				fw.flush();
				fw.close();
				// dsMap.put(key, DataSource.fromXML(custom.get(key)));
				if (ret.length() > 0)
					ret += ",";
				ret += key;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// DynamicDSGenerator generator = new DynamicDSGenerator() {
		// @Override
		// public DataSource getDataSource(String id, DSRequest dsRequest) {
		// DataSource ds = dsMap.get(id);
		// return ds;
		// }
		// };
		// DataSource.removeDynamicDSGenerator(ClSelection.DS_PREFIX);
		// DataSource.removeDynamicDSGenerator(ClSelection.DBCDS_PREFIX);
		// DataSource.addDynamicDSGenerator(generator, ClSelection.DS_PREFIX);
		// DataSource.addDynamicDSGenerator(generator,
		// ClSelection.DBCDS_PREFIX);
		dsNames = ret;
		return ret;
	}

	public static void createDebugDataSources(UserObject uo, Connection conn, String path) {
		String fullDS = createDatasources(conn, path);
		uo.setDatasourceNames(new ArrayList<String>());
		// if (uo.isDebug_ds()) {
		// // dsDebugMap.clear();
		// HashMap<String, String> custom = MDBConnection
		// .getCustomDatasources(true, conn);
		// for (String key : custom.keySet()) {
		// try {
		// String dsName = ClSelection.DEV_PREFIX + key;
		// String xml = custom.get(key);
		// xml = xml.replaceAll("\"" + key + "\"", "\"" + dsName
		// + "\"");
		// dsDebugMap.put(dsName, DataSource.fromXML(xml));
		// if (fullDS.length() > 0)
		// fullDS += ",";
		// fullDS += dsName;
		// uo.getDatasourceNames().add(key);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// DynamicDSGenerator generator = new DynamicDSGenerator() {
		// @Override
		// public DataSource getDataSource(String id, DSRequest dsRequest) {
		// DataSource ds = dsDebugMap.get(id);
		// return ds;
		// }
		// };
		// DataSource.removeDynamicDSGenerator(ClSelection.DEV_DBCDS_PREFIX);
		// DataSource.addDynamicDSGenerator(generator,
		// ClSelection.DEV_DBCDS_PREFIX);
		// }

		uo.setFullDS(fullDS);
	}

}
