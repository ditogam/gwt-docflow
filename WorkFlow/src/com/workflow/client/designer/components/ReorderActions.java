package com.workflow.client.designer.components;

import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.workflow.client.designer.PComponentTree;

public class ReorderActions extends HLayout {

	private Tree tree;
	private PComponentTree grid;
	private ListGridRecord node;

	public ReorderActions(Tree tree, PComponentTree grid, ListGridRecord node) {
		this.grid = grid;
		this.tree = tree;
		this.node = node;

		final ImgButton btnAdd = createButton("add.png", "Add item");
		btnAdd.setDisabled(!ComponentTreeNode.isCanvas(node));
		btnAdd.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addItemList(btnAdd);

			}
		});
		addMember(btnAdd);

		final boolean disabled = ComponentTreeNode.isUnmovable(node);

		final ImgButton btnUp = createButton("up.png", "Item Up");
		btnUp.setDisabled(disabled);
		addMember(btnUp);

		final ImgButton btnDown = createButton("down.png", "Item Down");
		btnDown.setDisabled(disabled);
		addMember(btnDown);

		setButtonMoveUpDownButtonsDisabled(btnUp, btnDown, disabled);

		btnUp.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveUpDown(btnUp, btnDown, true, disabled);

			}
		});
		btnDown.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveUpDown(btnUp, btnDown, false, disabled);

			}
		});

		ImgButton btnRemove = createButton("remove.png", "Item Remove");
		btnRemove.setDisabled(disabled);
		btnRemove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SC.ask("Do you want to delete record?", new BooleanCallback() {

					@Override
					public void execute(Boolean value) {
						if (value != null && value)
							ReorderActions.this.tree
									.remove(ReorderActions.this.node);
						ReorderActions.this.grid
								.dataChanged(ReorderActions.this.node);
					}
				});

			}
		});
		addMember(btnRemove);

		setAutoHeight();
		setAutoWidth();

	}

	private int getMyIndex(TreeNode[] children) {
		int index = -1;
		String src_id = node.getAttribute(ComponentTreeNode.ID_ATTRIBUTE);
		for (TreeNode treeNode : children) {
			index++;
			String dest_id = treeNode
					.getAttribute(ComponentTreeNode.ID_ATTRIBUTE);
			if (src_id.equals(dest_id)) {
				return index;
			}
		}
		return index;

	}

	private void setButtonMoveUpDownButtonsDisabled(ImgButton btnUp,
			ImgButton btnDown, boolean disabled) {
		if (disabled) {
			btnDown.setDisabled(disabled);
			btnUp.setDisabled(disabled);
			redraw();
			return;
		}
		TreeNode curr_node = (TreeNode) node;
		TreeNode parent_node = tree.getParent(curr_node);
		if (parent_node == null) {
			setButtonMoveUpDownButtonsDisabled(btnUp, btnDown, true);
			redraw();
			return;
		}

		TreeNode[] children = tree.getChildren(parent_node);

		if (children == null || children.length == 0) {
			setButtonMoveUpDownButtonsDisabled(btnUp, btnDown, true);
			redraw();
			return;
		}
		int my_index = getMyIndex(children);
		if (my_index <= 0)
			btnUp.setDisabled(true);
		if (my_index < 0 || my_index == children.length - 1)
			btnDown.setDisabled(true);
		redraw();
	}

	private void moveUpDown(ImgButton btnUp, ImgButton btnDown, boolean up,
			boolean disabled) {
		try {
			TreeNode curr_node = (TreeNode) node;
			TreeNode parent_node = tree.getParent(curr_node);
			if (parent_node == null) {
				return;
			}

			TreeNode[] children = tree.getChildren(parent_node);
			if (children == null || children.length == 0) {
				return;
			}
			int my_index = getMyIndex(children);
			for (int i = 0; i < children.length; i++) {
				tree.remove(children[i]);
				if (i == my_index) {
					TreeNode tmp = children[i];
					int mult = up ? -1 : 1;
					children[i] = children[i + mult * 1];
					children[i + mult * 1] = tmp;
					grid.dataChanged(children[i], children[i + mult * 1]);
					break;
				}
			}
			tree.addList(children, parent_node);
			grid.selectSingleRecord(node);

		} finally {
			setButtonMoveUpDownButtonsDisabled(btnUp, btnDown, disabled);
		}
	}

	private ImgButton createButton(String imgName, String comment) {
		ImgButton btnAdd = new ImgButton();
		btnAdd.setShowDown(false);
		btnAdd.setShowRollOver(false);
		btnAdd.setLayoutAlign(Alignment.CENTER);
		btnAdd.setSrc("actions/" + imgName);
		btnAdd.setPrompt(comment);
		btnAdd.setHeight(16);
		btnAdd.setWidth(16);
		return btnAdd;
	}

	void addItemList(ImgButton btn) {
		final Dialog componentsTreeDialog = new Dialog();
		componentsTreeDialog.setAutoCenter(true);
		componentsTreeDialog.setIsModal(true);
		componentsTreeDialog.setShowHeader(false);
		componentsTreeDialog.setShowEdges(false);
		componentsTreeDialog.setEdgeSize(10);

		componentsTreeDialog.setShowToolbar(false);
		componentsTreeDialog.setWidth(300);
		componentsTreeDialog.setHeight(500);

		String src_parent_id = node
				.getAttribute(ComponentTreeNode.PARENT_ID_ATTRIBUTE);
		String src_id = node.getAttribute(ComponentTreeNode.ID_ATTRIBUTE);

		final PComponentTree componentsTree = new PComponentTree(null, false);
		componentsTree.setWidth100();
		componentsTree.setHeight100();
		componentsTree.setAlternateRecordStyles(true);

		TreeNode[] nodes = componentsTree.getData().getAllNodes();

		for (TreeNode treeNode : nodes) {

			String dest_parent_id = treeNode
					.getAttribute(ComponentTreeNode.PARENT_ID_ATTRIBUTE);
			String dest_id = treeNode
					.getAttribute(ComponentTreeNode.ID_ATTRIBUTE);

			if (src_id.equals(dest_id)) {
				treeNode.setEnabled(false);
				continue;
			}

			if (dest_parent_id != null && src_parent_id != null
					&& dest_parent_id.equals(src_parent_id)
					&& !dest_parent_id.equals("1")
					&& !dest_parent_id.equals("1")) {
				treeNode.setEnabled(false);
				continue;
			}

			boolean enabled = false;

			enabled = ComponentTreeNode.canAcceptType(node, treeNode);

			if (!enabled) {
				treeNode.setEnabled(false);
				continue;
			}

		}

		componentsTreeDialog.addMember(componentsTree);
		componentsTreeDialog.setDismissOnOutsideClick(true);
		componentsTreeDialog
				.addVisibilityChangedHandler(new VisibilityChangedHandler() {

					@Override
					public void onVisibilityChanged(VisibilityChangedEvent event) {
						if (!event.getIsVisible())
							componentsTreeDialog.destroy();

					}
				});

		componentsTree.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				TreeNode selectedRecord = componentsTree.getSelectedRecord();
				if (selectedRecord == null)
					return;

				selectedRecord = new ComponentTreeNode(ComponentTreeNode
						.cloneAttributes(selectedRecord));
				setNewData((TreeNode) node, selectedRecord);

			}
		});

		Rectangle iconRect = btn.getPageRect();
		int topadd = iconRect.getTop() + 3 + iconRect.getHeight();
		componentsTreeDialog.show();
		componentsTreeDialog.moveTo(iconRect.getLeft() - 100, topadd);
	}

	private void setNewData(TreeNode parentNode, TreeNode child) {
		String parent_id = parentNode
				.getAttribute(ComponentTreeNode.ID_ATTRIBUTE);
		child.setAttribute(ComponentTreeNode.PARENT_ID_ATTRIBUTE, parent_id);
		tree.add(child, parentNode);
		grid.updateData(child);
		grid.updateData(parentNode);
		tree.openFolder(parentNode);
		grid.selectSingleRecord(child);
		grid.dataChanged(parentNode, child);
	}

}
