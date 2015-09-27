package com.spatialite.helpers;

import java.util.ArrayList;
import java.util.List;

import jsqlite.Stmt;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.spatialite.R;
import com.spatialite.utilities.ActivityHelper;
import com.spatialite.utilities.MyExtendedOverlayItem;

public class MarkersOverlay extends
		ItemizedOverlayWithBubble<MyExtendedOverlayItem> {

	private final jsqlite.Database mDatabase;
	private List<MyExtendedOverlayItem> items = null;
	private Drawable marker = null;
	private MapView mapv;
	

	public MarkersOverlay(Drawable marker, ResourceProxy pResourceProxy,
			ImageView dragImage, String databaseName, MapView mapv) {
		super(mapv.getContext(), new ArrayList<MyExtendedOverlayItem>(), mapv,
				new MyInfoWindow(R.layout.myinfowindow, mapv));
		((MyInfoWindow) mBubble).setMarkersOverlay(this);
		items = mItemsList;
		if (items == null)
			items = new ArrayList<MyExtendedOverlayItem>();
		items = mItemsList;
		mDatabase = new jsqlite.Database();
		this.mapv = mapv;
		this.marker = marker;
		try {
			mDatabase
					.open(databaseName, jsqlite.Constants.SQLITE_OPEN_READONLY);
			Stmt stmt = mDatabase
					.prepare("select building_id,x(g),y(g) from (select building_id,(transform(PointOnSurface(Geometry),4326)) g from (SELECT distinct building_id  FROM building_to_customers ) bc inner join buildings b on b.buid=bc.building_id  limit 30) k");
			while (stmt.step()) {
				// Set region name

				// Create JTS geometry from binary representation
				// returned from database

				// Geometry mGeometry = new
				// WKBReader().read(stmt.column_bytes(1));

				// Coordinate c = mGeometry.getCoordinates()[0];
				int id = stmt.column_int(0);
				GeoPoint gg = new GeoPoint(stmt.column_double(2),
						stmt.column_double(1));
				MyExtendedOverlayItem oi = new MyExtendedOverlayItem("Hello",
						id + " build", gg, mapv.getContext());
				oi.setMarker(marker);
				items.add(oi);

			}
			stmt.close();
		} catch (Throwable e) {
			ActivityHelper.showAlert(mapv.getContext(),
					" Exept2" + e.getMessage());
		}

		populate();
	}

	public void populateItems() {
		populate();
	}

	@Override
	public boolean onLongPress(MotionEvent event, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onLongPress(event, mapView);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}

	public void addMarker(GeoPoint item, String title, String text) {
		// ActivityHelper.showAlert(mapv.getContext(), " added new" + item);
		MyExtendedOverlayItem oi = new MyExtendedOverlayItem(title, title,
				item, mapv.getContext());
		oi.setMarker(marker);
		items.add(oi);
		populate();
	}

	@Override
	protected MyExtendedOverlayItem createItem(int i) {
		return (items.get(i));

	}

	@Override
	public int size() {
		if (items == null)
			return 0;
		return (items.size());
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1,
			android.graphics.Point arg2, IMapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return super.onTouchEvent(event, mapView);
	}

	
}
