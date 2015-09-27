/*
 * Copyright 2012 Hannes Janetzek
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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass {

	public static float add_degree = 0;

	private final SensorEventListener mListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (Math.abs(event.values[0] - mAngle) > 0.25) {
				mAngle = event.values[0];
				System.err.println("Compas angle" + event.values[0]);
				if (mMapView != null) {
					mMapView.getMapPosition().setRotation(-(getAngle()));
					mMapView.redrawMap(true);
					mMapView.mapActivity.invalidateMap();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	/* package */float mAngle = 0;
	/* package */MapView mMapView;

	private final SensorManager mSensorManager;
	private final Sensor mSensor;

	public Compass(MapActivity mapActivity, MapView mapView) {
		mMapView = mapView;
		mSensorManager = (SensorManager) mapActivity
				.getSystemService(Context.SENSOR_SERVICE);

		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	void enable() {
		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_UI);
	}

	void disable() {

		mSensorManager.unregisterListener(mListener);
		mMapView.getMapPosition().setRotation(0);
	}

	public float getRealAngle() {
		return mAngle;
	}

	public float getAngle() {
		return mAngle + add_degree;
	}
}
