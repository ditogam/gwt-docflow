package com.docflow.client.components.hr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.common.client.WindowResultObject;
import com.common.shared.model.UMObject;
import com.docflow.client.DocFlow;
import com.docflow.client.components.usermanager.WAddEditUMObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PersonItem extends VLayout implements WindowResultObject {

	private static String IMG_TYPE = getImgType();

	private static native String getImgType() /*-{
												var imgType = $wnd.isc.pickerImgType;
												return imgType == null || imgType === undefined ? "png" : imgType;
												}-*/;

	private LanguageItem tiLastName;
	private LanguageItem tiFirstName;
	private LanguageItem tiMiddleName;
	private DateItem diBirthDate;
	private SelectItem siSex;
	private SelectItem siNationality;
	private LanguageItem tiNationalityOther;
	private TextItem tiIdentityNom;
	private LanguageAreaItem tiAddress;
	private SelectItem siMarigeStatuse;
	private SelectItem siUser;
	private SelectItem siInitiator;
	private LanguageItem tiFamily;

	private DynamicForm dfPerson;
	private ListGrid lgEducation;
	private ListGrid lgWorkingExperience;
	private Tab eTab;
	private Tab pTab;
	private Tab wTab;
	private ImageUploadPanel uploadPanel;

	public static PersonItem personItem;

	private Integer personId;
	private Record rec;
	private DataSource dsStructure;
	private ToolStrip tsmenu;

	public PersonItem() {

		DataSource dsClassifier = DocFlow.getDataSource("ClassifierDS");

		dsClassifier.fetchData(new Criteria(), new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				setFieldData(response);
			}
		});

		personItem = this;
		tiLastName = new LanguageItem("person_last_name", "გვარი");
		((LanguageItem) tiLastName).setValue(3L, "");
		tiFirstName = new LanguageItem("person_first_name", "სახელი");
		dfPerson = new DynamicForm();
		DataSource dsPerson = DocFlow.getDataSource("PersonDS");
		dfPerson.setDataSource(dsPerson);

		dfPerson.setWidth("50%");
		dfPerson.setHeight100();
		lgEducation = new ListGrid();
		lgEducation.setWidth100();
		lgWorkingExperience = new ListGrid();
		lgWorkingExperience.setWidth100();
		lgEducation.setCanRemoveRecords(true);
		DataSource dsEducation = DocFlow.getDataSource("PersonEducationDS");
		lgEducation.setDataSource(dsEducation);
		lgEducation.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {

			}
		});
		tsmenu = new ToolStrip();
		tsmenu.setWidth100();
		tsmenu.setHeight("5%");
		ToolStripButton tsbSave = new ToolStripButton("Save",
				"icons/16/approved.png");
		tsmenu.addButton(tsbSave);
		tsbSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveData(null);

			}
		});
		this.addMember(tsmenu);
		tiMiddleName = new LanguageItem("person_middle_name", "მამის სახელი");
		diBirthDate = new DateItem("person_birth_date", "დაბადების ტარიღი");
		diBirthDate.setUseTextField(false);
		siSex = new SelectItem("person_sex", "სქესი");
		siNationality = new SelectItem("person_nationality", "ეროვნება");
		tiNationalityOther = new LanguageItem("person_nationality_other",
				"სხვ. ეროვნება");
		tiIdentityNom = new TextItem("person_identity_no", "პირადი #");
		tiAddress = new LanguageAreaItem("person_address", "მისამართი");
		siMarigeStatuse = new SelectItem("person_merige_statuse",
				"ოჯახური მდგომარეობა");
		tiFamily = new LanguageItem("person_family", "ოჯახი");
		siUser = new SelectItem("user_id", "მომხმარებელი");

		siInitiator = new SelectItem("initiator_id", "ინიციატორი");

		ListGrid pickListProperties = new ListGrid();
		pickListProperties.setShowFilterEditor(true);

		ListGridField person_id = new ListGridField("person_id");
		person_id.setHidden(true);
		ListGridField person_name = new ListGridField("person_name");

		siInitiator.setOptionDataSource(DocFlow.getDataSource("PersonShortDS"));
		siInitiator.setDisplayField("person_name");
		siInitiator.setValueField("person_id");
		siInitiator.setPickListWidth(300);
		siInitiator.setPickListFields(person_id, person_name);
		siInitiator.setPickListProperties(pickListProperties);
		siInitiator.setFetchMissingValues(true);
		siInitiator.setOptionCriteria(new Criteria("language_id", ""
				+ DocFlow.language_id));
		setIniciatorCriteria(-1);

		dfPerson.setFields(new HiddenItem("person_id"), tiFirstName,
				tiLastName, tiMiddleName, diBirthDate, siSex, siNationality,
				tiNationalityOther, tiIdentityNom, tiAddress, siMarigeStatuse,
				tiFamily, siUser, siInitiator);
		FormItem[] fi = dfPerson.getFields();
		for (FormItem formItem : fi) {
			formItem.setRequired(true);
		}

		tiMiddleName.setRequired(false);
		tiNationalityOther.setRequired(false);
		tiFamily.setRequired(false);
		siInitiator.setRequired(false);

		TabSet pTabSet = new TabSet();
		pTabSet.setWidth100();
		pTabSet.setHeight("95%");

		pTab = new Tab("ძირითადი ინფორმაცია");
		HLayout vl = new HLayout();
		uploadPanel = new ImageUploadPanel();
		uploadPanel.setWidth("50%");
		vl.addMember(dfPerson);
		vl.addMember(uploadPanel);
		pTab.setPane(vl);
		eTab = new Tab("განათლება");
		wTab = new Tab("სამუშაო გამოცდილება");
		addEducationTab();
		addWorkingExperience();
		pTabSet.setTabs(pTab, eTab, wTab);
		this.addMember(pTabSet);
		refreshUsers(null);
		PickerIcon dataPicker = new PickerIcon(PickerIcon.REFRESH,
				new FormItemClickHandler() {

					@Override
					public void onFormItemClick(FormItemIconClickEvent event) {
						refreshUsers(null);
					}
				});

		PickerIcon piAdd = new PickerIcon(new Picker("[SKIN]/actions/add."
				+ IMG_TYPE), new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				addUser();

			}
		});

		PickerIcon piEdit = new PickerIcon(new Picker("[SKIN]/actions/edit."
				+ IMG_TYPE), new FormItemClickHandler() {

			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				editUser();

			}
		});

		siUser.setIcons(dataPicker, piAdd, piEdit);
		siUser.setWidth(220);
	}

	private void setIniciatorCriteria(int id) {
		Criteria cr = new Criteria("not_person_id", id + "");
		siInitiator.setOptionCriteria(cr);

	}

	protected void editUser() {
		if (siUser.getValue() == null)
			return;
		Integer user_id;
		try {
			user_id = new Integer(siUser.getValue().toString());
			if (user_id <= 0)
				return;
		} catch (Exception e) {
			return;
		}
		UMObject user = new UMObject();
		user.setType(UMObject.USER);
		user.setIdVal(user_id);
		user.setTextVal(siUser.getDisplayValue());
		WAddEditUMObject.showWindow(user, this);

	}

	protected void addUser() {
		UMObject user = new UMObject();
		user.setType(UMObject.USER);
		WAddEditUMObject.showWindow(user, this);

	}

	private void refreshUsers(final Integer id) {
		DocFlow.docFlowService.getUserManagerObjects(UMObject.USER,
				new AsyncCallback<HashMap<Integer, ArrayList<UMObject>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							HashMap<Integer, ArrayList<UMObject>> result) {
						LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
						map.put("-1", "---");
						ArrayList<UMObject> umO = result.get(UMObject.USER);
						if (umO != null)
							for (UMObject umObject : umO) {
								map.put(umObject.getIdVal() + "",
										umObject.getTextVal());
							}
						siUser.setValueMap(map);
						if (id != null)
							siUser.setValue("" + id.toString());
					}
				});
	}

	private void addEducationTab() {
		VLayout vlEducation = new VLayout();

		ToolStrip tsmenuEducation = new ToolStrip();
		tsmenuEducation.setWidth100();
		tsmenuEducation.setHeight("5%");
		ToolStripButton tsbAdd = new ToolStripButton("Add",
				"icons/16/document_plain_new.png");
		tsmenuEducation.addButton(tsbAdd);
		tsbAdd.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("person_id", personId);
				lgEducation.startEditingNew(map);
			}
		});
		ToolStripButton tsbEdit = new ToolStripButton("Edit",
				"icons/16/download.png");
		tsmenuEducation.addButton(tsbEdit);
		tsbEdit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				lgEducation.startEditing();

			}
		});
		vlEducation.addMember(tsmenuEducation);
		lgEducation.setHeight("95%");
		vlEducation.addMember(lgEducation);

		lgEducation.setCanEdit(true);
		ListGridField lgfPerson_id = new ListGridField("person_id");
		lgfPerson_id.setType(ListGridFieldType.INTEGER);
		lgfPerson_id.setHidden(true);

		ListGridField lgfEducation_name_and_place = new ListGridField(
				"education_name_and_place", "სასწავლებლის სახელწოდება");
		lgfEducation_name_and_place.setWrap(true);
		ListGridField lgfFaculty = new ListGridField("faculty", "ფაკულტეტი");
		lgfFaculty.setWrap(true);

		ListGridField lgfEnter_year = new ListGridField("enter_year",
				"შესვლის წ.");
		lgfEnter_year.setWrap(true);
		lgfEnter_year.setType(ListGridFieldType.INTEGER);

		ListGridField lgfGreduate_year = new ListGridField("greduate_year",
				"დამთავრების წ.");
		lgfGreduate_year.setWrap(true);
		lgfGreduate_year.setType(ListGridFieldType.INTEGER);

		ListGridField lgfLeave_grade = new ListGridField("leave_grade",
				"გამოსვლის კურსი");
		lgfLeave_grade.setWrap(true);
		lgfLeave_grade.setType(ListGridFieldType.INTEGER);

		ListGridField lgfGreduate_degree_certificate_num = new ListGridField(
				"greduate_degree_certificate_num", "სპეციალობა და დიპლომის #");
		lgfGreduate_degree_certificate_num.setWrap(true);

		lgEducation.setFields(lgfPerson_id, lgfEducation_name_and_place,
				lgfEnter_year, lgfGreduate_year, lgfLeave_grade,
				lgfGreduate_degree_certificate_num);
		lgEducation.setAutoSaveEdits(false);
		eTab.setPane(vlEducation);
	}

	private void addWorkingExperience() {
		VLayout vlWorkingExperience = new VLayout();
		lgWorkingExperience.setHeaderSpans(new HeaderSpan("თვე და წელი",
				new String[] { "enter_date", "leave_date" }));
		ToolStrip tsmenuEducation = new ToolStrip();
		tsmenuEducation.setWidth100();
		tsmenuEducation.setHeight("5%");
		ToolStripButton tsbAdd = new ToolStripButton("Add",
				"icons/16/document_plain_new.png");
		tsmenuEducation.addButton(tsbAdd);

		ToolStripButton tsbEdit = new ToolStripButton("Edit",
				"icons/16/download.png");
		tsmenuEducation.addButton(tsbEdit);

		vlWorkingExperience.addMember(tsmenuEducation);
		lgWorkingExperience.setHeight("95%");
		vlWorkingExperience.addMember(lgWorkingExperience);

		lgWorkingExperience.setCanEdit(true);

		ListGridField lgfEnterDate = new ListGridField("enter_date", "მოსვლის");
		lgfEnterDate.setType(ListGridFieldType.DATE);

		ListGridField lgfLeave = new ListGridField("leave_date", "წასვლის");
		lgfLeave.setType(ListGridFieldType.DATE);

		ListGridField lgfWorkPosition = new ListGridField("work_position",
				"თანამდებობა");

		ListGridField lgfWorkPlace = new ListGridField("work_place",
				"ორგანიზაცია");

		lgWorkingExperience.setFields(lgfEnterDate, lgfLeave, lgfWorkPosition,
				lgfWorkPlace);

		lgWorkingExperience.setAutoSaveEdits(false);
		wTab.setPane(vlWorkingExperience);
	}

	private void setComboValues(SelectItem si, ArrayList<String[]> arrayList) {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String[] strings : arrayList) {
			map.put(strings[0], strings[1]);
		}
		si.setValueMap(map);

	}

	protected void setFieldData(DSResponse response) {
		HashMap<Integer, ArrayList<String[]>> values = new HashMap<Integer, ArrayList<String[]>>();
		Record[] recs = response.getData();
		for (Record record : recs) {
			Integer typeid = record.getAttributeAsInt("cl_type_id");
			ArrayList<String[]> dt = values.get(typeid);
			if (dt == null) {
				dt = new ArrayList<String[]>();
				values.put(typeid, dt);
			}
			dt.add(new String[] { record.getAttribute("cl_id"),
					record.getAttribute("cl_name") });
		}
		setComboValues(siSex, values.get(1));
		setComboValues(siNationality, values.get(2));
		setComboValues(siMarigeStatuse, values.get(3));

	}

	public void setPersonId(final Integer personId, Record rec,
			DataSource dsStructure) {
		this.rec = rec;
		this.dsStructure = dsStructure;
		setPersonId(personId);
		tsmenu.show();
	}

	public void setPersonId(final Integer personId) {
		tsmenu.hide();
		if (personId != null)
			setIniciatorCriteria(personId);
		this.personId = personId;
		Criteria cr = new Criteria();
		if (this.personId == null)
			this.personId = -1;
		cr.addCriteria("person_id", this.personId);
		cr.addCriteria("uniq", HTMLPanel.createUniqueId());
		uploadPanel.setImageID(-1);
		dfPerson.fetchData(cr, new DSCallback() {
			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] recs = response.getData();
				getOtherValues(recs);
			}
		});
		lgEducation.fetchData(cr);
		pTab.getTabSet().selectTab(pTab);
		if (personId == null) {
			eTab.setDisabled(true);
			wTab.setDisabled(true);
		} else {
			eTab.setDisabled(false);
			wTab.setDisabled(false);
		}
	}

	@SuppressWarnings("deprecation")
	public void saveData(final WindowResultObject valueSet) {
		if (!diBirthDate.getForm().validate())
			return;
		diBirthDate.getValueAsDate().setHours(0);
		diBirthDate.getValueAsDate().setMinutes(0);
		diBirthDate.getValueAsDate().setSeconds(0);
		diBirthDate.getValueAsDate().setDate(
				diBirthDate.getValueAsDate().getDate() + 1);
		diBirthDate.getValueAsDate().setDate(
				diBirthDate.getValueAsDate().getDate() - 1);
		setOtherValues();
		dfPerson.saveData(new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record rec = response.getData()[0];
				// diBirthDate.getValueAsDate().setHours(0);
				// diBirthDate.getValueAsDate().setMinutes(0);
				// diBirthDate.getValueAsDate().setSeconds(0);
				// diBirthDate.getValueAsDate().setDate(
				// diBirthDate.getValueAsDate().getDate() + 1);
				// diBirthDate.getValueAsDate().setDate(
				// diBirthDate.getValueAsDate().getDate() - 1);
				personId = rec.getAttributeAsInt("person_id");
				PersonItem.this.rec.setAttribute("object_id", personId);
				PersonItem.this.dsStructure.updateData(PersonItem.this.rec);

				eTab.setDisabled(false);
				wTab.setDisabled(false);
				lgEducation.saveAllEdits(new Function() {

					@Override
					public void execute() {
						setPersonId(personId, PersonItem.this.rec, dsStructure);
					}
				});

				if (valueSet != null)
					valueSet.setResult(personId);
			}
		});
	}

	public void setOtherValues() {
		dfPerson.setValue("picture_id", uploadPanel.getImageID());
		dfPerson.setValue("person_last_name_id", (Long) tiLastName.getId());
		dfPerson.setValue("person_first_name_id", (Long) tiFirstName.getId());
		dfPerson.setValue("person_address_tid", (Long) tiAddress.getId());
		dfPerson.setValue("person_family_id", (Long) tiFamily.getId());
		dfPerson.setValue("person_middle_name_id", (Long) tiMiddleName.getId());
		dfPerson.setValue("person_nationality_other_id",
				(Long) tiNationalityOther.getId());

	}

	public void getOtherValues(Record[] recs) {
		if (recs == null)
			return;
		if (recs.length == 0)
			return;
		Integer imageId = recs[0].getAttributeAsInt("picture_id");
		if (imageId != null) {
			uploadPanel.setImageID(imageId.intValue());
		}
		tiLastName.setValue(recs[0].getAttributeAsLong("person_last_name_id"),
				tiLastName.getValueAsString());
		tiFirstName.setValue(
				recs[0].getAttributeAsLong("person_first_name_id"),
				tiFirstName.getValueAsString());
		tiMiddleName.setValue(
				recs[0].getAttributeAsLong("person_middle_name_id"),
				tiMiddleName.getValueAsString());
		tiFamily.setValue(recs[0].getAttributeAsLong("person_family_id"),
				tiFamily.getValueAsString());

		tiAddress.setValue(recs[0].getAttributeAsLong("person_address_tid"),
				tiAddress.getValueAsString());
		tiNationalityOther.setValue(
				recs[0].getAttributeAsLong("person_nationality_other_id"),
				tiNationalityOther.getValueAsString());

	}

	@Override
	public void setResult(Object obj) {
		if (obj == null)
			return;
		if (obj instanceof UMObject) {
			refreshUsers((int) ((UMObject) obj).getIdVal());
		}
	}

}
