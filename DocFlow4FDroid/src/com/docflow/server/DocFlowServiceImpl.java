package com.docflow.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.common.db.DBConnection;
import com.common.db.UserManager;
import com.common.shared.ClSelectionItem;
import com.common.shared.DSFieldOptions;
import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.common.shared.ds.DsField;
import com.common.shared.map.GisLayer;
import com.common.shared.map.GisLayerFilter;
import com.common.shared.map.GisLayerOptions;
import com.common.shared.map.GisMap;
import com.common.shared.map.GisMapProperties;
import com.common.shared.model.UMObject;
import com.common.shared.usermanager.Group;
import com.common.shared.usermanager.Permission;
import com.common.shared.usermanager.SUser;
import com.common.shared.usermanager.TransfarableUser;
import com.common.shared.usermanager.User;
import com.docflow.client.DocFlowService;
import com.docflow.server.db.MDBConnection;
import com.docflow.server.db.map.BuildingDMI;
import com.docflow.server.db.map.MakeDBTask;
import com.docflow.server.export.Exporter;
import com.docflow.server.xml.XMLParser;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.DbExpoResult;
import com.docflow.shared.DocFlowException;
import com.docflow.shared.DocStatusCount;
import com.docflow.shared.FieldVerifier;
import com.docflow.shared.ListSizes;
import com.docflow.shared.Meter;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.PermissionSystemMap;
import com.docflow.shared.SCSystem;
import com.docflow.shared.StatusObject;
import com.docflow.shared.UserObject;
import com.docflow.shared.common.BFUMObject;
import com.docflow.shared.common.DocFormProcessor;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.GroupsAndPermitions;
import com.docflow.shared.common.IPermitionChecker;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocTypesAndPermitions;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflow.shared.docflow.NewDocuments;
import com.docflow.shared.hr.Captions;
import com.docflow.shared.map.MakeDBProcess;
import com.docflow.shared.map.MakeDBResponce;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.isomorphic.datasource.DSField;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.util.DataTools;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DocFlowServiceImpl extends RemoteServiceServlet implements
		DocFlowService {

	private static UserManager userManager;
	public static ArrayList<GisMap> gisMaps = null;
	public static HashMap<Integer, HashMap<Integer, ArrayList<ClSelectionItem>>> statuses = null;
	private WDocFlowANDServer remote_server = null;

	public static String dsDir = null;

	public DocFlowServiceImpl() {
	}

	public DocFlowServiceImpl(WDocFlowANDServer remote_server) {
		this.remote_server = remote_server;
	}

	public static UserManager getUserManager(Connection conn) throws Exception {

		if (userManager == null)
			userManager = new UserManager("DocFlow", conn);

		return userManager;

	}

	@Override
	public void bOperationsActive(int operid, int user_id, String user_name)
			throws Exception {
		MDBConnection.bOperationsActive(operid, user_id, user_name);

	}

	@Override
	public void changeZoneToCustomers(int[] customerIds, long zone)
			throws Exception {
		MDBConnection.changeZoneToCustomers(customerIds, zone);

	}

	@Override
	public Integer closeBankByDay(int bankid, Date bankDate, int pCity)
			throws Exception {
		return MDBConnection.closeBankByDay(bankid, bankDate, pCity);

	}

	private DocumentLong createDocumentFromCustomer(int customerId,
			String sStreetId, String scZona) {
		DocumentLong doc = new DocumentLong();
		doc.setCust_id(customerId);
		doc.setTransaction_date(System.currentTimeMillis());
		doc.setDoc_date(System.currentTimeMillis());

		try {
			int street = Integer.parseInt(sStreetId);
			ClSelectionItem so = ClSCGenerator.getValue(ClSelection.T_STREET,
					street);
			doc.setStreet_id(street);
			doc.setStreenname(so.getValue());

			so = ClSCGenerator.getValue(ClSelection.T_CITY, so.getParentId());
			doc.setCityid((int) so.getId());
			doc.setCityname(so.getValue());

			so = ClSCGenerator.getValue(ClSelection.T_SUBREGION,
					so.getParentId());
			doc.setSubregionid((int) so.getId());
			doc.setSubregionname(so.getValue());

			so = ClSCGenerator.getValue(ClSelection.T_REGION, so.getParentId());
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

	@Override
	public void devicedelete(int id, int deviceid) throws Exception {
		MDBConnection.devicedelete(id, deviceid);

	}

	@Override
	public DocumentShort documentChangeState(int doc, String replica,
			int doc_state, int user_id, int languageId, long time,
			boolean anyway) throws Exception {
		// TODO Auto-generated method stub
		return MDBConnection.documentChangeState(doc, replica, doc_state,
				user_id, languageId, time, anyway);
	}

	@Override
	public DocumentShort documentCorrection(int doc, String content_xml,
			int user_id, long transaction_date, ArrayList<DocumentFile> files,
			int languageId, int docdelay) throws Exception {
		long doc_date = System.currentTimeMillis();
		return MDBConnection.documentCorrection(doc, content_xml, user_id,
				transaction_date, doc_date, files, languageId, docdelay);
	}

	@Override
	public ArrayList<Integer> documentsChangeState(ArrayList<Integer> docids,
			String replica, int doc_state, int user_id, int languageId,
			long time) throws Exception {
		MDBConnection.bashConfiremed(replica, user_id, docids.size());
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<String> bash = new ArrayList<String>();
		String s = "";
		String s1 = "";
		for (int i = 0; i < docids.size(); i++) {
			if (i % 100 == 0) {
				bash.add(s);
				s = "";
			}
			if (s.length() > 0)
				s += ",";
			s += docids.get(i);
			if (s1.length() > 0)
				s1 += ",";
			s1 += docids.get(i);

		}

		// System.err.println("aaaaaaaaaaaaaaaaa =" + s1 + " size="
		// + docids.size());
		if (s.length() > 0)
			bash.add(s);

		// ArrayList<Integer> result = null;
		// for (Integer doc : docids) {
		// try {
		// documentChangeState(doc, replica, doc_state, user_id,
		// languageId, time, false);
		// } catch (Exception e) {
		// if (result == null)
		// result = new ArrayList<Integer>();
		// result.add(doc);
		// }
		// }
		// return result;

		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "select setDocStateBash( ?,?,?,?,?,?,?)";

		// for (String b : bash) {
		// System.err.println("cccccccccccccc" + b);
		// }

		Connection connection = null;
		try {
			connection = DBConnection.getConnection("DocFlow");

			stmt = connection.prepareStatement(sql);
			int index = 2;
			stmt.setString(index++, replica);
			stmt.setInt(index++, user_id);
			stmt.setTimestamp(index++, new Timestamp(time));
			stmt.setInt(index++, languageId);
			stmt.setBoolean(index++, false);
			stmt.setInt(index++, doc_state);

			for (String b : bash) {
				stmt.setString(1, b);
				rs = stmt.executeQuery();
				if (rs.next()) {
					s = rs.getString(1);
					if (s == null)
						s = "";
					s = s.trim();
					if (!s.isEmpty()) {
						for (String str : s.split(",")) {
							try {
								result.add(Integer.valueOf(str));
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}

				}
				rs.close();
				connection.commit();
			}

		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (Exception e2) {

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
				DBConnection.freeConnection(connection);

			} catch (Exception e2) {

			}
		}
		return result;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getAddressObject(
			int type, long id) throws Exception {
		HashMap<Integer, ArrayList<ClSelectionItem>> result = new HashMap<Integer, ArrayList<ClSelectionItem>>();
		result.put(ClSelection.T_REGION,
				ClSCGenerator.getToptValues(ClSelection.T_REGION));
		ClSelectionItem value = ClSCGenerator.getValue(type, id);
		switch (type) {
		case ClSelection.T_SUBREGION:
		case ClSelection.T_REGION:
			if (value != null) {
				result.put(ClSelection.T_SUBREGION, ClSCGenerator.getDepValues(
						ClSelection.T_REGION, ClSelection.T_SUBREGION,
						type == ClSelection.T_SUBREGION ? value.getParentId()
								: value.getId()));
			}
			break;
		case ClSelection.T_CITY:
			ClSelectionItem subregion = ClSCGenerator.getValue(
					ClSelection.T_CITY, id);
			if (subregion != null && value != null) {
				result.put(ClSelection.T_SUBREGION, ClSCGenerator.getDepValues(
						ClSelection.T_REGION, ClSelection.T_SUBREGION,
						subregion.getParentId()));
				result.put(ClSelection.T_CITY, ClSCGenerator.getDepValues(
						ClSelection.T_SUBREGION, ClSelection.T_CITY,
						value.getParentId()));
			}
			break;
		default:
			break;
		}
		return result;
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getAllTopTypes()
			throws Exception {
		return ClSCGenerator.parentItems;
	}

	@Override
	public ArrayList<ClSelectionItem> getCities(long subRegionId)
			throws Exception {

		return ClSCGenerator.getCities(subRegionId);
	}

	private static HashMap<Integer, DocStatusCounts> tCounts = new HashMap<Integer, DocStatusCounts>();

	@Override
	public ArrayList<DocStatusCount> getDocCountStatus(int languageId,
			int system_id) throws Exception {
		DocStatusCounts counts = tCounts.get(languageId);
		if (counts == null || counts.getDocStatusCounts() == null
				|| counts.creatitonTime < System.currentTimeMillis()) {
			counts = new DocStatusCounts();
			counts.setDocStatusCounts(MDBConnection
					.getDocStatusCount(languageId));
			tCounts.put(languageId, counts);

		}
		ArrayList<DocStatusCount> cnts = counts.getDocStatusCounts();

		ArrayList<DocStatusCount> result = new ArrayList<DocStatusCount>();
		for (DocStatusCount docStatusCount : cnts) {
			if (docStatusCount.getSystem_id() == system_id)
				result.add(docStatusCount);
		}
		return result;
	}

	@Override
	public DocTypeWithDocList getDocListForType(int doctypeid, int languageId,
			int userid, long startdate, long enddate) throws Exception {
		DocType dt = null;
		if (doctypeid > 0)
			dt = MDBConnection.getDocType(doctypeid, languageId);

		ArrayList<DocumentShort> docs = MDBConnection.getDocList(doctypeid,
				startdate, enddate, languageId, userid);
		DocTypeWithDocList result = new DocTypeWithDocList();
		result.setDocList(docs);
		result.setDocType(dt);
		return result;
	}

	@Override
	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print) throws Exception {

		return getDocListForType(doctypeid, startdate, enddate, languageId,
				criterias, print, false, null);

	}

	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print, boolean very_short, ListSizes sizes)
			throws Exception {
		return getDocListWithDocTypeXML(doctypeid, startdate, enddate,
				languageId, criterias, print, very_short, false, sizes);
	}

	@Override
	public DocType getDocType(int id, int languageId) throws Exception {
		// TODO Auto-generated method stub
		return MDBConnection.getDocType(id, languageId);
	}

	@Override
	public ArrayList<DocType> getDocTypes(int languageId, int user_id,
			int system_id) throws Exception {
		// TODO Auto-generated method stub
		return MDBConnection.getDocTypes(languageId, user_id, system_id, null);
	}

	@Override
	public DocumentLong getDocument(int docid, int languageId) throws Exception {
		DocumentLong doc = MDBConnection.getDocLong(docid, languageId);
		if (doc != null && doc.getCust_id() > 0) {
			doc.setCustomerShort(MDBConnection.getCustomerShort(doc
					.getCust_id()));
		}
		return doc;
	}

	@Override
	public ArrayList<DocumentFile> getFilesForDocument(int docId)
			throws Exception {
		// TODO Auto-generated method stub
		return MDBConnection.getFilesForDocument(docId);
	}

	@Override
	public GroupsAndPermitions getGroupsAndPermitions(boolean user,
			int user_or_group_id) throws Exception {
		GroupsAndPermitions result = new GroupsAndPermitions();
		result.setPermitionItems(MDBConnection.getPermitionItems(user,
				user_or_group_id));
		if (user)
			result.setUser_Groups(MDBConnection
					.getUser_Groups(user_or_group_id));
		return result;
	}

	@Override
	public ArrayList<ClSelectionItem> getItemsForType(int type, int subtype,
			long parentId) throws Exception {

		return ClSCGenerator.getDepValues(type, subtype, parentId);
	}

	@Override
	public HashMap<String, ArrayList<ClSelectionItem>> getListTypesForDocument(
			HashMap<String, String> listSqls, long customer_id)
			throws Exception {
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
						values = ClSCGenerator.getToptValues(longVal);

					}
					if (ids.length == 3) {
						int selfType = Integer.parseInt(ids[0]);
						int parentType = Integer.parseInt(ids[1]);
						long parent_id = Long.parseLong(ids[2]);
						values = ClSCGenerator.getDepValues(parentType,
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
				.getValueList(listSql, (int) customer_id, "Gass");
		keSet = sqlItems.keySet();
		for (String key : keSet) {
			result.put(key, sqlItems.get(key));
		}
		return result;
	}

	@Override
	public Meter getMetterValue(int metterid, boolean withplombs)
			throws Exception {
		// TODO Auto-generated method stub
		return MDBConnection.getMetterValue(metterid, withplombs);
	}

	@Override
	public HashMap<Integer, String> getNamesForTypes(
			HashMap<Integer, Long> typeIds) throws Exception {
		HashMap<Integer, String> ret = new HashMap<Integer, String>();
		Set<Integer> types = typeIds.keySet();
		for (Integer key : types) {
			long val = typeIds.get(key);
			String value = ClSCGenerator.getValueForTypeAndId(key, val);
			ret.put(key, value);
		}
		return ret;
	}

	@Override
	public ArrayList<ClSelectionItem> getRegions() throws Exception {

		return ClSCGenerator.getRegions();
	}

	@Override
	public Long getServerTime() throws Exception {
		return MDBConnection.getServerTime(null);
	}

	@Override
	public ArrayList<ClSelectionItem> getStreets(long cityId) throws Exception {

		return ClSCGenerator.getStreets(cityId);
	}

	@Override
	public ArrayList<ClSelectionItem> getSubRegions(long regionId)
			throws Exception {

		return ClSCGenerator.getSubRegions(regionId);
	}

	@Override
	public ArrayList<ClSelectionItem> getTopType(int type) throws Exception {

		return ClSCGenerator.getToptValues(type);
	}

	@Override
	public int[] getUserAddress(int userid) throws Exception {
		return MDBConnection.getUserAddress(userid, null);
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getUserAddress(
			int type, long id, int userid) throws Exception {
		int[] addr = getUserAddress(userid);
		HashMap<Integer, ArrayList<ClSelectionItem>> items = getAddressObject(
				addr[1] > -1 ? ClSelection.T_SUBREGION : ClSelection.T_REGION,
				addr[1] > -1 ? addr[1] : addr[0]);

		ClSelectionItem addrS = new ClSelectionItem();
		addrS.setParentId(addr[0]);
		addrS.setId(addr[1]);
		ArrayList<ClSelectionItem> kkk = new ArrayList<ClSelectionItem>();
		kkk.add(addrS);
		items.put(-1, kkk);
		return items;
	}

	@Override
	public HashMap<Integer, ArrayList<UMObject>> getUserManagerObjects(int type)
			throws Exception {
		try {
			HashMap<Integer, ArrayList<UMObject>> result = new HashMap<Integer, ArrayList<UMObject>>();
			UserManager userMan = getUserManager(null);
			ArrayList<UMObject> rList = null;
			if (type == 0 || type == UMObject.PERMITION) {
				HashMap<Integer, Permission> tmPermitionIds = userMan
						.getTmPermissionIds();
				Collection<Permission> pList = tmPermitionIds.values();
				rList = new ArrayList<UMObject>();
				for (Permission permition : pList) {
					UMObject umo = new UMObject();
					umo.setType(UMObject.PERMITION);
					umo.setIdVal(permition.getId());
					umo.setTextVal(permition.getPermission_name());
					// umo.setPwdApplyed(false);
					// umo.setPwd("");
					rList.add(umo);
				}
				result.put(UMObject.PERMITION, rList);
			}
			if (type == 0 || type == UMObject.GROUP) {
				HashMap<Integer, Group> tmGroupIds = userMan.getTmGroupIds();
				Collection<Group> gList = tmGroupIds.values();
				rList = new ArrayList<UMObject>();
				for (Group g : gList) {
					UMObject umo = new UMObject();
					umo.setType(UMObject.GROUP);
					umo.setIdVal(g.getId());
					umo.setTextVal(g.getGroup_name());
					// umo.setPwdApplyed(false);
					// umo.setPwd("");
					rList.add(umo);
				}
				result.put(UMObject.GROUP, rList);
			}
			if (type == 0 || type == UMObject.USER) {
				HashMap<Integer, SUser> tmUserIds = userMan.getTmUserIds();
				Collection<SUser> uList = tmUserIds.values();
				rList = new ArrayList<UMObject>();
				for (SUser u : uList) {
					UMObject umo = new UMObject();
					umo.setType(UMObject.USER);
					umo.setIdVal(u.getIdVal());
					umo.setTextVal(u.getTextVal());
					// umo.setCaption_id(u.getUser().getCaption_id());
					// umo.setPwdApplyed(false);
					// umo.setPwd("");
					rList.add(umo);
				}
				result.put(UMObject.USER, rList);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public DocTypeMapping getValueMap(int custId, int doctypeid,
			int languageId, int userId) throws Exception {
		Connection conn = null;
		try {
			conn = DBConnection.getConnection("DocFlow");
			DocType dt = MDBConnection.getDocType(doctypeid, languageId);
			if (!dt.isApplied_customer())
				custId = 0;

			HashMap<String, String> values = custId > 0 ? MDBConnection
					.getValueMap(dt.getCust_selectfields().split(","),
							dt.getCust_sql(), custId, "Gass")
					: new HashMap<String, String>();

			DocTypeMapping result = new DocTypeMapping();
			int[] address = getUserAddress(userId);
			final SUser u = getUserManager(conn).getTmUserIds().get(userId);

			final TransfarableUser tu = new TransfarableUser();
			tu.generateFromRealUser(u);
			tu.setRegionid(address[0]);
			tu.setSubregionid(address[1]);

			CustomerShort cust = custId > 0 ? MDBConnection
					.getCustomerShort(custId) : null;

			if (custId > 0 && cust == null)
				throw new Exception("Cannot find customer by id:" + custId);
			if (custId > 0 && !u.hasPermition("CAN_VIEW_ALL_REGIONS")) {
				if (!((address[0] == -1 || cust.getRegionid() == tu
						.getRegionid()) && (address[1] == -1 || cust
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
				if (address[0] != -1) {
					region_id = address[0];
				}
				if (address[1] != -1) {
					subregion_id = address[1];
				}
				if (region_id == null && subregion_id != null) {
					ClSelectionItem so = ClSCGenerator.getValue(
							ClSelection.T_SUBREGION, (long) subregion_id);
					region_id = (int) so.getParentId();
				}
				if (region_id == null) {
					ArrayList<ClSelectionItem> items = ClSCGenerator
							.getToptValues(ClSelection.T_REGION);
					if (items != null && !items.isEmpty()) {
						region_id = (int) items.get(0).getId();
					}
				}

				if (region_id != null && subregion_id == null) {
					subregion_id = getFirstItem(ClSelection.T_REGION,
							ClSelection.T_SUBREGION, region_id.longValue())
							.intValue();
				}

				long cityid = getFirstItem(ClSelection.T_SUBREGION,
						ClSelection.T_CITY, subregion_id.longValue())
						.intValue();
				street_id = getFirstItem(ClSelection.T_CITY,
						ClSelection.T_STREET, cityid).intValue();
				zone = getFirstItem(ClSelection.T_SUBREGION,
						ClSelection.T_ZONES, subregion_id.longValue())
						.intValue();

			}

			result.setDocument(createDocumentFromCustomer(custId, street_id
					+ "", zone + ""));

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

			setupDocData(custId, dt, values, result, u, tu, null);

			return result;

		} finally {
			try {
				DBConnection.freeConnection(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void setupDocData(int custId, DocType dt,
			HashMap<String, String> values, DocTypeMapping result,
			final SUser u, final TransfarableUser tu,
			HashMap<String, String> displayValues) throws Exception {
		IPermitionChecker permitionChecker = new IPermitionChecker() {

			@Override
			public boolean hasPermition(int permitionId) {
				return u.hasPermition(permitionId);
			}

			@Override
			public boolean hasPermition(String permition_name) {
				return u.hasPermition(permition_name);
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
		result.setSelections(getListTypesForDocument(pr.getListSqls(), custId));
	}

	private void putValue(HashMap<String, String> values, String key,
			Object value) {
		if (value != null)
			values.put(key, value.toString().trim());
	}

	private Long getFirstItem(int parent_type, int subtype, long parent_id) {
		ArrayList<ClSelectionItem> items = ClSCGenerator.getDepValues(
				parent_type, subtype, parent_id);
		if (items != null && !items.isEmpty()) {
			return items.get(0).getId();
		}
		return -1L;
	}

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	@Override
	public UserObject loginUser(String userName, String password,
			int language_id, int system) throws Exception {
		Connection conn = null;
		boolean android = language_id < 0;
		language_id = Math.abs(language_id);

		try {

			// getThreadLocalRequest().gets
			// getServletContext().getr
			conn = DBConnection.getConnection("DocFlow");
			User user = DBConnection.loginUser(userName, password, "DocFlow",
					conn);
			if (user == null)
				throw new Exception("არასწორი მომხმარებელი ან პაროლი!!!");
			SUser sUser = getUserManager(conn).getTmUserIds().get(user.getId());
			if (sUser == null)
				throw new Exception("არასწორი მომხმარებელი ან პაროლი!!!");

			TransfarableUser trUser = new TransfarableUser();
			trUser.generateFromRealUser(sUser);

			PermissionSystemMap.hasPermition(system, trUser);
			if (dsDir == null) {
				dsDir = android && remote_server != null ? remote_server
						.getServletContext().getRealPath("/ds")
						: getServletContext().getRealPath("/ds");
			}

			UserObject uo = new UserObject();
			int[] address = MDBConnection.getUserAddress(user.getId(), conn);
			trUser.setRegionid(address[0]);
			trUser.setSubregionid(address[1]);
			if (!android)
				uo.setCaptions(ClSCGenerator.getDepValues(
						ClSelection.T_LANGUAGE, ClSelection.T_CAPTIONS,
						language_id));
			uo.setUser(trUser);
			uo.setServerTime(MDBConnection.getServerTime(conn));
			uo.setUuid(UUID.randomUUID().toString());
			uo.setZoneConfiguration(MDBConnection.getZoConfiguration(conn));
			if (!android)
				uo.setUser_Data(MDBConnection.getUser_Data(user.getId(), conn));
			int[] status_systems = system == SCSystem.S_CC_AND_ECCIDENT ? new int[] {
					SCSystem.S_CALL_CENTER, SCSystem.S_ECCIDENT_CONTROLL }
					: new int[] { system };

			HashMap<Integer, ArrayList<ClSelectionItem>> statusTree = new HashMap<Integer, ArrayList<ClSelectionItem>>();
			HashMap<Integer, StatusObject> statusObjectTree = new HashMap<Integer, StatusObject>();
			HashMap<Integer, ArrayList<DocType>> system_docTypes = new HashMap<Integer, ArrayList<DocType>>();
			for (int i = 0; i < status_systems.length; i++) {
				try {
					PermissionSystemMap.hasPermition(status_systems[i], trUser);
					if (uo.getInitial_system() == null)
						uo.setInitial_system(status_systems[i]);
					statusTree.put(status_systems[i],
							getStatuses(conn, language_id, status_systems[i]));
					StatusObject so = new StatusObject();
					MDBConnection.getStatuses(conn, status_systems[i], so);
					statusObjectTree.put(status_systems[i], so);
					system_docTypes.put(status_systems[i], MDBConnection
							.getDocTypes(language_id, trUser.getUser_id(),
									status_systems[i], conn));
				} catch (Exception e) {

				}
			}
			uo.setStatusTree(statusTree);
			uo.setStatusObjectTree(statusObjectTree);
			if (android)
				uo.setSystem_docTypes(system_docTypes);

			MDBConnection.getDS_AND_JS(uo);
			if (android) {
				uo.setB_box(null);
				uo.setCql_filter(null);
				uo.setJavascript(null);
			}
			if (!android)
				uo.setMaps(getMaps());
			if (!android) {
				File gisProps = new File("gis.properties");
				if (gisProps.exists()) {
					String cont = null;
					try {
						FileReader fl = new FileReader(gisProps);
						char[] c = new char[(int) gisProps.length()];
						fl.read(c); // reads the content to the array
						fl.close();
						cont = new String(c);
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (cont != null && !cont.trim().isEmpty()) {
						ArrayList<GisLayer> layers = uo.getMaps().get(0)
								.getGisLayers();
						for (GisLayer gisLayer : layers) {
							gisLayer.setWmsurl(cont);
						}
					}
				}

				try {
					if (dsDir != null)
						CreateCustomDatasource.createDebugDataSources(uo, conn,
								dsDir);
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (PermissionSystemMap.hasPermition(PermissionNames.PLOMBS,
						trUser)) {
					uo.setPlombPermition(true);
				} else {
					Map<String, Integer> plombuserCriteria = new HashMap<String, Integer>();
					plombuserCriteria.put("userid", user.getId());
					Map<?, ?> plombUsers = DMIUtils.findRecordByCriteria(
							"PlombUsersDS", null, plombuserCriteria);
					if (plombUsers != null && !plombUsers.isEmpty()) {
						uo.setPlombPermition(false);
					}
				}
				if (SCSystem.S_PLOMBS == system
						&& uo.getPlombPermition() == null) {
					throw new Exception("You do not have permission!!!");
				}
			}
			if (uo.getUser().getRegionid() >= 0) {
				Map<String, Integer> mapsubCriteria = new HashMap<String, Integer>();
				mapsubCriteria.put("ppcityid", uo.getUser().getRegionid());
				if (uo.getUser().getSubregionid() >= 0)
					mapsubCriteria
							.put("pcityid", uo.getUser().getSubregionid());

				Map<?, ?> subregions = DMIUtils.findRecordByCriteria(
						"CustomerDS", "getSubregions", mapsubCriteria);
				uo.setCql_filter(DMIUtils.getRowValueSt(subregions
						.get("cusname")));
				String subreions = DMIUtils.getRowValueSt(subregions
						.get("cityname"));
				Map<String, Object> mapBboxCriteria = new HashMap<String, Object>();
				mapBboxCriteria.put("regionid", uo.getUser().getRegionid());
				mapBboxCriteria.put("sub_regions", subreions);
				mapBboxCriteria.put("srid", 900913);
				Map<?, ?> bboxMap = DMIUtils
						.findRecordByCriteria("BuildingsDS",
								"buildingsGetDimention", mapBboxCriteria);
				uo.setB_box(DMIUtils.getRowValueSt(bboxMap.get("feature_text")));
			}
			return uo;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(conn);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public ArrayList<ClSelectionItem> getStatuses(Connection con,
			int language_id, int system_id) throws Exception {
		if (statuses == null) {
			Connection conn = null;
			try {
				conn = con == null ? DBConnection.getConnection("DocFlow")
						: con;
				statuses = DBConnection.getStatuses("DocFlow", conn);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			} finally {
				try {
					DBConnection.freeConnection(conn);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		ArrayList<ClSelectionItem> result = null;
		HashMap<Integer, ArrayList<ClSelectionItem>> systemst = statuses
				.get(language_id);
		if (systemst != null) {
			result = systemst.get(system_id);
		}
		if (result == null)
			result = new ArrayList<ClSelectionItem>();
		return result;
	}

	public ArrayList<GisMap> getMaps() throws Exception {
		if (gisMaps != null) {
			return gisMaps;
		}
		Connection conn = null;
		try {
			conn = DBConnection.getConnection("MAP");

			gisMaps = DBConnection.getMaps("MAP", conn);
			ArrayList<GisMapProperties> gisMapProperties = DBConnection
					.getGisMapProperties("MAP", conn);
			ArrayList<GisLayer> gisLayers = DBConnection.getGisLayers("MAP",
					conn);
			ArrayList<GisLayerOptions> gisLayerOptions = DBConnection
					.getGisLayerOptions("MAP", conn);
			for (GisLayerOptions layerOptions : gisLayerOptions) {
				for (GisLayer gisLayer : gisLayers) {
					if (layerOptions.getGis_layer_id() == gisLayer.getId()) {
						layerOptions.setGisLayer(gisLayer);
						gisLayer.getGisLayerOptions().add(layerOptions);
					}
				}
			}
			ArrayList<GisLayerFilter> gisLayerFilters = DBConnection
					.getGisLayerFilter("MAP", conn);

			for (GisLayerFilter gisLayerFilter : gisLayerFilters) {
				for (GisLayer gisLayer : gisLayers) {
					if (gisLayerFilter.getGis_layer_id() == gisLayer.getId()) {
						gisLayer.getGisLayerFilters().add(gisLayerFilter);
					}
				}
			}

			for (GisLayer gisLayer : gisLayers) {
				for (GisMap gisMap : gisMaps) {
					if (gisMap.getId() == gisLayer.getGismap_id()) {
						gisLayer.setMap(gisMap);
						gisMap.getGisLayers().add(gisLayer);
					}
				}
			}
			for (GisMapProperties mapProperties : gisMapProperties) {
				for (GisMap gisMap : gisMaps) {
					if (gisMap.getId() == mapProperties.getGismap_id()) {
						mapProperties.setMap(gisMap);
						gisMap.getGisMaProperties().add(mapProperties);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				DBConnection.freeConnection(conn);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return gisMaps;
	}

	@Override
	public void reloadParams() throws Exception {
		userManager = null;
		ClSCGenerator.reloadParams();

	}

	@Override
	public void saveDocType(DocType dt) throws Exception {
		MDBConnection.saveDocType(dt);

	}

	@Override
	public DocumentShort saveDocument(DocumentLong document,
			ArrayList<DocumentFile> files, int languageId) throws Exception {
		return MDBConnection.saveDocument(document, files, languageId);
	}

	@Override
	public Integer saveUsermanagerObject(BFUMObject umObject, int regionid,
			int subregionid) throws Exception {
		Integer ret = MDBConnection.saveUsermanagerObject(umObject, regionid,
				subregionid);
		userManager = null;
		return ret;
	}

	@Override
	public void setPermitions(boolean user, int user_or_group_id,
			String permition_ids, String group_ids) throws Exception {
		MDBConnection.setPermitionItems(user, user_or_group_id, permition_ids,
				group_ids);
		userManager = null;
	}

	@Override
	public HashMap<Integer, Captions> getCaptions(Long id) throws Exception {

		return MDBConnection.getCaptions(id);
	}

	@Override
	public long saveCaptions(Captions[] captions) throws Exception {

		return MDBConnection.saveCaptions(captions);
	}

	@Override
	public Integer[] saveCoefToCustomers(int[] customerIds, double coef,
			long date, int user_id, String cancelary) throws Exception {
		return MDBConnection.saveCoefToCustomers(customerIds, coef, date,
				user_id, cancelary);
	}

	@Override
	public Integer closeBankByDayNew(int bankid, Date bankDate, int pCity,
			int acc_id, int user_id, String user_name) throws Exception {
		return MDBConnection.closeBankByDayNew(bankid, bankDate, pCity, acc_id,
				user_id, user_name);
	}

	@Override
	public void saveDocTypePermitions(int user_or_group_id, boolean user,
			ArrayList<Integer> docTypes) throws Exception {
		MDBConnection.saveDocTypePermitions(user_or_group_id, user, docTypes);

	}

	@Override
	public DocTypesAndPermitions getDocTypePermitions(int user_or_group_id,
			boolean user, int language_id) throws Exception {
		DocTypesAndPermitions docTypesAndPermitions = new DocTypesAndPermitions();
		docTypesAndPermitions.setDocTypes(getDocTypes(language_id, -1, -1));
		docTypesAndPermitions.setRestrictions(MDBConnection
				.getDocTypeRestrictions(user_or_group_id, user));
		return docTypesAndPermitions;
	}

	@Override
	public CustomerShort getCustomerShort(int cusid) throws Exception {
		return MDBConnection.getCustomerShort(cusid);
	}

	@Override
	public String getCenterCoordinates(Integer customer_id,
			String subregion_id, String to_srid) throws Exception {
		return BuildingDMI.getCenterCoordinates(customer_id, subregion_id,
				to_srid);
	}

	@Override
	public HashMap<String, ArrayList<ClSelectionItem>> getListTypes(
			HashMap<String, ArrayList<DSFieldOptions>> listSqls)
			throws Exception {
		return MDBConnection.getListTypes(listSqls);
	}

	@Override
	public void ping() throws Exception {
		System.out.println("ping");
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getTopTypes(int[] types)
			throws Exception {
		HashMap<Integer, ArrayList<ClSelectionItem>> ret = new HashMap<Integer, ArrayList<ClSelectionItem>>();
		for (int type : types) {
			ret.put(type, ClSCGenerator.getToptValues(type));
		}
		return ret;

	}

	@Override
	public DocTypeWithDocList getDocListWithDocTypeXML(int doctypeid,
			long startdate, long enddate, int languageId,
			ArrayList<String> criterias, boolean print, boolean very_short,
			boolean xml, ListSizes sizes) throws Exception {
		DocType dt = null;
		if (doctypeid > 0)
			dt = MDBConnection.getDocType(doctypeid, languageId);
		DocTypeWithDocList result = MDBConnection.getDocListForType(doctypeid,
				startdate, enddate, languageId, criterias, print, very_short,
				xml, sizes);
		if (!print) {
			result.setDocList(result.getDocList());
			result.setDocType(dt);
			return result;
		} else
			return new DocTypeWithDocList();
	}

	@Override
	public String createUniqueIDForFileTransfer() throws DocFlowException {
		String ret = UUID.randomUUID().toString();
		return ret;
	}

	private static final ConcurrentHashMap<String, MakeDBTask> makeDBTasks = new ConcurrentHashMap<String, MakeDBTask>();

	@Override
	public MakeDBProcess createDBMakingProcess(int subregionid,
			Date lastDownloadedTiles) throws DocFlowException {
		MakeDBProcess process = new MakeDBProcess();
		ArrayList<String> tasks = new ArrayList<String>();

		String[] stasks = { "Creating tiles DB", "Map info", "Buildings",
				"Roads", "Settlements", "District meters", "Vacuum",
				"Vacuuming", "Zipping" };
		for (String string : stasks)
			tasks.add(string);

		String sessionID = createUniqueIDForFileTransfer();
		process.setOperations(tasks.toArray(stasks));
		process.setSessionID(sessionID);
		makeDBTasks.put(sessionID, new MakeDBTask(subregionid, sessionID,
				lastDownloadedTiles));
		return process;
	}

	@Override
	public MakeDBResponce getMakeDBProcessStatus(String sessionID)
			throws DocFlowException {
		MakeDBTask dbTask = makeDBTasks.get(sessionID);
		if (makeDBTasks == null)
			return null;
		Exception ex = dbTask.getException();
		if (ex != null)
			throw DocFlowExceptionCriator.doThrow(ex);
		MakeDBResponce dbResponce = new MakeDBResponce();
		dbResponce.setCompleted(dbTask.isCompleted());
		dbResponce.setFileSize(dbTask.getFileSize());
		dbResponce.setOperationCompleted(dbTask.getOperationCompleted());
		return dbResponce;
	}

	public static void flushMakeDB(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws DocFlowException {
		String sessionID = request.getParameter("sessionid");
		MakeDBTask dbTask = makeDBTasks.get(sessionID);
		if (makeDBTasks == null)
			return;
		try {
			dbTask.flush(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw DocFlowExceptionCriator.doThrow(e);
		}
	}

	@Override
	public HashMap<String, Integer> checkForUpdates(int subregionid,
			Date lastDownloaded) throws DocFlowException {
		try {
			return MakeDBTask.checkForUpdates(subregionid, lastDownloaded);
		} catch (Exception e) {
			throw DocFlowExceptionCriator.doThrow(e);
		}
	}

	@Override
	public NewDocuments checkForNewDocumentsAndroid(Long p_session_id,
			int p_user_id, int p_subregion_id, String p_system_ids,
			String p_android_device_id) throws DocFlowException {
		try {
			String s = MDBConnection.checkForNewDocumentsAndroid(p_session_id,
					p_user_id, p_subregion_id, p_system_ids,
					p_android_device_id);

			String spl[] = s.split(":");

			NewDocuments res = new NewDocuments();
			res.setSession_id(Long.valueOf(spl[0]));
			res.setFull_count(Long.valueOf(spl[1]));
			res.setSystems(new HashMap<Integer, Integer>());
			if (spl.length > 2) {
				spl = spl[2].split(",");
				for (String st : spl) {
					String stat[] = st.split("#");
					res.getSystems().put(Integer.parseInt(stat[0]),
							Integer.parseInt(stat[1]));
				}
			}
			return res;
		} catch (Exception e) {
			throw DocFlowExceptionCriator.doThrow(e);
		}
	}

	@Override
	public void saveDocumentFiles(Integer id, ArrayList<DocumentFile> files)
			throws DocFlowException {
		try {
			MDBConnection.saveFiles(id, files, null);
		} catch (Exception e) {
			throw DocFlowExceptionCriator.doThrow(e);
		}

	}

	@Override
	public ClSelectionItem getDocumentStateValue(Long id)
			throws DocFlowException {
		try {
			return MDBConnection.getDocumentStateValue(id);
		} catch (Exception e) {
			throw DocFlowExceptionCriator.doThrow(e);
		}
	}

	private CDSResponce createDsRequest(String dsName, CDSRequest dsReques,
			Map<String, Object> criteria, Map<String, Object> values,
			Map<String, Object> old_values, String operationType)
			throws Exception {
		System.out.println("dsName=" + dsName + "OperationId= "
				+ dsReques.getOperationId() + " criteria=" + criteria
				+ " operationType=" + operationType);
		DSRequest req = getDsRequest(dsName);
		if (dsReques != null)
			DataTools.setProperties(dsReques.getAttributes(), req);
		Long start_row = dsReques.getStartRow();
		if (start_row != null)
			req.setStartRow(start_row);
		Long end_row = dsReques.getEndRow();
		if (end_row != null)
			req.setEndRow(end_row);
		req.setOperationType(operationType);
		if (dsReques.getOperationId() != null)
			req.setOperationId(dsReques.getOperationId());
		System.out.println("OperationId=" + req.getOperationId());
		if (criteria != null)
			req.setCriteria(criteria);
		if (values != null)
			req.setValues(values);
		if (values != null)
			req.setValues(values);
		CDSResponce result = new CDSResponce();
		try {
			DSResponse resp = req.execute();

			result.setStartRow(resp.getStartRow());
			result.setTotalRows(resp.getTotalRows());
			result.setEndRow(resp.getEndRow());
			result.setResult(resp.getDataList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private DSRequest getDsRequest(String dsName) {
		DSRequest req = new DSRequest();
		req.setDataSourceName(dsName);
		return req;
	}

	@Override
	public CDSResponce dsFetchData(String dsName, Map<String, Object> criteria,
			CDSRequest dsReques) throws Exception {
		return createDsRequest(dsName, dsReques, criteria, null, null, "fetch");
	}

	@Override
	public CDSResponce dsAddData(String dsName, Map<String, Object> values,
			CDSRequest dsReques) throws Exception {
		return createDsRequest(dsName, dsReques, null, values, null, "add");
	}

	@Override
	public CDSResponce dsUpdateData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques)
			throws Exception {
		return createDsRequest(dsName, dsReques, null, values, old_values,
				"update");
	}

	@Override
	public CDSResponce dsDeleteData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques)
			throws Exception {
		return createDsRequest(dsName, dsReques, null, values, old_values,
				"remove");
	}

	@Override
	public List<DsField> getDataSourceFields(String dsName) throws Exception {
		List<DsField> result = new ArrayList<DsField>();
		DSRequest req = getDsRequest(dsName);
		DataSource ds = req.getDataSource();
		List<DSField> fields = ds.getFields();
		for (DSField dsf : fields) {
			if (dsf.getBoolean("hidden", false))
				continue;
			DsField f = new DsField(dsf.getName(), dsf.getType(),
					dsf.getTitle(), dsf.getLength(), dsf.isRequired());
			result.add(f);

		}
		return result;
	}

	@Override
	public DocTypeMapping getDocumentWithMapping(int docid, int languageId,
			int userId) throws Exception {
		DocumentLong doc = getDocument(docid, languageId);
		DocTypeMapping mapping = new DocTypeMapping();
		mapping.setDocument(doc);
		mapping.setCustomerShort(doc.getCustomerShort());
		mapping.setDocType(getDocType(doc.getDoc_type_id(), languageId));
		mapping.setFiles(getFilesForDocument(docid));
		HashMap<String, String> displayValues = new HashMap<String, String>();
		HashMap<String, String> values = getData(doc.getContent_xml(),
				displayValues);

		int[] address = getUserAddress(userId);
		final SUser u = getUserManager(null).getTmUserIds().get(userId);

		final TransfarableUser tu = new TransfarableUser();
		tu.generateFromRealUser(u);
		tu.setRegionid(address[0]);
		tu.setSubregionid(address[1]);

		setupDocData(doc.getCust_id(), mapping.getDocType(), values, mapping,
				u, tu, displayValues);
		if (!displayValues.isEmpty())
			mapping.setDisplayValues(displayValues);
		mapping.setValues(values);
		return mapping;
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

	@Override
	public DbExpoResult createExportSession(int subregion_id)
			throws DocFlowException {
		return Exporter.createExporterSession(subregion_id);
	}

	@Override
	public DbExpoResult getExportStatus(String session_id)
			throws DocFlowException {
		return Exporter.getExporterCurrentStatus(session_id);
	}

}
