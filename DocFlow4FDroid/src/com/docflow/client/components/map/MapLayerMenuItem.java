package com.docflow.client.components.map;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

import com.common.shared.map.GisLayer;
import com.common.shared.map.GisLayerFilter;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class MapLayerMenuItem extends MenuItem {

	private int id;
	private String sql_filter;
	private boolean atleast_one_is_selected = false;

	class MapLayerMenuSubItem extends MenuItem {
		String filter;

		public MapLayerMenuSubItem(String title, String filter) {
			super(title);
			this.filter = filter;
		}
	}

	public MapLayerMenuItem(GisLayer gisLayer, final Layer layer, final Map map) {
		super(gisLayer.getTitle());
		id = gisLayer.getId();
		if (gisLayer.getGisLayerFilters() == null
				|| gisLayer.getGisLayerFilters().isEmpty()) {
			setChecked(gisLayer.isShowinmap());
			atleast_one_is_selected = gisLayer.isShowinmap();
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(MenuItemClickEvent event) {
					boolean ch = getChecked();
					layer.setIsVisible(!ch);
					setChecked(!ch);
					atleast_one_is_selected = !ch;
				}
			});
		} else {
			ArrayList<GisLayerFilter> filters = gisLayer.getGisLayerFilters();
			atleast_one_is_selected = gisLayer.isShowinmap();
			final MenuItem miAll = new MenuItem("All");
			miAll.setChecked(gisLayer.isShowinmap());
			final ArrayList<MapLayerMenuSubItem> subitems = new ArrayList<MapLayerMenuSubItem>();
			final Menu m = new Menu();
			ClickHandler h = new ClickHandler() {
				@Override
				public void onClick(MenuItemClickEvent event) {
					boolean ch = event.getItem().getChecked();
					event.getItem().setChecked(!ch);
					if (event.getItem().equals(miAll)) {
						for (MapLayerMenuSubItem mi : subitems) {
							mi.setChecked(!ch);
						}
					}
					setFilter(miAll, subitems, map, layer);
					m.redraw();
				}
			};
			miAll.addClickHandler(h);

			for (GisLayerFilter f : filters) {
				MapLayerMenuSubItem mi = new MapLayerMenuSubItem(f.getTitle(),
						f.getFilter_text());
				mi.setChecked(gisLayer.isShowinmap());
				subitems.add(mi);
				mi.addClickHandler(h);
			}

			MenuItem[] subMenuItems = new MenuItem[filters.size() + 1];
			subMenuItems[0] = miAll;
			for (int i = 1; i < subMenuItems.length; i++) {
				subMenuItems[i] = subitems.get(i - 1);
			}

			m.setItems(subMenuItems);
			this.setSubmenu(m);
		}
	}

	protected void setFilter(MenuItem miAll,
			ArrayList<MapLayerMenuSubItem> subitems, Map map, Layer layer) {
		boolean allcheked = true;
		boolean atleastonecheked = false;

		String filter = "";
		for (MapLayerMenuSubItem mi : subitems) {
			boolean ch = mi.getChecked();
			if (!ch)
				allcheked = false;
			else
				atleastonecheked = true;
			if (ch)
				filter += (filter.isEmpty() ? "" : (" OR ")) + "(" + mi.filter
						+ ")";
		}
		if (allcheked) {
			miAll.setChecked(true);
			filter = "";
		} else
			miAll.setChecked(false);

		layer.setIsVisible(atleastonecheked);
		atleast_one_is_selected = atleastonecheked;
		if (atleastonecheked) {
			WMS wms = WMS.narrowToWMS(layer.getJSObject());
			WMSParams params = new WMSParams();
			params.setCQLFilter(filter.isEmpty() ? null : filter);
			wms.mergeNewParams(params);
			wms.redraw(true);
			map.addLayer(layer);
		}
		sql_filter = filter;
	}

	public int getLayerId() {
		return id;
	}

	public String getSql_filter() {
		return sql_filter == null ? "" : sql_filter.trim();
	}

	public boolean isAtleast_one_is_selected() {
		return atleast_one_is_selected;
	}

}
