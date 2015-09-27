package com.docflowdroid.map;

import android.content.Intent;

import com.docflowdroid.DocFlow;
import com.docflowdroid.MainActivity;
import com.docflowdroid.MapActivity;
import com.docflowdroid.common.IMapObjectProcessor;
import com.docflowdroid.comp.MapButton;

public class MapObjectProcessor implements IMapObjectProcessor {

	@Override
	public void execute(final MapButton mapButton, final Integer cusid,
			final int subregion_id) {
		String value = mapButton.getValue();
		if (value != null && !value.trim().isEmpty())
			startMapActivity(mapButton, subregion_id, value);
		else {
			MainActivity.instance.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						String coords = DocFlow.docFlowService
								.getCenterCoordinates(cusid, subregion_id + "",
										GOOGLE_SRID + "");
						if (coords != null) {
							mapButton.setValue(coords);
							startMapActivity(mapButton, subregion_id, coords);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
	}

	private void startMapActivity(MapButton mapButton, final int subregion_id,
			String value) {
		try {
			MapActivity.drawable = mapButton.getDrawable();
			MapActivity.mapButton = mapButton;
			final Intent myIntent = new Intent(mapButton.getContext(),
					MapActivity.class);
			myIntent.putExtra(MapActivity.MAP_COORDS, value);
			myIntent.putExtra(MapActivity.MAP_SUBREGION_ID, subregion_id);
			MainActivity.instance.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MainActivity.instance.startActivity(myIntent);

				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
