package com.workflow.client.designer.components;

import java.util.Map;

public class BasicTreeNode extends ComponentTreeNode {

	public BasicTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public BasicTreeNode(String Id, String parentId, String name,
			boolean isOpen, String icon,
			ComponentTreeNode... children) {
		super(Id, parentId, name, isOpen, icon, children);
	}

	

}
