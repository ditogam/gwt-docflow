package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeNode;

public class MenuTreeNode extends CanvasTreeNode {

	public MenuTreeNode() {
		super("menu", null, "Menu", false, "menu.png", Menu.class);
		setChildren(new TreeNode[] { new MenuItemTreeNode() });
	}

	public MenuTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

	public class MenuItemTreeNode extends ComponentTreeNode {

		public MenuItemTreeNode() {
			super("menuitem", "menu", "MenuItem", false, "menu_item.png");
			setAttribute(CLASS_NAME_ATTRIBUTE, MenuItem.class.getName());
		}

		public MenuItemTreeNode(Map<String, String> attributes) {
			super(attributes);
		}

	}
}
