package com.docflow.client.components.hr;

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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class PStructure extends VLayout {

	private LanguageItem structure_name;
	private StaticTextItem parent_department;
	private IntegerItem max_substructures_count;
	private IntegerItem max_position_count;
	private LanguageAreaItem job_description;

	private PResponcibilities responcibilities;

	public static PStructure structure;

	public PStructure() {
		structure = this;
		structure_name = new LanguageItem("structure_name", DocFlow.getCaption(
				-50, "დასახელება"));
		max_substructures_count = new IntegerItem("max_substructures_count",
				DocFlow.getCaption(-50, "მაქს.სტრუქტურ. რაოდ."));
		max_position_count = new IntegerItem("max_position_count",
				DocFlow.getCaption(-50, "მაქს.პოზიც. რაოდ."));
		parent_department = new StaticTextItem("parent_department",
				DocFlow.getCaption(-50, "მშობელი სტრუქტურა"));
		job_description = new LanguageAreaItem("job_description",
				DocFlow.getCaption(-50, "აღწერა"));

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
		df.setDataSource(DocFlow.getDataSource("StructureDS"));

		df.setShowEdges(true);

		max_substructures_count.setStartRow(true);
		max_substructures_count.setEndRow(false);
		max_position_count.setStartRow(false);
		max_substructures_count.setEndRow(true);
		job_description.setStartRow(true);
		job_description.setEndRow(true);
		df.setFields(structure_name, max_substructures_count,
				max_position_count, parent_department, job_description);
		FormItem[] fi = df.getFields();
		for (FormItem formItem : fi) {
			formItem.setRequired(true);
		}
		parent_department.setRequired(false);
		responcibilities = new PResponcibilities();
		responcibilities.setWidth100();
		responcibilities.setHeight100();
		responcibilities.setShowEdges(true);
		this.addMember(df);
		this.addMember(responcibilities);

		// responcibilities.hide();
	}

	protected void saveData() {
		DynamicForm df = structure_name.getForm();

		if (!df.validate())
			return;

		// final Integer id = (Integer) df.getValue("id");
		df.saveData();
	}

	public void createNew() {
		responcibilities.hide();
		structure_name.getForm().clear();
		structure_name.getForm().setValue("id", (Integer) null);
		enableDisable(true);
	}

	public void setId(int id, TreeGrid tree, Record rec) {
		Criteria cr = new Criteria("id", "" + id);
		cr.setAttribute("language_id", DocFlow.language_id);
		structure_name.getForm().fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] recs = response.getData();
				if (recs == null)
					return;
				if (recs.length == 0)
					return;
				structure_name.setValue(
						recs[0].getAttributeAsLong("structure_name_id"),
						recs[0].getAttribute("structure_name"));
				job_description.setValue(
						recs[0].getAttributeAsLong("job_description_id"),
						recs[0].getAttribute("job_description"));

			}
		});
		structure_name.getForm().show();
		responcibilities.setItemId(id, 1);
		// responcibilities.show();

		enableDisable(true);
	}

	public void enableDisable(boolean enable) {
		DynamicForm df = structure_name.getForm();
		FormItem[] fi = df.getFields();
		for (FormItem formItem : fi) {
			if (!enable)
				formItem.disable();
			else
				formItem.enable();
		}
		if (!enable)
			responcibilities.disable();
		else
			responcibilities.enable();
	}
}
