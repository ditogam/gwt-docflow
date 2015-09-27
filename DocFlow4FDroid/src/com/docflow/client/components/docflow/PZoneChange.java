package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ClSelection;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PZoneChange extends VLayout {
	private DataSource dsZoneDS;
	private DataSource dsCustomer;

	private SelectItem siCustomerType;
	private SelectItem siZones;
	private TextItem tiNewZone;
	private ButtonItem biSaveData;
	private AddressComponent addrComp;
	private DynamicForm dmCriteria;
	private ListGrid lgCustomers;
	private ListGrid lgZoneCustomers;

	private ToolStripButton miSearch;

	private ToolStrip tsMain;

	private static int[] restrictedEdits = new int[] { 0, 1, 2, 9, 10 };
	private static int CHARCOUNT = 12;

	public PZoneChange() {
		restrictedEdits = DocFlow.user_obj.getZoneConfiguration()
				.getRestricted_edits();
		CHARCOUNT = DocFlow.user_obj.getZoneConfiguration().getCharcount();
		tsMain = new ToolStrip();
		tsMain.setWidth100();
		HashMap<String, String> listSqls = new HashMap<String, String>();
		listSqls.put("" + ClSelection.T_REGION, "" + ClSelection.T_REGION);
		listSqls.put("" + ClSelection.T_CUST_TYPE, "" + ClSelection.T_CUST_TYPE);
		dsZoneDS = DocFlow.getDataSource("ZoneDS");
		dsCustomer = DocFlow.getDataSource("CustomerDS");
		miSearch = new ToolStripButton("Search");
		tiNewZone = new TextItem("newzone", "New Zone");
		biSaveData = new ButtonItem("saveData", "Change Zone");

		tiNewZone.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				newZoneKeyPressed(event);

			}
		});
		VLayout vl = new VLayout();

		dmCriteria = new DynamicForm();
		dmCriteria.setNumCols(4);
		dmCriteria.setHeight("8%");
		dmCriteria.setTitleOrientation(TitleOrientation.TOP);
		siCustomerType = new SelectItem("custypeid", "Customer Type");
		siZones = new SelectItem("zones", "Zones");
		siZones.setValueField("zone");
		siZones.setAutoFetchData(false);
		siZones.setDisplayField("zone");
		siZones.setOptionDataSource(dsZoneDS);

		tsMain.setHeight("15");
		vl.addMember(tsMain);
		tsMain.addButton(miSearch);
		vl.addMember(dmCriteria);
		lgCustomers = new ListGrid();
		lgCustomers.setDataSource(dsCustomer);
		lgCustomers.setAutoFetchData(false);
		lgCustomers.setShowResizeBar(false);
		lgCustomers.setCanDragRecordsOut(true);
		lgCustomers.setDragDataAction(DragDataAction.COPY);
		lgCustomers.setHeight100();
		lgCustomers.setHeight100();

		DataSource dsSelectedData = new DataSource();
		dsSelectedData.setClientOnly(true);

		DataSourceIntegerField dsfcusid = new DataSourceIntegerField("cusid",
				"Customer ID");
		dsfcusid.setPrimaryKey(true);

		DataSourceTextField dsZone = new DataSourceTextField("zone", "Zone");
		dsSelectedData.setFields(dsfcusid, dsZone);

		lgZoneCustomers = new ListGrid();
		lgZoneCustomers.setDataSource(dsSelectedData);
		lgZoneCustomers.setAutoFetchData(false);
		lgZoneCustomers.setCanAcceptDroppedRecords(true);
		lgZoneCustomers.setCanRemoveRecords(true);
		lgZoneCustomers.setAutoFetchData(false);
		lgZoneCustomers.setPreventDuplicates(true);
		lgZoneCustomers.setHeight100();
		lgZoneCustomers.setWidth100();

		HStack hStack = new HStack(10);
		hStack.setWidth100();
		hStack.setHeight100();
		hStack.setShowEdges(true);

		VStack vStack = new VStack();
		vStack.setHeight100();
		vStack.setWidth("60%");
		vStack.addMember(lgCustomers);
		hStack.addMember(vStack);
		TransferImgButton arrowImg = new TransferImgButton(
				TransferImgButton.RIGHT);
		arrowImg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				lgZoneCustomers.transferSelectedData(lgCustomers);
			}
		});

		TransferImgButton arrowImgLeft = new TransferImgButton(
				TransferImgButton.LEFT);
		arrowImgLeft.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				lgZoneCustomers.removeSelectedData();
			}
		});
		hStack.addMember(arrowImg);
		hStack.addMember(arrowImgLeft);
		VStack vStack2 = new VStack();
		vStack2.setHeight100();
		vStack2.setWidth("40%");
		vStack2.addMember(lgZoneCustomers);
		hStack.addMember(vStack2);

		miSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				search();

			}
		});

		lgCustomers
				.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.events.ClickEvent event) {

					}

				});

		vl.addMember(hStack);

		this.addMember(vl);
		DocFlow.docFlowService
				.getListTypesForDocument(
						listSqls,
						-1,
						new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

							@Override
							public void onFailure(Throwable caught) {
								setResults(null);

							}

							@Override
							public void onSuccess(
									HashMap<String, ArrayList<ClSelectionItem>> result) {

								setResults(result);
							}
						});
	}

	private void addZoneCriteria(Criteria cr, FormItem fi, String fieldName) {
		Long criteria = null;
		try {
			criteria = Long.parseLong(fi.getValue().toString());
		} catch (Exception e) {

		}
		if (criteria != null)
			cr.addCriteria(fieldName, criteria.toString());
	}

	public void clearFields() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("-1", "---");
		try {
			siCustomerType.setValueMap(map);
			siCustomerType.setValue("-1");
			addrComp.getSiRegion().setValue("-1");
			addrComp.getSiSubregion().setValueMap(map);
			addrComp.getSiSubregion().setValue("-1");
			addrComp.getSiCity().setValueMap(map);
			addrComp.getSiCity().setValue("-1");
			addrComp.getSiStreet().setValueMap(map);
			addrComp.getSiStreet().setValue("-1");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void creteriaChanged() {
		siZones.setValue((Integer) null);
		Criteria cr = new Criteria();
		addZoneCriteria(cr, addrComp.getSiRegion(), "regionid");
		addZoneCriteria(cr, addrComp.getSiSubregion(), "subregionid");
		addZoneCriteria(cr, addrComp.getSiCity(), "cityid");
		addZoneCriteria(cr, addrComp.getSiStreet(), "streetid");
		cr.addCriteria("uniq", SC.generateID());
		siZones.setOptionCriteria(cr);
		siZones.fetchData(new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				if (response.getData() != null && response.getData().length > 0)
					siZones.setValue(response.getData()[0]
							.getAttributeAsDouble(siZones.getValueField()));

			}
		});
	}

	protected int getItemValue(FormItem item) {
		int val = 0;
		try {
			val = Integer.parseInt(item.getValue().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (val < 0)
			val = 0;
		return val;
	}

	protected void newZoneKeyPressed(KeyPressEvent event) {
		int[] selection = tiNewZone.getSelectionRange();
		if (selection[0] > selection[1]) {
			int tmp = selection[0];
			selection[0] = selection[1];
			selection[1] = tmp;
		}
		event.cancel();
		for (int i = 0; i < restrictedEdits.length; i++) {
			if (restrictedEdits[i] >= selection[0]
					&& restrictedEdits[i] <= selection[1]) {
				tiNewZone.setSelectionRange(selection[0] + 1, selection[0] + 1);
				return;
			}

		}
		if (selection[0] >= CHARCOUNT)
			return;
		char c = (char) event.getCharacterValue().intValue();
		String value = event.getItem().getValue().toString();
		char[] cr = value.toCharArray();
		cr[selection[0]] = c;
		value = new String(cr);
		tiNewZone.setValue(value);
		tiNewZone.setSelectionRange(selection[0] + 1, selection[0] + 1);
	}

	protected void saveData() {

		long zoneId = 0;
		try {
			if (tiNewZone.getValue().toString().length() != CHARCOUNT)
				throw new Exception();
			zoneId = Long.parseLong(tiNewZone.getValue().toString());
		} catch (Exception e) {
			SC.say("Please enter normal new Zone", new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					tiNewZone.focusInItem();
					tiNewZone.selectValue();

				}
			});
			return;
		}
		if (getItemValue((SelectItem) addrComp.getSiSubregion()) == 0) {
			SC.say("Please enter normal new Sub Region");
			return;
		}
		Record[] recs = lgZoneCustomers.getRecords();
		if (recs == null || recs.length == 0) {
			SC.say("Please add some data!!!");
			return;
		}
		int customerIds[] = new int[recs.length];
		for (int i = 0; i < customerIds.length; i++) {
			customerIds[i] = recs[i].getAttributeAsInt("cusid");
		}
		SplashDialog.showSplash();
		DocFlow.docFlowService.changeZoneToCustomers(customerIds, zoneId,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						SC.say(caught.toString());
					}

					@Override
					public void onSuccess(Void result) {
						search();
						lgZoneCustomers.selectAllRecords();
						lgZoneCustomers.removeSelectedData();
						SplashDialog.hideSplash();

					}
				});

	}

	private void search() {
		Criteria cr = new Criteria();
		addZoneCriteria(cr, siZones, "zone");
		addZoneCriteria(cr, siCustomerType, "custypeid");
		addZoneCriteria(cr, addrComp.getSiStreet(), "streetid");
		cr.addCriteria("uniq", SC.generateID());
		lgCustomers.fetchData(cr);
	}

	private void setNewZone(int region, int subregion) {
		String mask = "";
		String value = "" + region
				+ (((subregion < 10) ? "0" : "") + "" + subregion);
		for (int i = 0; i < CHARCOUNT; i++) {
			mask += "#";
			if (i > 2)
				value += "0";
		}
		tiNewZone.setMask(mask);
		tiNewZone.setValue(value);

	}

	private void setResults(HashMap<String, ArrayList<ClSelectionItem>> result) {
		result = result == null ? new HashMap<String, ArrayList<ClSelectionItem>>()
				: result;
		addrComp = new AddressComponent(true, true, result.get(""
				+ ClSelection.T_REGION));
		setSelectItems(siCustomerType, result.get("" + ClSelection.T_CUST_TYPE));

		addrComp.getSiRegion().setTitle("Region");
		addrComp.getSiSubregion().setTitle("Sub Region");
		addrComp.getSiCity().setTitle("City");
		addrComp.getSiStreet().setTitle("Street");
		biSaveData.setStartRow(false);
		biSaveData.setEndRow(false);
		FormItem[] items = new FormItem[] { addrComp.getSiRegion(),
				addrComp.getSiSubregion(), addrComp.getSiCity(),
				addrComp.getSiStreet(), siZones, siCustomerType, tiNewZone,
				biSaveData };
		dmCriteria.setFields(items);

		ChangedHandler addressChange = new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {

				if (event.getItem().equals(addrComp.getSiRegion())
						|| event.getItem().equals(addrComp.getSiSubregion())) {
					setZoneValue();
				}

				creteriaChanged();
			}
		};

		addrComp.getSiRegion().addChangedHandler(addressChange);
		addrComp.getSiSubregion().addChangedHandler(addressChange);
		addrComp.getSiCity().addChangedHandler(addressChange);
		addrComp.getSiStreet().addChangedHandler(addressChange);

		Criteria cr = new Criteria();
		addZoneCriteria(cr, addrComp.getSiRegion(), "regionid");
		cr.addCriteria("uniq", SC.generateID());
		siZones.setOptionCriteria(cr);
		setZoneValue();

		biSaveData
				.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						saveData();

					}
				});
	}

	private void setSelectItems(FormItem si, ArrayList<ClSelectionItem> items) {
		if (items == null) {
			items = new ArrayList<ClSelectionItem>();
		}
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("-1", "---");
		for (ClSelectionItem item : items) {
			map.put(item.getId() + "", item.getValue());
		}
		si.setValueMap(map);
		si.setValue("-1");
	}

	private void setZoneValue() {
		int regionId = getItemValue(addrComp.getSiRegion());
		int subRegionId = getItemValue(addrComp.getSiSubregion());
		setNewZone(regionId, subRegionId);
	}

}
