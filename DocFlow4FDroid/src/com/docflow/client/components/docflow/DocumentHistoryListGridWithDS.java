package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Date;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ListSizes;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentLong;
import com.docflow.shared.docflow.DocumentShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class DocumentHistoryListGridWithDS extends VLayout {
        public static ListGrid createDocumentListGrid(ListGrid listGrid) {
                listGrid = listGrid == null ? new ListGrid() : listGrid;
                listGrid.setShowRowNumbers(true);
                ListGridField uId = new ListGridField("_uId", "ID", 30);
                uId.setType(ListGridFieldType.TEXT);
                uId.setHidden(true);
                uId.setCanEdit(false);
                ListGridField docId = new ListGridField("_docId", "ID", 60);
                docId.setType(ListGridFieldType.INTEGER);
                docId.setCanEdit(false);
                ListGridField docversion = new ListGridField("_docversion", "ვერსია",
                                40);
                docversion.setType(ListGridFieldType.INTEGER);
                docversion.setCanEdit(false);

                ListGridField customer = new ListGridField("_customer", "აბონენტი", 60);
                customer.setType(ListGridFieldType.INTEGER);
                customer.setCanEdit(false);

                ListGridField customerName = new ListGridField("_customerName",
                                "აბონენტის სახელი", 180);
                customer.setCanEdit(false);

                ListGridField docDate = new ListGridField("_docDate", "თარიღი", 80);
                docDate.setType(ListGridFieldType.DATE);
                docDate.setCanEdit(false);
                ListGridField doc_flow_num = new ListGridField("_doc_flow_num",
                                "ნომერი", 100);
                doc_flow_num.setCanEdit(false);
                doc_flow_num.setType(ListGridFieldType.TEXT);

                final ListGridField docstatus = new ListGridField("_docstatus",
                                "სტატუსი", 80);
                docstatus.setCanEdit(true);
                docstatus.setType(ListGridFieldType.TEXT);

                final ListGridField docdelaystatus = new ListGridField(
                                "_docdelaystatus", "სტადია", 80);
                docstatus.setCanEdit(true);
                docstatus.setType(ListGridFieldType.TEXT);

                docstatus.setEditorType(new SelectItem());
                ListGridField doctype = new ListGridField("_doctype", "ტიპი", 180);
                doctype.setCanEdit(false);
                doctype.setType(ListGridFieldType.TEXT);
                ListGridField zona = new ListGridField("_zona", "ზონა", 60);
                zona.setCanEdit(false);
                ListGridField subregion = new ListGridField("_subregion", "რაიონი", 100);
                subregion.setCanEdit(false);
                ListGridField region = new ListGridField("_region", "რეგიონი", 100);
                region.setCanEdit(false);
                ListGridField applyer = new ListGridField("_applyer", "კონტ.რგოლი", 120);
                applyer.setCanEdit(false);
                ListGridField user_name = new ListGridField("_user_name", "User", 120);
                user_name.setCanEdit(false);

                ListGridField files = new ListGridField("_files", "F", 20);
                files.setAlign(Alignment.CENTER);
                files.setType(ListGridFieldType.IMAGE);
                files.setImageURLPrefix("icons/16/attachment");
                files.setImageURLSuffix(".png");
                files.setCanEdit(false);

                docId.setAlign(Alignment.LEFT);
                customer.setAlign(Alignment.LEFT);
                docversion.setAlign(Alignment.LEFT);
                doc_flow_num.setAlign(Alignment.LEFT);
                docDate.setAlign(Alignment.LEFT);
                doctype.setAlign(Alignment.LEFT);
                zona.setAlign(Alignment.LEFT);
                subregion.setAlign(Alignment.LEFT);
                region.setAlign(Alignment.LEFT);
                user_name.setAlign(Alignment.LEFT);
                docstatus.setAlign(Alignment.LEFT);
                applyer.setAlign(Alignment.LEFT);
                customerName.setAlign(Alignment.LEFT);
                docdelaystatus.setAlign(Alignment.LEFT);
                listGrid.setFields(uId, docId, files, docversion, doc_flow_num,
                                docDate, customer, customerName, doctype, zona, subregion,
                                region, user_name, docstatus, docdelaystatus, applyer);
                return listGrid;
        }

        public static ListGrid createDocumentListGrid() {
                return createDocumentListGrid(null);
        }

        public static ListGridRecord createrecord(DocumentShort documentShort,
                        ListGridRecord existance) {
                ListGridRecord rec = existance == null ? new ListGridRecord()
                                : existance;
                rec.setAttribute("_uId", HTMLPanel.createUniqueId());
                rec.setAttribute("_docId", documentShort.getId());
                rec.setAttribute("_docDate",
                                new Date(documentShort.getTransaction_date()));
                rec.setAttribute("_doc_flow_num", documentShort.getDoc_flow_num());
                rec.setAttribute("_docstatus", documentShort.getDocstatus());
                rec.setAttribute("_doctype", documentShort.getDoctype());
                rec.setAttribute("_user_name", documentShort.getUser_name());
                rec.setAttribute("_customer", documentShort.getCust_id());
                rec.setAttribute("_customerName", documentShort.getCustomer_name());

                rec.setAttribute("_docversion", documentShort.getVersion_id());
                rec.setAttribute("_zona", documentShort.getCzona());
                rec.setAttribute("_region", documentShort.getRegionname());
                rec.setAttribute("_subregion", documentShort.getSubregionname());
                rec.setAttribute("_applyer", documentShort.getController_name());
                rec.setAttribute("_docdelaystatus", documentShort.getTdocdelaystatus());
                rec.setAttribute("_files", documentShort.getFilecount() > 0 ? 1 : 0);
                return rec;
        }

        private ListGrid listGrid;
        private DocumentDataSource docDs;

        public DocumentHistoryListGridWithDS() {
                super();
                ToolStrip tsStrip = new ToolStrip();

                tsStrip.setHeight("8%");
                tsStrip.setWidth100();
                this.addMember(tsStrip);
                docDs = new DocumentDataSource();
                listGrid = createDocumentListGrid();
                listGrid.setHeight100();
                listGrid.setWidth100();
                listGrid.setDataSource(docDs);
                listGrid.setAutoFetchData(false);
                this.addMember(listGrid);
                listGrid.setCanEdit(false);

                listGrid.addDoubleClickHandler(new DoubleClickHandler() {

                        @Override
                        public void onDoubleClick(DoubleClickEvent event) {

                                setDocumentDetails();

                        }
                });

        }

        public void addDocument(DocumentShort documentShort) {
                ListGridRecord existance = (ListGridRecord) listGrid.getRecordList()
                                .find("_docId", documentShort.getId());
                boolean existed = existance != null;
                existance = createrecord(documentShort, existance);
                if (!existed) {
                        final ListGridRecord record=existance;
                        
                        listGrid.addData(existance,new DSCallback() {
                                @Override
                                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                                        listGrid.selectSingleRecord(record);
                                        listGrid.redraw();
                                        setDocumentDetails();
                                        
                                }
                        });
                        return;
                }
                
                listGrid.selectSingleRecord(existance);
                listGrid.redraw();
                setDocumentDetails();
        }

        public void setDocListAndType(DocTypeWithDocList result) {
                setDocTypeId(0);
                if (result == null)
                        return;
                docDs.setResult(result);
                listGrid.invalidateCache();
                listGrid.fetchData();
                // ArrayList<DocumentShort> docShort = result.getDocList();
                // for (DocumentShort documentShort : docShort) {
                // listGrid.addData(createrecord(documentShort, null));
                // }

        }

        public void setDocTypeId(int docType) {
                // listGrid.setData(new Record[] {});
        }

        private void setDocumentDetails() {
                try {
                        String sid = listGrid.getSelectedRecord().getAttribute("_docId");
                        int id = Integer.parseInt(sid);
                        SplashDialog.showSplash();
                        DocFlow.docFlowService.getDocument(id, DocFlow.language_id,
                                        new AsyncCallback<DocumentLong>() {
                                                @Override
                                                public void onFailure(Throwable caught) {
                                                        SplashDialog.hideSplash();
                                                        // TODO Auto-generated method stub

                                                }

                                                @Override
                                                public void onSuccess(DocumentLong result) {
                                                        SplashDialog.hideSplash();
                                                        DocFlowSectionStack.docFlowSectionStack.detailTabPane
                                                                        .setDocument(result);
                                                }
                                        });
                } catch (Exception e) {
                        // TODO: handle exception
                }
        }

        public void setCriteria(int docTypeId, long startTime, long endTime,
                        ArrayList<String> criterias) {
                docDs.setCriteria(docTypeId, startTime, endTime, criterias);
        }
}