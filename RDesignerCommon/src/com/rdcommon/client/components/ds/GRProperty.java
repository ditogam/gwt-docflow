package com.rdcommon.client.components.ds;

import java.util.ArrayList;
import java.util.TreeMap;

import com.rdcommon.shared.ds.DSCProp;
import com.smartgwt.client.data.Record;
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

public class GRProperty extends VLayout {
	private ToolStripButton tsbAdd;
	private ToolStripButton tsbEdit;
	private ToolStripButton tsbDelete;
	private ListGrid listGrid;
	private DSCProp prop;
	private int type;

	public GRProperty(DSCProp prop, int type) {
		this.prop = prop;
		this.type = type;
		listGrid = new ListGrid();
		listGrid.setFields(new ListGridField("propname", "Name"),
				new ListGridField("value", "Value"));
		ToolStrip toolStrip = new ToolStrip();
		this.addMember(toolStrip);
		this.addMember(listGrid);

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();
				ttsbuttonClick(objectSource);
			}

		};
		tsbAdd = createTSButton("[SKIN]/actions/add.png", tsbStateHandler,
				SelectionType.BUTTON, "Add Object", "selection", toolStrip);
		tsbAdd.setSelected(false);

		tsbEdit = createTSButton("[SKIN]/actions/edit.png", tsbStateHandler,
				SelectionType.BUTTON, "Edit Object", "selection", toolStrip);
		tsbEdit.setSelected(false);

		tsbDelete = createTSButton("[SKIN]/actions/remove.png",
				tsbStateHandler, SelectionType.BUTTON, "Remove Object",
				"selection", toolStrip);
		tsbDelete.setSelected(false);

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				addEditProperty(listGrid.getSelectedRecord());
			}
		});
		if (prop == null || prop.getAdditionalProps() == null
				|| prop.getAdditionalProps().isEmpty())
			return;

		ArrayList<Record> records = new ArrayList<Record>();
		for (String key : prop.getAdditionalProps().keySet()) {
			Record r = new Record();
			r.setAttribute("propname", key);
			r.setAttribute("value", prop.getAdditionalProps().get(key));
			records.add(r);
		}
		listGrid.setData(records.toArray(new Record[] {}));
	}

	protected void ttsbuttonClick(Object objectSource) {
		if (objectSource.equals(tsbAdd))
			addEditProperty(null);
		final Record record = listGrid.getSelectedRecord();
		if (record == null)
			return;
		if (objectSource.equals(tsbEdit))
			addEditProperty(record);
		if (objectSource.equals(tsbDelete))
			SC.ask("Do you want to remove property?", new BooleanCallback() {

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

	private void addEditProperty(Record record) {
		new DlgPropertyValue(listGrid, type, record).show();
	}

	private ToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler,
			SelectionType actionType, String toolTip, String group_id,
			ToolStrip toolStrip) {
		ToolStripButton tsbButton = new ToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setGroupTitle(group_id);
		toolStrip.addButton(tsbButton);
		return tsbButton;
	}

	public void saveData() {
		prop.setAdditionalProps(new TreeMap<String, String>());
		Record[] records = listGrid.getRecords();
		for (Record record : records) {
			prop.getAdditionalProps().put(record.getAttribute("propname"),
					record.getAttribute("value"));
		}
	}
}
