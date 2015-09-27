package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.IButton;
import com.workflow.client.Utils;

public class ButtonTreeNode extends ComponentTreeNode {

	public ButtonTreeNode(String Id, String parentId,
			ComponentTreeNode... children) {
		super(Utils.getClassSimpleName(IButton.class).toLowerCase(), parentId,
				Utils.getClassSimpleName(IButton.class), false, "button.gif",
				children);
		setAttribute(CLASS_NAME_ATTRIBUTE, IButton.class.getName());
	}

	public ButtonTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

}
