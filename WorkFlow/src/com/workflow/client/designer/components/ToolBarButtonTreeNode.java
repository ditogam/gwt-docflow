package com.workflow.client.designer.components;

import java.util.Map;

import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class ToolBarButtonTreeNode extends CanvasTreeNode {

	public ToolBarButtonTreeNode(String Id, String parentId, String name,
			String icon, Class<? extends StatefulCanvas> canvas) {
		super(Id, parentId, name, false, icon, canvas);
	}

	public ToolBarButtonTreeNode(String Id, String parentId, String name,
			Class<? extends StatefulCanvas> canvas) {
		super(Id, parentId, name, false, "button.gif", canvas);
	}

	public ToolBarButtonTreeNode(String Id, String parentId,
			Class<? extends StatefulCanvas> canvas) {
		this(Id, parentId, "ToolBarButton", "button.gif", canvas);
	}

	public ToolBarButtonTreeNode(String Id, String parentId) {
		this(Id, parentId, ToolStripButton.class);
	}

	public ToolBarButtonTreeNode(Map<String, String> attributes) {
		super(attributes);
	}

}
