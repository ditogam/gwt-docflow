package com.docflowdroid.common.comp;

import android.app.Activity;

import com.docflow.shared.common.FieldDefinition;

public interface IFormDefinitionPanel {

	int validateDate(IFormItem formItem, FieldDefinition fieldDef);

	Activity getContextActivity();

}
