package com.spatialite.activities;

import jsqlite.Exception;
import jsqlite.Stmt;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.spatialite.utilities.ActivityHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

/**
 * Map overlay to highlight various regions when the uses clicks on the MapView.
 */
public class MapSelectionOverlay extends Overlay {
	private static final String TAG = MapSelectionOverlay.class.getName();

	private final jsqlite.Database mDatabase;

	// Allocate once and reuse
	private final Paint mPaint = new Paint();
	private final Path mPath = new Path();

	private Geometry mGeometry;
	private MarkersOverlay markersOverlay;
	private MapView mapView;

	private boolean addingenabled = false;
	private boolean selectBuildingenabled = true;

	/**
	 * @param databaseName
	 *            Name of database containing the Regions table. Cannot be null.
	 * @param textVew
	 *            TextView used to display the region name. Cannot be null.
	 */

	public MapSelectionOverlay(final Context ctx, String databaseName,
			MarkersOverlay markersOverlay, MapView mapView) throws Exception {
		// TODO make sure databaseName and textView are not null
		super(ctx);
		this.markersOverlay = markersOverlay;
		this.mapView = mapView;
		// Open readonly database
		mDatabase = new jsqlite.Database();
		mDatabase.open(databaseName, jsqlite.Constants.SQLITE_OPEN_READONLY);

		// Edit paint style
		mPaint.setDither(true);
		mPaint.setColor(Color.rgb(128, 136, 231));
		mPaint.setAlpha(100);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(6);
	}

	/**
	 * Handle a "tap" event.
	 * 
	 * @see com.google.android.maps.Overlay#onTap(GeoPoint, MapView)
	 */

	/**
	 * Draw the overlay over the map.
	 * 
	 * @see com.google.android.maps.Overlay#draw(Canvas, MapView, boolean)
	 */

	public boolean isAddingenabled() {
		return addingenabled;
	}

	public void setAddingenabled(boolean addingenabled) {
		this.addingenabled = addingenabled;
	}

	@Override
	public boolean onLongPress(MotionEvent e, MapView mapView) {
		if (markersOverlay == null || !addingenabled)
			return true;

		IGeoPoint p = mapView.getProjection().fromPixels(e.getX(), e.getY());
		markersOverlay.addMarker((GeoPoint) p, "", "");

		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
		if (selectBuildingenabled)
			return super.onSingleTapConfirmed(e, mapView);
		IGeoPoint p = mapView.getProjection().fromPixels(e.getX(), e.getY());
		try {
			// Create query
			// TODO reuse stmt

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT   buid                            , \n");
			sql.append("         AsBinary(Transform(geometry,4326)), \n");
			sql.append("         AsText(Transform(geometry,4326)) \n");
			sql.append("FROM     buildings , \n");
			sql.append("         (SELECT c , \n");
			sql.append("                 buffer(c,10)                                                 bc \n");
			sql.append("         FROM    (SELECT Transform(MakePoint(?,?,4326),32638) c \n");
			sql.append("                 ) \n");
			sql.append("                 s \n");
			sql.append("         ) \n");
			sql.append("         k \n");
			sql.append("WHERE    intersects(bc,geometry) \n");
			sql.append("AND      buid IN \n");
			sql.append("         ( SELECT pkid \n");
			sql.append("         FROM    idx_buildings_Geometry \n");
			sql.append("         WHERE   xmin <= MbrMaxX(bc) \n");
			sql.append("         AND     xmax >= MbrMinX(bc) \n");
			sql.append("         AND     ymin <= MbrMaxY(bc) \n");
			sql.append("         AND     ymax >= MbrMinY(bc) \n");
			sql.append("         ) \n");
			sql.append("ORDER BY distance (geometry ,c) \n");
			sql.append("LIMIT    1;");

			Stmt stmt = mDatabase.prepare(sql.toString());

			stmt.bind(1, p.getLongitudeE6() / 1E6);
			stmt.bind(2, p.getLatitudeE6() / 1E6);

			if (stmt.step()) {
				// Set region name
				// Create JTS geometry from binary representation
				// returned from database
				try {
					mGeometry = new WKBReader().read(stmt.column_bytes(1));
					mapView.invalidate();

				} catch (ParseException e1) {
					mGeometry = null;
					ActivityHelper.showAlert(mapView.getContext(), "WKBReader"
							+ e1.getMessage());
					Log.e(TAG, e1.getMessage());
				}
			}
			stmt.close();
		} catch (Exception e2) {
			Log.e(TAG, e2.getMessage());
			ActivityHelper.showAlert(mapView.getContext(), e2.getMessage());
		}

		// Indicate tap was handled
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow) {

		if (mGeometry != null) {
			// TODO There could be more than one geometries

			Geometry g = mGeometry.getGeometryN(0);
			final Point p = new Point();
			boolean first = true;

			mPath.reset();
			for (Coordinate c : g.getCoordinates()) {
				// Convert lat/lon to pixels on screen
				// GeoPoint is immutable so allocation is unavoidable
				Projection projection = mapv.getProjection();
				projection.toPixels(new GeoPoint((int) (c.y * 1E6),
						(int) (c.x * 1E6)), p);

				// Set path starting point to first coordinate
				// otherwise default start is (0,0)
				if (first) {

					mPath.moveTo(p.x, p.y);
					first = false;
				}

				// Add new point to path
				mPath.lineTo(p.x, p.y);

			}

		}

		// Draw the path with give paint
		canvas.drawPath(mPath, mPaint);

	}

	public void find(Long cusid, Long buildingId) {
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT   buid                            , \n");
			sql.append("         AsBinary(Transform(geometry,4326)), \n");
			sql.append("         AsText(Transform(geometry,4326)) \n");
			sql.append("FROM     buildings where buid=? \n");

			Stmt stmt = mDatabase.prepare(sql.toString());

			stmt.bind(1, buildingId);
			if (stmt.step()) {
				// Set region name
				// Create JTS geometry from binary representation
				// returned from database
				try {
					mGeometry = new WKBReader().read(stmt.column_bytes(1));

					com.vividsolutions.jts.geom.Point p = mGeometry
							.getCentroid();

					GeoPoint gp = new GeoPoint((int) (p.getY() * 1E6),
							(int) (p.getX() * 1E6));
					ActivityHelper.showAlert(mapView.getContext(),
							"GeoPoint center=" + gp);
					mapView.getController().setCenter(gp);
					mapView.getController().setZoom(18);
					mapView.getController().setCenter(gp);
					mapView.invalidate();

				} catch (ParseException e1) {
					mGeometry = null;
					ActivityHelper.showAlert(mapView.getContext(), "WKBReader"
							+ e1.getMessage());
					Log.e(TAG, e1.getMessage());
				}
			}
			stmt.close();
		} catch (Exception e2) {
			Log.e(TAG, e2.getMessage());
			ActivityHelper.showAlert(mapView.getContext(), e2.getMessage());
		}

	}

	public boolean isSelectBuildingenabled() {
		return selectBuildingenabled;
	}

	public void setSelectBuildingenabled(boolean selectBuildingenabled) {
		this.selectBuildingenabled = selectBuildingenabled;
	}
}
