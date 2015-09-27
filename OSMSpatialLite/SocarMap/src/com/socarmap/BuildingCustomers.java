package com.socarmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import org.oscim.core.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.GeoPointHelper;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.NewBuilding;
import com.socarmap.proxy.beans.ZXYData;
import com.socarmap.ui.CustArrayAdapter;
import com.socarmap.ui.MapSelectionOverlay;
import com.socarmap.utils.Utils;

public class BuildingCustomers extends Activity implements
		OnItemLongClickListener {

	public static final String BC_BUILDING_ID = "BC_BUILDING_ID";
	public static final String BC_BUILDING_ADD_ID = "BC_BUILDING_ADD_ID";
	public static final String BC_REGION_ID = "BC_REGION_ID";
	public static final String BC_SUBREGION_ID = "BC_SUBREGION_ID";
	public static final String BC_IS_NEW_BUILDING = "BC_IS_NEW_BUILDING";

	public static final int BC_REQUEST_CODE = 100078;

	private ListView lv_bCustomers;
	private Long building_id;
	private String building_add_id;
	private Long region_id;
	private Long subregion_id;
	private Long zone;
	private boolean isnewBuilding = false;

	private GeoPoint geoPoint;

	private TreeMap<Long, Long> cusIds;
	private TreeMap<Long, Long> original;
	private ArrayList<CusShort> customers;

	private void ask_remove_selected_customers(final boolean empty) {
		long[] ids = lv_bCustomers.getCheckedItemIds();
		if (ids == null || ids.length == 0)
			return;
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					remove_selected_customers(empty);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getString(empty ? R.string.do_you_want_to_empty
						: R.string.do_you_want_to_delete))
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();

	}

	public void buttonClicked(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_cancel_cus_to_building:
			finish();
			break;
		case R.id.btn_save_cus_to_building:
			save();
			break;
		case R.id.btn_cus_add_to_building:
			searchForCustomers();
			break;
		case R.id.btn_delete_cus_to_building:
			ask_remove_selected_customers(false);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			try {
				long[] ids = data.getLongArrayExtra("cus_ids");
				ArrayList<CusShort> selectedCustomers = (ArrayList<CusShort>) data
						.getSerializableExtra("selectedCustomers");
				if (ids == null || ids.length == 0)
					return;

				for (long l : ids) {
					cusIds.put(l, l);
				}

				ArrayList<CusShort> toAdd = new ArrayList<CusShort>();

				for (CusShort s : selectedCustomers) {
					boolean add = true;
					for (CusShort c : customers) {
						if (c.getCus_id().longValue() == s.getCus_id()
								.longValue()) {
							add = false;
							break;
						}
					}
					if (add)
						toAdd.add(s);
				}
				for (CusShort s : toAdd) {
					customers.add(s);
				}
				setCustomers(customers);
			} catch (Throwable e) {
				ActivityHelper.showAlert(this, e);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cusIds = new TreeMap<Long, Long>();
		original = new TreeMap<Long, Long>();
		if (this.getIntent() == null) {
			finish();
			return;
		}
		if (this.getIntent().getExtras() == null) {
			finish();
			return;
		}
		Bundle bundle = this.getIntent().getExtras();
		building_add_id = bundle.getString(BC_BUILDING_ADD_ID, null);
		isnewBuilding = bundle.getBoolean(BC_IS_NEW_BUILDING, false);
		building_id = bundle.getLong(BC_BUILDING_ID, -1L);
		if (building_id.longValue() < 1 && building_add_id == null) {
			finish();
			return;
		}
		region_id = bundle.getLong(BC_REGION_ID, -1L);
		if (region_id.longValue() < 1) {
			finish();
			return;
		}
		subregion_id = bundle.getLong(BC_SUBREGION_ID, -1L);
		if (subregion_id.longValue() < 1) {
			finish();
			return;
		}

		Parcelable pa = bundle.getParcelable(MapSelectionOverlay.REQUEST_POINT);
		geoPoint = (GeoPoint) pa;

		setContentView(R.layout.activity_building_customers);
		lv_bCustomers = (ListView) findViewById(R.id.lv_b_customers);
		lv_bCustomers.setOnItemLongClickListener(this);
		lv_bCustomers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		refreshList();

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		OpenScreenDialog(position);
		return false;
	}

	private void OpenScreenDialog(final int position) {
		final CusShort cust = (CusShort) lv_bCustomers
				.getItemAtPosition(position);
		if (cust == null)
			return;
		CustomerSearch.showCustomerDetails(null, cust, this, null, -199999999);
	}

	private void refreshList() {
		ArrayList<CusShort> customers = null;
		try {
			if (building_id.longValue() > 0)
				customers = DBLoader.getInstance().getCustomers(subregion_id,
						null, null, true, null, building_id, false);
			else
				customers = DBLoader.getInstance().getCustomersForNewBuilding(
						building_add_id);
			if (customers == null)
				customers = new ArrayList<CusShort>();
			if (!customers.isEmpty())
				zone = customers.get(0).getZone();
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
			finish();
			return;
		}
		this.customers = customers;
		for (CusShort customer : customers) {
			cusIds.put(customer.getCus_id(), customer.getCus_id());
			original.put(customer.getCus_id(), customer.getCus_id());
		}
		setCustomers(customers);
	}

	protected void remove_selected_customers(final boolean empty) {
		long[] ids = lv_bCustomers.getCheckedItemIds();
		if (ids == null || ids.length == 0)
			return;
		TreeMap<Long, Long> cusIds = new TreeMap<Long, Long>(this.cusIds);
		ArrayList<CusShort> toRemove = new ArrayList<CusShort>();
		for (long l : ids) {

			for (CusShort customer : customers) {
				if (customer.getCus_id().longValue() == l)
					toRemove.add(customer);
			}
			cusIds.remove(l);
		}
		if (!empty && cusIds.isEmpty()) {
			ask_remove_selected_customers(true);
			return;
		}
		for (CusShort customer : toRemove) {
			customers.remove(customer);
		}
		this.cusIds = cusIds;
		setCustomers(customers);
	}

	private void save() {

		try {
			updateBuildings(cusIds);
		} catch (Exception e) {
			ActivityHelper.showAlert(this, e);
		}

		finish();

	}

	private void searchForCustomers() {
		Intent i = new Intent(this, CustomerSearch.class);
		i.putExtra(CustomerSearch.REGION_ID, region_id);
		i.putExtra(CustomerSearch.SUBREGION_ID, subregion_id);
		if (zone != null)
			i.putExtra(CustomerSearch.ZONE, zone);

		i.putExtra(MapSelectionOverlay.REQUEST_CODE, BC_REQUEST_CODE);
		startActivityForResult(i, BC_REQUEST_CODE);

	}

	private void setCustomers(ArrayList<CusShort> customers) {
		lv_bCustomers.setAdapter(new CustArrayAdapter(this,
				android.R.layout.simple_list_item_multiple_choice, customers));
	}

	private String sortAndToString(TreeMap<Long, Long> cusIds) {
		String ret = "";
		cusIds = cusIds == null ? new TreeMap<Long, Long>() : cusIds;
		ArrayList<Long> ids = new ArrayList<Long>(cusIds.values());
		Collections.sort(ids);
		for (Long id : ids) {
			if (!ret.isEmpty())
				ret += ",";
			ret += id;
		}
		return ret;
	}

	private void updateBuildings(TreeMap<Long, Long> cusIds) throws Exception {
		String origilan = sortAndToString(original);
		String toSave = sortAndToString(cusIds);
		if (origilan.equals(toSave))
			return;
		int[] cus_ids = new int[cusIds.size()];
		int i = 0;
		for (Long l : cusIds.keySet()) {
			cus_ids[i++] = l.intValue();
		}

		if (building_id.longValue() > 0) {

			ArrayList<ZXYData> list = DBLoader.getInstance().updateBuilding(
					building_id.intValue(), cus_ids);
			Utils.proceedZXYData(list, this);
			MainActivity.instance.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					MainActivity.instance.mapView.clearMap();
					MainActivity.instance.mapView.redrawMap(true);

				}
			});
			refreshList();
		} else {
			String sCus_ids = "";
			for (int cus_id : cus_ids) {
				if (sCus_ids.length() > 0)
					sCus_ids += ",";
				sCus_ids += cus_id;
			}
			NewBuilding newBuilding = DBSettingsLoader.getInstance()
					.getForNewBuilding(building_add_id);
			if (newBuilding == null) {
				newBuilding = new NewBuilding(building_add_id,
						GeoPointHelper.toMGeoPoint(geoPoint), cus_ids,
						sCus_ids, region_id.intValue(), subregion_id.intValue());
			}
			newBuilding.setScus_ids(sCus_ids);
			DBSettingsLoader.getInstance().proceedNewBuilding(newBuilding,
					false);

			Intent intent = new Intent();
			intent.putExtra("newBuilding", newBuilding);
			intent.putExtra("isnewBuilding", isnewBuilding);

			if (getParent() == null) {
				setResult(Activity.RESULT_OK, intent);
			} else {
				getParent().setResult(Activity.RESULT_OK, intent);
			}

		}
	}
}
