package com.docflow.client.components.map;

import java.util.Date;

import com.docflow.client.DocFlow;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.GregorianCalendar;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.calendar.CalendarEvent;
import com.smartgwt.client.widgets.calendar.events.DateChangedEvent;
import com.smartgwt.client.widgets.calendar.events.DateChangedHandler;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;

public class WCalendarDialog extends Window {

	// private static WCalendarDialog dialog = null;

	public static void showWindow(Criteria criteria, WDemagesDialog dDialog) {
		WCalendarDialog dialog = new WCalendarDialog(dDialog);
		dialog.criteria = criteria;
		Date dt = criteria.getAttributeAsDate("demage_time");
		dt = dt == null ? new Date() : dt;
		dialog.calendar.setChosenDate(dt);
		dialog.show();
	}

	private Criteria criteria;
	private Calendar calendar;

	private DataSource dsCalendar;
	private WDemagesDialog dDialog;

	public WCalendarDialog(WDemagesDialog dDialog) {
		this.setTitle("თარიღის არჩევა");
		this.dDialog = dDialog;
		dsCalendar = DocFlow.getDataSource("Demage_DescriptionDS");

		calendar = new Calendar() {
			@Override
			protected String getDayBodyHTML(Date date, CalendarEvent[] events,
					Calendar calendar, int rowNum, int colNum) {
				String returnStr = date.getDate() + "";
				if (events != null && events.length > 0) {
					for (CalendarEvent ce : events) {
						returnStr += "<br><font size=\"5\" color=\"red\"><b><p style=\"text-indent: 2em;\">"
								+ ce.getAttributeAsString("cnt") + "";
					}

				}
				return returnStr;
			}
		};
		calendar.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				WCalendarDialog.this.dDialog.setTime(calendar.getActiveTime());
				WCalendarDialog.this.destroy();

			}
		});
		calendar.setWidth100();
		calendar.setHeight100();

		calendar.setShowDayView(false);
		calendar.setShowWeekView(false);
		calendar.setShowOtherDays(false);
		calendar.setShowDayHeaders(false);
		calendar.setShowDatePickerButton(false);
		calendar.setShowAddEventButton(false);
		calendar.setDisableWeekends(false);
		calendar.setShowDateChooser(false);
		calendar.setCanCreateEvents(false);
		calendar.setStartDateField("demage_time");
		calendar.setEndDateField("demage_time");
		calendar.setDescriptionField("cnt");
		// calendar.setFetchOperation("getCalendar");

		// calendar.setDataSource(dsCalendar);
		// calendar.setAutoFetchData(true);
		calendar.addDateChangedHandler(new DateChangedHandler() {

			@Override
			public void onDateChanged(DateChangedEvent event) {
				refreshDate();

			}
		});
		this.addItem(calendar);

		SavePanel savePanel = new SavePanel("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}

		});
		// this.addItem(savePanel);
		this.setHeight(700);
		this.setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
	}

	protected void saveData() {

	}

	private void refreshDate() {
		long time = calendar.getChosenDate().getTime();
		GregorianCalendar start = new GregorianCalendar();
		start.setTimeInMillis(time);
		start.set(GregorianCalendar.DATE, 1);
		GregorianCalendar end = new GregorianCalendar();
		end.setTimeInMillis(start.getTimeInMillis());
		end.add(GregorianCalendar.MONTH, 1);
		end.set(GregorianCalendar.DATE, 1);
		end.add(GregorianCalendar.DATE, -1);
		criteria = criteria == null ? new Criteria() : criteria;
		criteria.setAttribute("demage_time_start", start.getTime());
		criteria.setAttribute("demage_time_end", end.getTime());
		DSRequest req = new DSRequest();
		req.setOperationId("getCalendar");
		dsCalendar.fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				calendar.setData(response.getData());

			}
		}, req);
	}

}
