package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Window;

public class PDSFieldSave extends CommonSavePanel {

	private PDSField field;
	private GRDSFields fields;
	private Record record;

	public PDSFieldSave(GRDSFields fields, Record record, DSField obj) {
		field = new PDSField(obj);
		this.fields = fields;
		this.record = record;
		field.setHeight100();
		field.setWidth100();
		this.addMember(field);
	}

	@Override
	public void saveData(Window win) throws Exception {
		Record r = record;
		boolean insert = r == null;
		if (r == null)
			r = new Record();
		DSField obj = field.saveData();
		fields.setDataValue(obj, r);

		if (insert)
			fields.listGrid.addData(r);
		else
			fields.listGrid.refreshRow(fields.listGrid.getRecordIndex(r));

	}

}
