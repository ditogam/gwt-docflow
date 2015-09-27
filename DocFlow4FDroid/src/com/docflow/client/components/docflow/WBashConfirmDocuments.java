package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.FieldDefinitionItem;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.common.DocumentFile;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class WBashConfirmDocuments extends Window {

	public static void showWindow(int docType) {
		new WBashConfirmDocuments(docType).show();
	}

	private ListGrid lgGrid;
	private int docType;
	private DocType doctypeValue;
	private HashMap<String, FieldDefinitionItem> definitionMap;
	private boolean applyed;
	private TextAreaItem taReplica;

	private CurrentTimeItem diDocDate;

	public WBashConfirmDocuments(int docType) {
		applyed = false;
		definitionMap = new HashMap<String, FieldDefinitionItem>();
		this.docType = docType;
		lgGrid = DocumentHistoryListGrid.createDocumentListGrid();
		ListGridField[] fields = lgGrid.getFields();

		ListGridField[] newfields = new ListGridField[fields.length + 2];
		for (int i = 0; i < fields.length; i++) {
			newfields[i] = fields[i];
			newfields[i].setCanEdit(false);
		}
		ListGridField lgfChecked = new ListGridField("_cheked", "CH");
		lgfChecked.setCanEdit(true);
		lgfChecked.setType(ListGridFieldType.BOOLEAN);
		ListGridField lgfXml = new ListGridField("_xml", "CH");
		lgfXml.setHidden(true);

		newfields[fields.length] = lgfChecked;
		newfields[fields.length + 1] = lgfXml;

		lgGrid.setFields(newfields);

		lgGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				if (event.getRecord() == null)
					return;
				ListGridField field = lgGrid.getField(event.getColNum());
				if (field == null)
					return;
				String fieldName = field.getName();
				if (fieldName != null
						&& (fieldName.equals("_docId") || fieldName
								.equals("_files"))) {
					int docid = event.getRecord().getAttributeAsInt("_docId");
					if (docid <= 0)
						return;
					if (event.getRecord().getAttributeAsInt("_files") <= 0)
						return;
					DocFlow.docFlowService.getFilesForDocument(docid,
							new AsyncCallback<ArrayList<DocumentFile>>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onSuccess(
										ArrayList<DocumentFile> result) {
									WDocFiles.showWindow(result, true);
								}

							});
				}

			}
		});

		final CheckboxItem chItem = new CheckboxItem("chItem",
				"Select/Unselect all documents");

		chItem.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				selectAllDocuments(chItem.getValueAsBoolean());

			}
		});

		ButtonItem biApply = new ButtonItem("biApply", "Apply");
		biApply.setStartRow(false);
		biApply.setEndRow(false);
		ButtonItem biRefresh = new ButtonItem("biRefresh", "Refresh");
		biRefresh.setStartRow(false);
		biRefresh.setEndRow(false);

		DynamicForm dfApply = new DynamicForm();
		dfApply.setTitleOrientation(TitleOrientation.TOP);
		dfApply.setNumCols(2);

		diDocDate = new CurrentTimeItem("_docDate", "თარიღი");
		taReplica = new TextAreaItem("_replica", "კომენტარი");
		taReplica.setWidth(700);

		dfApply.setFields(chItem, diDocDate, taReplica);

		biApply.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				apply();

			}
		});

		biRefresh.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();

			}
		});
		final HTMLPane itemViewer = new HTMLPane();
		VLayout vl = new VLayout();
		vl.addMember(dfApply);
		dfApply = new DynamicForm();
		dfApply.setFields(biApply, biRefresh);
		vl.addMember(dfApply);
		vl.addMember(lgGrid);
		vl.addMember(itemViewer);
		lgGrid.addRecordClickHandler(new RecordClickHandler() {

			@Override
			public void onRecordClick(RecordClickEvent event) {
				showDocContent(event.getRecord(), itemViewer);

			}
		});

		this.addItem(vl);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.setHeight(800);
		this.setWidth(1350);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					if (applyed) {
						DocFlowSectionStack.docFlowSectionStack.documentSearchForm
								.search(false);
					}
					destroy();
				}
			}
		});
		refresh();
	}

	protected void apply() {
		int status = DocFlow.user_obj.getApproved_status(DocFlow.system_id);
		final ArrayList<Integer> docids = new ArrayList<Integer>();
		RecordList records = lgGrid.getRecordList();
		for (int i = 0; i < records.getLength(); i++) {
			Record r = records.get(i);
			if (r.getAttributeAsBoolean("_cheked"))
				docids.add(r.getAttributeAsInt("_docId"));
		}
		if (docids.isEmpty()) {
			SC.say("Nothing to apply");
			return;
		}

		int user_id = DocFlow.user_id;
		int language_id = DocFlow.language_id;
		String replica = "";
		if (taReplica.getValue() != null)
			replica = taReplica.getValue().toString();
		Date dt = diDocDate.getValueAsDate();
		long time = dt.getTime();
		DocFlow.docFlowService.documentsChangeState(docids, replica, status,
				user_id, language_id, time,
				new AsyncCallback<ArrayList<Integer>>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.toString());

					}

					@Override
					public void onSuccess(ArrayList<Integer> result) {
						if (result == null)
							result = new ArrayList<Integer>();
						boolean applyed = result.size() != docids.size();
						if (result.size() > 0) {
							String docIds = "";
							for (Integer docId : result) {
								if (docIds.length() > 0)
									docIds += ",";
								docIds += docId;
							}
							SC.say("Could not change statuses for following doc ids=\n"
									+ docIds);

						}
						if (applyed)
							WBashConfirmDocuments.this.applyed = true;
						refresh();

					}
				});
	}

	protected void refresh() {
		SplashDialog.showSplash();
		Date dtStart = DocFlowSectionStack.docFlowSectionStack.documentSearchForm.diStart
				.getValueAsDate();
		Date dtEnd = DocFlowSectionStack.docFlowSectionStack.documentSearchForm.diEnd
				.getValueAsDate();
		ArrayList<String> criterias = DocFlowSectionStack.docFlowSectionStack.documentSearchForm
				.createCriteria();
		for (String cr : criterias) {
			if (cr.indexOf("doc_status_id") != -1) {
				criterias.remove(cr);
				break;
			}
		}
		criterias.add("doc_status_id="
				+ DocFlow.user_obj.getInitial_status(DocFlow.system_id));
		DocFlow.docFlowService.getDocListForType(docType, dtStart.getTime(),
				dtEnd.getTime(), DocFlow.language_id, criterias, false,
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

	protected void selectAllDocuments(Boolean checked) {
		RecordList records = lgGrid.getRecordList();
		for (int i = 0; i < records.getLength(); i++) {
			Record r = records.get(i);
			r.setAttribute("_cheked", checked);
		}
		lgGrid.redraw();
	}

	protected void setDocListAndType(DocTypeWithDocList result) {
		lgGrid.setData(new Record[] {});
		if (result == null)
			return;
		doctypeValue = result.getDocType();
		String dt = doctypeValue.getDoc_template();

		FormDefinition fd = new FormDefinition();
		fd.setXml(dt);

		for (FormGroup formGroup : fd.getFormGroups()) {
			ArrayList<FieldDefinition> fields = formGroup.getFieldDefinitions();
			for (FieldDefinition fieldDefinition : fields) {
				FieldDefinitionItem fi = new FieldDefinitionItem(
						fieldDefinition, null);
				definitionMap.put(fieldDefinition.getFieldName(), fi);
			}
		}
		ArrayList<DocumentShort> docShort = result.getDocList();
		for (DocumentShort documentShort : docShort) {
			Record rec = DocumentHistoryListGrid.createrecord(documentShort,
					null);
			rec.setAttribute("_xml", documentShort.getShxml());

			lgGrid.addData(rec);
		}
	}

	protected void showDocContent(Record record, HTMLPane itemViewer) {
		if (record == null) {
			itemViewer.setContents("");
			return;
		}
		String cont = record.getAttribute("_xml");
		if (cont == null) {
			itemViewer.setContents("");
			return;
		}

		HashMap<String, String> values = DocumentDetailTabPane.getData(cont,
				true, null);

		itemViewer.setContents(DocumentDetailTabPane.generateHtml(values, null,
				definitionMap, null, null));
	}
}
