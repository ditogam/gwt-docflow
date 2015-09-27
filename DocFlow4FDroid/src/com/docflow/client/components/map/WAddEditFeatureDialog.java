package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.gwtopenmaps.openlayers.client.event.EventType;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureModifiedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.handler.PathHandler;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.common.client.map.GisMapEvent;
import com.common.client.map.GisMapEventHandler;
import com.common.client.map.MapHelper;
import com.common.client.map.MapViewerPanel;
import com.common.shared.map.GisLayer;
import com.common.shared.map.GisMap;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.DocPanelSettingDataComplete;
import com.docflow.client.components.common.ExceptionDialog;
import com.docflow.client.components.common.FormDefinitionPanel;
import com.docflow.client.components.docflow.DocumentDetailTabPane;
import com.docflow.shared.MapObjectTypes;
import com.docflow.shared.PermissionNames;
import com.docflow.shared.common.FormDefinition;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WAddEditFeatureDialog extends Window implements GisMapEventHandler, DocPanelSettingDataComplete {

	public GoogleV3[] googleLayers;
	public GoogleV3 googleLayerAdded;

	private Record record;
	private MapOptions options;
	private Map map;
	private VectorFeature feature;
	private Bounds restrictedBounds;
	private Control modifyFeature;
	private Vector modifyVector;
	private FormDefinitionPanel dfMapObject;
	final ToolStripButton tsbGoogleEarth;
	private GisLayer layer;
	private MapViewerPanel mapViewerPanel;
	private HashMap<Boolean, ArrayList<String>> undoActions;

	private ToolStripButton tsbUndo = new ToolStripButton();
	private ToolStripButton tsbRedu = new ToolStripButton();
	private Style style;
	private String old_WKT;
	private int mao_object_type;

	public WAddEditFeatureDialog(Record record, Bounds restrictedBounds, GisLayer layer) {
		Timer.start("WAddEditFeatureDialog_create");
		this.record = record;
		this.layer = layer;
		undoActions = new HashMap<Boolean, ArrayList<String>>();
		undoActions.put(true, new ArrayList<String>());
		undoActions.put(false, new ArrayList<String>());
		this.setOverflow(Overflow.HIDDEN);
		setScrollbarSize(0);
		setWidth100();
		setHeight100();
		VLayout vlMain = new VLayout();

		this.restrictedBounds = restrictedBounds;
		final GisMap gisMap = DocFlow.user_obj.getMaps().get(0);
		Timer.start("WAddEditFeatureDialog_create", "createMapOptions");
		this.options = MapHelper.createMapOptions(gisMap);
		Timer.start("createMapOptions", "MapViewerPanel");
		Timer.start("MapWidget");
		mapViewerPanel = new MapViewerPanel(gisMap, false);
		Timer.start("MapViewerPanel", "begin_creating_components");
		ToolStrip tsMain = mapViewerPanel.getToolStrip();

		tsMain.removeFromParent();
		this.addItem(tsMain);
		final ToolStripButton tsbResize = new ToolStripButton();
		final ToolStripButton tsbDrag = new ToolStripButton();
		final ToolStripButton tsbReshape = new ToolStripButton();
		final ToolStripButton tsbRotate = new ToolStripButton();
		final ToolStripButton tsbGoto = new ToolStripButton();
		tsbGoogleEarth = new ToolStripButton();
		final ToolStripButton tsbSave = new ToolStripButton();
		final ToolStripButton tsbCancel = new ToolStripButton();
		tsbSave.setVisible(!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_SAVE_RESTRICT));
		final ToolStripButton tsbShowDebug = new ToolStripButton();

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(tsbShowDebug)) {
					SC.say(Timer.printall());
					return;
				}

				if (event.getSource().equals(tsbSave)) {
					saveData();
					return;
				}
				if (event.getSource().equals(tsbUndo)) {
					undoAction(true);
					return;
				}
				if (event.getSource().equals(tsbRedu)) {
					undoAction(false);
					return;
				}
				if (event.getSource().equals(tsbCancel)) {
					destroy();
					return;
				}
				if (event.getSource().equals(tsbGoto) && feature != null) {
					map.setCenter(feature.getCenterLonLat());
				}
				if (modifyFeature instanceof ModifyFeature) {
					ModifyFeature modifyFeature = (ModifyFeature) WAddEditFeatureDialog.this.modifyFeature;
					DocFlow.selectFeature(modifyFeature.getJSObject(), feature.getJSObject(), 0);
					if (event.getSource().equals(tsbDrag))
						modifyFeature.setMode(ModifyFeature.DRAG);
					if (event.getSource().equals(tsbResize))
						modifyFeature.setMode(ModifyFeature.RESIZE);
					if (event.getSource().equals(tsbReshape))
						modifyFeature.setMode(ModifyFeature.RESHAPE);
					if (event.getSource().equals(tsbRotate))
						modifyFeature.setMode(ModifyFeature.ROTATE);
					if (event.getSource().equals(tsbGoogleEarth)) {
						setGoogleEarth();
					}
					DocFlow.selectFeature(modifyFeature.getJSObject(), feature.getJSObject(), 1);
				}

			}
		};

		googleLayerAdded = null;
		Timer.start("createMapOptions", "MapViewerPanel");
		googleLayers = new GoogleV3[] { null, createGoogleLayer(GoogleV3MapType.G_NORMAL_MAP),
				createGoogleLayer(GoogleV3MapType.G_HYBRID_MAP), createGoogleLayer(GoogleV3MapType.G_SATELLITE_MAP),
				createGoogleLayer(GoogleV3MapType.G_TERRAIN_MAP) };

		tsbSave.setIcon("[SKIN]/actions/save.png");
		tsbSave.addClickHandler(tsbStateHandler);
		tsbSave.setTooltip("Save");
		tsbSave.setActionType(SelectionType.BUTTON);
		tsbSave.setSelected(false);
		tsMain.addButton(tsbSave);

		tsbCancel.setIcon("[SKIN]/actions/close.png");
		tsbCancel.addClickHandler(tsbStateHandler);
		tsbCancel.setTooltip("Close");
		tsbCancel.setActionType(SelectionType.BUTTON);
		tsbCancel.setSelected(false);
		tsMain.addButton(tsbCancel);
		tsMain.addSeparator();

		tsbGoto.setIcon("goto.png");
		tsbGoto.addClickHandler(tsbStateHandler);
		tsbGoto.setTooltip("Goto object");
		tsbGoto.setActionType(SelectionType.BUTTON);
		tsbGoto.setSelected(false);
		tsMain.addButton(tsbGoto);
		tsMain.addSeparator();

		tsbDrag.setIcon("map/drag.jpg");
		tsbDrag.addClickHandler(tsbStateHandler);
		tsbDrag.setTooltip("Drag");
		tsbDrag.setActionType(SelectionType.RADIO);
		tsbDrag.setRadioGroup("selection");
		tsbDrag.setSelected(true);
		tsMain.addButton(tsbDrag);

		Timer.start("WKT_CREATEFIATURE");
		WKT kml = new WKT();
		old_WKT = record.getAttribute("feature_text");
		Integer mot = record.getAttributeAsInt("mot");
		mao_object_type = mot == null ? 0 : mot.intValue();
		feature = kml.read(old_WKT)[0];
		Timer.step("WKT_CREATEFIATURE");
		String geomClass = feature.getGeometry().getClassName();
		if (!(geomClass.equals(Geometry.POINT_CLASS_NAME) || geomClass.equals(Geometry.MULTI_POINT_CLASS_NAME))) {
			tsbResize.setIcon("map/resize.jpg");
			tsbResize.addClickHandler(tsbStateHandler);
			tsbResize.setTooltip("Resize");
			tsbResize.setActionType(SelectionType.RADIO);
			tsbResize.setRadioGroup("selection");
			tsMain.addButton(tsbResize);

			tsbReshape.setIcon("map/resize_reshape.jpg");
			tsbReshape.addClickHandler(tsbStateHandler);
			tsbReshape.setTooltip("Reshape");
			tsbReshape.setActionType(SelectionType.RADIO);
			tsbReshape.setRadioGroup("selection");
			tsMain.addButton(tsbReshape);

			tsbRotate.setIcon("map/rotate.jpg");
			tsbRotate.addClickHandler(tsbStateHandler);
			tsbRotate.setTooltip("Rotate");
			tsbRotate.setActionType(SelectionType.RADIO);
			tsbRotate.setRadioGroup("selection");
			tsMain.addButton(tsbRotate);
		}

		tsMain.addSeparator();
		tsbUndo.setIcon("arrow_undo.png");
		tsbUndo.addClickHandler(tsbStateHandler);
		tsbUndo.setTooltip("Undo");
		tsbUndo.setActionType(SelectionType.BUTTON);
		tsbUndo.setDisabled(true);
		tsMain.addButton(tsbUndo);

		tsbRedu.setIcon("arrow_redo.png");
		tsbRedu.addClickHandler(tsbStateHandler);
		tsbRedu.setTooltip("Redo");
		tsbRedu.setActionType(SelectionType.BUTTON);
		tsbRedu.setDisabled(true);
		tsMain.addButton(tsbRedu);

		tsMain.addSeparator();

		tsbGoogleEarth.setIcon("map/google_earth.png");
		tsbGoogleEarth.addClickHandler(tsbStateHandler);
		tsbGoogleEarth.setTooltip("Google Earth");
		tsbGoogleEarth.setActionType(SelectionType.CHECKBOX);
		tsbGoogleEarth.setSelected(true);
		tsMain.addButton(tsbGoogleEarth);
		Timer.start("createDocForm");
		dfMapObject = createForm(record);
		if (dfMapObject != null)
			this.addItem(dfMapObject);
		Timer.step("createDocForm");
		options.setRestrictedExtent(restrictedBounds);
		mapViewerPanel.addGisMapEventHandler(this);

		if (DocFlow.hasPermition("CAN_RELOAD_PARAMS")) {
			tsMain.addSeparator();
			tsbShowDebug.setTitle("Show debug");
			tsbShowDebug.addClickHandler(tsbStateHandler);
			tsbShowDebug.setTooltip("Drag");
			tsbShowDebug.setActionType(SelectionType.BUTTON);
			tsMain.addButton(tsbShowDebug);
		}

		vlMain.setHeight100();
		vlMain.setWidth100();
		mapViewerPanel.setHeight100();
		mapViewerPanel.setWidth100();

		this.addItem(mapViewerPanel);

		// this.setHeight(h);
		// double diff = (restrictedBounds.getUpperRightY() - restrictedBounds
		// .getLowerLeftY())
		// / (restrictedBounds.getUpperRightX() - restrictedBounds
		// .getLowerLeftX());
		// this.setWidth((int)(h*diff));
		// this.setWidth(1100);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();
		this.setTitle("რუკის ელემენები");
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
		Timer.step("begin_creating_components");
	}

	@Override
	public void destroy() {
		if (dfMapObject != null)
			this.removeItem(dfMapObject);
		super.destroy();
	}

	public native void init(JSObject modifyVector, JSObject drawFeature, JSObject style, JSObject feature)/*-{
		$wnd._editor_modifyVector = modifyVector;
		$wnd._editor_drawFeature = drawFeature;
		$wnd._editor_style = style;
		$wnd._editor_feature = feature;
	}-*/;

	protected void undoAction(boolean undo) {
		ArrayList<String> array = undoActions.get(undo);
		String value = array.get(array.size() - 1);
		array.remove(array.size() - 1);
		undoActions.get(!undo).add(old_WKT);
		setUndoDisabled();

		modifyVector.removeFeature(feature);
		WKT kml = new WKT();
		String wkt = value;
		old_WKT = wkt;
		feature = kml.read(wkt)[0];
		// style = feature.getStyle();
		if (style != null)
			feature.setStyle(style);
		modifyVector.addFeature(feature);
		modifyFeature.activate();

		DocFlow.selectFeature(modifyFeature.getJSObject(), feature.getJSObject(), 1);
		setjsObjects();
	}

	private void setUndoDisabled() {
		tsbUndo.setDisabled(undoActions.get(true).isEmpty());
		tsbRedu.setDisabled(undoActions.get(false).isEmpty());
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

	}

	private GoogleV3 createGoogleLayer(GoogleV3MapType type) {
		GoogleV3Options gSatelliteOptions = GMapPanel.gOptions.get(type);
		GoogleV3 googleLayer = new GoogleV3("Google Satellite", gSatelliteOptions);
		googleLayer.setIsBaseLayer(false);

		return googleLayer;

	}

	private FormDefinitionPanel createForm(Record rec) {
		try {

			dfMapObject = GMapPanel.instance.getFormDefinitionPanel(layer.getId());
			if (dfMapObject == null)
				return null;
			dfMapObject.setDataComplete(this);
			dfMapObject.setAutoHeight();
			dfMapObject.setWidth100();
			return dfMapObject;

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void saveData() {
		try {
			String xml_text = "<DocDef/>";
			if (dfMapObject != null && !dfMapObject.validate())
				return;
			if (dfMapObject != null) {
				HashMap<String, Object> data = dfMapObject.getData();
				xml_text = FormDefinition.getXml(data);
			}
			WKT kml = new WKT();
			String feature_text = kml.write(feature);
			Criteria cr = new Criteria();
			java.util.Map mp = record.toMap();
			Set<Object> keys = mp.keySet();
			for (Object o : keys) {
				if (o == null)
					continue;
				Object v = mp.get(o);
				if (v == null)
					continue;
				cr.setAttribute(o.toString(), v);
			}

			cr.setAttribute("from_srid", Constants.GOOGLE_SRID);
			cr.setAttribute("feature_text", feature_text);
			cr.setAttribute("xml_text", xml_text);
			cr.setAttribute("layer_id", layer.getId());
			cr.setAttribute("user_id", DocFlow.user_id);
			DSRequest req = new DSRequest();
			req.setOperationId("updateMapObjectData");

			GMapPanel.instance.dsBuildings.fetchData(cr, new DSCallback() {

				@Override
				public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
					GMapPanel.instance.redrawWMS();
					destroy();

				}
			}, req);

		} catch (Exception e) {
			ExceptionDialog.showError(e);
			// SC.warn(e.getMessage());
		}
	}

	private void completeCreation(GisMap gisMap) {
		Timer.start("addcontrols");
		int filter_id = 0;
		mapViewerPanel.gethSlider().hide();
		mapViewerPanel.setHeight100();
		mapViewerPanel.setWidth100();
		map.updateSize();
		modifyVector = new Vector("Modify");
		map.addControl(new PanZoomBar());
		map.addControl(new Navigation());
		Timer.start("addcontrols", "adding_layers");
		modifyVector.addVectorFeatureModifiedListener(new VectorFeatureModifiedListener() {

			@Override
			public void onFeatureModified(FeatureModifiedEvent eventObject) {
				System.out.println("");
				WKT kml = new WKT();
				VectorFeature feature = eventObject.getVectorFeature();
				String value = kml.write(feature);
				undoActions.get(true).add(old_WKT);
				undoActions.get(false).clear();
				setUndoDisabled();
				old_WKT = value;

			}
		});

		if (this.layer != null) {
			Layer was = null;
			int id = -1;
			Layer[] layers = map.getLayers();
			for (Layer layer : layers) {
				try {
					id = layer.getJSObject().getPropertyAsInt("_GisLayerID");
					if (id == this.layer.getId()) {
						was = layer;
						break;
					}
				} catch (Throwable e) {
					// TODO: handle exception
				}
			}
			if (was == null) {
				was = MapHelper.createLayer(this.layer);
				map.addLayer(was);
			}
			if (!was.isVisible())
				was.setIsVisible(true);
			String filter = GMapPanel.instance.getLayerFilter(this.layer.getId());
			if (filter != null && !filter.trim().isEmpty() && was instanceof WMS) {
				WMS wms = WMS.narrowToWMS(was.getJSObject());
				WMSParams params = new WMSParams();
				params.setCQLFilter(filter.isEmpty() ? null : filter);
				wms.mergeNewParams(params);
				wms.redraw(true);
			}
		}

		EventHandler eh = new EventHandler() {
			@Override
			public void onHandle(EventObject eventObject) {
				if (map.getZoom() <= GMapPanel.MIN_FEATURE_SELECT_LEVEL - 2)
					map.zoomTo(map.getZoom() + 1);
			}
		};
		map.getEvents().register(EventType.MAP_MOVEEND, map, eh);

		// map.updateSize();
		Timer.start("adding_layers", "getting_styles");
		try {
			filter_id = record.getAttributeAsInt("type");
			try {
				JSObject stm = setStyleMap(layer.getId(), filter_id);
				System.out.println(stm);
				if (stm != null) {
					style = new Style();
					style.setJSObject(stm);

				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		Timer.start("getting_styles", "creating_map_objects");
		if (mao_object_type == MapObjectTypes.MOT_LINE) {

			FeatureAddedListener featureAddedListener = new FeatureAddedListener() {

				@Override
				public void onFeatureAdded(VectorFeature vectorFeature) {
					WKT kml = new WKT();
					String wf = kml.write(vectorFeature);
					createModifyFeature(wf);

				}
			};
			DrawFeatureOptions dfo = new DrawFeatureOptions();
			dfo.onFeatureAdded(featureAddedListener);
			PathHandler handler = new PathHandler();
			if (style != null)
				handler.setStyle(style);
			modifyFeature = new DrawFeature(modifyVector, handler, dfo);
		} else {
			modifyFeature = new ModifyFeature(modifyVector);
			((ModifyFeature) modifyFeature).setMode(ModifyFeature.DRAG);
		}
		map.addLayer(modifyVector);
		map.addControl(modifyFeature);

		map.setCenter(feature.getCenterLonLat(), 1 + GMapPanel.instance.mapViewerPanel.getMap().getZoom());

		if (mao_object_type == MapObjectTypes.MOT_LINE) {

			feature = null;
		}
		// map.zoomTo(map.getZoom() - 1);
		if (feature != null && style != null)
			feature.setStyle(style);

		modifyFeature.activate();
		if (feature != null) {
			// Bounds b = feature.getGeometry().getBounds();
			// map.zoomToExtent(b);
			modifyVector.addFeature(feature);
			DocFlow.selectFeature(modifyFeature.getJSObject(), feature.getJSObject(), 1);
		}
		Timer.start("creating_map_objects", "setjsObjects");
		setjsObjects();
		Timer.start("setjsObjects", "dfMapObject_set_date");
		if (dfMapObject != null) {
			dfMapObject.activate();
			Timer.start("xml_data");
			String xml_data = record.getAttribute("xml_text");
			Timer.start("xml_data", "DocumentDetailTabPane_get_data");
			HashMap<String, String> displayValues = new HashMap<String, String>();
			HashMap<String, String> values = DocumentDetailTabPane.getData(xml_data, false, displayValues);
			Timer.start("DocumentDetailTabPane_get_data", "DocumentDetailTabPane_set_data");
			Timer.start("settingDataComplete");
			dfMapObject.setData(values, record.getAttributeAsInt("cusid"), displayValues);
			Timer.step("settingDataComplete");
		}
		Timer.step("dfMapObject_set_date");

	}

	protected void createModifyFeature(String wf) {
		modifyFeature.deactivate();
		modifyVector.destroyFeatures();
		map.removeControl(modifyFeature);
		WKT kml = new WKT();
		feature = kml.read(wf)[0];
		old_WKT = wf;
		modifyFeature = new ModifyFeature(modifyVector);
		map.addControl(modifyFeature);
		((ModifyFeature) modifyFeature).setMode(ModifyFeature.DRAG);
		modifyFeature.activate();
		if (feature != null && style != null)
			feature.setStyle(style);
		modifyVector.addFeature(feature);

		setjsObjects();
		HashMap<String, Object> data = dfMapObject.getData();
		dfMapObject.setCalculatorProceed();
		DocFlow.selectFeature(modifyFeature.getJSObject(), feature.getJSObject(), 1);

	}

	public static native JSObject setStyleMap(int layer_id, int filter_id) /*-{
		return $wnd.createmapobjectstyle(layer_id, filter_id);
	}-*/;

	@Override
	public void onHandle(GisMapEvent eventObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void creationComplete() {
		Timer.start("MapWidget", "creationComplete_getMap");
		map = mapViewerPanel.getMap();
		Timer.start("creationComplete_getMap", "completeCreation");
		completeCreation(DocFlow.user_obj.getMaps().get(0));
		Timer.step("completeCreation");

	}

	private void setjsObjects() {
		try {
			init(modifyVector.getJSObject(), modifyFeature.getJSObject(), style == null ? null : style.getJSObject(),
					feature == null ? null : feature.getJSObject());
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	@Override
	public void settingDataComplete() {
		Timer.start("settingDataComplete", "setCalculatorProceed_do");
		dfMapObject.setCalculatorProceed();
		Timer.step("setCalculatorProceed_do");
	}

}
