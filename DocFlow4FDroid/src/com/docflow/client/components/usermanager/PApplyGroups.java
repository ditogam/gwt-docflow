package com.docflow.client.components.usermanager;

import java.util.ArrayList;

import com.docflow.shared.common.User_Group;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class PApplyGroups extends VLayout {

	private ListGrid lgGroups;

	public PApplyGroups(ArrayList<User_Group> user_Groups) {
		ListGridField lgfperm_id = new ListGridField("id");
		lgfperm_id.setHidden(true);
		lgfperm_id.setType(ListGridFieldType.INTEGER);
		lgfperm_id.setCanEdit(false);

		ListGridField lgfgroup_name = new ListGridField("name", "ჯგუფი", 200);
		lgfgroup_name.setCanEdit(false);
		ListGridField lgfgroup_applyed = new ListGridField("applyed", "ჩართვა",
				50);
		lgfgroup_applyed.setType(ListGridFieldType.BOOLEAN);
		lgfgroup_applyed.setCanEdit(true);
		lgGroups = new ListGrid();
		lgGroups.setCanEdit(true);
		lgGroups.setFields(lgfperm_id, lgfgroup_name, lgfgroup_applyed);
		ListGridRecord[] records = new ListGridRecord[user_Groups.size()];
		int i = 0;
		for (User_Group user_group : user_Groups) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("id", user_group.getGroup_id());
			rec.setAttribute("name", user_group.getGname());
			rec.setAttribute("applyed", user_group.isApplyed());
			records[i] = rec;
			i++;
		}
		lgGroups.setRecords(records);
		this.addMember(lgGroups);
	}

	public String getSelectedGroups() {
		String ret = "";
		RecordList records = lgGroups.getRecordList();
		int size = records.getLength();
		for (int i = 0; i < size; i++) {
			Record rec = records.get(i);
			boolean applyed = rec.getAttributeAsBoolean("applyed");

			if (applyed) {
				String s = "" + rec.getAttributeAsInt("id");
				if (ret.length() > 0)
					ret += ",";
				ret += s;
			}
		}

		return ret;
	}
}
