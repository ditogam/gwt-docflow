package com.docflowdroid.comp.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.View;

import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.common.shared.ds.DsField;
import com.docflowdroid.ActivityHelper;
import com.docflowdroid.DocFlow;
import com.docflowdroid.MainActivity;
import com.docflowdroid.common.ds.DsOperationResult;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;

public class ListGrid {

	public static int FETCH_LENGTH = 100;

	private ListGridField[] fields;
	private String dsName;
	private String dsOperationId;
	private boolean showRowNum;

	private long startRow = -1;
	private long endRow = -1;

	private int storeStartRow = 0;
	private int storeEndRow = FETCH_LENGTH;

	private Map<String, Object> criteria = null;
	private long totalRows;
	private List<Map<String, Object>> data;
	private boolean showHeader = true;
	private boolean isnewdata = true;

	private boolean fetchingData = false;

	private boolean autofetch;
	private ListGridView gridView;

	private ArrayList<Integer> fetcheData = new ArrayList<Integer>();

	public ListGrid() {
		this(null);
	}

	public ListGrid(ListGridField[] fields) {
		super();
		this.fields = fields;
	}

	public ListGridField[] getFields() {
		return fields;
	}

	public void setFields(ListGridField[] fields) {
		this.fields = fields;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getDsOperationId() {
		return dsOperationId;
	}

	public void setDsOperationId(String dsOperationId) {
		this.dsOperationId = dsOperationId;
	}

	public ListGridField getField(String name) {
		if (fields != null)
			for (int i = 0; i < fields.length; i++)
				if (fields[i].getFieldName().equals(name))
					return fields[i];
		return null;
	}

	public void refreshFromDs() {
		if (fields == null && !(dsName == null || dsName.trim().isEmpty())) {
			ProcessExecutor.execute(new IProcess() {

				@Override
				public void execute() throws Exception {

					MainActivity.instance.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								List<DsField> list = DocFlow.docFlowService
										.getDataSourceFields(dsName);
								fields = new ListGridField[list.size()];
								for (int i = 0; i < fields.length; i++) {
									fields[i] = new ListGridField(list.get(i));
								}
							} catch (Exception e) {
								final Exception ex = e;
								MainActivity.instance
										.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												ActivityHelper.showAlert(
														MainActivity.instance,
														ex);

											}
										});
							}
						}
					});

				}
			}, MainActivity.instance);
		}
	}

	public void fetchData() {
		fetchData(null);
	}

	public void fetchData(Map<String, Object> criteria) {
		fetchData(criteria, null);
	}

	public void fetchData(Map<String, Object> criteria,
			final DsOperationResult callback) {
		fetchData(criteria, callback, false, startRow, endRow);
	}

	public void setData(CDSResponce responce) {
		startRow = responce.getStartRow();
		endRow = (responce.getEndRow());

		fetcheData.clear();

		for (int i = (int) startRow - FETCH_LENGTH; i < (int) endRow
				+ FETCH_LENGTH; i++) {
			fetcheData.add(i);
		}

		setTotalRows(responce.getTotalRows());
		setData(responce.getResult());
		if (gridView != null)
			gridView.setData(getData(), isnewdata);
		isnewdata = false;
	}

	public boolean canDoPreviews(int index) {
		return !fetcheData.contains(index);
	}

	public void refreshData() {
		isnewdata = true;
		fetchData(criteria, null, true, startRow, endRow);
	}

	public void setContext() {

	}

	public void fetchData(Map<String, Object> cCriteria,
			final DsOperationResult callback, boolean refresh, long startRow,
			long endRow) {
		if ((dsName == null || dsName.trim().isEmpty()))
			return;
		if (!refresh && isCriteriaEquals(criteria, cCriteria)
				&& ((startRow == this.startRow) && (endRow == this.endRow)))
			return;
		if (startRow < 0)
			startRow = 0;
		if (endRow + 1 < startRow)
			endRow = FETCH_LENGTH;
		this.criteria = cCriteria;
		final CDSRequest dsReques = new CDSRequest(dsOperationId);
		dsReques.setStartRow(startRow);
		dsReques.setEndRow(endRow);

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {

				MainActivity.instance.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							CDSResponce responce = DocFlow.docFlowService
									.dsFetchData(dsName, criteria, dsReques);
							setData(responce);
							if (callback != null)
								callback.operationResult(responce);
							fetchingData = (false);
						} catch (Exception e) {
							final Exception ex = e;
							fetchingData = (false);
							MainActivity.instance.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									ActivityHelper.showAlert(
											MainActivity.instance, ex);

								}
							});
						}
					}
				});

			}
		}, MainActivity.instance);

	}

	private boolean isCriteriaAllValuesEquals(Map<String, Object> c1,
			Map<String, Object> c2) {
		Set<String> keys = c1.keySet();
		for (String key : keys) {
			Object o1 = c1.get(key);
			if (o1 == null)
				return false;
			Object o2 = c2.get(key);
			if (o2 == null)
				return false;
			if (!o1.equals(o2))
				return false;

		}
		return true;
	}

	private boolean isCriteriaEquals(Map<String, Object> criteria,
			Map<String, Object> cCriteria) {
		if (criteria != null && cCriteria != null) {
			return isCriteriaAllValuesEquals(criteria, cCriteria)
					&& isCriteriaAllValuesEquals(cCriteria, criteria);
		}
		return true;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public Map<String, Object> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, Object> criteria) {
		this.criteria = criteria;
	}

	public long getEndRow() {
		return endRow;
	}

	protected void setEndRow(long endRow) {
		this.endRow = endRow;
	}

	public long getTotalRows() {
		return totalRows;
	}

	private void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public long getStartRow() {
		return startRow;
	}

	public View createView(Context context) {
		try {
			gridView = new ListGridView(context);
			gridView.setGrid(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return gridView.getView();
	}

	public boolean isShowHeader() {
		return showHeader;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public boolean isAutofetch() {
		return autofetch;
	}

	public void setAutofetch(boolean autofetch) {
		this.autofetch = autofetch;
	}

	public ListGridView getGridView() {
		return gridView;
	}

	public void fetchNext() {
		if (fetchingData)
			return;
		fetchingData = (true);
		isnewdata = false;

		fetchData(criteria, null, false, endRow, endRow + FETCH_LENGTH);

	}

	public void fetchPreviews() {
		if (fetchingData)
			return;
		fetchingData = (true);
		isnewdata = false;
		fetchData(criteria, null, false, startRow - FETCH_LENGTH, startRow);
	}

	public void fetchFirst() {
		if (fetchingData)
			return;
		fetchingData = (true);
		isnewdata = false;

		fetchData(criteria, null, false, 0, FETCH_LENGTH);

	}

	public void fetchLast() {
		if (fetchingData)
			return;
		fetchingData = (true);
		isnewdata = false;
		fetchData(criteria, null, false, totalRows - FETCH_LENGTH, totalRows);
	}

	public void gotoPage(int page) {
		if (fetchingData)
			return;
		fetchingData = (true);
		isnewdata = false;
		int start_row = (page - 1) * FETCH_LENGTH;
		fetchData(criteria, null, false, start_row, start_row + FETCH_LENGTH);
	}

	public int getStoreEndRow() {
		return storeEndRow;
	}

	public int getStoreStartRow() {
		return storeStartRow;
	}

	public boolean isShowRowNum() {
		return showRowNum;
	}

	public void setShowRowNum(boolean showRowNum) {
		this.showRowNum = showRowNum;
	}

}
