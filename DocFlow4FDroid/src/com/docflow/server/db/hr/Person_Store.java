package com.docflow.server.db.hr;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.isomorphic.datasource.BasicDataSource;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.servlet.ISCFileItem;

public class Person_Store {

	public Object add(DSRequest record) throws Exception {
		return addUpdate(record, true);
	}

	private Object addUpdate(DSRequest record, boolean add) throws Exception {

		InputStream stream = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = (Connection) ((BasicDataSource) record.getDataSource())
					.getTransactionObject(record);
			ISCFileItem file = record.getUploadedFile("person_picture");
			byte arr[] = null;
			if (file != null) {
				stream = file.getInputStream();
				arr = new byte[stream.available()];
				stream.read(arr);
			}
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("select addPerson(?,?,?,?)");
			Integer id = getIntValue(record, "person_id");
			if (id == null)
				stmt.setNull(1, Types.INTEGER);
			else
				stmt.setInt(1, id);
			String name = getStringValue(record, "person_last_name");
			if (name == null)
				stmt.setNull(2, Types.VARCHAR);
			else
				stmt.setString(2, name);

			name = getStringValue(record, "person_first_name");
			if (name == null)
				stmt.setNull(3, Types.VARCHAR);
			else
				stmt.setString(3, name);

			if (arr == null)
				stmt.setNull(4, Types.VARBINARY);
			else
				stmt.setBytes(4, arr);
			rs = stmt.executeQuery();

			if (rs.next()) {
				id = rs.getInt(1);
			}
			conn.commit();

			return id;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			e.printStackTrace();
			throw new Exception(
					"áƒ¨áƒ”áƒªáƒ“áƒ�áƒ›áƒ� áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜áƒ¡ áƒ�áƒ¢áƒ•áƒ˜áƒ áƒ—áƒ•áƒ˜áƒ¡áƒ�áƒ¡ : "
							+ e.toString());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

		}

	}

	private Integer getIntValue(DSRequest record, String fieldName) {
		try {
			return Integer.parseInt(getStringValue(record, fieldName));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private String getStringValue(DSRequest record, String fieldName) {
		try {
			return record.getValues().get(fieldName).toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public Object update(DSRequest record) throws Exception {
		return addUpdate(record, false);
	}

}