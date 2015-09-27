package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class ToolBarTreeNode extends CanvasTreeNode {

	public ToolBarTreeNode(String Id, String parentId, String name,
			ComponentTreeNode... children) {
		super(Id, parentId, name, false, "toolbar.gif", ToolStrip.class,
				children);
	}

	public ToolBarTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

}
