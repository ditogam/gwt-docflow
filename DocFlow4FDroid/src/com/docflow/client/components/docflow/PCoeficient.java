package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PCoeficient extends VLayout {
	private DataSource dsZoneDS;
	private DataSource dsCustomer;

	private SelectItem siCustomerType;
	private SelectItem siZones;
	private SelectItem siBlocks;
	private TextItem tiCancelary;

	private ButtonItem biSaveData;
	private AddressComponent addrComp;
	private FloatItem fiCoef;
	private CurrentTimeItem diStart;

	private DynamicForm dmCriteria;
	private ListGrid lgCustomers;
	private ListGrid lgCoefCustomers;

	private ToolStripButton miSearch;

	private ToolStrip tsMain;

	public PCoeficient() {

		tsMain = new ToolStrip();
		tsMain.setWidth100();
		HashMap<String, String> listSqls = new HashMap<String, String>();
		listSqls.put("" + ClSelection.T_REGION, "" + ClSelection.T_REGION);
		listSqls.put("" + ClSelection.T_CUST_TYPE, "" + ClSelection.T_CUST_TYPE);
		listSqls.put("" + ClSelection.T_BLOCKS, "" + ClSelection.T_BLOCKS);
		dsZoneDS = DocFlow.getDataSource("ZoneDS");
		dsCustomer = DocFlow.getDataSource("CustomerDS");
		miSearch = new ToolStripButton("Search");
		biSaveData = new ButtonItem("saveData", "Set Coef");

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

		fiCoef = new FloatItem("fiCoef", "Coef");
		// fiCoef.setMask("0.0000");
		fiCoef.setValue("1.0001");

		diStart = new CurrentTimeItem("diStart", "Date");
		diStart.setUseTextField(false);
		diStart.setValue(DocFlow.getCurrentDate());

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
		lgCustomers.setShowRowNumbers(true);

		DataSource dsSelectedData = new DataSource();
		dsSelectedData.setClientOnly(true);

		DataSourceIntegerField dsfcusid = new DataSourceIntegerField("cusid",
				"Customer ID");
		dsfcusid.setPrimaryKey(true);

		DataSourceTextField dsZone = new DataSourceTextField("zone", "Zone");
		dsSelectedData.setFields(dsfcusid, dsZone);
		// dsSelectedData.setDropExtraFields(true);

		lgCoefCustomers = new ListGrid();
		// lgCoefCustomers.setDataSource(dsSelectedData);
		lgCoefCustomers.setAutoFetchData(false);
		lgCoefCustomers.setCanAcceptDroppedRecords(true);
		lgCoefCustomers.setCanRemoveRecords(true);
		lgCoefCustomers.setAutoFetchData(false);
		lgCoefCustomers.setPreventDuplicates(true);
		lgCoefCustomers.setHeight100();
		lgCoefCustomers.setWidth100();
		lgCoefCustomers.setShowRowNumbers(true);

		lgCoefCustomers.setFields(new ListGridField("cusid", "Customer ID"),
				new ListGridField("zone", "Zone"));

		HLayout hl = new HLayout();
		hl.setHeight100();
		hl.setWidth100();
		vl.addMember(hl);

		hl.addMember(lgCustomers);
		VStack vStack = new VStack();
		vStack.setHeight100();
		vStack.setWidth("30");
		vStack.setAlign(Alignment.CENTER);
		hl.addMember(vStack);

		TransferImgButton arrowImgAllRight = new TransferImgButton(
				TransferImgButton.RIGHT_ALL);
		arrowImgAllRight.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dsCustomer.fetchData(createCriteria(), new DSCallback() {

					@Override
					public void execute(DSResponse response, Object rawData,
							DSRequest request) {
						Record[] records = response.getData();
						// ArrayList<Record> newRecs = new ArrayList<Record>();
						RecordList rs = lgCoefCustomers.getRecordList();

						for (Record record : records) {
							int cusid = record.getAttributeAsInt("cusid");

							if (rs.find("cusid", cusid) == null) {
								Record rec = new Record();
								rec.setAttribute("cusid", cusid);
								rec.setAttribute("zone",
										record.getAttribute("zone"));
								lgCoefCustomers.addData(rec);
							}
						}
					}
				});
			}
		});

		TransferImgButton arrowImg = new TransferImgButton(
				TransferImgButton.RIGHT);
		arrowImg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				lgCoefCustomers.transferSelectedData(lgCustomers);
			}
		});

		TransferImgButton arrowImgLeft = new TransferImgButton(
				TransferImgButton.LEFT);
		arrowImgLeft.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				lgCoefCustomers.removeSelectedData();
			}
		});
		vStack.addMember(arrowImgAllRight);
		vStack.addMember(arrowImg);
		vStack.addMember(arrowImgLeft);

		lgCoefCustomers.setHeight100();
		lgCoefCustomers.setWidth("40%");
		hl.addMember(lgCoefCustomers);

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

		// vl.addMember(hStack);

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
		miSearch.setDisabled(!DocFlow
				.hasPermition(PermissionNames.CAN_CHANGE_COEDICIENT));
		biSaveData.setDisabled(!DocFlow
				.hasPermition(PermissionNames.CAN_CHANGE_COEDICIENT));

	}

	private void addZoneCriteria(Criteria cr, FormItem fi, String fieldName) {
		Long criteria = null;
		try {
			criteria = Long.parseLong(fi.getValue().toString());
		} catch (Exception e) {

		}
		if (criteria != null && criteria.longValue() >= 0)
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
		addZoneCriteria(cr, siBlocks, "block");
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

	protected int getItemValue(SelectItem item) {
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

	protected void saveData() {

		double coef = 0;

		try {

			Object v = fiCoef.getValue();
			String s = v.toString();
			coef = Double.parseDouble(s);
		} catch (Exception e) {
			SC.say("Please enter normal coeficient");
			return;
		}

		// if (getItemValue(addrComp.getSiSubregion()) == 0) {
		// SC.say("Please enter normal new Sub Region");
		// return;
		// }
		RecordList rs = lgCoefCustomers.getRecordList();
		if (rs == null || rs.getLength() == 0) {
			SC.say("Please add some data!!!");
			return;
		}
		int customerIds[] = new int[rs.getLength()];
		for (int i = 0; i < customerIds.length; i++) {
			customerIds[i] = rs.get(i).getAttributeAsInt("cusid");
		}
		String cancelary = tiCancelary.getValueAsString();
		if (cancelary == null || cancelary.trim().length() == 0) {
			SC.say("Please enter Cancelary #!!!");
			return;
		}
		SplashDialog.showSplash();
		DocFlow.docFlowService.saveCoefToCustomers(customerIds, coef, diStart
				.getValueAsDate().getTime(), DocFlow.user_id, cancelary,
				new AsyncCallback<Integer[]>() {

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						SC.say(caught.toString());
					}

					@Override
					public void onSuccess(Integer[] result) {
						search();

						ArrayList<Record> recs = new ArrayList<Record>();

						if (result != null) {
							RecordList rs = lgCoefCustomers.getRecordList();
							for (Integer cus_id : result) {
								Record or = rs.find("cusid", cus_id.intValue());
								if (or != null)
									recs.add(or);
							}
						}
						lgCoefCustomers.selectAllRecords();
						lgCoefCustomers.removeSelectedData();
						RecordList rs = lgCoefCustomers.getRecordList();
						for (Record r : recs) {
							rs.add(r);
						}
						if (recs.size() > 0) {
							SC.warn("Some records have not proceeded!!!!");
						}
						SplashDialog.hideSplash();

					}
				});

	}

	private void search() {
		Criteria cr = createCriteria();
		lgCustomers.fetchData(cr);
	}

	private Criteria createCriteria() {
		Criteria cr = new Criteria();
		addZoneCriteria(cr, siZones, "zone");
		addZoneCriteria(cr, addrComp.getSiRegion(), "regionid");
		addZoneCriteria(cr, addrComp.getSiSubregion(), "subregionid");
		addZoneCriteria(cr, siCustomerType, "custypeid");
		addZoneCriteria(cr, addrComp.getSiStreet(), "streetid");
		cr.addCriteria("uniq", SC.generateID());
		cr.addCriteria("coef", 1);
		return cr;
	}

	private void setResults(HashMap<String, ArrayList<ClSelectionItem>> result) {
		result = result == null ? new HashMap<String, ArrayList<ClSelectionItem>>()
				: result;
		addrComp = new AddressComponent(true, true, result.get(""
				+ ClSelection.T_REGION));
		setSelectItems(siCustomerType, result.get("" + ClSelection.T_CUST_TYPE));
		tiCancelary = new TextItem("tiCancelary", "კანცელარიის #");
		addrComp.getSiRegion().setTitle("Region");
		addrComp.getSiSubregion().setTitle("Sub Region");
		addrComp.getSiCity().setTitle("City");
		addrComp.getSiStreet().setTitle("Street");
		biSaveData.setStartRow(false);
		biSaveData.setEndRow(false);
		siBlocks = new SelectItem("siBlocks", "Block");
		setSelectItems(siBlocks, result.get("" + ClSelection.T_BLOCKS));
		// LinkedHashMap<String, String> map = new LinkedHashMap<String,
		// String>();
		// map.put("-1", "---");
		// map.put("1", "1");
		// map.put("2", "2");
		// map.put("3", "3");
		// map.put("4", "4");
		// siBlocks.setValueMap(map);
		siBlocks.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				creteriaChanged();

			}
		});
		FormItem[] items = new FormItem[] { addrComp.getSiRegion(),
				addrComp.getSiSubregion(), addrComp.getSiCity(),
				addrComp.getSiStreet(), siBlocks, siZones, siCustomerType,
				fiCoef, tiCancelary, diStart, biSaveData };
		dmCriteria.setFields(items);
		ChangedHandler addressChange = new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
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
		siCustomerType.setValue("1");
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

}
