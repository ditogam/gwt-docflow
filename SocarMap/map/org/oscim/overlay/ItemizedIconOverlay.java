/*
 * Copyright 2012 osmdroid
 * Copyright 2013 Hannes Janetzek
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.overlay;

import java.util.List;

import org.oscim.core.BoundingBox;
import org.oscim.core.PointF;
import org.oscim.view.MapView;
import org.oscim.view.MapViewPosition;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

public class ItemizedIconOverlay<Item extends OverlayItem> extends
		ItemizedOverlay<Item> {
	public static interface ActiveItem {
		public boolean run(final int aIndex);
	}

	/**
	 * When the item is touched one of these methods may be invoked depending on
	 * the type of touch. Each of them returns true if the event was completely
	 * handled.
	 * 
	 * @param <T>
	 *            ....
	 */
	public static interface OnItemGestureListener<T> {
		public boolean onItemLongPress(final int index, final T item);

		public boolean onItemSingleTapUp(final int index, final T item);
	}

	@SuppressWarnings("unused")
	private static final String TAG = ItemizedIconOverlay.class.getName();
	protected final List<Item> mItemList;

	protected OnItemGestureListener<Item> mOnItemGestureListener;

	private int mDrawnItemsLimit = Integer.MAX_VALUE;

	// public ItemizedIconOverlay(
	// final MapView mapView,
	// final Context pContext,
	// final List<Item> pList,
	// final ItemizedIconOverlay.OnItemGestureListener<Item>
	// pOnItemGestureListener) {
	// this(mapView, pList,
	// pContext.getResources().getDrawable(R.drawable.marker_default),
	// pOnItemGestureListener);
	// }

	private final PointF mItemPoint = new PointF();

	private final ActiveItem mActiveItemSingleTap = new ActiveItem() {
		@Override
		public boolean run(final int index) {
			final ItemizedIconOverlay<Item> that = ItemizedIconOverlay.this;
			if (that.mOnItemGestureListener == null) {
				return false;
			}
			return onSingleTapUpHelper(index, that.mItemList.get(index));
		}
	};

	private final ActiveItem mActiveItemLongPress = new ActiveItem() {
		@Override
		public boolean run(final int index) {
			final ItemizedIconOverlay<Item> that = ItemizedIconOverlay.this;
			if (that.mOnItemGestureListener == null) {
				return false;
			}
			return onLongPressHelper(index, getItem(index));
		}
	};

	public ItemizedIconOverlay(
			final MapView mapView,
			final List<Item> pList,
			final Drawable pDefaultMarker,
			final ItemizedIconOverlay.OnItemGestureListener<Item> pOnItemGestureListener) {

		super(mapView, pDefaultMarker);

		this.mItemList = pList;
		this.mOnItemGestureListener = pOnItemGestureListener;
		populate();
	}

	/**
	 * When a content sensitive action is performed the content item needs to be
	 * identified. This method does that and then performs the assigned task on
	 * that item.
	 * 
	 * @param event
	 *            ...
	 * @param task
	 *            ..
	 * @return true if event is handled false otherwise
	 */
	private boolean activateSelectedItems(MotionEvent event, ActiveItem task) {
		int size = mItemList.size();
		if (size == 0)
			return false;

		int eventX = (int) event.getX() - mMapView.getWidth() / 2;
		int eventY = (int) event.getY() - mMapView.getHeight() / 2;
		MapViewPosition mapViewPosition = mMapView.getMapViewPosition();

		BoundingBox bbox = mapViewPosition.getViewBox();

		int nearest = -1;
		double dist = Double.MAX_VALUE;

		for (int i = 0; i < size; i++) {
			Item item = getItem(i);
			if (!bbox.contains(item.mGeoPoint)) {
				// Log.d(TAG, "skip: " + item.getTitle());
				continue;
			}
			// final Drawable marker = (item.getMarker(0) == null) ?
			// this.mDefaultMarker : item
			// .getMarker(0);

			// TODO use intermediate projection
			mapViewPosition.project(item.getPoint(), mItemPoint);

			float dx = mItemPoint.x - eventX;
			float dy = mItemPoint.y - eventY;

			// Log.d(TAG, item.getTitle() + " " + mItemPoint + " " + dx + "/" +
			// dy);

			double d = dx * dx + dy * dy;

			// squared dist: 50*50 pixel
			if (d < 2500) {
				if (d < dist) {
					dist = d;
					nearest = i;
				}
			}
		}

		if (nearest >= 0 && task.run(nearest)) {
			return true;
		}

		return false;
	}

	public void addItem(final int location, final Item item) {
		mItemList.add(location, item);
	}

	public boolean addItem(final Item item) {
		final boolean result = mItemList.add(item);
		populate();
		return result;
	}

	public boolean addItems(final List<Item> items) {
		final boolean result = mItemList.addAll(items);
		populate();
		return result;
	}

	@Override
	protected Item createItem(final int index) {
		return mItemList.get(index);
	}

	public int getDrawnItemsLimit() {
		return this.mDrawnItemsLimit;
	}

	@Override
	public boolean onLongPress(final MotionEvent event) {
		return activateSelectedItems(event, mActiveItemLongPress)
				|| super.onLongPress(event);
	}

	protected boolean onLongPressHelper(final int index, final Item item) {
		return this.mOnItemGestureListener.onItemLongPress(index, item);
	}

	/**
	 * Each of these methods performs a item sensitive check. If the item is
	 * located its corresponding method is called. The result of the call is
	 * returned. Helper methods are provided so that child classes may more
	 * easily override behavior without resorting to overriding the
	 * ItemGestureListener methods.
	 */
	@Override
	public boolean onSingleTapUp(final MotionEvent event) {
		return activateSelectedItems(event, mActiveItemSingleTap)
				|| super.onSingleTapUp(event);
	}

	protected boolean onSingleTapUpHelper(final int index, final Item item) {
		return this.mOnItemGestureListener.onItemSingleTapUp(index, item);
	}

	@Override
	public boolean onSnapToItem(final int pX, final int pY,
			final PointF pSnapPoint) {
		// TODO Implement this!
		return false;
	}

	public void removeAllItems() {
		removeAllItems(true);
	}

	public void removeAllItems(final boolean withPopulate) {
		mItemList.clear();
		if (withPopulate) {
			populate();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Item removeItem(final int position) {
		final Item result = mItemList.remove(position);
		populate();
		return result;
	}

	public boolean removeItem(final Item item) {
		final boolean result = mItemList.remove(item);
		populate();
		return result;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public void setDrawnItemsLimit(final int aLimit) {
		this.mDrawnItemsLimit = aLimit;
	}

	@Override
	public int size() {
		return Math.min(mItemList.size(), mDrawnItemsLimit);
	}
}
