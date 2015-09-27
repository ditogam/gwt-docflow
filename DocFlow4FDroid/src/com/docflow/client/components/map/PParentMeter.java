package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.handler.PolygonHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.shared.PermissionNames;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PParentMeter extends VLayout {
	private PDistrictMeterForm meterForm;
	private Integer meter_cusid;
	private Integer subregionid;
	private Map map;
	private DrawFeature drawFeatureBox;
	private DrawFeature drawFeaturePoly;
	private Record record;
	public ListGrid lgListGrid;
	public ArrayList<String> meter_ids;

	private int zoom;
	private LonLat center;

	public PParentMeter(Record record) {
		this.record = record;
		meterForm = new PDistrictMeterForm(record, false, 1);
		meter_cusid = record.getAttributeAsInt("cusid");
		subregionid = record.getAttributeAsInt("raiid");
		map = GMapPanel.instance.mapViewerPanel.getMap();
		zoom = map.getZoom();
		center = map.getCenter();
		this.setWidth(400);
		GMapPanel.instance.mapViewerPanel.setShowResizeBar(true);
		GMapPanel.instance.mapViewerPanel.getToolStrip().disable();
		GMapPanel.instance.addMember(this);

		FeatureAddedListener featureAddedListener = new FeatureAddedListener() {

			@Override
			public void onFeatureAdded(VectorFeature vectorFeature) {
				WKT kml = new WKT();
				String wf = kml.write(vectorFeature);
				selectFeatures(wf, vectorFeature.getGeometry().getBounds());
				GMapPanel.instance.buildingLayer.destroyFeatures();

			}
		};
		GMapPanel.instance.drawFeature.deactivate();

		DrawFeatureOptions dfo = new DrawFeatureOptions();
		dfo.onFeatureAdded(featureAddedListener);
		// RegularPolygonHandlerOptions handlerOptions = new
		// RegularPolygonHandlerOptions();
		RegularPolygonHandler h = new RegularPolygonHandler();
		// h.setOptions(handlerOptions);
		// handlerOptions.setSides(4);
		drawFeatureBox = new DrawFeature(GMapPanel.instance.buildingLayer, h, dfo);
		map.addControl(drawFeatureBox);
		drawFeatureBox.activate();

		DrawFeatureOptions dfoPoli = new DrawFeatureOptions();
		dfoPoli.onFeatureAdded(featureAddedListener);

		PolygonHandler handlerP = new PolygonHandler();

		drawFeaturePoly = new DrawFeature(GMapPanel.instance.buildingLayer, handlerP, dfoPoli);
		map.addControl(drawFeaturePoly);

		ToolStrip tsMain = new ToolStrip();
		final ToolStripButton tsbSave = new ToolStripButton();
		final ToolStripButton tsbCancel = new ToolStripButton();
		final ToolStripButton tsbAdd = new ToolStripButton();
		final ToolStripButton tsbBox = new ToolStripButton();
		final ToolStripButton tsbPoligon = new ToolStripButton();
		final ToolStripButton tsbRemove = new ToolStripButton();
		tsbSave.setVisible(!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_SAVE_RESTRICT));
		lgListGrid = new ListGrid();
		meter_ids = new ArrayList<String>();
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(tsbSave))
					saveParentMetter();
				if (event.getSource().equals(tsbAdd))
					new WSelectParentMeters(PParentMeter.this, new Record[] {}, true, subregionid, meter_cusid).show();

				if (event.getSource().equals(tsbCancel))
					cancelEdit();
				if (event.getSource().equals(tsbRemove)) {
					removeData(lgListGrid, meter_ids, true);

				}
				if (event.getSource().equals(tsbBox) || event.getSource().equals(tsbPoligon)) {
					drawFeatureBox.deactivate();
					drawFeaturePoly.deactivate();
					if (event.getSource().equals(tsbBox))
						drawFeatureBox.activate();
					if (event.getSource().equals(tsbPoligon))
						drawFeaturePoly.activate();
				}

			}
		};

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

		tsbAdd.setIcon("[SKIN]/actions/add.png");
		tsbAdd.addClickHandler(tsbStateHandler);
		tsbAdd.setTooltip("Add");
		tsbAdd.setActionType(SelectionType.BUTTON);
		tsbAdd.setSelected(false);
		tsMain.addButton(tsbAdd);

		tsMain.addSeparator();

		tsbBox.setIcon("map/resize_reshape.jpg");
		tsbBox.addClickHandler(tsbStateHandler);
		tsbBox.setTooltip("Box");
		tsbBox.setActionType(SelectionType.RADIO);
		tsbBox.setRadioGroup("selection");
		tsbBox.setSelected(true);
		tsMain.addButton(tsbBox);

		tsbPoligon.setIcon("map/rotate.jpg");
		tsbPoligon.addClickHandler(tsbStateHandler);
		tsbPoligon.setTooltip("Polygon");
		tsbPoligon.setActionType(SelectionType.RADIO);
		tsbPoligon.setRadioGroup("selection");
		tsbPoligon.setSelected(false);
		tsMain.addButton(tsbPoligon);
		tsMain.setWidth100();

		tsMain.addSeparator();

		tsbRemove.setIcon("[SKIN]/actions/remove.png");
		tsbRemove.addClickHandler(tsbStateHandler);
		tsbRemove.setTooltip("Remove data");
		tsbRemove.setActionType(SelectionType.BUTTON);
		tsbRemove.setSelected(false);
		tsMain.addButton(tsbRemove);

		this.addMember(tsMain);
		this.addMember(meterForm);

		lgListGrid.setWidth100();
		lgListGrid.setHeight100();
		createListGrid(lgListGrid);
		this.addMember(lgListGrid);

		Criteria cr = new Criteria();
		cr.setAttribute("pmeter_cusid", meter_cusid);
		cr.setAttribute("subregionid", record.getAttributeAsInt("raiid"));
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				addData(lgListGrid, meter_ids, response.getData(), true);
				highlightMeterBuildings();

			}
		};
		ClientUtils.fetchData(cr, cb, "CustShortMeterDS", null);
	}

	public void addData(ListGrid lgGrid, ArrayList<String> metids, Record[] records, boolean add) {
		RecordList mRecords = lgGrid.getRecordList();
		for (Record record : records) {
			Integer met_id = record.getAttributeAsInt("meterid");
			if (metids.contains(met_id + ""))
				continue;
			mRecords.add(record);
			if (add)
				metids.add(met_id + "");
		}
	}

	protected void removeData(final ListGrid lgGrid, final ArrayList<String> meter_ids, final boolean hightl) {

		final Record[] records = lgGrid.getSelectedRecords();
		if (records == null || records.length == 0)
			return;
		SC.ask("Do you want to delete?", new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					for (Record record : records) {
						Integer met_id = record.getAttributeAsInt("meterid");
						meter_ids.remove("" + met_id);
					}
					lgGrid.removeSelectedData();
					if (hightl)
						highlightMeterBuildings();
				}

			}
		});

	}

	protected void cancelEdit() {
		drawFeatureBox.deactivate();
		drawFeaturePoly.deactivate();
		map.removeControl(drawFeatureBox);
		map.removeControl(drawFeaturePoly);
		GMapPanel.instance.buildingLayer.destroyFeatures();
		GMapPanel.instance.mapViewerPanel.setShowResizeBar(false);
		GMapPanel.instance.mapViewerPanel.getToolStrip().enable();
		map.setCenter(center, zoom);
		meter_ids = new ArrayList<String>();
		highlightMeterBuildings();
		removeFromParent();
		destroy();

	}

	protected void saveParentMetter() {

		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				cancelEdit();
			}
		};

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("meter_cusid", meter_cusid);
		String sMeter_ids = getMeterIds();
		criteria.put("meter_ids", sMeter_ids);
		try {
			ClientUtils.fetchData(criteria, cb, "CustShortMeterDS", "saveParentMeter");
		} catch (Exception e) {
			SC.warn(e.getMessage());
		}

	}

	private String getMeterIds() {
		String sMeter_ids = "";
		for (String meter : meter_ids) {
			if (sMeter_ids.length() > 0)
				sMeter_ids += ",";
			sMeter_ids += meter;
		}
		return sMeter_ids;
	}

	protected void selectFeatures(String wf, Bounds bounds) {
		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("subregionid", record.getAttributeAsInt("raiid"));
		criteria.put("pmeter_cusid", meter_cusid);
		criteria.put("to_srid", Constants.GOOGLE_SRID);
		criteria.put("from_srid", Constants.SYSTEM_SRID);
		criteria.put("has_no_parent", 1);
		criteria.put("mstatusid", 1);
		criteria.put("selection", wf);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				Record[] records = response.getData();
				if (records == null || records.length == 0)
					return;
				new WSelectParentMeters(PParentMeter.this, records, false, null, null).show();

			}
		};
		ClientUtils.fetchData(criteria, cb, "CustShortMeterDS", "fetchBuildCustomersResult");
	}

	public void createListGrid(final ListGrid lgGrid) {
		lgGrid.setFields(new ListGridField("cusid", "CusID", 60), new ListGridField("cusname", "Name", 200),
				new ListGridField("cityname", "City", 200), new ListGridField("streetname", "Street", 200),
				new ListGridField("mtypename", "Meter type", 200), new ListGridField("metserial", "Serial", 200),
				new ListGridField("meter_status", "Status", 200), new ListGridField("zone", "Zone", 200));
		lgGrid.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				Record rec = lgGrid.getSelectedRecord();
				if (rec == null)
					return;
				GMapPanel.instance.findCustomerObject(rec);

			}
		});
	}

	public void highlightMeterBuildings() {
		// if (meter_ids.isEmpty())
		// return;
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getData() == null || response.getData().length == 0)
					return;
				String wkt = response.getData()[0].getAttribute("feature_text");
				GMapPanel.instance.setCusSearchResult(wkt);
			}
		};

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("srid", Constants.GOOGLE_SRID);
		criteria.put("uuid", DocFlow.user_obj.getUuid());
		String sMeter_ids = getMeterIds();
		criteria.put("meter_ids", sMeter_ids);
		ClientUtils.fetchData(criteria, cb, "CustShortMeterDS", "fetchMeterBuildins");
	}

}
