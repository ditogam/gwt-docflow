package com.docflow.client.components.map;

import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class PBuildingsForm extends DynamicForm {

	private SelectItem siRegion;
	public SelectItem siSubregion;
	public TextItem tBuilding_no;
	private IntegerItem tNumOfFloor;
	private TextItem tStreet;

	public PBuildingsForm(Record record, ButtonItem biShowHide,
			boolean needBuildings) {
		if (biShowHide == null) {
			biShowHide = new ButtonItem();
			biShowHide.setVisible(false);
		}

		Integer regid = record.getAttributeAsInt("regid");
		Integer subregionid = record.getAttributeAsInt("raiid");

		Map<String, Object> topCrit = new TreeMap<String, Object>();
		Map<String, Object> regionCrit = new TreeMap<String, Object>();
		siRegion = new SelectItem("regid", DocFlow.getCaption(69));
		siSubregion = new SelectItem("raiid", DocFlow.getCaption(71));
		if (regid != null) {
			regionCrit.put("parentId", regid);
			// siRegion.setDisabled(true);
			topCrit.put("id", regid);
			siRegion.setValue(regid);
		}

		Map<String, Object> raionCrit = new TreeMap<String, Object>();
		if (subregionid != null) {
			raionCrit.put("parentId", subregionid);
			if (regid != null) {
				siSubregion.setDisabled(true);
				siSubregion.setValue(subregionid);
				raionCrit.put("id", subregionid);
			}
		}

		ClientUtils.fillSelectionCombo(siRegion, ClSelection.T_REGION, topCrit);
		ClientUtils.fillSelectionCombo(siSubregion, ClSelection.T_SUBREGION,
				regionCrit);
		ClientUtils.makeDependancy(siRegion, true, new FormItemDescr(
				siSubregion));

		tStreet = new TextItem("street", "Street");
		tStreet.setVisible(needBuildings);
		tBuilding_no = new TextItem("senobis_no", "Building #");
		tBuilding_no.setVisible(needBuildings);
		tNumOfFloor = new IntegerItem("sartuliano", "Num of floors");
		tNumOfFloor.setVisible(needBuildings);
		setNumCols(6);

		setFields(siRegion, siSubregion, tStreet, tBuilding_no, tNumOfFloor,
				biShowHide);
		setValues(record.toMap());
	}
}
