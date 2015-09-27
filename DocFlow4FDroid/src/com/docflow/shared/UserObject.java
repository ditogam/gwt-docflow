package com.docflow.shared;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.ClSelectionItem;
import com.common.shared.map.GisMap;
import com.common.shared.usermanager.TransfarableUser;
import com.docflow.shared.common.ZoneChangeConfiguration;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.user.client.rpc.IsSerializable;

public class UserObject implements IsSerializable {

	/**
	 * 
	 */
	private ArrayList<GisMap> maps;
	private String uuid;
	private String cql_filter;
	private String b_box;
	private long serverTime;
	private TransfarableUser user;
	private ArrayList<ClSelectionItem> captions;
	private Boolean plombPermition;
	private ZoneChangeConfiguration zoneConfiguration;
	private User_Data user_Data;
	private String javascript;
	private boolean debug_js;
	private boolean debug_ds;
	private ArrayList<String> datasourceNames;
	private String fullDS;
	private String imports;
	private ArrayList<String> methodes;
	private HashMap<Integer, ArrayList<ClSelectionItem>> statusTree;
	private HashMap<Integer, StatusObject> statusObjectTree;
	private Integer initial_system;
	private String android_map_renderer;
	private int android_check_status_interval;
	private HashMap<Integer, ArrayList<DocType>> system_docTypes;

	public ArrayList<ClSelectionItem> getCaptions() {
		return captions;
	}

	public long getServerTime() {
		return serverTime;
	}

	public TransfarableUser getUser() {
		return user;
	}

	public void setCaptions(ArrayList<ClSelectionItem> captions) {
		this.captions = captions;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public void setUser(TransfarableUser user) {
		this.user = user;
	}

	public ArrayList<GisMap> getMaps() {
		return maps;
	}

	public void setMaps(ArrayList<GisMap> maps) {
		this.maps = maps;
	}

	public String getCql_filter() {
		return cql_filter;
	}

	public void setCql_filter(String cql_filter) {
		this.cql_filter = cql_filter;
	}

	public String getB_box() {
		return b_box;
	}

	public void setB_box(String b_box) {
		this.b_box = b_box;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Boolean getPlombPermition() {
		return plombPermition;
	}

	public void setPlombPermition(Boolean plombPermition) {
		this.plombPermition = plombPermition;
	}

	public ZoneChangeConfiguration getZoneConfiguration() {
		return zoneConfiguration;
	}

	public void setZoneConfiguration(ZoneChangeConfiguration zoneConfiguration) {
		this.zoneConfiguration = zoneConfiguration;
	}

	public User_Data getUser_Data() {
		return user_Data;
	}

	public void setUser_Data(User_Data user_Data) {
		this.user_Data = user_Data;
	}

	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	public boolean isDebug_js() {
		return debug_js;
	}

	public void setDebug_js(boolean debug_js) {
		this.debug_js = debug_js;
	}

	public boolean isDebug_ds() {
		return debug_ds;
	}

	public void setDebug_ds(boolean debug_ds) {
		this.debug_ds = debug_ds;
	}

	public ArrayList<String> getDatasourceNames() {
		return datasourceNames;
	}

	public void setDatasourceNames(ArrayList<String> datasourceNames) {
		this.datasourceNames = datasourceNames;
	}

	public String getFullDS() {
		return fullDS;
	}

	public void setFullDS(String fullDS) {
		this.fullDS = fullDS;
	}

	public String getImports() {
		return imports;
	}

	public void setImports(String imports) {
		this.imports = imports;
	}

	public ArrayList<String> getMethodes() {
		return methodes;
	}

	public void setMethodes(ArrayList<String> methodes) {
		this.methodes = methodes;
	}

	public HashMap<Integer, ArrayList<ClSelectionItem>> getStatusTree() {
		return statusTree;
	}

	public void setStatusTree(
			HashMap<Integer, ArrayList<ClSelectionItem>> statusTree) {
		this.statusTree = statusTree;
	}

	public HashMap<Integer, StatusObject> getStatusObjectTree() {
		return statusObjectTree;
	}

	public void setStatusObjectTree(
			HashMap<Integer, StatusObject> statusObjectTree) {
		this.statusObjectTree = statusObjectTree;
	}

	public int getInitial_status(int system_id) {
		return statusObjectTree.get(system_id).getInitial_status();
	}

	public int getApproved_status(int system_id) {
		return statusObjectTree.get(system_id).getApproved_status();
	}

	public ArrayList<ClSelectionItem> getStatuses(int system_id) {
		return statusTree.get(system_id);
	}

	public Integer getInitial_system() {
		return initial_system;
	}

	public void setInitial_system(Integer initial_system) {
		this.initial_system = initial_system;
	}

	public String getAndroid_map_renderer() {
		return android_map_renderer;
	}

	public void setAndroid_map_renderer(String android_map_renderer) {
		this.android_map_renderer = android_map_renderer;
	}

	public int getAndroid_check_status_interval() {
		return android_check_status_interval;
	}

	public void setAndroid_check_status_interval(
			int android_check_status_interval) {
		this.android_check_status_interval = android_check_status_interval;
	}

	public HashMap<Integer, ArrayList<DocType>> getSystem_docTypes() {
		return system_docTypes;
	}

	public void setSystem_docTypes(
			HashMap<Integer, ArrayList<DocType>> system_docTypes) {
		this.system_docTypes = system_docTypes;
	}

}
