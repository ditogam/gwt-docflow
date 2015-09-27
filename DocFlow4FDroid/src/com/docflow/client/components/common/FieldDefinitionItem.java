package com.docflow.client.components.common;

import com.docflow.shared.common.FieldDefinition;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class FieldDefinitionItem {

	private FieldDefinition fieldDef;
	private FormItem formItem;

	public FieldDefinitionItem(FieldDefinition fieldDef, FormItem formItem) {
		setFieldDef(fieldDef);
		setFormItem(formItem);
	}

	public FieldDefinition getFieldDef() {
		return fieldDef;
	}

	public FormItem getFormItem() {
		return formItem;
	}

	public void setFieldDef(FieldDefinition fieldDef) {
		this.fieldDef = fieldDef;
	}

	public void setFormItem(FormItem formItem) {
		this.formItem = formItem;
	}

}
