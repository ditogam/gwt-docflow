package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class DSGroupName extends CommonSavePanel {
	private TextItem tfName;
	private TreeGrid tgGroups;
	private int id;
	private Record record;
	private DSGroup dsGroup;
	private PDSGroupTree dsGroupTree;
	private DSGroupNode parentNode;

	public DSGroupName(PDSGroupTree dsGroupTree, TreeGrid tgGroups, int id,
			Record record, DSGroupNode parentNode) {
		this.tgGroups = tgGroups;
		this.parentNode = parentNode;
		this.dsGroupTree = dsGroupTree;
		this.id = id;
		this.record = record;
		if (record != null)
			dsGroup = (DSGroup) record.getAttributeAsObject("dsGroup");
		DynamicForm dmForm = new DynamicForm();
		dmForm.setWidth100();
		dmForm.setHeight100();
		tfName = new TextItem("name", "Group Name");
		tfName.setRequired(true);
		if (dsGroup != null)
			tfName.setValue(dsGroup.getName());
		dmForm.setFields(tfName);
		this.addMember(dmForm);
	}

	@Override
	public void saveData(Window win) throws Exception {
		if (!tfName.validate())
			throw new Exception("Please set group name!!!");
		if (dsGroup == null) {
			dsGroup = new DSGroup();
			dsGroup.setName(tfName.getValueAsString().trim());
			record = new DSGroupNode(dsGroupTree.getMaxId(), id, dsGroup);
			tgGroups.getTree().add((DSGroupNode) record, parentNode);
		} else {
			dsGroup.setName(tfName.getValueAsString().trim());
			record.setAttribute("name", dsGroup.getName());
			tgGroups.refreshRow(tgGroups.getRecordIndex(record));
		}

	}
}
