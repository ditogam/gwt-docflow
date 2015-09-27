package com.rdcommon.client.components.ds;

import java.util.ArrayList;

import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

public class PGroupEdit extends HLayout {
	private PDSGroupTree tree;
	private PDSGroupData groupData;

	public PGroupEdit(DSGroup[] groups) {
		if (groups == null)
			groups = new DSGroup[] {};
		tree = new PDSGroupTree(groups, this);
		tree.setHeight100();
		tree.setWidth("50%");
		tree.setShowResizeBar(true);
		this.addMember(tree);
		groupData = new PDSGroupData();
		groupData.setHeight100();
		groupData.setWidth("50%");
		this.addMember(groupData);
	}

	public void setSelectionChanged(ListGridRecord selectedRecord) {
		groupData.setGroup((DSGroup) selectedRecord
				.getAttributeAsObject("dsGroup"));
	}
	
	public ArrayList<DSGroup> getData(){
		return tree.getData();
	}
}
