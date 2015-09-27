package com.rdcommon.client.components.ds;

import java.util.ArrayList;

import com.rdcommon.client.ClientUtils;
import com.rdcommon.client.CommonDialog;
import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSCProp;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public abstract class GridControl extends VLayout {
	protected ListGrid listGrid;

	private ToolStripButton tsbAdd;
	private ToolStripButton tsbEdit;
	private ToolStripButton tsbDelete;
	protected ToolStrip toolStrip;

	public GridControl(Object[] objects) {

		listGrid = new ListGrid();
		listGrid.setFields(getFields());
		toolStrip = new ToolStrip();
		this.addMember(toolStrip);
		this.addMember(listGrid);

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();
				ttsbuttonClick(objectSource);
			}

		};
		tsbAdd = ClientUtils.createTSButton("[SKIN]/actions/add.png",
				tsbStateHandler, SelectionType.BUTTON, "Add Object",
				"selection", toolStrip);
		tsbAdd.setSelected(false);

		tsbEdit = ClientUtils.createTSButton("[SKIN]/actions/edit.png",
				tsbStateHandler, SelectionType.BUTTON, "Edit Object",
				"selection", toolStrip);
		tsbEdit.setSelected(false);

		tsbDelete = ClientUtils.createTSButton("[SKIN]/actions/remove.png",
				tsbStateHandler, SelectionType.BUTTON, "Remove Object",
				"selection", toolStrip);
		tsbDelete.setSelected(false);

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				addEdit(listGrid.getSelectedRecord());
			}
		});
		if (objects != null)
			for (Object dscProp : objects) {
				Record record = new Record();
				setDataValue(dscProp, record);
				listGrid.addData(record);
			}
	}

	protected void addEdit(Record record) {
		Object val = null;
		if (record != null)
			val = record.getAttributeAsObject("_obj");
		int[] hw = getWindowHW();
		new CommonDialog(getCommonSavePanel(this, val, record), true, hw[0],
				hw[1], getWindowTitle()).show();
	}

	protected abstract int[] getWindowHW();

	protected abstract String getWindowTitle();

	protected abstract CommonSavePanel getCommonSavePanel(
			GridControl mainControl, Object val, Record record);

	public void setDataValue(Object val, Record record) {
		record.setAttribute("_obj", val);
		setOtherValues(val, record);
	}

	protected abstract void setOtherValues(Object val, Record record);

	public Object[] getData() {
		ArrayList<Object> data = new ArrayList<Object>();
		RecordList list = listGrid.getRecordList();
		for (int i = 0; i < list.getLength(); i++) {
			Object obj = list.get(i).getAttributeAsObject("_obj");
			data.add(obj);
		}
		return data.toArray();
	}

	protected void ttsbuttonClick(Object objectSource) {
		if (objectSource.equals(tsbAdd))
			addEdit(null);
		final Record record = listGrid.getSelectedRecord();
		if (record == null)
			return;
		if (objectSource.equals(tsbEdit))
			addEdit(record);
		if (objectSource.equals(tsbDelete))
			SC.ask("Do you want to remove data?", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					if (value != null && value) {
						listGrid.deselectAllRecords();
						listGrid.selectRecord(record);
						listGrid.removeSelectedData();
					}
				}
			});

	}

	protected abstract ListGridField[] getFields();

}
