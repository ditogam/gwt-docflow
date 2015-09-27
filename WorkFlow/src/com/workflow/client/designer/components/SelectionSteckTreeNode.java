package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.tree.TreeNode;

public class SelectionSteckTreeNode extends CanvasTreeNode {

	public SelectionSteckTreeNode() {
		super("sectionstack", null, "SectionStack", false, "TabSet.png",
				SectionStack.class);
		setChildren(new TreeNode[] { new SectionStackSectionTreeNode() });
	}

	public SelectionSteckTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public class SectionStackSectionTreeNode extends ComponentTreeNode {

		public SectionStackSectionTreeNode() {
			super("sectionstackselection", "sectionstack", "Selection", false,
					"Tab.png");
			setAttribute(CLASS_NAME_ATTRIBUTE,
					SectionStackSection.class.getName());
		}

		public SectionStackSectionTreeNode(Map<String, String> attributes) {
			super(attributes);
		}

	}
}
