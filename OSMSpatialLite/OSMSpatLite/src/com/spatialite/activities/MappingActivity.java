package com.spatialite.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.spatialite.R;
import com.spatialite.utilities.ActivityHelper;
import com.spatialite.utilities.Customer;

public class MappingActivity extends Activity implements OpenStreetMapConstants {

	private MapController mapController;
	private MyMapView mapView;
	private MyLocationOverlay mLocationOverlay;
	private ResourceProxy mResourceProxy;
	private MapSelectionOverlay selectionOverlay;
	private MarkersOverlay markersOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		String dbPath = getString(R.string.test_db);
		try {
			mResourceProxy = new ResourceProxyImpl(getApplicationContext());
			mapView = (MyMapView) findViewById(R.id.map);
			mapView.setTileSource(new XYTileSource("Mapnik",
					ResourceProxy.string.mapnik, 11, 18, 256, ".png",
					"http://tile.openstreetmap.org/"));
			mapView.setBuiltInZoomControls(true);
			mapView.setMultiTouchControls(true);

			this.mLocationOverlay = new MyLocationOverlay(
					this.getBaseContext(), mapView, mResourceProxy);
			mLocationOverlay.enableCompass();
			mLocationOverlay.disableMyLocation();
			mapView.getOverlays().add(this.mLocationOverlay);
			mapController = mapView.getController();
			mapController.setZoom(15);
			mapView.setUseDataConnection(false);
			GeoPoint boundsTopLeftCorner = new GeoPoint(41.0551605436933,
					45.0452185602457);
			GeoPoint boundsBottomLeftCorner = new GeoPoint(42.1314178243242,
					46.1199041955369);

			mapView.setScrollableAreaLimit(boundsTopLeftCorner,
					boundsBottomLeftCorner);
			mapController.setCenter(new GeoPoint(41.9666954087094,
					45.8389727460298));
			Drawable marker = getResources().getDrawable(R.drawable.marker);
			ImageView dragImage = (ImageView) findViewById(R.id.drag);
			markersOverlay = new MarkersOverlay(marker, mResourceProxy,
					dragImage, dbPath, mapView);
			selectionOverlay = new MapSelectionOverlay(this.getBaseContext(),
					dbPath, markersOverlay, mapView);
			mapView.getOverlays().add(selectionOverlay);

			mapView.getOverlays().add(markersOverlay);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();

			e.printStackTrace(new PrintWriter(sw));
			ActivityHelper.showAlert(this, "hhhh " + sw.toString());
		}
		try {
			DBLoader.initInstance(dbPath);
		} catch (Exception e) {
			ActivityHelper.showAlert(this, "Error loading db !!!");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.m_my_location:
			if (mLocationOverlay.isMyLocationEnabled()) {
				mLocationOverlay.disableFollowLocation();
				mLocationOverlay.disableMyLocation();
			} else {
				mLocationOverlay.enableFollowLocation();
				mLocationOverlay.enableMyLocation();
			}
			return true;
		case R.id.m_m_barcode:
			new IntentIntegrator(this).initiateScan();
			return true;
		case R.id.m_en_dis_zoom:
			mapView.setMotionEnabled(!mapView.isMotionEnabled());
			return true;
		case R.id.m_search:
			Intent myIntent = new Intent(this, CusromerSearch.class);
			startActivityForResult(myIntent, CusromerSearch.REQUEST_CODE);
			return true;
		case R.id.mi_building_select:
		case R.id.mi_cus_meter:
		case R.id.mi_distinct_meter:
			item.setChecked(!item.isChecked());
			selectionOverlay.setAddingenabled(item.isChecked()
					&& (item.getItemId() == R.id.mi_cus_meter || item
							.getItemId() == R.id.mi_distinct_meter));
			selectionOverlay.setSelectBuildingenabled(item.isChecked()
					&& (item.getItemId() == R.id.mi_building_select));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == -1 && requestCode == 10000 && data != null) {
			try {

				String sCusid = data.getStringExtra("cusid");
				Long cusid = null;

				if (sCusid != null) {
					try {
						cusid = Long.parseLong(sCusid.trim());
						findCustomer(cusid);
					} catch (Exception e) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						ActivityHelper.showAlert(getBaseContext(),
								"onActivityResult\n" + sw);
					}
				}

			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				ActivityHelper.showAlert(getBaseContext(), "onActivityResult\n"
						+ sw);
			}
			return;
		}

		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, data);
				if (scanResult != null) {
					String out = scanResult.getContents();
					if (out != null) {
						try {
							Long cus_id = Long.parseLong(out.trim());
							findCustomer(cus_id);
						} catch (Exception e) {
							ActivityHelper.showAlert(this,
									"Error while scanning customercode!!! "
											+ out + e.getMessage());
						}
					}
				}

			}
			break;
		}
		// case CusromerSearch.REQUEST_CODE:
		// if (resultCode == RESULT_OK) {
		// String sCusid = data.getStringExtra("cusid");
		// String sBuildingId = data.getStringExtra("buildingid");
		// Long cusid = null;
		// Long buildingId = null;
		// ActivityHelper.showAlert(getBaseContext(), "Setting result\n"
		// + sCusid + " " + sBuildingId);
		// if (sCusid != null && sBuildingId != null) {
		// try {
		// cusid = Long.parseLong(sCusid.trim());
		// buildingId = Long.parseLong(sCusid.trim());
		// findBuilding(cusid, buildingId);
		// } catch (Exception e) {
		// StringWriter sw = new StringWriter();
		// e.printStackTrace(new PrintWriter(sw));
		// ActivityHelper.showAlert(getBaseContext(),
		// "onActivityResult\n" + sw);
		// }
		// }
		// }
		// break;
		// }

	}

	private void findCustomer(Long cus_id) {
		try {
			ArrayList<Customer> list = DBLoader.getInstance().getCustomers(
					null, null, cus_id);
			if (list == null || list.isEmpty())
				throw new Exception("Cannot find customer " + cus_id + "!!!");
			Customer c = list.get(0);
			if (c.getBuilding_id() == null)
				ActivityHelper.showAlert(getBaseContext(), "Customer " + cus_id
						+ " has no building!!!");
			else
				findBuilding(c.getCus_id(), c.getBuilding_id());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ActivityHelper.showAlert(getBaseContext(), "findCustomer\n" + sw);
		}

	}

	private void findBuilding(Long cusid, Long buildingId) {
		selectionOverlay.find(cusid, buildingId);

	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();
		mapView.setUseDataConnection(checked);
	}
}
