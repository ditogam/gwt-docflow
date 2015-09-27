package com.docflowdroid.map;

import java.util.ArrayList;

public class TableDefination {
	private String tableName;
	private String idName;
	private String[] fieldNames;
	private String[][] geomFileds;

	public TableDefination(String tableName, String idName,
			String[] fieldNames, String[][] geomFileds) {
		super();
		this.tableName = tableName;
		this.idName = idName;
		this.fieldNames = fieldNames;
		this.geomFileds = geomFileds;
	}

	public String createDeleteStatement(String alise) {
		String ret = "delete from \"" + tableName
				+ "\"  where exists (select 1 from " + alise + ".\""
				+ tableName + "\" t2 where t2.\"" + idName + "\"=\""
				+ tableName + "\".\"" + idName + "\")";
		return ret;
	}

	public String createInsertStatement(String alise) {
		String ret = "insert into \"" + tableName + "\"(";
		String column_names = "";

		for (String fieldName : fieldNames) {
			if (!column_names.isEmpty())
				column_names += ",";
			column_names += "\"" + fieldName + "\"";
		}
		ret += column_names + ") select " + column_names + " from " + alise
				+ ".\"" + tableName + "\"";
		return ret;
	}

	public ArrayList<String> createUpdateStatement() {
		ArrayList<String> list = new ArrayList<String>();
		for (String[] fieldName : geomFileds) {
			String column_names = "\"" + fieldName[1] + "\"=GeomFromText(\""
					+ fieldName[0] + "\",4326), \"" + fieldName[0]
					+ "\"=null    ";
			String ret = "update \"" + tableName + "\" set " + column_names
					+ " where \"" + fieldName[0] + "\" is not null";
			list.add(ret);
			list.add("SELECT RebuildGeometryTriggers('" + tableName + "', '"
					+ fieldName[1] + "')");
			list.add("SELECT CreateSpatialIndex('" + tableName + "', '"
					+ fieldName[1] + "')");
			list.add("SELECT RecoverSpatialIndex('" + tableName + "', '"
					+ fieldName[1] + "',0)");
			list.add("SELECT RecoverSpatialIndex('" + tableName + "', '"
					+ fieldName[1] + "',1)");
		}

		return list;
	}

	public String getTableName() {
		return tableName;
	}
}
