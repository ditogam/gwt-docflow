package com.docflow.server.db.map;

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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.common.db.DBConnection;
import com.docflow.server.DMIUtils;

public class MakeDBTask implements Runnable {
	private int subregionid;
	private int subregionid_ForMap;
	private String sessionID;
	private int operationCompleted = 0;
	private Exception exception;
	private boolean completed;
	private String zip_file_name;
	private String zipFile_path;
	private Date lastDownloaded;
	private int fileSize;

	private static int val = 1;
	private static final TablesDefinition[] definitions = new TablesDefinition[] {
			new TablesDefinition("mapinfo", "getMapInfo",
					new FieldDefinition[] {
							new FieldDefinition("last_updated",
									java.sql.Types.TIMESTAMP),
							new FieldDefinition("geom_text"),
							new FieldDefinition("lcentroid_text"),
							new FieldDefinition("distr_geo"),
							new FieldDefinition("id", java.sql.Types.INTEGER),
							new FieldDefinition("region_id",
									java.sql.Types.INTEGER)

					}, false)

			,

			new TablesDefinition(
					"buildings",
					"getBuildings",
					new FieldDefinition[] {
							new FieldDefinition("geom_text"),
							new FieldDefinition("senobis_no"),
							new FieldDefinition("lcentroid_text"),
							new FieldDefinition("buid", java.sql.Types.INTEGER),
							new FieldDefinition("has_customers",
									java.sql.Types.INTEGER) }, true)

			,

			new TablesDefinition("roads", "getRoads", new FieldDefinition[] {
					new FieldDefinition("geom_text"),
					new FieldDefinition("rname"),
					new FieldDefinition("ruid", java.sql.Types.INTEGER) }, true)

			,

			new TablesDefinition(
					"settlements",
					"getSettlements",
					new FieldDefinition[] { new FieldDefinition("geom_text"),
							new FieldDefinition("id", java.sql.Types.INTEGER) },
					false),

			new TablesDefinition(
					"district_meters",
					"getDistrict_meters",
					new FieldDefinition[] {
							new FieldDefinition("geom_text"),
							new FieldDefinition("cusid", java.sql.Types.INTEGER) },
					true)

	};

	public MakeDBTask(int subregionid, String sessionID, Date lastDownloaded) {
		this.sessionID = sessionID;
		this.subregionid = subregionid;
		this.lastDownloaded = lastDownloaded;
		operationCompleted = 0;
		setCompleted(false);
		new Thread(this).start();
	}

	public static final HashMap<String, Integer> checkForUpdates(
			int subregionid, Date lastDownloaded) throws Exception {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		Map<?, ?> record = DMIUtils.findRecordById("MapDownloaderDS",
				"getSubregionForMap", subregionid, "subregion_id");

		if (record == null || !record.containsKey("subregion_id")) {
			throw new Exception("Mapping not found");
		}

		int subregionid_ForMap = DMIUtils.getRowValueLong(
				record.get("subregion_id")).intValue();

		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("subregion_id", subregionid_ForMap);
		if (lastDownloaded != null) {
			criteria.put("updatetime", new Timestamp(lastDownloaded.getTime()));
		}
		criteria.put("cnt", 1);
		for (TablesDefinition tb : definitions) {
			if (tb.isMakeAnyway()) {
				tb.addCounts(criteria, result);
			}

		}
		return result;
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
		Connection sqlite = null;
		try {

			Map<?, ?> record = DMIUtils.findRecordById("MapDownloaderDS",
					"getSubregionForMap", subregionid, "subregion_id");

			if (record == null || !record.containsKey("subregion_id")) {
				throw new Exception("Mapping not found");
			}

			subregionid_ForMap = DMIUtils.getRowValueLong(
					record.get("subregion_id")).intValue();

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
				InputStream is = this.getClass().getResourceAsStream(
						db_file_name);
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
			}

			operationCompleted++;
			Class.forName("org.sqlite.JDBC");

			Properties prop = new Properties();
			prop.setProperty("shared_cache", "true");
			sqlite = DriverManager
					.getConnection("jdbc:sqlite:" + db_name, prop);
			sqlite.setAutoCommit(false);

			// createTiles(subregionid_ForMap, sqlite, pg);
			// operationCompleted++;

			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("subregion_id", subregionid_ForMap);
			fileSize = 0;
			if (lastDownloaded != null) {
				criteria.put("updatetime",
						new Timestamp(lastDownloaded.getTime()));
			}

			for (TablesDefinition tb : definitions) {
				operationCompleted++;
				if (!tb.isMakeAnyway() && lastDownloaded != null)
					continue;
				fileSize += tb.insert(sqlite, criteria);
			}
			createVacuum(sqlite);
			operationCompleted++;
			vacuuming(sqlite);
			operationCompleted++;

			zip_file_name = "mydb.zip";
			zipFile_path = ioTempDir + System.getProperty("file.separator")
					+ zip_file_name;
			createZip(db_file_name, db_name, zipFile_path);
			operationCompleted++;
			completed = true;

		} catch (Exception e) {
			exception = e;
			setCompleted(true);
		} finally {
			try {
				DBConnection.freeConnection(sqlite);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
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
		sqlite.close();
	}

	private void createVacuum(Connection sqlite) throws SQLException {
		sqlite.commit();
		sqlite.setAutoCommit(true);
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
		System.out.println(dateFormat.format(new Date(1406802729083L)));
	}
}
