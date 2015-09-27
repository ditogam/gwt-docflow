package com.docflow.client.components.corector;

import java.util.HashMap;
import java.util.Map;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;

public class WCorector extends Window {
	private DynamicForm dfCorector;

	private ListGrid lgSchedule;

	private Record record;
	ListGrid lg;

	public WCorector(Record record, ListGrid lg) {
		this.record = record;
		this.lg = lg;
		TextItem tiName = new TextItem("name", "Server Name");
		tiName.setRequired(true);
		TextItem tiIp = new TextItem("ip", "IP(Address)");
		tiIp.setRequired(true);
		IntegerItem iiPort = new IntegerItem("port", "Server port");
		iiPort.setRequired(true);
		SelectItem siConfigXML = new SelectItem("config_xml_id", "Config xml");
		ClientUtils.fillCombo(siConfigXML, "MeterDeviceConfigDS", null, "id",
				"descript");
		siConfigXML.setRequired(true);
		SelectItem siRegion = new SelectItem("region_id", "Region");
		siRegion.setRequired(true);
		SelectItem siSubRegion = new SelectItem("subregion_id", "Sub region");
		siSubRegion.setRequired(true);

		ClientUtils.fillSelectionCombo(siRegion, ClSelection.T_REGION);
		ClientUtils.makeDependancy(siRegion, true, new FormItemDescr(
				siSubRegion));

		dfCorector = new DynamicForm();
		dfCorector.setNumCols(4);
		dfCorector.setFields(tiName, tiIp, iiPort, siConfigXML, siRegion,
				siSubRegion);
		lgSchedule = new ListGrid();
		lgSchedule.setDataSource(DocFlow.getDataSource("MDScheduleDS"));
		lgSchedule.setFetchOperation("fetchMeterDeviceSchedule");

		Map<String, Object> regionCrit = new HashMap<String, Object>();

		if (record != null && record.getAttributeAsInt("id") != null) {
			Integer meter_device_id = record.getAttributeAsInt("id");
			Criteria cr = new Criteria();
			cr.setAttribute("meter_device_id", meter_device_id);
			regionCrit.put("parentId", record.getAttributeAsInt("region_id"));
			lgSchedule.setCriteria(cr);
			lgSchedule.fetchData(cr);
		} else {
			lgSchedule.fetchData();
			iiPort.setValue(8090);
		}

		ClientUtils.fillSelectionCombo(siSubRegion, ClSelection.T_SUBREGION,
				regionCrit);
		// ListGridField lgfSelected=lgSchedule.getField("default_selected");
		// lgfSelected.setCanEdit(canEdit)

		SavePanel savePanel = new SavePanel("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}

		});
		VLayout vlMain = new VLayout();
		vlMain.setHeight100();
		vlMain.setWidth100();
		VLayout hlMain = new VLayout();
		hlMain.setHeight100();
		hlMain.setWidth100();
		hlMain.addMember(dfCorector);
		hlMain.addMember(lgSchedule);

		vlMain.addMember(hlMain);
		this.setHeight(450);
		this.setWidth(520);
		this.setTitle("Add/Edit Corector");
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		vlMain.addMember(savePanel);
		this.addItem(vlMain);
		if (record != null)
			dfCorector.setValues(record.toMap());
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void saveData() {
		if (!dfCorector.validate())
			return;
		String scedule = "";
		RecordList rcs = lgSchedule.getRecordList();
		for (int i = 0; i < rcs.getLength(); i++) {
			Record record = rcs.get(i);
			Boolean b = record.getAttributeAsBoolean("default_selected");
			if (b == null || !b.booleanValue())
				continue;
			Integer val = record.getAttributeAsInt("sh_time");
			if (scedule.length() > 0)
				scedule += ",";
			scedule += val;
		}
		if (scedule.length() == 0) {
			SC.warn("Please select schedule!!!!");
			return;
		}
		if (record == null)
			record = new Record();
		Map<String, Object> map = ClientUtils.fillMapFromForm(record.toMap(),
				dfCorector);
		map.put("shcedule", scedule);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				destroy();

			}
		};

		try {
			if (map.containsKey("id"))
				lg.updateData(new Record(map), cb);
			else
				lg.addData(new Record(map), cb);
		} catch (Exception e) {
			SC.warn(e.getMessage());
		}
	}
}
