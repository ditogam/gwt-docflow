package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

public class ToolBarMenuTreeNode extends CanvasTreeNode {

	public ToolBarMenuTreeNode(String Id, String parentId, String name,
			String icon, Class<? extends StatefulCanvas> canvas) {
		super(Id, parentId, name, false, icon, canvas);
	}

	public ToolBarMenuTreeNode(String Id, String parentId, String name,
			Class<? extends StatefulCanvas> canvas) {
		super(Id, parentId, name, false, "menu.png", canvas);
	}

	public ToolBarMenuTreeNode(String Id, String parentId,
			Class<? extends StatefulCanvas> canvas) {
		this(Id, parentId, "ToolBarMenuButton", "menu.png", canvas);
	}

	public ToolBarMenuTreeNode(String Id, String parentId) {
		this(Id, parentId, ToolStripMenuButton.class);
	}

	public ToolBarMenuTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

}
