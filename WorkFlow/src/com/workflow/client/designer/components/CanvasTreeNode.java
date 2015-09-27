package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.Canvas;

public class CanvasTreeNode extends ComponentTreeNode {

	public CanvasTreeNode(String Id, String parentId, String name,
			boolean isOpen, String icon, Class<? extends Canvas> canvas,
			ComponentTreeNode... children) {
		super(Id, parentId, name, isOpen, icon, children);
		setOtherProps();
		setAttribute(CLASS_NAME_ATTRIBUTE, canvas.getName());

	}

	private void setOtherProps() {
		setCanAcceptDrop(true);
		setCanSelect(true);
		setCanExpand(true);
		setAttribute("_bold", "1");
	}

	public CanvasTreeNode(Map<String, String> attributes) {
		super(attributes);
		setOtherProps();
	}

}
