package com.docflowdroid.common;

import com.docflow.shared.common.FieldDefinition;
import com.docflowdroid.common.comp.IFormItem;

public class FieldDefinitionItem {

	private FieldDefinition fieldDef;
	private IFormItem formItem;

	public FieldDefinitionItem(FieldDefinition fieldDef, IFormItem formItem) {
		setFieldDef(fieldDef);
		setFormItem(formItem);
	}

	public FieldDefinition getFieldDef() {
		return fieldDef;
	}

	public IFormItem getFormItem() {
		return formItem;
	}

	public void setFieldDef(FieldDefinition fieldDef) {
		this.fieldDef = fieldDef;
	}

	public void setFormItem(IFormItem formItem) {
		this.formItem = formItem;
	}

}