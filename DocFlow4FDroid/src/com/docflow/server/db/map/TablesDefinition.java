package com.docflow.server.db.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.docflow.server.DMIUtils;

public class TablesDefinition {
	private String table_name;
	private String operation_id;
	private FieldDefinition[] fieldDefinitions;
	private boolean makeAnyway;

	public TablesDefinition(String table_name, String operation_id,
			FieldDefinition[] fieldDefinitions, boolean makeAnyway) {
		super();
		this.table_name = table_name;
		this.operation_id = operation_id;
		this.fieldDefinitions = fieldDefinitions;
		this.makeAnyway = makeAnyway;
	}

	public void addCounts(Map<String, Object> criteria,
			HashMap<String, Integer> result) throws Exception {
		Map<?, ?> mp = DMIUtils.findRecordByCriteria("MapDownloaderDS",
				operation_id, criteria);
		if (mp != null && mp.containsKey("cnt")) {
			int cnt = DMIUtils.getRowValueLong(mp.get("cnt")).intValue();
			if (cnt > 0)
				result.put(table_name, cnt);
		}
	}

	public int insert(Connection connection, Map<String, Object> criteria)
			throws Exception {
		List<Map<?, ?>> list = DMIUtils.findRecordsByCriteria(
				"MapDownloaderDS", operation_id, criteria);
		int count = list.size();

		String insert_statement = "insert into \"" + table_name + "\"(";
		String asks = "";
		for (FieldDefinition fd : fieldDefinitions) {
			if (!asks.isEmpty()) {
				insert_statement += ",";
				asks += ",";
			}
			insert_statement += "\"" + fd.getToField() + "\"";
			asks += "?";
		}

		insert_statement += ") values (" + asks + ")";

		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(insert_statement);
			for (Map<?, ?> map : list) {
				for (int i = 0; i < fieldDefinitions.length; i++) {
					FieldDefinition fd = fieldDefinitions[i];
					fd.setValue(stmt, i + 1, map);

				}
				stmt.executeUpdate();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return count;
	}

	public boolean isMakeAnyway() {
		return makeAnyway;
	}
}
