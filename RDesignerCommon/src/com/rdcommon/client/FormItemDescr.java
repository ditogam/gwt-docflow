package com.rdcommon.client;

import java.util.Map;

import com.smartgwt.client.widgets.form.fields.FormItem;

public class FormItemDescr {
	public FormItem formItem;
	public String parentName;
	public Map<?, ?> aditionalCriteria;
	public String valueSet;

	public FormItemDescr(FormItem formItem) {
		this(formItem, "parentId");
	}

	public FormItemDescr(FormItem formItem, String parentName) {
		this(formItem, parentName, null);
	}

	public FormItemDescr(FormItem formItem, String parentName, String valueSet) {
		this(formItem, parentName, null, valueSet);
	}

	public FormItemDescr(FormItem formItem, String parentName,
			Map<?, ?> aditionalCriteria, String valueSet) {
		this.formItem = formItem;
		this.parentName = parentName;
		this.aditionalCriteria = aditionalCriteria;
		this.valueSet = valueSet;
	}
}
