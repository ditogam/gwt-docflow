package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.TreeNode;

public class TabSetTreeNode extends CanvasTreeNode {

	public TabSetTreeNode() {
		super("tabset", null, "TabSet", false, "TabSet.png", TabSet.class);
		setChildren(new TreeNode[] { new TabTreeNode() });
	}

	public TabSetTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public class TabTreeNode extends ComponentTreeNode {

		public TabTreeNode() {
			super("tab", "tabset", "Tab", false, "Tab.png");
			setAttribute(CLASS_NAME_ATTRIBUTE, Tab.class.getName());
		}

		public TabTreeNode(Map<String, String> attributes) {
			super(attributes);
		}

	}
}
