package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.DocPanelSettingDataComplete;
import com.docflow.client.components.common.FieldDefinitionItem;
import com.docflow.client.components.common.FormDefinitionPanel;
import com.docflow.client.components.common.SimpleFieldDefinitionListValue;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.client.components.docflow.chain.WSelectDocumentThread;
import com.docflow.shared.CustomerShort;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.common.DocTypeMapping;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLog;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ItemHoverEvent;
import com.smartgwt.client.widgets.form.fields.events.ItemHoverHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DocumentDetailTabPane extends VLayout implements
		DocPanelSettingDataComplete {

	public static String generateHtml(HashMap<String, String> values,
			HashMap<String, String> compValues,
			HashMap<String, FieldDefinitionItem> map, String error,
			String replica) {
		if (compValues == null) {
			compValues = values;
		}

		StringBuffer sbHtml = new StringBuffer("<table border=\"1\">");
		if (error != null && error.length() > 0)
			sbHtml.append("<tr><td BGCOLOR=\"#00FF00\">" + "<b>ERROR</b>"
					+ "</td><td" + " BGCOLOR=\"red\"" + "><b>" + error
					+ "</b></td></tr>");
		if (replica != null && replica.length() > 0)
			sbHtml.append("<tr><td BGCOLOR=\"#0000FF\">" + "<b>კომენტარი</b>"
					+ "</td><td" + " BGCOLOR=\"#FFF8DC\"" + "><b>" + replica
					+ "</b></td></tr>");
		Set<String> keys = values.keySet();
		for (String key : keys) {
			String val = values.get(key);
			String compVal = compValues.get(key);
			boolean changed = compVal == null || !val.equals(compVal);
			if (changed) {
				System.out.println("");
			}
			values.put(key, val);
			FieldDefinitionItem item = map.get(key);
			if (item != null && item.getFieldDef() != null
					&& item.getFieldDef().isHidden())
				continue;
			if (item == null)
				continue;

			FieldDefinition fd = item.getFieldDef();
			String tmp = null;
			if (fd.getFieldType() == FieldDefinition.FT_MAP_ITEM) {
				tmp = fd.getDisplayTitles();
				if (tmp != null && tmp.trim().isEmpty()) {
					tmp = null;
				} else
					tmp = tmp.trim();
			}
			String tr = item == null ? key : (tmp == null ? fd
					.getFieldCaption() : tmp);
			tr = item == null ? tr : DocFlow.getCaption(item.getFieldDef()
					.getFieldCaptionId(), tr);
			val = val == null ? "" : val.trim();
			if (!changed && val.isEmpty())
				continue;
			sbHtml.append("<tr><td BGCOLOR=\"#00FF00\">" + tr + "</td><td"
					+ (changed ? (" BGCOLOR=\"red\"") : "") + ">" + val
					+ "</td></tr>");
		}

		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	public static HashMap<String, String> getData(String xml, boolean forView,
			HashMap<String, String> displayValues) {
		HashMap<String, String> data = new HashMap<String, String>();
		displayValues = displayValues == null ? new HashMap<String, String>()
				: displayValues;
		Document doc = XMLParser.parse(xml);
		Node rootElem = doc.getChildNodes().item(0);
		NodeList nodeList = rootElem.getChildNodes();

		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				Element el = (Element) n;
				String value = el.getAttribute("value");
				String key = el.getAttribute("key");
				if (forView || displayValues != null) {
					String text = el.getAttribute("text");
					if (text != null) {
						if (forView)
							value = text;
						else
							displayValues.put(key, text);
					}

				}
				data.put(el.getAttribute("key"), value);

			}
		}
		return data;
	}

	private HTMLPane itemViewer;
	private HTMLPane itemViewerStatuse;
	private VLayout editorForm;
	private ToolStrip toolstrip;

	private PReplicaHistory pReplicaHistory;

	private FormDefinitionPanel lastItem;

	public static DocumentDetailTabPane documentDetails;
	private int doctypeid;
	private Tab editTab;
	private ToolStripButton bEdit;
	private ToolStripButton bSave;
	private ToolStripButton bNew;
	private ToolStripButton bChain;

	private ToolStripButton bFiles;
	private ToolStripButton bReloadparams;
	private ToolStripButton bDevel;
	public IntegerItem iiCustomer;
	private TextItem tiCancelaryNumber;
	private PickerIcon searchPicker;
	// private TextItem tiRegion;
	private CurrentTimeItem diTransactionDate;
	private Tab viewTab;
	private Tab statusTab;
	private Tab historyTab;
	private Tab replicaHistoryTab;

	private CustomerShort customerShort;

	private TabSet tabset;
	private DocumentLong doc;
	private SelectItem siStatus;
	private BooleanItem biSaveAnyway;

	private TextAreaItem taReplica;

	private CurrentTimeItem diDocDate;
	private ListGrid lgHistory;

	private HTMLPane itemViewerHistory;
	private HTMLPane htmlPaneCustomer;

	private DocType docType = null;

	private ArrayList<DocumentFile> docFiles = null;

	HashMap<String, DocumentLog> mapHistory = new HashMap<String, DocumentLog>();

	public int getDocId() {
		if (doc == null)
			return -1;
		return doc.getId();
	}

	public DocumentDetailTabPane() {
		super();
		documentDetails = this;

		tabset = new TabSet();
		toolstrip = new ToolStrip();
		bNew = new ToolStripButton("New", "icons/16/icon_add_files.png");
		bEdit = new ToolStripButton("Edit", "icons/16/document_plain_new.png");
		bSave = new ToolStripButton("Save", "icons/16/approved.png");
		bFiles = new ToolStripButton("Files", "demoApp/mail-attachment.png");
		bChain = new ToolStripButton("Chain", "icons/16/chain_arrow.png");
		bChain.setVisible(DocFlow.hasPermition("CAN_MAKE_CHAIN"));
		bReloadparams = new ToolStripButton("Reload params");
		bReloadparams.setVisible(DocFlow.hasPermition("CAN_RELOAD_PARAMS"));
		bDevel = new ToolStripButton("Dev console");
		bDevel.setVisible(DocFlow.hasPermition("CAN_RELOAD_PARAMS"));

		bEdit.setDisabled(true);
		toolstrip.addButton(bNew);
		toolstrip.addButton(bEdit);
		toolstrip.addButton(bSave);
		toolstrip.addButton(bFiles);
		toolstrip.addButton(bChain);
		bFiles.setDisabled(true);
		setPermitions();
		toolstrip.addButton(bReloadparams);
		toolstrip.addButton(bDevel);
		bDevel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SC.showConsole();

			}
		});
		bChain.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int docid = doc == null ? -1 : doc.getId();
				if (docid <= 0)
					return;
				new WSelectDocumentThread(docid);

			}
		});
		bReloadparams.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SplashDialog.showSplash();
				DocFlow.docFlowService.reloadParams(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						SC.say(caught.toString());
					}

					@Override
					public void onSuccess(Void result) {
						SplashDialog.hideSplash();

					}
				});

			}
		});
		bSave.setDisabled(true);
		setPermitions();
		itemViewer = new HTMLPane();
		// itemViewer.setDataSource(supplyItemDS);
		itemViewer.setWidth100();
		itemViewer.setMargin(25);
		itemViewer.setContents("");
		editorForm = new VLayout();

		itemViewerStatuse = new HTMLPane();
		itemViewerStatuse.setMargin(25);
		itemViewerStatuse.setContents("");
		// editorForm.setWidth(650);
		editorForm.setMargin(5);
		// editorForm.setNumCols(4);
		editorForm.setPadding(5);
		// editorForm.setAutoFocus(false);
		// // editorForm.setDataSource(supplyItemDS);
		// editorForm.setUseAllDataSourceFields(true);

		viewTab = new Tab("View");

		/* viewTab.setWidth100; */
		viewTab.setPane(itemViewer);

		editTab = new Tab("Edit");
		editTab.setDisabled(true);
		HLayout hlPanel = new HLayout();
		/* editTab.setWidth(); */
		editTab.setPane(hlPanel);
		// toolstrip.setHeight("5%");
		toolstrip.setWidth100();
		// toolstrip.setTop(0);
		// toolstrip.setLeft(0);
		addMember(toolstrip);

		addMember(tabset);
		DynamicForm dm = new DynamicForm();

		iiCustomer = new IntegerItem();
		iiCustomer.setTitle("Customer");
		searchPicker = new PickerIcon(PickerIcon.SEARCH,
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						searchForCustomer();
					}
				});
		iiCustomer.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				String keyName = event.getKeyName();
				if (keyName.equals("Enter")) {
					searchForCustomer();
				}

			}
		});

		PickerIcon searchCustPicker = new PickerIcon(new Picker(
				"[SKIN]/actions/search.png"), new FormItemClickHandler() {
			public void onFormItemClick(FormItemIconClickEvent event) {
				if (doc != null && doc.getId() > 0) {
					SC.say("Cannot change customer");
					return;
				}
				WDFSearchCustomers.showWindow();
			}
		});

		iiCustomer.setIcons(searchPicker, searchCustPicker);
		iiCustomer.setSelectOnFocus(true);
		dm.setTitleOrientation(TitleOrientation.TOP);

		editorForm.setWidth("80%");
		editorForm.setShowResizeBar(true);
		hlPanel.addMember(editorForm);
		iiCustomer.addItemHoverHandler(new ItemHoverHandler() {

			@Override
			public void onItemHover(ItemHoverEvent event) {
				String prompt = getCustomerHint();
				iiCustomer.setPrompt(prompt);

			}
		});

		tiCancelaryNumber = new TextItem();
		tiCancelaryNumber.setTitle(DocFlow.getCancelaryName());

		// tiRegion = new TextItem();
		// tiRegion.setTitle("Region, SubRegion, Zone");
		// tiRegion.setAttribute("readOnly", true);
		// tiRegion.setWidth(500);

		// tiSubRegion = new TextItem();
		// tiSubRegion.setTitle("Sub Region");
		// tiSubRegion.setAttribute("readOnly", true);
		//
		// tiZona = new TextItem();
		// tiZona.setTitle("Zone");
		// tiZona.setAttribute("readOnly", true);

		diTransactionDate = new CurrentTimeItem();
		diTransactionDate.setTitle("Transaction Date");

		ButtonItem biFiles = new ButtonItem("biFiles", "Files");
		biFiles.setIcon("demoApp/mail-attachment.png");
		biFiles.setEndRow(false);
		biFiles.setStartRow(false);

		bFiles.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int docid = doc == null ? -1 : doc.getId();
				if (docid <= 0)
					return;
				DocFlow.docFlowService.getFilesForDocument(docid,
						new AsyncCallback<ArrayList<DocumentFile>>() {

							@Override
							public void onFailure(Throwable caught) {

							}

							@Override
							public void onSuccess(ArrayList<DocumentFile> result) {
								showFiles(result, true);
							}

						});

			}
		});
		biFiles.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				int docid = doc == null ? -1 : doc.getId();
				if (docFiles == null) {
					if (docid <= 0) {
						showFiles(new ArrayList<DocumentFile>(), false);
					} else {
						DocFlow.docFlowService.getFilesForDocument(docid,
								new AsyncCallback<ArrayList<DocumentFile>>() {

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onSuccess(
											ArrayList<DocumentFile> result) {
										showFiles(result, false);
									}

								});
					}

				} else {
					showFiles(docFiles, false);
				}
			}
		});
		// diTransactionDate.setDisabled(false);
		dm.setNumCols(6);
		dm.setFields(iiCustomer, /* tiRegion, */tiCancelaryNumber,
				diTransactionDate, biFiles);
		editorForm.addMember(dm);
		htmlPaneCustomer = new HTMLPane();
		htmlPaneCustomer.setWidth("20%");
		htmlPaneCustomer.setBorder("1px solid");
		// htmlPaneCustomer.setShowEdges(true);

		hlPanel.addMember(htmlPaneCustomer);
		bNew.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				newClick();
			}
		});
		bSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DocFlow.refreshServerTime();
				if (tabset.getSelectedTab().equals(editTab))
					saveDocument();
				if (tabset.getSelectedTab().equals(statusTab))
					saveDocumentState();

			}

		});

		bEdit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DocFlow.refreshServerTime();
				editDoc();

			}
		});
		statusTab = new Tab();
		statusTab.setTitle("Status");

		HLayout vl = new HLayout();

		DynamicForm dmStatus = new DynamicForm();
		dmStatus.setTitleOrientation(TitleOrientation.TOP);
		siStatus = new SelectItem("_status_change", "სტატუსი");
		dmStatus.setNumCols(2);
		dmStatus.setWidth("50%");

		biSaveAnyway = new BooleanItem("biSaveAnyway", "Save anyway");
		biSaveAnyway.setValue(false);
		boolean has_anyway_status_permition = DocFlow
				.hasPermition("CAN_SAVE_STATE_ANYWAY");
		biSaveAnyway.setVisible(has_anyway_status_permition);
		if (!has_anyway_status_permition)
			siStatus.setColSpan(2);
		setStatuses();

		diDocDate = new CurrentTimeItem("_docDate", "თარიღი");
		diDocDate.setColSpan(2);
		taReplica = new TextAreaItem("_replica", "კომენტარი");
		taReplica.setColSpan(2);
		taReplica.setWidth(500);

		dmStatus.setFields(siStatus, biSaveAnyway, diDocDate, taReplica);
		dmStatus.setHeight("100%");
		vl.addMember(dmStatus);
		vl.addMember(itemViewerStatuse);
		statusTab.setPane(vl);
		statusTab.setDisabled(!(DocFlow
				.hasPermition(PermissionNames.CHANGE_STATUS) || DocFlow
				.hasPermition(PermissionNames.CAN_VIEW_DOC_STATUSE)));

		itemViewerHistory = new HTMLPane();
		itemViewerHistory.setWidth("70%");
		itemViewerHistory.setShowEdges(true);
		HLayout hlHistory = new HLayout();

		lgHistory = new ListGrid();
		lgHistory.setHeight("100%");
		lgHistory.setWidth("30%");
		ListGridField docversion = new ListGridField("_docversion", "ვერსია",
				40);
		docversion.setType(ListGridFieldType.INTEGER);
		docversion.setCanEdit(false);
		ListGridField docDate = new ListGridField("_docDate", "თარიღი", 80);
		docDate.setType(ListGridFieldType.DATE);
		docDate.setCanEdit(false);
		ListGridField docstatus = new ListGridField("_docstatus", "სტატუსი", 80);
		docstatus.setCanEdit(true);
		docstatus.setType(ListGridFieldType.TEXT);
		ListGridField user_name = new ListGridField("_user_name", "User", 120);
		user_name.setCanEdit(false);

		lgHistory.setFields(docversion, docDate, user_name, docstatus);
		lgHistory.setCanEdit(false);
		lgHistory.setShowResizeBar(true);

		docversion.setAlign(Alignment.LEFT);
		docDate.setAlign(Alignment.LEFT);
		user_name.setAlign(Alignment.LEFT);
		docstatus.setAlign(Alignment.LEFT);

		lgHistory.addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record rec = event.getSelectedRecord();
				if (rec != null) {
					setVersionId(rec.getAttribute("_docversion"));
				}

			}
		});

		hlHistory.addMember(lgHistory);
		hlHistory.addMember(itemViewerHistory);

		historyTab = new Tab("History");
		historyTab.setPane(hlHistory);

		replicaHistoryTab = new Tab("Replica history");
		pReplicaHistory = new PReplicaHistory();
		replicaHistoryTab.setPane(pReplicaHistory);

		tabset.setTabs(viewTab, editTab, statusTab, historyTab,
				replicaHistoryTab);
		setPermitions();
	}

	public void setStatuses() {
		LinkedHashMap<String, String> mapStatMap = new LinkedHashMap<String, String>();
		ArrayList<ClSelectionItem> docStates = DocFlow.user_obj
				.getStatuses(DocFlow.system_id);
		for (ClSelectionItem clSelectionItem : docStates) {
			mapStatMap.put(clSelectionItem.getId() + "",
					clSelectionItem.getValue());
		}
		siStatus.setValueMap(mapStatMap);
	}

	private void editDoc() {
		bSave.setDisabled(false);
		bNew.setDisabled(false);
		bEdit.setDisabled(true);

		editTab.setDisabled(false);
		setPermitions();
		this.tabset.selectTab(editTab);

	}

	private String getCustomerHint() {
		if (customerShort == null)
			return null;
		String result = "";
		result += "<b>Customer</b>:" + customerShort.getCusid() + "<br>";
		result += "<b>Name</b>:" + customerShort.getCusname() + "<br>";
		result += "<b>Region</b>:" + customerShort.getRegion() + "<br>";
		result += "<b>District</b>:" + customerShort.getRaion() + "<br>";
		result += "<b>City</b>:" + customerShort.getCityname() + "<br>";
		result += "<b>Zone</b>:" + customerShort.getZone() + "<br>";
		result += "<b>Street</b>:" + customerShort.getStreetname() + "<br>";
		result += "<b>Home</b>:" + customerShort.getHome().trim() + "<br>";
		result += "<b>Flat</b>:" + customerShort.getFlat().trim() + "<br>";
		result += "<b>Scope</b>:" + customerShort.getScopename();
		if (customerShort.getLoan() != null)
			result += "<br><b>Loan</b>:" + customerShort.getLoan();

		return result;
	}

	private Long getLongValue(HashMap<String, Object> data, String keyname) {
		try {
			return Long.parseLong(getStringValue(data, keyname, 0));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0L;
	}

	private String getStringValue(HashMap<String, Object> data, String keyname) {
		return getStringValue(data, keyname, 1);
	}

	private String getStringValue(HashMap<String, Object> data, String keyname,
			int index) {
		Object value = data.get(keyname);
		try {
			if (value instanceof String[])
				return ((String[]) (value))[index];
			else
				return value.toString();
		} catch (Exception e) {

		}
		return "";
	}

	

	public Integer seletedCustomer = null;

	private void makeDocument() {
		customerShort = null;
		int custid = -1;
		seletedCustomer = null;
		WDFSearchCustomers.clearPhone();
		this.doc = null;
		docFiles = null;
		// bSave.setDisabled(true);
		try {
			custid = Integer.parseInt(iiCustomer.getValue().toString());
		} catch (Exception e) {

			SC.say("Please enter customer", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					iiCustomer.focusInItem();
				}
			});
			return;
		}
		final int cust_id = custid;
		DocFlow.docFlowService.getValueMap(custid, doctypeid,
				DocFlow.language_id, DocFlow.user_id,
				new AsyncCallback<DocTypeMapping>() {

					@Override
					public void onFailure(Throwable caught) {
						htmlPaneCustomer.setContents(caught.getMessage());
						SC.say(caught.getMessage());

					}

					@Override
					public void onSuccess(DocTypeMapping result) {
						if (result != null) {
							seletedCustomer = cust_id;
							customerShort = result.getCustomerShort();
							htmlPaneCustomer.setContents(getCustomerHint());
							tiCancelaryNumber.setValue("");
							doc = result.getDocument();
							if (doc != null) {
								doc.setCustomerShort(customerShort);
								tiCancelaryNumber.setValue(doc
										.getCancelary_nom());
								if (doc.getId() >= 1) {
									tiCancelaryNumber.disable();
									iiCustomer.disable();
								} else {
									tiCancelaryNumber.enable();
									iiCustomer.enable();
								}
								diTransactionDate.setValue(new Date(doc
										.getTransaction_date()));

								setAddressFields();
							}
							if (DocFlow.setCancelaryPhoneNumber()
									&& tiCancelaryNumber.getValueAsString()
											.trim().isEmpty()) {
								tiCancelaryNumber.setValue(customerShort
										.getPhone());
							}

							lastItem.setData(result.getValues(), cust_id, null);
						}

					}
				});

	}

	public void newClick() {
		seletedCustomer = null;
		WDFSearchCustomers.clearPhone();
		DocFlow.refreshServerTime();
		System.out.println("Setting New Document");
		doc = null;
		bSave.setDisabled(false);
		bNew.setDisabled(true);
		bEdit.setDisabled(true);
		setPermitions();
		editTab.setDisabled(false);
		bFiles.setDisabled(true);
		bChain.setDisabled(true);
		tabset.selectTab(editTab);
		tiCancelaryNumber.setValue("");
		diTransactionDate.setValue(new Date(DocFlow.currenttime));
		customerShort = null;
		taReplica.setValue("");
		htmlPaneCustomer.setContents("");
		if (lastItem != null) {
			iiCustomer.setValue("");
			iiCustomer.enable();
			lastItem.setData(new HashMap<String, String>(), 0, null);
		}
		pReplicaHistory.setValues(-1L);
		System.out.println("sss");
	}

	public void saveDocument() {
		if (this.doc != null
				&& (this.doc.getDoc_status_id() == DocFlow.user_obj
						.getApproved_status(DocFlow.system_id) || (this.doc
						.getDoc_status_id() >= DocFlow.user_obj
						.getApproved_status(DocFlow.system_id)))) {
			SC.say("Cannot change document", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {

				}
			});
			return;
		}
		// if (this.doc == null) {
		// SC.say("Cannot save document", new BooleanCallback() {
		// @Override
		// public void execute(Boolean value) {
		//
		// }
		// });
		// return;
		// }
		if (docType == null
				|| (docType != null && docType.isApplied_customer() && customerShort == null)) {
			SC.say("Please search for customer", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {

				}
			});
			return;
		}

		String cancelary = null;
		try {
			cancelary = tiCancelaryNumber.getValue().toString();
			if (cancelary == null || cancelary.trim().length() == 0)
				throw new Exception();
		} catch (Exception e) {
			SC.say("Please select correct " + tiCancelaryNumber.getTitle(),
					new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							tiCancelaryNumber.focusInItem();

						}
					});
			return;
		}
		HashMap<String, Object> data = null;
		int docdelaystatuse = 0;
		if (lastItem != null) {

			int ds = lastItem.validateDate(diTransactionDate);
			if (ds == -1) {
				String error = "თარიღი მითითებულია მომავალში!!!";
				SC.say("Error!!!", error, new BooleanCallback() {

					@Override
					public void execute(Boolean value) {
						diTransactionDate.focusInItem();
					}
				});
				return;
			}
			if (ds == -2) {
				String error = "თარიღი ძალიან ძველია!!!";
				SC.say("Error!!!", error, new BooleanCallback() {

					@Override
					public void execute(Boolean value) {
						diTransactionDate.focusInItem();
					}
				});
				return;
			}
			if (docType.getDatefield().equals("@docdate"))
				docdelaystatuse = ds;
			if (!lastItem.validate())
				return;
			if (!docType.getDatefield().equals("@docdate"))
				docdelaystatuse = lastItem.getDelayInterval();
			data = lastItem.getData();
		}

		String content = FormDefinition.getXml(data);
		// String content = "<Doc>"+sb.toString().trim()+"</Doc>";
		final DocumentLong doc = this.doc == null ? new DocumentLong()
				: this.doc;
		if (this.doc == null) {
			doc.setDoc_flow_num("asdas");
			doc.setDoc_status_id(DocFlow.user_obj
					.getInitial_status(DocFlow.system_id));
			doc.setUser_id(DocFlow.user_id);
			doc.setDoc_type_id(doctypeid);
			doc.setDoc_date(System.currentTimeMillis());
		}
		doc.setDelaystatus(docdelaystatuse);
		doc.setContent_xml(content);
		if (!docType.isApplied_customer()) {
			setDefaultCustomerAttributer(doc, data);
		}
		Object cancNo = cancelary;
		doc.setCancelary_nom(cancNo == null ? null : cancNo.toString());
		doc.setUser_id(DocFlow.user_id);
		if (doc.getCustomer_name() == null)
			doc.setCustomer_name("");
		Date transactionDate = null;
		Object objTranDate = diTransactionDate.getValue();
		try {
			transactionDate = (Date) objTranDate;
			doc.setTransaction_date(transactionDate.getTime());
		} catch (Exception e) {
			transactionDate = new Date(0);
		}
		ArrayList<DocumentFile> docFilesToSend = new ArrayList<DocumentFile>();
		if (docFiles != null)
			for (DocumentFile df : docFiles) {
				if (df.getId() <= 0) {
					docFilesToSend.add(df);
				}
			}
		SplashDialog.showSplash();
		if (doc.getId() <= 0) {

			DocFlow.docFlowService.saveDocument(doc, docFilesToSend,
					DocFlow.language_id, new AsyncCallback<DocumentShort>() {
						@Override
						public void onFailure(Throwable caught) {
							SplashDialog.hideSplash();
							SC.say(caught.getMessage());

						}

						@Override
						public void onSuccess(DocumentShort result) {
							SplashDialog.hideSplash();
							saveResult(result);
						}
					});
		} else
			DocFlow.docFlowService.documentCorrection(doc.getId(), content,
					DocFlow.user_id, transactionDate.getTime(), docFilesToSend,
					DocFlow.language_id, docdelaystatuse,
					new AsyncCallback<DocumentShort>() {
						@Override
						public void onFailure(Throwable caught) {
							SplashDialog.hideSplash();
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(DocumentShort result) {
							SplashDialog.hideSplash();
							saveResult(result);

						}
					});

	}

	private void saveDocumentState() {
		if (!DocFlow.hasPermition(PermissionNames.CHANGE_STATUS)) {
			SC.say("You do not have permitions to change statuse");
			return;
		}

		boolean anyway = false;
		try {
			if (biSaveAnyway.isVisible())
				anyway = biSaveAnyway.getValueAsBoolean();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (doc != null && doc.getId() > 0) {
			if (this.doc.getDoc_status_id() >= DocFlow.user_obj
					.getApproved_status(DocFlow.system_id) && !anyway) {
				SC.say("Cannot change document", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {

					}
				});
				return;
			}
			int status = Integer.parseInt(siStatus.getValue().toString());
			int user_id = DocFlow.user_id;
			int language_id = DocFlow.language_id;
			String replica = "";
			if (taReplica.getValue() != null)
				replica = taReplica.getValue().toString();
			Date dt = diDocDate.getValueAsDate();
			long time = dt.getTime();

			if (biSaveAnyway.isVisible())
				biSaveAnyway.setValue(false);
			DocFlow.docFlowService.documentChangeState(doc.getId(), replica,
					status, user_id, language_id, time, anyway,
					new AsyncCallback<DocumentShort>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.say(caught.getMessage());

						}

						@Override
						public void onSuccess(DocumentShort result) {
							saveResult(result);

						}
					});

		}

	}

	private void saveResult(DocumentShort result) {
		DocFlowSectionStack.docFlowSectionStack.documentHistoryListGrid
				.addDocument(result);
		bSave.setDisabled(true);
		bNew.setDisabled(false);
		bEdit.setDisabled(false);
		pReplicaHistory.setValues((long) result.getId());
		setPermitions();
	}

	private void searchForCustomer() {
		if (doc != null && doc.getId() > 0) {
			SC.say("Cannot change customer");
			return;
		}
		makeDocument();
	}

	private void setAddressFields() {
		// tiRegion.setValue(doc.getRegionname() + "," + doc.getSubregionname()
		// + "," + doc.getCzona());
		// tiSubRegion.setValue(doc.getSubregionname());
		// tiZona.setValue(doc.getCzona());
	}

	private void setDefaultCustomerAttributer(DocumentLong doc,
			HashMap<String, Object> data) {
		try {
			doc.setStreet_id(getLongValue(data, "streetid").intValue());
			doc.setStreenname(getStringValue(data, "streetid"));
			doc.setCityid(getLongValue(data, "cityId").intValue());
			doc.setCityname(getStringValue(data, "cityId"));
			doc.setSubregionid(getLongValue(data, "subregionId").intValue());
			doc.setSubregionname(getStringValue(data, "subregionId"));
			doc.setRegionid(getLongValue(data, "regionId").intValue());
			doc.setRegionname(getStringValue(data, "regionId"));
			doc.setCustomer_name(getStringValue(data, "cusname"));
			doc.setCzona(getLongValue(data, "zone"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setDisabledToButton(boolean cannotaddedit, Canvas c) {
		boolean b = c.getDisabled();
		if (!b && cannotaddedit)
			c.setDisabled(true);
	}

	public void setCusID(Integer iCusId) {
		if (doc != null && doc.getId() > 0) {
			SC.say("Cannot change customer");
			return;
		}
		iiCustomer.setValue(iCusId);
		searchForCustomer();
	}

	public void setDocType(DocType result) {
		System.out.println("Setting DocType");
		if (lastItem != null) {
			// editorForm.removeMember(lastItem);
			lastItem.destroy();
			lastItem = null;
		}
		docType = result;
		// if (ncCustomer != null) {
		// editorForm.removeMember(ncCustomer);
		// ncCustomer = null;
		// }
		seletedCustomer = null;
		WDFSearchCustomers.clearPhone();
		try {
			bNew.setDisabled(false);
			bSave.setDisabled(true);
			editTab.setDisabled(true);
			bEdit.setDisabled(true);
			setPermitions();
			this.tabset.selectTab(viewTab);

			// if (result.getId() == 1 || result.getId() == 2) {
			// ncCustomer = new NewCustomer();
			// editorForm.addMember(ncCustomer);
			// iiCustomer.setVisible(false);
			// return;
			// }

			if (result == null || result.getDoc_template() == null
					|| result.getDoc_template().isEmpty()) {
				return;
			}

			try {
				iiCustomer.setVisible(result.isApplied_customer());
				diTransactionDate.setDisabled(!result.getDatefield().equals(
						"@docdate"));
				FormDefinition formd = new FormDefinition();
				formd.setXml(result.getDoc_template());

				// lastItem = new FinanceCorrection(true, 1, true);
				// editorForm.addMember(lastItem);
				// editorForm.redraw();
				lastItem = new FormDefinitionPanel(formd,
						new SimpleFieldDefinitionListValue(), docType, this);
				editorForm.addMember(lastItem);
			} catch (Exception e) {
				SC.say(e.getMessage());
			}

		} finally {
			// editorForm.redraw();
		}
	}

	public void setDocTypeId(int doctype) {
		doctypeid = doctype;
		bNew.setDisabled(true);
		bSave.setDisabled(true);
		bEdit.setDisabled(true);
		editTab.setDisabled(true);
		this.tabset.selectTab(viewTab);
		itemViewer.setContents("");
		setPermitions();
	}

	public void setDocument(final DocumentLong doc) {

		if (lastItem != null && docType != null
				&& docType.getId() == doc.getDoc_type_id()) {
			setPane(doc);
			setAddressFields();
			bNew.setDisabled(false);
			bEdit.setDisabled(false);
			bSave.setDisabled(true);
			bChain.setDisabled(false);
			setPermitions();
		} else {
			DocFlow.docFlowService.getDocType(doc.getDoc_type_id(),
					DocFlow.language_id, new AsyncCallback<DocType>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(DocType result) {
							setDocType(result);
							setPane(doc);
						}
					});
		}

	}

	private void setPane(DocumentLong doc) {
		mapHistory.clear();
		this.doc = doc;
		docFiles = null;
		final String cont = doc.getContent_xml();

		if (lastItem != null) {
			HashMap<String, String> displayValues = new HashMap<String, String>();
			HashMap<String, String> values = getData(cont, false, displayValues);
			String html = generateHtml(
					getData(cont, true, new HashMap<String, String>()), null,
					lastItem.getFormitemMap(), doc.getError(), doc.getReplic());
			itemViewer.setContents(html);
			itemViewerStatuse.setContents(html);
			lastItem.setData(values, doc.getCust_id(), displayValues);
			customerShort = doc.getCustomerShort();
			iiCustomer.setValue(doc.getCust_id());
			String str = getCustomerHint();
			iiCustomer.setPrompt(str);
			tiCancelaryNumber.setValue(doc.getCancelary_nom());
			htmlPaneCustomer.setContents(str);

		}
		bSave.setDisabled(true);
		bEdit.setDisabled(false);
		setPermitions();
		editTab.setDisabled(true);
		bFiles.setDisabled(doc.getFilecount() == 0);
		bChain.setDisabled(false);
		siStatus.setValue(doc.getDoc_status_id());
		taReplica.setValue(doc.getReplic());
		ArrayList<DocumentLog> logs = doc.getHistory();
		lgHistory.setData(new Record[] {});
		for (DocumentLog log : logs) {
			ListGridRecord rec = new ListGridRecord();
			rec.setAttribute("_docDate", new Date(log.getTransaction_date()));
			rec.setAttribute("_docstatus", log.getDocstatus());
			rec.setAttribute("_user_name", log.getUser_name());
			rec.setAttribute("_docversion", log.getVersion_id());
			rec.setAttribute("_applyer", log.getController_name());
			mapHistory.put("" + log.getVersion_id(), log);
			lgHistory.addData(rec);
		}

		pReplicaHistory.setValues((long) doc.getId());
		seletedCustomer = doc.getCust_id() > 0 ? doc.getCust_id() : null;
	}

	private void setPermitions() {
		boolean cannotaddedit = DocFlow
				.hasPermition(PermissionNames.CANNOT_ADD_EDIT_DOCUMENT);
		setDisabledToButton(cannotaddedit, bNew);
		setDisabledToButton(cannotaddedit, bSave);
		setDisabledToButton(cannotaddedit, bEdit);

	}

	private void setVersionId(Object versionid) {
		if (versionid == null) {
			return;
		}
		System.out.println(versionid.toString());
		DocumentLog log = mapHistory.get(versionid.toString());
		if (log == null) {
			return;
		}
		if (lastItem != null) {
			itemViewerHistory.setContents(generateHtml(
					getData(log.getXml(), true, null),
					getData(doc.getContent_xml(), true, null),
					lastItem.getFormitemMap(), null, doc.getReplic()));
		}

	}

	private void showFiles(ArrayList<DocumentFile> files, boolean readonly) {
		if (files == null)
			files = new ArrayList<DocumentFile>();
		docFiles = files;
		WDocFiles.showWindow(files, readonly);
	}

	public DocType getDocType() {
		return docType;
	}

	@Override
	public void settingDataComplete() {
		if (lastItem != null)
			lastItem.setCalculatorProceed();

	}

}
