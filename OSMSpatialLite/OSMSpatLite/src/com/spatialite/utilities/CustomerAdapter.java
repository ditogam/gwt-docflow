package com.spatialite.utilities;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.spatialite.R;

public class CustomerAdapter extends ArrayAdapter<Customer> {
	private Context appContext;

	public CustomerAdapter(ArrayList<Customer> items, Context appContext) {
		super(appContext, R.id.tv_cusname, items);
		this.appContext = appContext;
		if (items == null)
			items = new ArrayList<Customer>();
	}

	public void showAtivitie(View convertView) {
//		try {
//			final Customer value = (Customer) convertView.getTag();
//			String val = value == null ? "Unknown customer" : (value
//					.getCus_id() + " " + value.getBuilding_id() + " = " + value
//					.getCusname());
////			ActivityHelper.showAlert(getContext(), val);
//		} catch (Exception e) {
////			ActivityHelper.showAlert(getContext(), e.toString());
//		}
		//
	}

	@Override
	public View getView(final int position, final View convertView,
			ViewGroup parent) {
		View aView = null;

		if (convertView == null) {
			LayoutInflater aInflater = LayoutInflater.from(appContext);
			aView = aInflater
					.inflate(R.layout.cus_search_result, parent, false);
		} else {
			aView = convertView;
		}

		final Customer value = (Customer) getItem(position);

		TextView customer = (TextView) aView.findViewById(R.id.tv_cusname);
		CheckBox bCheck = (CheckBox) aView
				.findViewById(R.id.cb_building_assign);
		Button bGoToBuilding = (Button) aView
				.findViewById(R.id.b_go_to_building);
		if (value != null) {
			customer.setText(value.getCusname());
			bCheck.setChecked(value.getBuilding_id() != null);
			bGoToBuilding.setEnabled(value.getBuilding_id() != null);

			if (value.getBuilding_id() != null)
				bGoToBuilding.setTag(value);
			else
				bGoToBuilding.setTag(null);
		}

		return aView;
	}

}
