package com.workflow.client.designer;

import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderDropEvent;
import com.smartgwt.client.widgets.tree.events.FolderDropHandler;
import com.workflow.client.designer.components.CanvasTreeNode;
import com.workflow.client.designer.components.ComponentTreeNode;

public class PDesigner extends HLayout {
	private VLayout propertyPane;
	private VLayout contentPane;

	public PDesigner() {

		setShowEdges(true);

		propertyPane = new VLayout();
		propertyPane.setWidth("25%");
		propertyPane.setShowEdges(true);
		propertyPane.setShowResizeBar(true);

		contentPane = new VLayout();
		contentPane.setWidth("*");
		contentPane.setShowEdges(true);
		contentPane.setShowResizeBar(true);

		CanvasTreeNode node = new CanvasTreeNode("dataView", null, "Content",
				true, "cubes_blue.gif", VLayout.class);
		node.setAttribute(CanvasTreeNode.UNMOVABLE_ATTRIBUTE,
				CanvasTreeNode.UNMOVABLE_ATTRIBUTE);

		final PComponentTree contentTree = new PComponentTree(
				new TreeNode[] { node }, true);
		contentTree.setCanReorderRecords(true);
		contentTree.setCanAcceptDroppedRecords(true);
		contentTree.setCanReparentNodes(true);
		contentTree.setWidth100();
		contentTree.setHeight("30%");
		contentTree.addFolderDropHandler(new FolderDropHandler() {

			@Override
			public void onFolderDrop(FolderDropEvent event) {
				ComponentTreeNode.acceptMove(event, contentTree);
			}
		});

		PComponentView componentView = new PComponentView();
		componentView.setWidth100();
		componentView.setHeight("*");

		contentTree.setHeight("30%");

		contentTree.addDesignChangedHandler(componentView);

		PPropertyView propView = new PPropertyView();
		propView.setHeight100();
		propView.setWidth100();

		contentTree.addDesignChangedHandler(propView);

		propertyPane.setMembers(propView);

		contentPane.setMembers(componentView, contentTree);
		setMembers(propertyPane, contentPane);

		setWidth100();
		setHeight100();

		draw();
	}
}
