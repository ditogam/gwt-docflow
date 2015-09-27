package com.docflow.server.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sqlite.SQLiteConfig;

import com.docflow.server.DocFlowExceptionCriator;
import com.docflow.server.DocFlowServiceImpl;
import com.docflow.shared.ClSelection;
import com.docflow.shared.DBData;
import com.docflow.shared.DbExpoResult;
import com.docflow.shared.DocFlowException;

public class Exporter implements Runnable {

	private Map<String, Connection> db_Connections = new HashMap<String, Connection>();
	private Map<String, ServerDBData> tables = new HashMap<String, ServerDBData>();

	private Connection createConnection(String dbname) throws Exception {
		return connectionHelper.createConnection(dbname);
	}

	private Connection getConnection(String dbname) throws Exception {
		Connection conn = db_Connections.get(dbname);
		conn = conn == null ? createConnection(dbname) : conn;
		if (!db_Connections.containsKey(dbname))
			db_Connections.put(dbname, conn);
		return conn;

	}

	private void closeConnection(String dbname) throws Exception {
		Connection conn = db_Connections.get(dbname);
		if (conn != null)
			try {
				connectionHelper.closeConnection(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
		db_Connections.remove(dbname);
	}

	private ArrayList<String> run_afters = new ArrayList<String>();
	private static int val = 0;

	private void putValues(String tbl_name, String field_names,
			String primary_keys, String execute_sql, String db_name,
			String tbl_caption) {

		if (!tables.containsKey(tbl_name))
			tables.put(tbl_name, new ServerDBData(tbl_name, tbl_caption,
					field_names, primary_keys));
		tables.get(tbl_name).addScript(db_name, execute_sql);

	}

	private DbExpoResult current_status;
	private long timeout;
	private Connection spatialConnection;
	private File spatialiteDb;
	private IConnectionHelper connectionHelper;

	private Exporter(int subregion_id, String sessionid,
			IConnectionHelper connectionHelper) throws DocFlowException {
		current_status = new DbExpoResult();
		this.session_id = sessionid;
		this.connectionHelper = connectionHelper;
		timeout = System.currentTimeMillis() + (1000 * 60 * 5);
		createTableDescriptions(subregion_id);
	}

	public void start() {
		new Thread(this).start();
	}

	private void createTableDescriptions(int subregion_id)
			throws DocFlowException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String db_name = "DocFlow";

		try {
			conn = getConnection(db_name);
			stmt = conn.createStatement();
			String sql = "select tbl_caption,tbl_name,db_name,android.get_insert_sql_from_select(execute_sql, "
					+ subregion_id
					+ ") execute_sql,field_names,primary_keys,run_after from android.v_export_tables";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tbl_name = rs.getString("tbl_name");
				String field_names = rs.getString("field_names");
				String primary_keys = rs.getString("primary_keys");
				String run_after = rs.getString("run_after");
				String execute_sql = rs.getString("execute_sql");
				String tbl_caption = rs.getString("tbl_caption");
				String _db_name = rs.getString("db_name");

				if (!tables.containsKey(tbl_name)) {
					run_after = run_after == null ? "" : run_after.trim();
					if (!run_after.isEmpty()) {
						String delims[] = run_after.split(";");
						for (String delim : delims) {
							if (!delim.trim().isEmpty())
								run_afters.add(delim.trim());
						}
					}
				}

				putValues(tbl_name, field_names, primary_keys, execute_sql,
						_db_name, tbl_caption);

			}

			File file = createDBFile();
			SQLiteConfig config = new SQLiteConfig();
			config.enableLoadExtension(true);
			Properties prop = config.toProperties();
			prop.setProperty("shared_cache", "true");

			Class.forName("org.sqlite.JDBC");

			String connStr = "jdbc:sqlite:" + file.toURI().getPath();
			spatialConnection = DriverManager.getConnection(connStr, prop);
			spatialConnection.setAutoCommit(false);
			Statement stmt1 = null;
			try {
				stmt1 = spatialConnection.createStatement();
				stmt1.execute("SELECT load_extension('libspatialite') ");

				System.out.println("Spatialite extention loaded sucsessfully");
			} catch (Exception e) {
				System.out.println("Spatialite extention loaded error");
				e.printStackTrace();
			} finally {
				try {
					stmt1.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

			Collection<ServerDBData> values = tables.values();
			for (ServerDBData serverDBData : values) {
				serverDBData.createStetement(spatialConnection);
			}
		} catch (Throwable e) {
			throw DocFlowExceptionCriator.doThrow(e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
			}
			try {
				stmt.close();
			} catch (Exception e2) {
			}

			try {
				closeConnection(db_name);
			} catch (Exception e2) {
			}

		}

	}

	public File createDBFile() throws IOException, FileNotFoundException {
		byte[] bt = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = this.getClass().getResourceAsStream("mydb.db");
		ServerDBData.copyLarge(is, bos);
		bos.flush();
		bt = bos.toByteArray();
		bos.close();
		is.close();

		File directory = new File(System.getProperty("java.io.tmpdir"));

		String ioTempDir = directory.getAbsolutePath();
		if (!ioTempDir.endsWith(System.getProperty("file.separator")))
			ioTempDir += System.getProperty("file.separator");
		ioTempDir += "mydb";

		File dir = new File(ioTempDir);
		if (!dir.exists())
			dir.mkdirs();

		ioTempDir += System.getProperty("file.separator") + session_id
				+ (val++);
		dir = new File(ioTempDir);
		if (!dir.exists())
			dir.mkdirs();

		String db_file_name = "mydb.sqlite";
		String _db_name = ioTempDir + System.getProperty("file.separator")
				+ db_file_name;
		File file = new File(_db_name);
		spatialiteDb = file;
		FileOutputStream fos = new FileOutputStream(file);

		ServerDBData.copyLarge(new ByteArrayInputStream(bt), fos);
		fos.flush();
		fos.close();
		return file;
	}

	public ArrayList<DBData> getDescription() {
		ArrayList<DBData> result = new ArrayList<DBData>();
		Collection<ServerDBData> values = tables.values();
		for (ServerDBData serverDBData : values) {
			result.add(serverDBData.cloneObject());
		}
		DBData dbData = new DBData();
		dbData.setTbl_name("adding_ds");
		dbData.setTbl_caption("Datasources");
		result.add(dbData);

		dbData = new DBData();
		dbData.setTbl_name("running_after");
		dbData.setTbl_caption("სკრიპტი");
		result.add(dbData);

		dbData = new DBData();
		dbData.setTbl_name("creating_zip");
		dbData.setTbl_caption("არქივირება");
		result.add(dbData);

		return result;
	}

	private static Map<String, Exporter> exporter_sessions = new HashMap<String, Exporter>();
	private String session_id;

	public static void expireOldExporters() {
		long time = System.currentTimeMillis();
		ArrayList<String> removeSessions = new ArrayList<String>();

		for (String sessionid : exporter_sessions.keySet()) {
			Exporter e = exporter_sessions.get(sessionid);
			if (time > e.timeout) {
				e.destroy();
			}
		}
		for (String sessionid : removeSessions) {
			exporter_sessions.remove(sessionid);
		}
	}

	public static DbExpoResult createExporterSession(int subregion_id) {
		return createExporterSession(subregion_id, new WebConnectionHelper());
	}

	public static DbExpoResult createExporterSession(int subregion_id,
			IConnectionHelper connectionHelper) {
		expireOldExporters();
		DbExpoResult result = new DbExpoResult();
		try {
			result.setSession_id(UUID.randomUUID().toString());
			Exporter ex = new Exporter(subregion_id, result.getSession_id(),
					connectionHelper);
			result.setDbDatas(ex.getDescription());
			exporter_sessions.put(result.getSession_id(), ex);
			ex.start();
		} catch (Exception e) {
			e.printStackTrace();
			result.setException(DocFlowExceptionCriator.doThrow());
		}
		return result;
	}

	public static DbExpoResult getExporterCurrentStatus(String sessionid)
			throws DocFlowException {
		Exporter ex = exporter_sessions.get(sessionid);
		DbExpoResult result = ex.current_status;
		if ((result.getDone() != null && result.getDone().booleanValue())) {
			ex.timeout += System.currentTimeMillis();
		}
		expireOldExporters();
		if (result.getException() != null) {
			ex.destroy();
			throw result.getException();
		}
		return result;
	}

	private void destroy() {
		Collection<ServerDBData> values = tables.values();
		for (ServerDBData serverDBData : values) {
			// serverDBData.destroy();
		}
		exporter_sessions.remove(session_id);
	}

	private String readFile(File file) throws IOException {

		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}

	@Override
	public void run() {
		try {

			for (String table : tables.keySet()) {
				current_status.setTableName(table);
				ServerDBData data = tables.get(table);
				for (String db : data.getSql_scripts().keySet()) {
					String sql_script = data.getSql_scripts().get(db);
					try {
						collectTableData(db, data, sql_script);

					} catch (Throwable e) {
						System.err.println(db + " data=" + data.getTbl_name());
						current_status.setException(DocFlowExceptionCriator
								.doThrow(e));
						return;
					}
				}
				try {
					data.doFinish();

				} catch (Throwable e) {
					current_status.setException(DocFlowExceptionCriator
							.doThrow(e));
					return;
				}
			}

			current_status.setTableName("adding_ds");
			Statement stmt1 = null;
			try {
				stmt1 = spatialConnection.createStatement();
				stmt1.execute("create table " + ClSelection.SMARTGWTDATASOURCES
						+ " (id number, "
						+ ClSelection.SMARTGWTDATASOURCE_CONTENT
						+ " text,PRIMARY KEY ( id))");

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					stmt1.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

			if (DocFlowServiceImpl.dsDir != null) {
				PreparedStatement ps = null;
				try {
					ps = spatialConnection.prepareStatement("insert into "
							+ ClSelection.SMARTGWTDATASOURCES
							+ " (id , "+ClSelection.SMARTGWTDATASOURCE_CONTENT+") values(?,?)");

					File dsDir = new File(DocFlowServiceImpl.dsDir);
					File[] dsFiles = dsDir.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							if (name.toLowerCase().endsWith(".ds.xml")) {
								return true;
							} else {
								return false;
							}
						}
					});
					int id = 1;
					for (int i = 0; i < dsFiles.length; i++) {
						File file = dsFiles[i];
						try {
							String dsDefiniton = readFile(file);
							ps.setInt(1, id++);
							ps.setString(2, dsDefiniton);
							ps.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						ps.close();
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}

			}
			try {

				Statement stmt = spatialConnection.createStatement();
				current_status.setTableName("running_after");
				ArrayList<String> errorsrun_afters = new ArrayList<String>();
				for (String run_after : run_afters) {
					try {
						stmt.execute(run_after);
					} catch (Exception e) {
						errorsrun_afters.add(run_after);
						System.err.println("error on " + run_after);
					}
				}
				run_afters = errorsrun_afters;
				current_status.setTableName("creating_zip");
				writeOutputStream();
			} catch (Throwable e) {
				current_status.setException(DocFlowExceptionCriator.doThrow(e));
				return;
			}

		} finally {
			try {
				for (String db : db_Connections.keySet()) {
					closeConnection(db);
				}
			} catch (Throwable e) {
				// TODO: handle exception
			}
		}
		current_status.setDone(true);
		current_status.setTableName(null);
	}

	private void collectTableData(String db, ServerDBData data,
			String sql_script) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection(db);
			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql_script);
			// if (!rs.next()) {
			// return;
			// }
			// Reader r = rs.getCharacterStream("insertstmt");
			// data.appendInputStream(r);
			while (rs.next()) {
				data.addToBatch(rs);
			}
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
			}
			try {
				stmt.close();
			} catch (Exception e2) {
			}

		}

	}

	private ByteArrayOutputStream outstr;

	public static Integer writeBytes(String sessionid, OutputStream s_out)
			throws Exception {
		Exporter ex = null;
		ex = exporter_sessions.get(sessionid);
		if (ex == null)
			return null;
		byte[] bt = ex.outstr.toByteArray();
		ex.outstr.close();
		ServerDBData.copyLarge(new ByteArrayInputStream(bt), s_out);
		return bt.length;

	}

	public static void writeResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Exporter ex = null;
		try {

			String sessionid = request.getParameter("sessionid");
			if (sessionid == null)
				return;

			OutputStream s_out = response.getOutputStream();

			Integer length = writeBytes(sessionid, s_out);

			String outfile = "data.gz";
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ outfile + "\"");
			response.setHeader("Content-Type", "application/octet-stream;");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Length", String.valueOf(length));

			System.err.println("before wrote");
			System.err.println("before flush");
			s_out.flush();
			System.err.println("before close");
			s_out.close();
			System.err.println("ddd result bytes=" + length);
		} finally {
			if (ex != null)
				ex.destroy();
			expireOldExporters();
		}
	}

	public void writeOutputStream() throws Exception {
		try {
			outstr = new ByteArrayOutputStream();
			ZipOutputStream zipfile = new ZipOutputStream(outstr);
			// Collection<ServerDBData> values = tables.values();
			// for (ServerDBData serverDBData : values) {
			// serverDBData.addZipEntry(zipfile);
			// }
			ServerDBData data = new ServerDBData("update.sql");
			String outwr = "";
			for (String run_after : run_afters) {
				if (!outwr.isEmpty())
					outwr += ";\n";
				outwr += (run_after.trim());
			}
			data.appendInputStream(new StringReader(outwr));
			data.addZipEntry(zipfile);
			spatialConnection.commit();
			spatialConnection.close();

			FileInputStream fis = new FileInputStream(spatialiteDb);
			ZipEntry zipentry = new ZipEntry("exported.sqlite");
			zipfile.putNextEntry(zipentry);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServerDBData.copyLarge(fis, bos);

			byte[] by = bos.toByteArray();
			zipfile.write(by);

			zipfile.close();

			outstr.flush();
			current_status.setFileSize(outstr.size());
			spatialiteDb.delete();
			spatialiteDb.getParentFile().delete();
		} finally {

		}

	}

	public DbExpoResult getCurrent_status() {
		return current_status;
	}

	private static void createZip(Map<String, InputStream> files,
			OutputStream outstr) throws IOException {
		ZipOutputStream zipfile = new ZipOutputStream(outstr);
		Iterator<String> i = files.keySet().iterator();
		String fileName = null;
		ZipEntry zipentry = null;
		while (i.hasNext()) {
			fileName = i.next();
			zipentry = new ZipEntry(fileName);
			zipfile.putNextEntry(zipentry);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ServerDBData.copyLarge(files.get(fileName), bos);
			zipfile.write(bos.toByteArray());
		}
		zipfile.close();
	}

	public static void main(String[] args) throws IOException {
		ServerDBData data = new ServerDBData("ggggg");
		data.appendInputStream(new StringReader("jahskhaskdhsdkhskdfhskdjfhks"));
		File file = new File("test.gz");
		System.out.println(file.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(file);
		Map<String, InputStream> files = new HashMap<String, InputStream>();

		files.put(data.getTbl_name() + ".sql", data.getInputStream());
		createZip(files, fos);
		fos.flush();
		fos.close();
	}
}
