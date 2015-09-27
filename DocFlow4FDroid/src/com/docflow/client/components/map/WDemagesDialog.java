package com.docflow.client.components.map;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.format.WKT;
import org.gwtopenmaps.openlayers.client.layer.Markers;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

public class WDemagesDialog extends Window {

	private Date currentDate;
	private DataSource dsCalendar;
	private DynamicForm dfMain;
	private DateTimeFormat dFormatter = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat timeFormatter = DateTimeFormat
			.getFormat("dd/MM/yyyy HH:mm");
	private ListGrid listGrid;
	private TileGrid tileGrid;
	private SavePanel savePanel;
	private IButton showBtn;

	private Markers markers;
	private Marker marker;

	public WDemagesDialog() {
		markers = new Markers("Demages");
		GMapPanel.instance.mapViewerPanel.getMap().addLayer(markers);
		this.setTitle("დაზიანებები/ავარიები");
		dsCalendar = DocFlow.getDataSource("Demage_DescriptionDS");

		savePanel = new SavePanel("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setTime(currentDate);
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dfMain.clearValues();
				setTime(currentDate);
			}

		});

		savePanel.cancelBtn.setTitle("Clear");
		savePanel.saveBtn.setTitle("Search");
		showBtn = new IButton("Show");
		showBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showForm();

			}
		});
		showBtn.setVisible(false);
		savePanel.addItem(showBtn);

		dfMain = new DynamicForm();
		dfMain.setWidth100();
		dfMain.setHeight("10%");
		dfMain.setTitleOrientation(TitleOrientation.TOP);
		dfMain.setNumCols(4);

		listGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("point")) {
					IButton ib = new IButton("");
					ib.setIcon("[SKIN]/pickers/search_picker.png");
					ib.setWidth("20");
					ib.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							showPointOnMap(record);

						}

					});
					return ib;
				}
				return super.createRecordComponent(record, colNum);
			}
		};
		listGrid.setWidth100();
		listGrid.setHeight("50%");
		listGrid.setAutoFetchData(false);
		listGrid.setDataSource(dsCalendar);
		listGrid.setShowRecordComponents(true);
		listGrid.setShowRecordComponentsByCell(true);
		listGrid.setFields(new ListGridField("demage_type_name", "Type"),
				new ListGridField("ppcityname", "Region"), new ListGridField(
						"pcityname", "Subregion"), new ListGridField(
						"username", "User"), new ListGridField("rectime",
						"Registration"), new ListGridField("demage_time",
						"Event time"), new ListGridField("point", "", 30));

		SelectItem siRegion;
		SelectItem siSubregion;

		siRegion = new SelectItem("regid", DocFlow.getCaption(69));
		siSubregion = new SelectItem("pcityid", "Subregion");

		Record record = new Record();
		if (DocFlow.user_obj.getUser().getRegionid() >= 1)
			record.setAttribute("regid", DocFlow.user_obj.getUser()
					.getRegionid());

		if (DocFlow.user_obj.getUser().getSubregionid() >= 1)
			record.setAttribute("raiid", DocFlow.user_obj.getUser()
					.getSubregionid());

		Integer regid = record.getAttributeAsInt("regid");
		Integer subregionid = record.getAttributeAsInt("raiid");
		Map<String, Object> regionCrit = new TreeMap<String, Object>();

		if (regid != null) {
			regionCrit.put("parentId", regid);
			siRegion.setDisabled(true);
		}

		Map<String, Object> raionCrit = new TreeMap<String, Object>();
		if (subregionid != null) {
			raionCrit.put("parentId", subregionid);
			if (regid != null)
				siSubregion.setDisabled(true);
		}
		siRegion.setWidth("200");
		siSubregion.setWidth("200");
		ClientUtils.fillSelectionCombo(siRegion, ClSelection.T_REGION);
		ClientUtils.fillSelectionCombo(siSubregion, ClSelection.T_SUBREGION,
				regionCrit);
		ClientUtils.makeDependancy(siRegion, true, new FormItemDescr(
				siSubregion));
		SelectItem siDemageType = new SelectItem("demage_type", "Demage type");
		ClientUtils
				.fillSelectionCombo(siDemageType, ClSelection.T_DEMAGE_TYPES);

		currentDate = new Date();
		siDemageType.setWidth("200");
		dfMain.setFields(new StaticTextItem("mytime", "Date"), siDemageType,
				siRegion, siSubregion);

		PickerIcon searchPicker = new PickerIcon(new Picker("date.png"),
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						Criteria cr = createCriteria();
						WCalendarDialog.showWindow(cr, WDemagesDialog.this);
					}
				});
		dfMain.getField("mytime").setIcons(searchPicker);
		this.addItem(dfMain);

		CellFormatter cf = new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if (value != null) {

					try {
						Date dateValue = (Date) value;
						return timeFormatter.format(dateValue);
					} catch (Exception e) {
						return value.toString();
					}
				} else {
					return "";
				}
			}
		};
		listGrid.getField("rectime").setCellFormatter(cf);
		listGrid.getField("demage_time").setCellFormatter(cf);

		tileGrid = new TileGrid();
		tileGrid.setTileWidth(194);
		tileGrid.setTileHeight(165);
		tileGrid.setHeight("50%");
		tileGrid.setWidth100();
		tileGrid.setCanReorderTiles(true);
		tileGrid.setShowAllRecords(true);
		tileGrid.setSelectionType(SelectionStyle.SINGLE);
		tileGrid.setAnimateTileChange(false);
		tileGrid.setAutoFetchData(false);
		DetailViewerField pictureField = new DetailViewerField("file_data");
		pictureField.setType("imageFile");

		pictureField.setImageWidth(186);
		pictureField.setImageHeight(120);

		tileGrid.setFields(pictureField);
		tileGrid.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				Record record = tileGrid.getSelectedRecord();
				if (record == null)
					return;
				String _url = "fileDownload?id=" + record.getAttribute("id");
				com.google.gwt.user.client.Window
						.open(_url, "yourWindowName",
								"location=yes,resizable=yes,scrollbars=yess,status=yes");

			}
		});

		tileGrid.setDataSource(DocFlow
				.getDataSource("Demage_Description_FilesDS"));

		listGrid.addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Criteria cr = new Criteria();
				cr.setAttribute("description_id", event.getSelectedRecord()
						.getAttribute("id"));
				tileGrid.fetchData(cr);

			}
		});

		setTime(currentDate);
		this.addItem(savePanel);
		this.addItem(listGrid);
		this.addItem(tileGrid);
		this.setHeight(800);
		this.setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(false);
		this.setShowFooter(true);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {

					destroy();
					try {
						GMapPanel.instance.mapViewerPanel.getMap().removeLayer(
								markers);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		});
	}

	private int formheight = 0;

	protected void showForm() {
		setShowPanels(true);
		setHeight(formheight);
	}

	public void setTime(Date currentDate) {
		this.currentDate = currentDate;
		dfMain.setValue("mytime", dFormatter.format(currentDate));
		listGrid.fetchData(createCriteria());

	}

	protected void saveData() {

	}

	private void showPointOnMap(ListGridRecord record) {
		String attribute = record.getAttribute("feature_text");
		if (attribute == null)
			return;

		WKT kml = new WKT();
		VectorFeature[] features = kml.read(attribute);
		if (features == null || features.length != 1)
			return;

		Bounds bounds = features[0].getGeometry().getBounds();
		if (marker != null)
			markers.removeMarker(marker);
		Icon icon = new Icon("images/fire.png", new Size(24, 24));
		marker = new Marker(bounds.getCenterLonLat(), icon);
		markers.addMarker(marker);
		int zoom = GMapPanel.instance.mapViewerPanel.getMap().getZoom();
		zoom = zoom < 12 ? 12 : zoom;
		GMapPanel.instance.mapViewerPanel.getMap().setCenter(
				bounds.getCenterLonLat(), zoom);
		formheight = getHeight();
		setShowPanels(false);
		setHeight(120);

	}

	private void setShowPanels(boolean show) {
		savePanel.cancelBtn.setVisible(show);
		savePanel.saveBtn.setVisible(show);
		showBtn.setVisible(!show);
		listGrid.setVisible(show);
		tileGrid.setVisible(show);
		setIsModal(show);
	}

	private Criteria createCriteria() {
		Criteria cr = dfMain.getValuesAsCriteria();
		cr.setAttribute("demage_time", currentDate);
		cr.setAttribute("to_srid", Constants.GOOGLE_SRID);
		return cr;
	}

}
