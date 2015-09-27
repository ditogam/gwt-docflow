package com.docflow.client.components.hr;

import java.util.List;
import java.util.TreeMap;

import com.docflow.client.DocFlow;
import com.docflow.shared.hr.Responcibilities;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class LGJobResponcibilities extends VLayout {

	private ListGrid listGrid;
	private TreeMap<String, Responcibilities> resps;

	private SelectItem siResponcibilityType;
	private LanguageItem liIDescription;

	public LGJobResponcibilities() {

		siResponcibilityType = new SelectItem("siResponcibilityType",
				DocFlow.getCaption(-50, "responcibility_type"));
		liIDescription = new LanguageItem("liIDescription", DocFlow.getCaption(
				-50, "description"));

		DynamicForm df = new DynamicForm();
		df.setNumCols(3);
		df.setTitleOrientation(TitleOrientation.TOP);

		PickerIcon piAdd = new PickerIcon(new Picker("[SKIN]/actions/add.png"));
		PickerIcon piRemove = new PickerIcon(new Picker(
				"[SKIN]/actions/add.png"));

		StaticTextItem sItem = new StaticTextItem("sItem", "");
		sItem.setWidth(1);
		sItem.setIcons(piAdd, piRemove);
		df.setFields(siResponcibilityType, liIDescription, sItem);
		df.setWidth100();
		df.setHeight("10%");
		addMember(df);

		listGrid = new ListGrid();
		resps = new TreeMap<String, Responcibilities>();
		listGrid.setWrapCells(true);

		ListGridField resp_type = new ListGridField("resp_type ",
				DocFlow.getCaption(-50, "resp_type"));
		resp_type.setWidth(150);

		ListGridField resp_description = new ListGridField("resp_description ",
				DocFlow.getCaption(-50, "resp_description"));
		resp_description.setType(ListGridFieldType.INTEGER);
		resp_description.setWidth(250);

		listGrid.setFields(resp_type, resp_description);
		addMember(listGrid);
	}

	public void setResponcibilities(List<Responcibilities> responcibilities) {
		listGrid.setData(new Record[] {});
		Record[] recs = new Record[responcibilities.size()];
		for (int i = 0; i < recs.length; i++) {
			recs[i] = new Record();
			setDataRecord(recs[i], responcibilities.get(i), true);
		}
		listGrid.setData(recs);
	}

	public void setDataRecord(Record rec, Responcibilities item, boolean newItem) {
		if (newItem) {
			String uid = HTMLPanel.createUniqueId();
			rec.setAttribute("uid", uid);
			resps.put(uid, item);
		}
		rec.setAttribute("resp_type", item.getItem_type());
		rec.setAttribute("resp_description", item.getDescription());
	}
}
