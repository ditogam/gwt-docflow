package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WSelectParentMeters extends Window {

	private ListGrid lgListGrid;

	public WSelectParentMeters(final PParentMeter mp, Record[] records,
			boolean show_zones, final Integer subregionid,
			final Integer meter_cusid) {
		ToolStrip tsMain = new ToolStrip();
		if (show_zones && subregionid != null) {
			SelectItem cbZones = new SelectItem("zone", "Zones");
			Map<String, Object> subRegionCrit = new TreeMap<String, Object>();
			if (subregionid != null) {
				subRegionCrit.put("parentId", subregionid);
			}
			tsMain.addFormItem(cbZones);
			ClientUtils.fillSelectionCombo(cbZones, ClSelection.T_ZONES,
					subRegionCrit);
			cbZones.addChangedHandler(new ChangedHandler() {

				@Override
				public void onChanged(ChangedEvent event) {
					zonaChanged(subregionid, meter_cusid, mp, event.getValue());

				}
			});
		}

		this.setTitle("აბონენტების მიბმა საუბნო მრიცხველზე");
		lgListGrid = new ListGrid();
		mp.createListGrid(lgListGrid);

		final ToolStripButton tsbRemove = new ToolStripButton();
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (event.getSource().equals(tsbRemove))
					mp.removeData(lgListGrid, new ArrayList<String>(), false);
			}
		};

		tsbRemove.setIcon("[SKIN]/actions/remove.png");
		tsbRemove.addClickHandler(tsbStateHandler);
		tsbRemove.setTooltip("Remove data");
		tsbRemove.setActionType(SelectionType.BUTTON);
		tsbRemove.setSelected(false);
		tsMain.addButton(tsbRemove);

		lgListGrid.setHeight100();
		this.addItem(tsMain);

		this.addItem(lgListGrid);

		SavePanel savePanel = new SavePanel("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData(mp);
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}

		});
		this.addItem(savePanel);
		this.setHeight(700);
		this.setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
		mp.addData(lgListGrid, mp.meter_ids, records, false);
	}

	protected void zonaChanged(Integer subregionid, Integer meter_cusid,
			final PParentMeter mp, Object value) {
		lgListGrid.selectAllRecords();
		lgListGrid.removeSelectedData();

		java.util.Map<String, Object> criteria = new TreeMap<String, Object>();
		criteria.put("subregionid", subregionid);
		criteria.put("pmeter_cusid", meter_cusid);
		criteria.put("has_no_parent", 1);
		criteria.put("mstatusid", 1);
		criteria.put("zone", value);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				Record[] records = response.getData();
				if (records == null || records.length == 0)
					return;
				mp.addData(lgListGrid, mp.meter_ids, records, false);

			}
		};
		ClientUtils.fetchData(criteria, cb, "CustShortMeterDS", null);

	}

	protected void saveData(final PParentMeter mp) {
		try {
			lgListGrid.clearCriteria();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mp.addData(mp.lgListGrid, mp.meter_ids, lgListGrid.getRecords(), true);
		mp.highlightMeterBuildings();
		destroy();
	}

}
