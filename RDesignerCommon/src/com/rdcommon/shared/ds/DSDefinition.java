package com.rdcommon.shared.ds;

import java.util.ArrayList;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSDefinition extends DSCProp implements IsSerializable {

	public static final String DSDefinition_EXTN = "DSDEF_";
	public static final String TMP = "_TMP";

	private Integer id;
	private String dsName;
	private String tableName;
	private Boolean dropExtraFields;
	private String serverObjectClassName;

	private ArrayList<DSField> dsFields;
	private ArrayList<String> operationBindings;

	private ArrayList<DSFormDefinition> searchForms;
	private ArrayList<DSFormDefinition> inputForms;
	private ArrayList<DSFormDefinition> grids;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Boolean getDropExtraFields() {
		return dropExtraFields;
	}

	public void setDropExtraFields(Boolean dropExtraFields) {
		this.dropExtraFields = dropExtraFields;
	}

	public ArrayList<DSField> getDsFields() {
		return dsFields;
	}

	public void setDsFields(ArrayList<DSField> dsFields) {
		this.dsFields = dsFields;
	}

	public ArrayList<String> getOperationBindings() {
		return operationBindings;
	}

	public void setOperationBindings(ArrayList<String> operationBindings) {
		this.operationBindings = operationBindings;
	}

	public String getServerObjectClassName() {
		return serverObjectClassName;
	}

	public void setServerObjectClassName(String serverObjectClassName) {
		this.serverObjectClassName = serverObjectClassName;
	}

	public String createDSXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<DataSource ID=\"" + DSDefinition_EXTN + dsName
				+ "\" serverType=\"sql\" tableName=\"" + tableName + "\" ");

		if (additionalProps != null && !additionalProps.isEmpty()) {
			Set<String> keys = additionalProps.keySet();
			for (String key : keys) {
				sb.append(" " + key + "=\"" + additionalProps.get(key) + "\" ");
			}
		}
		sb.append(">\n\t<fields>");
		if (dsFields != null && !dsFields.isEmpty()) {
			for (DSField field : dsFields) {
				sb.append("\n\t\t" + field.createDSXML());
			}
		}
		sb.append("\n\t</fields>");
		if (serverObjectClassName != null
				&& !serverObjectClassName.trim().isEmpty())
			sb.append("\n\t<serverObject lookupStyle=\"new\" className=\""
					+ serverObjectClassName + "\"/>");
		if (operationBindings != null && !operationBindings.isEmpty()) {
			sb.append(">\n\t<operationBindings>");
			for (String operationBinding : operationBindings) {
				sb.append(operationBinding);
			}
			sb.append("\n\t</operationBindings>");
		}
		sb.append("\n</DataSource>");
		return sb.toString();
	}

	public ArrayList<DSFormDefinition> getSearchForms() {
		return searchForms;
	}

	public void setSearchForms(ArrayList<DSFormDefinition> searchForms) {
		this.searchForms = searchForms;
	}

	public ArrayList<DSFormDefinition> getInputForms() {
		return inputForms;
	}

	public void setInputForms(ArrayList<DSFormDefinition> inputForms) {
		this.inputForms = inputForms;
	}

	public ArrayList<DSFormDefinition> getGrids() {
		return grids;
	}

	public void setGrids(ArrayList<DSFormDefinition> grids) {
		this.grids = grids;
	}

	public DSFormDefinition getDSFormDefinition(
			ArrayList<DSFormDefinition> clientFieldDefs, String name) {
		if (clientFieldDefs != null && !clientFieldDefs.isEmpty()) {
			for (DSFormDefinition item : clientFieldDefs) {
				if (item.getName().equals(name))
					return item;
			}
		}
		return null;
	}

	public DSFormDefinition getSearchForm(String name) {
		return getDSFormDefinition(searchForms, name);
	}

	public DSFormDefinition getInputForm(String name) {
		return getDSFormDefinition(inputForms, name);
	}

	public DSFormDefinition getGrid(String name) {
		return getDSFormDefinition(grids, name);
	}

	public DSField getDSField(String name) {
		if (dsFields != null && !dsFields.isEmpty()) {
			for (DSField item : dsFields) {
				if (item.getfName().equals(name))
					return item;
			}
		}
		return null;
	}

	public static void main(String[] args) {

		StringBuffer SQL = new StringBuffer();
		SQL.append("SELECT   groupid        , \n");
		SQL.append("         decode(length(phone),8,5||phone,phone)      phone          , \n");
		SQL.append("         MIN(START_DATE), \n");
		SQL.append("         MAX(END_DATE) \n");
		SQL.append("FROM     (SELECT bg.group_id                          AS groupid   , \n");
		SQL.append("                 CAST(sp.param_value AS VARCHAR2(20)) AS phone     , \n");
		SQL.append("                 rt.DateConvertRev(START_DATE)           START_DATE, \n");
		SQL.append("                 rt.DateConvertRev(END_DATE)             END_DATE \n");
		SQL.append("         FROM    b1.nc_bgroup_items bg \n");
		SQL.append("                 INNER JOIN b1.subscribers s \n");
		SQL.append("                 ON      s.customer_id = ABS(to_number(bg.phone)) \n");
		SQL.append("                 INNER JOIN b1.subs_service_params sp \n");
		SQL.append("                 ON      sp.subscriber_id = s.subscriber_id \n");
		SQL.append("                 AND     sp.param_id      = 1 \n");
		SQL.append("         WHERE   bg.group_id              = ? \n");
		SQL.append("         AND     SYSDATE BETWEEN START_DATE AND     END_DATE \n");
		SQL.append("         AND     bg.phone LIKE '-%' \n");
		SQL.append("          \n");
		SQL.append("         UNION ALL \n");
		SQL.append("          \n");
		SQL.append("         SELECT bg.group_id                   , \n");
		SQL.append("                CAST(decode(length(bg.phone),8,5||bg.phone,bg.phone) AS VARCHAR2(20)), \n");
		SQL.append("                rt.DateConvertRev(START_DATE) , \n");
		SQL.append("                rt.DateConvertRev(END_DATE) \n");
		SQL.append("         FROM   b1.nc_bgroup_items bg \n");
		SQL.append("         WHERE  bg.group_id = ? \n");
		SQL.append("         AND    SYSDATE BETWEEN START_DATE AND    END_DATE \n");
		SQL.append("         AND    bg.phone NOT LIKE '-%' \n");
		SQL.append("          \n");
		SQL.append("         UNION ALL \n");
		SQL.append("          \n");
		SQL.append("         SELECT t.group_id                    , \n");
		SQL.append("                t.item_value                  , \n");
		SQL.append("                rt.DateConvertRev(t.startdate), \n");
		SQL.append("                rt.DateConvertRev(t.enddate) \n");
		SQL.append("         FROM   ccare.group_items t \n");
		SQL.append("         WHERE  t.group_id = ? \n");
		SQL.append("         ) \n");
		SQL.append("GROUP BY groupid, \n");
		SQL.append("         phone");
		System.out.println(SQL);
	}
}
