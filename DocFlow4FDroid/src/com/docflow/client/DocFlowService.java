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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("docflow")
public interface DocFlowService extends RemoteService {

	public CustomerShort getCustomerShort(int cusid) throws Exception;

	public void bOperationsActive(int operid, int user_id, String user_name)
			throws Exception;

	public void changeZoneToCustomers(int[] customerIds, long zone)
			throws Exception;

	public Integer[] saveCoefToCustomers(int[] customerIds, double coef,
			long date, int user_id, String cancelary) throws Exception;

	public Integer closeBankByDay(int bankid, Date bankDate, int pCity)
			throws Exception;

	public Integer closeBankByDayNew(int bankid, Date bankDate, int pCity,
			int acc_id, int user_id, String user_name) throws Exception;

	public void devicedelete(int id, int deviceid) throws Exception;

	public DocumentShort documentChangeState(int doc, String replica,
			int doc_state, int user_id, int languageId, long time,
			boolean anyway) throws Exception;

	public DocumentShort documentCorrection(int doc, String content_xml,
			int user_id, long transaction_date,
			ArrayList<DocumentFile> docFiles, int languageId, int docdelay)
			throws Exception;

	public ArrayList<Integer> documentsChangeState(ArrayList<Integer> docids,
			String replica, int doc_state, int user_id, int languageId,
			long time) throws Exception;

	public HashMap<Integer, ArrayList<ClSelectionItem>> getAddressObject(
			int type, long id) throws Exception;

	public HashMap<Integer, ArrayList<ClSelectionItem>> getAllTopTypes()
			throws Exception;

	public ArrayList<ClSelectionItem> getCities(long subRegionId)
			throws Exception;

	public ArrayList<DocStatusCount> getDocCountStatus(int languageId,
			int system_id) throws Exception;

	public DocTypeWithDocList getDocListForType(int doctypeid, int languageId,
			int userid, long startdate, long enddate) throws Exception;

	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print) throws Exception;

	public DocTypeWithDocList getDocListForType(int doctypeid, long startdate,
			long enddate, int languageId, ArrayList<String> criterias,
			boolean print, boolean very_short, ListSizes sizes)
			throws Exception;

	public DocTypeWithDocList getDocListWithDocTypeXML(int doctypeid,
			long startdate, long enddate, int languageId,
			ArrayList<String> criterias, boolean print, boolean very_short,
			boolean xml, ListSizes sizes) throws Exception;

	public DocType getDocType(int id, int languageId) throws Exception;

	public ArrayList<DocType> getDocTypes(int languageId, int user_id,
			int system_id) throws Exception;

	public DocumentLong getDocument(int docid, int languageId) throws Exception;

	public ArrayList<DocumentFile> getFilesForDocument(int docId)
			throws Exception;

	public GroupsAndPermitions getGroupsAndPermitions(boolean user,
			int user_or_group_id) throws Exception;

	public ArrayList<ClSelectionItem> getItemsForType(int type, int subtype,
			long parentId) throws Exception;

	public HashMap<String, ArrayList<ClSelectionItem>> getListTypesForDocument(
			HashMap<String, String> listSqls, long customer_id)
			throws Exception;

	public HashMap<String, ArrayList<ClSelectionItem>> getListTypes(
			HashMap<String, ArrayList<DSFieldOptions>> listSqls)
			throws Exception;

	public Meter getMetterValue(int metterid, boolean withplombs)
			throws Exception;

	public HashMap<Integer, String> getNamesForTypes(
			HashMap<Integer, Long> typeIds) throws Exception;

	public ArrayList<ClSelectionItem> getRegions() throws Exception;

	public Long getServerTime() throws Exception;

	public ArrayList<ClSelectionItem> getStreets(long cityId) throws Exception;

	public ArrayList<ClSelectionItem> getSubRegions(long regionId)
			throws Exception;

	public ArrayList<ClSelectionItem> getTopType(int type) throws Exception;

	public HashMap<Integer, ArrayList<ClSelectionItem>> getTopTypes(int[] types)
			throws Exception;

	public int[] getUserAddress(int userid) throws Exception;

	public HashMap<Integer, ArrayList<ClSelectionItem>> getUserAddress(
			int type, long id, int userid) throws Exception;

	public HashMap<Integer, ArrayList<UMObject>> getUserManagerObjects(int type)
			throws Exception;

	public DocTypeMapping getValueMap(int custId, int doctypeid,
			int languageId, int userId) throws Exception;

	String greetServer(String name) throws IllegalArgumentException;

	public UserObject loginUser(String userName, String password,
			int language_id, int system) throws Exception;

	public void reloadParams() throws Exception;

	public void saveDocType(DocType dt) throws Exception;

	public DocumentShort saveDocument(DocumentLong document,
			ArrayList<DocumentFile> docFiles, int languageId) throws Exception;

	public Integer saveUsermanagerObject(BFUMObject umObject, int regionid,
			int subregionid) throws Exception;

	public void setPermitions(boolean user, int user_or_group_id,
			String permition_ids, String group_ids) throws Exception;

	public HashMap<Integer, Captions> getCaptions(Long id) throws Exception;

	public long saveCaptions(Captions[] captions) throws Exception;

	public String getCenterCoordinates(Integer customer_id,
			String subregion_id, String to_srid) throws Exception;

	public void saveDocTypePermitions(int user_or_group_id, boolean user,
			ArrayList<Integer> docTypes) throws Exception;

	public DocTypesAndPermitions getDocTypePermitions(int user_or_group_id,
			boolean user, int language_id) throws Exception;

	public void ping() throws Exception;

	public MakeDBProcess createDBMakingProcess(int subregionid,
			Date lastDownloadedTiles) throws DocFlowException;

	public String createUniqueIDForFileTransfer() throws DocFlowException;

	public MakeDBResponce getMakeDBProcessStatus(String sessionID)
			throws DocFlowException;

	public HashMap<String, Integer> checkForUpdates(int subregionid,
			Date lastDownloaded) throws DocFlowException;

	public NewDocuments checkForNewDocumentsAndroid(Long p_session_id,
			int p_user_id, int p_subregion_id, String p_system_ids,
			String p_android_device_id) throws DocFlowException;

	public void saveDocumentFiles(Integer id, ArrayList<DocumentFile> files)
			throws DocFlowException;

	public ClSelectionItem getDocumentStateValue(Long id)
			throws DocFlowException;

	public CDSResponce dsFetchData(String dsName, Map<String, Object> criteria,
			CDSRequest dsReques) throws Exception;

	public CDSResponce dsAddData(String dsName, Map<String, Object> values,
			CDSRequest dsReques) throws Exception;

	public CDSResponce dsUpdateData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques)
			throws Exception;

	public CDSResponce dsDeleteData(String dsName, Map<String, Object> values,
			Map<String, Object> old_values, CDSRequest dsReques)
			throws Exception;

	public List<DsField> getDataSourceFields(String dsName) throws Exception;

	public DocTypeMapping getDocumentWithMapping(int docid, int languageId,
			int userId) throws Exception;

	public DbExpoResult createExportSession(int subregion_id)
			throws DocFlowException;
	
	public DbExpoResult getExportStatus(String session_id)
			throws DocFlowException;
	

}
