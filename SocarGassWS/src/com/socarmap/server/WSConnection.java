package com.socarmap.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import com.googlecode.xremoting.core.servlet.XRemotingServlet;
import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.BuildingInfo;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.Classifiers;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.Customer;
import com.socarmap.proxy.beans.DemageDescription;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.MakeDBProcess;
import com.socarmap.proxy.beans.MakeDBResponce;
import com.socarmap.proxy.beans.Meter;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.SUserContext;
import com.socarmap.proxy.beans.SocarException;
import com.socarmap.proxy.beans.UserContext;
import com.socarmap.proxy.beans.ZXYData;
import com.socarmap.proxy.beans.accident.Case;
import com.socarmap.proxy.beans.accident.Simple_View;
import com.socarmap.proxy.beans.accident.Step;
import com.socarmap.server.db.DBOperations;
import com.socarmap.server.tasks.CopyDB;

public class WSConnection extends XRemotingServlet implements IConnection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5895897330635828677L;

	private static final ConcurrentHashMap<String, FileDataKeeper> files = new ConcurrentHashMap<String, FileDataKeeper>();
	private static final ConcurrentHashMap<String, MakeDBTask> makeDBTasks = new ConcurrentHashMap<String, MakeDBTask>();

	private static Thread transferTimeoutChecker = null;

	private static void checkForFileThread() {
		if (transferTimeoutChecker == null) {
			transferTimeoutChecker = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(FileDataKeeper.DELAY_TIMEOUT * 1000 * 60);
						} catch (Exception e) {
						}
						ArrayList<String> toRemove = new ArrayList<String>();

						for (String key : files.keySet()) {
							if (files.get(key).checkForTimeout())
								toRemove.add(key);
						}
						for (String key : toRemove) {
							files.remove(key);
						}

					}

				}
			});
		}
	}

	public WSConnection() {
		// TODO Auto-generated constructor stub
	}

	private static Classifiers classifiers = null;

	private static void loadClassifiers() throws SocarException {
		if (classifiers != null)
			return;
		classifiers = new Classifiers();
		try {
			DBOperations.getMap(Constants.DBN_GASS,
					"SELECT  distinct pcityid,zone FROM v_zones", 1, 1, 0,
					null, classifiers.getZones());
			DBOperations.getMap(Constants.DBN_GASS,
					"SELECT pcityid, pcityname, ppcityid FROM pcity ", 0, 1, 2,
					null, classifiers.getSubregions());
			DBOperations.getMap(Constants.DBN_GASS,
					"SELECT ppcityid, ppcityname FROM ppcity ", 0, 1, null,
					classifiers.getRegions(), null);
			DBOperations
					.getMap(Constants.DBN_MAP,
							"select demage_type_id ,demage_type_name from demage_type ",
							0, 1, null, classifiers.getDemage_types(), null);
		} catch (Exception e) {
			throw SocarExceptionCriator.doThrow(e);
		}

	}

	@Override
	public UserContext loginUser(String user_name, String pwd, int system,
			int language_id) throws SocarException {
		try {
			loadClassifiers();
			UserContext ret = DBOperations.getUserID(user_name, pwd);
			if (ret != null) {
				ret.setLanguage_id(language_id);
				ret.setUserData(DBOperations.getUserData(ret));
				ret.setClassifiers(classifiers.getForUser(ret));
			} else
				throw new Exception(UserContext.INVALID_USER_NAME_AND_PWD);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}
	}

	@Override
	public Integer saveDemageDescription(String uID, DemageDescription descr,
			SUserContext context) throws SocarException {
		FileDataKeeper keeper = files.get(uID);
		if (keeper == null) {
			throw SocarExceptionCriator.doThrow("No files found");
		}
		try {
			descr.setBytes(keeper.getFiles());
			Integer ret = DBOperations.saveDemageDescription(descr,
					context.getUser_id());
			return ret;
		} catch (Exception e) {
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			keeper.clearData();
			files.remove(uID);
		}
	}

	@Override
	public ArrayList<IDValue> getList(int type, SUserContext context)
			throws SocarException {
		try {
			return DBOperations.getList(TP_DEMAGE_TYPE);
		} catch (Exception e) {
			throw SocarExceptionCriator.doThrow(e);
		}
	}

	@Override
	public String createUniqueIDForFileTransfer() throws SocarException {
		String ret = UUID.randomUUID().toString();
		return ret;
	}

	@Override
	public void transferFile(String uID, byte[] file_data)
			throws SocarException {
		transfer(uID, file_data);
	}

	public static void transfer(String uID, byte[] file_data) {
		FileDataKeeper keeper = files.get(uID);
		if (keeper == null) {
			files.put(uID, new FileDataKeeper(file_data));
			return;
		}
		keeper.addFile(file_data);
		checkForFileThread();
	}

	@Override
	protected Object getTarget() {
		return this;
	}

	public static byte[] compress(byte bt[]) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(bt);
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();

		return compressed;
	}

	@Override
	public ArrayList<ZXYData> updateBuilding(BuildingUpdate buildingUpdate,
			SUserContext context) throws SocarException {
		String cus_ids = "";
		int[] iCus_ids = buildingUpdate.getCus_ids();
		for (int i : iCus_ids) {
			if (cus_ids.length() > 0)
				cus_ids += ",";
			cus_ids += i;
		}
		try {
			DBOperations.updateBuilding(buildingUpdate.getBuid(), cus_ids);
		} catch (Exception e) {
			throw SocarExceptionCriator.doThrow(e);
		}
		ArrayList<ZXYData> result = new ArrayList<ZXYData>();

		return result;
	}

	@Override
	public byte[] getData(String uID) throws SocarException {
		FileDataKeeper keeper = files.get(uID);
		if (keeper == null) {
			throw SocarExceptionCriator.doThrow("No data found");
		}
		try {
			ArrayList<byte[]> arr = keeper.getFiles();
			byte[] ret = null;
			if (arr == null || arr.isEmpty())
				throw SocarExceptionCriator.doThrow("No data found");
			ret = arr.get(0);
			return ret;
		} finally {
			keeper.clearData();
			files.remove(uID);
		}

	}

	@Override
	public BuildingInfo getBuildingInfo(double x, double y, int srid,
			int buffer, SUserContext context) throws SocarException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT buid, ");
		sql.append("       Astext(Transform(b.the_geom, srid)) geom, ");
		sql.append("       raiid, ");
		sql.append("       regid ");
		sql.append("FROM   (SELECT (( St_transform(St_setsrid(Makepoint(?, ?, srid), ");
		sql.append("                               srid), ");
		sql.append("                         32638) ");
		sql.append("                               )) the_geom, ");
		sql.append("               srid ");
		sql.append("        FROM   (SELECT ? AS srid) sr) s ");
		sql.append("       INNER JOIN buildings b ");
		sql.append("               ON St_intersects (s.the_geom, b.the_geom) ");
		sql.append("     order by ST_distance (s.the_geom, b.the_geom) limit 1");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		BuildingInfo result = null;
		try {
			conn = DBOperations.getConnection(Constants.DBN_MAP);
			stmt = conn.prepareStatement(sql.toString());
			stmt.setDouble(1, x);
			stmt.setDouble(2, y);
			stmt.setInt(3, srid);
			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = new BuildingInfo();
				result.setBuilding(rs.getString("geom"));
				result.setBuilding_id(rs.getLong("buid"));
				result.setRegion_id(rs.getLong("regid"));
				result.setSubregion_id(rs.getLong("raiid"));

				if (context.getRegion_id() >= 0
						&& result.getRegion_id() != context.getRegion_id()) {
					result = null;
				}
				if (context.getSubregion_id() >= 0
						&& result.getSubregion_id() != context
								.getSubregion_id()) {
					result = null;
				}
			}

		} catch (Exception e) {
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}
		return result;
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();

		String id_name = "ppcityid";
		String index = "0";
		String geom_name = "the_geom";
		String tags_name = "'natural='||hex('water')";
		String table_name = "map_info";
		sb.append("SELECT " + index + " _layer, " + id_name + " _id,"
				+ "AsBinary( case when Within(buffered, " + geom_name
				+ ")=1 then buffered else (Intersection(buffered," + geom_name
				+ ")) end) geom," + tags_name + " _tags_name \n");
		sb.append("FROM "
				+ table_name
				+ ",(select x1, y1,x2, y2,simp, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (\n"
				+ "select *,transform(Geometry,3035) mGeometry from\n"
				+ "(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (select ? x1 ,? y1,? x2 ,? y2, 50 diff,? simp )))\n"
				+ ") po \n");
		sb.append("where " + id_name + " in (\n");
		sb.append("select pkid from idx_" + table_name + "_" + geom_name
				+ " where pkid MATCH\n");
		sb.append("RtreeIntersects(x1, y1,x2, y2))\n");
		System.out.println(sb);
	}

	@Override
	public ArrayList<CusShort> getCustomers(Long subregion_id, Long zone,
			Long customer_id, boolean with_buildings, Long cus_type_id,
			Long building_id, boolean building_free, SUserContext context)
			throws SocarException {
		// System.out.println("Start getCustomers");
		// Timer.start("getCustomers");
		
		String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
				+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname, zone \n"
				+ "FROM v_customer_full c "

				+ " where "
				+ (customer_id == null && with_buildings ? "building_id is not null"
						: "1=1") + " \n";
		if (customer_id != null) {
			sql += " and c.cusid=?";
			zone = null;
		}
		if (building_free) {
			sql += "and building_id is null";
		}

		if (subregion_id != null)
			sql += " and subregionid =? ";
		if (zone != null)
			sql += " and  zone=?";
		if (cus_type_id != null)
			sql += " and c.custypeid=?";
		else
			sql += " and c.custypeid<>-100";

		if (building_id != null)
			sql += " and building_id=?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<CusShort> result = null;
		try {
			conn = DBOperations.getConnection(Constants.DBN_GASS);
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			if (customer_id != null)
				stmt.setLong(index++, customer_id);

			if (subregion_id != null) {
				stmt.setLong(index++, subregion_id);

			}
			if (zone != null)
				stmt.setLong(index++, zone);
			if (cus_type_id != null) {
				stmt.setLong(index++, cus_type_id);
			}
			if (building_id != null)
				stmt.setLong(index++, building_id);
			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			result = new ArrayList<CusShort>();
			while (rs.next()) {
				result.add(new CusShort(rs.getLong("cusid"), rs
						.getString("cusname"), rs.getLong("building_id"), rs
						.getLong("zone")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
			// Timer.step("getCustomers");
			// System.out.println("Stop getCustomers in "
			// + (System.currentTimeMillis() - time) + "ms");
			// Timer.printall();
		}
		return result;
	}

	@Override
	public ArrayList<CusShort> getCustomersForMeter(Long subregion_id,
			Long zone, Long customer_id, SUserContext context)
			throws SocarException {
		String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
				+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname , zone\n"
				+ "FROM v_customer_full c "
				+ " where /*building_id is null*/ 1=1 and \n";
		if (customer_id != null)
			sql += " c.cusid=?";
		else
			sql += " subregionid =? and  zone=?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<CusShort> result = null;
		try {
			conn = DBOperations.getConnection(Constants.DBN_GASS);
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			if (customer_id != null)
				stmt.setLong(index++, customer_id);
			else {
				stmt.setLong(index++, subregion_id);
				stmt.setLong(index++, zone);
			}
			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			result = new ArrayList<CusShort>();
			while (rs.next()) {
				result.add(new CusShort(rs.getLong("cusid"), rs
						.getString("cusname"), rs.getLong("building_id"), rs
						.getLong("zone")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}
		return result;
	}

	@Override
	public ArrayList<CusShort> getCustomersForDistinctMeter(Long subregion_id,
			Long zone, Long customer_id, SUserContext context)
			throws SocarException {
		String sql = "SELECT c.cusid,case when building_id is null then 0 else building_id end building_id "
				+ ",cusname||' ('||cityname ||'-' || streetname ||'/' ||flat||'/' ||home||')' cusname, zone \n"
				+ "FROM v_customer_full c \n"
				+ "where building_id is null and c.custypeid=-100 and c.cusid not in (select dm.cusid from maps.district_meter_mapping dm) and \n";
		if (customer_id != null)
			sql += " c.cusid=?";
		else
			sql += " subregionid =? and  zone=?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<CusShort> result = null;
		int index = 1;
		try {
			conn = DBOperations.getConnection(Constants.DBN_GASS);
			stmt = conn.prepareStatement(sql.toString());
			if (customer_id != null)
				stmt.setLong(index++, customer_id);
			else {
				stmt.setLong(index++, subregion_id);
				stmt.setLong(index++, zone);
			}
			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			result = new ArrayList<CusShort>();
			while (rs.next()) {
				result.add(new CusShort(rs.getLong("cusid"), rs
						.getString("cusname"), rs.getLong("building_id"), rs
						.getLong("zone")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
		}
		return result;
	}

	@Override
	public Customer getCustomerFull(int cusidid, SUserContext context)
			throws SocarException {
		
		// System.out.println("Start getCustomerFull");
		// Timer.start("getCustomerFull");
		// long time = System.currentTimeMillis();
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
		sql.append("FROM   v_customer_full c ");
		sql.append("       INNER JOIN custype ct ");
		sql.append("               ON ct.custypeid = c.custypeid ");
		sql.append("       INNER JOIN cusstatus cs ");
		sql.append("               ON cs.cusstatusid = c.cusstatusid ");
		sql.append("WHERE  cusid = ? ");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		Customer result = null;
		try {
			conn = DBOperations.getConnection(Constants.DBN_GASS);
			stmt = conn.prepareStatement(sql.toString());

			stmt.setInt(1, cusidid);

			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();

			if (rs.next()) {
				result = new Customer();
				int index = 1;
				result.setCusid(rs.getInt(index++));
				result.setCusname(rs.getString(index++));
				result.setRegion(rs.getString(index++));
				result.setRaion(rs.getString(index++));
				result.setZone(rs.getLong(index++));
				result.setCityname(rs.getString(index++));
				result.setStreetname(rs.getString(index++));
				result.setHome(rs.getString(index++));
				result.setFlat(rs.getString(index++));
				result.setScopename(rs.getString(index++));
				result.setCusstatusname(rs.getString(index++));
				result.setCustypename(rs.getString(index++));
				result.setMeters(loadMeters((long) result.getCusid(), null,
						context, conn));
				result.setBalances(loadBalance((long) result.getCusid(),
						context, conn));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {
			DBOperations.closeAll(rs, stmt, conn);
			// Timer.step("getCustomerFull");
			// System.out.println("Stop getCustomerFull in "
			// + (System.currentTimeMillis() - time) + "ms");
			// Timer.printall();
		}
		return result;

	}

	@Override
	public ArrayList<Meter> loadMeters(Long customer_id, Long meter_id,
			SUserContext context) throws SocarException {
		return loadMeters(customer_id, meter_id, context, null);
	}

	private ArrayList<Meter> loadMeters(Long customer_id, Long meter_id,
			SUserContext context, Connection mconn) throws SocarException {
		String sqlMeter = " select meterid, m.cusid,mtypename,metserial,start_index, name mstatus, (select newval from docmeter dm where meterid = m.meterid and lastindex = 1) last_value\n"
				+ " from meter m, mtype mt, mstatus ms \n"
				+ " where m.mtypeid= mt.mtypeid and ms.mstatusid= m.mstatusid";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if (customer_id != null)
			sqlMeter += " and m.cusid=?";
		if (meter_id != null)
			sqlMeter += " and m.meterid=?";
		ArrayList<Meter> result = null;
		try {
			conn = mconn == null ? DBOperations
					.getConnection(Constants.DBN_GASS) : mconn;
			stmt = conn.prepareStatement(sqlMeter.toString());
			int index = 1;
			if (customer_id != null)
				stmt.setLong(index++, customer_id);
			if (meter_id != null)
				stmt.setLong(index++, meter_id);

			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			result = new ArrayList<Meter>();
			while (rs.next()) {
				result.add(new Meter(rs.getInt(0 + 1), rs.getInt(1 + 1), rs
						.getString(2 + 1), rs.getString(3 + 1), rs
						.getString(4 + 1), rs.getString(5 + 1), rs
						.getDouble(6 + 1)));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {

			DBOperations.closeAll(rs, stmt);
			if (mconn == null)
				DBOperations.closeAll(conn);
		}
		return result;
	}

	@Override
	public ArrayList<Balance> loadBalance(Long customer_id, SUserContext context)
			throws SocarException {

		return loadBalance(customer_id, context, null);
	}

	private ArrayList<Balance> loadBalance(Long customer_id,
			SUserContext context, Connection mconn) throws SocarException {
		String sqlBalance = " SELECT  nba.cusid,abs(nba.balance) balance,descrip,CASE\n"
				+ "  WHEN nba.balance > 0 THEN 'კრედიტი'\n"
				+ "   ELSE 'ვალი'\n"
				+ "    end\n"
				+ "  loantype\n"
				+ "   FROM nb_accounts nba, nb_account_type nbat\n"
				+ "  WHERE   nba.type_id= nbat.id and nba.balance <> 0  and nba.cusid=? and nba.type_id=1";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		ArrayList<Balance> result = null;
		try {
			conn = mconn == null ? DBOperations
					.getConnection(Constants.DBN_GASS) : mconn;
			stmt = conn.prepareStatement(sqlBalance.toString());
			int index = 1;

			stmt.setLong(index++, customer_id);

			// stmt.setInt(4, buffer);
			rs = stmt.executeQuery();
			result = new ArrayList<Balance>();
			while (rs.next()) {
				result.add(new Balance(rs.getInt(0 + 1), rs.getDouble(1 + 1),
						rs.getString(2 + 1), rs.getString(3 + 1)));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		} finally {

			DBOperations.closeAll(rs, stmt);
			if (mconn == null)
				DBOperations.closeAll(conn);
		}
		return result;
	}

	@Override
	public MakeDBProcess createDBMakingProcess(int subregionid, String szones,
			Date lastDownloadedTiles) throws SocarException {
		MakeDBProcess process = new MakeDBProcess();
		ArrayList<String> tasks = new ArrayList<String>();

		String[] stasks = { "Creating tiles DB", "Map info", "Buildings",
				"Roads", "Settlements", "District meters", "Region Bounds",
				"Customers", "Accounts", "Meters", "Cities", "Subregions",
				"Regions", "Zones", "Meter Type", "Meter Status",
				"Account type", "Customer type", "Customer status",
				"Building To Customers", "Users", "Vacuum", "Vacuuming",
				"Zipping" };
		for (String string : stasks) {
			tasks.add(string);
		}

		String sessionID = createUniqueIDForFileTransfer();
		process.setOperations(tasks.toArray(stasks));
		process.setSessionID(sessionID);
		// new CopyDB(null, subregionid, lastDownloadedTiles, process);
		makeDBTasks.put(sessionID, new MakeDBTask(subregionid, szones,
				sessionID, lastDownloadedTiles, false));
		return process;
	}

	public static void flushMakeDB(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws SocarException {
		String sessionID = request.getParameter("sessionid");

		MakeDBTask dbTask = makeDBTasks.get(sessionID);
		if (makeDBTasks == null)
			return;
		try {
			dbTask.flush(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}
	}

	@Override
	public MakeDBResponce getMakeDBProcessStatus(String sessionID)
			throws SocarException {
		MakeDBTask dbTask = makeDBTasks.get(sessionID);
		if (makeDBTasks == null)
			return null;
		Exception ex = dbTask.getException();
		if (ex != null)
			throw SocarExceptionCriator.doThrow(ex);
		MakeDBResponce dbResponce = new MakeDBResponce();
		dbResponce.setCompleted(dbTask.isCompleted());
		dbResponce.setFileSize(dbTask.getFileSize());
		dbResponce.setOperationCompleted(dbTask.getOperationCompleted());
		return dbResponce;
	}

	@Override
	public void ping() {
		// TODO Auto-generated method stub

	}

	@Override
	public MakeDBProcess downloadTileDB(int subregionid,
			Date lastDownloadedTiles) throws SocarException {
		MakeDBProcess process = new MakeDBProcess();
		try {
			new CopyDB(null, subregionid, lastDownloadedTiles, process);
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}
		return process;
	}

	@Override
	public Simple_View createAccidentCase(Case accident_case)
			throws SocarException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Simple_View saveAccidentStep(Step accident_case)
			throws SocarException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Simple_View> getAccidents(int user_id, Date start_date,
			Date end_date) throws SocarException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Simple_View> getMyAccidents(Integer status_id,
			int user_id, Date start_date, Date end_date) throws SocarException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Case getAccidentCase(int id, int user_id) throws SocarException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateDistinctMeter(int cusid, int user_id, double px,
			double py, int raiid, boolean demage, boolean remove)
			throws SocarException {
		try {
			return DBOperations.updateDistinctMeter(cusid, user_id, px, py,
					raiid, demage, remove);
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}

	}

	@Override
	public void proceedNewBuilding(NewBuilding newBuilding, boolean delete,
			SUserContext context) throws SocarException {
		try {
			DBOperations.proceedNewBuilding(newBuilding, delete, context);
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}

	}

	@Override
	public void saveMeter(Long meterid, double value, Long cusid, int user_id,
			long modify_time) throws SocarException {
		try {
			DBOperations.saveMeter(meterid, value, cusid, user_id, modify_time);
		} catch (Exception e) {
			e.printStackTrace();
			throw SocarExceptionCriator.doThrow(e);
		}

	}

}
