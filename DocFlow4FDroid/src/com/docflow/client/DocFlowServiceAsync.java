package com.docflow.client;

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
import com.docflow.shared.CustomerShort;
import com.docflow.shared.DbExpoResult;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DocFlowServiceAsync {
	void bOperationsActive(int operid, int user_id, String user_name,
			AsyncCallback<Void> callback);

	void changeZoneToCustomers(int[] customerIds, long zone,
			AsyncCallback<Void> callback);

	void closeBankByDay(int bankid, Date bankDate, int pCity,
			AsyncCallback<Integer> callback);

	void devicedelete(int id, int deviceid, AsyncCallback<Void> callback);

	void documentChangeState(int doc, String replica, int doc_state,
			int user_id, int languageId, long time, boolean anyway,
			AsyncCallback<DocumentShort> callback);

	void documentCorrection(int doc, String content_xml, int user_id,
			long transaction_date, ArrayList<DocumentFile> docFiles,
			int languageId, int docdelay, AsyncCallback<DocumentShort> callback);

	void documentsChangeState(ArrayList<Integer> docids, String replica,
			int doc_state, int user_id, int languageId, long time,
			AsyncCallback<ArrayList<Integer>> callback);

	void getAddressObject(int type, long id,
			AsyncCallback<HashMap<Integer, ArrayList<ClSelectionItem>>> callback);

	void getAllTopTypes(
			AsyncCallback<HashMap<Integer, ArrayList<ClSelectionItem>>> callback);

	void getCities(long subRegionId,
			AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getDocCountStatus(int languageId, int system_id,
			AsyncCallback<ArrayList<DocStatusCount>> callback);

	void getDocListForType(int doctypeid, int languageId, int userid,
			long startdate, long enddate,
			AsyncCallback<DocTypeWithDocList> callback);

	void getDocListForType(int doctypeid, long startdate, long enddate,
			int languageId, ArrayList<String> criterias, boolean print,
			AsyncCallback<DocTypeWithDocList> callback);

	void getDocType(int id, int languageId, AsyncCallback<DocType> callback);

	void getDocTypes(int languageId, int user_id, int system_id,
			AsyncCallback<ArrayList<DocType>> callback);

	void getDocument(int docid, int languageId,
			AsyncCallback<DocumentLong> callback);

	void getFilesForDocument(int docId,
			AsyncCallback<ArrayList<DocumentFile>> callback);

	void getGroupsAndPermitions(boolean user, int user_or_group_id,
			AsyncCallback<GroupsAndPermitions> callback);

	void getItemsForType(int type, int subtype, long parentId,
			AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getListTypesForDocument(HashMap<String, String> listSqls,
			long customer_id,
			AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>> callback);

	void getMetterValue(int metterid, boolean withplombs,
			AsyncCallback<Meter> callback);

	void getNamesForTypes(HashMap<Integer, Long> typeIds,
			AsyncCallback<HashMap<Integer, String>> callback);

	void getRegions(AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getServerTime(AsyncCallback<Long> callback);

	void getStreets(long cityId,
			AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getSubRegions(long regionId,
			AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getTopType(int type, AsyncCallback<ArrayList<ClSelectionItem>> callback);

	void getUserAddress(int userid, AsyncCallback<int[]> callback);

	void getUserAddress(int type, long id, int userid,
			AsyncCallback<HashMap<Integer, ArrayList<ClSelectionItem>>> callback);

	void getUserManagerObjects(int type,
			AsyncCallback<HashMap<Integer, ArrayList<UMObject>>> callback);

	void getValueMap(int custId, int doctypeid, int languageId, int userId,
			AsyncCallback<DocTypeMapping> callback);

	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void loginUser(String userName, String password, int language_id,
			int system, AsyncCallback<UserObject> callback);

	void reloadParams(AsyncCallback<Void> callback);

	void saveDocType(DocType dt, AsyncCallback<Void> callback);

	void saveDocument(DocumentLong document, ArrayList<DocumentFile> docFiles,
			int languageId, AsyncCallback<DocumentShort> callback);

	void saveUsermanagerObject(BFUMObject umObject, int regionid,
			int subregionid, AsyncCallback<Integer> callback);

	void setPermitions(boolean user, int user_or_group_id,
			String permition_ids, String group_ids, AsyncCallback<Void> callback);

	void getCaptions(Long id, AsyncCallback<HashMap<Integer, Captions>> callback);

	void saveCaptions(Captions[] captions, AsyncCallback<Long> callback);

	void saveCoefToCustomers(int[] customerIds, double coef, long date,
			int user_id, String cancelary, AsyncCallback<Integer[]> callback);

	void closeBankByDayNew(int bankid, Date bankDate, int pCity, int acc_id,
			int user_id, String user_name, AsyncCallback<Integer> callback);

	void saveDocTypePermitions(int user_or_group_id, boolean user,
			ArrayList<Integer> docTypes, AsyncCallback<Void> callback);

	void getDocTypePermitions(int user_or_group_id, boolean user,
			int language_id, AsyncCallback<DocTypesAndPermitions> callback);

	void getCustomerShort(int cusid, AsyncCallback<CustomerShort> callback);

	void getCenterCoordinates(Integer customer_id, String subregion_id,
			String to_srid, AsyncCallback<String> callback);

	void getListTypes(HashMap<String, ArrayList<DSFieldOptions>> listSqls,
			AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>> callback);

	void getDocListForType(int doctypeid, long startdate, long enddate,
			int languageId, ArrayList<String> criterias, boolean print,
			boolean very_short, ListSizes sizes,
			AsyncCallback<DocTypeWithDocList> callback);

	void ping(AsyncCallback<Void> callback);

	void getTopTypes(int[] types,
			AsyncCallback<HashMap<Integer, ArrayList<ClSelectionItem>>> callback);

	void getDocListWithDocTypeXML(int doctypeid, long startdate, long enddate,
			int languageId, ArrayList<String> criterias, boolean print,
			boolean very_short, boolean xml, ListSizes sizes,
			AsyncCallback<DocTypeWithDocList> callback);

	void createDBMakingProcess(int subregionid, Date lastDownloadedTiles,
			AsyncCallback<MakeDBProcess> callback);

	void createUniqueIDForFileTransfer(AsyncCallback<String> callback);

	void getMakeDBProcessStatus(String sessionID,
			AsyncCallback<MakeDBResponce> callback);

	void checkForUpdates(int subregionid, Date lastDownloaded,
			AsyncCallback<HashMap<String, Integer>> callback);

	void checkForNewDocumentsAndroid(Long p_session_id, int p_user_id,
			int p_subregion_id, String p_system_ids,
			String p_android_device_id, AsyncCallback<NewDocuments> callback);

	void saveDocumentFiles(Integer id, ArrayList<DocumentFile> files,
			AsyncCallback<Void> callback);

	void getDocumentStateValue(Long id, AsyncCallback<ClSelectionItem> callback);

	void dsFetchData(String dsName, Map<String, Object> criteria,
			CDSRequest dsReques, AsyncCallback<CDSResponce> callback);

	void dsAddData(String dsName, Map<String, Object> values,
			CDSRequest dsReques, AsyncCallback<CDSResponce> callback);

	void dsUpdateData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques,
			AsyncCallback<CDSResponce> callback);

	void dsDeleteData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques,
			AsyncCallback<CDSResponce> callback);

	void getDataSourceFields(String dsName,
			AsyncCallback<List<DsField>> callback);

	void getDocumentWithMapping(int docid, int languageId, int userId,
			AsyncCallback<DocTypeMapping> callback);

	void createExportSession(int subregion_id,
			AsyncCallback<DbExpoResult> callback);

	void getExportStatus(String session_id, AsyncCallback<DbExpoResult> callback);

}
