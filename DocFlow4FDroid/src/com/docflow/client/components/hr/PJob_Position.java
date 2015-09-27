package com.docflow.client.components.hr;

import com.common.client.WindowResultObject;
import com.docflow.client.DocFlow;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class PJob_Position extends VLayout implements WindowResultObject {
	private static String IMG_TYPE = getImgType();

	private static native String getImgType() /*-{
												var imgType = $wnd.isc.pickerImgType;
												return imgType == null || imgType === undefined ? "png" : imgType;
												}-*/;

	private LanguageItem tiPosition_name;
	private LanguageAreaItem tiPosition_description;
	private SelectItem siPerson_id;
	private PResponcibilities responcibilities;
	public static PJob_Position job_Position;

	public PJob_Position() {
		job_Position = this;
		tiPosition_name = new LanguageItem("position_name", DocFlow.getCaption(
				-50, "დასახელება"));
		tiPosition_description = new LanguageAreaItem("position_description",
				DocFlow.getCaption(-50, "აღწერა"));
		ListGrid pickListProperties = new ListGrid();
		pickListProperties.setShowFilterEditor(true);

		ListGridField person_id = new ListGridField("person_id");
		person_id.setHidden(true);
		ListGridField person_name = new ListGridField("person_name");
		siPerson_id = new SelectItem("person_id", "პიროვნება");
		siPerson_id.setOptionDataSource(DocFlow.getDataSource("PersonShortDS"));
		siPerson_id.setDisplayField("person_name");
		siPerson_id.setValueField("person_id");
		siPerson_id.setPickListWidth(300);
		siPerson_id.setPickListFields(person_id, person_name);
		siPerson_id.setPickListProperties(pickListProperties);
		siPerson_id.setFetchMissingValues(true);
		siPerson_id.setOptionCriteria(new Criteria("language_id", ""
				+ DocFlow.language_id));

		PickerIcon piEdit = new PickerIcon(new Picker("[SKIN]/actions/edit."
				+ IMG_TYPE), new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				Integer id = null;
				try {
					id = new Integer(siPerson_id.getValue().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
				WPerson.showForm(id, PJob_Position.this);

			}
		});
		siPerson_id.setIcons(piEdit);
		ToolStrip tsmenu = new ToolStrip();
		tsmenu.setWidth100();
		tsmenu.setHeight("5%");
		ToolStripButton tsbSave = new ToolStripButton("Save",
				"icons/16/approved.png");
		tsmenu.addButton(tsbSave);
		tsbSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveData();

			}
		});
		this.addMember(tsmenu);

		DynamicForm df = new DynamicForm();
		df.setTitleOrientation(TitleOrientation.TOP);
		df.setNumCols(2);
		df.setHeight("30%");
		df.setWidth100();
		df.setDataSource(DocFlow.getDataSource("JobPositionDS"));

		df.setFields(tiPosition_name, tiPosition_description, siPerson_id);
		FormItem[] fi = df.getFields();
		for (FormItem formItem : fi) {
			formItem.setRequired(true);
		}
		responcibilities = new PResponcibilities();
		responcibilities.setWidth100();
		responcibilities.setHeight100();
		responcibilities.setShowEdges(true);
		this.addMember(df);
		this.addMember(responcibilities);

	}

	protected void saveData() {
		DynamicForm df = tiPosition_name.getForm();

		if (!df.validate())
			return;

		// final Integer id = (Integer) df.getValue("id");
		df.saveData();

	}

	public void setId(int id, TreeGrid tree, Record rec) {
		Criteria cr = new Criteria("id", "" + id);
		cr.setAttribute("language_id", DocFlow.language_id);
		tiPosition_name.getForm().fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] recs = response.getData();
				if (recs == null)
					return;
				if (recs.length == 0)
					return;
				tiPosition_name.setValue(
						recs[0].getAttributeAsLong("position_name_id"),
						recs[0].getAttribute("position_name"));
				tiPosition_description.setValue(
						recs[0].getAttributeAsLong("position_description_id"),
						recs[0].getAttribute("position_description"));

			}
		});

		responcibilities.setItemId(id, 2);
	}

	@Override
	public void setResult(Object obj) {
		siPerson_id.setValue(obj);

	}

}
