package com.docflow.client.components.map;

import com.common.shared.map.GisLayer;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class MapToolStripButton extends ToolStripButton implements
		Comparable<MapToolStripButton> {
	private GisLayer layer;

	public MapToolStripButton() {
		super();
	}

	public GisLayer getLayer() {
		return layer;
	}

	public void setLayer(GisLayer layer) {
		this.layer = layer;
	}

	@Override
	public int compareTo(MapToolStripButton o) {
		if (o == null)
			return 0;
		return o.getID().compareTo(this.getID());
	}
}
