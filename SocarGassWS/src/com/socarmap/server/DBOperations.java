package com.socarmap.server;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.MGeoPoint;
import com.socarmap.proxy.beans.UserContext;
import com.socarmap.proxy.beans.UserData;

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

	public static boolean getMapData(String zoom, String x, String y,
			OutputStream out) throws Exception {
		String sql = "select img_data from maps.zoom_xy where zoom=? and x=? and y=? and created and img_data is not null and length(img_data)>100";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(Constants.DBN_MAP);

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, Integer.valueOf(zoom));
			stmt.setInt(2, Integer.valueOf(x));
			stmt.setInt(3, Integer.valueOf(y));
			rs = stmt.executeQuery();
			if (rs.next()) {
				out.write(rs.getBytes("img_data"));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(rs, stmt, conn);
		}
		return false;
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
		sql.append("               Envelope(Geomfromtext(geomtext, 4326)) geom ");
		sql.append("        FROM   v_region_bounds rb ");
		sql.append("               INNER JOIN (SELECT ? AS ppcityid, ");
		sql.append("                                  ? AS pcityid) u ");
		sql.append("                       ON ( u.ppcityid = -1 ");
		sql.append("                             OR ( rb.vtype = 1 ");
		sql.append("                                  AND id = u.ppcityid ) ) ");
		sql.append("                          AND ( u.pcityid = -1 ");
		sql.append("                                 OR ( rb.vtype = 2 ");
		sql.append("                                      AND id = u.pcityid ) )) k ");
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

	public static int updateBuilding(int buid) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = getConnection(Constants.DBN_MAP);

			stmt = conn.createStatement();
			stmt.executeUpdate("update buildings set has_customers=case when has_customers=1 then 0 else 1 end where buid ="
					+ buid);

			conn.commit();
			return buid;
		} catch (Exception e) {
			throw e;
		} finally {
			closeAll(stmt, conn);
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

}
