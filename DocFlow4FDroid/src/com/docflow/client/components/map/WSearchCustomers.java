package com.docflow.client.components.map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
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
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class WSearchCustomers extends Window {

	public static WSearchCustomers instance;

	public static void showWindow() {
		if (instance == null)
			instance = new WSearchCustomers();
		instance.show();
	}

	private PBuildingsForm form;

	private DynamicForm formSearch;

	private ComboBoxItem cbCity;
	private ComboBoxItem cbStreet;
	private ComboBoxItem cbZones;
	private IntegerItem iiCusID;
	private TextItem tiCusName;
	private SelectItem siHasBuilding;
	private ButtonItem biSearch;

	private VLayout vlCustomerSearch;
	private ListGrid lgRealCustomers;
	private Criteria lastCriteria;

	private DataSource dsCustomer;

	public WSearchCustomers() {
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

		cbCity = new ComboBoxItem("cityid", "City");
		cbStreet = new ComboBoxItem("streetid", "Street");
		cbZones = new ComboBoxItem("zone", "Zones");
		iiCusID = new IntegerItem("cusid", "Customer #");
		tiCusName = new TextItem("cus_name", "Cus name");
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
				searchCustomers(false);

			}
		});

		ButtonItem biClear = new ButtonItem("biClear", "Clear");

		biClear.setStartRow(false);
		biClear.setEndRow(false);

		biClear.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				clearSearch();

			}
		});

		formSearch = new DynamicForm();
		formSearch.setTitleOrientation(TitleOrientation.TOP);
		formSearch.setNumCols(4);
		formSearch.setFields(cbCity, cbStreet, cbZones, iiCusID, tiCusName,
				siHasBuilding, biClear);
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
		lgRealCustomers.setShowRowNumbers(true);
		vlCustomerSearch.addMember(lgRealCustomers);
		// vlCustomerSearch.setShowEdges(true);

		hlGrids.addMember(vlCustomerSearch);

		this.addItem(hlGrids);
		lgRealCustomers.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				GMapPanel.instance.findCustomerObject(lgRealCustomers
						.getSelectedRecord());

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
		IButton ibReport = new IButton("Report");
		ibReport.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchCustomers(true);

			}
		});
		savePanel.addItem(ibReport);
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		savePanel.addItem(l);
		this.addItem(savePanel);

		this.setHeight(700);
		this.setWidth(800);
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

	protected void clearSearch() {
		GMapPanel.instance.setSearch("aaaa1");

	}

	protected void search() {
		searchCustomers(false);
	}

	private Long getLongValue(FormItem formItem) {
		try {
			Object obj = formItem.getValue();
			return Long.parseLong(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	protected void searchCustomers(boolean print) {
		Long iCusId = getLongValue(iiCusID);
		Long iSubregionId = getLongValue(form.siSubregion);
		Long iStreetId = getLongValue(cbStreet);
		Long iCityId = getLongValue(cbCity);
		Long iZone = getLongValue(cbZones);
		Object cus_name = tiCusName.getValue();

		if (iSubregionId == null && iCusId == null && iCityId == null
				&& iStreetId == null && iZone == null
				&& (cus_name == null || cus_name.toString().trim().isEmpty())) {
			return;
		}
		lastCriteria = new Criteria();
		lastCriteria.setAttribute("cus_status_active", 1);

		if (iSubregionId != null)
			lastCriteria.setAttribute("subregionid", iSubregionId);
		if (iCityId != null)
			lastCriteria.setAttribute("cityid", iCityId);
		if (iCusId != null)
			lastCriteria.setAttribute("cusid", iCusId);
		if (iStreetId != null)
			lastCriteria.setAttribute("streetid", iStreetId);
		if (iZone != null)
			lastCriteria.setAttribute("zone", iZone);
		lastCriteria.setAttribute("hasbuilding", 1);
		Object hasBuilding = siHasBuilding.getValue();
		if (hasBuilding != null)
			lastCriteria.setAttribute(hasBuilding.toString(), 1);
		if (cus_name != null && !cus_name.toString().trim().isEmpty())
			lastCriteria.setAttribute("cus_name", cus_name.toString().trim());
		if (print) {
			lgRealCustomers.filterData(lastCriteria);
			DSRequest dsRequest = new DSRequest();
			dsRequest.setExportAs(ExportFormat.XLS);
			dsRequest.setExportDisplay(ExportDisplay.DOWNLOAD);
			lgRealCustomers.exportData(dsRequest);
		} else
			refreshCustomerData();

	}

	private void refreshCustomerData() {
		lgRealCustomers.filterData(lastCriteria);

		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				if (response.getData() == null
						|| response.getData().length == 0)
					return;
				String wkt = response.getData()[0].getAttribute("feature_text");
				GMapPanel.instance.setCusSearchResult(wkt);
			}
		};

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();

		// criteria.put("zone", "224041107004");
		criteria.put("srid", Constants.GOOGLE_SRID);
		criteria.put("hasbuilding", 1);
		criteria.put("uuid", DocFlow.user_obj.getUuid());
		ClientUtils.addEditionalCriteria(criteria, lastCriteria);
		ClientUtils.fetchData(lastCriteria, cb, "BuildingsDS",
				"fetchCustSearchResult");
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
						"zone", "ზონა", 180), has_building);
	}

}
