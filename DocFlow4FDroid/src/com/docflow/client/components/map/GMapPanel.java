package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.FeatureHighlightedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.handler.PointHandler;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.popup.AnchoredBubble;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.gwtopenmaps.openlayers.client.util.Attributes;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.common.client.map.GisMapEvent;
import com.common.client.map.GisMapEventHandler;
import com.common.client.map.MapHelper;
import com.common.client.map.MapViewerPanel;
import com.common.shared.map.GisLayer;
import com.common.shared.map.GisMap;
import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.FormDefinitionPanel;
import com.docflow.client.components.common.SimpleFieldDefinitionListValue;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.MapObjectTypes;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.XMLParceserHelper;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class GMapPanel extends HLayout implements GisMapEventHandler {

	// public enum MapState {
	// Navigation, Info, Buildings, DistrictMeter, PipeLines
	// }

	public static GMapPanel instance;
	public static final String FEATURE_ID_NAME = "gid__";
	public static final String FEATURE_TYPE_NAME = "ft__";

	public static final int MIN_FEATURE_SELECT_LEVEL = 9;

	public static GoogleV3[] googleLayers;
	public GoogleV3 googleLayerAdded;
	public DataSource dsBuildings;
	public Vector buildingLayer;
	public DrawFeature drawFeature;
	private SelectFeature selectFeature;

	private MapToolStripButton tsbSwitchNormal;

	private MapToolStripButton tsbInfo;
	private MapToolStripButton tsbBuildings;
	private MapToolStripButton tsbDistrictMeters;
	private MapToolStripButton tsbSocarPipeLine;

	private MapToolStripButton tsbSelect;
	private MapToolStripButton tsbAdd;
	private MapToolStripButton tsbEdit;
	private MapToolStripButton tsbDelete;
	private MapToolStripButton tsbChart;

	private ToolStripButton tsbDemages;

	private ToolStripButton tsbSetParentMeter;

	private ToolStripButton tsbGoogleEarth;

	private ToolStripButton tsbSearch;
	private ToolStripButton tsbSearchPipeLine;

	public MapViewerPanel mapViewerPanel;
	private Map map;

	private GisMap gisMap;

	private Popup popup;

	private MapToolStripButton mapState;

	private ArrayList<WMS> wmsLayers;
	private WMS baseLayer;
	private WMS buildingsWMSLayer;
	private WMS buildings_searchWMSLayer;
	private WMS district_meterWMSLayer;
	private java.util.Map<String, Integer> mpCriteria;

	private TreeMap<MapToolStripButton, ArrayList<MapToolStripButton>> buttonsForState;
	private ArrayList<ToolStripButton> manipulateButtons;
	private ArrayList<MapToolStripButton> mapObjectButtons;
	private Menu layersMenu = null;
	private ToolStrip tsMain;

	public GMapPanel() {
		instance = this;
		init(this);
		wmsLayers = new ArrayList<WMS>();
		dsBuildings = DocFlow.getDataSource("BuildingsDS");
		gisMap = createGisMap();
		gisMap.setHeight("" + (DocFlow.panelheight - 35));
		final MapViewerPanel vl = new MapViewerPanel(gisMap, true);
		mapViewerPanel = vl;
		buildingLayer = new Vector("Buildings");
		buttonsForState = new TreeMap<MapToolStripButton, ArrayList<MapToolStripButton>>();
		this.addMember(mapViewerPanel);

		mapViewerPanel.addGisMapEventHandler(this);
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();

				ttsbuttonClick(objectSource);

			}

		};

		tsbSwitchNormal = createTSButton("map/pan.png", tsbStateHandler, SelectionType.RADIO, "Navigation",
				"main_selection", vl.getToolStrip(), null);
		vl.getToolStrip().addSeparator();
		tsbSwitchNormal.setSelected(true);
		// if (DocFlow.hasPermition("CAN_SET_FEATURE_INFO") )
		{

			tsbInfo = createTSButton("map/information.png", tsbStateHandler, SelectionType.RADIO, "Info",
					"main_selection", vl.getToolStrip(), null);

		}

		ArrayList<GisLayer> layers = gisMap.getGisLayers();

		mapObjectButtons = new ArrayList<MapToolStripButton>();
		mapObjectButtons.add(tsbSwitchNormal);
		mapObjectButtons.add(tsbInfo);
		for (GisLayer gl : layers) {
			if (gl.getForm_defination_xml() == null || gl.getForm_defination_xml().trim().isEmpty())
				continue;
			Timer.start("createLayerDocForms_global");
			createLayerDocForms(gl);
			Timer.step("createLayerDocForms_global");
			MapToolStripButton tsbManipulate = createTSButton(gl.getButton_icon(), tsbStateHandler,
					SelectionType.RADIO, gl.getButton_title(), "main_selection", vl.getToolStrip(), gl);
			tsbManipulate.setSelected(false);
			mapObjectButtons.add(tsbManipulate);

		}

		vl.getToolStrip().addSeparator();
		tsbSelect = createTSButton("map/pointer.png", tsbStateHandler, SelectionType.RADIO, "Select Objects",
				"selection", vl.getToolStrip());
		tsbSelect.setSelected(false);

		tsbAdd = createTSButton("[SKIN]/actions/add.png", tsbStateHandler, SelectionType.RADIO, "Add Object",
				"selection", vl.getToolStrip());
		tsbAdd.setSelected(false);

		tsbEdit = createTSButton("[SKIN]/actions/edit.png", tsbStateHandler, SelectionType.RADIO, "Edit Object",
				"selection", vl.getToolStrip());
		tsbEdit.setSelected(false);

		tsbDelete = createTSButton("[SKIN]/actions/remove.png", tsbStateHandler, SelectionType.RADIO, "Remove Object",
				"selection", vl.getToolStrip());
		tsbDelete.setSelected(false);

		// if (DocFlow.hasPermition("CAN_SET_FEATURE_INFO") )
		{

			tsbChart = createTSButton("map/chart.png", tsbStateHandler, SelectionType.RADIO, "Chart", "selection",
					vl.getToolStrip());

			tsbChart.setSelected(false);

		}

		// if (DocFlow.hasPermition("CAN_SET_FEATURE_INFO") )
		{
			vl.getToolStrip().addSeparator();
			tsbDemages = createTSButton("fire.png", tsbStateHandler, SelectionType.BUTTON, "Demages", "selection1",
					vl.getToolStrip());

			tsbDemages.setSelected(false);
			tsbDemages.setDisabled(false);

		}
//		boolean select_p=!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_SELECT_RESTRICT);
//		boolean addedit_p=!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_ADDEDIT_RESTRICT);
//		boolean del_p=!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_DELETE_RESTRICT);
		for (MapToolStripButton btn : mapObjectButtons) {
			if (btn.getLayer() == null)
				continue;
			GisLayer l = btn.getLayer();
			ArrayList<MapToolStripButton> buttons = new ArrayList<MapToolStripButton>();
			if (l.getLayername().equals("building_map") || l.getLayername().equals("district_meters_map")) {
//				if(select_p)
					buttons.add(tsbSelect);
			}
			if (l.getLayername().equals("building_map")) {
//				if(select_p)
					buttons.add(tsbSelect);
				tsbBuildings = btn;
			}
			if (l.getLayername().equals("socar_pipelines")) {
//				if(select_p)
					buttons.add(tsbSelect);
				tsbSocarPipeLine = btn;
			}
//			if(addedit_p)
			{
				buttons.add(tsbAdd);
				buttons.add(tsbEdit);
			}
			if (/*del_p &&*/ l.isCan_delete())
				buttons.add(tsbDelete);
			if (l.getLayername().equals("district_meters_map")) {
				buttons.add(tsbChart);
				tsbDistrictMeters = btn;

			}

			buttonsForState.put(btn, buttons);
		}

		manipulateButtons = new ArrayList<ToolStripButton>();
//		if(select_p)
			manipulateButtons.add(tsbSelect);
//		if(addedit_p)
		{
			manipulateButtons.add(tsbAdd);
			manipulateButtons.add(tsbEdit);
		}
//		if (del_p)
			manipulateButtons.add(tsbDelete);
		manipulateButtons.add(tsbChart);

		setMapState(tsbSwitchNormal);

		vl.getToolStrip().addSeparator();
		tsMain = vl.getToolStrip();
		vl.getToolStrip().addSeparator();
		mpCriteria = new TreeMap<String, Integer>();
		mpCriteria.put("for_building", 1);
		if (DocFlow.user_obj.getUser().getRegionid() >= 1) {
			mpCriteria.put("regionid", DocFlow.user_obj.getUser().getRegionid());
			if (DocFlow.user_obj.getUser().getSubregionid() >= 1) {
				mpCriteria.put("subregionid", DocFlow.user_obj.getUser().getSubregionid());
			}
		}

		tsbGoogleEarth = new MapToolStripButton();
		tsbGoogleEarth.setIcon("map/google_earth.png");
		tsbGoogleEarth.addClickHandler(tsbStateHandler);
		tsbGoogleEarth.setTooltip("Google Earth");
		tsbGoogleEarth.setActionType(SelectionType.BUTTON);
		vl.getToolStrip().addButton(tsbGoogleEarth);

		tsbSearch = new MapToolStripButton();
		tsbSearch.setIcon("[SKIN]/actions/search.png");
		tsbSearch.addClickHandler(tsbStateHandler);
		tsbSearch.setTooltip("Search");
		tsbSearch.setActionType(SelectionType.BUTTON);
		vl.getToolStrip().addButton(tsbSearch);

		tsbSearchPipeLine = new MapToolStripButton();
		tsbSearchPipeLine.setIcon("[SKIN]/actions/view.png");
		tsbSearchPipeLine.addClickHandler(tsbStateHandler);
		tsbSearchPipeLine.setTooltip("PipeLine Search");
		tsbSearchPipeLine.setActionType(SelectionType.BUTTON);
		vl.getToolStrip().addButton(tsbSearchPipeLine);

		googleLayerAdded = null;

		googleLayers = new GoogleV3[] { null, createGoogleLayer(GoogleV3MapType.G_NORMAL_MAP),
				createGoogleLayer(GoogleV3MapType.G_HYBRID_MAP), createGoogleLayer(GoogleV3MapType.G_SATELLITE_MAP),
				createGoogleLayer(GoogleV3MapType.G_TERRAIN_MAP) };

	}

	public static TreeMap<GoogleV3MapType, GoogleV3Options> gOptions = new TreeMap<GoogleV3MapType, GoogleV3Options>();
	static {
		gOptions.put(GoogleV3MapType.G_NORMAL_MAP, createGoogleOptions(GoogleV3MapType.G_NORMAL_MAP));
		gOptions.put(GoogleV3MapType.G_HYBRID_MAP, createGoogleOptions(GoogleV3MapType.G_HYBRID_MAP));
		gOptions.put(GoogleV3MapType.G_SATELLITE_MAP, createGoogleOptions(GoogleV3MapType.G_SATELLITE_MAP));
		gOptions.put(GoogleV3MapType.G_TERRAIN_MAP, createGoogleOptions(GoogleV3MapType.G_TERRAIN_MAP));

	}

	private static GoogleV3Options createGoogleOptions(GoogleV3MapType type) {
		GoogleV3Options gSatelliteOptions = new GoogleV3Options();
		gSatelliteOptions.setIsBaseLayer(false);
		gSatelliteOptions.setSphericalMercator(true);
		gSatelliteOptions.getJSObject().setProperty("numZoomLevels", 35);
		gSatelliteOptions.setType(type);
		return gSatelliteOptions;
	}

	public static GoogleV3 createGoogleLayer(GoogleV3MapType type) {

		GoogleV3 googleLayer = new GoogleV3("Google Satellite", gOptions.get(type));
		googleLayer.setIsBaseLayer(false);
		return googleLayer;
	}

	private MapToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler, SelectionType actionType, String toolTip,
			String group_id, ToolStrip toolStrip, GisLayer gl) {
		MapToolStripButton tsbButton = new MapToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setLayer(gl);
		tsbButton.setRadioGroup(group_id);
		toolStrip.addButton(tsbButton);

		return tsbButton;
	}

	private MapToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler, SelectionType actionType, String toolTip,
			String group_id, ToolStrip toolStrip) {
		return createTSButton(icon, tsbStateHandler, actionType, toolTip, group_id, toolStrip, null);
	}

	private void searchAdvanced() {
		WSearchCustomers.showWindow();
	}

	public void setCusSearchResult(String wkt) {
		setSearch(DocFlow.user_obj.getUuid());
		try {
			WKT kml = new WKT();
			VectorFeature feature = kml.read(wkt)[0];
			Bounds center = feature.getGeometry().getBounds();
			map.zoomToExtent(center);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setSearch(String uid) {
		WMSParams params = new WMSParams();
		params.setParameter("viewparams", "uid:" + uid);
		buildings_searchWMSLayer.mergeNewParams(params);
		buildings_searchWMSLayer.redraw(true);
	}

	public void findCustomerObject(Record selectedRecord) {
		Integer cusid = selectedRecord.getAttributeAsInt("cusid");
		if (cusid == null)
			return;

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>(mpCriteria);

		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				Record[] records = response.getData();
				if (records == null || records.length == 0)
					return;
				centerToBuilding(records[0], false);

			}
		};

		criteria.put("cusid", cusid);
		criteria.put("srid", Constants.GOOGLE_SRID);
		criteria.put("cusname", selectedRecord.getAttributeAsObject("cusname"));
		ClientUtils.fetchData(criteria, cb, "BuildingsDS", "buildingsByCriteria");
	}

	public void centerToBuilding(Record record, boolean line) {
		WKT kml = new WKT();
		String wkt = record.getAttribute("feature_text");
		VectorFeature feature = kml.read(wkt)[0];
		LonLat center = feature.getCenterLonLat();
		if (map.getZoom() <= MIN_FEATURE_SELECT_LEVEL + 3)
			map.setCenter(center, MIN_FEATURE_SELECT_LEVEL + 3);
		else
			map.setCenter(center);
		buildingLayer.destroyFeatures();
		Style stBuilding = new Style();
		stBuilding.setStrokeWidth(3);
		stBuilding.setStroke(true);
		stBuilding.setFill(true);
		stBuilding.setFillColor("red");

		stBuilding.setFontSize("12px");
		stBuilding.setFontFamily("Courier New, monospace");
		stBuilding.setFontWeight("bold");
		final String cusname = record.getAttribute("street");
		if (cusname != null) {
			stBuilding.setLabel(cusname);
			feature.setAttributes(new Attributes() {
				{
					this.setAttribute("cusname", cusname);

				}
			});

			feature.setStyle(stBuilding);
		}
		buildingLayer.addFeature(feature);
	}

	public void addFeature(String wkt, Style style) {
		WKT kml = new WKT();

		VectorFeature feature = kml.read(wkt)[0];
		LonLat center = feature.getCenterLonLat();
		if (map.getZoom() <= MIN_FEATURE_SELECT_LEVEL + 3)
			map.setCenter(center, MIN_FEATURE_SELECT_LEVEL + 3);
		else
			map.setCenter(center);
		buildingLayer.destroyFeatures();
		feature.setStyle(style);
		buildingLayer.addFeature(feature);
	}

	protected void setBuildingFeatureSelected() {
		boolean selected = isEditingEnabled();
		if (selected) {
			drawFeature.activate();
			selectFeature.activate();
		} else {
			drawFeature.deactivate();
			selectFeature.deactivate();
			buildingLayer.destroyFeatures();
		}
	}

	private static Dialog googleDialog;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setGoogleEarth() {

		if (googleDialog == null) {
			googleDialog = new Dialog();
			googleDialog.setAutoCenter(true);
			googleDialog.setIsModal(true);
			googleDialog.setShowHeader(false);
			googleDialog.setShowEdges(false);
			googleDialog.setEdgeSize(10);
			googleDialog.setWidth(500);
			googleDialog.setHeight(400);

			googleDialog.setShowToolbar(false);
			googleDialog.setWidth(130);
			googleDialog.setHeight(210);

			java.util.Map bodyDefaults = new HashMap();
			bodyDefaults.put("layoutLeftMargin", 5);
			bodyDefaults.put("membersMargin", 10);
			googleDialog.setBodyDefaults(bodyDefaults);

			final IButton none = new IButton("NONE");
			final IButton normal = new IButton("NORMAL");
			final IButton hibrid = new IButton("HYBRID");
			final IButton satellite = new IButton("SATELLITE");
			final IButton terrain = new IButton("TERRAIN");

			ClickHandler ch = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int tp = 0;
					if (event.getSource().equals(normal))
						tp = 1;
					if (event.getSource().equals(hibrid))
						tp = 2;
					if (event.getSource().equals(satellite))
						tp = 3;
					if (event.getSource().equals(terrain))
						tp = 4;

					try {
						if (googleLayerAdded != null)
							map.removeLayer(googleLayerAdded);
						googleLayerAdded = googleLayers[tp];
						if (googleLayerAdded != null)
							map.addLayer(googleLayerAdded);
					} catch (Exception e) {
						// TODO: handle exception
					}
					googleDialog.hide();
				}
			};
			none.addClickHandler(ch);
			normal.addClickHandler(ch);
			hibrid.addClickHandler(ch);
			satellite.addClickHandler(ch);
			terrain.addClickHandler(ch);

			googleDialog.addItem(none);
			googleDialog.addItem(normal);
			googleDialog.addItem(hibrid);
			googleDialog.addItem(satellite);
			googleDialog.addItem(terrain);

		}

		Rectangle iconRect = tsbGoogleEarth.getPageRect();
		googleDialog.show();
		googleDialog.moveTo(iconRect.getLeft(), iconRect.getTop());
		// googleLayers = new GoogleV3[] { null,
		// createGoogleLayer(GoogleV3MapType.G_NORMAL_MAP),
		// createGoogleLayer(GoogleV3MapType.G_HYBRID_MAP),
		// createGoogleLayer(GoogleV3MapType.G_SATELLITE_MAP),
		// createGoogleLayer(GoogleV3MapType.G_TERRAIN_MAP) };

		// if (tsbGoogleEarth.isSelected()) {
		// map.addLayer(googleLayer);
		// } else {
		// map.removeLayer(googleLayer);
		// }
	}

	public void setMapState(MapToolStripButton mapState) {
		if (this.mapState != null && this.mapState.equals(mapState))
			return;
		this.mapState = mapState;
		destroyPopup();
		for (MapToolStripButton btn : mapObjectButtons) {
			btn.setSelected(false);
		}
		mapState.setSelected(true);
		for (ToolStripButton tsButton : manipulateButtons) {
			tsButton.setVisible(false);
		}
		ArrayList<MapToolStripButton> buttons = buttonsForState.get(mapState);
		if (buttons == null) {
			detectSelectEnabled();
			return;
		}

		if (buttons.size() == 0) {
			detectSelectEnabled();
			return;
		}
		for (ToolStripButton tsButton : buttons) {
			tsButton.setVisible(true);
		}
		buttons.get(0).setSelected(true);
		detectSelectEnabled();
		ttsbuttonClick(buttons.get(0));
	}

	private GisMap createGisMap() {
		GisMap gm = DocFlow.user_obj.getMaps().get(0);
		return gm;

	}

	@Override
	public void onHandle(GisMapEvent eventObject) {
		if (eventObject.getEvent_type() == GisMapEvent.MAP_MOVEEND) {
			detectSelectEnabled();
		}
	}

	private void detectSelectEnabled() {
		try {
			boolean selectFeautreEnable = map.getZoom() > MIN_FEATURE_SELECT_LEVEL;
			setVectorEnabled(selectFeautreEnable);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private HashMap<Integer, FormDefinitionPanel> formDefinitons = new HashMap<Integer, FormDefinitionPanel>();

	public FormDefinitionPanel getFormDefinitionPanel(int layer_id) {
		return formDefinitons.get(layer_id);
	}

	private void createLayerDocForms(GisLayer l) {
		if (l.getForm_defination_xml() == null || l.getForm_defination_xml().trim().isEmpty())
			return;
		try {

			FormDefinition formd = new FormDefinition();
			formd.setXml(l.getForm_defination_xml());
			FormDefinitionPanel dfMapObject = new FormDefinitionPanel(formd, new SimpleFieldDefinitionListValue(),
					null, null);
			dfMapObject.setShowEdges(true);
			dfMapObject.setAutoHeight();
			dfMapObject.setWidth100();
			formDefinitons.put(l.getId(), dfMapObject);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void selectFeatures(final String point, final Bounds maxBounds) {
		// Bounds b = mapViewerPanel.getMap().getExtent();
		// Timer.reset();
		Timer.start("destroyPopup");
		destroyPopup();
		Timer.start("destroyPopup", "creating_criteria");
		Criteria cr = new Criteria();
		cr.setAttribute("to_srid", Constants.GOOGLE_SRID);
		cr.setAttribute("from_srid", Constants.SYSTEM_SRID);
		cr.setAttribute("point", point);
		int selection_type = getSelectionType();
		cr.setAttribute("map_object_type", selection_type);
		cr.setAttribute("table_name", MapObjectTypes.getMapObjectTypeTblName(selection_type));
		Timer.start("creating_criteria", "creating_request");
		DSRequest req = new DSRequest();
		final boolean addSelected = isButtonSelected(tsbAdd);
		if (addSelected)
			req.setOperationId("getBufferedPoligonNew");
		else {

			MenuItem[] menuItems = layersMenu.getItems();
			String filter = "";
			for (MenuItem m : menuItems) {
				if (!(m instanceof MapLayerMenuItem))
					continue;
				MapLayerMenuItem mi = (MapLayerMenuItem) m;
				if (!mi.isAtleast_one_is_selected())
					continue;
				if (filter.isEmpty())
					filter = "<filters>";
				filter += "<filter id=\"" + mi.getLayerId() + "\" value=\"" + mi.getSql_filter() + "\"/>";
			}
			if (!filter.isEmpty())
				filter += "</filters>";
			if (filter.isEmpty())
				return;
			cr.setAttribute("filter", filter);

			if (isButtonSelected(tsbInfo)) {
				req.setOperationId("getObjectsInfo");
			} else
				req.setOperationId("getObjectsInfoForEdit");

		}
		Timer.start("creating_request", "starting_request");
		final String crit = req.getOperationId() + " opp id Criteria=" + cr.getValues().toString();
		dsBuildings.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				System.out.println(response.getTotalRows());
				Timer.start("starting_request", "recieved_request");
				Record[] records = response.getData();
				if (records == null || records.length == 0) {
					if (DocFlow.hasPermition("CAN_RELOAD_PARAMS"))
						SC.say(crit + " with no result!!!");
					return;
				}
				Timer.start("starting_request", "recieved_request");
				Record record = records[0];
				java.util.Map m = record.toMap();
				String feature_text = record.getAttribute("feature_text");
				if (feature_text == null || feature_text.trim().isEmpty())
					return;

				// String s = crit + "\n result=" + record.toMap().toString();
				// if (DocFlow.hasPermition("CAN_RELOAD_PARAMS"))
				// SC.say(s);
				if (isButtonSelected(tsbInfo)) {
					createInfiPopup(feature_text, maxBounds.getCenterLonLat());
					return;
				}
				Timer.start("recieved_request", "convertXmlRecord");
				record = convertXmlRecord(feature_text);

				Timer.start("convertXmlRecord", "isSubregionAllowedForUser");
				if (record == null)
					return;
				m = record.toMap();
				if (addSelected) {
					record.setAttribute("mot", records[0].getAttributeAsInt("mot"));
				}
				if (!isSubregionAllowedForUser(record))
					return;
				Timer.start("isSubregionAllowedForUser", "isButtonSelected");
				if (isButtonSelected(tsbDelete)) {
					deleteMapObject(record);
					return;
				}
				if (isButtonSelected(tsbChart)) {
					chartDistrictMeter(record);
					return;
				}
				// if (isButtonSelected(tsbAdd)) {
				// record.setAttribute("feature_text", point);
				// }

				if (isButtonSelected(tsbDistrictMeters, tsbSelect)) {
					setParentMeter(record);
					return;
				}
				if (isButtonSelected(tsbSocarPipeLine, tsbSelect)) {
					new PPipeLine(record);
					return;
				}

				if (isButtonSelected(tsbBuildings, tsbSelect))
					WBuildingsDialog.showWindow(record);
				else
				/*
				 * if (tsbAddBuilding.isSelected() ||
				 * tsbEditBuilding.isSelected())
				 */{
					Timer.start("isButtonSelected", "WAddEditFeatureDialog_show");
					new WAddEditFeatureDialog(record, map.getExtent(), mapState.getLayer()).show();
					Timer.step("WAddEditFeatureDialog_show");
				}
			}
		}, req);

	}

	private Record convertXmlRecord(String feature_text) {
		Document doc = XMLParser.parse(feature_text);
		Node rootElem = doc.getChildNodes().item(0);
		if (!rootElem.hasAttributes())
			return null;
		Record r = new Record();

		// v_id as oid,v_regid as regid,v_raiid as raiid,v_cusid as cusid,v_xml
		// as xml_text,feature_text
		int oid = XMLParceserHelper.getIntValue("oid", rootElem);
		r.setAttribute("buid", oid);
		r.setAttribute("oid", oid);
		r.setAttribute("regid", XMLParceserHelper.getIntValue("regid", rootElem));
		r.setAttribute("raiid", XMLParceserHelper.getIntValue("raiid", rootElem));
		r.setAttribute("cusid", XMLParceserHelper.getIntValue("cusid", rootElem));
		int filter_id = XMLParceserHelper.getIntValue("type", rootElem);
		r.setAttribute("type", filter_id);
		String xml_text = XMLParceserHelper.getAttribute("xml_text", rootElem);
		feature_text = XMLParceserHelper.getAttribute("feature_text", rootElem);
		r.setAttribute("feature_text", feature_text);
		r.setAttribute("xml_text", xml_text);
		return r;
	}

	private static int popupid = 1;

	protected void createInfiPopup(String info, LonLat centerLonLat) {
		destroyPopup();
		if (info == null || info.trim().isEmpty())
			return;
		System.out.println(info);
		popup = new AnchoredBubble(popupid++ + "", centerLonLat, new Size(500, 400), info, null, false);
		// popup.setBackgroundColor(BetterFly.userObject.getAppOptions()
		// .getPopup_color());
		popup.setAutoSize(true);

		// popup.setOpacity(BetterFly.userObject.getAppOptions()
		// .getPopup_opacity());
		// setPopupMargin(popup.getJSObject());
		map.addPopup(popup);

	}

	private void select_map_object(String geom_text, int srid) {

		Criteria cr = new Criteria();
		cr.setAttribute("to_srid", Constants.GOOGLE_SRID);
		cr.setAttribute("from_srid", srid);
		cr.setAttribute("point", geom_text);

		DSRequest req = new DSRequest();

		req.setOperationId("getTransforemed");

		dsBuildings.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				System.out.println(response.getTotalRows());

				Record[] records = response.getData();
				if (records == null || records.length == 0) {

					return;
				}

				Record record = records[0];

				WKT kml = new WKT();
				String ft = record.getAttribute("feature_text");
				VectorFeature[] features = kml.read(ft);

				if (features == null || features.length != 1)
					return;
				VectorFeature feature = features[0];
				LonLat center = feature.getCenterLonLat();

				buildingLayer.destroyFeatures();
				Style stBuilding = new Style();
				stBuilding.setStrokeWidth(3);
				stBuilding.setStroke(true);
				stBuilding.setFill(true);
				stBuilding.setFillColor("red");

				feature.setStyle(stBuilding);
				buildingLayer.addFeature(feature);
				map.setCenter(center);
			}
		}, req);

	}

	public native void init(Object popupController)/*-{
		$wnd._myclosePopupWindow = function(id) {
			popupController.@com.docflow.client.components.map.GMapPanel::destroyPopup(I)(id);
		};
		$wnd._select_map_object = function(geom_text, srid) {
			popupController.@com.docflow.client.components.map.GMapPanel::select_map_object(Ljava/lang/String;I)(geom_text,srid);
		};
	}-*/;

	public static native void setPopupMargin(JSObject popup)/*-{
		$wnd.setPopupMargin(popup);
	}-*/;

	private void destroyPopup() {
		buildingLayer.destroyFeatures();
		if (popup != null) {
			map.removePopup(popup);
		}

	}

	private void destroyPopup(int id) {
		destroyPopup();

	}

	public ArrayList<WMS> cloneWMSLayers() {
		ArrayList<WMS> wms = new ArrayList<WMS>();
		for (Layer layer : map.getLayers()) {
			if (layer.isVisible() && layer instanceof WMS)
				((WMS) layer).redraw(true);
			// WMSParams params = new WMSParams();
			// Date dt = new Date();
			// params.setParameter("ddd", dt.getTime() + "");
			// ((WMS) layer).mergeNewParams(params);
			// break;
		}
		return wms;
	}

	protected void chartDistrictMeter(Record record) {
		new WMeterChartDialog(record).show();

	}

	protected void setParentMeter(Record record) {
		new PParentMeter(record);
	}

	private void deleteDistrictMeter(Integer cusid) {
		Record rec = new Record();
		rec.setAttribute("buid", cusid);

		try {
			dsBuildings.removeData(rec, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					redrawWMS();

				}
			});
		} catch (Exception e) {
			SC.warn(e.getMessage());
		}

	}

	protected void deleteMapObject(Record record) {
		final Integer oid = record.getAttributeAsInt("oid");
		if (oid == null)
			return;
		if (oid.intValue() < 1) {
			return;
		}

		SC.ask("გინდათ წავშალოთ " + mapState.getLayer().getButton_title() + "???", new BooleanCallback() {

			@Override
			public void execute(Boolean value) {
				if (value.booleanValue()) {
					Criteria criteria = new Criteria();
					criteria.setAttribute("oid", oid);
					criteria.setAttribute("layer_id", mapState.getLayer().getId());
					criteria.setAttribute("user_id", DocFlow.user_id);
					DSRequest req = new DSRequest();
					req.setOperationId("deleteMapObjectData");

					GMapPanel.instance.dsBuildings.fetchData(criteria, new DSCallback() {

						@Override
						public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
							GMapPanel.instance.redrawWMS();
						}
					}, req);
				}
			}

		});

	}

	// protected void deleteMapObject(Record record) {
	// final Integer cusid = record.getAttributeAsInt("cusid");
	// if (cusid == null)
	// return;
	// if (cusid < 1) {
	// return;
	// }
	// Criteria criteria = new Criteria();
	// criteria.setAttribute("cusid", cusid);
	// criteria.setAttribute("ccusid", cusid);
	// criteria.setAttribute("parent_metters", 1);
	// DocFlow.getDataSource("CustomerDS").fetchData(criteria,
	// new DSCallback() {
	//
	// @Override
	// public void execute(DSResponse response, Object rawData,
	// DSRequest request) {
	// Record[] records = response.getData();
	// if (records == null || records.length == 0)
	// return;
	// Record record = records[0];
	// String cusName = record.getAttribute("cusname");
	// SC.ask("გინდათ წავშალოთ საუბნო მრიცხველი:" + cusName
	// + "???", new BooleanCallback() {
	//
	// @Override
	// public void execute(Boolean value) {
	// if (value.booleanValue())
	// deleteDistrictMeter(cusid);
	// }
	//
	// });
	// }
	// });
	// }

	protected boolean isSubregionAllowedForUser(Record record) {
		int region_id = DocFlow.user_obj.getUser().getRegionid();
		int sub_region_id = DocFlow.user_obj.getUser().getSubregionid();
		Integer regid = record.getAttributeAsInt("regid");
		Integer raiid = record.getAttributeAsInt("raiid");
		if (regid == null || raiid == null)
			return false;
		if (region_id >= 0 && region_id != regid.intValue())
			return false;
		if (sub_region_id >= 0 && sub_region_id != raiid.intValue())
			return false;
		return true;
	}

	private boolean isEditingEnabled() {
		if (tsbInfo.isVisible() && tsbInfo.isSelected() && !tsbInfo.isDisabled())
			return true;
		for (ToolStripButton tsButton : manipulateButtons) {
			if (tsButton.isVisible() && tsButton.isSelected())
				return true;
		}
		return false;
	}

	private int getSelectionType() {
		// for (int mot : MapObjectTypes.MO_TYPES) {
		// if (isMapObjectTypeEnabled(mot))
		// return mot;
		// }
		if (mapState.getLayer() != null)
			return mapState.getLayer().getId();
		return -1;
	}

	private boolean isButtonSelected(ToolStripButton mapState, ToolStripButton tsbButton) {
		if (!this.mapState.equals(mapState))
			return false;
		return isButtonSelected(tsbButton);
	}

	private boolean isButtonSelected(ToolStripButton tsbButton) {
		if (!tsbButton.isVisible())
			return false;
		if (tsbButton.isDisabled())
			return false;
		Boolean selected = tsbButton.isSelected() == null ? false : tsbButton.isSelected();
		return selected;
	}

	private void setVectorEnabled(boolean enable) {
		if (!enable)
			setMapState(tsbSwitchNormal);

		for (ToolStripButton tsButton : mapObjectButtons) {
			tsButton.setDisabled(!enable);
		}
		if (enable) {
			try {
				if (isEditingEnabled()) {
					drawFeature.activate();
					selectFeature.activate();

				} else {
					drawFeature.deactivate();
					selectFeature.deactivate();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			drawFeature.deactivate();
			selectFeature.deactivate();
		}
	}

	public void redrawWMS() {
		for (Layer layer : map.getLayers()) {

			{
				String ln = layer.getClassName();
				if (!ln.endsWith(".WMS"))
					continue;
				WMS wms = WMS.narrowToWMS(layer.getJSObject());
				wms.redraw(true);
			}
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
		ArrayList<MapLayerMenuItem> mitms = new ArrayList<MapLayerMenuItem>();
		for (int i = 0; i < layers.length; i++) {
			Layer layer = layers[i];
			String ln = layer.getClassName();
			if (!ln.endsWith(".WMS"))
				continue;
			WMS wms = WMS.narrowToWMS(layer.getJSObject());
			String wasName = wms.getName();
			if (wms.isBaseLayer()) {
				baseLayer = wms;
			}
			if (wasName.equals("building_map")) {
				buildingsWMSLayer = wms;
			}
			if (wasName.equals("building_searched_map")) {
				buildings_searchWMSLayer = wms;
			}

			if (wasName.equals("district_meters_map")) {
				district_meterWMSLayer = wms;
			}
			GisLayer l = gisMap.getGisLayers().get(i);
			if (l.getTitle() != null && !l.getTitle().trim().isEmpty()) {
				MapLayerMenuItem mi = new MapLayerMenuItem(l, layer, mapViewerPanel.getMap());
				mitms.add(mi);
			}
			wmsLayers.add(wms);
		}
		ArrayList<GisLayer> mls = gisMap.getGisLayers();

		for (GisLayer l : mls) {
			if (l.isShowinmap())
				continue;
			Layer layer = MapHelper.createLayer(l);
			layer.setIsVisible(false);
			map.addLayer(layer);
			MapLayerMenuItem mi = new MapLayerMenuItem(l, layer, mapViewerPanel.getMap());
			mitms.add(mi);
		}
		if (!mitms.isEmpty()) {
			layersMenu = new Menu();
			layersMenu.setItems(mitms.toArray(new MapLayerMenuItem[] {}));
			final ToolStripButton tsbLayers = new ToolStripButton();
			tsbLayers.setIcon("map/layers.png");
			tsbLayers.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Rectangle iconRect = tsbLayers.getPageRect();
					layersMenu.redraw();
					layersMenu.show();
					layersMenu.moveTo(iconRect.getLeft() + 2, iconRect.getTop() + iconRect.getHeight());
				}
			});
			tsbLayers.setTooltip("Layers");
			tsbLayers.setActionType(SelectionType.BUTTON);
			tsMain.addButton(tsbLayers);

		}
		mapViewerPanel.gethSlider().hide();
		map.addControl(new PanZoomBar());
		createVector();
		detectSelectEnabled();
		setMapState(tsbSwitchNormal);
		mapViewerPanel.addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				String sh = mapViewerPanel.getHeightAsString();
				int h = Integer.parseInt(sh) - 20;
				mapViewerPanel.getMapWidget().setHeight("" + h);
				mapViewerPanel.getMap().updateSize();
				System.out.println("dddddddddddddddd");
			}
		});
		if (DocFlow.user_obj.getB_box() != null) {
			createMapBox(DocFlow.user_obj.getB_box());
		}
		if (buildingsWMSLayer != null && DocFlow.user_obj.getCql_filter() != null) {
			WMSParams params = new WMSParams();
			params.setParameter("CQL_FILTER", DocFlow.user_obj.getCql_filter());
			buildingsWMSLayer.mergeNewParams(params);
			district_meterWMSLayer.mergeNewParams(params);
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
		if (!(center.lon() >= bounds.getLowerLeftX() && center.lon() >= bounds.getLowerLeftY()
				&& center.lat() <= bounds.getLowerLeftX() && center.lat() <= bounds.getLowerLeftY()))
			map.setCenter(bounds.getCenterLonLat());
		SplashDialog.hideSplash();

	}

	private void createVector() {
		FeatureAddedListener featureAddedListener = new FeatureAddedListener() {

			@Override
			public void onFeatureAdded(VectorFeature vectorFeature) {
				WKT kml = new WKT();
				String wf = kml.write(vectorFeature);
				selectFeatures(wf, vectorFeature.getGeometry().getBounds());
				buildingLayer.destroyFeatures();

			}
		};
		DrawFeatureOptions dfo = new DrawFeatureOptions();
		dfo.onFeatureAdded(featureAddedListener);

		drawFeature = new DrawFeature(buildingLayer, new PointHandler(), dfo);
		selectFeature = new SelectFeature(buildingLayer);

		selectFeature.addFeatureHighlightedListener(new FeatureHighlightedListener() {

			@Override
			public void onFeatureHighlighted(VectorFeature vectorFeature) {
				SC.say(map.getPixelFromLonLat(vectorFeature.getCenterLonLat()) + " id="
						+ vectorFeature.getAttributes().getAttributeAsString(FEATURE_ID_NAME));

			}
		});
		map.addLayer(buildingLayer);
		map.addControl(drawFeature);
		map.addControl(selectFeature);
	}

	private void ttsbuttonClick(Object objectSource) {
		if (objectSource.equals(tsbDemages)) {
			Criteria cr = new Criteria();
			// cr.setAttribute("demage_type", 2);
			cr.setAttribute("pcityid", 24);
			new WDemagesDialog().show();
			return;
		}
		if (objectSource instanceof MapToolStripButton) {
			MapToolStripButton tsb = (MapToolStripButton) objectSource;
			if (mapObjectButtons.contains(tsb))
				setMapState(tsb);
		}

		if (manipulateButtons.contains(objectSource))
			setBuildingFeatureSelected();
		if (objectSource.equals(tsbGoogleEarth))
			setGoogleEarth();
		if (objectSource.equals(tsbSearch))
			searchAdvanced();
		if (objectSource.equals(tsbSearch))
			searchAdvanced();
		if (objectSource.equals(tsbSearchPipeLine))
			new WSelectPipeLines(null, null).show();
	}

	public String getLayerFilter(int id) {
		String filter = "";
		MenuItem[] menuItems = layersMenu.getItems();
		for (MenuItem m : menuItems) {
			if (!(m instanceof MapLayerMenuItem))
				continue;
			MapLayerMenuItem mi = (MapLayerMenuItem) m;
			if (!mi.isAtleast_one_is_selected())
				continue;
			if (mi.getLayerId() == id) {
				filter = mi.getSql_filter();
				break;
			}
		}
		return filter;
	}
}
