package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class TreeGridTreeNode extends CanvasTreeNode {

	public TreeGridTreeNode() {
		super("treegrid", null, "TreeGrid", false, "TreeGrid.png",
				TreeGrid.class);
		setChildren(new TreeNode[] { new TreeGridFieldTreeNode() });
	}

	public TreeGridTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public class TreeGridFieldTreeNode extends ComponentTreeNode {

		public TreeGridFieldTreeNode() {
			super("treegridfield", "treegrid", "TreeGridField", false,
					"text.gif");
			setAttribute(CLASS_NAME_ATTRIBUTE, TreeGridField.class.getName());
		}

		public TreeGridFieldTreeNode(Map<String, String> attributes) {
			super(attributes);
		}

	}
}
