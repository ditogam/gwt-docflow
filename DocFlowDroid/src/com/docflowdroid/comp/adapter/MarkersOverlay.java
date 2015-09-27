package com.docflowdroid.comp.adapter;

import java.util.List;

import org.oscim.overlay.ItemizedIconOverlay;
import org.oscim.overlay.OverlayItem;
import org.oscim.view.MapView;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class MarkersOverlay<Item extends OverlayItem> extends
		ItemizedIconOverlay<Item> implements
		ItemizedIconOverlay.OnItemGestureListener<Item> {

	public MarkersOverlay(Drawable drawable, MapView mapv,
			Activity mainActivity, List<Item> aList) {
		super(mapv, aList, drawable, null);
		for (Item overlayItem : aList) {
			overlayItem.setMarker(drawable);
			addItem(overlayItem);
		}
		populate();
		mapv.redrawMap(true);

	}

	@Override
	public boolean onItemLongPress(int index, Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onItemSingleTapUp(int index, Item item) {
		// TODO Auto-generated method stub
		return false;
	}

}
