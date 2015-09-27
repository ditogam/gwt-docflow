package com.workflow.client.designer.components;

import com.smartgwt.client.widgets.form.DynamicForm;

public class DynamicFormTreeNode extends CanvasTreeNode {

	public DynamicFormTreeNode(String Id, String parentId, String name,
			boolean isOpen, String icon, ComponentTreeNode... children) {
		super(Id, parentId, name, isOpen, icon, DynamicForm.class, children);
	}

	
}
