package com.docflow.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.docflow.shared.docflow.DocumentShort;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.datasource.DataSourceManager;
import com.isomorphic.sql.SQLDataSource;

public class DocumentShortDSDMI {

	@SuppressWarnings({ "rawtypes" })
	public DSResponse fetch(DSRequest dsRequest) throws Exception {
		DataSource ds = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<DocumentShort> result = new ArrayList<DocumentShort>();
		String sqlT = "select " + "%s"
				+ " from document_v d where d.languageid=?  and (";
		Integer documentid = getDocumentID(dsRequest);
		if (documentid != null)
			sqlT += " (d.id = " + documentid.intValue() + ") or ";
		sqlT += " (d.doc_date>=? and d.doc_date<=? and 1=1 and ";
		Object criterias = dsRequest.getFieldValue("criterias");

		ArrayList c = (ArrayList) criterias;
		String criteria = "";

		for (Object key : c) {
			if (criteria.length() > 0)
				criteria += " and ";
			criteria += key;
		}
		// +
		sqlT += criteria + "))";
		long startdate = Long.parseLong(dsRequest.getFieldValue("startdate")
				.toString());
		long enddate = Long.parseLong(dsRequest.getFieldValue("enddate")
				.toString());
		int languageId = Integer.parseInt(dsRequest.getFieldValue("languageId")
				.toString());

		long startRow = dsRequest.getStartRow();
		long endRow = dsRequest.getEndRow();

		try {
			ds = DataSourceManager.get("DocumentShortDS");
			SQLDataSource sqlDS = (SQLDataSource) ds;

			con = sqlDS.getConnection();

			int totalRows = getNumberOfRows(String.format(sqlT, "count(1)"),
					con, startdate, enddate, languageId);
			long endr = endRow;
			if (endr == -1)
				endr = 100000000000000000L;

			long offset = Math.min(startRow, endr);
			long limit = Math.max(startRow, endr) - offset;
			sqlT += "  order by d.doc_date desc LIMIT %d OFFSET %d";
			String sql = String.format(sqlT,
					MDBConnection.DOCUMENTSHORT_FIELDS, limit, offset);
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setTimestamp(2, new Timestamp(MDBConnection.trim(startdate)));
			stmt.setTimestamp(3, new Timestamp(MDBConnection.trim(enddate)));
			// stmt.setTimestamp(5, date);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result.add(DBMapping.getDocumentShort(rs, null));
			}
			DSResponse dsResponse = new DSResponse();
			dsResponse.setTotalRows(totalRows);
			dsResponse.setStartRow(startRow);

			endRow = Math.min(endRow, totalRows);
			dsResponse.setEndRow(endRow);
			if (documentid != null)
				Collections.sort(result, new Comparator<DocumentShort>() {
					@Override
					public int compare(DocumentShort o1, DocumentShort o2) {
						return new Long(o2.getDoc_date()).compareTo(o1
								.getDoc_date());
					}
				});

			// just return the List of matching beans
			dsResponse.setData(result);

			return dsResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DataSourceManager.free(ds);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	private Integer getDocumentID(DSRequest dsRequest) {
		try {
			return Integer.parseInt(dsRequest.getFieldValue("documentid")
					.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private int getNumberOfRows(String sql, Connection con, long startdate,
			long enddate, int languageId) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int result = 0;

		try {

			stmt = con.prepareStatement(sql);
			stmt.setInt(1, languageId);
			stmt.setTimestamp(2, new Timestamp(MDBConnection.trim(startdate)));
			stmt.setTimestamp(3, new Timestamp(MDBConnection.trim(enddate)));
			rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				stmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

		}

		return result;
	}

	// public DocumentShort add(DocumentLong record) throws Exception {
	// DataSource ds = null;
	// Connection con = null;
	// try {
	// ds = DataSourceManager.get("DocumentShortDS");
	// SQLDataSource sqlDS = (SQLDataSource) ds;
	// con = sqlDS.getConnection();
	// MDBConnection.saveDocument(record, con);
	// DocumentShort ret=MDBConnection.getDoc(record.getId(), record.get,
	// connection)
	// return record;
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// try {
	// con.close();
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// try {
	// DataSourceManager.free(ds);
	// } catch (Exception e2) {
	// // TODO: handle exception
	// }
	// }
	//
	// }

}
