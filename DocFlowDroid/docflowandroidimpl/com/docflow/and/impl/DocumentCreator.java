package com.docflow.and.impl;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.common.shared.ClSelectionItem;
import com.common.shared.usermanager.TransfarableUser;
import com.docflow.and.impl.db.DBConnectionAnd;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.common.DocFormProcessor;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.IPermitionChecker;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.docflowdroid.DocFlow;
import com.google.gwt.xml.client.XMLParser;

public class DocumentCreator {
	public DocTypeMapping getValueMap(int custId, int doctypeid,
			int languageId, int userId) throws Exception {
		Connection conn = null;
		try {
			conn = DBConnectionAnd.getExportedDB();
			DocType dt = MDBConnection.getDocType(conn, doctypeid, languageId);
			if (!dt.isApplied_customer())
				custId = 0;

			HashMap<String, String> values = custId > 0 ? MDBConnection
					.getValueMap(conn, dt.getCust_selectfields().split(","),
							dt.getCust_sql(), custId)
					: new HashMap<String, String>();

			DocTypeMapping result = new DocTypeMapping();

			final TransfarableUser tu = DocFlow.user_obj.getUser();

			CustomerShort cust = custId > 0 ? MDBConnection.getCustomerShort(
					custId, conn) : null;

			if (custId > 0 && cust == null)
				throw new Exception("Cannot find customer by id:" + custId);
			if (custId > 0 && !DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS")) {
				if (!((tu.getRegionid() == -1 || cust.getRegionid() == tu
						.getRegionid()) && (tu.getSubregionid() == -1 || cust
						.getSubregionid() == tu.getSubregionid())))
					throw new Exception(
							"You do not have permitions to proceed this customer:"
									+ custId);
			}

			result.setCustomerShort(cust);
			result.setDocType(dt);
			result.setValues(values);

			int street_id = 0;
			long zone = 0;

			if (cust != null) {
				street_id = cust.getStreetid();
				zone = cust.getZone();
			} else {
				Integer region_id = null;
				Integer subregion_id = null;
				if (tu.getRegionid() != -1) {
					region_id = tu.getRegionid();
				}
				if (tu.getSubregionid() != -1) {
					subregion_id = tu.getSubregionid();
				}
				if (region_id == null && subregion_id != null) {
					ClSelectionItem so = MDBConnection.getClSelectionItem(conn,
							ClSelection.T_SUBREGION, subregion_id);
					region_id = (int) so.getParentId();
				}
				if (region_id == null) {
					ClSelectionItem item = MDBConnection.getClSelectionItem(
							conn, ClSelection.T_REGION);
					if (item != null) {
						region_id = (int) item.getId();
					}
				}

				if (region_id != null && subregion_id == null) {

					subregion_id = getFirstItem(conn, ClSelection.T_SUBREGION,
							region_id.longValue()).intValue();
				}

				long cityid = getFirstItem(conn, ClSelection.T_CITY,
						subregion_id.longValue()).intValue();
				street_id = getFirstItem(conn, ClSelection.T_STREET, cityid)
						.intValue();
				zone = getFirstItem(conn, ClSelection.T_ZONES,
						subregion_id.longValue()).intValue();

			}

			result.setDocument(createDocumentFromCustomer(conn, custId,
					street_id, zone + ""));

			if (result.getDocument() != null) {
				if (cust != null)
					result.getDocument().setCustomer_name(cust.getCusname());
				result.getDocument().setDoc_type_id(doctypeid);
				DocumentLong doc = result.getDocument();
				putValue(values, "streetid", doc.getStreet_id());
				putValue(values, "streenname", doc.getStreenname());
				putValue(values, "cityId", doc.getCityid());
				putValue(values, "cityname", doc.getCityname());
				putValue(values, "subregionId", doc.getSubregionid());
				putValue(values, "subregionname", doc.getSubregionname());
				putValue(values, "regionId", doc.getRegionid());
				putValue(values, "regionname", doc.getRegionname());
				putValue(values, "cusname", doc.getCustomer_name());
				putValue(values, "zone", doc.getCzona());

			}

			setupDocData(conn, custId, dt, values, result, tu, null);

			return result;

		} finally {
			DBConnectionAnd.closeAll(conn);
		}
	}

	private void setupDocData(Connection conn, int custId, DocType dt,
			HashMap<String, String> values, DocTypeMapping result,
			final TransfarableUser tu, HashMap<String, String> displayValues)
			throws Exception {
		IPermitionChecker permitionChecker = new IPermitionChecker() {

			@Override
			public boolean hasPermition(int permitionId) {
				return DocFlow.hasPermition(permitionId);
			}

			@Override
			public boolean hasPermition(String permition_name) {
				return DocFlow.hasPermition(permition_name);
			}
		};

		FormDefinition def = new FormDefinition();
		def.setXml(XMLParser.parse(dt.getDoc_template()));

		DocFormProcessor pr = new DocFormProcessor(def, values, null,
				permitionChecker, tu);
		if (displayValues != null && !displayValues.isEmpty()) {
			HashMap<String, FieldDefinition> fieldsMap = pr.getFieldsMap();
			Set<String> keys = fieldsMap.keySet();
			for (String key : keys) {
				if (!fieldsMap.get(key).displayValueApplied())
					displayValues.remove(key);
			}
		}
		result.setSelections(getListTypesForDocument(conn, pr.getListSqls(),
				custId));
	}

	private void putValue(HashMap<String, String> values, String key,
			Object value) {
		if (value != null)
			values.put(key, value.toString().trim());
	}

	public DocumentLong createDocumentFromCustomer(Connection conn,
			int customerId, int street, String scZona) {
		DocumentLong doc = new DocumentLong();
		doc.setCust_id(customerId);
		doc.setTransaction_date(System.currentTimeMillis());
		doc.setDoc_date(System.currentTimeMillis());

		try {
			ClSelectionItem so = getValue(conn, ClSelection.T_STREET, street);
			doc.setStreet_id(street);
			doc.setStreenname(so.getValue());

			so = getValue(conn, ClSelection.T_CITY, so.getParentId());
			doc.setCityid((int) so.getId());
			doc.setCityname(so.getValue());

			so = getValue(conn, ClSelection.T_SUBREGION, so.getParentId());
			doc.setSubregionid((int) so.getId());
			doc.setSubregionname(so.getValue());

			so = getValue(conn, ClSelection.T_REGION, so.getParentId());
			doc.setRegionid((int) so.getId());
			doc.setRegionname(so.getValue());

		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			doc.setCzona(Long.parseLong(scZona));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return doc;
	}

	private ClSelectionItem getValue(Connection conn, int type, long id)
			throws Exception {
		ClSelectionItem so = MDBConnection.getClSelectionItem(conn, type, id);
		if (so == null) {
			so = new ClSelectionItem();
			so.setId(-1);
			so.setValue("Unknown");
			so.setParentId(-1);
		}
		return so;
	}

	private Long getFirstItem(Connection conn, int type, long parent_id)
			throws Exception {
		ClSelectionItem item = MDBConnection.getClSelectionItem(conn, type,
				(long) parent_id, null);
		if (item != null) {
			return item.getId();
		}
		return -1L;
	}

	public HashMap<String, ArrayList<ClSelectionItem>> getListTypesForDocument(
			Connection conn, HashMap<String, String> listSqls, long customer_id)
			throws Exception {
		Connection myconn = null;

		try {

			myconn = conn == null ? DBConnectionAnd.getExportedDB() : conn;
			HashMap<String, ArrayList<ClSelectionItem>> result = new HashMap<String, ArrayList<ClSelectionItem>>();
			Set<String> keSet = listSqls.keySet();
			HashMap<String, String> listSql = new HashMap<String, String>();
			for (String key : keSet) {
				String val = listSqls.get(key);
				if (!val.toLowerCase().contains("select".toLowerCase())) {
					try {

						String[] ids = val.split("_");
						ArrayList<ClSelectionItem> values = null;
						if (ids.length == 1) {
							int longVal = Integer.parseInt(val);
							values = MDBConnection.getClSelectionItems(myconn,
									longVal);

						}
						if (ids.length == 3) {
							int selfType = Integer.parseInt(ids[0]);
							long parent_id = Long.parseLong(ids[2]);
							values = MDBConnection.getClSelectionItems(myconn,
									selfType, parent_id);
						}
						if (values == null)
							continue;
						result.put(key, values);
						continue;
					} catch (Exception e) {

					}
				}
				listSql.put(key, val);
			}

			HashMap<String, ArrayList<ClSelectionItem>> sqlItems = MDBConnection
					.getValueList(myconn, listSql, (int) customer_id);
			keSet = sqlItems.keySet();
			for (String key : keSet) {
				result.put(key, sqlItems.get(key));
			}
			return result;
		} finally {
			if (conn == null)
				DBConnectionAnd.closeAll(myconn);
		}
	}

	public DocTypeMapping getDocumentWithMapping(int docid, int languageId,
			int userId) throws Exception {
		Connection myconnE = null;
		Connection myconnD = null;

		try {
			myconnE = DBConnectionAnd.getExportedDB();
			myconnD = DBConnectionAnd.getDocFlowDB();
			DocumentLong doc = getDocument(myconnE, myconnD, docid, languageId);
			DocTypeMapping mapping = new DocTypeMapping();
			mapping.setDocument(doc);
			mapping.setCustomerShort(doc.getCustomerShort());
			mapping.setDocType(MDBConnection.getDocType(myconnE,
					doc.getDoc_type_id(), languageId));
			mapping.setFiles(MDBConnection.getFilesForDocument(myconnD, docid));
			HashMap<String, String> displayValues = new HashMap<String, String>();
			HashMap<String, String> values = getData(doc.getContent_xml(),
					displayValues);

			final TransfarableUser tu = DocFlow.user_obj.getUser();

			setupDocData(myconnE, doc.getCust_id(), mapping.getDocType(),
					values, mapping, tu, displayValues);
			if (!displayValues.isEmpty())
				mapping.setDisplayValues(displayValues);
			mapping.setValues(values);
			return mapping;
		} finally {
			DBConnectionAnd.closeAll(myconnE, myconnD);
		}

	}

	public static HashMap<String, String> getData(String xml,
			HashMap<String, String> displayValues) throws Exception {
		HashMap<String, String> data = new HashMap<String, String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xml
				.getBytes("UTF-8")));
		Node rootElem = doc.getChildNodes().item(0);
		NodeList nodeList = rootElem.getChildNodes();

		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				Element el = (Element) n;
				String key = el.getAttribute("key");
				String value = el.getAttribute("value");
				String text = el.getAttribute("text");
				if (displayValues != null && text != null)
					displayValues.put(key, text);
				if (value != null)
					data.put(key, value);

			}
		}
		return data;
	}

	public DocumentLong getDocument(Connection connE, Connection connD,
			int docid, int languageId) throws Exception {
		Connection myconnE = null;
		Connection myconnD = null;

		try {
			myconnE = connE == null ? DBConnectionAnd.getExportedDB() : connE;
			myconnD = connD == null ? DBConnectionAnd.getExportedDB() : connD;

			DocumentLong doc = MDBConnection.getDocLong(myconnD, docid,
					languageId);
			if (doc != null && doc.getCust_id() > 0) {
				doc.setCustomerShort(MDBConnection.getCustomerShort(
						doc.getCust_id(), myconnE));
			}
			return doc;
		} finally {
			if (connE == null)
				DBConnectionAnd.closeAll(myconnE, myconnD);
		}
	}
}
