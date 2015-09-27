package com.docflow.client.components.common.u;

import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.common.client.map.MapHelper;
import com.common.shared.map.GisMap;
import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.client.components.map.Constants;
import com.docflow.client.components.map.GMapPanel;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WMapDialog extends Window {

	public GoogleV3[] googleLayers;
	public GoogleV3 googleLayerAdded;

	private MapWidget mapWidget;
	private MapOptions options;
	private Map map;
	private VectorFeature point;

	private VLayout vlMap;
	private ModifyFeature drawFeature;
	private Vector modifyVector;
	private DynamicForm dfMapObject;
	final ToolStripButton tsbGoogleEarth;

	private MapButton mapButton;
	private static WMapDialog instance = null;
	DynamicForm dfSearch;
	private SelectItem siRegion;
	SelectItem siSubregion;

	public static void showMapDialog(MapButton mapButton) {
		if (instance == null)
			instance = new WMapDialog();
		instance.setMapButton(mapButton);
		instance.centerInPage();
		instance.show();
	}

	public WMapDialog() {
		dfSearch = new DynamicForm();
		siRegion = new SelectItem("regid", DocFlow.getCaption(69));
		siSubregion = new SelectItem("raiid", DocFlow.getCaption(71));
		java.util.Map<String, Object> regionCrit = new HashMap<String, Object>();

		Integer regid = DocFlow.user_obj.getUser().getRegionid();
		Integer subregionid = DocFlow.user_obj.getUser().getSubregionid();
		siRegion.setDisabled(false);
		siSubregion.setDisabled(false);
		if (regid > 0) {
			siRegion.setValue(regid);
			if (!DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS")) {
				siRegion.setDisabled(true);
				siSubregion.setDisabled(!DocFlow
						.hasPermition(PermissionNames.CAN_VIEW_ALL_SUBREGIONS));
			}
			regionCrit.put("parentId", regid);
		}

		if (subregionid > 0) {
			siSubregion.setValue(subregionid);
			siSubregion.setDisabled(!DocFlow
					.hasPermition(PermissionNames.CAN_VIEW_ALL_SUBREGIONS));

		}
		ClientUtils.fillSelectionCombo(siRegion, ClSelection.T_REGION);
		ClientUtils.fillSelectionCombo(siSubregion, ClSelection.T_SUBREGION,
				regionCrit);
		ClientUtils.makeDependancy(siRegion, true, new FormItemDescr(
				siSubregion));

		dfSearch.setFields(siRegion, siSubregion);
		dfSearch.setNumCols(4);

		siSubregion.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Object val = event.getValue();
				searchBySubregion(val);

			}
		});

		VLayout vlMain = new VLayout();

		final GisMap gisMap = DocFlow.user_obj.getMaps().get(0);
		this.options = MapHelper.createMapOptions(gisMap);
		mapWidget = new MapWidget("100%", "100%", options);
		ToolStrip tsMain = new ToolStrip();

		tsbGoogleEarth = new ToolStripButton();

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(tsbGoogleEarth)) {
					setGoogleEarth();
				}

			}
		};

		googleLayerAdded = null;

		googleLayers = new GoogleV3[] { null,
				GMapPanel.createGoogleLayer(GoogleV3MapType.G_NORMAL_MAP),
				GMapPanel.createGoogleLayer(GoogleV3MapType.G_HYBRID_MAP),
				GMapPanel.createGoogleLayer(GoogleV3MapType.G_SATELLITE_MAP),
				GMapPanel.createGoogleLayer(GoogleV3MapType.G_TERRAIN_MAP) };

		tsbGoogleEarth.setIcon("map/google_earth.png");
		tsbGoogleEarth.addClickHandler(tsbStateHandler);
		tsbGoogleEarth.setTooltip("Google Earth");
		tsbGoogleEarth.setActionType(SelectionType.CHECKBOX);
		tsbGoogleEarth.setSelected(true);
		tsMain.addButton(tsbGoogleEarth);
		tsMain.addSeparator();
		tsMain.addFormItem(siRegion);
		tsMain.addFormItem(siSubregion);
		vlMain.addMember(tsMain);

		// options.setRestrictedExtent(restrictedBounds);
		vlMap = new VLayout();

		int h = 700;
		vlMap.setHeight("98%");
		vlMap.setWidth100();
		vlMap.setStyleName("map-widget-borderbox");
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
				hide();
			}

		});
		savePanel.hl.addMember(new IButton("<b>Goto object<b>",
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						try {
							gotoSelectedFeature();
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				}));
		vlMain.addMember(savePanel);
		this.setHeight(h);
		this.setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.setTitle("რუკის ელემენები");
		map = mapWidget.getMap();
		map.addLayers(MapHelper.createLayers(gisMap, new Layer[] {}, true));
		map.updateSize();

		map.addControl(new PanZoomBar());
		map.addControl(new Navigation());

		modifyVector = new Vector("Modify");
		map.addLayer(modifyVector);
		drawFeature = new ModifyFeature(modifyVector);

		drawFeature.setMode(ModifyFeature.DRAG);
		map.addControl(drawFeature);
		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					if (point != null)
						point.destroy();

			}
		});

	}

	protected void saveData() {
		if (point == null) {
			SC.say("Unknown point");
			return;
		}
		WKT kml = new WKT();
		mapButton.setValue(kml.write(point));
		hide();
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

	public static native JSObject setGraphicOpacity(JSObject selectFeature,
			int graphicOpacity) /*-{
		selectFeature.graphicOpacity = graphicOpacity;
	}-*/;

	public static native JSObject setGraphicHeight(JSObject selectFeature,
			int graphicHeight) /*-{
		selectFeature.graphicHeight = graphicHeight;
	}-*/;

	public static native JSObject setGraphicWidth(JSObject selectFeature,
			int graphicWidth) /*-{
		selectFeature.graphicWidth = graphicWidth;
	}-*/;

	public static native void removeAllFeaturesAndSetStyle(
			JSObject modifyVector, JSObject stMap) /*-{
		modifyVector.removeAllFeatures();
		modifyVector.styleMap = stMap;
	}-*/;

	private void setValue(String value) {

		String icon = mapButton.getDisplayIcon();
		Style stPoint = new Style();
		stPoint.setFillOpacity(0);
		JSObject obj = stPoint.getJSObject();
		setGraphicOpacity(obj, 1);
		setGraphicHeight(obj, 25);
		setGraphicWidth(obj, 25);
		stPoint.setExternalGraphic(icon);

		StyleMap stMap = new StyleMap(stPoint, stPoint, stPoint);
		removeAllFeaturesAndSetStyle(modifyVector.getJSObject(),
				stMap.getJSObject());

		modifyVector.redraw();
		map.removeLayer(modifyVector);
		map.addLayer(modifyVector);
		if (point != null)
			point.destroy();
		point = null;
		if (value != null) {
			WKT kml = new WKT();
			try {
				VectorFeature[] vfs = kml.read(value);
				map.setCenter(vfs[0].getCenterLonLat(), 10);
				map.updateSize();
				point = vfs[0];

				// point.setStyle(stPoint);
				modifyVector.addFeature(point);
				drawFeature.activate();

				gotoSelectedFeature();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void gotoSelectedFeature() {
		DocFlow.selectFeature(drawFeature.getJSObject(), point.getJSObject(), 1);
		if (map.getZoom() <= GMapPanel.MIN_FEATURE_SELECT_LEVEL + 3)
			map.setCenter(point.getCenterLonLat(),
					GMapPanel.MIN_FEATURE_SELECT_LEVEL + 3);
		else
			map.setCenter(point.getCenterLonLat());
	}

	public void setMapButton(MapButton mapButton) {
		this.mapButton = mapButton;
		Object value = mapButton.getValue();
		boolean isNull = value == null || value.toString().trim().isEmpty();
		setValue(isNull ? null : value.toString().trim());
		siRegion.setVisible(isNull);
		siSubregion.setVisible(isNull);
		if (isNull) {
			boolean disabled = false;
			try {
				disabled = siSubregion.isDisabled();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (disabled) {
				try {
					searchBySubregion(siSubregion.getValue());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

	private void searchBySubregion(Object val) {
		if (val == null)
			return;

		String value = val.toString();
		SplashDialog.showSplash();
		DocFlow.docFlowService.getCenterCoordinates(null, value,
				Constants.GOOGLE_SRID + "", new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						SplashDialog.hideSplash();
						setValue(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();

					}
				});
	}

}
