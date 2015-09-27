package com.docflow.client.components.gasmonitor;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;

import com.common.client.map.GisMapEvent;
import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.components.map.AGMapPanel;
import com.docflow.client.components.map.MeterChartData;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

public class PMonitor extends AGMapPanel {

	private PMonitorPlace monitorPlace;
	private TreeMap<Integer, PupupClass> popups;

	private static final String MONITOR_PLACE_WIDTH = "MONITOR_PLACE_WIDTH";
	private Date expireTime;

	public static PMonitor instance;

	public PMonitor() {
		super();
		instance = this;
		popups = new TreeMap<Integer, PupupClass>();
		expireTime = new Date();
		long time = expireTime.getTime();
		time = time + (1000 * 60 * 60 * 24 * 14);// seven days
		expireTime.setTime(time);
		mapViewerPanel.setShowResizeBar(true);
		monitorPlace = new PMonitorPlace();
		setMonitorPlaceWidth();
		this.addMember(monitorPlace);
		mapViewerPanel.addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				setCookiesPlaceWidth(mapViewerPanel.getWidthAsString());
			}
		});
		init(monitorPlace);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				if (response == null
						|| response.getData().length < 1
						|| response.getData()[0].getAttribute("feature_text") == null)
					return;
				createPopups(response.getData()[0].getAttribute("feature_text"));

			}
		};
		Map<String, Object> criteria = new TreeMap<String, Object>();
		if (DocFlow.user_obj.getUser().getRegionid() > 0) {
			if (DocFlow.user_obj.getUser().getRegionid() > 0) {
				if (DocFlow.user_obj.getUser().getSubregionid() > 0)
					criteria.put("subregionid", DocFlow.user_obj.getUser()
							.getSubregionid());
			} else
				criteria.put("regionid", DocFlow.user_obj.getUser()
						.getRegionid());
		}

		ClientUtils.fetchData(criteria, cb, "BuildingsDS", "getMetterInfos");

		DSCallback cb1 = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {

				MeterChartData.setTypes(response.getData());
				monitorPlace.init();
			}
		};
		ClientUtils.fetchData(criteria, cb1, "MeterDeviceResultDS",
				"getAllDataTypes");

	}

	protected void createPopups(String feature_text) {
		String[] data = feature_text.split(" - ");
		WKT kml = new WKT();
		for (String d : data) {
			String[] d1 = d.split(";");
			String wkt = d1[3];

			VectorFeature feature = kml.read(wkt)[0];
			LonLat center = feature.getCenterLonLat();
			int id = Integer.parseInt(d1[0]);
			String html = "<b>"
					+ d1[2]
					+ "<br>1000</> <a href=\"#\" onclick=\"callFunction('"
					+ d1[0]
					+ "')\"> <img src=\"images/monitor/add.png\" alt=\"Smiley face\"  /> </a>";
			Popup p = new FramedCloud(d1[0] + "", center, new Size(300, 50),
					html, null, false);
			map.addPopup(p);

			popups.put(id, new PupupClass(p, id, center));
		}

	}

	public void locateMeter(int id) {
		PupupClass p = popups.get(id);
		if (p != null)
			map.setCenter(p.getCenter());
	}

	public static native void init(Object pointController)/*-{
															$wnd.controller = function(id) {
															pointController.@com.docflow.client.components.gasmonitor.PMonitorPlace::addMeterChart(Ljava/lang/String;)(id);
															};
															}-*/;

	private void setMonitorPlaceWidth() {
		mapViewerPanel.setWidth(getCookiesPlaceWidth());
	}

	@Override
	public void onHandle(GisMapEvent eventObject) {
		// TODO Auto-generated method stub

	}

	private String getCookiesPlaceWidth() {
		return "60%";
		// String monitor_place_width = GWT.getModuleName() +
		// MONITOR_PLACE_WIDTH;
		// monitor_place_width = Cookies.getCookie(monitor_place_width);
		// try {
		// return Integer.parseInt(monitor_place_width) + "";
		// } catch (Exception e) {
		// return "60%";
		// }
	}

	private void setCookiesPlaceWidth(String width) {
		// String monitor_place_width = GWT.getModuleName() +
		// MONITOR_PLACE_WIDTH;
		// Cookies.setCookie(monitor_place_width, width, expireTime);
	}

	@Override
	protected void addOtherButtons() {

	}

	@Override
	public void creationComplete() {
		super.creationComplete();
		map.removeLayer(buildingsWMSLayer);
		map.removeLayer(buildings_searchWMSLayer);
	}

	public PMonitorPlace getMonitorPlace() {
		return monitorPlace;
	}

	public org.gwtopenmaps.openlayers.client.Map getMap() {
		return map;
	}
}
