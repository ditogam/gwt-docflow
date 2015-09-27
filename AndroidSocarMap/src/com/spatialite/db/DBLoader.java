package com.spatialite.db;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.TreeMap;

import jsqlite.Database;
import jsqlite.Stmt;

import org.osmdroid.util.GeoPoint;

import com.spatialite.beans.Balance;
import com.spatialite.beans.Customer;
import com.spatialite.beans.Meter;
import com.spatialite.beans.UserData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

public class DBLoader {
	private static DBLoader instance;

	public TreeMap<Long, String> regions = new TreeMap<Long, String>();
	public TreeMap<Long, TreeMap<Long, String>> subregions = new TreeMap<Long, TreeMap<Long, String>>();
	public TreeMap<Long, TreeMap<Long, String>> zones = new TreeMap<Long, TreeMap<Long, String>>();

	public static DBLoader getInstance() {
		return instance;
	}

	public static DBLoader initInstance(String dbPath) throws Exception {
		if (instance == null)
			instance = new DBLoader(dbPath);
		return instance;
	}

	private String dbPath;

	private DBLoader(String dbPath) throws Exception {
		jsqlite.Database mDatabase = new Database();
		this.dbPath = dbPath;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			getMap(mDatabase, "SELECT  distinct pcityid,zone FROM zones", 1, 1,
					0, null, zones);
			getMap(mDatabase,
					"SELECT pcityid, pcityname, ppcityid FROM pcity ", 0, 1, 2,
					null, subregions);
			getMap(mDatabase, "SELECT ppcityid, ppcityname FROM ppcity ", 0, 1,
					null, regions, null);
		} finally {
			mDatabase.close();
		}

	}

	public ArrayList<Balance> loadBalance(Long customer_id) throws Exception {
		ArrayList<Balance> list = new ArrayList<Balance>();
		String sql = " SELECT  nba.cusid,abs(nba.balance) balance,descrip,CASE\n"
				+ "  WHEN nba.balance > 0 THEN 'კრედიტი'\n"
				+ "   ELSE 'ვალი'\n"
				+ "    end\n"
				+ "  loantype\n"
				+ "   FROM nb_account nba, nb_account_type nbat\n"
				+ "  WHERE   nba.type_id= nbat.id and nba.balance <> 0  and nba.cusid=?";

		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);

			stmt.bind(1, customer_id);

			while (stmt.step()) {
				list.add(new Balance(stmt.column_int(0), stmt.column_double(1),
						stmt.column_string(2), stmt.column_string(3)));
			}
		} finally {
			try {
				stmt.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public int checkUsernameAndPassword(String user_name, String password)
			throws Exception {
		byte[] bytesOfMessage = password.getBytes("UTF-8");

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytesOfMessage);

		BigInteger bigInt = new BigInteger(1, thedigest);
		String hashtext = bigInt.toString(16);
		password = URLDecoder.decode(hashtext);
		String sql = "select userid from users where username=? and  pass=?";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);

			stmt.bind(1, user_name);
			stmt.bind(2, password);

			if (stmt.step()) {
				return stmt.column_int(0);
			}
		} finally {
			try {
				stmt.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}

	public UserData getUser_data(String user_name, int user_id)
			throws Exception {

		String sql = "select ppcityid,pcityid, AsBinary(centroid(geom)) centroid,  AsBinary(GeomFromText('POINT('||MbrMinX(geom)||' ' ||MbrMinY(geom)||')',4326)) tl\n"
				+ ",  AsBinary(GeomFromText('POINT('||MbrMaxX(geom)||' ' ||MbrMaxY(geom)||')',4326)) br\n"
				+ "from\n"
				+ "(select ppcityid,pcityid,Envelope(GeomFromText(geomtext,4326)) geom from region_bounds rb\n"
				+ "inner join users u on (u.ppcityid=-1 or (rb.vtype=1 and id=u.ppcityid)) and (u.pcityid=-1 or (rb.vtype=2 and id=u.pcityid))\n"
				+ "where u.userid=?) k";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);

			stmt.bind(1, user_id);

			if (stmt.step()) {
				UserData result = new UserData();
				result.setUsername(user_name);
				result.setUserid(user_id);
				result.setPpcity(stmt.column_int(0));
				result.setPcity(stmt.column_int(1));
				result.setCenter(createGeom(stmt.column_bytes(2)));
				result.setBoundsTopLeftCorner(createGeom(stmt.column_bytes(3)));
				result.setBoundsBottomLeftCorner(createGeom(stmt
						.column_bytes(4)));
				return result;
			}
		} finally {
			try {
				stmt.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private GeoPoint createGeom(byte[] bytes) throws ParseException {
		Geometry mGeometry = new WKBReader().read(bytes);

		com.vividsolutions.jts.geom.Point p = mGeometry.getCentroid();

		GeoPoint gp = new GeoPoint((int) (p.getY() * 1E6),
				(int) (p.getX() * 1E6));
		return gp;
	}

	public ArrayList<Meter> loadMeters(Long customer_id) throws Exception {
		ArrayList<Meter> list = new ArrayList<Meter>();
		String sql = " select meterid, m.cusid,mtypename,metserial,start_index, name mstatus\n"
				+ " from meter m, mtype mt, mstatus ms \n"
				+ " where m.mtypeid= mt.mtypeid and ms.mstatusid= m.mstatusid and m.cusid=?";

		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);

			stmt.bind(1, customer_id);

			while (stmt.step()) {
				list.add(new Meter(stmt.column_int(0), stmt.column_int(1), stmt
						.column_string(2), stmt.column_string(3), stmt
						.column_string(4), stmt.column_string(5)));
			}
		} finally {
			try {
				stmt.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public ArrayList<Customer> getCustomers(Long subregion_id, Long zone,
			Long customer_id) throws Exception {
		ArrayList<Customer> list = new ArrayList<Customer>();
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
					+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname \n"
					+ "FROM customers c left join building_to_customers bc on bc.cusid=c.cusid\n where \n";
			if (customer_id != null)
				sql += " c.cusid=?";
			else
				sql += " subregionid =? and  zone=?";

			stmt = mDatabase.prepare(sql);
			if (customer_id != null)
				stmt.bind(1, customer_id);
			else {
				stmt.bind(1, subregion_id);
				stmt.bind(2, zone);
			}
			while (stmt.step()) {
				list.add(new Customer(stmt.column_long(0), stmt
						.column_string(2), stmt.column_long(1)));
			}
		} finally {
			try {
				stmt.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (jsqlite.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public String getRegionNames() {
		String result = "";
		for (Long key : regions.keySet()) {
			result += "\n" + key + ":" + regions.get(key);
		}
		return result.trim();
	}

	private void getMap(jsqlite.Database mDatabase, String sql,
			Integer id_index, Integer string_index, Integer parent_index,
			TreeMap<Long, String> rootNodes,
			TreeMap<Long, TreeMap<Long, String>> dependency) throws Exception {
		Stmt stmt = null;
		try {
			stmt = mDatabase.prepare(sql);
			while (stmt.step()) {
				Long id = stmt.column_long(id_index);
				String value = stmt.column_string(string_index);
				Long parent_id = null;
				if (parent_index != null)
					parent_id = stmt.column_long(parent_index);

				if (parent_index == null && rootNodes != null) {
					rootNodes.put(id, value);
				} else if (parent_index != null && dependency != null) {
					TreeMap<Long, String> map = dependency.get(parent_id);
					if (map == null) {
						map = new TreeMap<Long, String>();
						dependency.put(parent_id, map);
					}
					map.put(id, value);
				}

			}
		} finally {
			stmt.close();
		}
	}

}
