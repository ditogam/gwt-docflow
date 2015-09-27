package com.rdcommon.client.components.ds;

import java.util.ArrayList;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSField;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;

public class GRDSFields extends GridControl {

	private DataSource dsDataSourceFields;

	public GRDSFields(DSField[] fields) {
		super(fields);
	}

	@Override
	protected int[] getWindowHW() {
		return new int[] { 515, 530 };
	}

	@Override
	protected String getWindowTitle() {
		return "DS Field";
	}

	@Override
	protected CommonSavePanel getCommonSavePanel(GridControl mainControl,
			Object val, Record record) {

		return new PDSFieldSave(this, record, (DSField) val);
	}

	@Override
	protected void setOtherValues(Object val, Record record) {
		DSField field = (DSField) val;
		record.setAttribute("fName", field.getfName());
		record.setAttribute("fTitle", field.getfTitle());
		record.setAttribute("primaryKey", field.getPrimaryKey());
	}

	@Override
	protected ListGridField[] getFields() {
		ListGridField primaryKey = new ListGridField("primaryKey", "Is PK");
		primaryKey.setType(ListGridFieldType.BOOLEAN);
		return new ListGridField[] { new ListGridField("fName", "Field name"),
				new ListGridField("fTitle", "Field title"), primaryKey };
	}

	public DataSource getDataSource() {
		if (dsDataSourceFields == null) {
			dsDataSourceFields = new DataSource();
			dsDataSourceFields.setClientOnly(true);
			ArrayList<DataSourceField> dsFields = new ArrayList<DataSourceField>();
			ListGridField[] grFields = listGrid.getFields();
			for (ListGridField listGridField : grFields) {
				FieldType ft = FieldType.TEXT;
						
//						FieldType
//						.valueOf(listGridField.getType() == null ? FieldType.TEXT
//								.getValue() : listGridField.getType()
//								.getValue());
				if (ft == null)
					ft = FieldType.TEXT;
				DataSourceField f = new DataSourceField(
						listGridField.getName(), ft, listGridField.getTitle());
				dsFields.add(f);
			}
			dsDataSourceFields.setFields(dsFields
					.toArray(new DataSourceField[] {}));

		}
		dsDataSourceFields.setTestData();
		int lngth = listGrid.getRecordList().getLength();
		Record[] records = new Record[lngth];
		for (int i = 0; i < lngth; i++) {
			records[i] = listGrid.getRecordList().get(i);
		}
		dsDataSourceFields.setTestData(records);
		return dsDataSourceFields;
	}
}
