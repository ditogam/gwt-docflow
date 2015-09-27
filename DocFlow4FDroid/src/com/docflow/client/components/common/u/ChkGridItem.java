package com.docflow.client.components.common.u;

import java.util.ArrayList;

import com.common.shared.ClSelectionItem;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ChkGridItem extends CanvasItem {

	private ListGrid lg;
	private String value = null;

	private class ListDS extends DataSource {
		public ListDS() {
			super();
			setClientOnly(true);
			DataSourceIntegerField id = new DataSourceIntegerField("id");
			id.setHidden(true);
			id.setPrimaryKey(true);
			setFields(id, new DataSourceBooleanField("chk"),
					new DataSourceField("text", FieldType.TEXT));
			DataSourceField df = getPrimaryKeyField();
			System.out.println(df);
			String fieldNames[] = getFieldNames();
			for (String fn : fieldNames) {
				getField(fn).setCanEdit(fn.equals("chk"));
			}
			setTestData();
		}
	}

	public ChkGridItem() {
		super();
		// setWidth("*");
		// setHeight("*");
		// setColSpan("*");
		// setEndRow(true);
		// setStartRow(true);
		lg = new ListGrid();
		lg.setWidth100();
		lg.setHeight100();
		lg.setDataSource(new ListDS());
		lg.setCanEdit(true);
		lg.setShowHeader(false);
		setCanvas(lg);

	}

	@Override
	protected Canvas createCanvas() {
		return super.createCanvas();
	}

	// @Override
	// public void setWidth(String width) {
	// super.setWidth(width);
	// if (lg != null)
	// lg.setWidth100();
	// }
	//
	// @Override
	// public void setHeight(String height) {
	// super.setHeight(height);
	// if (lg != null)
	// lg.setHeight100();
	// }
	//
	// @Override
	// public void setWidth(int width) {
	// super.setWidth(width);
	// if (lg != null)
	// lg.setWidth100();
	// }
	//
	// @Override
	// public void setHeight(int height) {
	// super.setHeight(height);
	// if (lg != null)
	// lg.setHeight100();
	// }

	@Override
	public void setValue(Object value) {
		setValue(value == null ? (String) null : value.toString());
	}

	@Override
	public void setValue(String value) {
		this.value = value;
		if (lg == null)
			return;
		value = value == null ? "" : value.trim();
		String values[] = value.split(",");
		Record[] recs = lg.getDataSource().getTestData();
		for (Record r : recs) {
			String id = r.getAttributeAsString("id");
			boolean chk = false;
			for (String v : values) {
				if (v.equals(id)) {
					chk = true;
					break;
				}
			}
			r.setAttribute("chk", chk);
			lg.getDataSource().updateData(r);
		}
	}

	@Override
	public void setCanEdit(Boolean canEdit) {
		super.setCanEdit(canEdit);
		if (lg == null)
			return;
		if (canEdit != null)
			lg.setDisabled(!canEdit);
	}

	@Override
	public void setDisabled(Boolean disabled) {
		super.setDisabled(disabled);
		if (lg == null)
			return;
		if (disabled != null)
			lg.setDisabled(disabled);
	}

	@Override
	public Object getValue() {
		return getValues("id");
	}

	@Override
	public String getDisplayValue() {
		return getValues("text");
	}

	private String getValues(String field) {
		String ret = "";
		Record[] recs = lg.getDataSource().getTestData();
		for (Record r : recs) {
			Boolean chk = r.getAttributeAsBoolean("chk");
			if (chk != null && chk.booleanValue()) {
				if (!ret.isEmpty())
					ret += ",";
				ret += r.getAttributeAsString(field);

			}
		}
		return ret;
	}

	public void setItems(ArrayList<ClSelectionItem> items) {
		if (lg == null)
			return;
		DataSource ds = lg.getDataSource();
		// ds.setTestData();
		// for (Record rec : recs) {
		// ds.removeData(rec);
		// }
		ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
		for (ClSelectionItem item : items) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("id", item.getId());
			rec.setAttribute("chk", false);
			rec.setAttribute("text", item.getValue());
			records.add(rec);
			// ds.addData(rec);
		}
		ds.setTestData(records.toArray(new ListGridRecord[] {}));
		lg.fetchData();
		lg.invalidateCache();

		setValue(value);
	}
}
