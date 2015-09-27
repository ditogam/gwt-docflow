package com.docflow.client.components.docflow;

import com.docflow.client.DocFlow;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;

public class PReplicaHistory extends HLayout {
	private ListGrid lgHistory;
	private HTMLPane dfReplica;

	public PReplicaHistory() {
		lgHistory = new ListGrid();
		lgHistory.setWidth("70%");
		lgHistory.setHeight100();

		lgHistory.setFields(new ListGridField("hist_time", "Time"),
				new ListGridField("old_status", "Old status"),
				new ListGridField("new_status", "New status"),
				new ListGridField("user_name", "User"));

		lgHistory.setDataSource(DocFlow.getDataSource("DocReplicaDS"));
		lgHistory.setAutoFetchData(false);
		dfReplica = new HTMLPane();
		dfReplica.setWidth("30%");
		dfReplica.setHeight100();

		setMembers(lgHistory, dfReplica);
		setPaneContent(null);

		lgHistory.addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				setPaneContent(event.getSelectedRecord());
			}
		});

	}

	public void setValues(Long doc_id) {
		Criteria criteria = new Criteria();
		criteria.setAttribute("doc_id", doc_id);
		criteria.setAttribute("lang_id", DocFlow.language_id);
		criteria.setAttribute("ddd", System.currentTimeMillis());

		try {
			lgHistory.filterData(criteria, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					if (response.getData() != null
							&& response.getData().length > 0) {
						setPaneContent(response.getData()[0]);
						lgHistory.selectRecord(response.getData()[0]);
					} else
						setPaneContent(null);

				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void setPaneContent(Record record) {
		String value = null;
		if (record != null) {
			value = record.getAttribute("replica");
		}
		if (value != null)
			value = value.replace("\n", "<br>");
		dfReplica.setContents(value);
	}

}
