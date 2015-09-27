package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSFormDefinition;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridField;

public class GRDSForm extends GridControl {

	private String title;
	private DataSource dsDataSourceFields;
	private int field_type;

	public GRDSForm(DSFormDefinition[] formDefinitions, String title,
			int field_type, DataSource dsDataSourceFields) {
		super(formDefinitions);
		this.dsDataSourceFields = dsDataSourceFields;
		this.title = title;
		this.field_type = field_type;
	}

	@Override
	protected int[] getWindowHW() {
		return new int[] { 515, 530 };
	}

	@Override
	protected String getWindowTitle() {
		return title;
	}

	@Override
	protected CommonSavePanel getCommonSavePanel(GridControl mainControl,
			Object val, Record record) {
		return new PDSFormSave(this, (DSFormDefinition)val, field_type, dsDataSourceFields, record);
	}

	@Override
	protected void setOtherValues(Object val, Record record) {
		DSFormDefinition form = (DSFormDefinition) val;
		record.setAttribute("name", form.getName());
	}

	@Override
	protected ListGridField[] getFields() {
		return new ListGridField[] { new ListGridField("name", "Form name") };
	}

}
