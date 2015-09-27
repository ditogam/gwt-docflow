package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.AddressComponent;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class DocumentSearchForm extends DynamicForm {

        private ButtonItem findItem;
        private ButtonItem showStatusItem;
        private ButtonItem bashConfirm;

        CurrentTimeItem diStart;
        CurrentTimeItem diEnd;
        private TextItem tiUserName;
        private AddressComponent addrComp;
        private IntegerItem iiZona;
        private SelectItem siDocStatus;
        private IntegerItem iiCustomer;
        private TextItem tiContent;
        private IntegerItem tiDocId;
        private IntegerItem tiCancelary;
        private StaticTextItem stDocCount;

        private int docTypeId;

        public static DocumentSearchForm instance;

        public DocumentSearchForm() {
                instance = this;
                setTop(20);
                setCellPadding(6);
                setNumCols(5);
                setStyleName("defaultBorder");
                setTitleOrientation(TitleOrientation.TOP);

                findItem = new ButtonItem("Find");
                findItem.setIcon("silk/find.png");
                findItem.setWidth(70);
                findItem.setEndRow(false);
                HashMap<String, String> listSqls = new HashMap<String, String>();
                listSqls.put("" + ClSelection.T_REGION, "" + ClSelection.T_REGION);
                listSqls.put("" + ClSelection.T_DOC_STATUS, ""
                                + ClSelection.T_DOC_STATUS + "_" + ClSelection.T_LANGUAGE + "_"
                                + DocFlow.language_id);
                siDocStatus = new SelectItem("siDocStatus", "Doc Status");
                diStart = new CurrentTimeItem("diStart", "Start");
                diStart.setUseTextField(false);
                diEnd = new CurrentTimeItem("diEnd", "End");
                diEnd.setUseTextField(false);

                setCurrentDate();
                tiUserName = new TextItem("tiUserName", "User Name");
                iiZona = new IntegerItem("iiZona", "Zona");
                iiCustomer = new IntegerItem("iiCustomer", "Customer");
                tiContent = new TextItem("tiContent", "Content");
                tiDocId = new IntegerItem("tiDocId", "Doc ID");
                tiCancelary = new IntegerItem("tiCancelary", "Cancelary");
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
                findItem.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                                search(false);

                        }
                });
                stDocCount = new StaticTextItem("stDocCount", "Result Count");

                if (true) {
                        showStatusItem = new ButtonItem("showStatusItem", "Report");
                        showStatusItem.setStartRow(false);
                        showStatusItem.setEndRow(false);
                        showStatusItem.addClickHandler(new ClickHandler() {

                                @Override
                                public void onClick(ClickEvent event) {
                                        Date dtStart = diStart.getValueAsDate();
                                        Date dtEnd = diEnd.getValueAsDate();
                                        int language = DocFlow.language_id;
                                        int doctype = docTypeId;
                                        long startdate = dtStart.getTime();
                                        long enddate = dtEnd.getTime();
                                        int userid = DocFlow
                                                        .hasPermition(PermissionNames.VIEW_ALL_DOCUMENTS) ? 0
                                                        : DocFlow.user_id;
                                        String _url = "reportgenerator.jsp?language=" + language
                                                        + "&doctype=" + doctype + "&startdate=" + startdate
                                                        + "&enddate=" + enddate + "&userid=" + userid;
                                        ArrayList<String> criterias = createCriteria();
                                        String ret = "";
                                        Document doc = XMLParser.createDocument();
                                        Element rootElem = doc.createElement("DocDef");
                                        doc.appendChild(rootElem);

                                        for (String key : criterias) {
                                                Element val = doc.createElement("Val");
                                                val.setAttribute("V", key);
                                                rootElem.appendChild(val);
                                        }
                                        ret = doc.toString();
                                        _url += "&xml=" + URL.encode(ret);
                                        Window.open(_url, "yourWindowName",
                                                        "location=yes,resizable=yes,scrollbars=yess,status=yes");

                                }
                        });

                        bashConfirm = new ButtonItem("bashConfirm", "Bash Confirm");
                        bashConfirm.setStartRow(false);
                        bashConfirm.setEndRow(false);
                        bashConfirm.setDisabled(true);

                        bashConfirm.addClickHandler(new ClickHandler() {

                                @Override
                                public void onClick(ClickEvent event) {
                                        WBashConfirmDocuments.showWindow(docTypeId);

                                }
                        });
                }

        }

        private void addCriteria(ArrayList<String> criterias, FormItem formitem,
                        String formatedString, String notvalue) {
                Object obj = formitem.getValue();
                if (obj == null)
                        return;
                String value = obj.toString().trim();
                if (value.length() == 0)
                        return;
                if (value.equals(notvalue))
                        return;
                value = formatedString.replaceAll("%s", value);
                criterias.add(value);
        }

        public ArrayList<String> createCriteria() {
                ArrayList<String> criterias = new ArrayList<String>();
                criterias.add("system_id=" + DocFlow.system_id);
                addCriteria(criterias, tiUserName, "user_name='%s'", "");
                addCriteria(criterias, addrComp.getSiRegion(), "regionid=%s", "-1");
                addCriteria(criterias, addrComp.getSiSubregion(), "subregionid=%s",
                                "-1");
                addCriteria(criterias, addrComp.getSiCity(), "cityid=%s", "-1");
                addCriteria(criterias, iiZona, "czona=%s", "");
                addCriteria(criterias, siDocStatus, "doc_status_id=%s", "-1");
                addCriteria(criterias, iiCustomer, "cust_id=%s", "");
                addCriteria(criterias, tiContent, "content_xml like '%%s%'", "");
                addCriteria(criterias, tiDocId, "id::character varying like '%s%'", "");
                addCriteria(criterias, tiCancelary, "cancelary_nom like '%%s%'", "");
                String doctypeFilter = "doc_type_id=" + docTypeId;
                if (docTypeId <= 0)
                        doctypeFilter = "group_id=" + Math.abs(docTypeId);
                criterias.add(doctypeFilter);
                if (!DocFlow.hasPermition(PermissionNames.VIEW_ALL_DOCUMENTS))
                        criterias.add("user_id=" + DocFlow.user_id);
                return criterias;
        }

        public void search(final boolean print) {

                Date dtStart = diStart.getValueAsDate();
                Date dtEnd = diEnd.getValueAsDate();
                ArrayList<String> criterias = createCriteria();
                SplashDialog.showSplash();
                DocFlow.docFlowService.getDocListForType(docTypeId, dtStart.getTime(),
                                dtEnd.getTime(), DocFlow.language_id, criterias, print,
                                new AsyncCallback<DocTypeWithDocList>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                                SplashDialog.hideSplash();
                                                SC.say(caught.getMessage());

                                        }

                                        @Override
                                        public void onSuccess(DocTypeWithDocList result) {
                                                SplashDialog.hideSplash();
                                                if (!print) {
                                                        stDocCount.setValue(result.getDocList().size());
                                                        DocFlowSectionStack.docFlowSectionStack.documentHistoryListGrid
                                                                        .setDocListAndType(result);
                                                        DocFlowSectionStack.docFlowSectionStack.detailTabPane
                                                                        .setDocType(result.getDocType());
                                                }
                                        }
                                });

        }

        public void setCurrentDate() {
                diStart.setValue(DocFlow.getCurrentDate());
                diEnd.setValue(new Date(DocFlow.currenttime + (1000 * 60 * 60 * 24)));
        }

        public void setDocState(int state) {
                siDocStatus.setValue("" + state);
        }

        public void setDocTypeId(int docType) {
                this.docTypeId = docType;
                bashConfirm.setDisabled(docType < 0);
                bashConfirm.setVisible(DocFlow
                                .hasPermition(PermissionNames.CAN_CONFIRM_BASH)
                                || (docType == 30 && DocFlow
                                                .hasPermition(PermissionNames.CAN_CONFIRM_BASH_COEF)));
        }

        private void setResults(HashMap<String, ArrayList<ClSelectionItem>> result) {
                result = result == null ? new HashMap<String, ArrayList<ClSelectionItem>>()
                                : result;
                addrComp = new AddressComponent(false, true, result.get(""
                                + ClSelection.T_REGION));
                setStatuses();
                addrComp.getSiRegion().setTitle("Region");
                addrComp.getSiSubregion().setTitle("Sub Region");
                addrComp.getSiCity().setTitle("City");
                stDocCount.setTitleOrientation(TitleOrientation.LEFT);
                stDocCount.setRequired(true);
                FormItem[] items = new FormItem[] { diStart, diEnd, tiUserName,
                                addrComp.getSiRegion(), addrComp.getSiSubregion(),
                                addrComp.getSiCity(), iiZona, siDocStatus, iiCustomer,
                                tiContent, tiDocId, tiCancelary, findItem, showStatusItem,
                                bashConfirm, stDocCount };
                // if (showStatusItem != null) {
                // FormItem[] itemsTmp = new FormItem[items.length + 1];
                // for (int i = 0; i < items.length; i++) {
                // itemsTmp[i] = items[i];
                // }
                // itemsTmp[items.length] = showStatusItem;
                // items = itemsTmp;
                // }
                setFields(items);
                // showStatusItem.hide();
                // if (!DocFlow.hasPermition("CAN_SEE_STATUSES")) {
                // showStatusItem.hide();
                // }

        }

        private HashMap<Integer, Integer> mpCurrentStatus = new HashMap<Integer, Integer>();

        public void saveStatus() {
                int cur_status = -1;
                try {
                        cur_status = Integer.parseInt(siDocStatus.getValueAsString());
                } catch (Exception e) {
                }
                mpCurrentStatus.put(DocFlow.system_id, cur_status);

        }

        public void setStatuses() {
                setSelectItems(siDocStatus,
                                DocFlow.user_obj.getStatuses(DocFlow.system_id));
                try {
                        siDocStatus.setValue(mpCurrentStatus.get(DocFlow.system_id)
                                        .toString());
                } catch (Exception e) {
                }
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