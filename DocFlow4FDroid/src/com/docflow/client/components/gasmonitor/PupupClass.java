package com.docflow.client.components.gasmonitor;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.popup.Popup;

public class PupupClass {
	private Popup popup;
	private int id;
	private LonLat center;

	public PupupClass(Popup popup, int id, LonLat center) {
		this.center = center;
		this.id = id;
		this.popup = popup;

	}

	public Popup getPopup() {
		return popup;
	}

	public int getId() {
		return id;
	}

	public LonLat getCenter() {
		return center;
	}

}
