package com.spatialite.activities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Importer {
	public static void main(String[] args) {
		importData();
	}

	private static void importData() {
		Connection cPostgres = null;
		Connection cSQLite = null;
		Statement stmtPG = null;
		PreparedStatement stmtSQLite = null;
		ResultSet rsPG = null;
		try {
			Class.forName("org.postgresql.Driver");
			Class.forName("org.sqlite.JDBC");
			cPostgres = DriverManager.getConnection(
					"jdbc:postgresql://localhost:9996/gass", "gass",
					"gilelumimani");
			cSQLite = DriverManager
					.getConnection("jdbc:sqlite:/Users/dito/Documents/android/spatialite-android/spatialite-android/socarmap.sqlite");
			cSQLite.setAutoCommit(false);
			stmtPG = cPostgres.createStatement();

			stmtSQLite = cSQLite
					.prepareStatement("insert into customers (cusid, cusname,region, raion, zone,cityname,streetname, home, "
							+ "flat, scopename, subregionid, regionid, cityid, streetid, cusstatusid, custypeid, loan) values("
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + ")");

			ResultSet rs = cSQLite.createStatement().executeQuery(
					"select cusid from customers  where zone=999999");
			while (rs.next()) {
				System.out.println(rs.getLong(1));
			}
			rs.getStatement().close();
			rs.close();
			rsPG = stmtPG.executeQuery("select * from v_customer_full");
			while (rsPG.next()) {
				int paramindex = 1;
				stmtSQLite.setLong(paramindex++, rsPG.getLong("cusid"));
				stmtSQLite.setString(paramindex++, rsPG.getString("cusname"));
				stmtSQLite.setString(paramindex++, rsPG.getString("region"));
				stmtSQLite.setString(paramindex++, rsPG.getString("raion"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("zone"));
				stmtSQLite.setString(paramindex++, rsPG.getString("cityname"));
				stmtSQLite
						.setString(paramindex++, rsPG.getString("streetname"));
				stmtSQLite.setString(paramindex++, rsPG.getString("home"));
				stmtSQLite.setString(paramindex++, rsPG.getString("flat"));
				stmtSQLite.setString(paramindex++, rsPG.getString("scopename"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("subregionid"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("regionid"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("cityid"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("streetid"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("cusstatusid"));
				stmtSQLite.setLong(paramindex++, rsPG.getLong("custypeid"));
				stmtSQLite.setString(paramindex++, rsPG.getString("loan"));
				stmtSQLite.execute();
				
			}
			// stmtSQLite.executeBatch();
			cSQLite.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cPostgres != null)
				try {
					cPostgres.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			if (cSQLite != null)
				try {
					cSQLite.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			if (stmtPG != null)
				try {
					stmtPG.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			if (stmtSQLite != null)
				try {
					stmtSQLite.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			if (rsPG != null)
				try {
					rsPG.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
		}
	}

}
