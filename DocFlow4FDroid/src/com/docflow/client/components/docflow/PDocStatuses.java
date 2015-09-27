package com.docflow.client.components.docflow;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.shared.DocStatusCount;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class PDocStatuses extends HLayout {
	private Timer timer;
	private ListGrid lgStatuses;
	private static final int TIMEOUT = 10000;
	private static final String STATUS_NAME = "status_";
	private boolean started = false;

	ArrayList<ClSelectionItem> docStatuses;

	public static PDocStatuses instance;

	public PDocStatuses() {
		instance = this;
		createStatusGrid();
		timer = new Timer() {

			@Override
			public void run() {
				timer.cancel();
				if (!started) {
					return;
				}
				callStatus();

			}
		};

	}

	public void createStatusGrid() {
		ListGridField lgDocTypeId = new ListGridField("doctypegroupid");
		lgDocTypeId.setHidden(true);
		lgDocTypeId.setType(ListGridFieldType.INTEGER);
		ListGridField lgDocTypeName = new ListGridField("doctypegroupiname",
				"Doc Type Name", 200);
		lgStatuses = new ListGrid();
		lgStatuses.setWidth100();
		lgStatuses.setHeight100();
		this.addMember(lgStatuses);
		lgStatuses.setFields(lgDocTypeId, lgDocTypeName);
		createColumns();
	}

	public void callStatus() {
		DocFlow.docFlowService.getDocCountStatus(DocFlow.language_id,
				DocFlow.system_id,
				new AsyncCallback<ArrayList<DocStatusCount>>() {

					@Override
					public void onFailure(Throwable caught) {
						timer.schedule(TIMEOUT);
					}

					@Override
					public void onSuccess(ArrayList<DocStatusCount> result) {
						refreshStatus(result);
						timer.schedule(TIMEOUT);
					}
				});
	}

	public void doTiming(boolean start) {
		this.started = start;
		if (start) {
			if (timer != null)
				timer.schedule(10);
			// DocFlow.doclistWindow.hide();
		}

		else {
			if (timer != null)
				timer.cancel();
			// DocFlow.doclistWindow.show();
		}
	}

	private void refreshStatus(ArrayList<DocStatusCount> result) {

		Record[] recs = lgStatuses.getRecords();
		for (Record record : recs) {
			for (int i = 0; i < docStatuses.size(); i++) {
				record.setAttribute("status_" + docStatuses.get(i).getId(),
						(String) null);
			}
			record.setAttribute("fullCount", (Integer) null);
		}
		TreeMap<Integer, Integer> mapSums = new TreeMap<Integer, Integer>();
		for (DocStatusCount docStatusCount : result) {
			if (docStatusCount.getSystem_id() != DocFlow.system_id)
				continue;
			Record rec = lgStatuses.getRecordList().find("doctypegroupid",
					docStatusCount.getGroup_id());
			if (rec == null) {
				rec = new ListGridRecord();
				rec.setAttribute("doctypegroupid", docStatusCount.getGroup_id());
				rec.setAttribute("doctypegroupiname",
						docStatusCount.getDoctypegroupvalue());
				lgStatuses.addData(rec);
			}
			rec.setAttribute("status_" + docStatusCount.getDoc_status_id(),
					docStatusCount.getCountofdocs());
			Integer sum = mapSums.get(docStatusCount.getGroup_id());
			if (sum == null) {
				sum = new Integer(0);
			}
			sum = new Integer(sum.intValue() + docStatusCount.getCountofdocs());
			mapSums.put(docStatusCount.getGroup_id(), sum);
		}
		Set<Integer> keys = mapSums.keySet();
		for (Integer key : keys) {
			Record rec = lgStatuses.getRecordList().find("doctypegroupid", key);
			if (rec != null)
				rec.setAttribute("fullCount", mapSums.get(key));
		}
		lgStatuses.refreshFields();
	}

	public void recreateStatuses() {
		lgStatuses.destroy();

		createStatusGrid();
		callStatus();
	}

	protected void createColumns() {

		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		ListGridField[] oldFields = lgStatuses.getAllFields();
		for (int i = 0; i < oldFields.length; i++) {
			oldFields[i].setHidden(i == 0);
			fields.add(oldFields[i]);
		}
		// fields.add(lgDocTypeId);
		// fields.add(lgDocTypeName);

		docStatuses = DocFlow.user_obj.getStatuses(DocFlow.system_id);
		TreeMap<Long, Long> mapIds = new TreeMap<Long, Long>();
		for (ClSelectionItem clSelectionItem : docStatuses) {
			if (mapIds.get(clSelectionItem.getId()) != null)
				continue;
			mapIds.put(clSelectionItem.getId(), clSelectionItem.getId());
			ListGridField lgStatus = new ListGridField(STATUS_NAME
					+ clSelectionItem.getId(), clSelectionItem.getValue(), 50);
			fields.add(lgStatus);
			lgStatus.setType(ListGridFieldType.INTEGER);
		}
		ListGridField lgSum = new ListGridField("fullCount", "SUM", 50);
		lgSum.setType(ListGridFieldType.INTEGER);
		fields.add(lgSum);

		lgStatuses.setFields(fields.toArray(new ListGridField[] {}));

		mapIds.clear();

		callStatus();
		lgStatuses.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {

				Record rec = event.getRecord();

				if (rec == null)
					return;
				int state = -1;
				String colName = lgStatuses.getFieldName(event.getColNum());
				if (colName.startsWith(STATUS_NAME)) {
					colName = colName.substring(STATUS_NAME.length());
					try {
						state = Integer.parseInt(colName);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				try {
					int groupid = rec.getAttributeAsInt("doctypegroupid");
					DocTypeTreeGrid.getInstance().selectGroupId(groupid, state);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});

	}
}
