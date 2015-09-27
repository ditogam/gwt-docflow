package com.socarmap.ui;

import java.util.ArrayList;
import java.util.List;

import org.oscim.view.MapView;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.socarmap.MainActivity;
import com.socarmap.R;
import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.UserData;

public class MarkersOverlay extends
		ItemizedOverlayWithBubble<MyExtendedOverlayItem> {

	private Drawable cusmeter = null;
	private Drawable dismeter = null;
	private Drawable demage = null;
	private Drawable add_building = null;
	private MapView mapv;
	public static MarkersOverlay instance;

	public static MarkersOverlay getInstance() {
		return instance;
	}

	public MarkersOverlay(UserData userData, Drawable cusmeter,
			Drawable dismeter, Drawable demage, Drawable add_building,
			ImageView dragImage, String databaseName, MapView mapv,
			MainActivity mainActivity) {
		super(mapv, mainActivity, new ArrayList<MyExtendedOverlayItem>(),
				new MyInfoWindow(R.layout.myinfowindow, mapv));
		instance = this;
		((MyInfoWindow) mBubble).setMarkersOverlay(this, mainActivity);
		if (mItemsList == null)
			mItemsList = new ArrayList<MyExtendedOverlayItem>();
		this.mapv = mapv;
		this.cusmeter = cusmeter;
		this.dismeter = dismeter;
		this.demage = demage;
		this.add_building = add_building;

		try {
			ArrayList<CusMeter> cusMeters = DBLoader.getInstance()
					.loadDistinctMeters((long) userData.getPpcity(),
							(long) userData.getPcity());
			for (CusMeter cusMeter : cusMeters) {
				addMarker(cusMeter, false);
			}
		} catch (Throwable e) {
		}

		try {
			ArrayList<CusMeter> demages = DBSettingsLoader.getInstance()
					.loadDemages();
			for (CusMeter dmg : demages) {
				addMarker(dmg, false);
			}
		} catch (Throwable e) {
		}

		try {
			ArrayList<NewBuilding> newBuildings = DBSettingsLoader
					.getInstance().getForNewBuildings(null);
			for (NewBuilding dmg : newBuildings) {
				addMarker(dmg, false);
			}
		} catch (Throwable e) {
		}

		populate();
		mapv.redrawMap(true);

	}

	public void addMarker(CusMeter cusMeter) {
		addMarker(cusMeter, true);
	}

	private void addMarker(CusMeter cusMeter, boolean populate) {
		MyExtendedOverlayItem oi = new MyExtendedOverlayItem(cusMeter,
				mapv.getContext());

		Drawable marker = dismeter;
		if (cusMeter.getType() == CusMeter.CUSTOMER)
			marker = cusmeter;
		if (cusMeter.getType() == CusMeter.DEMAGE)
			marker = demage;
		oi.setMarker(marker);
		addItem(oi);
		// items.add(oi);
		// if (populate)
		// populate();
	}

	public void addMarker(NewBuilding newBuilding) {
		addMarker(newBuilding, true);
	}

	private void addMarker(NewBuilding newBuilding, boolean populate) {
		MyExtendedOverlayItem oi = new MyExtendedOverlayItem(newBuilding,
				mMapView.getContext());

		oi.setMarker(add_building);
		addItem(oi);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		if (mBubble != null && mBubble.mIsVisible)
			mBubble.close();
		return super.onSingleTapUp(event);
	}

	public void populateItems() {
		populate();
		mapv.redrawMap(true);
	}

	public void updateDemageId(int old_demage_id, int new_demage_id) {
		List<MyExtendedOverlayItem> items = mItemList;
		for (MyExtendedOverlayItem item : items) {
			CusMeter cm = item.getCusMeter();
			if (cm == null)
				continue;
			if (cm.getType() != CusMeter.DEMAGE)
				continue;
			if (cm.getCusid() == old_demage_id) {
				cm.setCusid(new_demage_id);
				cm.setMeterid(new_demage_id);
			}
		}
	}

}
