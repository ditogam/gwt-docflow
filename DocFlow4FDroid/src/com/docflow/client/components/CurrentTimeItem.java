package com.docflow.client.components;

import com.docflow.client.DocFlow;
import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.form.fields.DateItem;

public class CurrentTimeItem extends DateItem {

	public CurrentTimeItem() {
		setCurrentDate();
	}

	public CurrentTimeItem(JavaScriptObject jsObj) {
		super(jsObj);
		setCurrentDate();
	}

	public CurrentTimeItem(String name) {
		super(name);
		setCurrentDate();
	}

	public CurrentTimeItem(String name, String title) {
		super(name, title);
		setCurrentDate();
	}

	private void setCurrentDate() {
		setValue(DocFlow.getCurrentDate());
	}

	@Override
	public void setValue(Object value) {
		if (value == null)
			setCurrentDate();
		else {
			super.setValue(value);
		}
	}
}
