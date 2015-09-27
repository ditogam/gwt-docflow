package com.socarmap.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jsqlite.Database;
import jsqlite.Stmt;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.socarmap.LoginActivity;
import com.socarmap.MainActivity;
import com.socarmap.helper.ConnectionHelper;
import com.socarmap.helper.GeoPointHelper;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.MGeoPoint;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.proxy.beans.UserData;

public class DBSettingsLoader {
	private static DBSettingsLoader instance;

	public static DBSettingsLoader getInstance() {
		return instance;
	}

	public static DBSettingsLoader initInstance(String dbPath) throws Exception {
		if (instance == null)
			instance = new DBSettingsLoader(dbPath);
		return instance;
	}

	private static int[] str2IntArray(String val) {
		ArrayList<Integer> intArr = new ArrayList<Integer>();
		try {
			for (String a : val.trim().split(",")) {
				try {
					intArr.add(Integer.valueOf(a.trim()));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		int[] arr = new int[intArr.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = intArr.get(i);
		}
		return arr;
	}

	private String dbPath;

	private DBSettingsLoader(String dbPath) throws Exception {
		this.dbPath = dbPath;
		if (ConnectionHelper.userContext != null
				&& ConnectionHelper.userContext.getClassifiers() != null) {
			DBLoader.demage_types = ConnectionHelper.userContext
					.getClassifiers().getDemage_types();
			jsqlite.Database mDatabase = new Database();
			try {
				HashMap<Long, String> demage_types = DBLoader.demage_types;
				mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
				execCommand("delete from demage_types", mDatabase);
				for (Long key : demage_types.keySet()) {
					execCommand(
							"insert into demage_types(dtype_id, dtype_name) values ("
									+ key + ",'" + demage_types.get(key) + "')",
							mDatabase);
				}

			} catch (Exception e) {
			} finally {

				mDatabase.close();
			}

		} else {
			jsqlite.Database mDatabase = new Database();
			try {
				mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
				DBLoader.getMap(mDatabase,
						"SELECT dtype_id, dtype_name FROM demage_types ", 0, 1,
						null, DBLoader.demage_types, null);

			} finally {
				mDatabase.close();
			}
		}
	}

	public jsqlite.Database beginTransaction(String dbPath) throws Exception {
		if (dbPath == null)
			dbPath = this.dbPath;
		jsqlite.Database mDatabase = new Database();
		mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
		execCommand("BEGIN TRANSACTION", mDatabase);
		return mDatabase;
	}

	public void deleteNewBuilding(String building_add_id,
			jsqlite.Database aDatabase) throws Exception {
		jsqlite.Database mDatabase = aDatabase == null ? new Database()
				: aDatabase;
		Stmt stmtDelete = null;
		try {
			if (aDatabase == null)
				mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String sql = "delete from newbuilding where building_id=?";

			stmtDelete = mDatabase.prepare(sql);
			stmtDelete.bind(1, building_add_id);
			stmtDelete.step();

		} finally {
			try {
				stmtDelete.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (aDatabase == null)
					mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void execCommand(String command, jsqlite.Database mDatabase)
			throws Exception {
		Stmt stmt = mDatabase.prepare(command);
		stmt.step();
		stmt.close();
	}

	public String getDbPath() {
		return dbPath;
	}

	public DemageDescription getDemageDescription(int id) throws Exception {
		String sql = "select demage_type, time, description from demage_description where id=?";
		jsqlite.Database mDatabase = new Database();
		Stmt stmtSelect = null;

		Stmt stmtFiles = null;
		DemageDescription result = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			stmtSelect = mDatabase.prepare(sql);
			int index = 1;
			stmtSelect.bind(index++, id);
			if (stmtSelect.step()) {
				result = new DemageDescription();
				result.setId(id);
				result.setDemage_type(stmtSelect.column_int(0));
				result.setDescription(stmtSelect.column_string(2));
				result.setTime(stmtSelect.column_long(1));

			}

			if (result != null) {
				sql = "select data from demage_description_item where dd_id=?";
				stmtFiles = mDatabase.prepare(sql);
				stmtFiles.bind(1, id);
				ArrayList<byte[]> bytes = new ArrayList<byte[]>();
				while (stmtFiles.step()) {
					bytes.add(stmtFiles.column_bytes(0));
				}
				result.setBytes(bytes);
			}

			return result;

		} finally {
			try {
				stmtFiles.close();
			} catch (Exception e) {
			}
			try {
				stmtSelect.close();
			} catch (Exception e) {
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
			}
		}
	}

	public NewBuilding getForNewBuilding(String building_add_id)
			throws Exception {
		ArrayList<NewBuilding> res = getForNewBuildings(building_add_id);
		if (res != null && !res.isEmpty())
			return res.get(0);
		return null;
	}

	public ArrayList<NewBuilding> getForNewBuildings(String building_add_id)
			throws Exception {
		ArrayList<NewBuilding> result = new ArrayList<NewBuilding>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT  p_x, p_y,cus_ids, building_id,ppcityid,pcityid \n");
		sql.append("FROM newbuilding c where user_id>0\n");

		if (building_add_id != null)
			sql.append(" and building_id=? ");

		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql.toString());
			if (building_add_id != null)
				stmt.bind(1, building_add_id);

			while (stmt.step()) {

				String cusids = stmt.column_string(2);

				result.add(new NewBuilding(stmt.column_string(3),
						GeoPointHelper.toMGeoPoint(stmt.column_int(0),
								stmt.column_int(1)), str2IntArray(cusids),
						cusids, stmt.column_int(4), stmt.column_int(5)));
			}
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public long getLastUpdated(Context context) {
		long lastUpdated = 0;

		SQLiteConnection connectionHelper = null;
		SQLiteDatabase database = null;

		try {
			connectionHelper = new SQLiteConnection(context, new File(dbPath));
			database = connectionHelper.getReadableDatabase();
			Cursor cursor = database.query("lastupdated",
					"updatetime".split(","), null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				long lastupdated = cursor.getLong(0);
				lastUpdated = Math.max(lastUpdated, lastupdated);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Throwable e) {
		} finally {
			try {
				database.close();
			} catch (Exception e2) {
			}
			try {
				connectionHelper.close();
			} catch (Exception e2) {
			}
		}

		return lastUpdated;
	}

	public ArrayList<CusMeter> loadDemages() throws Exception {
		ArrayList<CusMeter> list = new ArrayList<CusMeter>();
		StringBuffer sql = new StringBuffer();
		sql.append("select id,p_x,p_y,dtype_name from demage_description d \n");
		sql.append("inner join demage_types dt on dt.dtype_id=d.demage_type");

		jsqlite.Database mDatabase = new Database();
		jsqlite.Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql.toString());

			while (stmt.step()) {

				MGeoPoint gp = new MGeoPoint(
						(int) (stmt.column_double(2) * 1E6),
						(int) (stmt.column_double(1) * 1E6));

				list.add(new CusMeter(stmt.column_int(0), stmt.column_int(0),
						"", "", "", "", 0, stmt.column_string(3), gp,
						CusMeter.DEMAGE));
			}
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public UserData loadUserData() throws Exception {
		UserData userData = null;
		String sql = " SELECT  user_id, user_name, last_center_x, last_center_y, zoom\n"
				+ "   FROM user_data";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				userData = new UserData();
				userData.setUserid(stmt.column_int(0));
				userData.setUsername(stmt.column_string(1));
				int last_center_x = stmt.column_int(2);
				int last_center_y = stmt.column_int(3);
				MGeoPoint point = new MGeoPoint(last_center_y, last_center_x);
				userData.setCenter(point);
				userData.setZoom(stmt.column_int(4));

			}
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return userData;
	}

	public void proceedBuildings(int buid, int[] cus_ids, int proceeded,
			Database settingsDB) throws Exception {
		Stmt stmt = null;
		execCommand("delete from building_update where buid=" + buid,
				settingsDB);
		String sql = "insert into building_update (buid, cus_ids , proceeded , user_id , modify_time ) values(?,?,?,?,?)";
		try {
			stmt = settingsDB.prepare(sql);
			int index = 1;
			stmt.bind(index++, buid);
			String sCus_ids = "";
			for (int cus_id : cus_ids) {
				if (sCus_ids.length() > 0)
					sCus_ids += ",";
				sCus_ids += cus_id;
			}
			stmt.bind(index++, sCus_ids);
			stmt.bind(index++, proceeded);
			stmt.bind(index++, LoginActivity.userData.getUserid());
			stmt.bind(index++, System.currentTimeMillis());
			stmt.step();
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

	}

	public void proceedNewBuilding(NewBuilding newBuilding, boolean delete)
			throws Exception {

		int proceeded = 0;
		if (ConnectionHelper.userContext != null) {
			try {

				ConnectionHelper.getConnection().proceedNewBuilding(
						newBuilding, delete,
						ConnectionHelper.userContext.getShort_value());
				proceeded = 1;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		jsqlite.Database mDatabase = new Database();
		Stmt stmtInsert = null;
		try {

			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String sql = "insert into newbuilding (cus_ids, p_y, p_x, building_id, user_id,  modify_time, proceeded,ppcityid,pcityid) values(?,?,?,?,?,?,?,?,?)";
			deleteNewBuilding(newBuilding.getBuilding_add_id(), mDatabase);
			if (delete && proceeded == 1)
				return;
			int user_id = MainActivity.getUserData().getUserid();
			if (delete)
				user_id = -1 * user_id;
			int index = 1;
			stmtInsert = mDatabase.prepare(sql);
			stmtInsert.bind(index++, newBuilding.getScus_ids());
			stmtInsert.bind(index++, newBuilding.getLocation().mLatitudeE6);
			stmtInsert.bind(index++, newBuilding.getLocation().mLongitudeE6);
			stmtInsert.bind(index++, newBuilding.getBuilding_add_id());
			stmtInsert.bind(index++, user_id);
			stmtInsert.bind(index++, System.currentTimeMillis());
			stmtInsert.bind(index++, proceeded);
			stmtInsert.bind(index++, newBuilding.getPpcityid());
			stmtInsert.bind(index++, newBuilding.getPcityid());
			stmtInsert.step();

		} finally {

			try {
				stmtInsert.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void saveDemageDescription(DemageDescription description)
			throws Exception {
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		Stmt stmtSelect = null;
		Stmt stmtCreate = null;
		Stmt stmtInsert = null;
		boolean idset = description.getId() > 0;
		String sql = "";
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			int proceeded = idset ? 1 : 0;
			if (!idset) {
				sql = "select max(abs(id)) from demage_description where id<0";
				stmtSelect = mDatabase.prepare(sql);
				if (stmtSelect.step()) {
					int id = stmtSelect.column_int(0);
					id++;
					description.setId(-1 * id);
					stmtSelect.close();
					idset = true;
				}
			}

			sql = " insert into demage_description (%suser_id, demage_type, time, description, p_x,p_y,proceeded) values (%s?,?,?,?,?,?,?)";

			if (idset)
				sql = String.format(sql, "id,", "?,");
			else
				sql = String.format(sql, "", "");
			stmt = mDatabase.prepare(sql);
			int index = 1;
			if (idset)
				stmt.bind(index++, description.getId());
			stmt.bind(index++, LoginActivity.userData.getUserid());
			stmt.bind(index++, description.getDemage_type());
			stmt.bind(index++, description.getTime());
			stmt.bind(index++, description.getDescription());
			stmt.bind(index++, description.getPx());
			stmt.bind(index++, description.getPy());
			stmt.bind(index++, proceeded);
			stmt.step();
			if (!idset) {
				sql = "select max(id) from demage_description";
				stmtSelect = mDatabase.prepare(sql);
				if (stmtSelect.step()) {
					description.setId(stmtSelect.column_int(0));
				}
			}
			// if(description.getBytes()==null)
			// description.setBytes(new ArrayList<byte[]>());
			sql = "CREATE TABLE IF NOT EXISTS demage_description_item (id INTEGER PRIMARY KEY, dd_id INTEGER, data bynary)";
			stmtCreate = mDatabase.prepare(sql);
			stmtCreate.step();
			sql = "insert into demage_description_item (dd_id , data ) values (?,?)";

			for (byte[] buffer : description.getBytes()) {
				try {
					stmtInsert = mDatabase.prepare(sql);
					stmtInsert.bind(1, description.getId());
					stmtInsert.bind(2, buffer);
					stmtInsert.step();
				} finally {
					stmtInsert.close();
				}
			}

		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
			try {
				stmtSelect.close();
			} catch (Exception e) {
			}
			try {
				stmtCreate.close();
			} catch (Exception e) {
			}
			try {
				stmtInsert.close();
			} catch (Exception e) {
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
			}
		}

	}

	public void saveMeter(CusMeter cusMeter, int action, int pcity_id,
			int user_id, Database settingsDB) throws Exception {

		execCommand("delete from meter where cusid=" + cusMeter.getCusid(),
				settingsDB);
		Stmt stmt = null;
		String sql = "insert into meter ( p_y, p_x, meterid, cusid, user_id, action, modify_time,pcity_id) values(?,?,?,?,?,?,?,?)";
		try {
			stmt = settingsDB.prepare(sql);
			int index = 1;
			stmt.bind(index++, cusMeter.getLocation().mLatitudeE6);
			stmt.bind(index++, cusMeter.getLocation().mLongitudeE6);
			stmt.bind(index++, cusMeter.getMeterid());
			stmt.bind(index++, cusMeter.getCusid());
			stmt.bind(index++, user_id);
			stmt.bind(index++, action);
			stmt.bind(index++, System.currentTimeMillis());
			stmt.bind(index++, pcity_id);
			stmt.step();
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

	}

	public void saveMeterValue(Long meterid, Long cusid, double value,
			int user_id, Database settingsDB) throws Exception {
		execCommand("delete from meter_value where meterid=" + meterid,
				settingsDB);
		Stmt stmt = null;
		String sql = "insert into meter_value (meterid, cusid, user_id, value, modify_time) values(?,?,?,?,?)";
		try {
			stmt = settingsDB.prepare(sql);
			int index = 1;
			stmt.bind(index++, meterid);
			stmt.bind(index++, cusid);
			stmt.bind(index++, user_id);
			stmt.bind(index++, value);
			stmt.bind(index++, System.currentTimeMillis());
			stmt.step();
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

	}

	public void saveUserData(UserData userData) throws Exception {

		String sql = " insert into user_data (user_id, user_name, last_center_x, last_center_y, zoom) values (?,?,?,?,?)";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		Stmt stmtDelete = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			stmt = mDatabase.prepare(sql);
			stmtDelete = mDatabase.prepare("delete from user_data");
			stmtDelete.step();
			int index = 1;
			stmt.bind(index++, userData.getUserid());
			stmt.bind(index++, userData.getUsername());
			stmt.bind(index++, userData.getCenter().mLongitudeE6);
			stmt.bind(index++, userData.getCenter().mLatitudeE6);
			stmt.bind(index++, userData.getZoom());
			stmt.step();

		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				stmtDelete.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void updateLast(Context context, long lastUpdated) {
		SQLiteConnection connectionHelper = null;
		SQLiteDatabase database = null;

		try {
			connectionHelper = new SQLiteConnection(context, new File(dbPath));
			database = connectionHelper.getReadableDatabase();
			database.execSQL("delete from lastupdated");
			database.execSQL("insert into lastupdated(updatetime) values ("
					+ lastUpdated + ")");
		} catch (Throwable e) {
		} finally {
			try {
				database.close();
			} catch (Exception e2) {
			}
			try {
				connectionHelper.close();
			} catch (Exception e2) {
			}
		}
	}

}
