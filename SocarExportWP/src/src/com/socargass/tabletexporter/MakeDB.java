package src.com.socargass.tabletexporter;

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
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

public class MakeDB {
	private static int val = 1;

	public static void generateDB(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {

		int subregionid = 0;
		try {
			subregionid = Integer.parseInt(request.getParameter("subregion"));
		} catch (Exception e) {

		}
		String zones = "";
		try {
			String szones = request.getParameter("zones");
			szones = szones.trim();
			for (String str : szones.split(",")) {
				Long.parseLong(str.trim());
			}
			zones = " and zone in (" + szones + ")";
		} catch (Exception e) {

		}
		ServletContext context = request.getSession().getServletContext();
		File directory = (File) context
				.getAttribute("javax.servlet.context.tempdir");

		String ioTempDir = directory.getAbsolutePath();
		if (!ioTempDir.endsWith(System.getProperty("file.separator")))
			ioTempDir += System.getProperty("file.separator");
		ioTempDir += "mydb";

		File dir = new File(ioTempDir);
		if (!dir.exists())
			dir.mkdirs();
		ioTempDir += System.getProperty("file.separator") + session.getId()
				+ (val++);
		dir = new File(ioTempDir);
		if (!dir.exists())
			dir.mkdirs();

		String db_file_name = "mydb.sqlite";
		String db_name = ioTempDir + System.getProperty("file.separator")
				+ db_file_name;

		try {
			InputStream is = DBConnection.class
					.getResourceAsStream(db_file_name);
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

		Class.forName("org.sqlite.JDBC");

		Properties prop = new Properties();
		prop.setProperty("shared_cache", "true");
		Connection sqlite = DriverManager.getConnection("jdbc:sqlite:"
				+ db_name, prop);
		sqlite.setAutoCommit(false);

		Connection pg = DBConnection.getConnection("GassMap");
		ResultSet rs = pg
				.createStatement()
				.executeQuery(
						"select buid, regid,raiid, astext(transform(the_geom,4326)) the_geom from buildings where raiid="
								+ subregionid + "");
		PreparedStatement ps = sqlite
				.prepareStatement("delete from buildings where raiid="
						+ subregionid + "");
		ps.executeUpdate();

		ps.close();
		ps = sqlite
				.prepareStatement("insert into buildings(buid, regid,raiid,geom_text) values (?,?,?,?)");
		while (rs.next()) {
			int index = 1;
			int buid = rs.getInt(index++);

			ps.setInt(1, buid);
			ps.setInt(index, rs.getInt(index++));
			ps.setInt(index, rs.getInt(index++));
			ps.setString(index, rs.getString(index++));
			ps.executeUpdate();
		}
		// out.print("ending building" + "<br>");
		rs.getStatement().close();
		rs.close();
		ps.close();
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

		pg = DBConnection.getConnection("Gass");

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

		sqlite.commit();
		sqlite.setAutoCommit(true);

		sqlite.createStatement().execute("vacuum");
		// sqlite.createStatement().execute("vacuum district_meters");
		sqlite.close();
		String zip_file_name = "mydb.zip";
		String zipFile_path = ioTempDir + System.getProperty("file.separator")
				+ zip_file_name;
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				zipFile_path));

		ZipEntry ze = new ZipEntry(db_file_name);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(db_name);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();

		// remember close it
		zos.close();
		// out.print(new File(db_name).getAbsolutePath());
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
		delete(new File(ioTempDir));

	}

	private static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}
}
