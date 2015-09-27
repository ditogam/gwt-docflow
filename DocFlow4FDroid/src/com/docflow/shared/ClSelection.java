package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClSelection implements IsSerializable {

	/**
	 * 
	 */

	public static boolean reloaded = false;

	private int type;
	private String sql;
	private int parenttype;
	private String dbName;
	private String dsName;

	public static final String SMARTGWTDATASOURCES = "smartgwtdatasources";
	public static final String SMARTGWTDATASOURCE_CONTENT = "ds_definiton";

	public static final int T_NONE = -1;
	public static final int T_REGION = 1;
	public static final int T_SUBREGION = 2;
	public static final int T_CITY = 3;
	public static final int T_STREET = 4;
	public static final int T_HOME_TYPE = 5;
	public static final int T_CUST_BUISNESS = 6;
	public static final int T_CUST_TYPE = 7;
	public static final int T_CUST_STATUS = 8;
	public static final int T_CUST_CLASS = 9;
	public static final int T_TARIF_PLAN = 10;
	public static final int T_CUST_SCOPE = 11;
	public static final int T_GAS_PRESS = 12;
	public static final int T_CUST_LOG_TYPE = 13;
	public static final int T_LANGUAGE = 14;
	public static final int T_DOC_STATUS = 15;
	public static final int T_CAPTIONS = 16;
	public static final int T_METTER_TYPE = 17;
	public static final int T_DOC_TYPE_GROUP = 18;
	public static final int T_BANKS = 19;
	public static final int T_COR_TYPE = 20;
	public static final int T_METTER_STATUS = 21;
	public static final int T_ZONES = 22;
	public static final int T_STANDARTTARIFPLAN = 23;
	public static final int T_PLOMB_STATUS = 24;
	public static final int T_PLOMB_PLACE = 25;
	public static final int T_RESTRUCTURE_TYPE = 26;
	public static final int T_RESTRUCTURE_STATUS = 27;
	public static final int T_PLOMB_COLOR = 28;
	public static final int T_PLOMB_DISTRIBUTOR = 29;
	public static final int T_ZONES_NEW = 30;
	public static final int T_CITY_NEW = 31;
	public static final int T_DEMAGE_TYPES = 32;
	public static final int T_BOPER_ACC_TYPE = 33;
	public static final int T_YES_NO = 34;
	public static final int T_BLOCKS = 35;
	public static final int T_SYSTEMS = 40;

	public static ClSelection[] SELECTIONS = new ClSelection[] {
			new ClSelection(
					T_REGION,
					"select ppcityid id,ppcityname cvalue from ppcity order by 2",
					T_NONE, "Gass"),
			new ClSelection(
					T_SUBREGION,
					"select pcityid id,pcityname cvalue, ppcityid parentId from pcity  order by 3, 2",
					T_REGION, "Gass"),
			new ClSelection(
					T_CITY,
					"select cityid id,cityname cvalue, pcityid parentId from city  order by 3, 2",
					T_SUBREGION, "Gass"),
			new ClSelection(
					T_STREET,
					"select streetid id,streetname cvalue, cityid parentId from street order by 3, 2",
					T_CITY, "Gass"),
			new ClSelection(
					T_ZONES,
					"select zone id,zone::text cvalue, pcityid parentId from v_zones order by 3, 1 desc",
					T_SUBREGION, "Gass"),
			new ClSelection(
					T_HOME_TYPE,
					"select 0 id, 'კორპუსი' cvalue union all select 1 , 'კერძო' cvalue",
					T_NONE, "Gass"),
			new ClSelection(T_STANDARTTARIFPLAN,
					"select 1 id, 'სტანდარტული' cvalue", T_NONE, "Gass"),
			new ClSelection(
					T_CUST_BUISNESS,
					"select id,\"name\" cvalue from customer_business order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_CUST_TYPE,
					"select custypeid id,custypename cvalue from custype order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_CUST_STATUS,
					"select cusstatusid id,cusstatusname cvalue from cusstatus order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_CUST_CLASS,
					"select  id,classname cvalue from cusclass order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_TARIF_PLAN,
					"select id,  cvalue, parentId from feeder.v_tarif_by_regions",
					T_REGION, "Gass"),
			new ClSelection(
					T_CUST_SCOPE,
					"select  id,\"name\" cvalue from customer_scope order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_GAS_PRESS,
					"select  id,pressname cvalue from gasspress order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_CUST_LOG_TYPE,
					"select  id,\"name\" cvalue from customer_log_type order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_LANGUAGE,
					"select id, language_name cvalue from languages order by 1",
					T_NONE, "DocFlow"),
			new ClSelection(
					T_DOC_STATUS,
					"select id, docstatuscaptionvalue cvalue, statuslang parentId from doc_status_v order by 3,1",
					T_LANGUAGE, "DocFlow"),
			new ClSelection(
					T_CAPTIONS,
					"select id, caption_value cvalue, language_id parentId from captions order by 3,2",
					T_LANGUAGE, "DocFlow"),
			new ClSelection(
					T_METTER_TYPE,
					"select mtypeid id, mtypename cvalue from mtype where statusid=1  order by 1 desc",
					T_NONE, "Gass"),
			new ClSelection(
					T_DOC_TYPE_GROUP,
					"select distinct group_id id, doctypegroupvalue cvalue, typelang parentId from doc_type_v where hidden=false order by 3,1",
					T_LANGUAGE, "DocFlow"),
			new ClSelection(
					T_BANKS,
					"select bankid id ,bname cvalue from banks where bankid>=1000 order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_COR_TYPE,
					"select cortypeid id, cortypename cvalue from cortype order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_METTER_STATUS,
					"select mstatusid id, name cvalue from mstatus order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_PLOMB_STATUS,
					"select id, name cvalue from plomb_status order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_PLOMB_PLACE,
					"select id, name cvalue from plomb_place order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_RESTRUCTURE_TYPE,
					"select id, name cvalue from nb_restructure_type order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_RESTRUCTURE_STATUS,
					"select id, restructure_type cvalue from nb_restructure_status order by 1",
					T_NONE, "Gass"),
			new ClSelection(
					T_PLOMB_COLOR,
					"select id, name cvalue from plomb_color where id!=1 order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_PLOMB_DISTRIBUTOR,
					"select id, name cvalue from plomb_distributor order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_ZONES_NEW,
					"select id, cvalue,parentId from v_zones_new order by 1",
					T_SUBREGION, "Gass"),
			new ClSelection(
					T_CITY_NEW,
					"select id, cvalue, parentId from v_subregions_new order by 1",
					T_SUBREGION, "Gass"),
			new ClSelection(
					T_DEMAGE_TYPES,
					"select demage_type_id id, demage_type_name cvalue from demage_type order by 1",
					T_NONE, "MAP"),
			new ClSelection(
					T_BOPER_ACC_TYPE,
					"select id, nm cvalue from feeder.boper_acc_type order by 1",
					T_NONE, "Gass"),
			new ClSelection(T_YES_NO,
					"select id, name cvalue from yesno order by 1", T_NONE,
					"DocFlow"),
			new ClSelection(T_BLOCKS,
					"select id, name cvalue from blocks order by 1", T_NONE,
					"Gass") };

	public ClSelection() {

	}

	// private static String getTypeName(int value) {
	// Field[] fields = ClSelection.class.getFields();
	// for (Field field : fields) {
	// if (field.getType().equals(Integer.TYPE)
	// && Modifier.isStatic(field.getModifiers())
	// && Modifier.isFinal(field.getModifiers())) {
	// try {
	// Object obj = field.get(null);
	// int val = Integer.parseInt(obj.toString());
	// if (val == value)
	// return field.getName();
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	// return "";
	// }

	// public static void main(String[] args) {
	// for (ClSelection selection : SELECTIONS) {
	// System.out.println("insert into clselection values("
	// + selection.type + ",'" + getTypeName(selection.getType())
	// + "','" + selection.sql.replaceAll("'", "''") + "',"
	// + selection.parenttype + ",'" + selection.dbName + "');");
	// }
	//
	// }

	public ClSelection(int type, String sql, int parenttype, String dbName) {
		this.type = type;
		this.sql = sql;
		this.parenttype = parenttype;
		this.dbName = dbName;
		this.dsName = dbName;

	}

	public String getDbName() {
		return dbName;
	}

	public int getParenttype() {
		return parenttype;
	}

	public String getSql() {
		return sql;
	}

	public int getType() {
		return type;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getID() {
		return getTypeNameDS(type);
	}

	public static final String DS_PREFIX = "CDS__";
	public static final String DBCDS_PREFIX = "DBCDS_";
	public static final String DEV_PREFIX = "DEV_";
	public static final String DEV_DBCDS_PREFIX = DEV_PREFIX + DBCDS_PREFIX;

	public static final String getTypeNameDS(int type) {
		return DS_PREFIX + type;
	}

	public String createXML() {
		String id = getID();
		String sw = "";
		sw += ("<DataSource ID=\"" + id + "\" serverType=\"sql\" dbName=\""
				+ dsName + "\" >\n");
		sw += ("<fields>\n");
		sw += ("	<field name=\"id\" type=\"integer\" title=\"ID\" primaryKey=\"true\" hidden=\"true\" />\n");
		sw += ("	<field name=\"cvalue\" type=\"text\" title=\"cvalue\" />\n");
		if (parenttype != T_NONE)
			sw += ("	<field name=\"parentId\" type=\"integer\" title=\"parentId\" />\n");
		sw += ("</fields>\n");
		sw += ("<operationBindings>\n");
		sw += ("	<operationBinding operationType=\"fetch\" operationId=\"fetchSelections\">\n");
		sw += ("		<tableClause> <![CDATA[ (" + sql + ") " + id + " ]]></tableClause>");
		sw += ("	</operationBinding>\n");
		sw += ("</operationBindings>\n");
		sw += ("</DataSource>");
		return sw.toString();
	}

	public static void main(String[] args) {
		for (final ClSelection clSelection : ClSelection.SELECTIONS) {
			System.out.println(clSelection.createXML());

		}
	}
}
