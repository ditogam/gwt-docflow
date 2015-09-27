package com.docflowdroid.comp.ds;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.docflowdroid.R;

public class ListViewAdapter extends ArrayAdapter<ListGridRecordHelper> {
	protected ListGrid grid;

	public ListViewAdapter(Context context, ListGrid grid) {
		super(context, R.layout.lg_row);
		this.grid = grid;
	}

	public void setData(ArrayList<ListGridRecordHelper> data) {
		clear();
		addAll(data);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ListGridRecordHelper item = getItem(position);
		if (item == null)
			return super.getView(position, convertView, parent);
		View cv = item.getView(getContext());
		if (cv == null) {

			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cv = (LinearLayout) inflater.inflate(R.layout.lg_row, null);

		}
		return cv;
	}

}
