package com.docflow.client.components.docflow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.client.components.map.PBuildingsForm;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class WDFSearchCustomers extends Window {

	public static WDFSearchCustomers instance;

	public static void showWindow() {
		if (instance == null)
			instance = new WDFSearchCustomers();
		instance.show();
	}

	public static void clearPhone() {
		if (instance != null)
			instance.tiPhone.clearValue();
	}

	private PBuildingsForm form;

	private DynamicForm formSearch;

	private ComboBoxItem cbCity;
	private ComboBoxItem cbStreet;
	private ComboBoxItem cbZones;
	private TextItem tiPhone;
	private SelectItem siHasBuilding;
	private ButtonItem biSearch;

	private VLayout vlCustomerSearch;
	private ListGrid lgRealCustomers;
	private Criteria lastCriteria;

	private DataSource dsCustomer;

	public WDFSearchCustomers() {
		this.setTitle("ძებნა");
		dsCustomer = DocFlow.getDataSource("CustomerDS");
		// dsLocal.setClientOnly(true);
		// dsLocal.setDropExtraFields(false);
		// DataSourceField cusId = new DataSourceField("cusid",
		// FieldType.INTEGER);
		// cusId.setPrimaryKey(true);
		// dsLocal.setFields(cusId);

		Record record = new Record();
		if (DocFlow.user_obj.getUser().getRegionid() >= 1)
			record.setAttribute("regid", DocFlow.user_obj.getUser()
					.getRegionid());

		if (DocFlow.user_obj.getUser().getSubregionid() >= 1)
			record.setAttribute("raiid", DocFlow.user_obj.getUser()
					.getSubregionid());

		Integer subregionid = record.getAttributeAsInt("raiid");

		form = new PBuildingsForm(record, null, false);

		this.addItem(form);

		// this.addItem(toolStrip);
		VLayout hlGrids = new VLayout();
		hlGrids.setHeight100();

		vlCustomerSearch = new VLayout();
		vlCustomerSearch.setHeight100();
		vlCustomerSearch.setShowResizeBar(true);

		Map<String, Object> subRegionCrit = new TreeMap<String, Object>();
		if (subregionid != null) {
			subRegionCrit.put("parentId", subregionid);
		}

		Map<String, Object> streetCrit = new TreeMap<String, Object>();

		streetCrit.put("parentId", -1);

		cbCity = new ComboBoxItem("cityid", "ქალაქი");
		cbStreet = new ComboBoxItem("streetid", "ქუჩა");
		cbZones = new ComboBoxItem("zone", "ზონა");
		tiPhone = new IntegerItem("phone", "ტელეფონის #");
		siHasBuilding = new SelectItem("biHasBuilding", "სახლი მიბმულია");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("NONE", "---");
		map.put("building_free", "თავისუფალი");
		map.put("for_building", "სახლზე მიბმული");
		siHasBuilding.setValueMap(map);
		siHasBuilding.setValue("NONE");

		ClientUtils.fillSelectionCombo(cbCity, ClSelection.T_CITY,
				subRegionCrit);
		ClientUtils.fillSelectionCombo(cbStreet, ClSelection.T_STREET,
				streetCrit);
		ClientUtils.fillSelectionCombo(cbZones, ClSelection.T_ZONES,
				subRegionCrit);

		ClientUtils.makeDependancy(form.siSubregion, true, new FormItemDescr(
				cbCity), new FormItemDescr(cbZones));

		ClientUtils.makeDependancy(cbCity, true, new FormItemDescr(cbStreet));

		biSearch = new ButtonItem("biSearch", "Search");
		biSearch.setIcon("[SKIN]/actions/search.png");
		// biSearch.setStartRow(false);
		biSearch.setEndRow(false);

		biSearch.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				searchCustomers();

			}
		});

		formSearch = new DynamicForm();
		formSearch.setNumCols(6);
		formSearch.setFields(cbCity, cbStreet, cbZones, tiPhone, siHasBuilding);
		// formSearch.setIsGroup(true);
		// formSearch.setGroupTitle("");

		vlCustomerSearch.addMember(formSearch);

		lgRealCustomers = new ListGrid();
		createFields(lgRealCustomers);
		// lgRealCustomers.setAutoFetchData(false);
		lgRealCustomers.setDataSource(dsCustomer);

		lgRealCustomers.setCanDragRecordsOut(true);
		lgRealCustomers.setDragDataAction(DragDataAction.MOVE);
		// lgRealCustomers.setAutoSaveEdits(false);
		lgRealCustomers.setWaitForSave(true);
		vlCustomerSearch.addMember(lgRealCustomers);
		// vlCustomerSearch.setShowEdges(true);

		hlGrids.addMember(vlCustomerSearch);

		this.addItem(hlGrids);
		lgRealCustomers.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (lgRealCustomers.getSelectedRecord() == null)
					return;
				Integer cusid = lgRealCustomers.getSelectedRecord()
						.getAttributeAsInt("cusid");
				if (cusid == null)
					return;
				DocumentDetailTabPane.documentDetails.setCusID(cusid);
				hide();
			}
		});
		SavePanel savePanel = new SavePanel("Search", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// destroy();
				hide();
			}

		});
		addFieldKeyEnter(form);
		addFieldKeyEnter(formSearch);
		this.addItem(savePanel);
		this.setHeight(700);
		this.setWidth(900);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				// if (!event.getIsVisible()) {
				// destroy();
				// }

			}
		});
	}

	protected void search() {
		searchCustomers();
	}

	private Long getLongValue(FormItem formItem) {
		try {
			Object obj = formItem.getValue();
			return Long.parseLong(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private void addFieldKeyEnter(DynamicForm dynamicForm) {

		FormItem[] formItems = dynamicForm.getFields();
		for (FormItem formItem : formItems) {
			if (formItem instanceof ButtonItem)
				continue;
			formItem.addKeyDownHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					String keyName = event.getKeyName();
					if (keyName.equals("Enter")) {
						searchCustomers();
					}

				}
			});
		}

	}

	private String getStringValue(FormItem formItem) {
		try {
			Object obj = formItem.getValue();
			return obj.toString().trim();
		} catch (Exception e) {
			return null;
		}
	}

	protected void searchCustomers() {
		String sPhone = getStringValue(tiPhone);
		Long iStreetId = getLongValue(cbStreet);
		Long iCityId = getLongValue(cbCity);
		Long iZone = getLongValue(cbZones);
		if ((sPhone == null || sPhone.isEmpty()) && iCityId == null
				&& iStreetId == null && iZone == null) {
			SC.say("Please enter criteria!!!");
			return;
		}
		lastCriteria = new Criteria();
		if (iCityId != null)
			lastCriteria.setAttribute("cityid", iCityId);
		if (sPhone != null && !sPhone.isEmpty())
			lastCriteria.setAttribute("phone", sPhone);
		if (iStreetId != null)
			lastCriteria.setAttribute("streetid", iStreetId);
		if (iZone != null)
			lastCriteria.setAttribute("zone", iZone);
		lastCriteria.setAttribute("hasbuilding", 1);
		Object hasBuilding = siHasBuilding.getValue();
		if (hasBuilding != null)
			lastCriteria.setAttribute(hasBuilding.toString(), 1);
		lgRealCustomers.filterData(lastCriteria);
	}

	private void createFields(ListGrid listGrid) {
		ListGridField cusid = new ListGridField("cusid", "აბონენტი #", 60);
		ListGridField has_building = new ListGridField("has_building",
				"მიბ.სახლი", 20);
		has_building.setType(ListGridFieldType.BOOLEAN);
		listGrid.setFields(cusid, new ListGridField("cusname",
				"აბონენტის სახელი", 180), new ListGridField("cityname",
				"ქალაქ/სოფელი", 100), new ListGridField("streetname", "ქუჩა",
				100), new ListGridField("home", "სახლი", 60),
				new ListGridField("flat", "ბინის #", 50), new ListGridField(
						"phone", "ტელეფონის #", 100), new ListGridField("zone",
						"ზონა", 180), has_building);
	}

}
