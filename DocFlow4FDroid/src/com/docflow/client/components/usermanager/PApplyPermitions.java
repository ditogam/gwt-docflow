package com.docflow.client.components.usermanager;

import java.util.ArrayList;

import com.docflow.shared.common.PermitionItem;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class PApplyPermitions extends VLayout {

	private ListGrid lgPermitions;

	public PApplyPermitions(ArrayList<PermitionItem> permitionItems) {

		ListGridField lgfperm_id = new ListGridField("id");
		lgfperm_id.setHidden(true);
		lgfperm_id.setType(ListGridFieldType.INTEGER);
		lgfperm_id.setCanEdit(false);

		ListGridField lgfperm_name = new ListGridField("name", "უფლება", 200);
		lgfperm_name.setCanEdit(false);
		ListGridField lgfperm_applyed = new ListGridField("applyed", "ჩართვა",
				50);
		lgfperm_applyed.setType(ListGridFieldType.BOOLEAN);
		lgfperm_applyed.setCanEdit(true);
		ListGridField lgfperm_denied = new ListGridField("denied",
				"გამონაკლისი", 50);
		lgfperm_denied.setType(ListGridFieldType.BOOLEAN);
		lgfperm_denied.setCanEdit(true);

		lgPermitions = new ListGrid();
		lgPermitions.setCanEdit(true);
		lgPermitions.setFields(lgfperm_id, lgfperm_name, lgfperm_applyed,
				lgfperm_denied);
		ListGridRecord[] records = new ListGridRecord[permitionItems.size()];
		int i = 0;
		for (PermitionItem permitionItem : permitionItems) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("id", permitionItem.getPermition_id());
			rec.setAttribute("name", permitionItem.getPermitionName());
			rec.setAttribute("applyed", permitionItem.isApplyed());
			rec.setAttribute("denied", permitionItem.isDenied());
			records[i] = rec;
			i++;
		}
		lgPermitions.setRecords(records);
		this.addMember(lgPermitions);
	}

	public String getSelectedPermitions() {
		String ret = "";
		RecordList records = lgPermitions.getRecordList();
		int size = records.getLength();
		for (int i = 0; i < size; i++) {
			Record rec = records.get(i);
			boolean applyed = rec.getAttributeAsBoolean("applyed");
			boolean denied = rec.getAttributeAsBoolean("denied");
			if (denied || applyed) {
				String s = "" + rec.getAttributeAsInt("id");
				if (denied)
					s = "-" + s;
				if (ret.length() > 0)
					ret += ",";
				ret += s;
			}
		}

		return ret;
	}
}
