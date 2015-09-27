package com.rdcommon.shared.ds;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSClientFieldDef extends DSComponent implements IsSerializable {

	private int type;
	private String groupName;
	private String actualType;
	private String parentFieldName;
	private String parentFieldValueName;

	private String changeHandlerMethode;
	private String defaultValue;

	private String dsName;
	private Boolean dsIsCustomGenerated;
	private String dsFunction;
	private String dsIdField;
	private String dsValueField;
	private transient DSField parentField;

	public int getType() {
		if (type == 0)
			type = 1;
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getActualType() {
		return actualType;
	}

	public void setActualType(String actualType) {
		this.actualType = actualType;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getDsFunction() {
		return dsFunction;
	}

	public void setDsFunction(String dsFunction) {
		this.dsFunction = dsFunction;
	}

	public String getDsIdField() {
		return dsIdField;
	}

	public void setDsIdField(String dsIdField) {
		this.dsIdField = dsIdField;
	}

	public String getDsValueField() {
		return dsValueField;
	}

	public void setDsValueField(String dsValueField) {
		this.dsValueField = dsValueField;
	}

	public String getParentFieldName() {
		return parentFieldName;
	}

	public void setParentFieldName(String parentFieldName) {
		this.parentFieldName = parentFieldName;
	}

	public DSField getParentField() {
		return parentField;
	}

	public void setParentField(DSField parentField) {
		this.parentField = parentField;
	}

	public String getChangeHandlerMethode() {
		return changeHandlerMethode;
	}

	public void setChangeHandlerMethode(String changeHandlerMethode) {
		this.changeHandlerMethode = changeHandlerMethode;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public void mergeProps(DSCProp p, DSCProp o) {
		super.mergeProps(p, o);
		DSClientFieldDef p1 = (DSClientFieldDef) p;
		DSClientFieldDef o1 = (DSClientFieldDef) o;

		if (o1.groupName != null)
			this.groupName = o1.groupName;
		else
			this.groupName = p1.groupName;

		if (o1.type != 0)
			this.type = o1.type;
		else
			this.type = p1.type;

		if (o1.dsIsCustomGenerated != null)
			this.dsIsCustomGenerated = o1.dsIsCustomGenerated;
		else
			this.dsIsCustomGenerated = p1.dsIsCustomGenerated;

		if (o1.actualType != null)
			this.actualType = o1.actualType;
		else
			this.actualType = p1.actualType;

		if (o1.dsName != null)
			this.dsName = o1.dsName;
		else
			this.dsName = p1.dsName;

		if (o1.dsFunction != null)
			this.dsFunction = o1.dsFunction;
		else
			this.dsFunction = p1.dsFunction;

		if (o1.dsIdField != null)
			this.dsIdField = o1.dsIdField;
		else
			this.dsIdField = p1.dsIdField;

		if (o1.dsValueField != null)
			this.dsValueField = o1.dsValueField;
		else
			this.dsValueField = p1.dsValueField;

		if (o1.parentFieldName != null)
			this.parentFieldName = o1.parentFieldName;
		else
			this.parentFieldName = p1.parentFieldName;

		if (o1.parentFieldValueName != null)
			this.parentFieldValueName = o1.parentFieldValueName;
		else
			this.parentFieldValueName = p1.parentFieldValueName;

		if (o1.defaultValue != null)
			this.defaultValue = o1.defaultValue;
		else
			this.defaultValue = p1.defaultValue;

		if (o1.changeHandlerMethode != null)
			this.changeHandlerMethode = o1.changeHandlerMethode;
		else
			this.changeHandlerMethode = p1.changeHandlerMethode;

	}

	public Boolean getDsIsCustomGenerated() {
		return dsIsCustomGenerated;
	}

	public void setDsIsCustomGenerated(Boolean dsIsCustomGenerated) {
		this.dsIsCustomGenerated = dsIsCustomGenerated;
	}

	public String getParentFieldValueName() {
		return parentFieldValueName;
	}

	public void setParentFieldValueName(String parentFieldValueName) {
		this.parentFieldValueName = parentFieldValueName;
	}

}
