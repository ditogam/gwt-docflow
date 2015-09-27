package com.socarmap.server.db;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.MGeoPoint;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.SUserContext;
import com.socarmap.proxy.beans.UserContext;
import com.socarmap.proxy.beans.UserData;
import com.socarmap.server.Constants;
import com.socarmap.server.ProtectedConfig;

public class DBOperations {

	public static void closeAll(Wrapper... closables) {
		for (Wrapper wrapper : closables) {
			if (wrapper == null)
				continue;
			try {
				if (wrapper instanceof Connection) {
					if (!((Connection) wrapper).getAutoCommit())
						((Connection) wrapper).rollback();
					((Connection) wrapper).close();
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (wrapper instanceof Statement) {
					((Statement) wrapper).close();
					continue;
				}
				if (wrapper instanceof ResultSet) {
					((ResultSet) wrapper).close();
					continue;
				}
				if (wrapper instanceof PreparedStatement) {
					((PreparedStatement) wrapper).close();
					continue;
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static Connection getConnection(String name) throws Exception {
		Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup("java:/comp/env");
		DataSource ds = (DataSource) envContext.lookup("jdbc/" + name);

		BasicDataSource t = (BasicDataSource) ds;
		System.out.println("MaxActive=" + t.getMaxActive() + " InitialSize="
				+ t.getInitialSize() + " MaxIdle=" + t.getMaxIdle()
				+ " NumActive=" + t.getNumActive() + " NumIdle="
				+ t.getNumIdle());
		Connection conn = ds.getConnection();
		conn.setAutoCommit(false);
		return conn;
	}

	public static ArrayList<IDValue> getList(int type) throws Exception {
		String sql = "";
		String db = Constants.DBN_MAP;
		if (type == IConnection.TP_DEMAGE_TYPE) {
			sql = "select demage_type_id ,demage_type_name from demage_type";
			db = Constants.DBN_MAP;
		}
		ArrayList<IDValue> result = new ArrayList<IDValue>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(db);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				IDValue res = new IDValue(rs.getLong(1), rs.getString(2));
				result.add(res);
			}

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rs, stmt, conn);
		}

	}

	public static UserContext getUserID(String user_name, String pwd)
			throws Exception {
		String sql = "select userid,ppcityid,pcityid from users where username=? and pass=?";
		UserContext result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(Constants.DBN_GASS);

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user_name);
			stmt.setString(2, pwd);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new UserContext();
				result.setPermitions(new ArrayList<String>());
				result.setUser_name(user_name);
				result.setUser_id(rs.getInt("userid"));
				result.setRegion_id(rs.getInt("ppcityid"));
				result.setSubregion_id(rs.getInt("pcityid"));
			}

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rs, stmt, conn);
		}

	}

	public static int getSubregionForMap(int subregion_id) throws Exception {
		String sql = "select  case when sm.subregion_id is null then sr.subregion_id else sm.subregion_id end subregion_id\n"
				+ "from (select ? subregion_id) sr\n"
				+ "LEFT JOIN maps.subregion_mappings sm ON sm.real_subregion_id = sr.subregion_id";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(Constants.DBN_GASS);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, subregion_id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("subregion_id");
			}

			return subregion_id;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rs, stmt, conn);
		}

	}

	public static UserData getUserData(UserContext context) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ppcityid, ");
		sql.append("       pcityid, ");
		sql.append("       Xmin(Centroid(geom)) centroidx, ");
		sql.append("       Ymin(Centroid(geom)) centroidy, ");
		sql.append("       Xmin(geom)           XMin, ");
		sql.append("       Ymin(geom)           YMin, ");
		sql.append("       Xmax(geom)           XMax, ");
		sql.append("       Ymax(geom)           YMax ");
		sql.append("FROM   (SELECT ppcityid, ");
		sql.append("               pcityid, ");
		sql.append("               st_collect(Geomfromtext(geomtext, 4326)) geom ");
		sql.append("        FROM   v_region_bounds rb ");
		sql.append("               INNER JOIN (SELECT ? AS ppcityid, ");
		sql.append("                                  ? AS pcityid) u ");
		sql.append("                       ON ( u.ppcityid = -1 ");
		sql.append("                             OR ( rb.vtype = 1 ");
		sql.append("                                  AND id = u.ppcityid ) ) ");
		sql.append("                          AND ( u.pcityid = -1 ");
		sql.append("                                 OR ( rb.vtype = 2 ");
		sql.append("                                      AND id = u.pcityid ) ) group by ppcityid,                pcityid) k ");
		UserData result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(Constants.DBN_MAP);

			stmt = conn.prepareStatement(sql.toString());
			stmt.setInt(1, context.getRegion_id());
			stmt.setInt(2, context.getSubregion_id());
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new UserData();
				result.setUsername(context.getUser_name());
				result.setUserid(context.getUser_id());
				result.setPpcity(context.getRegion_id());
				result.setPcity(context.getSubregion_id());
				result.setCenter(createGeom(rs.getDouble("centroidx"),
						rs.getDouble("centroidy")));
				result.setBoundsTopLeftCorner(createGeom(rs.getDouble("XMin"),
						rs.getDouble("YMin")));
				result.setBoundsBottomLeftCorner(createGeom(
						rs.getDouble("XMax"), rs.getDouble("YMax")));
			}

			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rs, stmt, conn);
		}

	}

	public static void getMap(String dbName, String sql, Integer id_index,
			Integer string_index, Integer parent_index,
			HashMap<Long, String> rootNodes,
			HashMap<Long, HashMap<Long, String>> dependency) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(dbName);
			stmt = conn.prepareStatement(sql.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Long id = rs.getLong(id_index + 1);
				String value = rs.getString(string_index + 1);
				Long parent_id = null;
				if (parent_index != null)
					parent_id = rs.getLong(parent_index + 1);
				if (parent_index == null && rootNodes != null) {
					rootNodes.put(id, value);
				} else if (parent_index != null && dependency != null) {
					HashMap<Long, String> map = dependency.get(parent_id);
					if (map == null) {
						map = new HashMap<Long, String>();
						dependency.put(parent_id, map);
					}
					map.put(id, value);
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rs, stmt, conn);
		}
	}

	private static MGeoPoint createGeom(double x, double y) {
		MGeoPoint gp = new MGeoPoint((int) (y * 1E6), (int) (x * 1E6));
		return gp;
	}

	public static boolean updateBuilding(int buid, String cus_ids)
			throws Exception {
		Connection conn = null;
		Connection connGass = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmtGass = null;
		ResultSet rsGass = null;
		boolean res = false;
		try {
			conn = getConnection(Constants.DBN_MAP);
			connGass = getConnection(Constants.DBN_GASS);
			stmt = conn
					.prepareStatement("select globals.save_building_customers(?,?) updated");
			stmt.setInt(1, buid);
			stmt.setString(2, cus_ids);
			rs = stmt.executeQuery();

			if (rs.next()) {
				int updated = rs.getInt("updated");
				res = updated != 0;
			}
			stmtGass = connGass
					.prepareStatement("select maps.save_building_customers(?,?) updated");
			stmtGass.setInt(1, buid);
			stmtGass.setString(2, cus_ids);
			rsGass = stmtGass.executeQuery();
			conn.commit();
			connGass.commit();
			return res;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connGass.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.getMessage());
		} finally {
			closeAll(rs, rsGass, stmt, stmtGass, conn, connGass);
		}

	}

	public static int saveDemageDescription(DemageDescription dd, int user_id)
			throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement psInsertDescr = null;
		PreparedStatement psInsertDescrFiles = null;
		ResultSet rsKey = null;
		int id = -1;
		try {
			conn = getConnection(Constants.DBN_MAP);

			stmt = conn.createStatement();
			rsKey = stmt
					.executeQuery("select nextval('demage_description_id_seq') nv");
			if (rsKey.next())
				id = rsKey.getInt("nv");

			psInsertDescr = conn
					.prepareStatement("insert into demage_description(id,demage_type,demage_time,description,the_geom,user_id) "
							+ "values (?,?,?,?, transform(ST_GeomFromText('POINT('||?||' '||?||')',4326),32638),?)");
			int index = 1;
			psInsertDescr.setInt(index++, id);
			psInsertDescr.setInt(index++, dd.getDemage_type());
			psInsertDescr.setTimestamp(index++, new Timestamp(dd.getTime()));
			psInsertDescr.setString(index++, dd.getDescription());
			psInsertDescr.setDouble(index++, dd.getPx());
			psInsertDescr.setDouble(index++, dd.getPy());
			psInsertDescr.setDouble(index++, user_id);
			psInsertDescr.executeUpdate();

			psInsertDescrFiles = conn
					.prepareStatement("insert into demage_description_files(description_id,file_data) "
							+ "values (?,?)");

			ArrayList<byte[]> files = dd.getBytes();
			for (byte[] bs : files) {
				psInsertDescrFiles.setInt(1, id);
				psInsertDescrFiles.setBytes(2, bs);
				psInsertDescrFiles.executeUpdate();
			}

			conn.commit();
			return id;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(rsKey, stmt, psInsertDescr, psInsertDescrFiles, conn);
		}

	}

	private static int getDistinctMeterObject(int cusid, Connection connMaps)
			throws Exception {
		ResultSet rs = null;
		PreparedStatement psSelect = null;
		try {
			psSelect = connMaps
					.prepareStatement("select meter_point_id from maps.district_meter_mapping where cusid=?");
			psSelect.setInt(1, cusid);
			rs = psSelect.executeQuery();
			if (rs.next())
				return rs.getInt("meter_point_id");
			return -1;
		} finally {
			closeAll(rs, psSelect);
		}
	}

	private static int getPPcityid(int raiid, Connection connBilling)
			throws Exception {
		ResultSet rs = null;
		PreparedStatement psSelect = null;
		try {
			psSelect = connBilling
					.prepareStatement("select ppcityid from pcity where pcityid=?");
			psSelect.setInt(1, raiid);
			rs = psSelect.executeQuery();
			if (rs.next())
				return rs.getInt("ppcityid");
			return -1;
		} finally {
			closeAll(rs, psSelect);
		}
	}

	public static int updateDistinctMeter(int cusid, int user_id, double px,
			double py, int raiid, boolean demage, boolean remove)
			throws Exception {
		System.out.println("updateDistinctMeter cusid=" + cusid + " user_id="
				+ user_id + " px=" + px + " py=" + py + " raiid=" + raiid
				+ " demage=" + demage + " remove=" + remove);
		int ret = -1;
		Connection connMaps = null;
		Connection connBilling = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;
		try {
			connMaps = getConnection(Constants.DBN_MAP);
			connBilling = getConnection(Constants.DBN_GASS);
			if (demage) {

				String sql = "";
				if (!remove)
					sql = "update demage_description set the_geom=transform(ST_GeomFromText('POINT('||?||' '||?||')',4326),32638),user_id=? where id=? ";
				else
					sql = "delete from demage_description  where id=? ";
				psUpdate = connMaps.prepareStatement(sql);
				int index = 1;
				if (remove)
					psUpdate.setInt(index, cusid);
				else {
					psUpdate.setDouble(index++, px);
					psUpdate.setDouble(index++, py);
					psUpdate.setInt(index++, user_id);
					psUpdate.setInt(index++, cusid);
				}
				psUpdate.executeUpdate();

			} else {
				if (remove) {
					removeDistrict_Meter_mapping(cusid, connMaps);
					removeDistrict_Meter_mapping(cusid, connBilling);
				} else {
					int meter_point_id = getDistinctMeterObject(cusid, connMaps);
					int regid = getPPcityid(raiid, connBilling);
					psUpdate = connMaps
							.prepareStatement("select globals.saveMapObject(?, ?, ?, ?,?, ?, ?, astext(makepoint(?,?,?))) buid");
					int index = 1;
					psUpdate.setString(index++, "district_meters");
					psUpdate.setInt(index++, meter_point_id);
					psUpdate.setInt(index++, regid);
					psUpdate.setInt(index++, raiid);
					psUpdate.setInt(index++, -100000);
					psUpdate.setInt(index++, 4326);
					psUpdate.setInt(index++, 32638);
					psUpdate.setDouble(index++, px);
					psUpdate.setDouble(index++, py);
					psUpdate.setInt(index++, 4326);
					rs = psUpdate.executeQuery();
					rs.next();
					meter_point_id = rs.getInt(1);
					saveDistrict_Meter_mapping(meter_point_id, cusid,
							connBilling);
					saveDistrict_Meter_mapping(meter_point_id, cusid, connMaps);
				}
			}
			connBilling.commit();
			connMaps.commit();
			return ret;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			closeAll(rs, psUpdate, connBilling, connMaps);
		}
	}

	public static void saveDistrict_Meter_mapping(int map_point_id, int cusid,
			Connection conn) throws Exception {
		PreparedStatement psAdd = null;
		try {

			removeDistrict_Meter_mapping(cusid, conn);
			psAdd = conn
					.prepareStatement("insert into maps.district_meter_mapping (cusid,meter_point_id) values (?,?)");

			psAdd.setInt(1, cusid);
			psAdd.setInt(2, map_point_id);

			psAdd.executeUpdate();

		} finally {
			try {
				psAdd.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public static void removeDistrict_Meter_mapping(int cusid, Connection conn)
			throws Exception {
		PreparedStatement psDelete = null;
		try {
			psDelete = conn
					.prepareStatement("delete from maps.district_meter_mapping where cusid=?");
			psDelete.setInt(1, cusid);

			psDelete.executeUpdate();

		} finally {
			try {
				psDelete.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public static void proceedNewBuilding(NewBuilding newBuilding,
			boolean delete, SUserContext context) throws Exception {
		Connection connMaps = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;
		try {
			connMaps = getConnection(Constants.DBN_MAP);

			// p_building_id ,p_user_id ,p_pcityid
			// ,p_ppcityid ,p_cus_ids ,p_the_geom
			// , p_remove
			psUpdate = connMaps
					.prepareStatement("select globals.savenewbuilding(?,?,?,?,?,transform(ST_GeomFromText('POINT('||?||' '||?||')',4326),32638),?) buid");
			int index = 1;
			psUpdate.setString(index++, newBuilding.getBuilding_add_id());
			psUpdate.setInt(index++, context.getUser_id());
			psUpdate.setInt(index++, newBuilding.getPcityid());
			psUpdate.setInt(index++, newBuilding.getPpcityid());
			psUpdate.setString(index++, newBuilding.getScus_ids());
			psUpdate.setDouble(index++, newBuilding.getLocation()
					.getLongitudeE6() / 1E6);
			psUpdate.setDouble(index++, newBuilding.getLocation()
					.getLatitudeE6() / 1E6);
			psUpdate.setBoolean(index++, delete);
			rs = psUpdate.executeQuery();
			rs.next();

			connMaps.commit();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			closeAll(rs, psUpdate, connMaps);
		}
	}

	public static void saveMeter(Long meterid, double value, Long cusid,
			int user_id, long modify_time) throws Exception {
		Connection connMaps = null;
		PreparedStatement psDelete = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;
		try {
			connMaps = getConnection(Constants.DBN_GASS);
			psDelete = connMaps
					.prepareStatement("delete from meter_value where meterid="
							+ meterid);
			psDelete.executeUpdate();
			// p_building_id character varying ,p_user_id integer,p_pcityid
			// integer,p_ppcityid integer,p_cus_ids character varying,p_the_geom
			// geometry, p_remove boolean
			String sql = "insert into meter_value (meterid, cusid, user_id, value, modify_time) values(?,?,?,?,?)";
			psUpdate = connMaps.prepareStatement(sql);
			int index = 1;
			psUpdate.setInt(index++, meterid.intValue());
			psUpdate.setInt(index++, cusid.intValue());
			psUpdate.setInt(index++, user_id);
			psUpdate.setDouble(index++, value);
			psUpdate.setTimestamp(index++, new Timestamp(modify_time));
			psUpdate.executeUpdate();

			connMaps.commit();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			closeAll(rs, psDelete, psUpdate, connMaps);
		}

	}

	private static Session session;

	public static void main(String[] args) {
		JSch jsch = new JSch();

		try {

			String serverProperties = "IO6zmslxjc72+44Oo5PGFNUKnWtvRVu62XUvhaAMdImudGRqzWRIIdXPUo+m/avYJSGSeVbGkKtR8whlDxQAKseYW80AG00xsqW1+HusBORXQhy2IXSTTZUXKvYJKuvf8RaBUXRU7mfOf8UCFQdW7A==";
			serverProperties = ProtectedConfig.decrypt(serverProperties);
			final Properties pr = new Properties();
			pr.load(new StringReader(serverProperties));
			session = jsch.getSession(pr.getProperty("username"),
					pr.getProperty("server"),
					Integer.valueOf(pr.getProperty("port")));
			UserInfo ui = new UserInfo() {

				@Override
				public void showMessage(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean promptYesNo(String arg0) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public boolean promptPassphrase(String arg0) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public String getPassword() {
					// TODO Auto-generated method stub
					return pr.getProperty("password");
				}

				@Override
				public String getPassphrase() {
					// TODO Auto-generated method stub
					return null;
				}
			};

			session.setUserInfo(ui);

			session.connect(10000);

			session.setPortForwardingL(8787, pr.getProperty("mapserver"),
					Integer.valueOf(pr.getProperty("mapserverport")));
		} catch (Throwable e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}
