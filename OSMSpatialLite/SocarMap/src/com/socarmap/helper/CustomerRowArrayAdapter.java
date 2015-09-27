package com.socarmap.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.socarmap.CustomerSearch;
import com.socarmap.proxy.beans.CusShort;
import com.socarmap.proxy.beans.IDValue;

public class CustomerRowArrayAdapter extends DeleteRowArrayAdapter {
	public CustomerRowArrayAdapter(Context context, ArrayList<IDValue> items) {
		super(context, items);

	}

	@Override
	public void operateText(IDValue planet, View v, int position) {
		CusShort cust = new CusShort(planet.getId(), planet.getValue(), 0L, 0L);
		try {
			CustomerSearch.showCustomerDetails(null, cust, getContext(), null,
					-199999999);
		} catch (Exception e) {
			ActivityHelper.showAlert(getContext(), e);
		}

	}
}
