package com.docflow.shared;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

import com.common.shared.ClSelectionItem;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class Customer implements IsSerializable {

	/**
	 * 
	 */

	private int cusid;
	private String cusname;
	private int streetid;
	private String phone;
	private int custypeid;
	private String job;
	private int hometypeid;
	private double startamount;
	private Timestamp startdate;
	private long ndoc;
	private String home;
	private String flat;
	private String userr;

	private int cusstatusid;
	private int classid;
	private int scope_id;
	private int gasspressid;
	private int business_id;

	private String private_number;
	private String cus_inn;

	private long regionId;
	private ArrayList<ClSelectionItem> subregions;
	private long subregionId;
	private ArrayList<ClSelectionItem> cities;
	private long cityId;
	private ArrayList<ClSelectionItem> streets;

	private long building_id;

	private static transient TreeMap<String, String> typeNames;

	public static void setTypeNames(
			TreeMap<Integer, ArrayList<ClSelectionItem>> types) {
		if (typeNames != null) {
			return;
		}
		Set<Integer> keys = types.keySet();
		typeNames = new TreeMap<String, String>();
		for (Integer key : keys) {
			ArrayList<ClSelectionItem> values = types.get(key);
			for (ClSelectionItem si : values) {
				String nkey = key + "_" + si.getId();
				typeNames.put(nkey, si.getValue());
			}
		}
	}

	private transient TreeMap<Integer, String> typeNMap;

	public void fromXml(String xml) {
		TreeMap<String, String> data = new TreeMap<String, String>();
		Document doc = XMLParser.parse(xml);
		Node rootElem = doc.getChildNodes().item(0);
		NodeList nodeList = rootElem.getChildNodes();

		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				Element el = (Element) n;
				data.put(el.getAttribute("key"), el.getAttribute("value"));

			}
		}
		cusid = getIntValue(data.get("cusid"));
		cusname = getAttribute(data.get("cusname"));
		streetid = getIntValue(data.get("streetid"));
		phone = getAttribute(data.get("phone"));
		custypeid = getIntValue(data.get("custypeid"));
		job = getAttribute(data.get("job"));
		hometypeid = getIntValue(data.get("hometypeid"));
		startamount = getDoubleValue(data.get("startamount"));
		startdate = getTimeValue(data.get("startdate"));
		ndoc = getLongValue(data.get("ndoc"));
		home = getAttribute(data.get("home"));
		flat = getAttribute(data.get("flat"));
		userr = getAttribute(data.get("userr"));
		cusstatusid = getIntValue(data.get("cusstatusid"));
		classid = getIntValue(data.get("classid"));
		scope_id = getIntValue(data.get("scope_id"));
		gasspressid = getIntValue(data.get("gasspressid"));
		business_id = getIntValue(data.get("business_id"));

		regionId = getLongValue(data.get("regionId"));
		subregionId = getLongValue(data.get("subregionId"));
		cityId = getLongValue(data.get("cityId"));

		private_number = getAttribute(data.get("private_number"));
		cus_inn = getAttribute(data.get("cus_inn"));

	}

	private String getAttribute(String value) {
		return getAttribute(value, "");
	}

	private String getAttribute(String value, String def) {

		Object str = null;
		try {
			str = value;
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (str != null) {
			return str.toString();
		}
		return def;
	}

	public long getBuilding_id() {
		return building_id;
	}

	public int getBusiness_id() {
		return business_id;
	}

	public ArrayList<ClSelectionItem> getCities() {
		return cities;
	}

	public long getCityId() {
		return cityId;
	}

	public int getClassid() {
		return classid;
	}

	public String getCus_inn() {
		return cus_inn;
	}

	public int getCusid() {
		return cusid;
	}

	public String getCusname() {
		return cusname;
	}

	public int getCusstatusid() {
		return cusstatusid;
	}

	public int getCustypeid() {
		return custypeid;
	}

	public TreeMap<String, String> getData() {
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("cusid", cusid + "");
		map.put("cusname", cusname);
		map.put("streetid",
				getValueForType(ClSelection.T_STREET, streetid + ""));
		map.put("phone", phone);
		map.put("custypeid",
				getValueForType(ClSelection.T_CUST_TYPE, custypeid + ""));
		map.put("job", job);
		map.put("hometypeid",
				getValueForType(ClSelection.T_HOME_TYPE, hometypeid + ""));
		map.put("startamount", startamount + "");
		map.put("startdate", new Date(startdate.getTime()) + "");
		map.put("ndoc", ndoc + "");
		map.put("home", home);
		map.put("flat", flat);
		map.put("userr", userr);
		map.put("cusstatusid",
				getValueForType(ClSelection.T_CUST_STATUS, cusstatusid + ""));
		map.put("classid", classid + "");
		map.put("scope_id",
				getValueForType(ClSelection.T_CUST_SCOPE, scope_id + ""));
		map.put("gasspressid",
				getValueForType(ClSelection.T_GAS_PRESS, gasspressid + ""));
		map.put("business_id",
				getValueForType(ClSelection.T_CUST_BUISNESS, business_id + ""));

		map.put("regionId",
				getValueForType(ClSelection.T_REGION, regionId + ""));
		map.put("subregionId",
				getValueForType(ClSelection.T_SUBREGION, subregionId + ""));
		map.put("cityId", getValueForType(ClSelection.T_CITY, cityId + ""));

		map.put("private_number", private_number);
		map.put("cus_inn", cus_inn);
		return map;
	}

	public TreeMap<String, Object> getDataObject() {
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		map.put("cusid", cusid + "");
		map.put("cusname", cusname);
		map.put("streetid", streetid + "");
		map.put("phone", phone);
		map.put("custypeid", custypeid + "");
		map.put("job", job);
		map.put("hometypeid", hometypeid + "");
		map.put("startamount", startamount + "");
		map.put("startdate", startdate.getTime() + "");
		map.put("ndoc", ndoc + "");
		map.put("home", home);
		map.put("flat", flat);
		map.put("userr", userr);
		map.put("cusstatusid", cusstatusid + "");
		map.put("classid", classid + "");
		map.put("scope_id", scope_id + "");
		map.put("gasspressid", gasspressid + "");
		map.put("business_id", business_id + "");

		map.put("regionId", regionId + "");
		map.put("subregionId", subregionId + "");
		map.put("cityId", cityId + "");

		map.put("private_number", private_number);
		map.put("cus_inn", cus_inn);
		return map;
	}

	private double getDoubleValue(String value) {
		try {
			return Double.parseDouble(getAttribute(value, "0"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public String getFlat() {
		return flat;
	}

	public int getGasspressid() {
		return gasspressid;
	}

	public String getHome() {
		return home;
	}

	public int getHometypeid() {
		return hometypeid;
	}

	private int getIntValue(String value) {
		try {
			return Integer.parseInt(getAttribute(value, "0"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public String getJob() {
		return job;
	}

	private long getLongValue(String value) {
		try {
			return Long.parseLong(getAttribute(value, "0"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public long getNdoc() {
		return ndoc;
	}

	public String getPhone() {
		return phone;
	}

	public String getPrivate_number() {
		return private_number;
	}

	public long getRegionId() {
		return regionId;
	}

	public int getScope_id() {
		return scope_id;
	}

	public double getStartamount() {
		return startamount;
	}

	public Timestamp getStartdate() {
		return startdate;
	}

	public int getStreetid() {
		return streetid;
	}

	public ArrayList<ClSelectionItem> getStreets() {
		return streets;
	}

	public long getSubregionId() {
		return subregionId;
	}

	public ArrayList<ClSelectionItem> getSubregions() {
		return subregions;
	}

	private Timestamp getTimeValue(String value) {
		try {
			long time = getLongValue(value);
			return new Timestamp(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public TreeMap<Integer, String> getTypeNMap() {
		return typeNMap;
	}

	public TreeMap<Integer, Long> getTypesAndIds() {
		TreeMap<Integer, Long> ret = new TreeMap<Integer, Long>();
		ret.put(ClSelection.T_REGION, regionId);
		ret.put(ClSelection.T_SUBREGION, subregionId);
		ret.put(ClSelection.T_CITY, cityId);
		ret.put(ClSelection.T_STREET, new Long(streetid));
		ret.put(ClSelection.T_CUST_TYPE, new Long(custypeid));
		ret.put(ClSelection.T_HOME_TYPE, new Long(hometypeid));
		ret.put(ClSelection.T_CUST_STATUS, new Long(cusstatusid));
		ret.put(ClSelection.T_CUST_CLASS, new Long(classid));
		ret.put(ClSelection.T_CUST_SCOPE, new Long(scope_id));
		ret.put(ClSelection.T_GAS_PRESS, new Long(gasspressid));
		ret.put(ClSelection.T_CUST_BUISNESS, new Long(business_id));

		return ret;
	}

	public String getUserr() {
		return userr;
	}

	private String getValueForType(int type, String defaultVal) {
		if (typeNMap == null)
			return defaultVal;
		String ret = typeNMap.get(type);
		if (ret == null)
			ret = defaultVal;
		return ret;
	}

	public void setBuilding_id(long building_id) {
		this.building_id = building_id;
	}

	public void setBusiness_id(int business_id) {
		this.business_id = business_id;
	}

	public void setCities(ArrayList<ClSelectionItem> cities) {
		this.cities = cities;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public void setClassid(int classid) {
		this.classid = classid;
	}

	public void setCus_inn(String cus_inn) {
		this.cus_inn = cus_inn;
	}

	public void setCusid(int cusid) {
		this.cusid = cusid;
	}

	public void setCusname(String cusname) {
		this.cusname = cusname;
	}

	public void setCusstatusid(int cusstatusid) {
		this.cusstatusid = cusstatusid;
	}

	public void setCustypeid(int custypeid) {
		this.custypeid = custypeid;
	}

	private void setElement(Document doc, Element rootElem, String key,
			String value) {
		Element val = doc.createElement("Val");
		val.setAttribute("key", key);
		val.setAttribute("value", value);
		rootElem.appendChild(val);
	}

	public void setFlat(String flat) {
		this.flat = flat;
	}

	public void setGasspressid(int gasspressid) {
		this.gasspressid = gasspressid;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public void setHometypeid(int hometypeid) {
		this.hometypeid = hometypeid;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public void setNdoc(long ndoc) {
		this.ndoc = ndoc;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPrivate_number(String private_number) {
		this.private_number = private_number;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public void setScope_id(int scope_id) {
		this.scope_id = scope_id;
	}

	public void setStartamount(double startamount) {
		this.startamount = startamount;
	}

	public void setStartdate(Timestamp startdate) {
		this.startdate = startdate;
	}

	public void setStreetid(int streetid) {
		this.streetid = streetid;
	}

	public void setStreets(ArrayList<ClSelectionItem> streets) {
		this.streets = streets;
	}

	public void setSubregionId(long subregionId) {
		this.subregionId = subregionId;
	}

	public void setSubregions(ArrayList<ClSelectionItem> subregions) {
		this.subregions = subregions;
	}

	public void setTypeNMap(TreeMap<Integer, String> typeNMap) {
		this.typeNMap = typeNMap;
	}

	public void setUserr(String userr) {
		this.userr = userr;
	}

	public String toXml() {
		Document doc = XMLParser.createDocument();
		Element rootElem = doc.createElement("DocDef");
		doc.appendChild(rootElem);
		setElement(doc, rootElem, "cusid", cusid + "");
		setElement(doc, rootElem, "cusname", cusname);
		setElement(doc, rootElem, "streetid", streetid + "");
		setElement(doc, rootElem, "phone", phone);
		setElement(doc, rootElem, "custypeid", custypeid + "");
		setElement(doc, rootElem, "job", job);
		setElement(doc, rootElem, "hometypeid", hometypeid + "");
		setElement(doc, rootElem, "startamount", startamount + "");
		setElement(doc, rootElem, "startdate", startdate.getTime() + "");
		setElement(doc, rootElem, "ndoc", ndoc + "");
		setElement(doc, rootElem, "home", home);
		setElement(doc, rootElem, "flat", flat);
		setElement(doc, rootElem, "userr", userr);
		setElement(doc, rootElem, "cusstatusid", cusstatusid + "");
		setElement(doc, rootElem, "classid", classid + "");
		setElement(doc, rootElem, "scope_id", scope_id + "");
		setElement(doc, rootElem, "gasspressid", gasspressid + "");
		setElement(doc, rootElem, "business_id", business_id + "");

		setElement(doc, rootElem, "regionId", regionId + "");
		setElement(doc, rootElem, "subregionId", subregionId + "");
		setElement(doc, rootElem, "cityId", cityId + "");

		setElement(doc, rootElem, "private_number", private_number);
		setElement(doc, rootElem, "cus_inn", cus_inn);

		String ret = doc.toString();

		return ret;

	}

}
