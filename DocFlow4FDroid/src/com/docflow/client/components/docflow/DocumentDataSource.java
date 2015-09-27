package com.docflow.client.components.docflow;

import java.util.ArrayList;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.ListSizes;
import com.docflow.shared.docflow.DocTypeWithDocList;
import com.docflow.shared.docflow.DocumentShort;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class DocumentDataSource extends DataSource {
	public DocumentDataSource() {
		DataSourceField f = new DataSourceField("_docId", FieldType.INTEGER);
		f.setPrimaryKey(true);
		setFields(f);
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
	}

	private DocTypeWithDocList result;

	public void setResult(DocTypeWithDocList result) {
		this.result = result;
	}

	@Override
	protected Object transformRequest(DSRequest request) {
		String requestId = request.getRequestId();
		DSResponse response = new DSResponse();
		response.setAttribute("clientContext",
				request.getAttributeAsObject("clientContext"));
		// Asume success. TODO: check from security standpoint
		response.setStatus(0);
		switch (request.getOperationType()) {
		case FETCH:
			executeFetch(requestId, request, response);
			break;
		case ADD:
			executeAdd(requestId, request, response);
			break;
		case UPDATE:
			executeUpdate(requestId, request, response);
			break;
		case REMOVE:
			executeRemove(requestId, request, response);
			break;
		default:
			// FIXME: ignore instead, or log a warning?
			throw new UnsupportedOperationException(
					"Operation not supported on CustomDataSource: "
							+ request.getOperationType().getValue());
		}
		return request.getData();
	}

	protected void executeFetch(final String requestId,
			final DSRequest request, final DSResponse response) {

		if (result == null) {
			ListSizes ls = new ListSizes();
			ls.setStart_row(request.getStartRow());
			ls.setEnd_row(request.getEndRow());
			ls.setGenerate_sizes(true);

			SplashDialog.showSplash();
			DocFlow.docFlowService.getDocListForType(docTypeId, startTime,
					endTime, DocFlow.language_id, criterias, false, false, ls,
					new AsyncCallback<DocTypeWithDocList>() {

						@Override
						public void onFailure(Throwable caught) {
							SplashDialog.hideSplash();
							SC.say(caught.getMessage());

						}

						@Override
						public void onSuccess(DocTypeWithDocList result) {
							SplashDialog.hideSplash();
							setResult(result);
							executeFetch(requestId, request, response);
						}
					});

			return;
		}
		ArrayList<DocumentShort> docShort = result.getDocList();
		response.setStartRow(result.getStart_row());
		response.setEndRow(result.getEnd_row());
		response.setTotalRows(result.getTotal_count());
		ListGridRecord[] list = new ListGridRecord[docShort.size()];
		for (int i = 0; i < list.length; i++) {
			ListGridRecord rec = DocumentHistoryListGrid.createrecord(
					docShort.get(i), null);
			list[i] = rec;
		}
		response.setData(list);
		result = null;
		processResponse(requestId, response);
	}

	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
		Object o=response.getData();
		System.out.println(o);

	}

	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {

	}

	protected void executeRemove(String requestId, DSRequest request,
			DSResponse response) {

	}

	private int docTypeId;
	private long startTime;
	private long endTime;
	private ArrayList<String> criterias;

	public void setCriteria(int docTypeId, long startTime, long endTime,
			ArrayList<String> criterias) {
		this.docTypeId = docTypeId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.criterias = criterias;
	}
}
