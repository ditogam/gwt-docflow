package com.docflow.client.components.map;

import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.gwtopenmaps.openlayers.client.event.EventType;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.common.client.map.MapHelper;
import com.common.shared.map.GisLayer;
import com.common.shared.map.GisMap;
import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.components.SavePanel;
import com.docflow.client.components.common.ExceptionDialog;
import com.docflow.shared.MapObjectTypes;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WAddEditFeatureDialogNew extends Window {

	public GoogleV3[] googleLayers;
	public GoogleV3 googleLayerAdded;

	private Record record;
	private MapWidget mapWidget;
	private MapOptions options;
	private Map map;
	private VectorFeature feature;
	@SuppressWarnings("unused")
	private Bounds restrictedBounds;
	private VLayout vlMap;
	private ModifyFeature drawFeature;
	private Vector modifyVector;
	private int map_object_type;
	private boolean pointObject;
	private DynamicForm dfMapObject;
	final ToolStripButton tsbGoogleEarth;

	public WAddEditFeatureDialogNew(Record record, Bounds restrictedBounds, GisLayer layer) {

		this.record = record;
		map_object_type = record.getAttributeAsInt("map_object_type");
		pointObject = MapObjectTypes.getMapObjectTypeIsPoint(map_object_type);
		VLayout vlMain = new VLayout();

		dfMapObject = createForm(map_object_type, record);
		if (dfMapObject != null)
			vlMain.addMember(dfMapObject);
		this.restrictedBounds = restrictedBounds;
		final GisMap gisMap = DocFlow.user_obj.getMaps().get(0);
		this.options = MapHelper.createMapOptions(gisMap);

		ToolStrip tsMain = new ToolStrip();
		final ToolStripButton tsbResize = new ToolStripButton();
		final ToolStripButton tsbDrag = new ToolStripButton();
		final ToolStripButton tsbReshape = new ToolStripButton();
		final ToolStripButton tsbRotate = new ToolStripButton();
		tsbGoogleEarth = new ToolStripButton();

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				DocFlow.selectFeature(drawFeature.getJSObject(),
						feature.getJSObject(), 0);
				if (event.getSource().equals(tsbDrag))
					drawFeature.setMode(ModifyFeature.DRAG);
				if (event.getSource().equals(tsbResize))
					drawFeature.setMode(ModifyFeature.RESIZE);
				if (event.getSource().equals(tsbReshape))
					drawFeature.setMode(ModifyFeature.RESHAPE);
				if (event.getSource().equals(tsbRotate))
					drawFeature.setMode(ModifyFeature.ROTATE);
				if (event.getSource().equals(tsbGoogleEarth)) {
					setGoogleEarth();
				}
				DocFlow.selectFeature(drawFeature.getJSObject(),
						feature.getJSObject(), 1);
			}
		};

		googleLayerAdded = null;

		googleLayers = new GoogleV3[] { null,
				createGoogleLayer(GoogleV3MapType.G_NORMAL_MAP),
				createGoogleLayer(GoogleV3MapType.G_HYBRID_MAP),
				createGoogleLayer(GoogleV3MapType.G_SATELLITE_MAP),
				createGoogleLayer(GoogleV3MapType.G_TERRAIN_MAP) };

		tsbDrag.setIcon("map/drag.jpg");
		tsbDrag.addClickHandler(tsbStateHandler);
		tsbDrag.setTooltip("Drag");
		tsbDrag.setActionType(SelectionType.RADIO);
		tsbDrag.setRadioGroup("selection");
		tsbDrag.setSelected(true);
		tsMain.addButton(tsbDrag);
		if (!pointObject) {
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

		tsbGoogleEarth.setIcon("map/google_earth.png");
		tsbGoogleEarth.addClickHandler(tsbStateHandler);
		tsbGoogleEarth.setTooltip("Google Earth");
		tsbGoogleEarth.setActionType(SelectionType.CHECKBOX);
		tsbGoogleEarth.setSelected(true);
		tsMain.addButton(tsbGoogleEarth);
		vlMain.addMember(tsMain);

		options.setRestrictedExtent(restrictedBounds);
		vlMap = new VLayout();

		int h = 500;
		vlMap.setHeight("98%");
		vlMap.setWidth100();
		createMap(gisMap);

		vlMap.addMember(mapWidget);

		vlMain.setHeight100();
		vlMain.setWidth100();

		vlMain.addMember(vlMap);
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
		// double diff = (restrictedBounds.getUpperRightY() - restrictedBounds
		// .getLowerLeftY())
		// / (restrictedBounds.getUpperRightX() - restrictedBounds
		// .getLowerLeftX());
		// this.setWidth((int)(h*diff));
		this.setWidth(700);
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
		GoogleV3 googleLayer = new GoogleV3("Google Satellite",
				gSatelliteOptions);
		googleLayer.setIsBaseLayer(false);

		return googleLayer;

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

	@SuppressWarnings("unchecked")
	protected void saveData() {
		try {
			if (dfMapObject != null && !dfMapObject.validate())
				return;
			WKT kml = new WKT();
			String build_str = kml.write(feature);
			Record record = this.record;
			if (dfMapObject != null) {
				java.util.Map<String, Object> mp = ClientUtils.fillMapFromForm(
						record.toMap(), dfMapObject);
				record = new Record(mp);
			}
			record.setAttribute("srid", Constants.GOOGLE_SRID);
			record.setAttribute("to_srid", Constants.SYSTEM_SRID);
			record.setAttribute("feature_text", build_str);

			GMapPanel.instance.dsBuildings.updateData(record, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					GMapPanel.instance.redrawWMS();
					destroy();
				}
			});

		} catch (Exception e) {
			ExceptionDialog.showError(e);
			// SC.warn(e.getMessage());
		}
	}

	private void completeCreation(GisMap gisMap) {

		modifyVector = new Vector("Modify");

		drawFeature = new ModifyFeature(modifyVector);

		drawFeature.setMode(ModifyFeature.DRAG);
		map.addLayers(MapHelper.createLayers(gisMap,
				new Layer[] { modifyVector }, true));

		map.addControl(drawFeature);
		EventHandler eh = new EventHandler() {
			@Override
			public void onHandle(EventObject eventObject) {
				if (map.getZoom() <= GMapPanel.MIN_FEATURE_SELECT_LEVEL - 2)
					map.zoomTo(map.getZoom() + 1);
			}
		};
		map.getEvents().register(EventType.MAP_MOVEEND, map, eh);

		String sh = vlMap.getHeightAsString();
		int h = Integer.parseInt(sh) - 20;
		mapWidget.setHeight("" + h);
		String sw = vlMap.getWidthAsString();
		int w = Integer.parseInt(sw) - 10;
		mapWidget.setWidth("" + w);

		map.updateSize();

		map.addControl(new PanZoomBar());
		map.addControl(new Navigation());
		WKT kml = new WKT();
		String wkt = record.getAttribute("feature_text");
		feature = kml.read(wkt)[0];
		if (pointObject) {
			String icon = MapObjectTypes.getMapObjectTypeIcon(map_object_type);
			int[] imegeSize = MapObjectTypes
					.getMapObjectTypeIconSize(map_object_type);
			Style stPoint = new Style();
			stPoint.getJSObject().setProperty("graphicHeight", imegeSize[0]);
			stPoint.getJSObject().setProperty("graphicWidth", imegeSize[1]);
			stPoint.setExternalGraphic(icon);
			feature.setStyle(stPoint);
		}

		Bounds b = feature.getGeometry().getBounds();
		map.zoomToExtent(b);
		map.setCenter(feature.getCenterLonLat());
		map.zoomTo(map.getZoom() - 1);
		modifyVector.addFeature(feature);
		drawFeature.activate();
		DocFlow.selectFeature(drawFeature.getJSObject(), feature.getJSObject(),
				1);
		// selectFeature.activate();
	}

	private void createMap(final GisMap gisMap) {
		mapWidget = new MapWidget("90%", "90%", options) {

			@Override
			protected void onAttach() {
				super.onAttach();
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						mapWidget.setSize("100%", "100%");
						map = mapWidget.getMap();

						completeCreation(gisMap);
					}

				});
			}
		};
	}

}
