package com.docflowdroid.comp.adapter;

import java.util.List;

import org.oscim.overlay.ItemizedIconOverlay;
import org.oscim.overlay.OverlayItem;
import org.oscim.view.MapView;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * An itemized overlay with an InfoWindow or "bubble" which opens when the user
 * taps on an overlay item, and displays item attributes. <br>
 * Items must be ExtendedOverlayItem. <br>
 * 
 * @see ExtendedOverlayItem
 * @see InfoWindow
 * @author M.Kergall
 * @param <Item>
 *            ...
 */
public class ItemizedOverlayWithBubble<Item extends OverlayItem> extends
		ItemizedIconOverlay<Item> implements
		ItemizedIconOverlay.OnItemGestureListener<Item> {

	protected List<Item> mItemsList;

	// the item currently showing the bubble. Null if none.
	protected OverlayItem mItemWithBubble;

	static int layoutResId = 0;

	public ItemizedOverlayWithBubble(final MapView mapView,
			final Context context, final List<Item> aList, Drawable drawable) {

		// super(mapView, pList, pDefaultMarker, pOnItemGestureListener)

		super(mapView, aList, drawable, null);

		mItemsList = aList;

		mItemWithBubble = null;

		mOnItemGestureListener = this;
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
