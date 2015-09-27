package com.docflow.client.components.hr;

import java.util.ArrayList;
import java.util.HashMap;

import com.docflow.client.DocFlow;
import com.docflow.shared.hr.Captions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
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

public class PResponcibilities extends VLayout implements LanguageValueSet {

	private static String IMG_TYPE = getImgType();

	private static native String getImgType() /*-{
												var imgType = $wnd.isc.pickerImgType;
												return imgType == null || imgType === undefined ? "png" : imgType;
												}-*/;

	private SelectItem siResp_type;
	private LanguageAreaItem tiJob_description;

	private ListGrid lgResponcibilities;

	private boolean editingType;

	private Record selectedRecord;

	private Integer id;
	private Integer item_type;

	private DataSource responcibilitiesDS;

	public PResponcibilities() {
		siResp_type = new SelectItem("resp_type_id", "უფლებამოსილების ტიპი");
		siResp_type.setOptionDataSource(DocFlow
				.getDataSource("Responcibility_TypeDS"));
		siResp_type.setOptionCriteria(new Criteria("language_id",
				DocFlow.language_id + ""));

		PickerIcon piAdd = new PickerIcon(new Picker("[SKIN]/actions/add."
				+ IMG_TYPE), new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				addRespType();

			}
		});

		PickerIcon piEdit = new PickerIcon(new Picker("[SKIN]/actions/edit."
				+ IMG_TYPE), new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				editRespType();

			}
		});
		siResp_type.setValueField("id");
		siResp_type.setDisplayField("resp_type_name");
		siResp_type.setFetchMissingValues(true);
		siResp_type.setIcons(piAdd, piEdit);
		siResp_type.setRequired(true);
		tiJob_description = new LanguageAreaItem("description", "აღწერა");
		tiJob_description.setRequired(true);

		DynamicForm dm = new DynamicForm();
		dm.setTitleOrientation(TitleOrientation.LEFT);
		dm.setNumCols(2);
		dm.setFields(siResp_type, tiJob_description);
		dm.setWidth100();
		dm.setHeight("20%");
		ToolStrip ts = new ToolStrip();

		ToolStripButton tsbAdd = new ToolStripButton("", "[SKIN]/actions/add."
				+ IMG_TYPE);
		ts.addButton(tsbAdd);

		tsbAdd.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addResp();

			}
		});

		ToolStripButton tsbEdit = new ToolStripButton("",
				"[SKIN]/actions/edit." + IMG_TYPE);
		ts.addButton(tsbEdit);

		tsbEdit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				editResp();

			}
		});

		ToolStripButton tsbSave = new ToolStripButton("icons/16/approved.png");
		tsbSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveResp();

			}
		});
		ts.addButton(tsbSave);
		ts.setWidth100();
		this.addMember(ts);
		this.addMember(dm);
		responcibilitiesDS = DocFlow.getDataSource("ResponcibilitiesDS");
		lgResponcibilities = new ListGrid();
		lgResponcibilities.setWidth100();
		lgResponcibilities.setHeight("75%");
		lgResponcibilities.setFields(new ListGridField("resp_type_name",
				"უფლებამოსილების ტიპი", 150), new ListGridField("description",
				"აღწერა", 300));
		lgResponcibilities.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				chooseRecord();

			}
		});

		this.addMember(lgResponcibilities);
	}

	protected void chooseRecord() {
		selectedRecord = lgResponcibilities.getSelectedRecord();
		siResp_type.setValue(selectedRecord.getAttributeAsInt("resp_type_id"));
		tiJob_description.setValue(
				selectedRecord.getAttributeAsLong("description_id"),
				selectedRecord.getAttribute("description"));
	}

	protected void editResp() {
		if (selectedRecord == null) {
			SC.warn("აირჩიეთ ჩანაწერი!!!");
			return;
		}
		if (!siResp_type.getForm().validate())
			return;
		setFieldValues();
		lgResponcibilities.selectRecord(selectedRecord);
		siResp_type.clearValue();
		tiJob_description.clearValue();
		tiJob_description.setValue(null, null);
	}

	public void setFieldValues() {
		selectedRecord.setAttribute("resp_type_id", siResp_type.getValue());
		selectedRecord.setAttribute("resp_type_name",
				siResp_type.getDisplayValue());
		selectedRecord
				.setAttribute("description", tiJob_description.getValue());
		selectedRecord
				.setAttribute("description_id", tiJob_description.getId());
		selectedRecord.setAttribute("edited", true);

	}

	public void setItemId(Integer id, Integer item_type) {
		this.id = id;
		this.item_type = item_type;
		Criteria cr = new Criteria();
		cr.setAttribute("item_id", id);
		cr.setAttribute("item_type_id", item_type);
		cr.setAttribute("language_id", DocFlow.language_id);
		responcibilitiesDS.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				lgResponcibilities.setData(response.getData());

			}
		});
	}

	protected void saveResp() {
		Record[] recs = lgResponcibilities.getRecords();
		ArrayList<Record> editedRecords = new ArrayList<Record>();
		for (Record record : recs) {
			if (record.getAttribute("edited") != null) {
				Record r = new Record(record.toMap());
				r.setAttribute("item_id", id);
				r.setAttribute("item_type_id", item_type);
				r.setAttribute("language_id", DocFlow.language_id);
				editedRecords.add(r);
			}

		}

		DSCallback callback = new DSCallback() {
			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				// TODO Auto-generated method stub

			}
		};

		for (Record record : editedRecords) {
			DSRequest dsr = new DSRequest();

			try {
				if (record.getAttributeAsInt("id") == null) {
					responcibilitiesDS.addData(record, callback, dsr);
				} else {
					responcibilitiesDS.updateData(record, callback, dsr);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		setItemId(id, item_type);
	}

	protected void addResp() {
		if (!siResp_type.getForm().validate())
			return;
		selectedRecord = new Record();
		setFieldValues();
		lgResponcibilities.addData(selectedRecord);
		lgResponcibilities.selectRecord(selectedRecord);
		selectedRecord = null;
	}

	protected void editRespType() {
		Integer sid = null;
		try {
			sid = Integer.parseInt(siResp_type.getValue().toString());
		} catch (Exception e) {
			return;
		}
		final Integer id = sid;
		editingType = true;
		Criteria cr = new Criteria();
		cr.setAttribute("language_id", DocFlow.language_id + "");
		cr.setAttribute("id", id);
		siResp_type.getOptionDataSource().fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] data = response.getData();
				if (data == null)
					return;
				if (data.length == 0)
					return;
				final Record r = data[0];
				final Long resp_type_name_id = r
						.getAttributeAsLong("resp_type_name_id");

				DocFlow.docFlowService.getCaptions(resp_type_name_id,
						new AsyncCallback<HashMap<Integer, Captions>>() {
							@Override
							public void onSuccess(
									HashMap<Integer, Captions> result) {
								WCaptions.showForm(true, resp_type_name_id,
										siResp_type.getTitle(),
										PResponcibilities.this, result);
							}

							@Override
							public void onFailure(Throwable caught) {
								SC.say(caught.getMessage());

							}
						});
			}
		});
	}

	protected void addRespType() {
		editingType = false;
		WCaptions.showForm(false, null, siResp_type.getTitle(), this,
				new HashMap<Integer, Captions>());
	}

	@Override
	public void setValue(final Long resp_id, String value) {
		if (editingType) {
			Integer sid = null;
			try {
				sid = Integer.parseInt(siResp_type.getValue().toString());
			} catch (Exception e) {
				return;
			}
			final Integer id = sid;
			Criteria cr = new Criteria();
			cr.setAttribute("language_id", DocFlow.language_id + "");
			cr.setAttribute("id", id);
			siResp_type.getOptionDataSource().fetchData(cr, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					Record[] data = response.getData();
					if (data == null)
						return;
					if (data.length == 0)
						return;
					final Record r = data[0];
					r.setAttribute("resp_type_name_id", resp_id);
					r.setAttribute("language_id", DocFlow.language_id);
					siResp_type.getOptionDataSource().updateData(r,
							new DSCallback() {

								@Override
								public void execute(DSResponse response,
										Object rawData, DSRequest request) {
									setRespTypeId(id);

								}
							});
				}
			});
			return;
		}
		Record r = new Record();
		r.setAttribute("resp_type_name_id", resp_id);
		r.setAttribute("resp_type_name", value);
		r.setAttribute("language_id", DocFlow.language_id);
		siResp_type.getOptionDataSource().addData(r, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] data = response.getData();
				if (data == null)
					return;
				if (data.length == 0)
					return;
				final Record r = data[0];
				final Integer id = r.getAttributeAsInt("id");
				setRespTypeId(id);

			}
		});
	}

	public void setRespTypeId(final Integer id) {
		siResp_type.fetchData(new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				siResp_type.setValue(id);

			}
		});
	}
}
