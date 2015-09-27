package com.docactivator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import nanoxml.XMLElement;

import com.common.db.DBConnection;
import com.docflow.common.server.db.MDBConnection;
import com.docflow.shared.docflow.CustomerShort;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;

public class DBOperations {

	public static final int FT_STRING = 1; // java.textfield.string
	public static final int FT_INTEGER = 2; // java.textfield.int
	public static final int FT_DOUBLE = 3; // java.textfield.double
	public static final int FT_BOOLEAN = 4; // java.textfield.boolean
	public static final int FT_DATE = 5; // java.date
	public static final int FT_BIGINT = 6; // java.textfield.int or
	public static final int FT_FLOAT = 7; // java.textfield.int or
	public static final int FT_NUMBER = 8; // java.textfield.int or

	private static TreeMap<Integer, DocType> dt = null;

	private static DocType getDocType(int docTypeId) throws Exception {
		if (dt == null) {
			ArrayList<DocType> dtList = MDBConnection.getDocTypes(1);
			dt = new TreeMap<Integer, DocType>();
			for (DocType docType : dtList) {
				dt.put(docType.getId(), docType);
			}
		}
		return dt.get(docTypeId);
	}

	@SuppressWarnings("rawtypes")
	private static void analizeXML(TreeMap<String, String> strings,
			XMLElement el) {
		// System.out.println(el);
		Enumeration attrNames = el.enumerateAttributeNames();
		while (attrNames.hasMoreElements()) {
			Object object = (Object) attrNames.nextElement();
			String keyName = object.toString().toLowerCase();
			if (keyName.equals("GROUPTITLE".toLowerCase())
					|| keyName.equals("FIELDCAPTION".toLowerCase())) {
				String value = el.getStringAttribute(object.toString());
				if (value != null && value.trim().length() != 0) {
					strings.put(value, value);

				}
			}

		}
		for (Object obj : el.getChildren()) {
			analizeXML(strings, (XMLElement) obj);
		}

	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private static void putXML(TreeMap<String, Integer> captions, XMLElement el) {
		// System.out.println(el);
		Enumeration attrNames = el.enumerateAttributeNames();
		while (attrNames.hasMoreElements()) {
			Object object = (Object) attrNames.nextElement();
			String keyName = object.toString().toLowerCase();
			if (keyName.equals("GROUPTITLE".toLowerCase())
					|| keyName.equals("FIELDCAPTION".toLowerCase())) {
				String value = el.getStringAttribute(object.toString());
				if (value != null && value.trim().length() != 0) {
					value = value.trim();
					Integer id = captions.get(value);
					if (id == null)
						continue;
					el.setAttribute("fieldCaptionId", id.toString());
				}
			}

		}
		for (Object obj : el.getChildren()) {
			putXML(captions, (XMLElement) obj);
		}

	}

	public static void main(String[] args) {

		boolean k = true;
		if (k)
			return;
		try {
			TreeMap<String, String> strings = new TreeMap<String, String>();
			getDocType(0);
			Set<Integer> keys = dt.keySet();
			for (Integer key : keys) {
				DocType _dt = MDBConnection.getDocType(key, 1);
				dt.put(key, _dt);
				if (_dt.getDoc_template() == null
						|| _dt.getDoc_template().trim().length() == 0)
					continue;
				try {
					XMLElement el = new XMLElement();
					el.parseString(_dt.getDoc_template());
					analizeXML(strings, el);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			int captionid = 44;
			TreeMap<String, Integer> mapCations = new TreeMap<String, Integer>();

			for (String str : strings.keySet()) {
				mapCations.put(str, captionid);
				for (int i = 0; i < 3; i++) {
					// String insertCommand =
					// "insert into captions values(%d,%d,'%s');";
					// System.out.println(String.format(insertCommand,
					// captionid,
					// (i + 1), str.trim()));

				}
				captionid++;
			}
			Set<String> stKeys = mapCations.keySet();
			// for (String stKey : stKeys) {
			// String k=mapCations.get(stKey).toString()+" ";
			// if(k.length()<4)
			// k+=" ";
			// System.out.println(k + stKey.trim());
			// }
			for (Integer key : keys) {
				DocType _dt = dt.get(key);
				if (_dt.getDoc_template() == null
						|| _dt.getDoc_template().trim().length() == 0)
					continue;
				try {
					String str = _dt.getDoc_template().trim();

					for (String stKey : stKeys) {
						if (captionid == -1) {
							System.out.println("Cannot find key for ==="
									+ stKey);
							continue;
						}
						String fieldCaption = "fieldCaption=\"%s\"";
						String fieldCaptionTrimed = String.format(fieldCaption,
								stKey.trim());
						fieldCaption = String.format(fieldCaption, stKey);
						String groupCaption = "groupTitle=\"%s\"";
						String groupCaptionTrimed = String.format(groupCaption,
								stKey.trim());
						groupCaption = String.format(groupCaption, stKey);

						captionid = Captions.getCaptionID(stKey);

						String fieldCaptionid = "fieldCaptionId=\"" + captionid
								+ "\"";
						str = str.replaceAll(fieldCaption, fieldCaptionTrimed
								+ " " + fieldCaptionid);
						str = str.replaceAll(groupCaption, groupCaptionTrimed
								+ " " + fieldCaptionid);
					}
					str = str.replaceAll("'", "''");
					System.out.println("update doc_type set xmlold='" + str
							+ "' where id=" + key + ";");

					XMLElement el = new XMLElement();
					el.parseString(str);
					// putXML(mapCations, el);
					// StringWriter st = new StringWriter();
					//
					// el.write(st);
					// System.out.println(st.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public synchronized static void operate(int count) throws Exception {
		ArrayList<DocumentLong> docList = null;
		int rcount = 0;
		try {

			docList = MDBConnection.getApprovedDocList(count);
			rcount = docList.size();
			for (DocumentLong doc : docList) {

				String sValue = "";
				try {
					canOperateWithDoc(doc.getId());
					XMLElement el = new XMLElement();
					el.parseString(doc.getContent_xml());
					TreeMap<String, String> vauemap = new TreeMap<String, String>();
					TreeMap<String, String> vaueTextmap = new TreeMap<String, String>();
					Vector childs = el.getChildren();
					for (Object o : childs) {
						XMLElement ch = (XMLElement) o;
						Object key = ch.getAttribute("key");
						Object value = ch.getAttribute("value");
						Object text = ch.getAttribute("text");
						sValue += "Key = " + key + " value=" + value + "\n";

						vauemap.put(key.toString(), value.toString());
						if (text != null)
							vaueTextmap.put("#" + key.toString(),
									text.toString());
					}
					System.out.println(sValue);
					Integer custid = operateWithDocument(doc, vauemap,
							vaueTextmap);
					CustomerShort cs = null;
					if (doc.getCust_id() == 0 && custid != null
							&& custid.intValue() > 0) {
						cs = MDBConnection.getCustomerShort(custid);
					}

					setDocumentStateAndCustomer(doc, cs);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					try {
						setDocumentError(doc.getId(), doc.getError_status(),
								sValue + sw.toString());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		} finally {
			dt = null;
			System.out.println("countttt = rcount" + rcount);
			if (rcount == 0) {
				Thread.sleep(1000);
			}
		}

	}

	private static void canOperateWithDoc(int docid) throws Exception {
		String sql = String.format(
				"insert into document_read_list(docid) values(%d)", docid);
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBConnection.getConnection("DocFlow");
			con.setAutoCommit(true);
			stmt = con.createStatement();
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			throw new Exception("Error writing doc" + docid + " "
					+ e.getMessage());

		} finally {
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
		}

	}

	private static void setDocumentStateAndCustomer(DocumentLong doc,
			CustomerShort cs) throws Exception {
		String sql = "update documents set error=null, doc_status_id=? ";
		if (cs != null) {
			sql += ", cust_id=?, street_id = ?, streenname=?, cityid=?, cityname=?, subregionid=?, subregionname=?, regionid=?, regionname=?, czona=?,customer_name=? ";
		}
		sql += " where id=?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, doc.getApplied_status());
			if (cs != null) {
				stmt.setInt(2, cs.getCusid());
				stmt.setInt(3, cs.getStreetid());
				stmt.setString(4, cs.getStreetname());
				stmt.setInt(5, cs.getCityid());
				stmt.setString(6, cs.getCityname());
				stmt.setInt(7, cs.getSubregionid());
				stmt.setString(8, cs.getRaion());
				stmt.setInt(9, cs.getRegionid());
				stmt.setString(10, cs.getRegion());
				stmt.setLong(11, cs.getZone());
				stmt.setString(12, cs.getCusname());
			}
			stmt.setInt(cs == null ? 2 : 13, doc.getId());
			stmt.executeUpdate();

			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.getMessage());
		} finally {
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
		}

	}

	private static void setDocumentError(int docId, int error_status,
			String error) throws Exception {
		String sql = "update documents set doc_status_id=?, error=? ";

		sql += " where id=?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = DBConnection.getConnection("DocFlow");
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, error_status);
			stmt.setString(2, error);
			stmt.setInt(3, docId);
			stmt.executeUpdate();

			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.getMessage());
		} finally {
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
		}

	}

	private static Integer operateWithDocument(DocumentLong doc,
			TreeMap<String, String> vauemap, TreeMap<String, String> vaueTextmap)
			throws Exception {
		Integer result = null;
		DocType dt = getDocType(doc.getDoc_type_id());
		String[] st = dt.getRealdoctypeid().split("::");
		String functionname = st[0];
		String[] params = st[1].split(";");
		String execSql = "select " + functionname + "(";
		for (int i = 0; i < params.length; i++) {
			if (i > 0)
				execSql += ",";
			execSql += "?";
		}
		execSql += ")";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = execSql;
		try {
			con = DBConnection.getConnection("Gass");
			stmt = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				try {
					setDBValue(doc, vauemap, vaueTextmap, params[i], i + 1,
							stmt);
				} catch (Exception e) {
					System.out.println(params[i]);
					e.printStackTrace();
					throw e;
				}
			}

			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			con.commit();
			return result;

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
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
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	private static void setDBValue(DocumentShort doc,
			TreeMap<String, String> vauemap,
			TreeMap<String, String> vaueTextmap, String paramName, int index,
			PreparedStatement stmt) throws Exception {
		String[] paramDef = paramName.split(":");
		String keyName = paramDef[0];
		int ptype = FT_STRING;
		if (paramDef.length > 1) {
			try {
				ptype = Integer.parseInt(paramDef[1]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		if (keyName.startsWith("@"))
			setFieldValueFromDoc(doc, keyName, index, stmt, ptype);
		else {
			if (keyName.startsWith("$")) {
				String value = "";
				if (keyName.length() > 1)
					value = keyName.substring(1);
				setFieldValue(value, index, ptype, stmt, paramName);
			} else {
				String value = vauemap.get(keyName);
				if (keyName.startsWith("#"))
					value = vaueTextmap.get(keyName);
				setFieldValue(value, index, ptype, stmt, paramName);
			}
		}

	}

	private static void setFieldValue(String value, int index, int ptype,
			PreparedStatement stmt, String paramname) throws Exception {
		if (value == null) {

			int sqlType = Types.VARCHAR;
			switch (Math.abs(ptype)) {
			case FT_STRING:
				sqlType = Types.VARCHAR;
				break;
			case FT_DOUBLE:
				sqlType = Types.DOUBLE;
				break;
			case FT_FLOAT:
				sqlType = Types.FLOAT;
				break;
			case FT_INTEGER:
				sqlType = Types.INTEGER;
				break;
			case FT_DATE:
				sqlType = Types.DATE;
				break;
			case FT_BOOLEAN:
				sqlType = Types.BOOLEAN;
				break;
			case FT_BIGINT:
				sqlType = Types.BIGINT;
				break;
			case FT_NUMBER:
				sqlType = Types.NUMERIC;
				break;
			default:
				sqlType = Types.VARCHAR;
				break;
			}

			stmt.setNull(index, sqlType);
			return;
		}
		Number n = null;
		if (Math.abs(ptype) == FT_DOUBLE || Math.abs(ptype) == FT_BIGINT
				|| Math.abs(ptype) == FT_FLOAT || Math.abs(ptype) == FT_INTEGER
				|| Math.abs(ptype) == FT_NUMBER
				|| Math.abs(ptype) == FT_BOOLEAN) {
			try {
				if (value.trim().length() == 0) {
					setFieldValue(null, index, ptype, stmt, paramname);
					return;
				}
				n = Double.parseDouble(value);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (ptype < 0)
				n = n.doubleValue() * -1;
		}

		switch (Math.abs(ptype)) {
		case FT_STRING:
			stmt.setString(index, value);
			break;
		case FT_DOUBLE:
			stmt.setDouble(index, n.doubleValue());
			break;
		case FT_NUMBER:
			stmt.setObject(index, n.doubleValue(), Types.NUMERIC);
			break;
		case FT_FLOAT:
			stmt.setFloat(index, n.floatValue());
			break;
		case FT_INTEGER:
			stmt.setInt(index, n.intValue());
			break;
		case FT_DATE:
			Timestamp t = new Timestamp(Long.parseLong(value));
			if (ptype < 0) {
				String sDate = t.toString();
				stmt.setString(index, sDate);
				break;
			} else {
				stmt.setTimestamp(index, t);
				break;
			}
		case FT_BOOLEAN:
			stmt.setBoolean(index, n.intValue() < 1 ? false : true);
			break;
		case FT_BIGINT:
			stmt.setLong(index, n.longValue());
			break;
		default:
			stmt.setString(index, value);
			break;
		}
	}

	private static void setFieldValueFromDoc(DocumentShort doc, String keyName,
			int index, PreparedStatement stmt, int ptype) throws Exception {
		keyName = keyName.toLowerCase();
		if (keyName.equals("@docnum"))
			setFieldValue(doc.getCancelary_nom(), index, ptype, stmt, keyName);
		if (keyName.equals("@docdate"))
			setFieldValue(doc.getDoc_date() + "", index, ptype, stmt, keyName);
		if (keyName.equals("@user"))
			setFieldValue(doc.getUser_id() + "", index, ptype, stmt, keyName);
		if (keyName.equals("@username"))
			setFieldValue(doc.getUser_name() + "", index, ptype, stmt, keyName);
		if (keyName.equals("@cusid"))
			setFieldValue(doc.getCust_id() + "", index, ptype, stmt, keyName);
		if (keyName.equals("@delay"))
			setFieldValue(doc.getDelaystatus() + "", index, ptype, stmt,
					keyName);
	}
}
