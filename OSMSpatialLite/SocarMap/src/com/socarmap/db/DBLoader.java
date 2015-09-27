package com.socarmap.db;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import jsqlite.Database;
import jsqlite.Stmt;

import org.oscim.core.GeoPoint;

import com.socarmap.helper.ConnectionHelper;
import com.socarmap.helper.GeoPointHelper;
import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.Classifiers;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.Customer;
import com.socarmap.proxy.beans.MGeoPoint;
import com.socarmap.proxy.beans.Meter;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.proxy.beans.UserData;
import com.socarmap.proxy.beans.ZXYData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

public class DBLoader {
	private static DBLoader instance;
	public static HashMap<Long, String> demage_types = new HashMap<Long, String>();

	public static DBLoader getInstance() {
		return instance;
	}

	public static void getMap(jsqlite.Database mDatabase, String sql,
			Integer id_index, Integer string_index, Integer parent_index,
			HashMap<Long, String> rootNodes,
			HashMap<Long, HashMap<Long, String>> dependency) throws Exception {
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
					HashMap<Long, String> map = dependency.get(parent_id);
					if (map == null) {
						map = new HashMap<Long, String>();
						dependency.put(parent_id, map);
					}
					map.put(id, value);
				}

			}
		} finally {
			stmt.close();
		}
	}

	public static DBLoader initInstance(String dbPath) throws Exception {
		if (instance == null)
			instance = new DBLoader(dbPath);
		return instance;
	}

	public HashMap<Long, String> regions = new HashMap<Long, String>();

	public HashMap<Long, HashMap<Long, String>> subregions = new HashMap<Long, HashMap<Long, String>>();

	public HashMap<Long, HashMap<Long, String>> zones = new HashMap<Long, HashMap<Long, String>>();

	private String dbPath;

	private DBLoader(String dbPath) throws Exception {
		jsqlite.Database mDatabase = new Database();
		this.dbPath = dbPath;
		if (ConnectionHelper.userContext != null
				&& ConnectionHelper.userContext.getClassifiers() != null) {
			Classifiers c = ConnectionHelper.userContext.getClassifiers();
			demage_types = c.getDemage_types();
			regions = c.getRegions();
			subregions = c.getSubregions();
			zones = c.getZones();
		} else {
			try {
				mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
				getMap(mDatabase, "SELECT  distinct pcityid,zone FROM zones",
						1, 1, 0, null, zones);
				getMap(mDatabase,
						"SELECT pcityid, pcityname, ppcityid FROM pcity ", 0,
						1, 2, null, subregions);
				getMap(mDatabase, "SELECT ppcityid, ppcityname FROM ppcity ",
						0, 1, null, regions, null);

			} finally {
				mDatabase.close();
			}

		}
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	private MGeoPoint createGeom(byte[] bytes) throws ParseException {
		Geometry mGeometry = new WKBReader().read(bytes);

		com.vividsolutions.jts.geom.Point p = mGeometry.getCentroid();

		MGeoPoint gp = new MGeoPoint((int) (p.getY() * 1E6),
				(int) (p.getX() * 1E6));
		return gp;
	}

	private void deleteDistMeter(int cusid, jsqlite.Database mDatabase)
			throws Exception {

		String sql = "delete FROM district_meters  where cusid=?";
		Stmt stmt = mDatabase.prepare(sql);
		stmt.bind(1, cusid);
		stmt.step();
		stmt.close();

	}

	public CusMeter getCusMeters(Long meter_id, boolean customer,
			GeoPoint geoPoint) throws Exception {
		CusMeter result = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m.meterid, m.cusid,mtypename,metserial,start_index, name mstatus,c.cusname, last_value\n");
		sql.append("FROM customers c \n");
		sql.append("inner join meter m on m.cusid=c.cusid\n");
		sql.append("inner join mtype mt on m.mtypeid= mt.mtypeid\n");
		sql.append("inner join mstatus ms on ms.mstatusid= m.mstatusid\n");
		sql.append("where m.meterid=?");

		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql.toString());

			stmt.bind(1, meter_id);

			while (stmt.step()) {

				String cusname = stmt.column_string(6);

				result = (new CusMeter(stmt.column_int(0), stmt.column_int(1),
						stmt.column_string(2), stmt.column_string(3),
						stmt.column_string(4), stmt.column_string(5),
						stmt.column_double(6), cusname,
						GeoPointHelper.toMGeoPoint(geoPoint),
						customer ? CusMeter.CUSTOMER : CusMeter.METTER));
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

	public Customer getCustomerFull(int cusidid) throws Exception {
		Customer result = null;

		if (ConnectionHelper.userContext != null) {
			try {
				result = ConnectionHelper.getConnection().getCustomerFull(
						cusidid, ConnectionHelper.userContext.getShort_value());
				if (result != null)
					return result;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT cusid, ");
		sql.append("       cusname, ");
		sql.append("       region, ");
		sql.append("       raion, ");
		sql.append("       zone, ");
		sql.append("       cityname, ");
		sql.append("       streetname, ");
		sql.append("       home, ");
		sql.append("       flat, ");
		sql.append("       scopename, ");
		sql.append("       cusstatusname, ");
		sql.append("       custypename ");
		sql.append("FROM   customers c ");
		sql.append("       INNER JOIN custype ct ");
		sql.append("               ON ct.custypeid = c.custypeid ");
		sql.append("       INNER JOIN cusstatus cs ");
		sql.append("               ON cs.cusstatusid = c.cusstatusid ");
		sql.append("WHERE  cusid = ? ");
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql.toString());

			stmt.bind(1, cusidid);

			if (stmt.step()) {
				int index = 0;
				result = new Customer();
				result.setCusid(stmt.column_int(index++));
				result.setCusname(stmt.column_string(index++));
				result.setRegion(stmt.column_string(index++));
				result.setRaion(stmt.column_string(index++));
				result.setZone(stmt.column_long(index++));
				result.setCityname(stmt.column_string(index++));
				result.setStreetname(stmt.column_string(index++));
				result.setHome(stmt.column_string(index++));
				result.setFlat(stmt.column_string(index++));
				result.setScopename(stmt.column_string(index++));
				result.setCusstatusname(stmt.column_string(index++));
				result.setCustypename(stmt.column_string(index++));

				return result;
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
		return null;
	}

	public ArrayList<CusShort> getCustomers(Long subregion_id, Long zone,
			Long customer_id, boolean with_buildings, Long cus_type_id,
			Long building_id, boolean building_free) throws Exception {

		ArrayList<CusShort> list = null;
		if (ConnectionHelper.userContext != null) {
			try {
				list = ConnectionHelper.getConnection().getCustomers(
						subregion_id, zone, customer_id, with_buildings,
						cus_type_id, building_id, building_free,
						ConnectionHelper.userContext.getShort_value());
				if (list != null)
					return list;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		list = new ArrayList<CusShort>();
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
				+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname, zone \n"
				+ "FROM customers c "
				+ (customer_id == null && with_buildings ? "inner" : "left")
				+ " join building_to_customers bc on bc.cusid=c.cusid\n"
				+ " where 1=1 \n";

		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			if (customer_id != null) {
				sql += " and c.cusid=?";
				zone = null;
			}
			if (building_free) {
				sql += "and not exists (select 1 from building_to_customers bc where bc.cusid=c.cusid)";
			}

			if (subregion_id != null)
				sql += " and subregionid =? ";
			if (zone != null)
				sql += " and  zone=?";
			if (cus_type_id != null)
				sql += " and c.custypeid=?";
			else
				sql += " and c.custypeid!=-100";

			if (building_id != null)
				sql += " and building_id=?";
			stmt = mDatabase.prepare(sql);
			int index = 1;
			if (customer_id != null)
				stmt.bind(index++, customer_id);

			if (subregion_id != null) {
				stmt.bind(index++, subregion_id);

			}
			if (zone != null)
				stmt.bind(index++, zone);
			if (cus_type_id != null) {
				stmt.bind(index++, cus_type_id);
			}
			if (building_id != null)
				stmt.bind(index++, building_id);

			while (stmt.step()) {
				list.add(new CusShort(stmt.column_long(0), stmt
						.column_string(2), stmt.column_long(1), stmt
						.column_long(3)));
			}
		} finally {
			try {
				stmt.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public ArrayList<CusShort> getCustomersForDistinctMeter(Long subregion_id,
			Long zone, Long customer_id) throws Exception {
		ArrayList<CusShort> list = null;
		if (ConnectionHelper.userContext != null) {
			try {
				list = ConnectionHelper.getConnection()
						.getCustomersForDistinctMeter(subregion_id, zone,
								customer_id,
								ConnectionHelper.userContext.getShort_value());
				if (list != null)
					return list;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}

		list = new ArrayList<CusShort>();
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
					+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname \n"
					+ "FROM customers c  left join building_to_customers bc on bc.cusid=c.cusid\n"
					+ "where building_id is null and c.custypeid=-100 and c.cusid not in (select dm.cusid from district_meters dm) and \n";
			if (customer_id != null)
				sql += " c.cusid=?";
			else
				sql += " subregionid =? and  zone=?";
			stmt = mDatabase.prepare(sql);
			int index = 1;
			if (customer_id != null)
				stmt.bind(index++, customer_id);
			else {
				stmt.bind(index++, subregion_id);
				stmt.bind(index++, zone);
			}
			while (stmt.step()) {
				list.add(new CusShort(stmt.column_long(0), stmt
						.column_string(2), stmt.column_long(1), -1L));
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

	public ArrayList<CusShort> getCustomersForMeter(Long subregion_id,
			Long zone, Long customer_id) throws Exception {
		ArrayList<CusShort> list = null;
		if (ConnectionHelper.userContext != null) {
			try {
				list = ConnectionHelper.getConnection().getCustomersForMeter(
						subregion_id, zone, customer_id,
						ConnectionHelper.userContext.getShort_value());
				if (list != null)
					return list;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}

		list = new ArrayList<CusShort>();
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
					+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname \n"
					+ "FROM customers c  left join building_to_customers bc on bc.cusid=c.cusid\n"
					+ " where /*building_id is null*/ 1=1 and \n";
			if (customer_id != null)
				sql += " c.cusid=?";
			else
				sql += " subregionid =? and  zone=?";

			stmt = mDatabase.prepare(sql);
			int index = 1;
			if (customer_id != null)
				stmt.bind(index++, customer_id);
			else {
				stmt.bind(index++, subregion_id);
				stmt.bind(index++, zone);
			}
			while (stmt.step()) {
				list.add(new CusShort(stmt.column_long(0), stmt
						.column_string(2), stmt.column_long(1), -1L));
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

	public ArrayList<CusShort> getCustomersForNewBuilding(String building_add_id)
			throws Exception {
		ArrayList<CusShort> list = null;
		// if (ConnectionHelper.userContext != null) {
		// try {
		// list = ConnectionHelper.getConnection()
		// .getCustomersForDistinctMeter(subregion_id, zone,
		// customer_id, ConnectionHelper.userContext);
		// if (list != null)
		// return list;
		// } catch (Throwable e) {
		// }
		// }

		list = new ArrayList<CusShort>();
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {

			NewBuilding newBuilding = DBSettingsLoader.getInstance()
					.getForNewBuilding(building_add_id);
			if (newBuilding == null)
				return null;
			String cusids = newBuilding.getScus_ids();
			if (cusids == null)
				cusids = "";
			cusids = cusids.trim();
			if (cusids.isEmpty())
				return null;
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);
			String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
					+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname, zone \n"
					+ "FROM customers c  left join building_to_customers bc on bc.cusid=c.cusid\n"
					+ "where building_id is null and \n";

			sql += " c.cusid in (" + cusids + ")";

			stmt = mDatabase.prepare(sql);

			while (stmt.step()) {
				list.add(new CusShort(stmt.column_long(0), stmt
						.column_string(2), stmt.column_long(1), stmt
						.column_long(3)));
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

	public Meter getMeter(Long meter_id) throws Exception {
		ArrayList<Meter> meters = loadMeters(null, meter_id);
		if (meters != null && !meters.isEmpty())
			return meters.get(0);
		return null;
	}

	public String getRegionNames() {
		String result = "";
		for (Long key : regions.keySet()) {
			result += "\n" + key + ":" + regions.get(key);
		}
		return result.trim();
	}

	public long[] getUser_data()

	throws Exception {
		long[] result = new long[] { -1, -1 };
		String sql = "select ppcityid,pcityid from pcity";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);

			if (stmt.step()) {
				result[0] = stmt.column_long(0);
				result[1] = stmt.column_long(1);
				return result;
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
		return null;
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mDatabase.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ArrayList<Balance> loadBalance(Long customer_id) throws Exception {
		ArrayList<Balance> list = null;
		if (ConnectionHelper.userContext != null) {
			try {
				list = ConnectionHelper.getConnection().loadBalance(
						customer_id,
						ConnectionHelper.userContext.getShort_value());
				if (list != null)
					return list;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}
		list = new ArrayList<Balance>();
		String sql = " SELECT  nba.cusid,abs(nba.balance) balance,descrip,CASE\n"
				+ "  WHEN nba.balance > 0 THEN 'კრედიტი'\n"
				+ "   ELSE 'ვალი'\n"
				+ "    end\n"
				+ "  loantype\n"
				+ "   FROM nb_accounts nba, nb_account_type nbat\n"
				+ "  WHERE   nba.type_id= nbat.id and nba.balance <> 0  and nba.cusid=? and nba.type_id=1";

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

	public ArrayList<CusMeter> loadDistinctMeters(Long pcity_id, Long ppcity_id)
			throws Exception {
		ArrayList<CusMeter> list = new ArrayList<CusMeter>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m.meterid, m.cusid,mtypename,metserial,start_index, name mstatus,c.cusname,case when c.custypeid=-100 then 0 else 1 end custypeid, AsBinary(the_geom), last_value\n");
		sql.append("FROM district_meters dm inner join customers c on c.cusid=dm.cusid\n");
		sql.append("left join (SELECT max(meterid) meterid, cusid\n");
		sql.append("FROM meter group by cusid) mm on mm.cusid=c.cusid\n");
		sql.append("left join meter m on m.meterid=mm.meterid\n");
		sql.append("left join mtype mt on m.mtypeid= mt.mtypeid\n");
		sql.append("left join mstatus ms on ms.mstatusid= m.mstatusid\n");

		sql.append("where 1=1 ");

		if (ppcity_id != null && ppcity_id.longValue() > -1)
			sql.append("and c.regionid=?");

		if (pcity_id != null && pcity_id.longValue() > -1)
			sql.append("and c.subregionid=?");

		jsqlite.Database mDatabase = new Database();
		jsqlite.Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql.toString());
			int index = 1;
			if (ppcity_id != null && ppcity_id.longValue() > -1)
				stmt.bind(index++, ppcity_id);

			if (pcity_id != null && pcity_id.longValue() > -1)
				stmt.bind(index++, pcity_id);

			while (stmt.step()) {
				int custypeid = stmt.column_int(7);
				boolean customer = custypeid == 1;
				Geometry mGeometry = new WKBReader().read(stmt.column_bytes(8));
				String cusname = stmt.column_string(6);

				com.vividsolutions.jts.geom.Point p = mGeometry.getCentroid();

				MGeoPoint gp = new MGeoPoint((int) (p.getY() * 1E6),
						(int) (p.getX() * 1E6));

				list.add(new CusMeter(stmt.column_int(0), stmt.column_int(1),
						stmt.column_string(2), stmt.column_string(3), stmt
								.column_string(4), stmt.column_string(5), stmt
								.column_double(9), cusname, gp,
						customer ? CusMeter.CUSTOMER : CusMeter.METTER));
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

	public ArrayList<Meter> loadMeters(Long customer_id) throws Exception {
		return loadMeters(customer_id, null);
	}

	public ArrayList<Meter> loadMeters(Long customer_id, Long meter_id)
			throws Exception {

		ArrayList<Meter> list = null;
		if (ConnectionHelper.userContext != null) {
			try {
				list = ConnectionHelper.getConnection()
						.loadMeters(customer_id, meter_id,
								ConnectionHelper.userContext.getShort_value());
				if (list != null)
					return list;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}
		list = new ArrayList<Meter>();
		String sql = " select meterid, m.cusid,mtypename,metserial,start_index, name mstatus, last_value\n"
				+ " from meter m, mtype mt, mstatus ms \n"
				+ " where m.mtypeid= mt.mtypeid and ms.mstatusid= m.mstatusid";
		if (customer_id != null)
			sql += " and m.cusid=?";
		if (meter_id != null)
			sql += " and m.meterid=?";
		jsqlite.Database mDatabase = new Database();
		Stmt stmt = null;
		try {
			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

			stmt = mDatabase.prepare(sql);
			int index = 1;
			if (customer_id != null)
				stmt.bind(index++, customer_id);
			if (meter_id != null)
				stmt.bind(index++, meter_id);
			while (stmt.step()) {
				list.add(new Meter(stmt.column_int(0), stmt.column_int(1), stmt
						.column_string(2), stmt.column_string(3), stmt
						.column_string(4), stmt.column_string(5), stmt
						.column_double(6)));
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

	private void proceedBuildings(int buid, int[] cus_ids, Database currentDB)
			throws Exception {
		String sql = "delete from building_to_customers where building_id=?";
		Stmt stmt = currentDB.prepare(sql);
		stmt.bind(1, buid);
		stmt.step();
		stmt.close();

		for (int i : cus_ids) {
			stmt = currentDB
					.prepare("insert into building_to_customers(building_id,cusid) values(?,?)");
			stmt.bind(1, buid);
			stmt.bind(2, i);
			stmt.step();
			stmt.close();
		}

		sql = "update buildings set has_customer=? where buid=?";
		stmt = currentDB.prepare(sql);
		stmt.bind(1, cus_ids.length > 0 ? 1 : 0);
		stmt.bind(2, buid);
		stmt.step();
		stmt.close();

	}

	public void saveCusMeter(CusMeter cusMeter, int raiid, int action,
			int user_id) throws Exception {
		int proceeded = 0;
		if (ConnectionHelper.userContext != null) {
			try {
				MGeoPoint p = cusMeter.getLocation();
				ConnectionHelper.getConnection().updateDistinctMeter(
						cusMeter.getCusid(), user_id, p.mLongitudeE6 / 1E6,
						p.mLatitudeE6 / 1E6, raiid,
						cusMeter.getType() == CusMeter.DEMAGE,
						action == CusMeter.ACTION_DELETE);
				proceeded = 1;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}
		jsqlite.Database settingsDB = null;
		jsqlite.Database currentDB = null;
		try {
			settingsDB = DBSettingsLoader.getInstance().beginTransaction(null);
			currentDB = DBSettingsLoader.getInstance().beginTransaction(dbPath);
			if (cusMeter.getType() == CusMeter.DEMAGE) {
				String sql = "update demage_description set p_x=?, p_y=?,proceeded=0 where id=?";

				Stmt stmt = null;
				try {
					stmt = settingsDB.prepare(sql);
					int index = 1;
					MGeoPoint p = cusMeter.getLocation();
					stmt.bind(index++, p.mLongitudeE6 / 1E6);
					stmt.bind(index++, p.mLatitudeE6 / 1E6);
					stmt.bind(index++, cusMeter.getCusid());
					stmt.step();
				} finally {
					try {
						stmt.close();
					} catch (Exception e) {
					}
				}

			} else {
				deleteDistMeter(cusMeter.getCusid(), currentDB);
				if (proceeded == 0)
					DBSettingsLoader.getInstance().saveMeter(cusMeter, action,
							raiid, user_id, settingsDB);
				if (action != CusMeter.ACTION_DELETE) {
					String sql = "insert into district_meters (raiid,  cusid, the_geom) values(?,?,MakePoint(?,?,4326))";
					Stmt stmt = null;
					try {
						stmt = currentDB.prepare(sql);
						int index = 1;
						MGeoPoint p = cusMeter.getLocation();
						stmt.bind(index++, raiid);
						stmt.bind(index++, cusMeter.getCusid());
						stmt.bind(index++, p.mLongitudeE6 / 1E6);
						stmt.bind(index++, p.mLatitudeE6 / 1E6);
						stmt.step();
					} finally {
						try {
							stmt.close();
						} catch (Exception e) {
						}
					}

				}
			}
			DBSettingsLoader.getInstance().execCommand("COMMIT", settingsDB);
			DBSettingsLoader.getInstance().execCommand("COMMIT", currentDB);
		} catch (Exception e) {
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						settingsDB);
			} catch (Exception e2) {
			}
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						currentDB);
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			try {
				settingsDB.close();
			} catch (Exception e2) {
			}
			try {
				currentDB.close();
			} catch (Exception e2) {
			}
		}

	}

	public Meter saveMeter(Long meterid, double value, Long cusid, int user_id)
			throws Exception {
		int proceeded = 0;
		if (ConnectionHelper.userContext != null) {
			try {

				ConnectionHelper.getConnection().saveMeter(meterid, value,
						cusid, user_id, System.currentTimeMillis());
				proceeded = 1;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		jsqlite.Database settingsDB = null;
		jsqlite.Database currentDB = null;
		try {
			settingsDB = DBSettingsLoader.getInstance().beginTransaction(null);
			currentDB = DBSettingsLoader.getInstance().beginTransaction(dbPath);
			if (proceeded == 0)
				DBSettingsLoader.getInstance().saveMeterValue(meterid, cusid,
						value, user_id, settingsDB);

			String sql = "update meter set last_value=? where meterid=?";
			Stmt stmt = null;
			try {
				stmt = currentDB.prepare(sql);
				int index = 1;

				stmt.bind(index++, value);
				stmt.bind(index++, meterid);
				stmt.step();
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}

			DBSettingsLoader.getInstance().execCommand("COMMIT", settingsDB);
			DBSettingsLoader.getInstance().execCommand("COMMIT", currentDB);
		} catch (Exception e) {
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						settingsDB);
			} catch (Exception e2) {
			}
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						currentDB);
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			try {
				settingsDB.close();
			} catch (Exception e2) {
			}
			try {
				currentDB.close();
			} catch (Exception e2) {
			}
		}
		return getMeter(meterid);
	}

	public ArrayList<ZXYData> updateBuilding(int buid, int[] cus_ids)
			throws Exception {
		ArrayList<ZXYData> list = new ArrayList<ZXYData>();
		int proceeded = 0;
		if (ConnectionHelper.userContext != null) {
			try {
				BuildingUpdate update = new BuildingUpdate();
				update.setBuid(buid);
				update.setCus_ids(cus_ids);
				list = ConnectionHelper.getConnection().updateBuilding(update,
						ConnectionHelper.userContext.getShort_value());
				proceeded = 1;
			} catch (SocarException e) {
				throw e;
			} catch (Throwable e) {
			}
		}
		jsqlite.Database settingsDB = null;
		jsqlite.Database currentDB = null;
		try {
			settingsDB = DBSettingsLoader.getInstance().beginTransaction(null);
			currentDB = DBSettingsLoader.getInstance().beginTransaction(dbPath);

			proceedBuildings(buid, cus_ids, currentDB);
			DBSettingsLoader.getInstance().proceedBuildings(buid, cus_ids,
					proceeded, settingsDB);
			DBSettingsLoader.getInstance().execCommand("COMMIT", settingsDB);
			DBSettingsLoader.getInstance().execCommand("COMMIT", currentDB);
		} catch (Exception e) {
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						settingsDB);
			} catch (Exception e2) {
			}
			try {
				DBSettingsLoader.getInstance().execCommand("ROLLBACK",
						currentDB);
			} catch (Exception e2) {
			}
			throw e;
		} finally {
			try {
				settingsDB.close();
			} catch (Exception e2) {
			}
			try {
				currentDB.close();
			} catch (Exception e2) {
			}
		}

		return list;
	}

}
