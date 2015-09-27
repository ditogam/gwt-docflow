package com.socarmap;

import java.util.ArrayList;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.database.MapDatabases;
import org.oscim.database.MapOptions;
import org.oscim.overlay.ItemizedOverlay;
import org.oscim.utils.AndroidUtils;
import org.oscim.view.DebugSettings;
import org.oscim.view.LocationHandler;
import org.oscim.view.MapActivity;
import org.oscim.view.MapListener;
import org.oscim.view.MapView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.BarcodeLauncher;
import com.socarmap.helper.ConnectionHelper;
import com.socarmap.helper.GPSTracker;
import com.socarmap.helper.GeoPointHelper;
import com.socarmap.helper.IntentIntegrator;
import com.socarmap.proxy.beans.CusMeter;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.UserData;
import com.socarmap.ui.MapSelectionOverlay;
import com.socarmap.ui.MarkersOverlay;

public class MainActivity extends MapActivity {

	public MapView mapView;
	// private MyLocationOverlay mLocationOverlay;
	private MapSelectionOverlay selectionOverlay;
	private MarkersOverlay markersOverlay;
	private Menu menu;
	private static UserData userData;
	public static MainActivity instance = null;

	public static Long getCustomer_id(String barcode) {
		String split[] = barcode.split("-");
		return Long.parseLong(split[0]);
	}

	public static UserData getUserData() {
		return userData;
	}

	private GPSTracker gps;

	private DebugSettings dsDefault;

	private DebugSettings dsTiles;

	private void checkManipulateStatus() {
		if (menu == null)
			return;
		MenuItem item = null;

		int[] ids = new int[] { R.id.mi_cus_meter, R.id.mi_building_select,
				R.id.mi_distinct_meter, R.id.mi_demage, R.id.mi_building_add };
		for (int i = 0; i < ids.length; i++) {
			MenuItem citem = menu.findItem(ids[i]);
			if (citem == null)
				continue;
			if (citem.isChecked()) {
				item = citem;
				break;
			}
		}
		if (item == null)
			return;
		boolean addingCusMeterEnabled = (item.getItemId() == R.id.mi_cus_meter);
		selectionOverlay.setAddingCusMeterEnabled(addingCusMeterEnabled);
		boolean addingDistMeterEnabled = item.getItemId() == R.id.mi_distinct_meter;
		selectionOverlay.setAddingDistMeterEnabled(addingDistMeterEnabled);
		boolean selectBuildingenabled = (item.getItemId() == R.id.mi_building_select);
		selectionOverlay.setSelectBuildingenabled(selectBuildingenabled);
		boolean selectDemageenabled = (item.getItemId() == R.id.mi_demage);
		selectionOverlay.setAddingDemageEnabled(selectDemageenabled);
		boolean addBuildingenabled = (item.getItemId() == R.id.mi_building_add);
		selectionOverlay.setAddBuildingenabled(addBuildingenabled);

	}

	public void deleteNewBuilding(NewBuilding newBuilding) throws Throwable {
		try {
			DBSettingsLoader.getInstance()
					.proceedNewBuilding(newBuilding, true);
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
			throw e;
		}
	}

	private void findBuilding(Long cusid, Long buildingId) {
		selectionOverlay.find(cusid, buildingId);

	}

	private void findCustomer(Long cus_id) {
		try {
			ArrayList<CusShort> list = DBLoader.getInstance().getCustomers(
					null, null, cus_id, true, null, null, false);
			if (list == null || list.isEmpty())
				throw new Exception("Cannot find customer " + cus_id + "!!!");
			CusShort c = list.get(0);
			if (c.getBuilding_id() == null)
				ActivityHelper.showAlert(this, "Customer " + cus_id
						+ " has no building!!!");
			else
				findBuilding(c.getCus_id(), c.getBuilding_id());
		} catch (Exception e) {
			ActivityHelper.showAlert(this, e);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			super.onActivityResult(requestCode, resultCode, data);

			if (resultCode != -1)
				return;

			if (requestCode == MapSelectionOverlay.REQUEST_CODE_DEMAGE
					&& data != null) {
				GeoPoint geoPoint = (GeoPoint) data
						.getParcelableExtra(MapSelectionOverlay.REQUEST_POINT);

				int dId = data.getIntExtra(DemageActivity.DEMAGE_DESCRIPTION,
						-1);
				String sTypeName = data
						.getStringExtra(DemageActivity.DEMAGE_DESCRIPTION_TYPE_NAME);
				CusMeter cm = new CusMeter(dId, 0, "", "", "", "", 0,
						sTypeName, GeoPointHelper.toMGeoPoint(geoPoint),
						CusMeter.DEMAGE);
				markersOverlay.addMarker(cm);

			}
			if ((requestCode == MapSelectionOverlay.REQUEST_CODE_CUS_METER || requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER)
					&& data != null) {
				try {

					// String sCusid = data.getStringExtra("cusid");
					String sMeterid = data.getStringExtra("meter_id");
					boolean customer = data.getBooleanExtra("customer", true);
					Parcelable pa = data
							.getParcelableExtra(MapSelectionOverlay.REQUEST_POINT);
					if (pa == null) {
						return;
					}

					GeoPoint geoPoint = (GeoPoint) pa;
					CusMeter cm = DBLoader.getInstance().getCusMeters(
							Long.parseLong(sMeterid), customer, geoPoint);
					if (cm == null) {
						ActivityHelper.showAlert(this, "Cannot find meter!!!");
					}

					DBLoader.getInstance().saveCusMeter(cm,
							userData.getPcity(), CusMeter.ACTION_ADD,
							userData.getUserid());
					markersOverlay.addMarker(cm);

				} catch (Throwable e) {
					ActivityHelper.showAlert(this, e);
				}
				return;
			}

			if ((requestCode == MapSelectionOverlay.REQUEST_CODE_ADD_BUILDING)
					&& data != null) {
				try {

					// String sCusid = data.getStringExtra("cusid");
					NewBuilding newBuilding = (NewBuilding) data
							.getSerializableExtra("newBuilding");
					if (data.getBooleanExtra("isnewBuilding", false))
						markersOverlay.addMarker(newBuilding);

				} catch (Exception e) {
					ActivityHelper.showAlert(getBaseContext(), e);
				}
				return;
			}

			if (resultCode == -1 && requestCode == 10000 && data != null) {
				try {

					String sCusid = data.getStringExtra("cusid");
					Long cusid = null;

					if (sCusid != null) {
						try {
							cusid = Long.parseLong(sCusid.trim());
							findCustomer(cusid);
						} catch (Exception e) {
							ActivityHelper.showAlert(getBaseContext(), e);
						}
					}

				} catch (Exception e) {
					ActivityHelper.showAlert(getBaseContext(), e);
				}
				return;
			}

			switch (requestCode) {
			case IntentIntegrator.REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					String scanResult = data.getStringExtra("SCAN_RESULT");
					if (scanResult != null) {
						// String out = scanResult.getContents();
						String out = scanResult;
						if (out != null) {
							try {
								Long cus_id = getCustomer_id(out);
								findCustomer(cus_id);
							} catch (Exception e) {
								ActivityHelper.showAlert(this, e);
							}
						}
					}

				}
				break;
			}
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				ActivityHelper.showAlert(getApplicationContext(), ex);
			}
		});
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		instance = this;
		LoginActivity.receiver = ConnectionHelper.init(this);
		String dbPath = getString(R.string.socar_db);
		try {
			gps = new GPSTracker(this);
			setContentView(R.layout.activity_main);

			mapView = (MapView) findViewById(R.id.map);
			mapView.setClickable(true);
			mapView.setFocusable(true);
			@SuppressWarnings("unused")
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			MapOptions options = new MapOptions(MapDatabases.SPATIALITE_READER);
			options.put("file", dbPath);
			options.put("style_file", getString(R.string.style_file));
			mapView.setMapDatabase(options);
			// mapView.setBuiltInZoomControls(true);
			// mapView.setMultiTouchControls(true);
			// mapView.setUseDataConnection(true);
			// mapController = mapView.getController();
			// DebugSettings debugSettings = mapView.getDebugSettings();
			// if (debugSettings == null)
			dsDefault = new DebugSettings(false, true, false, false, false);
			dsTiles = new DebugSettings(false, false, false, false, false);
			// debugSettings.drawTileCoordinates=true;
			// mapView.setDebugSettings(debugSettings);
			userData = LoginActivity.userData;
			byte zoom = 0;
			if (userData.getZoom() >= 10)
				zoom = (byte) (userData.getZoom());
			else
				zoom = (15);
			GeoPoint center = GeoPointHelper.toGeoPoint(userData.getCenter());

			MapPosition mp = new MapPosition(center, zoom, 1);

			mapView.setMapCenter(mp);

			Drawable cusmeter = ItemizedOverlay.makeMarker(getResources(),
					R.drawable.customer_meter, null);
			Drawable dismeter = ItemizedOverlay.makeMarker(getResources(),
					R.drawable.district_meter, null);
			Drawable demage = ItemizedOverlay.makeMarker(getResources(),
					R.drawable.fire_big, null);

			Drawable add_building = ItemizedOverlay.makeMarker(getResources(),
					R.drawable.home, null);

			ImageView dragImage = (ImageView) findViewById(R.id.drag);
			// this.mLocationOverlay = new MyLocationOverlay(this, mapView,
			// mResourceProxy);
			// mLocationOverlay.enableCompass();
			// mLocationOverlay.disableMyLocation();
			// mapView.getOverlays().add(this.mLocationOverlay);

			// markersOverlay = new MyItemizedOverlay(userData, cusmeter,
			// dismeter,
			// demage, dragImage, dbPath, mapView, this);

			// ArrayList<ExtendedOverlayItem> items = new
			// ArrayList<ExtendedOverlayItem>();
			// ExtendedOverlayItem item = new ExtendedOverlayItem("ddd", "ccc",
			// new GeoPoint(41.941253, 45.818797));
			// ItemizedOverlayWithBubble<ExtendedOverlayItem> ov = new
			// ItemizedOverlayWithBubble<ExtendedOverlayItem>(
			// this, items, mapView);

			selectionOverlay = new MapSelectionOverlay(userData,
					this.getBaseContext(), dbPath, null, mapView, this);

			// final MyOverlay mo = new MyOverlay(this);
			// mapView.getOverlays().add(mo);
			mapView.getOverlays().add(selectionOverlay);
			markersOverlay = new MarkersOverlay(userData, cusmeter, dismeter,
					demage, add_building, dragImage, dbPath, mapView, this);
			mapView.getOverlays().add(markersOverlay);
			mapView.getMapPosition().setListener(new MapListener() {

				@Override
				public boolean onStateChanged(MapPosition event) {
					saveUserData();
					return false;
				}
			});
			mapView.createLocationHandler(R.id.snapToLocationView);
			// item.setMarker(ItemizedOverlay.makeMarker(getResources(),
			// R.drawable.customer_meter, null));
			// ov.addItem(item);
			// mapView.setCenter(item.mGeoPoint);
			// mapView.setMapCenter(mapPosition)pListener(new MapListener() {
			//
			// @Override
			// public boolean onZoom(ZoomEvent arg0) {
			// saveUserData();
			// // mo.positionChanged(mapView);
			// return true;
			// }
			//
			// @Override
			// public boolean onScroll(ScrollEvent arg0) {
			// saveUserData();
			// // mo.positionChanged(mapView);
			// return true;
			// }
			// });
			// mo.positionChanged(mapView);
			// mapView.enableCompass(true);
			// MapScaleBar msb=new MapScaleBar(mapView);
			// msb.setShowMapScaleBar(true);
			// msb.setImperialUnits(true);
			// LocationHandler l=new LocationHandler(this);
			// l.enableShowMyLocation(true);
			// l.disableSnapToLocation(true);
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		this.menu = menu;
		return true;
	}

	public void showToastOnUiThread(final String text) {

		if (AndroidUtils.currentThreadIsUiThread()) {
			Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			toast.show();
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(MainActivity.this, text,
							Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		saveUserData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case R.id.m_degree:
			Intent myI = new Intent(this, AddDegree.class);
			startActivity(myI);
			return true;
		case R.id.m_my_location:
			// gps = new GPSTracker(this);
			// if (gps.canGetLocation()) {
			//
			// double latitude = gps.getLatitude();
			// double longitude = gps.getLongitude();
			// mapView.setCenter(new GeoPoint(latitude, longitude));
			// Toast.makeText(
			// getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: "
			// + longitude, Toast.LENGTH_LONG).show();
			// } else {
			// // can't get location
			// // GPS or Network is not enabled
			// // Ask user to enable GPS/network in settings
			// gps.showSettingsAlert();
			// }

			LocationHandler hnd = mapView.getLocationHandler();
			boolean enabled = hnd != null && hnd.isShowMyLocationEnabled();
			if (enabled)
				hnd.disableShowMyLocation();
			else {
				if (hnd != null)
					hnd.enableShowMyLocation(true);
			}
			item.setChecked(enabled = hnd != null
					&& hnd.isShowMyLocationEnabled());
			return true;
		case R.id.m_compass:
			System.err.println("Compas MENUUU");
			mapView.enableCompass(!mapView.getCompassEnabled());
			item.setChecked(mapView.getCompassEnabled());
			return true;

		case R.id.m_goto_center:
			try {
				MapPosition mp = mapView.getMapFileCenter();
				if (mp != null) {
					mp.zoomLevel = mp.zoomLevel < 15 ? 15 : mp.zoomLevel;
					mapView.setCenter(new GeoPoint(mp.lat, mp.lon));
				}
			} catch (Exception e) {
			}
			return true;
		case R.id.m_m_barcode:
			// new IntentIntegrator(this).initiateScan();
			BarcodeLauncher.scanBarCode(this);
			return true;
			// case R.id.m_en_dis_zoom:
			// mapView.setMotionEnabled(!mapView.isMotionEnabled());
			// return true;
		case R.id.m_search:
			Intent myIntent = new Intent(this, CustomerSearch.class);
			try {
				long[] regs = DBLoader.getInstance().getUser_data();
				myIntent.putExtra(CustomerSearch.REGION_ID, regs[0]);
				myIntent.putExtra(CustomerSearch.SUBREGION_ID, regs[1]);
				myIntent.putExtra(CustomerSearch.REGS_DISABLE, 0);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startActivityForResult(myIntent, CustomerSearch.REQUEST_CODE);
			return true;
		case R.id.m_download:
			Intent myIntentDownload = new Intent(this, ImportActivity.class);
			startActivity(myIntentDownload);
			return true;
		case R.id.mi_building_select:
		case R.id.mi_cus_meter:
		case R.id.mi_distinct_meter:
		case R.id.mi_demage:
		case R.id.mi_building_add:
			item.setChecked(!item.isChecked());
			checkManipulateStatus();
			return true;
		case R.id.mi_show_titles:
			item.setChecked(!item.isChecked());
			showHideTiles(item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

		saveUserData();
	}

	@Override
	protected void onStop() {
		try {
			if (LoginActivity.receiver != null)
				unregisterReceiver(LoginActivity.receiver);
		} catch (Throwable e) {
		}
		super.onStop();
	}

	public void saveUserData() {
		try {
			userData.setCenter(GeoPointHelper.toMGeoPoint(mapView
					.getBoundingBox().getCenterPoint()));
			userData.setZoom(mapView.getMapPosition().getMapPosition().zoomLevel);
			DBSettingsLoader.getInstance().saveUserData(userData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showHideTiles(MenuItem item) {
		DebugSettings ds = item.isChecked() ? dsDefault : dsTiles;
		mapView.setDebugSettings(ds);
		mapView.redrawMap(true);
	}

	public void updateCusMeter(CusMeter cm, int action) throws Throwable {
		try {
			DBLoader.getInstance().saveCusMeter(cm, userData.getPcity(),
					action, userData.getUserid());
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
			throw e;
		}
	}

	public void updateNewBuilding(NewBuilding newBuilding) throws Throwable {
		try {
			DBSettingsLoader.getInstance().proceedNewBuilding(newBuilding,
					false);
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
			throw e;
		}
	}

}
