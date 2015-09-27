package com.socarmap;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.socarmap.helper.MeterBRowArrayAdapter;
import com.socarmap.proxy.beans.Balance;
import com.socarmap.proxy.beans.Customer;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.Meter;

public class CustomerDetailFull {

	public static View createCustomerView(Context context, Customer customer,
			ArrayList<Balance> balances, ArrayList<Meter> meters, int user_id) {

		LayoutInflater inflater = LayoutInflater.from(context);
		final View convertView = inflater.inflate(R.layout.customer_detail,
				null);
		setText(convertView, R.id.ti_cusid, customer.getCusid() + "");
		setText(convertView, R.id.ti_cusname, customer.getCusname());
		setText(convertView, R.id.ti_region, customer.getRegion());
		setText(convertView, R.id.ti_raion, customer.getRaion());
		setText(convertView, R.id.ti_zone, customer.getZone() + "");
		setText(convertView, R.id.ti_cityname, customer.getCityname());
		setText(convertView, R.id.ti_cusstatusname, customer.getCusstatusname());
		setText(convertView, R.id.ti_custypename, customer.getCustypename());
		setText(convertView, R.id.ti_address, customer.getAddress());

		final ArrayAdapter<?> adapterB = new ArrayAdapter<Balance>(context,
				android.R.layout.simple_expandable_list_item_1, balances);
		ArrayList<IDValue> items = new ArrayList<IDValue>();
		for (Meter meter : meters) {
			items.add(new IDValue(meter.getMeterid(), meter.toString()));
		}
		// ArrayAdapter<?> adapterM = new ArrayAdapter<Meter>(context,
		// android.R.layout.simple_expandable_list_item_1, meters);

		final ArrayAdapter<?> adapterM = new MeterBRowArrayAdapter(context,
				items, user_id);

		setArray(convertView, R.id.lv_customer_detail_grid, adapterB);
		ToggleButton button = (ToggleButton) convertView
				.findViewById(R.id.tb_customer_grid);
		button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				setArray(convertView, R.id.lv_customer_detail_grid,
						isChecked ? adapterM : adapterB);

			}
		});
		return convertView;
	}

	private static void setArray(View convertView, int id,
			ArrayAdapter<?> adapter) {
		ListView textView = (ListView) convertView.findViewById(id);
		if (textView != null)
			textView.setAdapter(adapter);
	}

	private static void setText(View convertView, int id, String text) {
		TextView textView = (TextView) convertView.findViewById(id);
		if (textView != null)
			textView.setText(text);
	}

	// private void OpenScreenDialog(final int position) {
	// final CusShort cust = (CusShort) lvCustomers
	// .getItemAtPosition(position);
	// if (cust == null)
	// return;
	// AlertDialog.Builder screenDialog = new AlertDialog.Builder(this);
	// screenDialog.setTitle("Customer details");
	//
	// LinearLayout buttonLayout = new LinearLayout(this);
	// buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
	//
	// Button bShowBalance = new Button(this);
	// bShowBalance.setText("Balances");
	// Button bShowMetters = new Button(this);
	// bShowMetters.setText("Metters");
	// buttonLayout.addView(bShowBalance);
	// buttonLayout.addView(bShowMetters);
	// final Intent i = new Intent(this, CustomerDetail.class);
	// i.putExtra("cusid", cust.getCus_id());
	// bShowBalance.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View paramView) {
	// i.putExtra("detail_type", CustomerDetail.DT_BALANCE);
	// startActivity(i);
	// }
	// });
	//
	// bShowMetters.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View paramView) {
	// i.putExtra("detail_type", CustomerDetail.DT_METER);
	// startActivity(i);
	// }
	// });
	//
	// if (geoPoint == null && cust.getBuilding_id() != null
	// && cust.getBuilding_id().longValue() > 0) {
	// Button bGoToBuilding = new Button(this);
	// bGoToBuilding.setText("Go to building");
	// buttonLayout.addView(bGoToBuilding);
	//
	// bGoToBuilding.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View paramView) {
	// Intent intent = new Intent();
	// intent.putExtra("cusid", cust.getCus_id() + "");
	// intent.putExtra("buildingid", cust.getBuilding_id() + "");
	// if (getParent() == null) {
	// setResult(Activity.RESULT_OK, intent);
	// } else {
	// getParent().setResult(Activity.RESULT_OK, intent);
	// }
	//
	// finish();
	// }
	// });
	// }
	// LinearLayout dialogLayout = new LinearLayout(this);
	// if (geoPoint != null) {
	// final Spinner spMeters = new Spinner(this);
	// try {
	// ArrayList<Meter> meters = DBLoader.getInstance().loadMeters(
	// cust.getCus_id());
	// if (meters != null && !meters.isEmpty()) {
	// TreeMap<Long, String> map = new TreeMap<Long, String>();
	// for (Meter meter : meters) {
	// map.put((long) meter.getMeterid(), meter.toString());
	// }
	// setupSpinnerFromMap(spMeters, map);
	// Button bAddMeter = new Button(this);
	// String text = "";
	// if (requestCode == MapSelectionOverlay.REQUEST_CODE_DIST_METER)
	// text = getString(R.string.add_cus_meter);
	// else if (requestCode == MapSelectionOverlay.REQUEST_CODE_CUS_METER)
	// text = getString(R.string.add_distinct_meter);
	// final boolean customer = requestCode ==
	// MapSelectionOverlay.REQUEST_CODE_CUS_METER;
	// bAddMeter.setText(text);
	// LinearLayout metterLayout = new LinearLayout(this);
	// metterLayout.setOrientation(LinearLayout.HORIZONTAL);
	// metterLayout.addView(spMeters);
	// metterLayout.addView(bAddMeter);
	// dialogLayout.addView(metterLayout);
	// bAddMeter.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View paramView) {
	// try {
	// Long meter_id = ((IDValue) spMeters
	// .getSelectedItem()).getId();
	// Intent intent = new Intent();
	// intent.putExtra("cusid", cust.getCus_id() + "");
	// intent.putExtra("meter_id", meter_id + "");
	// intent.putExtra("customer", customer);
	// intent.putExtra(
	// MapSelectionOverlay.REQUEST_POINT,
	// (Parcelable) geoPoint);
	// if (getParent() == null) {
	// setResult(Activity.RESULT_OK, intent);
	// } else {
	// getParent().setResult(Activity.RESULT_OK,
	// intent);
	// }
	//
	// finish();
	//
	// } catch (Throwable e) {
	// }
	//
	// }
	// });
	// }
	//
	// } catch (Throwable e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// TextView tvCustomer = new TextView(getBaseContext());
	// tvCustomer.setText(cust.toString());
	//
	// dialogLayout.setOrientation(LinearLayout.VERTICAL);
	//
	// dialogLayout.addView(tvCustomer);
	// dialogLayout.addView(buttonLayout);
	// screenDialog.setView(dialogLayout);
	//
	// screenDialog.setPositiveButton("OK",
	// new DialogInterface.OnClickListener() {
	// // do something when the button is clicked
	// public void onClick(DialogInterface arg0, int arg1) {
	//
	// }
	// });
	// screenDialog.show();
	// }
}
