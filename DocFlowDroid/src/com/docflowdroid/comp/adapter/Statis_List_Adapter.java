package com.docflowdroid.comp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.common.shared.ClSelectionItem;
import com.docflowdroid.R;
import com.docflowdroid.comp.StatusBox;

public class Statis_List_Adapter extends ArrayAdapter<ClSelectionItem> {
	private ArrayList<ClSelectionItem> items;

	public Statis_List_Adapter(Context context, ArrayList<ClSelectionItem> items) {
		super(context, R.layout.status_row, items);
		this.items = items;
	}

	public ArrayList<ClSelectionItem> getItems() {
		return items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ClSelectionItem item = getItem(position);
		if (item == null)
			return super.getView(position, convertView, parent);
		return getCustomView(convertView, item);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ClSelectionItem item = getItem(position);
		if (item == null)
			return super.getView(position, convertView, parent);
		return getCustomView(convertView, item);
	}

	private View getCustomView(View convertView, ClSelectionItem item) {
		try {

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.status_row, null);
				StatusBox box = (StatusBox) convertView
						.findViewById(R.id.dl_status_box_item);
				TextView tv = (TextView) convertView
						.findViewById(R.id.dl_status_box_text);
				convertView.setTag(new Statis_List_Holder(box, tv));
			}
			Statis_List_Holder viewHolder = (Statis_List_Holder) convertView
					.getTag();
			viewHolder.box.setColor(item.getAdditional_value());

			viewHolder.box.setVisibility(item.getId() < 0 ? View.GONE
					: View.VISIBLE);

			viewHolder.tv.setText(item.getValue());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private class Statis_List_Holder {
		StatusBox box;
		TextView tv;

		public Statis_List_Holder(StatusBox box, TextView tv) {
			this.box = box;
			this.tv = tv;
		}
	}
}
