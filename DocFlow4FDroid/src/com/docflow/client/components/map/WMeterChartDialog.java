package com.docflow.client.components.map;

import com.docflow.client.components.SavePanel;
import com.docflow.shared.MapObjectTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ChartType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.chart.FacetChart;
import com.smartgwt.client.widgets.cube.Facet;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class WMeterChartDialog extends Window {

	@SuppressWarnings("unused")
	private Record record;
	private VLayout vlChart;
	private int map_object_type;
	private DynamicForm dfMapObject;

	public WMeterChartDialog(Record record) {

		this.record = record;
		map_object_type = record.getAttributeAsInt("map_object_type");
		VLayout vlMain = new VLayout();

		dfMapObject = createForm(MapObjectTypes.MO_DISTRICT_METER_TYPE, record);
		if (dfMapObject != null)
			vlMain.addMember(dfMapObject);

		vlChart = new VLayout();
		createChart();

		int h = 500;
		vlChart.setHeight("98%");
		vlChart.setWidth100();

		vlMain.setHeight100();
		vlMain.setWidth100();

		vlMain.addMember(vlChart);
		this.addItem(vlMain);

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
		vlMain.addMember(savePanel);

		this.setHeight(h);
		this.setWidth(700);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();
		this.setTitle("გრაფიკი");
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});

	}

	private void createChart() {
		if (SC.hasCharts()) {
			if (SC.hasDrawing()) {
				final FacetChart chart = new FacetChart();
				chart.setData(new Record[] {});
				chart.setFacets(new Facet("region", "Region"), new Facet(
						"product", "Product"));
				chart.setValueProperty("sales");
				chart.setChartType(ChartType.AREA);
				chart.setTitle("Sales by Product and Region");

				final DynamicForm chartSelector = new DynamicForm();
				final SelectItem chartType = new SelectItem("chartType",
						"Chart Type");
				chartType.setValueMap(ChartType.AREA.getValue(),
						ChartType.BAR.getValue(), ChartType.COLUMN.getValue(),
						ChartType.DOUGHNUT.getValue(),
						ChartType.LINE.getValue(), ChartType.PIE.getValue(),
						ChartType.RADAR.getValue());
				chartType.setDefaultToFirstOption(true);

				chartType.addChangedHandler(new ChangedHandler() {
					public void onChanged(ChangedEvent event) {
						String selectedChartType = chartType.getValueAsString();
						if (ChartType.AREA.getValue().equals(selectedChartType)) {
							chart.setChartType(ChartType.AREA);
						} else if (ChartType.BAR.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.BAR);
						} else if (ChartType.COLUMN.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.COLUMN);
						} else if (ChartType.DOUGHNUT.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.DOUGHNUT);
						} else if (ChartType.LINE.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.LINE);
						} else if (ChartType.PIE.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.PIE);
						} else if (ChartType.RADAR.getValue().equals(
								selectedChartType)) {
							chart.setChartType(ChartType.RADAR);
						}
					}
				});
				chartSelector.setFields(chartType);

				vlChart.addMember(chartSelector);
				vlChart.addMember(chart);

			} else {
				HTMLFlow htmlFlow = new HTMLFlow(
						"<div class='explorerCheckErrorMessage'><p>This example is disabled in this SDK because it requires the optional "
								+ "<a href=\"http://www.smartclient.com/product/index.jsp#drawing\" target=\"_blank\">Drawing module</a>.</p>"
								+ "<p>Click <a href=\"http://www.smartclient.com/smartgwtee/showcase/#simpleChart\" target=\"\">here</a> to see this example on smartclient.com</p></div>");
				htmlFlow.setWidth100();
				vlChart.addMember(htmlFlow);
			}
		} else {
			HTMLFlow htmlFlow = new HTMLFlow(
					"<div class='explorerCheckErrorMessage'><p>This example is disabled in this SDK because it requires the optional "
							+ "<a href=\"http://www.smartclient.com/product/index.jsp#analytics\" target=\"_blank\">Analytics module</a>.</p>"
							+ "<p>Click <a href=\"http://www.smartclient.com/smartgwtee/showcase/#simpleChart\" target=\"\">here</a> to see this example on smartclient.com</p></div>");
			htmlFlow.setWidth100();
			vlChart.addMember(htmlFlow);
		}

	}

	private DynamicForm createForm(int type, Record rec) {
		switch (type) {
		case MapObjectTypes.MO_BUILDING_TYPE:
			return new PBuildingsForm(rec, null, true);
		case MapObjectTypes.MO_DISTRICT_METER_TYPE:
			return new PDistrictMeterForm(rec, true, 2);

		default:
			return null;
		}
	}

	protected void saveData() {
	}

}
