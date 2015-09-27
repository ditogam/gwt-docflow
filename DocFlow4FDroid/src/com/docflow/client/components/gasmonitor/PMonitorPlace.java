package com.docflow.client.components.gasmonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.components.map.AGMapPanel;
import com.docflow.client.components.map.MeterChartData;
import com.docflow.shared.GasMonitorTemplateItem;
import com.docflow.shared.IMonitorChartType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.ChartType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PMonitorPlace extends VLayout {

	private static final String MONITOR_PLACE_CHARTS = "MONITOR_PLACE_CHARTS";

	private ToolStripButton tsbPlus;
	private ToolStripButton tsbClearAll;
	private ToolStripButton tsbSearch;
	private ToolStripButton tsbSave;
	private ToolStripButton tsbSaveAs;
	private TreeMap<String, PMonitorChart> charts;
	private SelectItem cbTemplates;
	private DateItem diStarttime;
	private VLayout chartsPanel;
	private SelectItem siDataTypes;
	private SelectItem siDayTypes;

	private Date expireTime;

	public PMonitorPlace() {
		expireTime = new Date();
		long time = expireTime.getTime();
		time = time + (1000 * 60 * 60 * 24 * 14);// seven days
		expireTime.setTime(time);
		charts = new TreeMap<String, PMonitorChart>();
		ToolStrip stMain = new ToolStrip();
		stMain.setWidth100();
		stMain.setHeight(24);
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();

				ttsbuttonClick(objectSource);

			}

		};
		cbTemplates = new SelectItem("cbTemplates", "Templates");
		cbTemplates.setTooltip("Templates");
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		map.put("user_id", DocFlow.user_id);
		ClientUtils.fillCombo(cbTemplates, "GasMonitorTemplateDS", null,
				"mtemplate_id", "mtemplate_name", map);
		stMain.addFormItem(cbTemplates);
		cbTemplates.setWidth(200);
		cbTemplates.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				setTemplate();

			}
		});
		tsbSave = AGMapPanel.createTSButton("monitor/save.png",
				tsbStateHandler, SelectionType.BUTTON, "Save", "main", stMain);
		tsbSaveAs = AGMapPanel.createTSButton("monitor/saveas.png",
				tsbStateHandler, SelectionType.BUTTON, "Save AS", "main",
				stMain);
		stMain.addSeparator();
		tsbPlus = AGMapPanel.createTSButton("[SKIN]/actions/add.png",
				tsbStateHandler, SelectionType.BUTTON, "Add", "main", stMain);
		tsbClearAll = AGMapPanel.createTSButton("monitor/clear.png",
				tsbStateHandler, SelectionType.BUTTON, "Clear all", "main",
				stMain);

		ToolStrip stAdditional = new ToolStrip();
		diStarttime = new DateItem("diStarttime", "Start Time");
		diStarttime.setTooltip("Starttime");
		diStarttime.setUseTextField(false);
		diStarttime.setWrapTitle(false);
		diStarttime.setValue(new Date());
		stAdditional.addFormItem(diStarttime);
		final SelectItem chartType = new SelectItem("chartType", "Chart Type");

		chartType.setValueMap(ChartType.AREA.getValue(),
				ChartType.BAR.getValue(), ChartType.COLUMN.getValue(),
				ChartType.DOUGHNUT.getValue(), ChartType.LINE.getValue(),
				ChartType.PIE.getValue(), ChartType.RADAR.getValue());
		chartType.setDefaultToFirstOption(true);
		chartType.setWrapTitle(false);

		chartType.addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				String selectedChartType = chartType.getValueAsString();
				ChartType chartTp = null;
				if (ChartType.AREA.getValue().equals(selectedChartType)) {
					chartTp = (ChartType.AREA);
				} else if (ChartType.BAR.getValue().equals(selectedChartType)) {
					chartTp = (ChartType.BAR);
				} else if (ChartType.COLUMN.getValue()
						.equals(selectedChartType)) {
					chartTp = (ChartType.COLUMN);
				} else if (ChartType.DOUGHNUT.getValue().equals(
						selectedChartType)) {
					chartTp = (ChartType.DOUGHNUT);
				} else if (ChartType.LINE.getValue().equals(selectedChartType)) {
					chartTp = (ChartType.LINE);
				} else if (ChartType.PIE.getValue().equals(selectedChartType)) {
					chartTp = (ChartType.PIE);
				} else if (ChartType.RADAR.getValue().equals(selectedChartType)) {
					chartTp = (ChartType.RADAR);
				}
				setChartType(chartTp);
			}
		});
		stAdditional.addFormItem(chartType);
		this.addMember(stMain);
		stMain = new ToolStrip();
		this.addMember(stAdditional);
		this.addMember(stMain);

		siDayTypes = new SelectItem("siDayTypes", "M/D/H");
		ClientUtils.fillCombo(siDayTypes, "MeterDeviceResultDS",
				"getDataTypes", "id", "values");
		Integer dayType = IMonitorChartType.DT_HOUR;
		siDayTypes.setValue(dayType);

		siDataTypes = new SelectItem("siDataTypes", "Type");
		siDataTypes.setWidth(500);
		siDayTypes.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				Integer type = -1;
				try {
					type = Integer.parseInt(event.getValue().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
				MeterChartData.setComboValues(type, siDataTypes);

			}
		});
		stMain.addFormItem(siDayTypes);
		stMain = new ToolStrip();
		this.addMember(stMain);
		stMain.addFormItem(siDataTypes);
		tsbSearch = AGMapPanel
				.createTSButton("[SKIN]/actions/search.png", tsbStateHandler,
						SelectionType.BUTTON, "Search", "main", stMain);
		MeterChartData.setComboValues(dayType, siDataTypes);

		chartsPanel = new VLayout();
		chartsPanel.setHeight100();
		chartsPanel.setWidth100();
		chartsPanel.setOverflow(Overflow.AUTO);
		this.addMember(chartsPanel);

	}

	protected void setTemplate() {
		clearAllCharts();
		if (cbTemplates.getSelectedRecord() == null)
			return;
		String value = cbTemplates.getSelectedRecord().getAttribute(
				"mtemplatedata");
		createCharts(value);
	}

	public void init() {
		MeterChartData.setComboValues(IMonitorChartType.DT_HOUR, siDataTypes);
		createCharts();
	}

	protected void setChartType(ChartType chartType) {
		if (chartType == null)
			return;
		Collection<PMonitorChart> charts = this.charts.values();
		for (PMonitorChart pMonitorChart : charts) {

			pMonitorChart.setChartType(chartType);
		}

	}

	private void createCharts() {
		String monitor_charts = GWT.getModuleName() + MONITOR_PLACE_CHARTS;
		monitor_charts = Cookies.getCookie(monitor_charts);
		if (monitor_charts == null)
			createDefaultCharts();
		else
			createCharts(monitor_charts);
	}

	private void createCharts(String monitor_charts) {
		try {
			ArrayList<GasMonitorTemplateItem> chatTemps = GasMonitorTemplateItem
					.createFromString(monitor_charts);
			clearAllCharts();
			for (GasMonitorTemplateItem chatTemp : chatTemps) {
				addChart(chatTemp, false);
			}
			saveCharts();
		} catch (Exception e) {
			createDefaultCharts();
		}

	}

	private void addChart(GasMonitorTemplateItem chatTemp, boolean save) {
		addChart(chatTemp, save, false);
	}

	public void addMeterChart(String id) {
		GasMonitorTemplateItem g = new GasMonitorTemplateItem(
				IMonitorChartType.METER, Integer.parseInt(id));
		addChart(g, true, true);
		PMonitorChart pcht = charts.get(g.getValue());
		if (pcht != null) {
			chartsPanel.scrollToBottom();
			pcht.chart.focus();
		}
	}

	public void removeChart(final GasMonitorTemplateItem chatTemp,
			final boolean save, boolean ask) {
		PMonitorChart pcht = charts.get(chatTemp.getValue());
		if (pcht != null) {
			if (ask) {
				SC.ask("Do you want to remove chart?", new BooleanCallback() {

					@Override
					public void execute(Boolean value) {
						if (value)
							removeChart(chatTemp, save, false);

					}
				});
				return;
			}
			charts.remove(chatTemp.getValue());
			pcht.removeFromParent();
			pcht.destroy();
			if (save)
				saveCharts();
		}
	}

	public void addChart(GasMonitorTemplateItem chatTemp, boolean save,
			boolean alertIfExists) {
		String id = chatTemp.getValue();
		PMonitorChart pcht = charts.get(id);
		if (pcht != null) {
			if (alertIfExists) {
				SC.say("This kind of chart already exists!!!");
				pcht.locate();
			}
			return;
		}
		String val = siDataTypes.getValueAsString();
		Integer daytype = Integer.parseInt(siDayTypes.getValue().toString()
				.trim());
		pcht = new PMonitorChart(chatTemp, diStarttime.getValueAsDate(),
				daytype, val);
		charts.put(id, pcht);
		chartsPanel.addMember(pcht);

		if (save) {
			saveCharts();

		}
	}

	private void saveCharts(boolean warn) {
		String vals = getTemplateData();
		String monitor_charts = GWT.getModuleName() + MONITOR_PLACE_CHARTS;
		if (vals.length() == 0)
			Cookies.removeCookie(monitor_charts);
		else
			Cookies.setCookie(monitor_charts, vals, expireTime);
	}

	public String getTemplateData() {
		String vals = "";
		Set<String> keys = charts.keySet();
		for (String key : keys) {
			if (vals.length() > 0)
				vals += ";";
			vals += charts.get(key).getChatTemp().getValue();
		}
		return vals;
	}

	private void saveCharts() {
		saveCharts(false);
	}

	private void createDefaultCharts() {
		// if (DocFlow.user_obj.getUser().getRegionid() > 0) {
		// if (DocFlow.user_obj.getUser().getRegionid() > 0) {
		// if (DocFlow.user_obj.getUser().getSubregionid() > 0)
		// addChart(new GasMonitorTemplateItem(
		// IMonitorChartType.SUB_REGION, DocFlow.user_obj
		// .getUser().getSubregionid()), false);
		// } else
		// addChart(new GasMonitorTemplateItem(IMonitorChartType.REGION,
		// DocFlow.user_obj.getUser().getRegionid()), false);
		// saveCharts();
		// } else {
		// DSCallback cb = new DSCallback() {
		//
		// @Override
		// public void execute(DSResponse response, Object rawData,
		// DSRequest request) {
		// Record[] records = response.getData();
		// if (records == null || records.length == 0)
		// return;
		// for (Record record : records) {
		// addChart(
		// new GasMonitorTemplateItem(
		// IMonitorChartType.REGION,
		// record.getAttributeAsInt("id")), false);
		// }
		// saveCharts();
		// }
		// };
		// ClientUtils.fetchData(new TreeMap<String, Object>(), cb,
		// ClSelection.getTypeNameDS(ClSelection.T_REGION),
		// "fetchSelections");
		// }
	}

	protected void ttsbuttonClick(Object objectSource) {
		if (tsbClearAll != null && objectSource.equals(tsbClearAll))
			clearAll();
		if (tsbPlus != null && objectSource.equals(tsbPlus))
			WAddChartDialog.showDialog();

		if (tsbSave != null && objectSource.equals(tsbSave))
			saveTemplateCharts();
		if (tsbSaveAs != null && objectSource.equals(tsbSaveAs))
			saveTemplateChartsAs();

		if (tsbSearch != null && objectSource.equals(tsbSearch))
			setSearch();

	}

	private void saveTemplateChartsAs() {
		String val = getTemplateData();
		if (val == null || val.trim().length() == 0)
			return;
		new WSaveTemplateDialog(val, cbTemplates);

	}

	private void saveTemplateCharts() {
		String val = getTemplateData();
		if (val == null || val.trim().length() == 0)
			return;
		ListGridRecord rec = cbTemplates.getSelectedRecord();
		if (rec == null)
			return;
		rec.setAttribute("mtemplatedata", val);
		try {
			DocFlow.getDataSource("GasMonitorTemplateDS").updateData(rec,
					new DSCallback() {

						@Override
						public void execute(DSResponse response,
								Object rawData, DSRequest request) {
							DSRequest req = new DSRequest();
							req.setAttribute("_UUUUUUUIDUUU",
									HTMLPanel.createUniqueId());
							cbTemplates.fetchData(new DSCallback() {

								@Override
								public void execute(DSResponse response,
										Object rawData, DSRequest request) {
									// TODO Auto-generated method stub

								}
							}, req);
							if (response.getData() != null
									&& response.getData().length != 0) {
								try {
									int id = Integer.parseInt(response
											.getData()[0].getAttribute(
											"mtemplate_id").trim());
									cbTemplates.setValue(id);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					});

		} catch (Exception e) {
			SC.warn(e.getMessage());
		}

	}

	private void setSearch() {
		Collection<PMonitorChart> vals = charts.values();
		PMonitorChart[] arr = vals.toArray(new PMonitorChart[] {});
		for (PMonitorChart pMonitorChart : arr) {
			String val = siDataTypes.getValueAsString();
			Integer daytype = Integer.parseInt(siDayTypes.getValue().toString()
					.trim());
			pMonitorChart.setStarttime(diStarttime.getValueAsDate(), val,
					daytype);

		}

	}

	private void clearAll() {
		SC.ask("Do you want to remove all chart?", new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (!value)
					return;
				clearAllCharts();
			}
		});

	}

	public void clearAllCharts() {
		Collection<PMonitorChart> vals = charts.values();
		PMonitorChart[] arr = vals.toArray(new PMonitorChart[] {});
		for (PMonitorChart pMonitorChart : arr) {
			removeChart(pMonitorChart.getChatTemp(), false, false);
		}
	}
}
