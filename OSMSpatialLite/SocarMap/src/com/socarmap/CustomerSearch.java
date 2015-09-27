package com.socarmap;

import java.util.ArrayList;
import java.util.HashMap;

import org.oscim.core.GeoPoint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.socarmap.db.DBLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.BarcodeLauncher;
import com.socarmap.helper.IntentIntegrator;
import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.Customer;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.Meter;
import com.socarmap.ui.CustArrayAdapter;
import com.socarmap.ui.IDValueAdapter;
import com.socarmap.ui.MapSelectionOverlay;

@SuppressLint("NewApi")
public class CustomerSearch extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnItemLongClickListener {

	public static final String ZONE = "ZONE";
	public static final String REGION_ID = "BC_REGION_ID";
	public static final String SUBREGION_ID = "BC_SUBREGION_ID";
	public static final String REGS_DISABLE = "REGS_DISABLE";

	public static void setSpinnerValues(Spinner spinner, Long id, String value,
			OnItemSelectedListener listener) {
		HashMap<Long, String> map = new HashMap<Long, String>();
		map.put(id, value);
		setupSpinnerFromMap(spinner, map, listener);
		spinner.setSelection(0);
	}
	private Spinner spRegion;
	private Spinner spSubregions;
	private Spinner spZones;
	private EditText teCusID;
	private ListView lvCustomers;
	private CheckBox cbWith_buildings;
	private Button btnSearchForCustomer;
	private Long cus_type_id;
	private GeoPoint geoPoint;

	private int requestCode;
	private Long region_id;
	private Long subregion_id;
	private Long zone;
	private boolean regs_disabled = true;

	private boolean setting_myself = false;

	public static final int REQUEST_CODE = 10000; // Only use bottom 16

	public static void setupSpinnerFromMap(Spinner spinner,
			HashMap<Long, String> map, OnItemSelectedListener listener) {
		IDValueAdapter adapter = new IDValueAdapter(map, spinner.getContext());
		spinner.setAdapter(adapter);
		if (listener != null)
			spinner.setOnItemSelectedListener(listener);
	}

	public static void setupSpinnerFromValues(Spinner spinner,
			ArrayList<IDValue> values, OnItemSelectedListener listener) {
		IDValueAdapter adapter = new IDValueAdapter(values,
				spinner.getContext());
		spinner.setAdapter(adapter);
		if (listener != null)
			spinner.setOnItemSelectedListener(listener);
	}

	public static void showCustomerDetails(final GeoPoint geoPoint,
			final CusShort cust, Context context, final Activity activity,
			int requestCode) {
		Customer customer = null;
		ArrayList<Balance> balances = null;
		ArrayList<Meter> meters = null;
		try {
			customer = DBLoader.getInstance().getCustomerFull(
					cust.getCus_id().intValue());
			if (customer == null)
				throw new Exception("Customer is null!!!");
			meters = customer.getMeters();
			balances = customer.getBalances();
			meters = meters == null ? DBLoader.getInstance().loadMeters(
					cust.getCus_id()) : meters;
			balances = balances == null ? DBLoader.getInstance().loadBalance(
					cust.getCus_id()) : balances;
		} catch (Throwable e) {
			ActivityHelper.showAlert(context, e);
			return;
		}

		AlertDialog.Builder screenDialog = new AlertDialog.Builder(context);
		screenDialog.setTitle("Customer details");

		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

		if (geoPoint == null && cust.getBuilding_id() != null
				&& cust.getBuilding_id().longValue() > 0 && activity != null) {
			Button bGoToBuilding = new Button(context);
			bGoToBuilding.setText("Go to building");
			buttonLayout.addView(bGoToBuilding);

			bGoToBuilding.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View paramView) {
					Intent intent = new Intent();
					intent.putExtra("cusid", cust.getCus_id() + "");
					intent.putExtra("buildingid", cust.getBuilding_id() + "");
					if (activity.getParent() == null) {
						activity.setResult(Activity.RESULT_OK, intent);
					} else {
						activity.getParent().setResult(Activity.RESULT_OK,
								intent);
					}

					activity.finish();
				}
			});
		}
		LinearLayout dialogLayout = new LinearLayout(context);
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		if (geoPoint != null) {
			final Spinner spMeters = new Spinner(context);
			try {

				if (meters != null && !meters.isEmpty()) {
					HashMap<Long, String> map = new HashMap<Long, String>();
					for (Meter meter : meters) {
						map.put((long) meter.getMeterid(), meter.toString());
					}
					setupSpinnerFromMap(spMeters, map, null);
					Button bAddMeter = new Button(context);
					String text = "";
					if (requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER)
						text = context.getString(R.string.add_distinct_meter);
					else if (requestCode == MapSelectionOverlay.REQUEST_CODE_CUS_METER)
						text = context.getString(R.string.add_cus_meter);
					final boolean iscustomer = requestCode == MapSelectionOverlay.REQUEST_CODE_CUS_METER;
					bAddMeter.setText(text);
					LinearLayout metterLayout = new LinearLayout(context);
					metterLayout.setOrientation(LinearLayout.HORIZONTAL);
					metterLayout.addView(spMeters);
					metterLayout.addView(bAddMeter);
					dialogLayout.addView(metterLayout);
					bAddMeter.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View paramView) {
							try {
								Long meter_id = ((IDValue) spMeters
										.getSelectedItem()).getId();
								Intent intent = new Intent();
								intent.putExtra("cusid", cust.getCus_id() + "");
								intent.putExtra("meter_id", meter_id + "");
								intent.putExtra("customer", iscustomer);
								intent.putExtra(
										MapSelectionOverlay.REQUEST_POINT,
										geoPoint);
								if (activity.getParent() == null) {
									activity.setResult(Activity.RESULT_OK,
											intent);
								} else {
									activity.getParent().setResult(
											Activity.RESULT_OK, intent);
								}

								activity.finish();

							} catch (Exception e) {
							}

						}
					});
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		dialogLayout.addView(buttonLayout);

		View customerView = CustomerDetailFull.createCustomerView(context,
				customer, balances, meters, MainActivity.getUserData()
						.getUserid());
		dialogLayout.addView(customerView);
		screenDialog.setView(dialogLayout);

		screenDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		screenDialog.show();
	}

	private ArrayList<CusShort> customers;

	private void makeSearch() {
		if (this.getIntent() == null)
			return;
		if (this.getIntent().getExtras() == null)
			return;
		Bundle bundle = this.getIntent().getExtras();

		requestCode = bundle.getInt(MapSelectionOverlay.REQUEST_CODE, -1);
		region_id = bundle.getLong(REGION_ID, -1L);
		regs_disabled = !bundle.containsKey(REGS_DISABLE);
		if (region_id.longValue() >= 1) {
			try {
				setting_myself = true;
				if (regs_disabled)
					spRegion.setEnabled(false);
				String value = DBLoader.getInstance().regions.get(region_id);
				setSpinnerValues(spRegion, region_id, value, this);
				spRegion.setSelection(0);
				subregion_id = bundle.getLong(SUBREGION_ID, -1L);
				HashMap<Long, String> map = DBLoader.getInstance().subregions
						.get(region_id);
				if (subregion_id.longValue() >= 1) {
					if (regs_disabled)
						spSubregions.setEnabled(false);

					if (map != null) {
						value = map.get(subregion_id);
						setSpinnerValues(spSubregions, subregion_id, value,
								this);
						zone = bundle.getLong(ZONE, -1L);
						map = DBLoader.getInstance().zones.get(subregion_id);
						if (zone.longValue() >= 1) {
							spZones.setEnabled(false);

							if (map != null) {
								value = map.get(zone);
								setSpinnerValues(spZones, zone, value, this);
								searchForCustomers();
							}
						} else {
							// setting_myself = false;
							setupSpinnerFromMap(spZones, map, this);
						}
					}
				} else {
					setting_myself = false;
					setupSpinnerFromMap(spSubregions, map, this);
				}
			} finally {
				// setting_myself = false;
			}
		}
		if (requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER
				|| requestCode == BuildingCustomers.BC_REQUEST_CODE) {
			cus_type_id = requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER ? -100L
					: null;
			cbWith_buildings.setChecked(false);
			cbWith_buildings.setEnabled(false);
		}
		Parcelable pa = bundle.getParcelable(MapSelectionOverlay.REQUEST_POINT);
		if (pa == null)
			return;
		geoPoint = (GeoPoint) pa;
		if (requestCode != MapSelectionOverlay.REQUEST_CODE_CUS_METER
				&& requestCode != MapSelectionOverlay.REQUEST_CODE_DIST_METER) {
			geoPoint = null;
			return;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				// IntentResult scanResult =
				// IntentIntegrator.parseActivityResult(
				// requestCode, resultCode, data);
				String scanResult = intent.getStringExtra("SCAN_RESULT");
				if (scanResult != null) {
					// String out = scanResult.getContents();
					String out = scanResult;
					try {
						Long cus_id = MainActivity.getCustomer_id(out);
						teCusID.setText("" + cus_id);
						searchForCustomers();
					} catch (Exception e) {
						ActivityHelper.showAlert(this, e);
					}
				}

				break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_search);
		try {
			setting_myself = false;
			spRegion = (Spinner) findViewById(R.id.sp_region);
			spSubregions = (Spinner) findViewById(R.id.sp_subregion);
			spZones = (Spinner) findViewById(R.id.sp_zone);
			teCusID = (EditText) findViewById(R.id.te_cus_id);
			lvCustomers = (ListView) findViewById(R.id.lv_customers);
			cbWith_buildings = (CheckBox) findViewById(R.id.chb_with_buildings);
			btnSearchForCustomer = (Button) findViewById(R.id.btn_search_cust);
			btnSearchForCustomer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View paramView) {
					searchForCustomers();

				}
			});
			cus_type_id = null;
			setupSpinnerFromMap(spRegion, DBLoader.getInstance().regions, this);
			setupSpinnerFromMap(spSubregions, (HashMap<Long, String>) null,
					this);
			setupSpinnerFromMap(spZones, (HashMap<Long, String>) null, this);

		} catch (Exception e) {
			ActivityHelper.showAlert(this, e);
		}
		makeSearch();
		if (requestCode == BuildingCustomers.BC_REQUEST_CODE) {
			lvCustomers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			lvCustomers.setOnItemLongClickListener(this);
			Button bSetSelection = (Button) findViewById(R.id.btn_search_select);
			bSetSelection.setVisibility(View.VISIBLE);
			bSetSelection.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectCustomers();

				}
			});
		} else
			lvCustomers.setOnItemClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_customer_search, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View v,
			int position, long id) {

		OpenScreenDialog(position);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		OpenScreenDialog(position);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		if (setting_myself)
			return;

		IDValue g = (IDValue) parent.getItemAtPosition(pos);
		if (view.equals(spRegion) || parent.equals(spRegion)) {
			setupSpinnerFromMap(spSubregions,
					DBLoader.getInstance().subregions.get(g.getId()), this);
			setupSpinnerFromMap(spZones, null, this);
			spSubregions.setSelection(Adapter.NO_SELECTION);
			spZones.setSelection(Adapter.NO_SELECTION);
		}
		if (view.equals(spSubregions) || parent.equals(spSubregions)) {
			setupSpinnerFromMap(spZones,
					DBLoader.getInstance().zones.get(g.getId()), this);
			spZones.setSelection(Adapter.NO_SELECTION);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.mi_search:
			searchForCustomers();
			return true;
		case R.id.mi_barcode:
			startBarcode();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void OpenScreenDialog(final int position) {
		final CusShort cust = (CusShort) lvCustomers
				.getItemAtPosition(position);
		if (cust == null)
			return;
		showCustomerDetails(geoPoint, cust, this, this, requestCode);
	}

	private void searchForCustomers() {

		Long subregion_id = null;
		Long zone = null;
		Long customer_id = null;
		try {
			customer_id = Long.parseLong(teCusID.getText().toString());
		} catch (Exception e) {
		}
		try {

			subregion_id = ((IDValue) spSubregions.getSelectedItem()).getId();

			zone = ((IDValue) spZones.getSelectedItem()).getId();

		} catch (Exception e) {
		}
		if (customer_id == null && !(subregion_id != null && zone != null)) {
			ActivityHelper.showAlert(this,
					"Please select cusid or subregion and zone!!! ");
			return;
		}
		try {
			boolean with_buildings = cbWith_buildings.isChecked();
			ArrayList<CusShort> customers = null;
			if (geoPoint == null)
				customers = DBLoader.getInstance().getCustomers(subregion_id,
						zone, customer_id, with_buildings, cus_type_id, null,
						requestCode == BuildingCustomers.BC_REQUEST_CODE);
			else {
				if (requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER)
					customers = DBLoader.getInstance()
							.getCustomersForDistinctMeter(subregion_id, zone,
									customer_id);
				else if (requestCode == MapSelectionOverlay.REQUEST_CODE_CUS_METER)
					customers = DBLoader.getInstance().getCustomersForMeter(
							subregion_id, zone, customer_id);
				else
					customers = new ArrayList<CusShort>();
			}
			this.customers = customers;
			if (requestCode == BuildingCustomers.BC_REQUEST_CODE) {
				lvCustomers.setAdapter(new CustArrayAdapter(this,
						android.R.layout.simple_list_item_checked, customers));
			} else
				lvCustomers.setAdapter(new ArrayAdapter<CusShort>(this,
						android.R.layout.simple_expandable_list_item_1,
						customers));
		} catch (Exception e) {
			ActivityHelper.showAlert(this, e);
		}

	}

	protected void selectCustomers() {

		long[] ids = lvCustomers.getCheckedItemIds();

		if (ids == null || ids.length == 0) {
			ActivityHelper.showAlert(this,
					getString(R.string.select_at_least_one_cust));
			return;
		}
		ArrayList<CusShort> selectedCustomers = new ArrayList<CusShort>();

		if (customers != null) {
			for (CusShort cusShort : customers) {
				for (long l : ids) {
					if (cusShort.getCus_id().longValue() == l) {
						selectedCustomers.add(cusShort);
						break;
					}
				}
			}
		}

		Intent intent = new Intent();
		intent.putExtra("cus_ids", ids);
		intent.putExtra("selectedCustomers", selectedCustomers);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, intent);
		} else {
			getParent().setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	private void startBarcode() {
		// new IntentIntegrator(this).initiateScan();
		// Intent intent = new Intent("com.google.saxing.client.android.SCAN");
		// intent.setPackage("com.google.saxing.client.android");
		// startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
		BarcodeLauncher.scanBarCode(this);
	}

}
