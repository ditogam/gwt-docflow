package com.docflow.and.impl;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import jsqlite.JDBC2z.JDBCConnection;

import com.common.db.DBClassMapping;
import com.common.shared.ClSelectionItem;
import com.common.shared.usermanager.Permission;
import com.common.shared.usermanager.TransfarableUser;
import com.common.shared.usermanager.User;
import com.docflow.and.impl.db.ADBResultObjectExecutor;
import com.docflow.and.impl.db.DBConnectionAnd;
import com.docflow.and.impl.db.DbParam;
import com.docflow.and.impl.db.ExecutorConstructor;
import com.docflow.and.impl.db.SimpleDBObjectExecutor;
import com.docflow.and.impl.ds.DataSource;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.MDBConnectionCommon;
import com.docflow.shared.StatusObject;
import com.docflow.shared.UserObject;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.docflowdroid.DocFlow;
import com.docflowdroid.R;
import com.docflowdroid.helper.Utils;

public class MDBConnection implements MDBConnectionCommon {
	private static SimpleDBObjectExecutor<CustomerShort> customer_get = null;

	public static long trim(long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
		System.out.println(cal.getTime());
		return cal.getTimeInMillis();
	}

	public static CustomerShort getCustomerShort(int cusid, Connection conn)
			throws Exception {
		if (customer_get == null)
			customer_get = new SimpleDBObjectExecutor<CustomerShort>(
					new ExecutorConstructor(), "getCustomerShort");
		return customer_get.setConnection(conn)
				.setParams(DbParam.intParam(cusid))
				.getObjectFromDB("select * from v_customer_full where cusid=?");
	}

	public static User getUser(String userName, String password, Connection conn)
			throws Exception {
		String pwdmd5 = Utils.md5(Utils.md5(password.trim()));
		User user = new SimpleDBObjectExecutor<User>(new ExecutorConstructor(
				conn, DbParam.stringParam(userName)), DBClassMapping.class,
				"getUser")
				.getObjectFromDB("select id, user_name,regionid,subregionid,pwd from susers where lower(user_name)=lower(trim(?))  limit 1");
		if (user == null)
			throw new Exception(
					DocFlow.activity.getString(R.string.invalid_user_name));
		if (!user.getPwd().equals(pwdmd5))
			throw new Exception(
					DocFlow.activity.getString(R.string.invalid_user_name));
		return user;
	}

	public static User getUser(int userid, Connection conn) throws Exception {
		User user = new SimpleDBObjectExecutor<User>(new ExecutorConstructor(
				conn, DbParam.intParam(userid)), DBClassMapping.class,
				"getUser")
				.getObjectFromDB("select id, user_name,regionid,subregionid,pwd from susers where id=? limit 1");

		return user;
	}

	public static TransfarableUser getSUser(User user, Connection conn)
			throws Exception {
		ArrayList<Permission> permisions = new SimpleDBObjectExecutor<Permission>(
				new ExecutorConstructor(conn, DbParam.intParam(user.getId())),
				DBClassMapping.class, "getPermition")
				.getObjectsFromDB("SELECT p.id,p.permission_name FROM user_permitions up inner join spermitions p on p.id=up.permision_id and up.id=?");
		TransfarableUser tu = TransfarableUser.generateFromDBUser(user,
				permisions);
		return tu;
	}

	public static ArrayList<ClSelectionItem> getClSelectionItems(
			Connection conn, int type, Long parent_id, Long id,
			boolean singleRow) throws Exception {
		String sql = "SELECT id,cvalue,parentId FROM clselection where type_id=? "
				+ (parent_id == null ? " and ?>0 " : " and parentId=?")
				+ (id == null ? " and ?>0 " : " and id=?")
				+ " order by 1 "
				+ (singleRow ? " limit 1" : "");

		ArrayList<ClSelectionItem> list = new SimpleDBObjectExecutor<ClSelectionItem>(
				new ExecutorConstructor(conn, DbParam.intParam(type),
						parent_id == null ? DbParam.intParam(1) : DbParam
								.longParam(parent_id),
						id == null ? DbParam.intParam(1) : DbParam
								.longParam(id)), DBClassMapping.class,
				"getClSelectionItem").getObjectsFromDB(sql);
		return list;

	}

	public static ArrayList<ClSelectionItem> getClSelectionItems(
			Connection conn, int type) throws Exception {
		return getClSelectionItems(conn, type, null, null, false);
	}

	public static ArrayList<ClSelectionItem> getClSelectionItems(
			Connection conn, int type, Long parent_id) throws Exception {
		return getClSelectionItems(conn, type, parent_id, null, false);

	}

	public static ClSelectionItem getClSelectionItem(Connection conn, int type)
			throws Exception {
		return getClSelectionItem(conn, type, null, null);
	}

	public static ClSelectionItem getClSelectionItem(Connection conn, int type,
			long id) throws Exception {
		return getClSelectionItem(conn, type, null, id);
	}

	public static ClSelectionItem getClSelectionItem(Connection conn, int type,
			Long parent_id, Long id) throws Exception {
		ArrayList<ClSelectionItem> list = getClSelectionItems(conn, type,
				parent_id, id, true);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public static ArrayList<ClSelectionItem> getTopTypes(Connection conn,
			int... types) throws Exception {

		String sql = "SELECT id,cvalue,type_id parentId FROM clselection where ";
		if (types == null)
			types = new int[0];
		if (types.length == 0)
			sql += "  (parentId is null or parentId==-1)";
		else {
			String where = "";
			for (int type : types) {
				if (!where.isEmpty())
					where += ",";
				where += Integer.valueOf(type);
			}
			sql += "  type_id in (" + where + ")";
		}
		sql += " order by type_id,id";
		return new SimpleDBObjectExecutor<ClSelectionItem>(
				new ExecutorConstructor(conn), DBClassMapping.class,
				"getClSelectionItem").getObjectsFromDB(sql);
	}

	public static ArrayList<ClSelectionItem> getStatuses(Connection conn,
			int language_id, int system_id) throws Exception {
		return new SimpleDBObjectExecutor<ClSelectionItem>(
				new ExecutorConstructor(conn, DbParam.intParam(language_id),
						DbParam.intParam(system_id)), MDBConnection.class,
				"getStatusObj")
				.getObjectsFromDB("select id, docstatuscaptionvalue, state_color from doc_system_status_v ds where statuslang=? and system_id=? order by id");
	}

	public static StatusObject getStatusParams(Connection conn, int system_id)
			throws Exception {
		return new SimpleDBObjectExecutor<StatusObject>(
				new ExecutorConstructor(conn, DbParam.intParam(system_id)),
				MDBConnection.class, "getStatusParam")
				.getObjectFromDB("select initial_status,approved_status,applied_status,error_status,next_status,check_for_statuses from doc_systems where id=? limit 1");
	}

	public static void setUserObjectConfig(Connection conn, UserObject uo)
			throws Exception {
		Object[] config = new SimpleDBObjectExecutor<Object[]>(
				new ExecutorConstructor(conn), MDBConnection.class, "getConfig")
				.getObjectFromDB("select android_map_renderer,methode_bodys,android_check_status_interval from configurations limit 1");
		uo.setAndroid_check_status_interval((Integer) config[1]);
		String[] arr = { (String) config[2] };
		uo.setMethodes(new ArrayList<String>(Arrays.asList(arr)));
		uo.setAndroid_map_renderer((String) config[0]);

	}

	public static StatusObject getStatusParam(ResultSet rs) throws Exception {
		StatusObject result = new StatusObject();
		result.setInitial_status(rs.getInt("initial_status"));
		result.setApproved_status(rs.getInt("approved_status"));
		result.setApplied_status(rs.getInt("applied_status"));
		result.setError_status(rs.getInt("error_status"));
		result.setCheck_for_statuses(rs.getInt("check_for_statuses") == 1);
		result.setNext_status(rs.getInt("next_status"));
		return result;
	}

	public static ClSelectionItem getStatusObj(ResultSet rs) throws Exception {
		ClSelectionItem item = new ClSelectionItem();
		item.setId(rs.getLong("id"));
		item.setValue(rs.getString("docstatuscaptionvalue"));
		item.setAdditional_value(rs.getInt("state_color"));
		return item;
	}

	public static ClSelectionItem getKeyValue(ResultSet rs) throws Exception {
		ClSelectionItem item = new ClSelectionItem();
		item.setId(rs.getLong(1));
		item.setValue(rs.getString(2));
		return item;
	}

	public static ArrayList<DocType> getDocTypes(int language_id, int user_id,
			int system_id, Connection conn) throws Exception {
		String sql = "SELECT dt.* FROM doc_type_v dt inner join user_doc_types udt on udt.doc_type_id=dt.id where dt.system_id=? and dt.typelang=? and udt.user_id=? order by sort_order,id";
		return new SimpleDBObjectExecutor<DocType>(new ExecutorConstructor(
				conn, DbParam.intParam(system_id),
				DbParam.intParam(language_id), DbParam.intParam(user_id)),
				"getDocType").getObjectsFromDB(sql);

	}

	public static ArrayList<DocType> getAllDocTypes(int language_id)
			throws Exception {
		String sql = "SELECT dt.* FROM doc_type_v dt where dt.typelang=?";
		return new SimpleDBObjectExecutor<DocType>(new ExecutorConstructor(
				DbParam.intParam(language_id)), "getDocType")
				.getObjectsFromDB(sql);

	}

	public static Object[] getConfig(ResultSet rs) throws Exception {

		Object[] result = new Object[] { rs.getString("android_map_renderer"),
				rs.getInt("android_check_status_interval"),
				rs.getString("methode_bodys") };
		return result;
	}

	public static DocType getDocType(Connection conn, int doctypeid,
			int languageId) throws Exception {
		String sql = "SELECT dt.* FROM doc_type_v dt where dt.id=? and dt.typelang=? limit 1";
		return new SimpleDBObjectExecutor<DocType>(
				new ExecutorConstructor(conn, DbParam.intParam(doctypeid),
						DbParam.intParam(languageId)), "getDocType")
				.getObjectFromDB(sql);
	}

	public static HashMap<String, String> getValueMap(Connection conn,
			final String fieldNames[], String sql, int custid) throws Exception {

		final HashMap<String, String> map = new HashMap<String, String>();
		new ADBResultObjectExecutor<HashMap<String, String>>(
				new ExecutorConstructor(conn, DbParam.intParam(custid))) {
			@Override
			public HashMap<String, String> getResult(ResultSet rs)
					throws Exception {
				for (String key : fieldNames) {
					String value = rs.getString(key);
					map.put(key, value);
				}
				return map;
			}
		}.getObjectFromDB(sql);

		return map;
	}

	public static HashMap<String, ArrayList<ClSelectionItem>> getValueList(
			Connection conn, HashMap<String, String> listSql, int customer_id)
			throws Exception {
		Connection myconn = null;
		HashMap<String, ArrayList<ClSelectionItem>> result = new HashMap<String, ArrayList<ClSelectionItem>>();
		try {
			myconn = conn == null ? DBConnectionAnd.getExportedDB() : conn;
			Set<String> kesSet = listSql.keySet();
			for (String key : kesSet) {
				String sql = listSql.get(key);
				result.put(
						key,
						new SimpleDBObjectExecutor<ClSelectionItem>(
								new ExecutorConstructor(conn, DbParam
										.intParam(customer_id)),
								MDBConnection.class, "getKeyValue")
								.getObjectsFromDB(sql));

			}
		} finally {
			if (conn == null)
				DBConnectionAnd.closeAll(myconn);
		}
		return result;
	}

	public static DocumentLong getDocLong(Connection myconnD, int docid,
			int languageId) throws Exception {
		throw new Exception("Not implemented");
	}

	public static ArrayList<DocumentFile> getFilesForDocument(
			Connection myconnD, int docid) throws Exception {
		throw new Exception("Not implemented");
	}

	public static void reloadsql_custom_functions() throws Exception {
		ArrayList<SQLCustomFuntion> customFuntions = new ADBResultObjectExecutor<SQLCustomFuntion>(
				new ExecutorConstructor()) {
			@Override
			public SQLCustomFuntion getResult(ResultSet rs) throws Exception {
				SQLCustomFuntion result = new SQLCustomFuntion(
						rs.getString("function_name"),
						rs.getInt("param_count"), rs.getString("bean_shell"),
						rs.getString("replace_function"));
				return result;
			}
		}.getObjectsFromDB("select function_name,param_count,bean_shell,replace_function from sql_custom_functions");
		DBConnectionAnd.customFuntions = customFuntions;
	}

	public static ArrayList<Integer> testDocTypes() throws Exception {
		// AndroidDocFlowServiceImpl impl = new AndroidDocFlowServiceImpl();
		JDBCConnection con = null;
		ArrayList<Integer> errors = new ArrayList<Integer>();
		try {
			con = (JDBCConnection) DBConnectionAnd.getExportedDB();

			ArrayList<DocType> dts = MDBConnection.getAllDocTypes(1);

			for (DocType docType : dts) {
				try {
					if (!docType.isApplied_customer()
							&& (docType.getCust_sql() == null || docType
									.getCust_sql().trim().isEmpty()))
						continue;
					HashMap<String, String> values = MDBConnection.getValueMap(
							con, docType.getCust_selectfields().split(","),
							docType.getCust_sql(), 544391);
					System.out.println(values);

				} catch (Throwable e) {
					errors.add(docType.getId());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			DBConnectionAnd.closeAll(con);
		}
		return errors;
	}

	public static ArrayList<DataSource> reloadDataSources() throws Exception {
		ArrayList<DataSource> list = new ADBResultObjectExecutor<DataSource>(
				new ExecutorConstructor()) {
			@Override
			public DataSource getResult(ResultSet rs) throws Exception {
				DataSource result = null;
				try {
					String content = rs
							.getString(ClSelection.SMARTGWTDATASOURCE_CONTENT);
					result = DataSource.createInstance(content);
				} catch (Throwable e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String error = sw.toString();
					System.out.println(error);
				}
				return result;
			}
		}.getObjectsFromDB("select " + ClSelection.SMARTGWTDATASOURCE_CONTENT
				+ " from " + ClSelection.SMARTGWTDATASOURCES);

		DataSource.putAll(list);

		return list;

	}

}
