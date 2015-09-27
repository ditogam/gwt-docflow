package com.docflow.client.components.map;

import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ClSelection;
import com.docflow.shared.CustomerShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WBuildingsDialog extends Window {

	public static void showWindow(Record record) {
		new WBuildingsDialog(record).show();
	}

	private PBuildingsForm form;

	private ButtonItem biShowHide;

	private DynamicForm formSearch;

	private ComboBoxItem cbCity;
	private ComboBoxItem cbStreet;
	private ComboBoxItem cbZones;
	private IntegerItem iiCusID;
	private TextItem tiCusName;
	private ButtonItem biSearch;

	private VLayout vlCustomerSearch;
	private ListGrid lgRealCustomers;
	private ListGrid lgBuildingCustomers;
	private Criteria lastCriteria;

	private DataSource dsCustomer;
	private DataSource dsBuildings;

	private DataSource dsLocal;

	private String globalZone = null;
	private Integer buid;

	public WBuildingsDialog(Record record) {
		this.setTitle("აბონენტების მიბმა");
		dsCustomer = DocFlow.getDataSource("CustomerDS");
		dsBuildings = DocFlow.getDataSource("BuildingsDS");
		dsLocal = DocFlow.getDataSource("CustShortDS");
		// dsLocal.setClientOnly(true);
		// dsLocal.setDropExtraFields(false);
		// DataSourceField cusId = new DataSourceField("cusid",
		// FieldType.INTEGER);
		// cusId.setPrimaryKey(true);
		// dsLocal.setFields(cusId);

		Integer subregionid = record.getAttributeAsInt("raiid");
		buid = record.getAttributeAsInt("buid");

		biShowHide = new ButtonItem("biSearch", "");
		biShowHide.setIcon("form/expand.png");
		biShowHide.setStartRow(false);
		biShowHide.setEndRow(false);
		biShowHide
				.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						showHideSearch();

					}
				});

		form = new PBuildingsForm(record, biShowHide, true);

		this.addItem(form);
		ToolStrip toolStrip = new ToolStrip();
		ToolStripButton tsbRemove = new ToolStripButton("",
				"[SKIN]/TransferIcons/delete.png");
		toolStrip.addButton(tsbRemove);
		tsbRemove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				removeSelectedCustomers();

			}
		});

		ToolStripButton tsbMove = new ToolStripButton("",
				"[SKIN]/TransferIcons/down_last.png");
		toolStrip.addButton(tsbMove);
		tsbMove.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveSelectedRecords();

			}
		});

		// this.addItem(toolStrip);
		VLayout hlGrids = new VLayout();
		hlGrids.setHeight100();

		vlCustomerSearch = new VLayout();
		vlCustomerSearch.setHeight("50%");
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

		ClientUtils.fillSelectionCombo(cbCity, ClSelection.T_CITY_NEW,
				subRegionCrit);
		ClientUtils.fillSelectionCombo(cbStreet, ClSelection.T_STREET,
				streetCrit);
		ClientUtils.fillSelectionCombo(cbZones, ClSelection.T_ZONES_NEW,
				subRegionCrit);

		ClientUtils.makeDependancy(form.siSubregion, true, new FormItemDescr(
				cbCity), new FormItemDescr(cbZones));

		ClientUtils.makeDependancy(cbCity, true, new FormItemDescr(cbStreet));

		biSearch = new ButtonItem("biSearch", "Search");
		biSearch.setIcon("[SKIN]/actions/search.png");
		biSearch.setStartRow(false);
		biSearch.setEndRow(false);

		biSearch.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				searchCustomers();

			}
		});

		formSearch = new DynamicForm();
		formSearch.setNumCols(4);
		formSearch.setTitleOrientation(TitleOrientation.TOP);
		formSearch.setFields(cbCity, cbStreet, cbZones, iiCusID, tiCusName,
				biSearch);
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
		lgRealCustomers.setCanDragSelectText(true);
		lgRealCustomers.setShowRowNumbers(true);
		vlCustomerSearch.addMember(lgRealCustomers);
		// vlCustomerSearch.setShowEdges(true);

		hlGrids.addMember(vlCustomerSearch);
		vlCustomerSearch.addMember(toolStrip);

		vlCustomerSearch.hide();
		vlCustomerSearch
				.addVisibilityChangedHandler(new VisibilityChangedHandler() {

					@Override
					public void onVisibilityChanged(VisibilityChangedEvent event) {
						if (event.getIsVisible()) {
							biShowHide.setIcon("form/collapse.png");
						} else {
							biShowHide.setIcon("form/expand.png");

						}

					}
				});

		lgBuildingCustomers = new ListGrid();

		lgBuildingCustomers.setDataSource(dsLocal);
		lgBuildingCustomers.setAutoFetchData(false);
		lgBuildingCustomers.setCanDragSelectText(true);
		lgBuildingCustomers.setShowRowNumbers(true);
		lgBuildingCustomers.addDropHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				canTransferData(event);
			}
		});
		lgBuildingCustomers.setCanAcceptDroppedRecords(true);
		lgBuildingCustomers.setPreventDuplicates(true);

		Criteria cr = new Criteria();
		cr.setAttribute("building_id", buid);
		lgBuildingCustomers.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				setBuildingCustomerData(response.getData());

			}
		});

		createFields(lgBuildingCustomers);
		//

		lgBuildingCustomers.setHeight100();

		lgBuildingCustomers.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				makeSearch();

			}
		});

		hlGrids.addMember(lgBuildingCustomers);

		this.addItem(hlGrids);

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
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
	}

	protected void makeSearch() {
		ListGridRecord rec = lgBuildingCustomers.getSelectedRecord();
		if (rec == null)
			return;

		try {
			if (!vlCustomerSearch.isVisible())
				return;
		} catch (Exception e) {
			return;
		}

		int cus_id = rec.getAttributeAsInt("cusid");
		SplashDialog.showSplash();
		DocFlow.docFlowService.getCustomerShort(cus_id,
				new AsyncCallback<CustomerShort>() {

					@Override
					public void onSuccess(CustomerShort result) {
						SplashDialog.hideSplash();
						doSearch(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						SC.warn(caught.getMessage());

					}
				});

	}

	protected void doSearch(CustomerShort result) {
		cbCity.setValue(result.getCityid());
		cbStreet.clearValue();
		cbZones.setValue(result.getZone() + "");
		Criteria cr = cbStreet.getOptionCriteria();
		cr.setAttribute("parentId", result.getCityid());
		cbStreet.setOptionCriteria(cr);
		cbStreet.setValue(result.getStreetid());
		form.tBuilding_no.setValue(result.getHome());
		iiCusID.clearValue();
		tiCusName.clearValue();
		searchCustomers();
	}

	protected void saveData() {
		final String zone = null;
		Record[] records = lgBuildingCustomers.getRecords();
		records = records == null ? new Record[] {} : records;
		final Record[] fRecords = records;
		if (records.length == 0) {
			SC.ask("სია ცარიელია, დარწმუნებული ხართ რომ გინდათ ცარიელის დამატება?",
					new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							if (value.booleanValue())
								saveData(zone, fRecords);

						}
					});
			return;
		}
		saveData(zone, records);
	}

	private void saveData(String zone, final Record[] records) {
		for (Record record : records) {
			String cZone = record.getAttribute("zone");
			if (zone == null)
				zone = cZone;
			if (!cZone.equals(zone)) {
				SC.ask("არსებობს სხვადასხვა ზონის აბონენტები, გთხოვთ მიაქციოთ ყურადღება!!!",
						new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if (value != null && value.booleanValue())
									saveDataFinal();
							}
						});
				return;
			}

		}
		saveDataFinal();
	}

	private void saveDataFinal() {
		Record rec = new Record();
		rec.setAttribute("buid", buid);
		String custs = getBuildingCustomers();
		rec.setAttribute("feature_text", custs);
		DSRequest req = new DSRequest();

		req.setOperationId("saveBuildingCustomers");
		try {
			dsBuildings.addData(rec, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					GMapPanel.instance.redrawWMS();
					destroy();

				}
			}, req);
		} catch (Exception e) {
			SC.warn(e.toString());
		}
	}

	protected void moveSelectedRecords() {
		if (canTransferData(null))
			lgBuildingCustomers.transferSelectedData(lgRealCustomers);

	}

	protected void removeSelectedCustomers() {
		final Record[] records = lgBuildingCustomers.getSelectedRecords();
		if (records == null || records.length < 1)
			return;
		SC.ask("გინდათ წაშლა???", new BooleanCallback() {

			@Override
			public void execute(Boolean value) {
				if (value) {
					if (lgBuildingCustomers.getRecordList().getLength() == records.length) {
						setSearchDisabled(false, null);
					}
					lgBuildingCustomers.removeSelectedData(new DSCallback() {

						@Override
						public void execute(DSResponse response,
								Object rawData, DSRequest request) {
							searchCustomers();

						}
					}, new DSRequest());

				}

			}
		});

	}

	protected void setBuildingCustomerData(Record[] records) {
		if (records == null || records.length < 1)
			return;
		String zone = checkIfExistsWithDifferentZones(records);
		// lgBuildingCustomers.setData(records);
		// dsLocal.invalidateCache();
		setSearchDisabled(true, zone);

	}

	private String checkIfExistsWithDifferentZones(Record[] records) {
		String zone = null;
		boolean onceCheced = false;
		for (Record record : records) {
			String cZone = record.getAttribute("zone");
			if (zone == null)
				zone = cZone;
			if (!cZone.equals(zone) && !onceCheced) {
				SC.say("არსებობს სხვადასხვა ზონის აბონენტები, გთხოვთ მიაქციოთ ყურადღება!!!");
				onceCheced = true;
			}

		}
		return zone;
	}

	private void setSearchDisabled(boolean disabled, String zone) {
		globalZone = zone;
		formSearch.clearValues();
		cbZones.setValue(zone);
		cbZones.setDisabled(false);

		// FormItem[] formItems = formSearch.getFields();
		// for (FormItem formItem : formItems) {
		// if (!(formItem instanceof ButtonItem) )
		// formItem.setDisabled(disabled);
		// }
	}

	// protected void copySelectedData(Record[] selectedRecords) {
	// lgRealCustomers.startEditing();
	// RecordList rs = lgBuildingCustomers.getRecordList();
	// RecordList rs1 = lgRealCustomers.getRecordList();
	// for (Record record : selectedRecords) {
	// int cusid = record.getAttributeAsInt("cusid");
	// Record r = rs.find("cusid", cusid);
	// if (r != null)
	// continue;
	// rs.add(record);
	//
	// }
	//
	// lgRealCustomers.removeSelectedData();
	// lgRealCustomers.saveAllEdits();
	//
	// dsLocal.invalidateCache();
	// // refreshCustomerData();
	// }

	protected void searchCustomers() {
		Long iCusId = getLongValue(iiCusID);
		Long iStreetId = getLongValue(cbStreet);
		Long iZone = getLongValue(cbZones);
		String buildNum = getString(form.tBuilding_no);
		Object cus_name = tiCusName.getValue();

		if (iCusId == null && iStreetId == null && iZone == null
				&& (cus_name == null || cus_name.toString().trim().isEmpty())) {
			return;
		}
		lastCriteria = new Criteria();
		if (iCusId != null)
			lastCriteria.setAttribute("cusid", iCusId);
		else {
			if (iStreetId != null)
				lastCriteria.setAttribute("streetid", iStreetId);
			if (iZone != null)
				lastCriteria.setAttribute("zone", iZone);
			if (buildNum != null)
				lastCriteria.setAttribute("buildNum", buildNum);
			if (cus_name != null && !cus_name.toString().trim().isEmpty())
				lastCriteria.setAttribute("cus_name", cus_name.toString()
						.trim());
		}
		refreshCustomerData();

	}

	private void refreshCustomerData() {
		String buildingCustomers = getBuildingCustomers();
		if (buildingCustomers != null && buildingCustomers.length() > 0)
			lastCriteria.setAttribute("not_cust", buildingCustomers);
		lastCriteria.setAttribute("building_free", 1);
		lgRealCustomers.filterData(lastCriteria);
	}

	private String getBuildingCustomers() {
		String str = "";
		Record[] records = lgBuildingCustomers.getRecordList().toArray();
		TreeMap<String, String> mp = new TreeMap<String, String>();
		for (Record record : records) {
			String s = record.getAttributeAsInt("cusid") + "";
			mp.put(s, s);
		}
		for (String record : mp.keySet()) {
			if (str.length() > 0)
				str += ",";
			str += record;
		}
		if (str.length() == 0)
			str = null;
		return str;
	}

	private Long getLongValue(FormItem formItem) {
		try {
			Object obj = formItem.getValue();
			return Long.parseLong(obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private String getString(FormItem formItem) {
		try {
			Object obj = formItem.getValue();
			return obj.toString();
		} catch (Exception e) {
			return null;
		}
	}

	protected void showHideSearch() {
		if (vlCustomerSearch.isVisible()) {
			biShowHide.setIcon("form/expand.png");
			vlCustomerSearch.hide();
		} else {
			biShowHide.setIcon("form/collapse.png");
			vlCustomerSearch.show();
		}

	}

	private void createFields(ListGrid listGrid) {
		ListGridField cusid = new ListGridField("cusid", "აბონენტი #", 60);
		listGrid.setFields(cusid, new ListGridField("cusname",
				"აბონენტის სახელი", 180), new ListGridField("cityname",
				"ქალაქ/სოფელი", 100), new ListGridField("streetname", "ქუჩა",
				100), new ListGridField("home", "სახლი", 60),
				new ListGridField("flat", "ბინის #", 50), new ListGridField(
						"zone", "ზონა", 180));
	}

	private boolean canTransferData(DropEvent event) {
		Record[] selectedRecords = lgRealCustomers.getSelectedRecords();
		if (event != null)
			event.cancel();
		String zone = null;
		for (Record record : selectedRecords) {
			String cZone = record.getAttribute("zone");
			if (zone == null)
				zone = cZone;

			if (!cZone.equals(zone)
					|| (globalZone != null && !globalZone.equals(cZone))) {
				SC.say("არსებობს სხვადასხვა ზონის აბონენტები, გთხოვთ მიაქციოთ ყურადღება!!!");
			}

		}
		do_operate_customers(selectedRecords, zone);

		return false;
	}

	private void do_operate_customers(Record[] selectedRecords, String zone) {
		globalZone = zone;
		setSearchDisabled(true, zone);
		for (final Record record : selectedRecords) {
			lgRealCustomers.removeData(record, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					lgBuildingCustomers.addData(record);

				}
			});
		}
	}

}
