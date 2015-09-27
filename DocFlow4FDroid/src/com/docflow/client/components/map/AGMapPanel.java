package com.docflow.client.components.map;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;

import com.common.client.map.GisMapEventHandler;
import com.common.client.map.MapViewerPanel;
import com.common.shared.map.GisMap;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.SplashDialog;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public abstract class AGMapPanel extends HLayout implements GisMapEventHandler {

	public static final String FEATURE_ID_NAME = "gid__";
	public static final String FEATURE_TYPE_NAME = "ft__";

	public static final int MIN_FEATURE_SELECT_LEVEL = 9;

	public GoogleV3 googleLayer;

	protected ToolStripButton tsbSwitchNormal;
	private ToolStripButton tsbGoogleEarth;

	protected MapViewerPanel mapViewerPanel;
	protected Map map;

	private GisMap gisMap;

	protected ArrayList<WMS> wmsLayers;
	protected WMS baseLayer;
	protected WMS buildingsWMSLayer;
	protected WMS buildings_searchWMSLayer;

	public AGMapPanel() {
		wmsLayers = new ArrayList<WMS>();
		gisMap = createGisMap();
		gisMap.setHeight("" + (DocFlow.panelheight - 35));
		final MapViewerPanel vl = new MapViewerPanel(gisMap, true);
		mapViewerPanel = vl;
		this.addMember(mapViewerPanel);

		mapViewerPanel.addGisMapEventHandler(this);
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();

				ttsbuttonClick(objectSource);

			}

		};

		tsbSwitchNormal = createTSButton("map/pan.png", tsbStateHandler,
				SelectionType.RADIO, "Navigation", "main_selection",
				vl.getToolStrip());
		vl.getToolStrip().addSeparator();
		tsbSwitchNormal.setSelected(true);
		addOtherButtons();
		// if (DocFlow.hasPermition("CAN_SET_FEATURE_INFO") )

		vl.getToolStrip().addSeparator();

		tsbGoogleEarth = new ToolStripButton();
		tsbGoogleEarth.setIcon("map/google_earth.png");
		tsbGoogleEarth.addClickHandler(tsbStateHandler);
		tsbGoogleEarth.setTooltip("Google Earth");
		tsbGoogleEarth.setActionType(SelectionType.CHECKBOX);
		vl.getToolStrip().addButton(tsbGoogleEarth);

		GoogleV3Options gmOptions = new GoogleV3Options();
		gmOptions.setIsBaseLayer(false);
		gmOptions.setSphericalMercator(true);
		gmOptions.getJSObject().setProperty("numZoomLevels", 35);
		gmOptions.setType(GoogleV3MapType.G_HYBRID_MAP);
		googleLayer = new GoogleV3("Google Earth", gmOptions);
		googleLayer.setIsBaseLayer(false);
		// googleLayer.setOpacity(0.5f);

	}

	protected abstract void addOtherButtons();

	public static ToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler,
			SelectionType actionType, String toolTip, String group_id,
			ToolStrip toolStrip) {
		ToolStripButton tsbButton = new ToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setRadioGroup(group_id);
		toolStrip.addButton(tsbButton);
		return tsbButton;
	}

	private void setGoogleEarth() {
		if (tsbGoogleEarth.isSelected()) {
			map.addLayer(googleLayer);
		} else {
			map.removeLayer(googleLayer);
		}
	}

	private GisMap createGisMap() {
		GisMap gm = DocFlow.user_obj.getMaps().get(0);
		return gm;

	}

	public void redrawWMS() {
		for (WMS wms : wmsLayers) {
			wms.redraw(true);
			// WMSParams params = new WMSParams();
			// Date dt = new Date();
			// params.setParameter("ddd", dt.getTime() + "");
			// ((WMS) layer).mergeNewParams(params);
			// break;
		}
	}

	@Override
	public void creationComplete() {
		map = mapViewerPanel.getMap();
		Layer[] layers = map.getLayers();
		for (Layer layer : layers) {
			String ln = layer.getClassName();
			if (!ln.endsWith(".WMS"))
				continue;
			WMS wms = WMS.narrowToWMS(layer.getJSObject());
			if (wms.isBaseLayer()) {
				baseLayer = wms;
			}
			if (wms.getName().equals("building_map")) {
				buildingsWMSLayer = wms;
			}
			if (wms.getName().equals("building_searched_map")) {
				buildings_searchWMSLayer = wms;
			}

			wmsLayers.add(wms);
		}

		mapViewerPanel.gethSlider().hide();
		map.addControl(new PanZoomBar());

		mapViewerPanel.addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				String sh = mapViewerPanel.getHeightAsString();
				int h = Integer.parseInt(sh) - 20;
				mapViewerPanel.getMapWidget().setHeight("" + h);
				mapViewerPanel.getMap().updateSize();
				System.out.println("ssssssssssssss");
			}
		});
		if (DocFlow.user_obj.getB_box() != null) {
			createMapBox(DocFlow.user_obj.getB_box());
		}

	}

	private void createMapBox(String attribute) {
		WKT kml = new WKT();
		VectorFeature[] features = kml.read(attribute);
		if (features == null || features.length != 1)
			return;

		Bounds bounds = features[0].getGeometry().getBounds();

		map.setRestrictedExtent(bounds);
		LonLat center = map.getCenter();
		if (!(center.lon() >= bounds.getLowerLeftX()
				&& center.lon() >= bounds.getLowerLeftY()
				&& center.lat() <= bounds.getLowerLeftX() && center.lat() <= bounds
				.getLowerLeftY()))
			map.setCenter(bounds.getCenterLonLat());
		SplashDialog.hideSplash();

	}

	protected void ttsbuttonClick(Object objectSource) {

		if (objectSource.equals(tsbGoogleEarth))
			setGoogleEarth();

	}
}
