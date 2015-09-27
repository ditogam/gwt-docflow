package com.socarmap.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.socargass.tabletexporter.MakeDB;
import com.socarmap.server.db.DBOperations;
import com.socarmap.server.tasks.CopyDB;

public class MakeDBTask implements Runnable {
	private int subregionid;
	private int subregionid_ForMap;
	private String szones;
	private String sessionID;
	private int operationCompleted = 0;
	private Exception exception;
	private boolean completed;
	private String zip_file_name;
	private String zipFile_path;
	private Date lastDownloaded;
	private boolean shouldCopyTiles;
	private int fileSize;

	private static int val = 1;

	public MakeDBTask(int subregionid, String szones, String sessionID,
			Date lastDownloaded, boolean shouldCopyTiles) {
		this.sessionID = sessionID;
		this.szones = szones;
		this.subregionid = subregionid;
		this.lastDownloaded = lastDownloaded;
		this.shouldCopyTiles = shouldCopyTiles;
		operationCompleted = 0;
		setCompleted(false);
		new Thread(this).start();
	}

	public Exception getException() {
		return exception;
	}

	public int getOperationCompleted() {
		return operationCompleted;
	}

	public int getFileSize() {
		return fileSize;
	}

	@Override
	public void run() {
		String zones = "";
		try {
			szones = szones.trim();
			for (String str : szones.split(",")) {
				Long.parseLong(str.trim());
			}
			zones = " and zone in (" + szones + ")";
		} catch (Exception e) {

		}

		Connection pg = null;
		try {
			subregionid_ForMap = DBOperations.getSubregionForMap(subregionid);
			File directory = new File(System.getProperty("java.io.tmpdir"));

			String ioTempDir = directory.getAbsolutePath();
			if (!ioTempDir.endsWith(System.getProperty("file.separator")))
				ioTempDir += System.getProperty("file.separator");
			ioTempDir += "mydb";

			File dir = new File(ioTempDir);
			if (!dir.exists())
				dir.mkdirs();
			ioTempDir += System.getProperty("file.separator") + sessionID
					+ (val++);
			dir = new File(ioTempDir);
			if (!dir.exists())
				dir.mkdirs();

			String db_file_name = "mydb.sqlite";
			String db_name = ioTempDir + System.getProperty("file.separator")
					+ db_file_name;

			try {
				InputStream is = MakeDB.class.getResourceAsStream(db_file_name);
				// out.print("is==" + is);
				byte[] bt = new byte[is.available()];
				is.read(bt);
				FileOutputStream fos = new FileOutputStream(db_name);
				fos.write(bt);
				fos.flush();
				fos.close();
				// out.print(db_name);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				// out.print(sw);
			}
			if (shouldCopyTiles)
				new CopyDB(db_name, subregionid_ForMap, lastDownloaded, null);
			operationCompleted++;
			Class.forName("org.sqlite.JDBC");

			Properties prop = new Properties();
			prop.setProperty("shared_cache", "true");
			Connection sqlite = DriverManager.getConnection("jdbc:sqlite:"
					+ db_name, prop);
			sqlite.setAutoCommit(false);

			pg = DBOperations.getConnection(Constants.DBN_MAP);

			// createTiles(subregionid_ForMap, sqlite, pg);
			// operationCompleted++;

			createMapInfo(subregionid_ForMap, sqlite, pg);
			operationCompleted++;

			createBuildings(subregionid_ForMap, sqlite, pg);
			operationCompleted++;

			createRoads(subregionid_ForMap, sqlite, pg);
			operationCompleted++;

			createSettlements(subregionid_ForMap, sqlite, pg);
			operationCompleted++;

			createDistrict_meters(subregionid_ForMap, sqlite, pg);
			operationCompleted++;

			createregionBounds(sqlite, pg);
			operationCompleted++;

			pg = createCustomers(subregionid, zones, sqlite);
			operationCompleted++;

			createNB_Accounts(subregionid, zones, sqlite, pg);
			operationCompleted++;

			createMeter(subregionid, zones, sqlite, pg);
			operationCompleted++;
			createCity(subregionid, sqlite, pg);
			operationCompleted++;

			createPCity(subregionid, sqlite, pg);
			operationCompleted++;
			createPPCity(subregionid, sqlite, pg);
			operationCompleted++;

			createZones(subregionid, zones, sqlite, pg);
			operationCompleted++;
			createMType(sqlite, pg);
			operationCompleted++;

			createMStatus(sqlite, pg);
			operationCompleted++;

			createNBAccountType(sqlite, pg);
			operationCompleted++;

			createCusType(sqlite, pg);
			operationCompleted++;

			createCusStatus(sqlite, pg);
			operationCompleted++;

			createBuildingToCustomers(subregionid, sqlite, pg);
			operationCompleted++;
			createUsers(sqlite, pg);
			operationCompleted++;

			createVacuum(sqlite);
			operationCompleted++;

			vacuuming(sqlite);
			operationCompleted++;
			zip_file_name = "mydb.zip";
			zipFile_path = ioTempDir + System.getProperty("file.separator")
					+ zip_file_name;
			createZip(db_file_name, db_name, zipFile_path);
			completed = true;
			operationCompleted++;
		} catch (Exception e) {
			exception = e;
			setCompleted(true);
		} finally {
			DBOperations.closeAll(pg);
		}
	}

	@SuppressWarnings("unused")
	private void createTiles(int subregionid_ForMap2, Connection sqlite,
			Connection pg) throws Exception {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select (f).zoom,(f).x, (f).y from(select subregion_id,maps.creategeometryzxy(the_geom,10,17,false) f from subregions where subregion_id is not null and  subregion_id ="
								+ subregionid_ForMap2
								+ " order by  subregion_id ) l");
		ps = sqlite
				.prepareStatement("insert into tiles(zoom,x,y) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();

	}

	private void createSettlements(int subregionid_ForMap2, Connection sqlite,
			Connection pg) throws Exception {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select id, subregion_id, astext(transform(the_geom,4326)) from maps.settlements where subregion_id="
								+ subregionid_ForMap2);
		ps = sqlite
				.prepareStatement("insert into settlements(id, subregion_id, geom_text) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();

	}

	private void createMapInfo(int subregionid_ForMap2, Connection sqlite,
			Connection pg) throws Exception {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select subregion_id, astext(transform(the_geom,4326)), astext(transform(globals.st_subregion_centroid(subregion_id),4326)) from subregions where the_geom is not null and subregion_id ="
								+ subregionid + "");
		ps = sqlite
				.prepareStatement("insert into map_info(ppcityid, the_geom_text,center_text) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			int buid = rs.getInt(index++);
			ps.setInt(1, buid);
			ps.setString(index, rs.getString(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}
		// out.print("ending building" + "<br>");
		rs.getStatement().close();
		rs.close();
		ps.close();

	}

	public void flush(HttpServletResponse response) throws Exception {
		createResponce(response, zip_file_name, zipFile_path);
	}

	private void createResponce(HttpServletResponse response,
			String zip_file_name, String zipFile_path) throws Exception {
		ServletOutputStream sos = response.getOutputStream();
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ zip_file_name + "\"");
		FileInputStream fis = new FileInputStream(zipFile_path);
		byte[] zip = new byte[fis.available()];
		fis.read(zip);
		fis.close();
		sos.write(zip);
		sos.flush();
	}

	private void createZip(String db_file_name, String db_name,
			String zipFile_path) throws FileNotFoundException, IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				zipFile_path));

		ZipEntry ze = new ZipEntry(db_file_name);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(db_name);
		byte[] buffer = new byte[1024];
		int len;
		fileSize = 0;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();

		// remember close it
		zos.close();

		in = new FileInputStream(zipFile_path);
		buffer = new byte[1024];
		while ((len = in.read(buffer)) > 0) {
			fileSize += len;
		}
		in.close();
		new File(db_name).delete();
	}

	private void vacuuming(Connection sqlite) throws SQLException {
		sqlite.createStatement().execute("vacuum");
		// sqlite.createStatement().execute("vacuum district_meters");
		sqlite.close();
	}

	private void createVacuum(Connection sqlite) throws SQLException {
		sqlite.commit();
		sqlite.setAutoCommit(true);
	}

	private void createUsers(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select userid, username,md5(pass) pass,ppcityid,pcityid from users");
		ps = sqlite
				.prepareStatement("insert into users(userid, username, pass,ppcityid,pcityid) values (?,?,?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.setString(index, rs.getString(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createBuildingToCustomers(int subregionid, Connection sqlite,
			Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select building_id, cusid from maps.building_to_customers where cusid in (select cusid from v_customer_full where subregionid="
								+ subregionid + ")");
		ps = sqlite
				.prepareStatement("insert into building_to_customers(building_id,cusid) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createCusStatus(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select cusstatusid,cusstatusname   from cusstatus");
		ps = sqlite
				.prepareStatement("insert into cusstatus(cusstatusid,cusstatusname) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createCusType(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select custypeid,custypename   from custype");
		ps = sqlite
				.prepareStatement("insert into custype(custypeid,custypename) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createNBAccountType(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select id,descrip   from nb_account_type");
		ps = sqlite
				.prepareStatement("insert into nb_account_type(id,descrip) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createMStatus(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select mstatusid,name  from mstatus");
		ps = sqlite
				.prepareStatement("insert into mstatus(mstatusid,name) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createMType(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select mtypeid,mtypename  from mtype");
		ps = sqlite
				.prepareStatement("insert into mtype(mtypeid,mtypename) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createZones(int subregionid, String zones, Connection sqlite,
			Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		String sql = "select distinct ct.pcityid, zone\n"
				+ "  from customer c\n" + " inner join streets s\n"
				+ "    on s.streetid = c.streetid\n" + " inner join city ct\n"
				+ "    on s.cityid = ct.cityid\n" + " where ct.pcityid = "
				+ subregionid + zones + "";
		rs = pg.createStatement().executeQuery(sql);
		ps = sqlite
				.prepareStatement("insert into zones(pcityid, zone) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setLong(index, rs.getLong(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createPPCity(int subregionid, Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select ppcityid,ppcityname from ppcity where ppcityid in (select ppcityid from pcity where pcityid="
								+ subregionid + ")");
		ps = sqlite
				.prepareStatement("insert into ppcity(ppcityid,ppcityname) values (?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createPCity(int subregionid, Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select pcityid,pcityname, ppcityid from pcity where pcityid="
						+ subregionid + "");
		ps = sqlite
				.prepareStatement("insert into pcity(pcityid,pcityname, ppcityid) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createCity(int subregionid, Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select cityid,cityname, pcityid from city where pcityid="
						+ subregionid + "");
		ps = sqlite
				.prepareStatement("insert into city(cityid,cityname, pcityid) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createMeter(int subregionid, String zones, Connection sqlite,
			Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(

				"SELECT a.cusid,\n" + "       a.mtypeid,\n"
						+ "       meterid,\n" + "       start_index,\n"
						+ "       metserial,\n" + "       mstatusid,\n"
						+ "       (select newval\n"
						+ "          from docmeter dm\n"
						+ "         where meterid = a.meterid\n"
						+ "           and lastindex = 1) last_value\n"
						+ "  FROM meter a\n"
						+ " where a.cusid in (select cusid\n"
						+ "                     from customer c\n"
						+ "                    inner join streets s\n"
						+ "                       on s.streetid = c.streetid\n"
						+ "                    inner join city ct\n"
						+ "                       on s.cityid = ct.cityid\n"
						+ "                    where ct.pcityid = "
						+ subregionid + zones + ")");

		ps = sqlite
				.prepareStatement("insert into meter (cusid, mtypeid,meterid,start_index,metserial,last_value,mstatusid) values("
						+ "?,?,?,?,?,?,?" + ")");
		while (rs.next()) {
			int paramindex = 1;
			ps.setLong(paramindex++, rs.getLong("cusid"));
			ps.setLong(paramindex++, rs.getLong("mtypeid"));
			ps.setLong(paramindex++, rs.getLong("meterid"));
			ps.setDouble(paramindex++, rs.getDouble("start_index"));
			ps.setString(paramindex++, rs.getString("metserial"));
			ps.setDouble(paramindex++, rs.getDouble("last_value"));
			ps.setLong(paramindex++, rs.getLong("mstatusid"));
			ps.executeUpdate();
		}
		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createNB_Accounts(int subregionid, String zones,
			Connection sqlite, Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"SELECT a.cusid, a.type_id, a.balance\n"
						+ "  FROM nb_accounts a\n"
						+ " where a.cusid in (select cusid\n"
						+ "                     from customer c\n"
						+ "                    inner join streets s\n"
						+ "                       on s.streetid = c.streetid\n"
						+ "                    inner join city ct\n"
						+ "                       on s.cityid = ct.cityid\n"
						+ "                    where ct.pcityid = "
						+ subregionid + zones + ")");

		ps = sqlite
				.prepareStatement("insert into nb_accounts (cusid, type_id,balance) values("
						+ "?,?,?" + ")");
		while (rs.next()) {
			int paramindex = 1;
			ps.setLong(paramindex++, rs.getLong("cusid"));
			ps.setLong(paramindex++, rs.getLong("type_id"));
			ps.setDouble(paramindex++, rs.getDouble("balance"));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private Connection createCustomers(int subregionid, String zones,
			Connection sqlite) throws Exception, SQLException {
		Connection pg;
		ResultSet rs;
		PreparedStatement ps;
		pg = DBOperations.getConnection(Constants.DBN_GASS);

		rs = pg.createStatement().executeQuery(
				"select * from v_customer_full where subregionid="
						+ subregionid + zones);
		ps = sqlite
				.prepareStatement("insert into customers (cusid, cusname,region, raion, zone,cityname,streetname, home, "
						+ "flat, scopename, subregionid, regionid, cityid, streetid, cusstatusid, custypeid) values("
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + ")");
		while (rs.next()) {
			int paramindex = 1;
			ps.setLong(paramindex++, rs.getLong("cusid"));
			ps.setString(paramindex++, rs.getString("cusname"));
			ps.setString(paramindex++, rs.getString("region"));
			ps.setString(paramindex++, rs.getString("raion"));
			ps.setLong(paramindex++, rs.getLong("zone"));
			ps.setString(paramindex++, rs.getString("cityname"));
			ps.setString(paramindex++, rs.getString("streetname"));
			ps.setString(paramindex++, rs.getString("home"));
			ps.setString(paramindex++, rs.getString("flat"));
			ps.setString(paramindex++, rs.getString("scopename"));
			ps.setLong(paramindex++, rs.getLong("subregionid"));
			ps.setLong(paramindex++, rs.getLong("regionid"));
			ps.setLong(paramindex++, rs.getLong("cityid"));
			ps.setLong(paramindex++, rs.getLong("streetid"));
			ps.setLong(paramindex++, rs.getLong("cusstatusid"));
			ps.setLong(paramindex++, rs.getLong("custypeid"));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
		return pg;
	}

	private void createregionBounds(Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement().executeQuery(
				"select vtype, id, geomtext from v_region_bounds");
		ps = sqlite
				.prepareStatement("insert into region_bounds(vtype, id, geomtext) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();

		pg.close();
	}

	private void createDistrict_meters(int subregionid, Connection sqlite,
			Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select cusid,raiid, astext(transform(the_geom,4326)) the_geom from district_meters where raiid="
								+ subregionid + "");
		ps = sqlite
				.prepareStatement("insert into district_meters(cusid, raiid,geom_text) values (?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}

		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createBuildings(int subregionid, Connection sqlite,
			Connection pg) throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select buid, regid,raiid, astext(transform(the_geom,4326)) the_geom, has_customers from buildings where raiid="
								+ subregionid + "");
		ps = sqlite.prepareStatement("delete from buildings where raiid="
				+ subregionid + "");
		ps.executeUpdate();

		ps.close();
		ps = sqlite
				.prepareStatement("insert into buildings(buid, regid,raiid,geom_text,has_customer) values (?,?,?,?,?)");
		while (rs.next()) {
			int index = 1;
			int buid = rs.getInt(index++);

			ps.setInt(1, buid);
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.executeUpdate();
		}
		// out.print("ending building" + "<br>");
		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	private void createRoads(int subregionid, Connection sqlite, Connection pg)
			throws SQLException {
		ResultSet rs;
		PreparedStatement ps;
		rs = pg.createStatement()
				.executeQuery(
						"select ruid, rname,regid,raiid, astext(transform(the_geom,4326)) the_geom from roads where raiid="
								+ subregionid + "");
		ps = sqlite.prepareStatement("delete from roads where raiid="
				+ subregionid + "");
		ps.executeUpdate();

		ps.close();
		ps = sqlite
				.prepareStatement("insert into roads(ruid,rname, regid,raiid,geom_text) values (?,?,?,?,?)");
		while (rs.next()) {
			int index = 1;
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}
		// out.print("ending building" + "<br>");
		rs.getStatement().close();
		rs.close();
		ps.close();
	}

	public void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.parse("2013-02-26 10:30:51").getTime());
	}
}
