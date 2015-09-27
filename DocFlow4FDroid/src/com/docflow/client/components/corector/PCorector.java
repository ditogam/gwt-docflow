package com.docflow.client.components.corector;

import com.docflow.client.DocFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PCorector extends VLayout {

	private ListGrid lgCorrectors;

	public PCorector() {

		ToolStrip ts = new ToolStrip();
		final ToolStripButton tsbAdd = new ToolStripButton("",
				"[SKIN]/actions/add.png");
		ts.addButton(tsbAdd);
		final ToolStripButton tsbEdit = new ToolStripButton("",
				"[SKIN]/actions/approve.png");
		ts.addButton(tsbEdit);
		ClickHandler ch = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getSource().equals(tsbAdd))
					addData();
				if (event.getSource().equals(tsbEdit))
					editData();

			}
		};
		tsbAdd.addClickHandler(ch);
		tsbEdit.addClickHandler(ch);
		this.addMember(ts);

		// this.addMember(df);

		lgCorrectors = new ListGrid();
		lgCorrectors.setDataSource(DocFlow.getDataSource("MeterDeviceDS"));
		lgCorrectors.setAutoFetchData(true);
		// lgCorrectors.setFetchOperation("fetchMeterDevices");
		// lgCorrectors.setAddOperation("addCorector");
		// lgCorrectors.setUpdateOperation("updateCorector");
		lgCorrectors.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				editData();

			}
		});

		// lgCorrectors.setFields(new ListGridField("name", "Name"),
		// new ListGridField("ip", "IP"), new ListGridField("port",
		// "Reg", 50), new ListGridField("type", "Type", 50),
		// new ListGridField("rw", "ReadWrite", 100));
		lgCorrectors.setHeight100();
		this.addMember(lgCorrectors);
	}

	protected void editData() {
		if (lgCorrectors.getSelectedRecord() == null)
			return;
		new WCorector(lgCorrectors.getSelectedRecord(), lgCorrectors).show();

	}

	protected void addData() {
		new WCorector(null, lgCorrectors).show();

	}

	protected void getDataValues() {
		// TODO Auto-generated method stub

	}
}
