package com.rdcommon.client.components.ds;

import java.util.ArrayList;
import java.util.TreeMap;

import com.rdcommon.client.CommonDialog;
import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class PDSGroupTree extends VLayout {
	private static final int START_NODE = 2;
	private TreeGrid tgGroups;
	private ToolStripButton tsbAdd;
	private ToolStripButton tsbEdit;
	private ToolStripButton tsbDelete;
	private int maxid;
	private PGroupEdit groupEdit;

	public PDSGroupTree(DSGroup[] groups, final PGroupEdit groupEdit) {
		if (groups == null)
			groups = new DSGroup[] {};
		this.groupEdit = groupEdit;
		tgGroups = new TreeGrid();
		tgGroups.setWidth100();
		tgGroups.setHeight100();
		TreeGridField tgfName = new TreeGridField("name");
		tgGroups.setCanReorderRecords(true);
		tgGroups.setCanAcceptDroppedRecords(true);
		tgGroups.setShowOpenIcons(false);
		tgGroups.setDropIconSuffix("into");
		tgGroups.setClosedIconSuffix("");
		tgGroups.setData(createGroupTree(groups));
		tgGroups.setFields(tgfName);
		tgGroups.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				groupEdit.setSelectionChanged(tgGroups.getSelectedRecord());

			}
		});
		ToolStrip toolStrip = new ToolStrip();
		this.addMember(toolStrip);
		this.addMember(tgGroups);

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();
				ttsbuttonClick(objectSource);
			}

		};
		tsbAdd = createTSButton("[SKIN]/actions/add.png", tsbStateHandler,
				SelectionType.BUTTON, "Add Object", "selection", toolStrip);
		tsbAdd.setSelected(false);

		tsbEdit = createTSButton("[SKIN]/actions/edit.png", tsbStateHandler,
				SelectionType.BUTTON, "Edit Object", "selection", toolStrip);
		tsbEdit.setSelected(false);

		tsbDelete = createTSButton("[SKIN]/actions/remove.png",
				tsbStateHandler, SelectionType.BUTTON, "Remove Object",
				"selection", toolStrip);
		tsbDelete.setSelected(false);

	}

	protected void ttsbuttonClick(Object objectSource) {
		final Record record = tgGroups.getSelectedRecord();
		if (record == null)
			return;
		int id = record.getAttributeAsInt("id");
		// DSGroup dsGroup = (DSGroup) record.getAttributeAsObject("dsGroup");
		if (tsbAdd.equals(objectSource)) {
			addEditData(id, null, (DSGroupNode) record);
			return;
		}
		if (id == START_NODE)
			return;
		if (tsbDelete.equals(objectSource)) {
			if (id != START_NODE)
				SC.ask("Do you want to delete group?", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (!value)
							return;
						tgGroups.removeData(record);
					}
				});
			return;
		}
		if (tsbEdit.equals(objectSource)) {
			addEditData(id, record, null);
		}

	}

	private void addEditData(int id, Record record, DSGroupNode parent) {
		new CommonDialog(new DSGroupName(this, tgGroups, id, record, parent),
				true, 90, 270, "Group " + (record == null ? " Add" : "Edit"))
				.show();
	}

	private Tree createGroupTree(DSGroup[] groups) {
		Tree grTree = new Tree();
		grTree.setModelType(TreeModelType.PARENT);
		grTree.setRootValue(1);
		grTree.setNameProperty("name");
		grTree.setIdField("id");
		grTree.setParentIdField("parent_id");
		grTree.setOpenProperty("isOpen");
		DSGroupNode rootNode = new DSGroupNode(START_NODE, 1, null);
		ArrayList<DSGroupNode> groupData = new ArrayList<DSGroupNode>();
		maxid = recretateData(groupData, groups, START_NODE);
		groupData.add(rootNode);
		grTree.setData(groupData.toArray(new DSGroupNode[] {}));
		return grTree;
	}

	private int recretateData(ArrayList<DSGroupNode> groupData,
			DSGroup[] groups, int parent_id) {
		int p_node_id = parent_id;
		for (DSGroup dsGroup : groups) {
			groupData.add(new DSGroupNode(++p_node_id, parent_id, dsGroup));
			if (dsGroup.getDsGroups() != null) {
				p_node_id = recretateData(groupData, dsGroup.getDsGroups()
						.toArray(new DSGroup[] {}), p_node_id);
			}
		}
		return p_node_id;
	}

	private ToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler,
			SelectionType actionType, String toolTip, String group_id,
			ToolStrip toolStrip) {
		ToolStripButton tsbButton = new ToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setRadioGroup(group_id);
		toolStrip.addButton(tsbButton);
		return tsbButton;
	}

	public int getMaxId() {
		return ++maxid;
	}

	private void recteateTree(
			TreeMap<Integer, TreeMap<Integer, ArrayList<TreeNode>>> map,
			TreeNode[] nodes, Integer p_id) {
		TreeMap<Integer, ArrayList<TreeNode>> child = map.get(p_id);
		if (child == null) {
			child = new TreeMap<Integer, ArrayList<TreeNode>>();
			map.put(p_id, child);
		}
		for (TreeNode treeNode : nodes) {
			if (treeNode instanceof DSGroupNode) {
				Integer parent_id = treeNode.getAttributeAsInt("parent_id");
				if (parent_id == null || parent_id.intValue() != p_id)
					continue;
				Integer id = treeNode.getAttributeAsInt("id");

			}
		}
	}

	public ArrayList<DSGroup> getData() {
		Tree tree = tgGroups.getTree();

		TreeNode[] nodes = tree.getAllNodes();
		TreeMap<Integer, DSGroup> allNodes = new TreeMap<Integer, DSGroup>();
		for (TreeNode treeNode : nodes) {
			if (treeNode instanceof DSGroupNode) {
				Integer id = treeNode.getAttributeAsInt("id");
				if (id == null || id.intValue() <= START_NODE)
					continue;
				Integer parent_id = treeNode.getAttributeAsInt("parent_id");
				if (parent_id == null)
					continue;
				DSGroup group = (DSGroup) treeNode
						.getAttributeAsObject("dsGroup");
				if (group == null)
					continue;
				group.setDsGroups(new ArrayList<DSGroup>());
				allNodes.put(id, group);
			}
		}
		ArrayList<DSGroup> groups = new ArrayList<DSGroup>();
		for (TreeNode treeNode : nodes) {
			if (treeNode instanceof DSGroupNode) {
				Integer parent_id = treeNode.getAttributeAsInt("parent_id");
				if (parent_id == null)
					continue;
				DSGroup group = (DSGroup) treeNode
						.getAttributeAsObject("dsGroup");
				if (parent_id.intValue() == START_NODE) {
					groups.add(group);
				} else {
					DSGroup parrentGroup = allNodes.get(parent_id);
					if (parrentGroup == null)
						continue;
					if (parrentGroup.getDsGroups() == null)
						parrentGroup.setDsGroups(new ArrayList<DSGroup>());
					parrentGroup.getDsGroups().add(group);
				}
			}
		}

		// TreeMap<Integer, TreeMap<Integer, ArrayList<TreeNode>>> map = new
		// TreeMap<Integer, TreeMap<Integer, ArrayList<TreeNode>>>();
		// recteateTree(map, nodes, 1);

		return groups;
	}
}
