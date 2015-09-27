package com.docflow.client.components.docflow.chain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.client.components.common.FieldDefinitionItem;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.client.components.docflow.DocumentDetailTabPane;
import com.docflow.client.components.docflow.DocumentHistoryListGrid;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.MiniDateRangeItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.viewer.DetailViewer;

public class WSelectDocumentThread extends Dialog {
	private DataSource documentThreadDS;
	private DetailViewer itemViewer;
	private int thread_id;
	private ToolStripButton bNew;
	private ToolStripButton bDelete;
	private ToolStripButton bSearch;
	private ToolStripButton bSave;
	private ToolStrip tsMain;
	private ListGrid lgGrid;
	private DynamicForm dfNew;
	private DynamicForm dfSearch;
	private ListGrid lgThreads;
	private int doc_id;

	public WSelectDocumentThread(int doc_id) {
		documentThreadDS = DataSource.get("DocumentThreadDS");
		Criteria cr = new Criteria();
		this.doc_id = doc_id;
		cr.setAttribute("doc_id", doc_id);
		documentThreadDS.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse dsResponse, Object data,
					DSRequest dsRequest) {
				Record[] rec = dsResponse.getData();
				setData(rec, true);
				show();
			}
		});
		itemViewer = new DetailViewer();
		itemViewer.setDataSource(documentThreadDS);

		itemViewer.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {

				RecordList recordList = itemViewer.getRecordList();
				if (recordList == null || recordList.isEmpty())
					return;
				Record rec = recordList.get(0);
				if (rec == null)
					return;
				tsMain.setVisible(true);
				dfSearch.setVisible(false);
				lgThreads.setVisible(false);
				dfNew.setVisible(true);
				dfNew.editRecord(rec);
				bSave.setDisabled(false);
			}
		});

		itemViewer
				.setEmptyMessage("აირჩიეთ არსებული ჯაჭვი ან შექმენით ახალი!!!");
		tsMain = new ToolStrip();
		tsMain.setWidth100();
		bNew = new ToolStripButton("New", "icons/16/icon_add_files.png");
		bDelete = new ToolStripButton("Delete", "icons/16/close.png");
		bSearch = new ToolStripButton("Choose",
				"[SKIN]/pickers/search_picker.png");
		bSave = new ToolStripButton("Save", "icons/16/approved.png");
		tsMain.addButton(bNew);
		tsMain.addButton(bSearch);
		tsMain.addButton(bSave);
		tsMain.addButton(bDelete);
		this.addItem(tsMain);
		tsMain.setVisible(false);

		bSave.setDisabled(true);

		dfNew = new DynamicForm();
		dfNew.setWidth100();
		dfNew.setVisible(false);
		this.addItem(dfNew);

		dfNew.setDataSource(documentThreadDS);

		dfNew.setNumCols(2);
		dfNew.setFields(new TextItem("thread_name", "Name"));
		dfNew.getField("thread_name").setRequired(true);

		dfSearch = new DynamicForm();
		dfSearch.setWidth100();
		dfSearch.setVisible(false);
		dfSearch.setTitleOrientation(TitleOrientation.TOP);
		dfSearch.setNumCols(4);

		AddressComponent addrComp = new AddressComponent(false, true, null);
		addrComp.getSiRegion().setTitle("Region");
		addrComp.getSiRegion().setName("region_id");
		addrComp.getSiSubregion().setTitle("Sub Region");
		addrComp.getSiSubregion().setName("subregion_id");
		addrComp.getSiCity().setTitle("City");
		addrComp.getSiCity().setName("city_id");
		ButtonItem findItem = new ButtonItem("Find");
		findItem.setIcon("silk/find.png");
		findItem.setWidth(70);
		findItem.setStartRow(false);
		findItem.setEndRow(false);

		ButtonItem clear = new ButtonItem("cls", "Clear");
		clear.setStartRow(false);
		clear.setEndRow(false);

		clear.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				FormItem[] formItems = dfSearch.getFields();
				for (FormItem formItem : formItems) {
					if (formItem instanceof SelectItem)
						continue;
					formItem.clearValue();
				}
			}
		});

		dfSearch.setFields(new IntegerItem("thread_id", "Thread ID"),
				new MiniDateRangeItem("start_end", "Thread Start/End"),
				new TextItem("rec_user", "Rec User"), new IntegerItem("doc_id",
						"Doc ID"), new TextItem("cancelary", "Cancelary #"),
				new MiniDateRangeItem("doc_start_end", "Doc Start/End"),
				new IntegerItem("cus_id", "Cus ID"), addrComp.getSiRegion(),
				addrComp.getSiSubregion(), addrComp.getSiCity(), findItem,
				clear);

		this.addItem(dfSearch);

		lgThreads = new ListGrid();
		lgThreads.setWidth100();
		lgThreads.setHeight("150");
		lgThreads.setVisible(false);
		lgThreads.setDataSource(documentThreadDS);
		lgThreads.addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record rec = event.getSelectedRecord();
				if (rec == null)
					return;
				setData(new Record[] { rec }, false);

			}
		});
		lgThreads.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (thread_id == 0) {
					SC.say("გთხოვთ მონიშნოთ ჩანაწერი!!!");
					return;
				}
				updateBinding(thread_id);
			}
		});
		this.addItem(lgThreads);

		findItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				lgThreads.invalidateCache();
				@SuppressWarnings("unchecked")
				Map<String, Object> mp = dfSearch.getValues();
				MiniDateRangeItem start_end = (MiniDateRangeItem) dfSearch
						.getField("start_end");

				if (start_end.getFromDate() != null
						&& start_end.getToDate() != null) {
					mp.put("st_date", start_end.getFromDate());
					mp.put("end_date", start_end.getToDate());
				}

				start_end = (MiniDateRangeItem) dfSearch
						.getField("doc_start_end");

				if (start_end.getFromDate() != null
						&& start_end.getToDate() != null) {
					mp.put("doc_st_date", start_end.getFromDate());
					mp.put("doc_end_date", start_end.getToDate());
				}
				Criteria cr = new Criteria();
				for (String k : mp.keySet()) {
					if (k != null) {
						Object val = mp.get(k);
						if (val != null) {

							if (val.equals("-1")
									&& (k.equals("region_id")
											|| k.equals("subregion_id") || k
												.equals("city_id")))
								continue;

							cr.setAttribute(k.toString(), val);
						}
					}

				}
				lgThreads.fetchData(cr);

			}
		});

		this.addItem(itemViewer);
		lgGrid = new ListGrid() {
			@Override
			protected Canvas getCellHoverComponent(Record record,
					Integer rowNum, Integer colNum) {
				String cont = record.getAttribute("_xml");
				if (cont == null) {
					return null;
				}

				String docType_xml = record.getAttribute("_dtxml");
				if (docType_xml == null) {
					return null;
				}

				HashMap<String, String> values = DocumentDetailTabPane.getData(
						cont, true, null);

				FormDefinition fd = new FormDefinition();
				fd.setXml(docType_xml);
				HashMap<String, FieldDefinitionItem> definitionMap = new HashMap<String, FieldDefinitionItem>();
				for (FormGroup formGroup : fd.getFormGroups()) {
					ArrayList<FieldDefinition> fields = formGroup
							.getFieldDefinitions();
					for (FieldDefinition fieldDefinition : fields) {
						FieldDefinitionItem fi = new FieldDefinitionItem(
								fieldDefinition, null);
						definitionMap.put(fieldDefinition.getFieldName(), fi);
					}
				}

				HTMLPane itemViewer = new HTMLPane();

				itemViewer.setWidth(800);
				itemViewer.setHeight(700);
				String data = DocumentDetailTabPane.generateHtml(values, null,
						definitionMap, null, null);
				itemViewer.setContents(data);
				return itemViewer;
			}

		};

		lgGrid = DocumentHistoryListGrid.createDocumentListGrid(lgGrid);
		lgGrid.setCanHover(true);
		lgGrid.setShowHover(true);
		lgGrid.setShowHoverComponents(true);
		ListGridField[] fields = lgGrid.getFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setCanEdit(false);
		}

		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					destroy();
			}
		});

		bNew.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dfSearch.setVisible(false);
				lgThreads.setVisible(false);
				dfNew.setVisible(true);
				// ValuesManager vm = dfNew.getValuesManager();
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("reg_time", new Date());
				mp.put("reg_user_id", DocFlow.user_id);
				mp.put("doc_id", WSelectDocumentThread.this.doc_id);
				// mp.put("thread_id", -1);
				dfNew.editNewRecord(mp);
				bSave.setDisabled(false);
			}
		});

		bSearch.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dfSearch.setVisible(true);
				lgThreads.setVisible(true);
				dfNew.setVisible(false);
				bSave.setDisabled(false);
				setData(null, false);
			}
		});

		bDelete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (thread_id <= 0)
					return;
				SC.ask("გინდათ დოკუმენი ამოვაგდოთ ჯაჭვიდან?",
						new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								int mThread_id = 0;
								updateBinding(mThread_id);

							}
						});
			}
		});

		bSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (dfNew.isVisible()) {
					if (!dfNew.validate())
						return;
					dfNew.saveData(new DSCallback() {
						@Override
						public void execute(DSResponse dsResponse, Object data,
								DSRequest dsRequest) {
							setData(dsResponse.getData(), true);
							dfNew.hide();
						}
					});

				}
				if (dfSearch.isVisible()) {
					if (thread_id == 0) {
						SC.say("გთხოვთ მონიშნოთ ჩანაწერი!!!");
						return;
					}
					updateBinding(thread_id);
				}
			}
		});

		this.addItem(lgGrid);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.setHeight(700);
		this.setWidth(1000);
		this.centerInPage();
	}

	public void setData(Record[] rec, boolean hideToolstrip) {
		if (rec == null)
			rec = new Record[] {};
		itemViewer.setData(rec);

		thread_id = rec.length == 0 ? 0 : rec[0].getAttributeAsInt("thread_id");
		tsMain.setVisible(thread_id == 0 || !hideToolstrip);

		ArrayList<String> criterias = new ArrayList<String>();
		criterias.add("thread_id=" + (thread_id == 0 ? -1 : thread_id));
		criterias.add("system_id=" + DocFlow.system_id);
		SplashDialog.showSplash();
		DocFlow.docFlowService.getDocListWithDocTypeXML(-1, 0,
				(new Date().getTime()) + (100000 * 20000), DocFlow.language_id,
				criterias, false, false, true, null,
				new AsyncCallback<DocTypeWithDocList>() {

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						System.out.println(caught.getMessage());

					}

					@Override
					public void onSuccess(DocTypeWithDocList result) {
						SplashDialog.hideSplash();
						setDocListAndType(result);
					}
				});

	}

	protected void setDocListAndType(DocTypeWithDocList result) {
		lgGrid.setData(new Record[] {});
		if (result == null)
			return;

		ArrayList<DocumentShort> docShort = result.getDocList();
		for (DocumentShort documentShort : docShort) {
			Record rec = DocumentHistoryListGrid.createrecord(documentShort,
					null);
			rec.setAttribute("_xml", documentShort.getShxml());
			rec.setAttribute("_dtxml", documentShort.getDoc_template());
			lgGrid.addData(rec);
		}
	}

	private void updateBinding(final int mThread_id) {
		final Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("thread_id", mThread_id);
		mp.put("doc_id", WSelectDocumentThread.this.doc_id);
		DSRequest req = new DSRequest();
		req.setOperationId("updateBinding");
		documentThreadDS.updateData(new Record(mp), new DSCallback() {

			@Override
			public void execute(DSResponse dsResponse, Object data,
					DSRequest dsRequest) {

				if (mThread_id > 0) {
					Criteria cr = new Criteria();
					cr.setAttribute("thread_id", mThread_id);
					documentThreadDS.fetchData(cr, new DSCallback() {

						@Override
						public void execute(DSResponse dsResponse, Object data,
								DSRequest dsRequest) {
							Record[] rec = dsResponse.getData();
							setData(rec, true);
						}
					});
				} else {
					setData(null, true);
				}
				dfNew.hide();
				dfSearch.setVisible(false);
				lgThreads.setVisible(false);
			}
		}, req);
	}
}
