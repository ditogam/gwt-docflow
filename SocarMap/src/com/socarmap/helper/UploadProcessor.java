package com.socarmap.helper;

import java.util.ArrayList;
import java.util.UUID;

import jsqlite.Database;
import jsqlite.Stmt;

import com.socarmap.db.DBSettingsLoader;
import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.SUserContext;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.ui.MarkersOverlay;

public class UploadProcessor {
	private DBSettingsLoader loader;

	public UploadProcessor(DBSettingsLoader loader) {
		this.loader = loader;
	}

	private ArrayList<byte[]> getBytes(int id, jsqlite.Database mDatabase)
			throws Exception {
		ArrayList<byte[]> bytes = new ArrayList<byte[]>();
		Stmt stmt = null;
		stmt = mDatabase
				.prepare("select data from demage_description_item where dd_id="
						+ id);
		while (stmt.step()) {
			bytes.add(stmt.column_bytes(0));
		}
		stmt.close();
		return bytes;
	}

	private int[] getIntValues(String str) {
		if (str == null)
			str = "";
		str = str.trim();
		if (str.isEmpty())
			return new int[] {};
		String[] spl = str.split(",");
		int[] result = new int[spl.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.valueOf(spl[i]);
		}
		return result;
	}

	public void upload() {
		IConnection conn = null;
		try {
			conn = ConnectionHelper.getConnection();
		} catch (Throwable e) {
			return;
		}

		String sql = "select buid,cus_ids,user_id,modify_time from building_update where proceeded is null or proceeded=0";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(loader.getDbPath(),
					jsqlite.Constants.SQLITE_OPEN_READWRITE);

			sql = "select buid,cus_ids,user_id,modify_time from building_update where proceeded is null or proceeded=0 limit 10";
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				int buid = stmt.column_int(0);
				String cus_ids = stmt.column_string(1);
				int user_id = stmt.column_int(2);
				// long modify_time = stmt.column_long(3);
				try {
					SUserContext uc = new SUserContext();
					uc.setUser_id(user_id);
					BuildingUpdate update = new BuildingUpdate();
					update.setBuid(buid);
					update.setCus_ids(getIntValues(cus_ids));
					conn.updateBuilding(update,
							ConnectionHelper.userContext.getShort_value());
					loader.execCommand(
							"update building_update set proceeded=1 where buid="
									+ buid, mDatabase);
				} catch (SocarException e) {
					try {
						loader.execCommand(
								"update building_update set proceeded=-1 where buid="
										+ buid, mDatabase);
					} catch (Throwable e2) {
					}
				} catch (Throwable e) {
					e.printStackTrace();

				}
			}
			stmt.close();

			sql = "select meterid,cusid,user_id,value,modify_time from meter_value where proceeded is null or proceeded=0  limit 10";
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				int index = 0;
				long meterid = stmt.column_int(index++);
				long cusid = stmt.column_int(index++);
				int user_id = stmt.column_int(index++);
				double value = stmt.column_double(index++);
				long modify_time = stmt.column_long(index++);
				try {
					conn.saveMeter(meterid, value, cusid, user_id, modify_time);
					loader.execCommand(
							"update meter_value set proceeded=1 where buid="
									+ meterid, mDatabase);
				} catch (SocarException e) {
					try {
						loader.execCommand(
								"update meter_value set proceeded=-1 where buid="
										+ meterid, mDatabase);
					} catch (Throwable e2) {
					}
				} catch (Throwable e) {

				}
			}
			stmt.close();

			sql = "select p_y, p_x, meterid, cusid, user_id,action,pcity_id from meter  where cusid>0 limit 10";
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				int index = 0;
				long p_y = stmt.column_int(index++);
				long p_x = stmt.column_int(index++);
				int meterid = stmt.column_int(index++);
				int cusid = stmt.column_int(index++);
				int user_id = stmt.column_int(index++);
				int action = stmt.column_int(index++);
				int pcity_id = stmt.column_int(index++);
				try {
					conn.updateDistinctMeter(cusid, user_id, p_x / 1E6,
							p_y / 1E6, pcity_id, false,
							action == CusMeter.ACTION_DELETE);
					loader.execCommand("delete from meter where meterid="
							+ meterid + " and cusid=" + cusid, mDatabase);
				} catch (SocarException e) {
					try {
						loader.execCommand("update meter set cusid="
								+ (-1 * cusid) + " where meterid=" + meterid
								+ " and cusid=" + cusid, mDatabase);
					} catch (Throwable e2) {
					}
				} catch (Throwable e) {

				}
			}
			stmt.close();

			sql = "select id, demage_type, time, description, p_y, p_x, user_id from demage_description where proceeded is null or proceeded=0  limit 10";
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				int index = 0;
				int id = stmt.column_int(index++);
				int demage_type = stmt.column_int(index++);
				long time = stmt.column_long(index++);
				String description = stmt.column_string(index++);
				double p_y = stmt.column_double(index++);
				double p_x = stmt.column_double(index++);
				int user_id = stmt.column_int(index++);
				try {
					if (id > 0) {
						conn.updateDistinctMeter(id, user_id, p_x, p_y, 0,
								true, false);
						loader.execCommand(
								"update demage_description set proceeded=1 where id="
										+ id, mDatabase);
					} else {

						String uId = null;
						uId = UUID.randomUUID().toString();
						// uId = ConnectionHelper.getConnection()
						// .createUniqueIDForFileTransfer();

						DemageDescription dd = new DemageDescription();

						dd.setDemage_type(demage_type);
						dd.setDescription(description);
						dd.setTime(time);
						dd.setPx(p_x);
						dd.setPy(p_y);
						dd.setBytes(new ArrayList<byte[]>());
						SUserContext uc = new SUserContext();
						uc.setUser_id(user_id);
						ArrayList<byte[]> bytes = getBytes(id, mDatabase);
						for (byte[] bt : bytes) {
							ConnectionHelper.uploadFile(uId, bt);
						}
						int new_id = conn.saveDemageDescription(uId, dd, uc);
						MarkersOverlay.getInstance().updateDemageId(id, new_id);
						loader.execCommand(
								"update demage_description set proceeded=1, id="
										+ new_id + " where id=" + id, mDatabase);

					}

				} catch (SocarException e) {
					try {
						loader.execCommand(
								"update demage_description set proceeded=-1 where id="
										+ id, mDatabase);
					} catch (Throwable e2) {
					}
				} catch (Throwable e) {

				}
			}
			stmt.close();

		} catch (Throwable e) {
			e.printStackTrace();
		}

		finally {
			try {
				stmt.close();
			} catch (Throwable e) {
			}
			try {
				mDatabase.close();
			} catch (Throwable e) {
			}
		}
	}
}
