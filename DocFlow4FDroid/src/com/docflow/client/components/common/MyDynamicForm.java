package com.docflow.client.components.common;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.form.DynamicForm;

public class MyDynamicForm extends DynamicForm {
	private boolean newLine;

	public MyDynamicForm() {
//		setShowEdges(true);
	}

	public MyDynamicForm(JavaScriptObject jsObj) {
		super(jsObj);
		// TODO Auto-generated constructor stub
	}

	public boolean isNewLine() {
		return newLine;
	}

	public void setNewLine(boolean newLine) {
		this.newLine = newLine;
	}

}
