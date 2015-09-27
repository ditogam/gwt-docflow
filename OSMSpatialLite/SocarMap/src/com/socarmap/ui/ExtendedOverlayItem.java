package com.socarmap.ui;

import org.oscim.core.GeoPoint;
import org.oscim.overlay.OverlayItem;
import org.oscim.view.MapView;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * An OverlayItem to use in ItemizedOverlayWithBubble<br>
 * - more complete: can contain an image and a sub-description that will be
 * displayed in the bubble, <br>
 * - and flexible: attributes are modifiable<br>
 * Known Issues:<br>
 * - Bubble offset is not perfect on h&xhdpi resolutions, due to an osmdroid
 * issue on marker drawing<br>
 * - Bubble offset is at 0 when using the default marker => set the marker on
 * each item!<br>
 * 
 * @see ItemizedOverlayWithBubble
 * @author M.Kergall
 */
public class ExtendedOverlayItem extends OverlayItem {

	// now, they are modifiable
	private String mTitle, mDescription;
	// now, they are modifiable
	// a third field that can be displayed in
	// the infowindow, on a third line
	// that will be shown in the infowindow.
	// unfortunately, this is not so simple...
	private String mSubDescription;
	private Drawable mImage;
	private Object mRelatedObject; // reference to an object (of any kind)
									// linked to this item.

	public ExtendedOverlayItem(String aTitle, String aDescription,
			GeoPoint aGeoPoint) {
		super(aTitle, aDescription, aGeoPoint);
		mTitle = aTitle;
		mDescription = aDescription;
		mSubDescription = null;
		mImage = null;
		mRelatedObject = null;
	}

	public String getDescription() {
		return mDescription;
	}

	/**
	 * From a HotspotPlace and drawable dimensions (width, height), return the
	 * hotspot position. Could be a public method of HotspotPlace or
	 * OverlayItem...
	 * 
	 * @param place
	 *            ...
	 * @param w
	 *            ...
	 * @param h
	 *            ...
	 * @return ...
	 */
	public Point getHotspot(HotspotPlace place, int w, int h) {
		Point hp = new Point();
		if (place == null)
			place = HotspotPlace.BOTTOM_CENTER; // use same default than in
												// osmdroid.
		switch (place) {
		case NONE:
			hp.set(0, 0);
			break;
		case BOTTOM_CENTER:
			hp.set(w / 2, 0);
			break;
		case LOWER_LEFT_CORNER:
			hp.set(0, 0);
			break;
		case LOWER_RIGHT_CORNER:
			hp.set(w, 0);
			break;
		case CENTER:
			hp.set(w / 2, -h / 2);
			break;
		case LEFT_CENTER:
			hp.set(0, -h / 2);
			break;
		case RIGHT_CENTER:
			hp.set(w, -h / 2);
			break;
		case TOP_CENTER:
			hp.set(w / 2, -h);
			break;
		case UPPER_LEFT_CORNER:
			hp.set(0, -h);
			break;
		case UPPER_RIGHT_CORNER:
			hp.set(w, -h);
			break;
		}
		return hp;
	}

	public Drawable getImage() {
		return mImage;
	}

	public Object getRelatedObject() {
		return mRelatedObject;
	}

	public String getSubDescription() {
		return mSubDescription;
	}

	@Override
	public String getTitle() {
		return mTitle;
	}

	public void setDescription(String aDescription) {
		mDescription = aDescription;
	}

	public void setImage(Drawable anImage) {
		mImage = anImage;
	}

	public void setRelatedObject(Object o) {
		mRelatedObject = o;
	}

	public void setSubDescription(String aSubDescription) {
		mSubDescription = aSubDescription;
	}

	public void setTitle(String aTitle) {
		mTitle = aTitle;
	}

	/**
	 * Populates this bubble with all item info:
	 * <ul>
	 * title and description in any case,
	 * </ul>
	 * <ul>
	 * image and sub-description if any.
	 * </ul>
	 * and centers the map on the item. <br>
	 * 
	 * @param bubble
	 *            ...
	 * @param mapView
	 *            ...
	 */
	public void showBubble(InfoWindow bubble, MapView mapView) {
		// offset the bubble to be top-centered on the marker:
		Drawable marker = getMarker(0 /* OverlayItem.ITEM_STATE_FOCUSED_MASK */);
		int markerWidth = 0, markerHeight = 0;
		if (marker != null) {
			markerWidth = marker.getIntrinsicWidth();
			markerHeight = marker.getIntrinsicHeight();
		} // else... we don't have the default marker size => don't user default
			// markers!!!
		Point markerH = getHotspot(getMarkerHotspot(), markerWidth,
				markerHeight);
		Point bubbleH = getHotspot(HotspotPlace.TOP_CENTER, markerWidth,
				markerHeight);
		bubbleH.offset(-markerH.x, -markerH.y);

		bubble.open(this, bubbleH.x, bubbleH.y);
	}
}
