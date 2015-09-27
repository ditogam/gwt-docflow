package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.common.client.CardLayoutCanvas;
import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.TextMatchStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PBankLive extends VLayout {
	private DataSource dsBankLiveDetails;
	private DataSource dsDayCloseRegionDS;
	private DataSource dsDayCloseRegionDetailsDS;
	private DataSource dsReaderListDeviceDS;

	private DynamicForm dmCriteria;
	private ListGrid lgBankLive;
	private ListGrid lgBankDetails;
	private ListGrid lgCloseDayRegion;
	private ListGrid lgCloseDayRegionDetails;
	private ListGrid lgReaderList;

	private CardLayoutCanvas cl;

	private ToolStripButton miSearchLive;
	// private ToolStripButton miSearchDay;
	private ToolStripButton miSearchDayRegion;
	private ToolStripButton miSearchReaderList;

	private DataSource dsBankLive;

	private ToolStrip tsMain;

	private Record lastSelectedSource;

	private CurrentTimeItem diStart;
	private CurrentTimeItem diEnd;
	private SelectItem siCustomerType;
	private SelectItem siBanks;
	private SelectItem siBOper_AccType;
	private AddressComponent addrComp;
	private CheckboxItem cbiUseDate;
	private IntegerItem iiCusID;
	private ButtonItem biOperate;

	private ListGrid selectedGrid;

	public PBankLive() {

		lastSelectedSource = null;
		tsMain = new ToolStrip();
		tsMain.setWidth100();
		HashMap<String, String> listSqls = new HashMap<String, String>();
		listSqls.put("" + ClSelection.T_REGION, "" + ClSelection.T_REGION);
		listSqls.put("" + ClSelection.T_CUST_TYPE, "" + ClSelection.T_CUST_TYPE);
		listSqls.put("" + ClSelection.T_BANKS, "" + ClSelection.T_BANKS);
		listSqls.put("" + ClSelection.T_BOPER_ACC_TYPE, ""
				+ ClSelection.T_BOPER_ACC_TYPE);
		dsBankLive = DocFlow.getDataSource("BankLiveDS");
		dsDayCloseRegionDS = DocFlow.getDataSource("DayCloseRegionDS");
		dsDayCloseRegionDetailsDS = DocFlow.getDataSource("DayCloseDetailsDS");
		dsBankLiveDetails = DocFlow.getDataSource("BankLiveDetailsDS");
		dsReaderListDeviceDS = DocFlow.getDataSource("ReaderListDeviceDS");
		diStart = new CurrentTimeItem("diStart", "Start");
		diStart.setUseTextField(false);
		diEnd = new CurrentTimeItem("diEnd", "End");
		diEnd.setUseTextField(false);
		miSearchLive = new ToolStripButton("Bank Live");
		// biSearchLive.setStartRow(false);
		// biSearchLive.setEndRow(false);
		// miSearchDay = new ToolStripButton("Day Close");
		// biSearchDay.setStartRow(false);
		// biSearchDay.setEndRow(false);
		miSearchDayRegion = new ToolStripButton("Day Close by Region");
		// biSearchDayRegion.setStartRow(false);
		// biSearchDayRegion.setEndRow(false);
		miSearchReaderList = new ToolStripButton("Reader List Device");
		// biSearchReaderList.setStartRow(false);
		// biSearchReaderList.setEndRow(false);

		VLayout vl = new VLayout();

		dmCriteria = new DynamicForm();
		dmCriteria.setNumCols(4);
		dmCriteria.setHeight("8%");
		dmCriteria.setTitleOrientation(TitleOrientation.TOP);
		siCustomerType = new SelectItem("custypeid", "Customer Type");
		siBOper_AccType = new SelectItem("acc_id", "Account Type");
		siBOper_AccType.setWidth(250);
		siBanks = new SelectItem("bankid", "Bank");
		tsMain.setHeight("15");
		vl.addMember(tsMain);
		// Menu menu = new Menu();
		// menu.setShowShadow(true);
		// menu.setShadowDepth(3);
		//
		// menu.setItems(miSearchLive, miSearchDay, miSearchDayRegion,
		// miSearchReaderList);
		// ToolStripMenuButton menuButton = new ToolStripMenuButton("Menu",
		// menu);
		tsMain.addButton(miSearchLive);
		// tsMain.addButton(miSearchDay);
		tsMain.addButton(miSearchDayRegion);
		tsMain.addButton(miSearchReaderList);
		// tsMain.addMenuButton(menuButton);
		vl.addMember(dmCriteria);
		lgBankLive = new ListGrid();
		lgBankLive.setDataSource(dsBankLive);
		lgBankLive.setAutoFetchData(false);
		lgBankLive.setCanEdit(false);
		lgBankLive.setShowResizeBar(true);

		lgBankDetails = new ListGrid();
		lgBankDetails.setDataSource(dsBankLiveDetails);
		lgBankDetails.setAutoFetchData(false);
		lgBankDetails.setCanEdit(false);

		cl = new CardLayoutCanvas();

		miSearchLive.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				biOperate.hide();
				selectedGrid = null;
				lastSelectedSource = null;
				cl.showCard("live");
				lgBankLive.fetchData(dmCriteria.getValuesAsCriteria());

			}
		});

		miSearchDayRegion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				biOperate.show();
				biOperate.setTitle("დღის დახურვა");
				selectedGrid = null;
				lastSelectedSource = null;
				cl.showCard("dayRegion");
				searchDayCloseRegion(false, -1);

			}
		});
		lgBankLive.setHeight("50%");

		lgBankDetails.setHeight("50%");

		lgBankLive
				.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.events.ClickEvent event) {
						changed();

					}

				});

		VLayout vl1 = new VLayout();
		vl1.setWidth100();
		vl1.setHeight100();
		vl1.addMember(lgBankLive);
		vl1.addMember(lgBankDetails);

		cl.setWidth100();
		cl.setHeight("87%");
		cl.addCard("live", vl1);

		vl.addMember(cl);

		lgCloseDayRegion = new ListGrid() {
			// @Override
			// protected Canvas createRecordComponent(final ListGridRecord
			// record,
			// Integer colNum) {
			//
			// return renderButton(this, record, colNum, null);
			// }

		};
		//
		// // SummaryFunctionType.COUNT;
		// lgCloseDayRegion.setShowRecordComponents(true); // required to render
		// // button
		// lgCloseDayRegion.setShowRecordComponentsByCell(true); // required to
		// // render
		// button
		lgCloseDayRegion.setDataSource(dsDayCloseRegionDS);
		lgCloseDayRegion.setAutoFetchData(false);
		lgCloseDayRegion.setCanEdit(false);
		lgCloseDayRegion.setWidth100();

		vl1 = new VLayout();
		vl1.setWidth100();
		vl1.setHeight100();

		lgCloseDayRegion.setHeight("50%");

		lgCloseDayRegionDetails = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				return renderButton(this, record, colNum, null);
			}

			@Override
			public Canvas updateRecordComponent(ListGridRecord record,
					Integer colNum, Canvas component, boolean recordChanged) {
				return renderButton(this, record, colNum, component);
			};
		};

		lgCloseDayRegion
				.addSelectionChangedHandler(new SelectionChangedHandler() {

					@Override
					public void onSelectionChanged(SelectionEvent event) {
						selectedGrid = lgCloseDayRegion;
						lastSelectedSource = lgCloseDayRegion
								.getSelectedRecord();
						String fieldname = "btn";
						boolean notActive = lastSelectedSource
								.getAttribute(fieldname) == null
								|| lastSelectedSource.getAttribute(fieldname)
										.trim().equals("");
						biOperate.setDisabled(notActive);
						changedDaily(lgCloseDayRegion, lgCloseDayRegionDetails,
								(ListGridRecord) lastSelectedSource, false,
								false);

					}
				});

		// ClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
		//
		// @Override
		// public void onClick(
		// com.smartgwt.client.widgets.events.ClickEvent event) {
		//
		//
		// }
		//
		// });

		// SummaryFunctionType.COUNT;
		lgCloseDayRegionDetails.setShowRecordComponents(true); // required to
																// render
		// button
		lgCloseDayRegionDetails.setShowRecordComponentsByCell(true); // required
																		// to
		// render
		// button
		lgCloseDayRegionDetails.setDataSource(dsDayCloseRegionDetailsDS);
		lgCloseDayRegionDetails.setAutoFetchData(false);
		lgCloseDayRegionDetails.setCanEdit(false);
		lgCloseDayRegionDetails.setShowResizeBar(true);
		lgCloseDayRegionDetails.setHeight("50%");
		lgCloseDayRegionDetails.setShowFilterEditor(true);
		// ListGridField[] fields=lgCloseDayRegionDetails.getFields();
		// for (ListGridField listGridField : fields) {
		// listGridField.setCanFilter(canFilter)
		// }

		vl1.addMember(lgCloseDayRegion);
		vl1.addMember(lgCloseDayRegionDetails);

		cl.addCard("dayRegion", vl1);

		lgReaderList = new ListGrid() {
			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if (getFieldName(colNum).equals("cusid")) {
					int active = record.getAttributeAsInt("active");
					int m3 = record.getAttribute("m3") != null ? record
							.getAttributeAsInt("m3") : 0;
					if (active != 0 || m3 != 0) {
						String supstyle = "font-weight:bold; color:#d64949;";
						return supstyle;
					}
					{
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}

			// @Override
			// protected Canvas createRecordComponent(final ListGridRecord
			// record,
			// Integer colNum) {
			// return renderButton(this, record, colNum, null);
			// }

		};
		lgReaderList.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedGrid = lgReaderList;
				lastSelectedSource = selectedGrid.getSelectedRecord();
				biOperate.setDisabled(false);
			}
		});
		// lgReaderList.setShowRecordComponents(true); // required to render
		// // button
		// lgReaderList.setShowRecordComponentsByCell(true); // required to
		// render
		// button
		// ListGridField f = null;

		lgReaderList.setDataSource(dsReaderListDeviceDS);
		lgReaderList.setAutoFetchData(false);
		lgReaderList.setCanEdit(false);
		lgReaderList.setWidth100();
		lgReaderList.setHeight100();
		cl.addCard("readerList", lgReaderList);

		miSearchReaderList.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				biOperate.setTitle("Delete");
				biOperate.show();
				selectedGrid = null;
				lastSelectedSource = null;
				readerlistShow();
			}
		});

		cl.showCard("live");

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
		this.addMember(vl);
		lgCloseDayRegion.setShowResizeBar(true);

		miSearchLive.setDisabled(!DocFlow
				.hasPermition(PermissionNames.CAN_SEE_BANK_LIVE));
		miSearchDayRegion.setDisabled(!DocFlow
				.hasPermition(PermissionNames.CAN_CLOSE_DAY));
		miSearchReaderList.setDisabled(!DocFlow
				.hasPermition(PermissionNames.CAN_SEE_READER_LIST));
		biOperate = new ButtonItem("biOperate", "");
		biOperate.setStartRow(false);
		biOperate.setEndRow(false);
		biOperate.setVisible(false);
		biOperate
				.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

					@Override
					public void onClick(
							com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						if (selectedGrid == null || lastSelectedSource == null)
							return;

						opereateWithButton(selectedGrid,
								(ListGridRecord) lastSelectedSource, null, null);
						selectedGrid = null;
						lastSelectedSource = null;
					}
				});

	}

	private void changed() {
		Record rec = lgBankLive.getSelectedRecord();
		if (rec == null) {
			return;
		}
		int bankid = rec.getAttributeAsInt("bankid");
		Criteria crit = dmCriteria.getValuesAsCriteria();
		crit.addCriteria("bankid", bankid);
		lgBankDetails.fetchData(crit);
	}

	@SuppressWarnings("deprecation")
	protected void changedDaily(ListGrid lgSource, ListGrid lgDest,
			final ListGridRecord record, boolean withrange,
			boolean refreshSource) {
		Record rec = record;
		if (rec == null)
			rec = lastSelectedSource;
		if (rec == null) {
			return;
		}
		int recordIndex = lgSource.getRecordIndex(rec);
		int bankid = rec.getAttributeAsInt("bankid");
		Criteria crit = dmCriteria.getValuesAsCriteria();
		crit.addCriteria("bankid", bankid);
		int ppcity = 999999999;
		try {
			ppcity = rec.getAttributeAsInt("ppcityid");
		} catch (Exception e) {
			// TODO: handle exception
		}

		int pcity = 999999999;
		try {
			pcity = rec.getAttributeAsInt("pcity");
		} catch (Exception e) {
			// TODO: handle exception
		}

		crit.addCriteria("ppcity", ppcity);
		crit.addCriteria("pcity", pcity);
		crit.addCriteria("uniqueid", HTMLPanel.createUniqueId());
		crit.addCriteria("acc_id", rec.getAttribute("acc_id"));

		Boolean u = cbiUseDate.getValueAsBoolean();
		if (u == null)
			u = new Boolean(false);
		crit.addCriteria("cbiUseDate", u);
		Date dt = rec.getAttributeAsDate("bankdate");
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(0);
		crit.addCriteria("bankdate", dt);

		fetchWithRange(crit, lgDest, withrange, lgDest.getDrawArea()[0]);
		if (refreshSource) {
			if (lgSource.equals(lgCloseDayRegion)) {
				searchDayCloseRegion(withrange, recordIndex);
			}
		}

	}

	public void clearFields() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("-1", "---");
		setCurrentDate();

		try {
			siBanks.setValue((String) null);
			// siBanks.setValueMap(map);
			siBanks.setValue("-1");
			cbiUseDate.setValue(false);
			// siCustomerType.setValueMap(map);
			siCustomerType.setValue("-1");
			siBOper_AccType.setValue(DocFlow.user_obj.getZoneConfiguration()
					.getBoper_acctype_default() + "");
			addrComp.getSiRegion().setValue("-1");
			addrComp.getSiSubregion().setValueMap(map);
			addrComp.getSiSubregion().setValue("-1");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void fetchWithRange(Criteria crit, final ListGrid lg,
			boolean withrange, final int recordIndex) {
		if (true) {
			lg.fetchData(crit);
		}
		int startRow = lg.getDrawArea()[0];
		int endRow = lg.getDrawArea()[1];
		if (!withrange || (endRow == 0)) {
			lg.fetchData(crit);
			return;
		}

		com.smartgwt.client.data.DSRequest dsRequest = new com.smartgwt.client.data.DSRequest();
		// dsRequest.setStartRow(Math.max(0, startRow - REFRESH_ROW_OFFSET));
		// dsRequest.setEndRow(endRow + REFRESH_ROW_OFFSET);
		dsRequest.setStartRow(startRow);
		dsRequest.setEndRow(endRow);
		dsRequest.setSortBy(lg.getSort());
		dsRequest.setTextMatchStyle(TextMatchStyle.SUBSTRING);

		final DataSource ds = lg.getDataSource();
		ds.setShowPrompt(false);

		ds.fetchData(crit, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					com.smartgwt.client.data.DSRequest request) {
				final ResultSet resultset = new ResultSet();
				resultset.setDataSource(ds);
				resultset.setCriteria(lg.getCriteria());
				resultset.setFetchMode(FetchMode.PAGED);
				resultset.setInitialLength(response.getTotalRows());
				resultset.setInitialData(response.getData());

				lg.setData(resultset);
				// if (recordIndex >= 0) {
				// System.out.println();
				// lg.selectRecord(recordIndex);
				// }

			}
		}, dsRequest);
	}

	protected void opereateWithButton(final ListGrid grid,
			final ListGridRecord record,
			final com.smartgwt.client.widgets.events.ClickEvent event,
			final IButton button) {
		if (grid.equals(lgCloseDayRegionDetails)) {
			ListGrid lgTmp = null;
			if (grid.equals(lgCloseDayRegionDetails)) {
				lgTmp = lgCloseDayRegion;
			}
			final ListGrid lgSource = lgTmp;
			// final Record masterSelectRecord = lgSource.getSelectedRecord();
			SC.ask("Do you want to make (not)active?", new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					if (!value)
						return;
					final int operid = record.getAttributeAsInt("boperid");
					SplashDialog.showSplash();
					DocFlow.docFlowService.bOperationsActive(operid,
							DocFlow.user_id, DocFlow.user_name,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									SplashDialog.hideSplash();
									SC.say(caught.getMessage());

								}

								@Override
								public void onSuccess(Void result) {
									SplashDialog.hideSplash();
									// changedDaily(lgSource, grid, true, true);

									int active = record
											.getAttributeAsInt("bactive");
									double dsum = record
											.getAttributeAsDouble("amount");
									int add = (active == 0) ? -1 : 1;
									active = 1 - active;
									record.setAttribute("bactive", active);

									String activate = "activate";
									if (active == 0)
										activate = "deactivate";
									button.setTitle(activate);

									grid.redraw();
									int count = lastSelectedSource
											.getAttributeAsInt("scc1");
									count += add;

									double sum = lastSelectedSource
											.getAttributeAsDouble("amnt1sum");
									sum += dsum * add;
									lgSource.setCanEdit(false);
									lgSource.selectRecord(lastSelectedSource);
									// lgSource.startEditing();
									lastSelectedSource.setAttribute("amnt1sum",
											sum);
									lastSelectedSource.setAttribute("scc1",
											count);
									lgSource.saveAllEdits();
									lgSource.redraw();

								}
							});

				}
			});
		} else {

			if (grid.equals(lgCloseDayRegion)) {
				// ListGrid lgTmp = null;
				// if (grid.equals(lgCloseDayRegion)) {
				// lgTmp = lgCloseDayRegionDetails;
				// } else {
				// lgTmp = lgCloseDayRegionDetails;
				// }
				// final ListGrid lgDetails = lgTmp;

				SC.ask("Do you want to close the day?", new BooleanCallback() {

					@SuppressWarnings("deprecation")
					@Override
					public void execute(Boolean value) {
						if (!value)
							return;
						int pCity = -1;
						if (grid.equals(lgCloseDayRegion)) {
							pCity = -1 * record.getAttributeAsInt("ppcityid");
						} else {
							pCity = record.getAttributeAsInt("pcityid");
						}

						SplashDialog.showSplash();
						int bankid = record.getAttributeAsInt("userid");
						Date dt = record.getAttributeAsDate("bankdate");
						dt.setHours(0);
						dt.setMinutes(0);
						dt.setSeconds(0);
						DocFlow.docFlowService.closeBankByDayNew(bankid, dt,
								pCity, record.getAttributeAsInt("acc_id"),
								DocFlow.user_id, DocFlow.user_name,
								new AsyncCallback<Integer>() {

									@Override
									public void onFailure(Throwable caught) {
										SplashDialog.hideSplash();
										SC.say(caught.getMessage());
									}

									@Override
									public void onSuccess(Integer result) {
										SplashDialog.hideSplash();
										try {
											if (result != null
													&& result.intValue() > 0) {
												BooleanCallback bcb = new BooleanCallback() {

													@Override
													public void execute(
															Boolean value) {
														// button.setDisabled(true);
														lastSelectedSource = record;
														lgCloseDayRegion
																.invalidateCache();
														changedDaily(
																lgCloseDayRegion,
																lgCloseDayRegionDetails,
																(ListGridRecord) lastSelectedSource,
																false, false);

													}
												};
												SC.say("შეტყობინება",
														"დაიხურა "
																+ result.toString()
																+ " ოპერაცია!!!",
														bcb);

											} else {
												SC.say("შეტყობინება",
														"არც ერთი ოპერაცია არ შესრულებულა!!!");
											}
										} catch (Exception e) {
											// TODO: handle exception
										}
										// grid.selectRecord(record);
										// grid.removeSelectedData();
										// lgDetails.selectAllRecords();
										// lgDetails.removeSelectedData();
										// changedDaily(lgDetails, grid, record,
										// true, true);
										// grid.startEditing();
										// record.setAttribute("btn", "0");
										// grid.selectRecord(record);
										// grid.removeSelectedData();
										// lgDetails.selectAllRecords();
										// lgDetails.removeSelectedData();

									}
								});

					}
				});
			} else {
				if (grid.equals(lgReaderList)) {

					SC.ask("Do you want to delete?", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (!value)
								return;
							SplashDialog.showSplash();
							int id = record.getAttributeAsInt("id");
							int deviceid = record.getAttributeAsInt("deviceid");
							DocFlow.docFlowService.devicedelete(id, deviceid,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											SplashDialog.hideSplash();
											SC.say(caught.getMessage());

										}

										@Override
										public void onSuccess(Void result) {
											SplashDialog.hideSplash();

											readerlistShow();
										}
									});
						}
					});
				}
			}
		}

	}

	private void readerlistShow() {
		int pcityid = -1;
		try {
			pcityid = Integer.parseInt(addrComp.getSiSubregion().getValue()
					.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (pcityid < 0) {

			SC.say("Please choose subregion!!!", new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					addrComp.getSiSubregion().focusInItem();
				}
			});
			return;
		}
		Criteria cr = new Criteria();
		cr.addCriteria("pcityid", pcityid);
		cr.addCriteria("uniq", HTMLPanel.createUniqueId());
		lgReaderList.fetchData(cr);
		cl.showCard("readerList");
	}

	private Canvas renderButton(final ListGrid grid,
			final ListGridRecord record, Integer colNum, Canvas component) {
		String fieldname = grid.getFieldName(colNum);
		if (fieldname.equals("btn")
				|| (grid.equals(lgReaderList) && fieldname.equals("active"))) {
			if (record.getAttribute(fieldname) == null
					|| record.getAttribute(fieldname).trim().equals(""))
				return null;
			final IButton button = component != null ? ((IButton) component)
					: new IButton();
			if (grid.equals(lgCloseDayRegionDetails)) {
				String activate = "activate";
				if (record.getAttributeAsInt("bactive") == 0)
					activate = "deactivate";
				button.setTitle(activate);
			} else {
				if (grid.equals(lgReaderList)) {
					button.setTitle("Delete");
				} else
					button.setTitle("დღის დახურვა");
			}
			button.setWidth100();
			button.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
				@Override
				public void onClick(
						com.smartgwt.client.widgets.events.ClickEvent event) {

					opereateWithButton(grid, record, event, button);

				}
			});
			return button;
		}
		return null;
	}

	private void searchDayCloseRegion(boolean withrange, int recordIndex) {
		Criteria crit = dmCriteria.getValuesAsCriteria();
		Boolean u = cbiUseDate.getValueAsBoolean();
		if (u == null)
			u = new Boolean(false);
		crit.addCriteria("cbiUseDate", u);
		crit.addCriteria("uniq", HTMLPanel.createUniqueId());

		int boper_acc_type = DocFlow.user_obj.getZoneConfiguration()
				.getBoper_acctype_default();
		try {
			Object obj = siBOper_AccType.getValue();
			boper_acc_type = Integer.parseInt(obj.toString().trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		crit.addCriteria("acc_id", boper_acc_type);
		fetchWithRange(crit, lgCloseDayRegion, withrange, recordIndex);
	}

	public void setCurrentDate() {
		diStart.setValue(DocFlow.getCurrentDate());
		diEnd.setValue(new Date(DocFlow.currenttime + (1000 * 60 * 60 * 24)));
	}

	private void setResults(HashMap<String, ArrayList<ClSelectionItem>> result) {
		result = result == null ? new HashMap<String, ArrayList<ClSelectionItem>>()
				: result;

		Set<String> keys = result.keySet();
		for (String key : keys) {
			// ArrayList<ClSelectionItem> items = result.get(key);
			System.out.println(key);
		}
		setSelectItems(siCustomerType, result.get("" + ClSelection.T_CUST_TYPE));
		setSelectItems(siBanks, result.get("" + ClSelection.T_BANKS));
		setSelectItems(siBOper_AccType,
				result.get("" + ClSelection.T_BOPER_ACC_TYPE));
		siBOper_AccType.setValue(DocFlow.user_obj.getZoneConfiguration()
				.getBoper_acctype_default() + "");
		addrComp = new AddressComponent(false, false, result.get(""
				+ ClSelection.T_REGION));
		addrComp.getSiRegion().setTitle("Region");
		addrComp.getSiSubregion().setTitle("Sub Region");
		cbiUseDate = new CheckboxItem("cbiUseDate1", "Use Date");
		iiCusID = new IntegerItem("cusid", "Customer #");
		FormItem[] items = new FormItem[] { diStart, diEnd,
				addrComp.getSiRegion(), addrComp.getSiSubregion(), siBanks,
				siCustomerType, iiCusID, siBOper_AccType, cbiUseDate, biOperate };
		dmCriteria.setFields(items);
		// showStatusItem.hide();
		// if (!DocFlow.hasPermition("CAN_SEE_STATUSES")) {
		// showStatusItem.hide();
		// }

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
