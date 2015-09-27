package com.docflow.client.components.gasmonitor;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.map.AGMapPanel;
import com.docflow.client.components.map.MeterChartData;
import com.docflow.shared.Calendar;
import com.docflow.shared.ClSelection;
import com.docflow.shared.GasMonitorTemplateItem;
import com.docflow.shared.GregorianCalendar;
import com.docflow.shared.IMonitorChartType;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ChartType;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.chart.FacetChart;
import com.smartgwt.client.widgets.cube.Facet;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PMonitorChart extends VLayout {

	private ToolStripButton tsbRemove;
	private ToolStripButton tsbLocate;

	public FacetChart chart;

	private GasMonitorTemplateItem chatTemp;
	private Bounds bounds;

	private SelectItem siSubregion;
	private SelectItem siMeters;

	private Date starttime;
	private DataSource dataSource;
	private Integer meter_device_id;
	private String types;
	private int dayType;

	public PMonitorChart(GasMonitorTemplateItem chatTemp, Date starttime,
			int dayType, String types) {
		dataSource = DocFlow.getDataSource("MeterDeviceResultDS");
		this.chatTemp = chatTemp;
		this.dayType = dayType;
		this.starttime = starttime;
		this.types = types;
		ToolStrip stMain = new ToolStrip();
		stMain.setWidth100();
		stMain.setHeight(24);
		this.addMember(stMain);
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();

				ttsbuttonClick(objectSource);

			}

		};

		createChart();
		tsbRemove = AGMapPanel
				.createTSButton("[SKIN]/actions/remove.png", tsbStateHandler,
						SelectionType.BUTTON, "Remove", "main", stMain);
		// if (chatTemp.getType() == IMonitorChartType.METER)
		tsbLocate = AGMapPanel
				.createTSButton("monitor/locate.png", tsbStateHandler,
						SelectionType.BUTTON, "Locate", "main", stMain);

		if (chatTemp.getType() != IMonitorChartType.METER) {
			siMeters = new SelectItem("siMeters", "Metter");
			Map<String, Object> subregCrit = new TreeMap<String, Object>();
			subregCrit.put("subregionid", chatTemp.getId());
			if (chatTemp.getType() == IMonitorChartType.REGION) {
				siSubregion = new SelectItem("siSubregion", "Subregion");
				stMain.addFormItem(siSubregion);
				Map<String, Object> regionCrit = new TreeMap<String, Object>();
				regionCrit.put("parentId", chatTemp.getId());
				ClientUtils.fillSelectionCombo(siSubregion,
						ClSelection.T_SUBREGION, regionCrit);
				subregCrit.put("subregionid", -1);
				ClientUtils.makeDependancy(siSubregion, true,
						new FormItemDescr(siMeters, "subregionid"));
				siSubregion.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						meter_device_id = null;

					}
				});
			}
			stMain.addFormItem(siMeters);
			ClientUtils.fillCombo(siMeters, "BuildingsDS", "getMetters",
					"buid", "feature_text", subregCrit);
			siMeters.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					try {
						setMetterValues(Integer.parseInt(event.getValue()
								.toString()));

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

		}

		this.setHeight("500");
		this.setWidth100();
		setShowEdges(true);
		if (chatTemp.getType() == IMonitorChartType.METER) {
			setMetterValues(chatTemp.getId());
		}
		Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("tp", chatTemp.getType());
		criteria.put("id", chatTemp.getId());
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				if (response == null || response.getData().length < 1)
					return;
				setValues(response.getData()[0]);

			}
		};
		ClientUtils
				.fetchData(criteria, cb, "BuildingsDS", "getObjectPositions");

	}

	public void setStarttime(Date starttime, String types, int dayType) {
		this.starttime = starttime;
		this.types = types;
		this.dayType = dayType;
		if (meter_device_id != null)
			setMetterValues(meter_device_id);
	}

	private void setMetterValues(int meter_device_id) {
		this.meter_device_id = meter_device_id;
		DateTimeFormat df = DateTimeFormat.getFormat("yyyy-MM-dd");
		Criteria criteria = new Criteria();

		long starttime = this.starttime.getTime();
		Calendar cal1 = new GregorianCalendar();
		cal1.setTimeInMillis(starttime);
		cal1.add(Calendar.DAY_OF_MONTH, 1);
		long endtime = cal1.getTimeInMillis();
		setChartType(null);
		if (dayType != IMonitorChartType.DT_HOUR) {
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(starttime);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			starttime = cal.getTimeInMillis();
			cal.set(Calendar.DAY_OF_MONTH, cal.getLastDayOfMonth());
			endtime = cal.getTimeInMillis();
		}

		criteria.setAttribute("startdate", df.format(new Date(starttime)));
		criteria.setAttribute("enddate", df.format(new Date(endtime)));
		criteria.setAttribute("meter_device_id", meter_device_id);
		criteria.setAttribute("types", types);
		dataSource.fetchData(criteria, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				setValues(response.getData());

			}

		});

	}

	private void setValues(Record[] data) {
		if (data == null)
			data = new Record[] {};
		chart.setData(MeterChartData.getData(dayType, data));

	}

	protected void setValues(Record record) {
		chart.setTitle(record.getAttribute("senobis_no"));
		WKT kml = new WKT();
		String wkt = record.getAttribute("feature_text");
		VectorFeature feature = kml.read(wkt)[0];
		bounds = feature.getGeometry().getBounds();
	}

	protected void ttsbuttonClick(Object objectSource) {
		if (tsbLocate != null && objectSource.equals(tsbLocate)
				&& bounds != null) {
			if (chatTemp.getType() == IMonitorChartType.METER)
				PMonitor.instance.getMap().setCenter(bounds.getCenterLonLat());
			else
				PMonitor.instance.getMap().zoomToExtent(bounds);
		}
		if (tsbRemove != null && objectSource.equals(tsbRemove))
			PMonitor.instance.getMonitorPlace().removeChart(chatTemp, true,
					true);

	}

	private void createChart() {
		if (SC.hasCharts()) {
			if (SC.hasDrawing()) {
				chart = new FacetChart();
				chart.setData(new MeterChartData[] {});
				chart.setFacets(new Facet("data_date", "Date"), new Facet(
						"type", "Type"));
				chart.setValueProperty("value");
				chart.setChartType(ChartType.AREA);
				this.addMember(chart);

			} else {
				HTMLFlow htmlFlow = new HTMLFlow(
						"<div class='explorerCheckErrorMessage'><p>This example is disabled in this SDK because it requires the optional "
								+ "<a href=\"http://www.smartclient.com/product/index.jsp#drawing\" target=\"_blank\">Drawing module</a>.</p>"
								+ "<p>Click <a href=\"http://www.smartclient.com/smartgwtee/showcase/#simpleChart\" target=\"\">here</a> to see this example on smartclient.com</p></div>");
				htmlFlow.setWidth100();
				this.addMember(htmlFlow);
			}
		} else {
			HTMLFlow htmlFlow = new HTMLFlow(
					"<div class='explorerCheckErrorMessage'><p>This example is disabled in this SDK because it requires the optional "
							+ "<a href=\"http://www.smartclient.com/product/index.jsp#analytics\" target=\"_blank\">Analytics module</a>.</p>"
							+ "<p>Click <a href=\"http://www.smartclient.com/smartgwtee/showcase/#simpleChart\" target=\"\">here</a> to see this example on smartclient.com</p></div>");
			htmlFlow.setWidth100();
			this.addMember(htmlFlow);
		}

	}

	public void locate() {
		// TODO Auto-generated method stub

	}

	public GasMonitorTemplateItem getChatTemp() {
		return chatTemp;
	}

	ChartType lastchartType = ChartType.AREA;

	public void setChartType(ChartType chartType) {
		if (chartType == null)
			chartType = lastchartType;
		if (chartType == null)
			chartType = ChartType.AREA;
		lastchartType = chartType;
		if (dayType == IMonitorChartType.DT_MONTH)
			chart.setChartType(ChartType.BAR);
		else
			chart.setChartType(chartType);
	}

	public void setDate(Date starttime) {

	}

}
