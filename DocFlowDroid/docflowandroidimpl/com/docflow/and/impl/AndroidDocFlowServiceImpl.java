package com.docflow.and.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.shared.ClSelectionItem;
import com.common.shared.DSFieldOptions;
import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.common.shared.ds.DsField;
import com.common.shared.model.UMObject;
import com.common.shared.usermanager.User;
import com.docflow.and.impl.db.ADBResultObjectExecutor;
import com.docflow.and.impl.db.DBConnectionAnd;
import com.docflow.and.impl.db.ExecutorConstructor;
import com.docflow.and.impl.ds.DataSource;
import com.docflow.client.DocFlowService;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.DbExpoResult;
import com.docflow.shared.DocFlowException;
import com.docflow.shared.DocStatusCount;
import com.docflow.shared.ListSizes;
import com.docflow.shared.Meter;
import com.docflow.shared.UserObject;
import com.docflow.shared.common.BFUMObject;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.GroupsAndPermitions;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocTypesAndPermitions;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.docflow.shared.docflow.NewDocuments;
import com.docflow.shared.hr.Captions;
import com.docflow.shared.map.MakeDBProcess;
import com.docflow.shared.map.MakeDBResponce;

public class AndroidDocFlowServiceImpl implements DocFlowService {

	public AndroidDocFlowServiceImpl() throws Throwable {

	}

	@Override
	public CustomerShort getCustomerShort(int cusid) throws Exception {
		return MDBConnection.getCustomerShort(cusid, null);
	}

	@Override
	public UserObject loginUser(String userName, String password,
			int language_id, int system) throws Exception {
		return LoginManager.login(userName, password, language_id, system);
	}

	@Override
	public DocTypeMapping getValueMap(int custId, int doctypeid,
			int languageId, int userId) throws Exception {
		return new DocumentCreator().getValueMap(custId, doctypeid, languageId,
				userId);
	}

	@Override
	public HashMap<String, ArrayList<ClSelectionItem>> getListTypesForDocument(
			HashMap<String, String> listSqls, long customer_id)
			throws Exception {
		return new DocumentCreator().getListTypesForDocument(null, listSqls,
				customer_id);
	}

	@Override
	public DocTypeMapping getDocumentWithMapping(int docid, int languageId,
			int userId) throws Exception {
		return new DocumentCreator().getDocumentWithMapping(docid, languageId,
				userId);

	}

	@Override
	public DocumentLong getDocument(int docid, int languageId) throws Exception {
		return new DocumentCreator().getDocument(null, null, docid, languageId);
	}

	@Override
	public ArrayList<DocumentFile> getFilesForDocument(int docId)
			throws Exception {
		return MDBConnection.getFilesForDocument(null, docId);
	}

	@Override
	public Long getServerTime() throws Exception {
		return System.currentTimeMillis();
	}

	@Override
	public ArrayList<ClSelectionItem> getStreets(long cityId) throws Exception {
		return MDBConnection.getClSelectionItems(null, ClSelection.T_STREET,
				cityId);
	}

	@Override
	public ArrayList<ClSelectionItem> getSubRegions(long regionId)
			throws Exception {
		return MDBConnection.getClSelectionItems(null, ClSelection.T_SUBREGION,
				regionId);
	}

	@Override
	public ArrayList<ClSelectionItem> getTopType(int type) throws Exception {
		return ClSelectionItemLoader.getTopType(null, type);
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getTopTypes(int[] types)
			throws Exception {
		return ClSelectionItemLoader.getTopTypes(null, types);
	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getAddressObject(
			int type, long id) throws Exception {
		Connection conn = null;
		try {
			conn = DBConnectionAnd.getExportedDB();

			HashMap<Integer, ArrayList<ClSelectionItem>> result = new HashMap<Integer, ArrayList<ClSelectionItem>>();
			result.put(ClSelection.T_REGION, ClSelectionItemLoader.getTopType(
					conn, ClSelection.T_REGION));
			ClSelectionItem value = MDBConnection.getClSelectionItem(conn,
					type, id);
			switch (type) {
			case ClSelection.T_SUBREGION:
			case ClSelection.T_REGION:
				if (value != null) {
					result.put(ClSelection.T_SUBREGION, MDBConnection
							.getClSelectionItems(
									conn,
									ClSelection.T_SUBREGION,
									type == ClSelection.T_SUBREGION ? value
											.getParentId() : value.getId()));
				}
				break;
			case ClSelection.T_CITY:
				ClSelectionItem subregion = MDBConnection.getClSelectionItem(
						conn, ClSelection.T_CITY, id);
				if (subregion != null && value != null) {
					result.put(ClSelection.T_SUBREGION, MDBConnection
							.getClSelectionItems(conn, ClSelection.T_SUBREGION,
									subregion.getParentId()));
					result.put(ClSelection.T_CITY, MDBConnection
							.getClSelectionItems(conn, ClSelection.T_CITY,
									value.getParentId()));
				}
				break;
			default:
				break;
			}
			return result;

		} finally {
			DBConnectionAnd.closeAll(conn);
		}

	}

	@Override
	public HashMap<Integer, ArrayList<ClSelectionItem>> getAllTopTypes()
			throws Exception {
		return ClSelectionItemLoader.getAllTopTypes(null);
	}

	@Override
	public ArrayList<ClSelectionItem> getCities(long subRegionId)
			throws Exception {
		return MDBConnection.getClSelectionItems(null, ClSelection.T_CITY,
				subRegionId);
	}

	@Override
	public void ping() throws Exception {
		System.out.println("ping");

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
	public ArrayList<ClSelectionItem> getRegions() throws Exception {
		return ClSelectionItemLoader.getTopType(null, ClSelection.T_REGION);
	}

	@Override
	public int[] getUserAddress(int userid) throws Exception {
		User u = MDBConnection.getUser(userid, null);
		int[] addr = new int[] { u.getRegionid(), u.getSubregionid() };
		return addr;

	}

	private CDSResponce createDsRequest(String dsName, CDSRequest dsReques,
			Map<String, Object> criteria, Map<String, Object> values,
			Map<String, Object> old_values, String operationType)
			throws Exception {
		System.out.println("dsName=" + dsName + "OperationId= "
				+ dsReques.getOperationId() + " criteria=" + criteria
				+ " operationType=" + operationType);
		String sql = DataSource.getRequestSql(dsName, operationType,
				dsReques.getOperationId(), criteria);

		Long start_row = dsReques.getStartRow();

		Long end_row = dsReques.getEndRow();

		CDSResponce result = new CDSResponce();
		Connection con = null;
		try {
			con = DBConnectionAnd.getExportedDB();
			String count_select = "select count(1) cnt from (" + sql + ") k";
			Integer totalRows = new ADBResultObjectExecutor<Integer>(
					new ExecutorConstructor(con)) {
				@Override
				public Integer getResult(ResultSet rs) throws Exception {

					return rs.getInt("cnt");
				}
			}.getObjectFromDB(count_select);

			String select = "select * from (" + sql + ") k";

			if (start_row != null && end_row != null)
				select = select + " OFFSET " + start_row + " LIMIT "
						+ (end_row - start_row);
			final Map<String, Integer> metadata = new HashMap<String, Integer>();
			ArrayList<Map<String, Object>> resultList = new ADBResultObjectExecutor<Map<String, Object>>(
					new ExecutorConstructor(con)) {
				@Override
				public Map<String, Object> getResult(ResultSet rs)
						throws Exception {
					if (metadata.isEmpty())
						generateColumns(metadata, rs.getMetaData());
					Map<String, Object> result = new HashMap<String, Object>();
					putValues(metadata, rs, result);
					return result;
				}
			}.getObjectsFromDB(select);

			result.setStartRow(start_row);
			result.setTotalRows((long) totalRows.longValue());
			result.setEndRow(end_row);
			result.setResult(resultList);
		} finally {
			DBConnectionAnd.closeAll(con);
		}
		return result;
	}

	protected void generateColumns(Map<String, Integer> metadata,
			ResultSetMetaData rsMetadata) throws Exception {
		int columnCount = rsMetadata.getColumnCount();

		for (int i = 0; i < columnCount; i++) {
			String field_name = rsMetadata.getColumnName(i + 1);

			metadata.put(field_name, rsMetadata.getColumnType(i + 1));

		}

	}

	protected void putValues(Map<String, Integer> metadata, ResultSet rs,
			Map<String, Object> result) throws Exception {
		for (String fieldName : metadata.keySet()) {

			String field_name = fieldName;
			int last_index = field_name.lastIndexOf(".");
			if (last_index > -1) {
				field_name = field_name.substring(last_index + 1);
			}

			Object value = rs.getObject(fieldName);
			if (value != null)
				result.put(field_name, value);
		}
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
	public void bOperationsActive(int operid, int user_id, String user_name)
			throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void changeZoneToCustomers(int[] customerIds, long zone)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Integer[] saveCoefToCustomers(int[] customerIds, double coef,
			long date, int user_id, String cancelary) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Integer closeBankByDay(int bankid, Date bankDate, int pCity)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Integer closeBankByDayNew(int bankid, Date bankDate, int pCity,
			int acc_id, int user_id, String user_name) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void devicedelete(int id, int deviceid) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public DocumentShort documentChangeState(int doc, String replica,
			int doc_state, int user_id, int languageId, long time,
			boolean anyway) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DocumentShort documentCorrection(int doc, String content_xml,
			int user_id, long transaction_date,
			ArrayList<DocumentFile> docFiles, int languageId, int docdelay)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ArrayList<Integer> documentsChangeState(ArrayList<Integer> docids,
			String replica, int doc_state, int user_id, int languageId,
			long time) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ArrayList<DocStatusCount> getDocCountStatus(int languageId,
			int system_id) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DocTypeWithDocList getDocListForType(int doctypeid, int languageId,
			int userid, long startdate, long enddate) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print, boolean very_short, ListSizes sizes)
			throws Exception {
		return getDocListWithDocTypeXML(doctypeid, startdate, enddate,
				languageId, criterias, print, very_short, false, sizes);
	}

	@Override
	public DocTypeWithDocList getDocListWithDocTypeXML(int doctypeid,
			long startdate, long enddate, int languageId,
			ArrayList<String> criterias, boolean print, boolean very_short,
			boolean xml, ListSizes sizes) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DocType getDocType(int id, int languageId) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ArrayList<DocType> getDocTypes(int languageId, int user_id,
			int system_id) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public GroupsAndPermitions getGroupsAndPermitions(boolean user,
			int user_or_group_id) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ArrayList<ClSelectionItem> getItemsForType(int type, int subtype,
			long parentId) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HashMap<String, ArrayList<ClSelectionItem>> getListTypes(
			HashMap<String, ArrayList<DSFieldOptions>> listSqls)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Meter getMetterValue(int metterid, boolean withplombs)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HashMap<Integer, String> getNamesForTypes(
			HashMap<Integer, Long> typeIds) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HashMap<Integer, ArrayList<UMObject>> getUserManagerObjects(int type)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String greetServer(String name) throws IllegalArgumentException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void reloadParams() throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void saveDocType(DocType dt) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public DocumentShort saveDocument(DocumentLong document,
			ArrayList<DocumentFile> docFiles, int languageId) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public Integer saveUsermanagerObject(BFUMObject umObject, int regionid,
			int subregionid) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void setPermitions(boolean user, int user_or_group_id,
			String permition_ids, String group_ids) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public HashMap<Integer, Captions> getCaptions(Long id) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public long saveCaptions(Captions[] captions) throws Exception {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCenterCoordinates(Integer customer_id,
			String subregion_id, String to_srid) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void saveDocTypePermitions(int user_or_group_id, boolean user,
			ArrayList<Integer> docTypes) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public DocTypesAndPermitions getDocTypePermitions(int user_or_group_id,
			boolean user, int language_id) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public MakeDBProcess createDBMakingProcess(int subregionid,
			Date lastDownloadedTiles) throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public String createUniqueIDForFileTransfer() throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public MakeDBResponce getMakeDBProcessStatus(String sessionID)
			throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public HashMap<String, Integer> checkForUpdates(int subregionid,
			Date lastDownloaded) throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public NewDocuments checkForNewDocumentsAndroid(Long p_session_id,
			int p_user_id, int p_subregion_id, String p_system_ids,
			String p_android_device_id) throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public void saveDocumentFiles(Integer id, ArrayList<DocumentFile> files)
			throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public ClSelectionItem getDocumentStateValue(Long id)
			throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public List<DsField> getDataSourceFields(String dsName) throws Exception {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public DbExpoResult createExportSession(int subregion_id)
			throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

	@Override
	public DbExpoResult getExportStatus(String session_id)
			throws DocFlowException {
		throw new RuntimeException("Not implemented");

	}

}
