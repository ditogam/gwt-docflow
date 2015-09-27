package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.handler.PolygonHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;
import org.gwtopenmaps.openlayers.client.util.JSObject;

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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PPipeLine extends VLayout {
	private Integer spl_id;
	private Integer group_id;
	private Integer subregionid;
	private Integer regionid;
	private Integer type;
	private Map map;
	private DrawFeature drawFeatureBox;
	private DrawFeature drawFeaturePoly;
	private Record record;
	public ListGrid lgListGrid;
	public ArrayList<String> pilepine_ids;
	private TextItem tiGroupName;

	private int zoom;
	private LonLat center;

	public PPipeLine(Record record) {
		this.record = record;
		group_id = record.getAttributeAsInt("cusid");
		if (group_id != null && group_id.intValue() == 0)
			group_id = null;
		spl_id = record.getAttributeAsInt("oid");
		subregionid = record.getAttributeAsInt("raiid");
		regionid = record.getAttributeAsInt("regid");
		type = record.getAttributeAsInt("type");
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
		final ToolStripButton tsbBox = new ToolStripButton();
		final ToolStripButton tsbPoligon = new ToolStripButton();
		final ToolStripButton tsbRemove = new ToolStripButton();

		lgListGrid = new ListGrid();

		pilepine_ids = new ArrayList<String>();
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(tsbSave))
					savePipeLine();

				if (event.getSource().equals(tsbCancel))
					cancelEdit();
				if (event.getSource().equals(tsbRemove)) {
					removeData(lgListGrid, pilepine_ids, true);

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
		tsbSave.setVisible(!DocFlow.hasPermition(PermissionNames.MAP_OBJECT_SAVE_RESTRICT));
		tsbCancel.setIcon("[SKIN]/actions/close.png");
		tsbCancel.addClickHandler(tsbStateHandler);
		tsbCancel.setTooltip("Close");
		tsbCancel.setActionType(SelectionType.BUTTON);
		tsbCancel.setSelected(false);
		tsMain.addButton(tsbCancel);

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

		lgListGrid.setWidth100();
		lgListGrid.setHeight100();
		createListGrid(lgListGrid);
		tiGroupName = new TextItem("tiGroupName", "GroupName");
		tiGroupName.setWidth("100%");
		tiGroupName.setShowTitle(false);
		tiGroupName.setRequired(true);
		DynamicForm dm = new DynamicForm();
		dm.setAutoHeight();
		dm.setWidth100();
		dm.setNumCols(1);
		dm.setFields(tiGroupName);
		this.addMember(dm);
		this.addMember(lgListGrid);

		Criteria cr = new Criteria();
		if (group_id != null)
			cr.setAttribute("group_id", group_id);
		cr.setAttribute("to_srid", Constants.GOOGLE_SRID);
		cr.setAttribute("id", spl_id);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				addData(lgListGrid, response.getData(), true, tiGroupName);

			}
		};
		ClientUtils.fetchData(cr, cb, "PipeLineDS", "getPipeLinesById");
	}

	public static int addData(ListGrid lgGrid, Record[] records, boolean add, TextItem tiGroupName) {
		int len=0;
		RecordList mRecords = lgGrid.getRecordList();
		if (tiGroupName != null && records != null && records.length > 0)
			tiGroupName.setValue(records[0].getAttribute("group_name"));
		for (Record record : records) {
			Integer spl_id = record.getAttributeAsInt("spl_id");
			if (mRecords.find("spl_id", spl_id) != null)
				continue;
			len+=record.getAttributeAsInt("len");
			mRecords.add(record);
		}
		return len;
	}

	protected void removeData(final ListGrid lgGrid, final ArrayList<String> pipelene_ids, final boolean hightl) {

		final Record[] records = lgGrid.getSelectedRecords();
		if (records == null || records.length == 0)
			return;
		SC.ask("Do you want to delete?", new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					for (Record record : records) {
						Integer met_id = record.getAttributeAsInt("meterid");
						pipelene_ids.remove("" + met_id);
					}
					lgGrid.removeSelectedData();

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
		removeFromParent();
		destroy();

	}

	protected void savePipeLine() {
		if (!tiGroupName.validate())
			return;

		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				cancelEdit();
			}
		};

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		int group_id = this.group_id == null ? 0 : this.group_id.intValue();
		criteria.put("group_id", group_id);
		String sPipeLine_ids = getPipeLineIds();
		criteria.put("pipeline_ids", sPipeLine_ids);
		criteria.put("subregionid", subregionid);
		criteria.put("group_name", tiGroupName.getValueAsString().trim());
		criteria.put("type", type);
		try {
			ClientUtils.fetchData(criteria, cb, "PipeLineDS", "savePipeLines");
		} catch (Exception e) {
			SC.warn(e.getMessage());
		}

	}

	private String getPipeLineIds() {
		String sPipeLine_ids = "";
		RecordList mRecords = lgListGrid.getRecordList();
		Record records[] = mRecords.toArray();
		for (Record record : records) {
			Integer spl_id = record.getAttributeAsInt("spl_id");
			if (sPipeLine_ids.length() > 0)
				sPipeLine_ids += ",";
			sPipeLine_ids += spl_id;
		}

		return sPipeLine_ids;
	}

	protected void selectFeatures(String wf, Bounds bounds) {
		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("subregionid", subregionid);
		criteria.put("group_id", group_id);
		criteria.put("to_srid", Constants.GOOGLE_SRID);
		criteria.put("from_srid", Constants.SYSTEM_SRID);
		criteria.put("has_no_parent", 1);
		criteria.put("mstatusid", 1);
		criteria.put("type", type);
		criteria.put("selection", wf);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				Record[] records = response.getData();
				if (records == null || records.length == 0)
					return;
				new WSelectPipeLines(PPipeLine.this, records).show();
			}
		};
		ClientUtils.fetchData(criteria, cb, "PipeLineDS", "selectPipeLinesByPolygon");
	}

	public static void createListGrid(final ListGrid lgGrid) {
		lgGrid.setFields(new ListGridField("spl_id", "ID", 60), new ListGridField("title", "Name"),new ListGridField("len", "Len", 60),new ListGridField("diametri", "D", 50),new ListGridField("masala", "M", 60));
		lgGrid.setShowRowNumbers(true);
		lgGrid.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				Record rec = lgGrid.getSelectedRecord();
				if (rec == null)
					return;
				String feature_text = rec.getAttribute("feature_text");
				if (feature_text == null || feature_text.trim().isEmpty())
					return;
				try {
					JSObject stm = WAddEditFeatureDialog.setStyleMap(8, rec.getAttributeAsInt("type"));
					Style style = new Style();
					style.setJSObject(stm);
					GMapPanel.instance.addFeature(feature_text, style);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

}
