package com.osmreader;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import com.common.db.DBConnection;

public class OSMPGReader implements CSVTableModel {

	private static ArrayList<String> getTableNames(Connection con)
			throws Exception {
		ResultSet rs = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			rs = con.getMetaData().getTables(null, null, null,
					new String[] { "TABLE" });
			while (rs.next()) {
				String tablename = rs.getString("TABLE_NAME");
				if (tablename.startsWith("planet_osm_")) {
					System.out.println();
					result.add(rs.getString("TABLE_SCHEM") + "." + tablename);
				}
			}
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		return result;

	}

	private static ArrayList<String> getColumnNames(Connection con,
			String tableName) throws Exception {
		ResultSet rs = null;
		Statement statement = null;

		ArrayList<String> result = new ArrayList<String>();
		try {
			String sql = "select * from " + tableName + " where 1=2";
			statement = con.createStatement();
			rs = statement.executeQuery(sql);
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i <= numberOfColumns; i++) {
				String colName = rsMetaData.getColumnName(i);
				String colType = rsMetaData.getColumnTypeName(i);
				if (!colName.startsWith("addr:") && colType.equals("text")
						&& !colName.equalsIgnoreCase("name")
						&& !colName.equalsIgnoreCase("ref")
						&& !colName.equalsIgnoreCase("ele")
						&& !colName.equalsIgnoreCase("population")
						&& !colName.equalsIgnoreCase("shop"))
					result.add(colName);

			}
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}

			if (statement != null)
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		return result;

	}

	private static ArrayList<String> getColumnValues(Connection con,
			String tableName, String columnName) throws Exception {
		ResultSet rs = null;
		Statement statement = null;

		ArrayList<String> result = new ArrayList<String>();
		try {
			String sql = "select distinct \"" + columnName + "\" val from "
					+ tableName + " where \"" + columnName + "\" is not null";
			statement = con.createStatement();
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				String value = rs.getString("val");
				if (value != null && value.trim().length() > 0)
					result.add(value);
			}

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}

			if (statement != null)
				try {
					rs.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		return result;

	}

	public static void main(String[] args) throws Exception {
		Connection con = null;
		try {
			con = DBConnection.getConnection("OSM");
			ArrayList<String> tableNames = getTableNames(con);
			for (String tablename : tableNames) {
				System.out.println(tablename);
				ArrayList<String> colNames = getColumnNames(con, tablename);
				ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
				for (String colName : colNames) {
					ArrayList<String> colData = getColumnValues(con, tablename,
							colName);
					if (colData.size() > 100)
						System.out.println(colName);
					data.add(colData);
				}
				OSMPGReader model = new OSMPGReader(colNames, data);
				CSVWriter w = new CSVWriter();
				w.write(new File(tablename + ".csv"), model, '\t');
			}

		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
	}

	private ArrayList<String> columnNames;
	private ArrayList<ArrayList<String>> data;

	public OSMPGReader(ArrayList<String> columnNames,
			ArrayList<ArrayList<String>> data) {
		this.columnNames = columnNames;
		this.data = data;

	}

	@Override
	public String getValueAt(int row, int column) {
		try {
			return data.get(column).get(row);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	@Override
	public int getColumnCount() {

		return columnNames.size();
	}

	@Override
	public int getRowCount() {
		int rowCount = 0;
		for (ArrayList<String> arrayList : data) {
			rowCount = Math.max(arrayList.size(), rowCount);
		}
		return rowCount;
	}

	@Override
	public String getColumnName(int column) {
		try {
			return columnNames.get(column);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	@Override
	public boolean areColumnsVisible() {
		return true;
	}
}
