package com.rdcommon.shared.ds;

import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSField extends DSCProp implements IsSerializable {

	private String fName;
	private String fTitle;
	private Boolean primaryKey;

	private DSClientFieldDef searchProps;
	private DSClientFieldDef formProps;
	private DSClientFieldDef gridProps;
	private DSClientFieldDef exportProps;

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getfTitle() {
		return fTitle;
	}

	public void setfTitle(String fTitle) {
		this.fTitle = fTitle;
	}

	public Boolean getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public DSClientFieldDef getSearchProps() {
		return searchProps;
	}

	public void setSearchProps(DSClientFieldDef searchProps) {
		this.searchProps = searchProps;
	}

	public DSClientFieldDef getFormProps() {
		return formProps;
	}

	public void setFormProps(DSClientFieldDef formProps) {
		this.formProps = formProps;
	}

	public DSClientFieldDef getGridProps() {
		return gridProps;
	}

	public void setGridProps(DSClientFieldDef gridProps) {
		this.gridProps = gridProps;
	}

	public DSClientFieldDef getExportProps() {
		return exportProps;
	}

	public void setExportProps(DSClientFieldDef exportProps) {
		this.exportProps = exportProps;
	}

	public String createDSXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<field name=\"" + fName + "\"  ");
		if (primaryKey != null && primaryKey)
			sb.append(" primaryKey=\"true\" ");
		if (additionalProps != null && !additionalProps.isEmpty()) {
			Set<String> keys = additionalProps.keySet();
			for (String key : keys) {
				sb.append(" " + key + "=\"" + additionalProps.get(key) + "\" ");
			}
		}
		sb.append("/>");
		return sb.toString();
	}

}
