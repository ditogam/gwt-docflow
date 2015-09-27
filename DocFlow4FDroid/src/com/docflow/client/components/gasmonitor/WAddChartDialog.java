package com.docflow.client.components.gasmonitor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.docflow.shared.GasMonitorTemplateItem;
import com.docflow.shared.IMonitorChartType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class WAddChartDialog extends Window {

	private DynamicForm dfMapObject;
	private SelectItem siObjectType;
	private SelectItem siRegion;
	private SelectItem siSubregion;
	private SelectItem siMeters;

	private static WAddChartDialog instance;

	public static void showDialog() {
		if (instance == null)
			instance = new WAddChartDialog();
		instance.show();
	}

	public WAddChartDialog() {

		VLayout vlMain = new VLayout();

		dfMapObject = new DynamicForm();
		if (dfMapObject != null)
			vlMain.addMember(dfMapObject);
		dfMapObject.setNumCols(4);
		Map<String, Object> regionCrit = new TreeMap<String, Object>();
		siObjectType = new SelectItem("siObjectType", "Object type");

		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(IMonitorChartType.REGION + "", "Region");
		valueMap.put(IMonitorChartType.SUB_REGION + "", "Sub region");
		valueMap.put(IMonitorChartType.METER + "", "Meter");
		siObjectType.setValueMap(valueMap);

		siObjectType.setValue(IMonitorChartType.REGION + "");

		siRegion = new SelectItem("regid", DocFlow.getCaption(69));
		siSubregion = new SelectItem("raiid", "Subregion");
		siMeters = new SelectItem("siMeters", "Metters");
		regionCrit.put("parentId", -1);
		Map<String, Object> raionCrit = new TreeMap<String, Object>();
		raionCrit.put("parentId", -1);
		if (DocFlow.user_obj.getUser().getRegionid() > 0) {
			regionCrit
					.put("parentId", DocFlow.user_obj.getUser().getRegionid());
			siRegion.setDisabled(true);
		}

		if (DocFlow.user_obj.getUser().getSubregionid() > 0) {
			raionCrit.put("subregionid", DocFlow.user_obj.getUser()
					.getSubregionid());
			siSubregion.setDisabled(true);
		} else {
			raionCrit.put("subregionid", -100);
		}

		ClientUtils.fillSelectionCombo(siRegion, ClSelection.T_REGION);
		ClientUtils.fillSelectionCombo(siSubregion, ClSelection.T_SUBREGION,
				regionCrit);
		ClientUtils.fillCombo(siMeters, "BuildingsDS", "getMetters", "buid",
				"feature_text", raionCrit);
		ClientUtils.makeDependancy(siRegion, true, new FormItemDescr(
				siSubregion));
		ClientUtils.makeDependancy(siSubregion, true, new FormItemDescr(
				siMeters, "subregionid"));
		dfMapObject.setFields(siObjectType, siRegion, siSubregion, siMeters);
		vlMain.setHeight100();
		vlMain.setWidth100();

		this.addItem(vlMain);

		SavePanel savePanel = new SavePanel("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addData();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}

		});
		vlMain.addMember(savePanel);
		this.setHeight(130);
		this.setWidth(500);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();
		this.setTitle("გრაფიკი");

	}

	protected void addData() {
		SelectItem selectItem = null;
		int type = Integer.parseInt(siObjectType.getValueAsString());
		switch (type) {
		case IMonitorChartType.REGION:
			selectItem = siRegion;
			break;
		case IMonitorChartType.SUB_REGION:
			selectItem = siSubregion;
			break;
		case IMonitorChartType.METER:
			selectItem = siMeters;
			break;
		}
		if (selectItem == null)
			return;

		Object id = selectItem.getValue();
		if (id == null)
			return;
		PMonitor.instance.getMonitorPlace().addChart(
				new GasMonitorTemplateItem(type, new Integer(id.toString())),
				true, true);

	}

}
