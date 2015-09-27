package com.docflow.server.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.input.ReaderInputStream;

import com.docflow.shared.DBData;

public class ServerDBData extends DBData {

	private Map<String, GetValue> fieldMapping = new HashMap<String, GetValue>();

	private abstract class GetValue {
		String fieldname;
		int index;

		public GetValue(String fieldname, int index) {
			this.fieldname = fieldname;
			this.index = index;
		}

		abstract void setValue(ResultSet rs, PreparedStatement stmt)
				throws Exception;
	}

	@Override
	public void setField_names(String field_names) {
		super.setField_names(field_names);
		String[] splitted = field_names.split(",");
		int i = 1;
		for (String field : splitted) {
			if (field.trim().isEmpty())
				continue;
			String[] s = field.trim().split(" ");
			String field_name = s[0].trim();
			String field_type = s[1].trim();
			GetValue getValue = null;

			if (field_type.equalsIgnoreCase("text"))
				getValue = new GetValue(field_name, i++) {

					@Override
					void setValue(ResultSet rs, PreparedStatement stmt)
							throws Exception {
						stmt.setString(this.index, rs.getString(this.fieldname));
					}
				};
			else if (field_type.equalsIgnoreCase("blob"))
				getValue = new GetValue(field_name, i++) {

					@Override
					void setValue(ResultSet rs, PreparedStatement stmt)
							throws Exception {
						stmt.setBytes(this.index, rs.getBytes(this.fieldname));
					}
				};
			else
				getValue = new GetValue(field_name, i++) {

					@Override
					void setValue(ResultSet rs, PreparedStatement stmt)
							throws Exception {
						Object o = rs.getObject(this.fieldname);
						if (o != null)
							stmt.setDouble(this.index,
									Double.valueOf(o.toString()));
						else
							stmt.setNull(this.index, java.sql.Types.NUMERIC);
					}
				};
			fieldMapping.put(field_name, getValue);

		}
	}

	private static final int BUFFER_SIZE = 1024 * 4;
	private static final int BATCH_SIZE = 1000;
	private Map<String, String> sql_scripts;
	private ByteArrayOutputStream bos;
	private String insert_stmt;
	private int current_count;
	private PreparedStatement stmt;

	public ServerDBData(String tbl_name, String tbl_caption,
			String field_names, String primary_keys) {
		super();
		this.tbl_name = tbl_name;
		this.tbl_caption = tbl_caption;
		setField_names(field_names);
		this.primary_keys = primary_keys;
		insert_stmt = "";
		for (int i = 0; i < fieldMapping.size(); i++) {
			if (!insert_stmt.isEmpty())
				insert_stmt += ",";
			insert_stmt += "?";
		}
		insert_stmt = "insert into tmp_" + tbl_name + " values (" + insert_stmt
				+ ")";
	}

	public ServerDBData(String tbl_name) {
		super();
		this.tbl_name = tbl_name;
	}

	public void addScript(String db_name, String sql_script) {
		if (sql_scripts == null)
			sql_scripts = new HashMap<String, String>();
		sql_scripts.put(db_name, sql_script);
	}

	public Map<String, String> getSql_scripts() {
		return sql_scripts;
	}

	public DBData cloneObject() {
		DBData result = new DBData();
		result.setField_names(field_names);
		result.setPrimary_keys(primary_keys);
		result.setTbl_caption(tbl_caption);
		result.setTbl_name(tbl_name);
		return result;
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public void createStetement(Connection con) throws Exception {
		Statement stmt = con.createStatement();
		stmt.execute("create table tmp_" + tbl_name + " (" + field_names + ")");
		stmt.execute("create table " + tbl_name + " (" + field_names
				+ ",PRIMARY KEY ( " + primary_keys + " ))");
		stmt.close();
		this.stmt = con.prepareStatement(insert_stmt);
	}

	public void addToBatch(ResultSet rs) throws Exception {
		for (GetValue v : fieldMapping.values()) {
			v.setValue(rs, stmt);
		}
		current_count++;
		stmt.addBatch();
		if (current_count > BATCH_SIZE) {
			stmt.executeBatch();
			current_count = 0;
		}
	}

	public void doFinish() throws Exception {
		if (current_count > 0) {
			stmt.executeBatch();
		}
		stmt.close();
		Statement stmt = this.stmt.getConnection().createStatement();
		stmt.execute("insert into " + tbl_name + " select * from  tmp_"
				+ tbl_name);
		stmt.execute("drop table tmp_" + tbl_name);
		stmt.close();
	}

	public void appendInputStream(Reader r) throws IOException {
		if (bos == null)
			bos = new ByteArrayOutputStream();
		else
			bos.write(new String(new char[] { (char) 10 }).getBytes());
		copyLarge(new ReaderInputStream(r), bos);
		r.close();
	}

	public void addZipEntry(ZipOutputStream zipfile) throws IOException {
		String fileName = tbl_name;
		bos.flush();
		ZipEntry zipentry = new ZipEntry(fileName);
		zipfile.putNextEntry(zipentry);
		byte[] by = bos.toByteArray();
		zipfile.write(by);
		bos.close();
	}

	public InputStream getInputStream() throws IOException {
		byte[] by = bos.toByteArray();
		bos.close();
		return new ByteArrayInputStream(by);
	}

	public void destroy() {
		try {
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
