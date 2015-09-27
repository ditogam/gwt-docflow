/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
package org.oscim.view;

import java.util.List;
import java.util.TreeMap;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.view.MapView;

import com.socarmap.MainActivity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class LocationHandler {
	private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;

	private final MyLocationListener mLocationListener;
	private final LocationManager mLocationManager;
	private boolean mShowMyLocation;

	private final ToggleButton mSnapToLocationView;
	private boolean mSnapToLocation;

	/* package */final MapView mTileMap;
	int button_id;

	public LocationHandler(MapView tileMap, int button_id) {
		mTileMap = tileMap;
		this.button_id = button_id;
		mLocationManager = (LocationManager) tileMap.mapActivity
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();

		mSnapToLocationView = (ToggleButton) tileMap.mapActivity
				.findViewById(button_id);

		mSnapToLocationView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isSnapToLocationEnabled()) {
					disableSnapToLocation(true);
				} else {
					enableSnapToLocation(true);
				}
			}
		});
	}

	TreeMap<String, MyLocationListener> mpProviders = new TreeMap<String, LocationHandler.MyLocationListener>();

	@SuppressWarnings("deprecation")
	public boolean enableShowMyLocation(boolean centerAtFirstFix) {
		Log.d("TileMap", "enableShowMyLocation " + mShowMyLocation);

		gotoLastKnownPosition();

		if (!mShowMyLocation) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.NO_REQUIREMENT);
			String bestProvider = mLocationManager.getBestProvider(criteria,
					true);

			if (bestProvider == null) {
				// mTileMap.showDialog(DIALOG_LOCATION_PROVIDER_DISABLED);
				return false;
			}
			mShowMyLocation = true;
			enableMyLocation();
			Log.d("TileMap", "enableShowMyLocation " + mShowMyLocation);

			mLocationListener.setFirstCenter(centerAtFirstFix);

			mSnapToLocationView.setVisibility(View.VISIBLE);
			mTileMap.invalidate();
			return true;
		}
		return false;
	}

	public synchronized void enableMyLocation() {
		List<String> providers = mLocationManager.getAllProviders();
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0L, 0L, mLocationListener);
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			mLocationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							0L, 0L, mLocationListener);
		} else {
		}
	}

	void gotoLastKnownPosition() {
		Location currentLocation;
		Location bestLocation = null;

		for (String provider : mLocationManager.getProviders(true)) {
			currentLocation = mLocationManager.getLastKnownLocation(provider);
			if (currentLocation == null)
				continue;
			if (bestLocation == null
					|| currentLocation.getAccuracy() < bestLocation
							.getAccuracy()) {
				bestLocation = currentLocation;
			}
		}

		if (bestLocation != null) {
			MapPosition mapPosition = getLocation(bestLocation);

			mTileMap.setMapCenter(mapPosition);
			accuracy = bestLocation.getAccuracy();
			mLocation = bestLocation;
			MainActivity.instance.invalidateMap();

		} else {
			// mTileMap.showToastOnUiThread(mTileMap
			// .getString(R.string.error_last_location_unknown));
		}
	}

	private MapPosition getLocation(Location bestLocation) {
		byte zoom = mTileMap.getMapPosition().getMapPosition().zoomLevel;
		float scale = mTileMap.getMapPosition().getMapPosition().scale;
		if (zoom < 15) {
			zoom = (byte) 15;
			scale = 0;
		}

		MapPosition mapPosition = new MapPosition(bestLocation.getLatitude(),
				bestLocation.getLongitude(), zoom, scale, 0);
		return mapPosition;
	}

	/**
	 * Disables the "show my location" mode.
	 * 
	 * @return ...
	 */
	public boolean disableShowMyLocation() {
		if (mShowMyLocation) {
			mShowMyLocation = false;
			disableSnapToLocation(false);

			// mLocationManager.removeUpdates(mLocationListener);
			removeAll();
			// if (circleOverlay != null) {
			// mapView.getOverlays().remove(circleOverlay);
			// mapView.getOverlays().remove(itemizedOverlay);
			// circleOverlay = null;
			// itemizedOverlay = null;
			// }

			mSnapToLocationView.setVisibility(View.GONE);

			return true;
		}
		return false;
	}

	private void removeAll() {
		mLocationManager.removeUpdates(mLocationListener);
		// Collection<MyLocationListener> list = mpProviders.values();
		// for (MyLocationListener myLocationListener : list) {
		// mLocationManager.removeUpdates(myLocationListener);
		// }
	}

	/**
	 * Returns the status of the "show my location" mode.
	 * 
	 * @return true if the "show my location" mode is enabled, false otherwise.
	 */
	public boolean isShowMyLocationEnabled() {
		return mShowMyLocation;
	}

	/**
	 * Disables the "snap to location" mode.
	 * 
	 * @param showToast
	 *            defines whether a toast message is displayed or not.
	 */
	public void disableSnapToLocation(boolean showToast) {
		mLocation = null;
		if (mSnapToLocation) {
			mSnapToLocation = false;
			mSnapToLocationView.setChecked(false);

			mTileMap.setClickable(true);

			if (showToast) {
				// mTileMap.showToastOnUiThread(mTileMap
				// .getString(R.string.snap_to_location_disabled));
			}
		}
	}

	/**
	 * Enables the "snap to location" mode.
	 * 
	 * @param showToast
	 *            defines whether a toast message is displayed or not.
	 */
	public void enableSnapToLocation(boolean showToast) {
		if (!mSnapToLocation) {
			mSnapToLocation = true;
			mShowMyLocation = true;
			mTileMap.setClickable(false);

			if (showToast) {
				// mTileMap.showToastOnUiThread(mTileMap
				// .getString(R.string.snap_to_location_enabled));
			}
		}
	}

	/**
	 * Returns the status of the "snap to location" mode.
	 * 
	 * @return true if the "snap to location" mode is enabled, false otherwise.
	 */
	public boolean isSnapToLocationEnabled() {
		return mSnapToLocation;
	}

	Float accuracy;

	public Float getAccuracy() {
		return accuracy;
	}

	Location mLocation = null;

	public Location getLocation() {
		return mLocation;
	}

	class MyLocationListener implements LocationListener {

		private boolean mSetCenter;

		@Override
		public void onLocationChanged(Location location) {

			Log.d("LocationListener",
					"onLocationChanged, " + " lon:" + location.getLongitude()
							+ " lat:" + location.getLatitude());
			mLocation = null;
			if (!isShowMyLocationEnabled()) {
				return;
			}
			mLocation = location;
			accuracy = location.getAccuracy();

			if (mSetCenter || isSnapToLocationEnabled()) {
				mSetCenter = false;

				// mTileMap.map.setCenter(point);
				mTileMap.setMapCenter(getLocation(mLocation));
				MainActivity.instance.invalidateMap();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			try {
				mLocationManager.removeUpdates(mpProviders.get(provider));
			} catch (Throwable e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			try {
				mLocationManager.requestLocationUpdates(provider, 0L, 0L,
						mpProviders.get(provider));
			} catch (Throwable e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			mSetCenter = mSetCenter;
		}

		boolean isFirstCenter() {
			return mSetCenter;
		}

		void setFirstCenter(boolean center) {
			mSetCenter = center;
		}
	}
}
