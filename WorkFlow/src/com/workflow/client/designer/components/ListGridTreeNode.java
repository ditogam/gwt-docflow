package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class ListGridTreeNode extends CanvasTreeNode {

	public ListGridTreeNode() {
		super("listgrid", null, "ListGrid", false, "ListGrid.png",
				ListGrid.class);
		setChildren(new TreeNode[] { new ListGridFieldTreeNode() });
	}

	public ListGridTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public class ListGridFieldTreeNode extends ComponentTreeNode {

		public ListGridFieldTreeNode() {
			super("listgridfield", "listgrid", "ListGridField", false,
					"text.gif");
			setAttribute(CLASS_NAME_ATTRIBUTE, ListGridField.class.getName());
		}

		public ListGridFieldTreeNode(Map<String, String> attributes) {
			super(attributes);
		}

	}
}
