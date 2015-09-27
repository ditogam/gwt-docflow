package com.spatialite.helpers;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyMapView extends MapView {

	private boolean motionEnabled = true;

	protected Rect mScrollableAreaLimit;

	public MyMapView(final Context context, final int tileSizePixels,
			final ResourceProxy resourceProxy,
			MapTileProviderBase tileProvider,
			final Handler tileRequestCompleteHandler, final AttributeSet attrs) {
		super(context, tileSizePixels, resourceProxy, tileProvider,
				tileRequestCompleteHandler, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyMapView(final Context context, final AttributeSet attrs) {
		this(context, 256, new DefaultResourceProxyImpl(context), null, null,
				attrs);
	}

	/**
	 * Standard Constructor.
	 */
	public MyMapView(final Context context, final int tileSizePixels) {
		this(context, tileSizePixels, new DefaultResourceProxyImpl(context));
	}

	public MyMapView(final Context context, final int tileSizePixels,
			final ResourceProxy resourceProxy) {
		this(context, tileSizePixels, resourceProxy, null);
	}

	public MyMapView(final Context context, final int tileSizePixels,
			final ResourceProxy resourceProxy,
			final MapTileProviderBase aTileProvider) {
		this(context, tileSizePixels, resourceProxy, aTileProvider, null);
	}

	public MyMapView(final Context context, final int tileSizePixels,
			final ResourceProxy resourceProxy,
			final MapTileProviderBase aTileProvider,
			final Handler tileRequestCompleteHandler) {
		this(context, tileSizePixels, resourceProxy, aTileProvider,
				tileRequestCompleteHandler, null);
	}

	public boolean isMotionEnabled() {
		return motionEnabled;
	}

	public void setMotionEnabled(boolean motionEnabled) {
		this.motionEnabled = motionEnabled;
		setMultiTouchControls(motionEnabled);
	}

	@Override
	public boolean canZoomIn() {
		if (!motionEnabled)
			return false;
		return super.canZoomIn();
	}

	@Override
	public boolean canZoomOut() {
		if (!motionEnabled)
			return false;
		return super.canZoomOut();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!motionEnabled)
			return false;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (boundsTopLeftCorner == null || boundsBottomLeftCorner == null)
				super.dispatchTouchEvent(event);
			lastCenter = getMapCenter();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (boundsTopLeftCorner == null || boundsBottomLeftCorner == null)
				super.dispatchTouchEvent(event);

			Projection proj = getProjection();
			GeoPoint screenTopLeft = (GeoPoint) proj.fromPixels(0, 0);
			GeoPoint screenBottomLeft = (GeoPoint) proj.fromPixels(getWidth(),
					getHeight());

			int screenTopLat = screenTopLeft.getLatitudeE6();
			int screenBottomLat = screenBottomLeft.getLatitudeE6();
			int screenLeftlong = screenTopLeft.getLongitudeE6();
			int screenRightlong = screenBottomLeft.getLongitudeE6();

			if (screenTopLat < mapTopLat || screenBottomLat > mapBottomLat) {
				getController().setCenter(lastCenter);
				return false;
			}
			if (screenLeftlong < mapLeftlong || screenRightlong > mapRightlong) {
				getController().setCenter(lastCenter);
				return false;
			}

			lastCenter = getMapCenter();
			return super.dispatchTouchEvent(event);
		}
		return super.dispatchTouchEvent(event);
	}

	GeoPoint boundsTopLeftCorner;
	GeoPoint boundsBottomLeftCorner;
	int mapTopLat;
	int mapBottomLat;
	int mapLeftlong;
	int mapRightlong;

	IGeoPoint lastCenter = null;

	public void setScrollableAreaLimit(GeoPoint boundsTopLeftCorner,
			GeoPoint boundsBottomLeftCorner) {

		this.boundsTopLeftCorner = boundsTopLeftCorner;

		this.boundsBottomLeftCorner = boundsBottomLeftCorner;

		mapTopLat = boundsTopLeftCorner.getLatitudeE6();
		mapBottomLat = boundsBottomLeftCorner.getLatitudeE6();
		mapLeftlong = boundsTopLeftCorner.getLongitudeE6();
		mapRightlong = boundsBottomLeftCorner.getLongitudeE6();
	}

}
