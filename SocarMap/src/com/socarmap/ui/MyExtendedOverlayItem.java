package com.socarmap.ui;

import android.content.Context;

import com.socarmap.helper.GeoPointHelper;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.NewBuilding;

public class MyExtendedOverlayItem extends ExtendedOverlayItem {

	private CusMeter cusMeter;
	private Context context;

	private NewBuilding newBuilding;

	public MyExtendedOverlayItem(CusMeter cusMeter, Context context) {
		super(cusMeter.toString(), cusMeter.toString(), GeoPointHelper
				.toGeoPoint(cusMeter.getLocation()));

		this.setCusMeter(cusMeter);
		this.setContext(context);
	}

	public MyExtendedOverlayItem(NewBuilding newBuilding, Context context) {
		super(newBuilding.getBuilding_add_id(), newBuilding
				.getBuilding_add_id(), GeoPointHelper.toGeoPoint(newBuilding
				.getLocation()));
		this.setNewBuilding(newBuilding);
		this.setContext(context);
	}

	public Context getContext() {
		return context;
	}

	public CusMeter getCusMeter() {
		return cusMeter;
	}

	public NewBuilding getNewBuilding() {
		return newBuilding;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private void setCusMeter(CusMeter cusMeter) {
		this.cusMeter = cusMeter;
	}

	public void setNewBuilding(NewBuilding newBuilding) {
		this.newBuilding = newBuilding;
	}

}
