package com.socarmap.ui;

import java.util.ArrayList;

import jsqlite.Exception;
import jsqlite.Stmt;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.overlay.Overlay;
import org.oscim.renderer.layer.Layer;
import org.oscim.renderer.layer.LineLayer;
import org.oscim.renderer.overlays.BasicOverlay;
import org.oscim.theme.renderinstruction.Line;
import org.oscim.utils.FastMath;
import org.oscim.view.MapView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;

import com.socarmap.BuildingCustomers;
import com.socarmap.CustomerSearch;
import com.socarmap.DemageActivity;
import com.socarmap.MainActivity;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.UserData;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

/**
 * Map overlay to highlight various regions when the uses clicks on the MapView.
 */
public class MapSelectionOverlay extends Overlay {

	class BuildingPath extends BasicOverlay {

		private static final byte MAX_ZOOM = 20;
		private static final int MIN_DIST = 4;

		// pre-projected points to zoomlovel 20
		private int[] mPreprojected;

		// projected points
		private float[] mPPoints;
		private final short[] mIndex;
		private int mSize;

		private final Line mLine;

		// limit coords
		private final int max = 2048;

		public BuildingPath(MapView mapView) {
			super(mapView);
			mLine = new Line(Color.BLUE, 3.0f, Cap.BUTT);
			mIndex = new short[1];
			mPPoints = new float[1];
		}

		// note: this is called from GL-Thread. so check your syncs!
		// TODO use an Overlay-Thread to build up layers (like for Labeling)
		@Override
		public synchronized void update(MapPosition curPos,
				boolean positionChanged, boolean tilesChanged) {

			if (!tilesChanged && !mUpdatePoints)
				return;

			float[] projected = mPPoints;

			if (mUpdatePoints) {
				// pre-project point on zoomlelvel 20
				synchronized (mPoints) {
					mUpdatePoints = false;

					ArrayList<GeoPoint> geopoints = mPoints;
					int size = geopoints.size();
					int[] points = mPreprojected;
					mSize = size * 2;

					if (mSize > projected.length) {
						points = mPreprojected = new int[mSize];
						projected = mPPoints = new float[mSize];
					}

					for (int i = 0, j = 0; i < size; i++, j += 2) {
						GeoPoint p = geopoints.get(i);
						points[j + 0] = (int) MercatorProjection
								.longitudeToPixelX(p.getLongitude(), MAX_ZOOM);
						points[j + 1] = (int) MercatorProjection
								.latitudeToPixelY(p.getLatitude(), MAX_ZOOM);
					}
				}
			}

			int size = mSize;

			// keep position to render relative to current state
			updateMapPosition();

			// items are placed relative to scale == 1
			mMapPosition.scale = 1;

			// layers.clear();
			LineLayer ll = (LineLayer) layers.getLayer(1, Layer.LINE);
			// reset verticesCnt to reuse layer
			ll.verticesCnt = 0;
			ll.line = mLine;
			ll.width = 2.5f;

			int x, y, px = 0, py = 0;
			int i = 0;

			int diff = MAX_ZOOM - mMapPosition.zoomLevel;
			int mx = (int) mMapPosition.x;
			int my = (int) mMapPosition.y;

			for (int j = 0; j < size; j += 2) {
				// TODO translate mapPosition and do this after clipping
				x = (mPreprojected[j + 0] >> diff) - mx;
				y = (mPreprojected[j + 1] >> diff) - my;

				// TODO use line clipping, this doesnt work with 'GreatCircle'
				// TODO clip to view bounding box
				if (x > max || x < -max || y > max || y < -max) {
					if (i > 2) {
						mIndex[0] = (short) i;
						ll.addLine(projected, mIndex, false);
					}
					i = 0;
					continue;
				}

				// skip too near points
				int dx = x - px;
				int dy = y - py;
				if ((i == 0) || FastMath.absMaxCmp(dx, dy, MIN_DIST)) {
					projected[i + 0] = px = x;
					projected[i + 1] = py = y;
					i += 2;
				}
			}

			mIndex[0] = (short) i;
			ll.addLine(projected, mIndex, false);

			newData = true;

		}

	}

	public static final int REQUEST_CODE_CUS_METER = 10001;
	public static final int REQUEST_CODE_DIST_METER = 10002;
	public static final int REQUEST_CODE_DEMAGE = 10003;

	public static final int REQUEST_CODE_ADD_BUILDING = 10004;
	public static final String REQUEST_POINT = "point";

	public static final String REQUEST_CODE = "REQUEST_CODE";

	private static final String TAG = MapSelectionOverlay.class.getName();

	private final jsqlite.Database mDatabase;

	// Allocate once and reuse
	private final Paint mPaint = new Paint();
	private Geometry mGeometry;

	private MapView mapView;
	private boolean addingCusMeterEnabled = false;
	private boolean addingDemageEnabled = false;
	private boolean addingDistMeterEnabled = false;
	private boolean selectBuildingenabled = true;
	private boolean addBuildingenabled = false;
	@SuppressWarnings("unused")
	private Context ctx;

	private MainActivity mainActivity;
	boolean mUpdatePoints;

	final ArrayList<GeoPoint> mPoints;

	/**
	 * @param databaseName
	 *            Name of database containing the Regions table. Cannot be null.
	 * @param textVew
	 *            TextView used to display the region name. Cannot be null.
	 */

	public MapSelectionOverlay(UserData userData, final Context ctx,
			String databaseName, MarkersOverlay markersOverlay,
			MapView mapView, MainActivity mainActivity) throws Exception {
		// TODO make sure databaseName and textView are not null
		super(mapView);
		this.mapView = mapView;
		this.mainActivity = mainActivity;
		// Open readonly database
		mDatabase = new jsqlite.Database();
		mDatabase.open(databaseName, jsqlite.Constants.SQLITE_OPEN_READONLY);
		this.mPoints = new ArrayList<GeoPoint>();

		// Edit paint style
		mPaint.setDither(true);
		mPaint.setColor(Color.rgb(128, 136, 231));
		mPaint.setAlpha(100);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(6);
		mLayer = new BuildingPath(mapView);
	}

	/**
	 * Handle a "tap" event.
	 * 
	 * @see com.google.android.maps.Overlay#onTap(GeoPoint, MapView)
	 */

	public void addPoint(final GeoPoint pt) {
		synchronized (mPoints) {
			mPoints.add(pt);
			mUpdatePoints = true;
		}
	}

	private void drawBuilding(MapView mapView) {
		mPoints.clear();
		if (mGeometry != null) {
			Coordinate[] coords = mGeometry.getCoordinates();

			for (Coordinate c : coords) {
				addPoint(new GeoPoint((int) (c.y * 1E6), (int) (c.x * 1E6)));
			}

		}
		mapView.redrawMap(true);
	}

	public void find(Long cusid, Long buildingId) {
		try {
			mPoints.clear();
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT   buid                            , \n");
			sql.append("         AsBinary((the_geom)) \n");
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

					mapView.setMapCenter(new MapPosition(gp, (byte) 18, 0));

					drawBuilding(mapView);

				} catch (ParseException e1) {
					mGeometry = null;
					ActivityHelper.showAlert(mapView.getContext(), e1);
					Log.e(TAG, e1.getMessage());
				}
			}
			stmt.close();
		} catch (Exception e2) {
			Log.e(TAG, e2.getMessage());
			ActivityHelper.showAlert(mapView.getContext(), e2);
		}

	}

	/**
	 * Draw the overlay over the map.
	 * 
	 * @see com.google.android.maps.Overlay#draw(Canvas, MapView, boolean)
	 */

	private long[] getPointPosition(GeoPoint p) {
		Long region_id = null;
		Long subregion_id = null;
		Stmt stmt = null;

		// ActivityHelper.showAlert(mainActivity, p.toString());
		try {
			// Create query
			// TODO reuse stmt

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT pc.ppcityid,pc.pcityid\n");
			sql.append("FROM map_info mi\n");
			sql.append("inner join pcity pc on pc.pcityid=mi.ppcityid\n");
			// sql.append("where Intersects(the_geom, MakePoint(?,?,4326))");
			// sql.append("order by Distance(the_geom, MakePoint(?,?,4326)) \n");
			sql.append("limit 1");

			stmt = mDatabase.prepare(sql.toString());

			// stmt.bind(1, p.longitudeE6 / 1E6);
			// stmt.bind(2, p.latitudeE6 / 1E6);
			// stmt.bind(3, p.longitudeE6 / 1E6);
			// stmt.bind(4, p.latitudeE6 / 1E6);

			if (stmt.step()) {
				region_id = stmt.column_long(0);
				subregion_id = stmt.column_long(1);
				return new long[] { region_id, subregion_id };
			}

		} catch (Exception e2) {
			Log.e(TAG, e2.getMessage());
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean isAddBuildingenabled() {
		return addBuildingenabled;
	}

	public boolean isAddingCusMeterEnabled() {
		return addingCusMeterEnabled;
	}

	public boolean isAddingDemageEnabled() {
		return addingDemageEnabled;
	}

	public boolean isAddingDistMeterEnabled() {
		return addingDistMeterEnabled;
	}

	public boolean isSelectBuildingenabled() {
		return selectBuildingenabled;
	}

	@Override
	public boolean onLongPress(MotionEvent e) {
		GeoPoint p = mMapView.getMapViewPosition().fromScreenPixels(e.getX(),
				e.getY());

		long[] position_options = getPointPosition(p);
		if (position_options == null)
			return true;

		if (selectBuildingenabled) {
			showBuilding(e, mapView, p);
		}
		if (mapView.getMapPosition().getMapPosition().zoomLevel < 15) {
			return true;
		}
		if (!addingCusMeterEnabled && !addingDistMeterEnabled
				&& !addingDemageEnabled && !addBuildingenabled)
			return true;

		Class<?> acctivityClass = CustomerSearch.class;
		if (addingDemageEnabled)
			acctivityClass = DemageActivity.class;
		if (addBuildingenabled)
			acctivityClass = BuildingCustomers.class;

		Intent intent = new Intent(mapView.getContext(), acctivityClass);
		if (!addingDemageEnabled) {
			intent.putExtra(CustomerSearch.REGION_ID, position_options[0]);
			intent.putExtra(CustomerSearch.SUBREGION_ID, position_options[1]);
		}
		intent.putExtra(REQUEST_POINT, p);
		int requestCode = REQUEST_CODE_CUS_METER;
		if (addingDistMeterEnabled)
			requestCode = REQUEST_CODE_DIST_METER;
		if (addBuildingenabled) {
			requestCode = REQUEST_CODE_ADD_BUILDING;
			acctivityClass = BuildingCustomers.class;
			TelephonyManager tManager = (TelephonyManager) mainActivity
					.getSystemService(Context.TELEPHONY_SERVICE);
			String building_id = null;
			try {
				building_id = tManager.getDeviceId() + "_"
						+ System.currentTimeMillis();
			} catch (Throwable e2) {
				e2.printStackTrace();
			}
			intent.putExtra(BuildingCustomers.BC_REGION_ID, position_options[0]);
			intent.putExtra(BuildingCustomers.BC_SUBREGION_ID,
					position_options[1]);
			intent.putExtra(BuildingCustomers.BC_BUILDING_ADD_ID, building_id);
			intent.putExtra(BuildingCustomers.BC_IS_NEW_BUILDING, true);
		}
		if (addingDemageEnabled)
			requestCode = REQUEST_CODE_DEMAGE;
		intent.putExtra(REQUEST_POINT, p);
		intent.putExtra(REQUEST_CODE, requestCode);
		mainActivity.startActivityForResult(intent, requestCode);

		// markersOverlay.addMarker((GeoPoint) p, "", "");

		return true;
	}

	public void setAddBuildingenabled(boolean addBuildingenabled) {
		this.addBuildingenabled = addBuildingenabled;
	}

	public void setAddingCusMeterEnabled(boolean addingCusMeterEnabled) {
		this.addingCusMeterEnabled = addingCusMeterEnabled;
	}

	public void setAddingDemageEnabled(boolean addingDemageEnabled) {
		this.addingDemageEnabled = addingDemageEnabled;
	}

	public void setAddingDistMeterEnabled(boolean addingDistMeterEnabled) {
		this.addingDistMeterEnabled = addingDistMeterEnabled;
	}

	public void setSelectBuildingenabled(boolean selectBuildingenabled) {
		this.selectBuildingenabled = selectBuildingenabled;
	}

	public void showBuilding(MotionEvent e, MapView mapView, GeoPoint p) {
		mGeometry = null;
		Long building_id = null;
		Long region_id = null;
		Long subregion_id = null;

		try {

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT   buid                            , \n");
			sql.append("         AsBinary(the_geom),raiid,regid\n");
			sql.append("FROM     buildings , \n");
			sql.append("         (SELECT c , \n");
			sql.append("                 transform( buffer(transform(c,2163),10)  ,4326)         bc \n");
			sql.append("         FROM    (SELECT MakePoint(?,?,4326) c \n");
			sql.append("                 ) \n");
			sql.append("                 s \n");
			sql.append("         ) \n");
			sql.append("         k \n");
			sql.append("WHERE    intersects(bc,the_geom) \n");
			sql.append("AND      buid IN \n");
			sql.append("         ( SELECT pkid \n");
			sql.append("         FROM    idx_buildings_the_geom\n");
			sql.append("         WHERE   xmin <= MbrMaxX(bc) \n");
			sql.append("         AND     xmax >= MbrMinX(bc) \n");
			sql.append("         AND     ymin <= MbrMaxY(bc) \n");
			sql.append("         AND     ymax >= MbrMinY(bc) \n");
			sql.append("         ) \n");
			sql.append("ORDER BY distance (the_geom ,c) \n");
			sql.append("LIMIT    1");

			Stmt stmt = mDatabase.prepare(sql.toString());

			stmt.bind(1, p.longitudeE6 / 1E6);
			stmt.bind(2, p.latitudeE6 / 1E6);

			if (stmt.step()) {
				building_id = stmt.column_long(0);
				region_id = stmt.column_long(3);
				subregion_id = stmt.column_long(2);
				try {
					mGeometry = new WKBReader().read(stmt.column_bytes(1));

				} catch (ParseException e1) {
					mGeometry = null;
					Log.e(TAG, e1.getMessage());
				}
			}
			stmt.close();
		} catch (Exception e2) {
			Log.e(TAG, e2.getMessage());
		}
		Long mRegion = (long) MainActivity.getUserData().getPcity();
		mRegion = mRegion.longValue() < 0 ? region_id : mRegion;

		Long mSubregion_id = (long) MainActivity.getUserData().getPpcity();
		mSubregion_id = mSubregion_id.longValue() < 0 ? subregion_id
				: mSubregion_id;

		if (mGeometry != null && building_id != null && region_id != null
				&& subregion_id != null) {
			if (mRegion.intValue() == region_id.intValue()
					&& subregion_id.intValue() == mSubregion_id.intValue()) {
				Intent in = new Intent(mainActivity, BuildingCustomers.class);
				in.putExtra(BuildingCustomers.BC_BUILDING_ID, building_id);
				in.putExtra(BuildingCustomers.BC_REGION_ID, region_id);
				in.putExtra(BuildingCustomers.BC_SUBREGION_ID, subregion_id);
				// updateBuidingMapData(building_id);
				mainActivity.startActivity(in);
			}
		}

		drawBuilding(mapView);
	}

	public void updateBuidingMapData(Long building_id) {
		try {
			BuildingUpdate buildingUpdate = new BuildingUpdate();
			buildingUpdate.setBuid(building_id.intValue());

		} catch (Throwable e2) {
		}
	}
}
