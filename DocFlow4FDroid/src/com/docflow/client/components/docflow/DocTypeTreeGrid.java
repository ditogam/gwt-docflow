package com.docflow.client.components.docflow;

import java.util.HashMap;

import com.docflow.client.DocFlow;
import com.docflow.client.components.CardLayoutCanvas;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DocTypeTreeGrid extends TreeGrid {

	private int lastselid = -1;
	// public static DocTypeTreeGrid instance;
	private static HashMap<Integer, DocTypeTreeGrid> instances = new HashMap<Integer, DocTypeTreeGrid>();
	private int system_id;

	private boolean settingmyself = false;

	public static DocTypeTreeGrid getInstance() {
		return instances.get(DocFlow.system_id);
	}

	public DocTypeTreeGrid(int system_id) {
		super();
		this.system_id = system_id;
		instances.put(system_id, this);

		DataSource ds = new DataSource();
		ds.setClientOnly(true);
		String id = "DocTypeListDs_" + system_id;
		ds.setID(id);
		ds.setTitleField("Name");
		ds.setRecordXPath("/DocTypeGroup/DocType");
		DataSourceTextField nameField = new DataSourceTextField("Name", "დოკუმენტის ტიპები", 128);
		DataSourceTextField jsField = new DataSourceTextField("JSType");
		jsField.setHidden(true);

		DataSourceIntegerField dgroup = new DataSourceIntegerField("TypeId", "Employee ID");
		dgroup.setPrimaryKey(true);
		dgroup.setRequired(true);

		DataSourceIntegerField doctype = new DataSourceIntegerField("GroupId", "Manager");
		doctype.setRequired(true);
		doctype.setForeignKey(id + ".TypeId");
		doctype.setRootValue("0");

		ds.setFields(nameField, dgroup, doctype, jsField);
		ds.setDataURL("doctypetree.jsp?lang=" + DocFlow.language_id + "&user_id=" + DocFlow.user_id + "&system_id="
				+ system_id);
		this.setDataSource(ds);
		this.setAutoFetchData(true);

		TreeGridField field = new TreeGridField("Name", "დოკუმენტის ტიპები");
		field.setCanSort(false);
		this.setFields(field);
		this.addDataArrivedHandler(new DataArrivedHandler() {

			@Override
			public void onDataArrived(com.smartgwt.client.widgets.grid.events.DataArrivedEvent event) {
				getData().openAll();

			}
		});
		this.fetchData(new Criteria(), new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				System.out.println(rawData);

			}
		});

		this.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (!DocFlow.hasPermition(PermissionNames.CAN_EDIT_DOC_TYPE))
					return;
				int id = getSelectionId(getSelectedRecord());
				if (id <= 0)
					return;
				DocFlow.docFlowService.getDocType(id, DocFlow.language_id, new AsyncCallback<DocType>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(DocType result) {
						WDocType.showIt(result);

					}
				});
			}
		});

		this.addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if (settingmyself) {
					return;
				}
				try {
					int id = getSelectionId(getSelectedRecord());
					if (lastselid != id) {
						lastselid = id;
						if (id == 7) {
							DocFlow.docFlow.card.showCard(CardLayoutCanvas.ZONECHANGE_PANEL);
						} else if (id == 27) {
							DocFlow.docFlow.card.showCard(CardLayoutCanvas.LIVE_PANEL);
						} else if (id == 31) {
							DocFlow.docFlow.card.showCard(CardLayoutCanvas.COEF_PANEL);
						} else if (id == 45) {
							DocFlow.docFlow.card.showCard(CardLayoutCanvas.PLOMB_PANEL);
						} else if (id == 50 && DocFlow.hasPermition(PermissionNames.READER_LIST_CHANGE)) {
							DocFlow.docFlow.card.showCard(CardLayoutCanvas.READER_LIST_PANEL);
						} else if (isJS(getSelectedRecord())) {
							CustomPanel.attachPanel(id);
						} else {
							attachDocflowPanel(lastselid);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

	public static void attachDocflowPanel(int id) {
		DocFlow.docFlow.card.showCard(CardLayoutCanvas.DOCFLOW_PANEL);
		DocFlowSectionStack.docFlowSectionStack.setDoctype(id);
	}

	private int getSelectionId(Record rec) {
		int id = Integer.parseInt(rec.getAttribute("TypeId"));
		return id;
	}

	private boolean isJS(Record rec) {
		try {
			String val = rec.getAttribute("JSType");
			return Integer.parseInt(val) == 1;
		} catch (Exception e) {
			return false;
		}
	}

	public void selectGroupId(int groupid, int state) {
		try {
			settingmyself = true;
			try {
				deselectAllRecords();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Record rec = null;
			Tree tr = getData();
			String tp_id = "-" + groupid;
			rec = tr.findById(tp_id);
			if (rec == null) {
				TreeNode[] trs = tr.getAllNodes();
				for (TreeNode treeNode : trs) {
					String grid = treeNode.getAttribute("TypeId");
					System.out.println(grid);
					if (grid != null && grid.equals(tp_id)) {
						rec = treeNode;
						break;
					}
				}
			}

			if (rec != null) {

				int id = -groupid;
				if (lastselid != id) {
					lastselid = id;
					DocFlowSectionStack.docFlowSectionStack.documentSearchForm.setCurrentDate();
					DocFlowSectionStack.docFlowSectionStack.documentSearchForm.setDocState(state);
					DocFlowSectionStack.docFlowSectionStack.setDoctype(lastselid);
				}
				selectRecord(rec, true);
				rec = getSelectedRecord();
				System.out.println(rec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			settingmyself = false;
		}

	}

	private HashMap<Integer, Integer> mpCurrentStatus = new HashMap<Integer, Integer>();

	public void saveStatus() {
		mpCurrentStatus.put(DocFlow.system_id, lastselid);

	}

	public void setCurrentDocType() {
		try {
			DocFlowSectionStack.docFlowSectionStack.setDoctype(mpCurrentStatus.get(DocFlow.system_id).intValue());
		} catch (Exception e) {
		}
	}

}
