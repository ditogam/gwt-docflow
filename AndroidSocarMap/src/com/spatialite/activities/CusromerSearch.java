package com.spatialite.activities;

import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.spatialite.R;
import com.spatialite.beans.Customer;
import com.spatialite.beans.IDValue;
import com.spatialite.db.DBLoader;
import com.spatialite.helpers.CustomerDetail;
import com.spatialite.helpers.IntentIntegrator;
import com.spatialite.helpers.IntentResult;
import com.spatialite.utilities.ActivityHelper;
import com.spatialite.utilities.IDValueAdapter;

@SuppressLint("NewApi")
public class CusromerSearch extends Activity implements OnItemSelectedListener,
		OnItemClickListener {
	private Spinner spRegion;
	private Spinner spSubregions;
	private Spinner spZones;
	private EditText teCusID;
	private ListView lvCustomers;

	public static final int REQUEST_CODE = 10000; // Only use bottom 16

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cusromer_search);
		try {
			spRegion = (Spinner) findViewById(R.id.sp_region);
			spSubregions = (Spinner) findViewById(R.id.sp_subregion);
			spZones = (Spinner) findViewById(R.id.sp_zone);
			teCusID = (EditText) findViewById(R.id.te_cus_id);
			lvCustomers = (ListView) findViewById(R.id.lv_customers);
			setupSpinnerFromMap(spRegion, DBLoader.getInstance().regions);
			setupSpinnerFromMap(spSubregions, null);
			setupSpinnerFromMap(spZones, null);

		} catch (Exception e) {
			ActivityHelper.showAlert(this, "Error " + e.getMessage());
		}
		lvCustomers.setOnItemClickListener(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cusromer_search, menu);
		return true;
	}

	private void setupSpinnerFromMap(Spinner spinner, TreeMap<Long, String> map) {
		IDValueAdapter adapter = new IDValueAdapter(map,
				getApplicationContext());
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

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

	private void startBarcode() {
		new IntentIntegrator(this).initiateScan();
	}

	private void searchForCustomers() {

		Long subregion_id = null;
		Long zone = null;
		Long customer_id = null;
		try {
			customer_id = Long.parseLong(teCusID.getText().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			subregion_id = ((IDValue) spRegion.getSelectedItem()).getId();
			zone = ((IDValue) spZones.getSelectedItem()).getId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (customer_id == null && !(subregion_id != null && zone != null)) {
			ActivityHelper.showAlert(this,
					"Please select cusid or subregion and zone!!! ");
			return;
		}
		try {
			// lvCustomers.setAdapter(new CustomerAdapter(DBLoader.getInstance()
			// .getCustomers(subregion_id, zone, customer_id),
			// getBaseContext()));

			lvCustomers.setAdapter(new ArrayAdapter<Customer>(this,
					android.R.layout.simple_expandable_list_item_1, DBLoader
							.getInstance().getCustomers(subregion_id, zone,
									customer_id)));
		} catch (Exception e) {
			ActivityHelper.showAlert(this, "Error while loading customers!!! "
					+ e.getMessage());
		}

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		IDValue g = (IDValue) parent.getItemAtPosition(pos);
		if (view.equals(spRegion) || parent.equals(spRegion)) {
			setupSpinnerFromMap(spSubregions,
					DBLoader.getInstance().subregions.get(g.getId()));
			setupSpinnerFromMap(spZones, null);
			spSubregions.setSelection(Adapter.NO_SELECTION);
			spZones.setSelection(Adapter.NO_SELECTION);
		}
		if (view.equals(spSubregions) || parent.equals(spSubregions)) {
			setupSpinnerFromMap(spZones,
					DBLoader.getInstance().zones.get(g.getId()));
			spZones.setSelection(Adapter.NO_SELECTION);
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, intent);
				if (scanResult != null) {
					String out = scanResult.getContents();
					if (out != null) {
						try {
							Long cus_id = Long.parseLong(out.trim());
							teCusID.setText("" + cus_id);
							searchForCustomers();
						} catch (Exception e) {
							ActivityHelper.showAlert(
									this,
									"Error while scanning customercode!!! "
											+ e.getMessage());
						}
					}
				}
				break;
			}
		}
	}

	private void OpenScreenDialog(final int position) {
		final Customer cust = (Customer) lvCustomers
				.getItemAtPosition(position);
		if (cust == null)
			return;
		AlertDialog.Builder screenDialog = new AlertDialog.Builder(this);
		screenDialog.setTitle("Customer details");

		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

		Button bShowBalance = new Button(this);
		bShowBalance.setText("Balances");
		Button bShowMetters = new Button(this);
		bShowMetters.setText("Metters");
		buttonLayout.addView(bShowBalance);
		buttonLayout.addView(bShowMetters);
		final Intent i = new Intent(this, CustomerDetail.class);
		i.putExtra("cusid", cust.getCus_id());
		bShowBalance.setOnClickListener(new View.OnClickListener() {

			public void onClick(View paramView) {
				i.putExtra("detail_type", CustomerDetail.DT_BALANCE);
				startActivity(i);
			}
		});

		bShowMetters.setOnClickListener(new View.OnClickListener() {

			public void onClick(View paramView) {
				i.putExtra("detail_type", CustomerDetail.DT_METER);
				startActivity(i);
			}
		});

		if (cust.getBuilding_id() != null) {
			Button bGoToBuilding = new Button(this);
			bGoToBuilding.setText("Go to building");
			buttonLayout.addView(bGoToBuilding);

			bGoToBuilding.setOnClickListener(new View.OnClickListener() {

				public void onClick(View paramView) {
					Intent intent = new Intent();
					intent.putExtra("cusid", cust.getCus_id() + "");
					intent.putExtra("buildingid", cust.getBuilding_id() + "");
					if (getParent() == null) {
						setResult(Activity.RESULT_OK, intent);
					} else {
						getParent().setResult(Activity.RESULT_OK, intent);
					}

					finish();
				}
			});
		}

		TextView tvCustomer = new TextView(getBaseContext());
		tvCustomer.setText(cust.toString());
		LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(LinearLayout.VERTICAL);

		dialogLayout.addView(tvCustomer);
		dialogLayout.addView(buttonLayout);
		screenDialog.setView(dialogLayout);

		screenDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		screenDialog.show();
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View v,
			int position, long id) {
		OpenScreenDialog(position);

	}
}
