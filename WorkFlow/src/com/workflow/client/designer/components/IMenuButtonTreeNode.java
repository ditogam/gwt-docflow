package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.menu.IMenuButton;
import com.workflow.client.Utils;

public class IMenuButtonTreeNode extends ComponentTreeNode {

	public IMenuButtonTreeNode(String Id, String parentId) {
		super(Utils.getClassSimpleName(IMenuButton.class).toLowerCase(), parentId,
				Utils.getClassSimpleName(IMenuButton.class), false, "menu.png");
		setAttribute(CLASS_NAME_ATTRIBUTE, IMenuButton.class.getName());
	}

	public IMenuButtonTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

}
