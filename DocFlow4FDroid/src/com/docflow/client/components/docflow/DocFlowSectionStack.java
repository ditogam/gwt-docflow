package com.docflow.client.components.docflow;

import com.docflow.client.DocFlow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class DocFlowSectionStack extends SectionStack {

	public DocumentSearchForm documentSearchForm;
	public DocumentHistoryListGrid documentHistoryListGrid;
	public DocumentDetailTabPane detailTabPane;

	public static DocFlowSectionStack docFlowSectionStack;
	boolean timingFirstTime = true;

	public DocFlowSectionStack() {

		super();
		docFlowSectionStack = this;
		setVisibilityMode(VisibilityMode.MULTIPLE);

		final SectionStackSection searchtSection = new SectionStackSection(
				"Search");

		searchtSection.setExpanded(true);
		searchtSection.setResizeable(false);
		documentSearchForm = new DocumentSearchForm();
		Canvas searchCanvas = documentSearchForm;
		if (DocFlow.hasPermition("CAN_SEE_STATUSES")) {
			searchtSection.setTitle("Search and Daily Report");
			final TabSet topTabSet = new TabSet();
			Tab tTabSearch = new Tab("Search");
			tTabSearch.setPane(documentSearchForm);

			Tab tTabDailyReport = new Tab("Daily Report");
			final PDocStatuses dfStatus = new PDocStatuses();

			tTabDailyReport.setPane(dfStatus);
			topTabSet.addTab(tTabSearch);
			topTabSet.addTab(tTabDailyReport);
			topTabSet.addTabSelectedHandler(new TabSelectedHandler() {
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					if (timingFirstTime) {
						timingFirstTime = false;
						return;
					}
					dfStatus.doTiming(event.getTabNum() == 1);

				}
			});
			this.addSectionHeaderClickHandler(new SectionHeaderClickHandler() {

				@Override
				public void onSectionHeaderClick(SectionHeaderClickEvent event) {
					topTabSet.setSelectedTab(0);
					dfStatus.doTiming(false);
				}
			});
			searchCanvas = topTabSet;

		}
		searchCanvas.setHeight("30%");
		searchtSection.addItem(searchCanvas);

		this.addSection(searchtSection);

		SectionStackSection historySection = new SectionStackSection("History");
		historySection.setExpanded(true);
		historySection.setResizeable(true);
		documentHistoryListGrid = new DocumentHistoryListGrid();
		historySection.setItems(documentHistoryListGrid);
		historySection.setResizeable(true);
		this.addSection(historySection);

		SectionStackSection detailsSection = new SectionStackSection(
				"Document Details");
		detailsSection.setExpanded(true);
		detailsSection.setResizeable(true);
		detailTabPane = new DocumentDetailTabPane();
		detailsSection.setItems(detailTabPane);
		this.addSection(detailsSection);

	}

	public void setDoctype(int docType) {
		documentSearchForm.setDocTypeId(docType);
		documentHistoryListGrid.setDocTypeId(docType);
		detailTabPane.setDocTypeId(docType);
		documentSearchForm.search(false);
		// SplashDialog.showSplash();
		// Date dtStart = documentSearchForm.diStart.getValueAsDate();
		// Date dtEnd = documentSearchForm.diEnd.getValueAsDate();
		// DocFlow.docFlowService.getDocListForType(docType,
		// DocFlow.language_id,
		// DocFlow.hasPermition(PermitionNames.VIEW_ALL_DOCUMENTS) ? 0
		// : DocFlow.user_id, dtStart.getTime(), dtEnd.getTime(),
		// new AsyncCallback<DocTypeWithDocList>() {
		//
		// @Override
		// public void onSuccess(DocTypeWithDocList result) {
		// SplashDialog.hideSplash();
		// documentHistoryListGrid.setDocListAndType(result);
		// detailTabPane.setDocType(result.getDocType());
		// }
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// SplashDialog.hideSplash();
		// System.out.println(caught.getMessage());
		//
		// }
		// });

	}
}
