package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridField;

public class GROperationBindings extends GridControl {

	public GROperationBindings(String[] objects) {
		super(objects);
	}

	@Override
	protected int[] getWindowHW() {
		return new int[] { 480, 530 };
	}

	@Override
	protected String getWindowTitle() {
		return "OperationBinding";
	}

	@Override
	protected void setOtherValues(Object val, Record record) {
		record.setAttribute("operationBind", val.toString());
	}

	@Override
	protected ListGridField[] getFields() {

		return new ListGridField[] { new ListGridField("operationBind",
				"Operation Binding") };
	}

	@Override
	protected CommonSavePanel getCommonSavePanel(GridControl mainControl,
			Object val, Record record) {
		return new POperationBinding(this, record, val);
	}

}
