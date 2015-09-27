package com.socarmap.ui;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.socarmap.proxy.beans.CusShort;

public class CustArrayAdapter extends ArrayAdapter<CusShort> {

	private List<CusShort> objects;

	public CustArrayAdapter(Context context, int textViewResourceId,
			List<CusShort> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	@Override
	public long getItemId(int position) {
		return objects.get(position).getCus_id();
	}

	@Override
	public boolean hasStableIds() {
		super.hasStableIds();
		return true;
	}

}
