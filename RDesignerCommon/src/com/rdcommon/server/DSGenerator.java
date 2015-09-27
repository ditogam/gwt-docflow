package com.rdcommon.server;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.datasource.DataSourceManager;
import com.isomorphic.datasource.DynamicDSGenerator;
import com.rdcommon.server.generator.ClassGenerator;
import com.rdcommon.server.generator.DynamicGenerator;
import com.rdcommon.shared.GlobalValues;
import com.rdcommon.shared.ds.DSDefinition;

public class DSGenerator {

	public static GlobalValues globalValues = null;
	private static TreeMap<String, DataSource> dsMap = new TreeMap<String, DataSource>();

	private static DSGenerator instance;

	public static DSGenerator getInstance() {
		if (instance == null)
			instance = new DSGenerator();
		return instance;
	}

	private DSGenerator() {

		DataSource.addDynamicDSGenerator(new DynamicDSGenerator() {
			@Override
			public DataSource getDataSource(String id, DSRequest dsRequest) {
				DataSource ds = dsMap.get(id);
				return ds;
			}
		}, DSDefinition.DSDefinition_EXTN);
	}

	public void registerDSs(ArrayList<DSDefinition> definitions) {
		if (definitions != null && !definitions.isEmpty()) {
			for (DSDefinition dsDefinition : definitions) {
				registerDS(dsDefinition, false);
			}
		}
	}

	public void registerDS(DSDefinition definition, boolean temp) {
		String tmp_def = definition.getDsName();
		if (temp)
			definition.setDsName(tmp_def + DSDefinition.TMP);
		String xml = definition.createDSXML();
		System.out.println(xml);
		dsMap.remove(DSDefinition.DSDefinition_EXTN + definition.getDsName());
		try {
			DataSource ds = DataSource.fromXML(xml);
			dsMap.put(DSDefinition.DSDefinition_EXTN + definition.getDsName(),
					ds);
			DataSourceManager.getDataSource(DSDefinition.DSDefinition_EXTN
					+ definition.getDsName(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		definition.setDsName(tmp_def);
	}

	public static String getDSDefiniString() {
		try {
			if (globalValues == null) {
				globalValues = DynamicGenerator.generate();
				getInstance().registerDSs(globalValues.getDsDefinitions());
				ClassGenerator.generateClasses(globalValues
						.getClassDefinitions());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(Test.ds);

		String ret = "";
		Set<String> keys = dsMap.keySet();
		for (String key : keys) {
			if (ret.length() > 0)
				ret += ",";
			ret += key;
		}

		return ret;
	}
}
