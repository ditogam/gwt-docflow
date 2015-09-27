package com.socarmap.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.socarmap.R;
import com.socarmap.proxy.beans.IDValue;

public class SelectArrayAdapter extends ArrayAdapter<IDValue> {
	private LayoutInflater inflater;
	private ArrayList<IDValue> items;

	public SelectArrayAdapter(Context context, ArrayList<IDValue> items) {
		super(context, R.layout.list_row_checked, R.id.rowTextView, items);
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	public ArrayList<IDValue> getItems() {
		return items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Planet to display
		IDValue planet = this.getItem(position);

		// The child views in each row.
		CheckBox checkBox;
		TextView textView;

		// Create a new row view
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row_checked, null);
			final View mv = convertView;
			// Find the child views.
			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			checkBox = (CheckBox) convertView.findViewById(R.id.cbx_selected);
			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new SelectViewHolder(textView, checkBox));
			// If CheckBox is toggled, update the planet it is tagged with.
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					IDValue planet = getItem(position);
					planet.toggleChecked();
					SelectViewHolder viewHolder = (SelectViewHolder) mv
							.getTag();
					viewHolder.getCheckBox().setChecked(planet.isSelected());
				}
			});
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					IDValue planet = (IDValue) cb.getTag();
					planet.setSelected(cb.isChecked());
				}
			});
		}
		// Reuse existing row view
		else {
			// Because we use a ViewHolder, we avoid having to call
			// findViewById().
			SelectViewHolder viewHolder = (SelectViewHolder) convertView
					.getTag();
			checkBox = viewHolder.getCheckBox();
			textView = viewHolder.getTextView();
		}

		// Tag the CheckBox with the Planet it is displaying, so that we can
		// access the planet in onClick() when the CheckBox is toggled.
		checkBox.setTag(planet);
		// Display planet data
		checkBox.setChecked(planet.isSelected());
		textView.setText(planet.getValue());
		return convertView;
	}

	public Object onRetainNonConfigurationInstance() {
		return items;
	}
}
