package com.socarmap.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.socarmap.R;
import com.socarmap.proxy.beans.IDValue;

public abstract class ButtonRowArrayAdapter extends ArrayAdapter<IDValue> {
	private LayoutInflater inflater;
	protected ArrayList<IDValue> items;

	public ButtonRowArrayAdapter(Context context, ArrayList<IDValue> items) {
		super(context, R.layout.list__button_row, R.id.rowTextView, items);
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	public abstract String getButtonName();

	public ArrayList<IDValue> getItems() {
		return items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// Planet to display
		final IDValue planet = this.getItem(position);

		// The child views in each row.
		Button btnDelete;
		TextView textView;

		// Create a new row view
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list__button_row, null);
			// Find the child views.
			textView = (TextView) convertView
					.findViewById(R.id.deleterowTextView);
			btnDelete = (Button) convertView.findViewById(R.id.btn_delete_row);
			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new ButtonRowViewHolder(textView, btnDelete));

			btnDelete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					operate(planet, v);
				}
			});

			textView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					operateText(planet, v, position);

				}
			});
		}
		// Reuse existing row view
		else {
			// Because we use a ViewHolder, we avoid having to call
			// findViewById().
			ButtonRowViewHolder viewHolder = (ButtonRowViewHolder) convertView
					.getTag();
			btnDelete = viewHolder.getBtnDelete();
			textView = viewHolder.getTextView();
		}

		// Tag the CheckBox with the Planet it is displaying, so that we can
		// access the planet in onClick() when the CheckBox is toggled.
		btnDelete.setTag(planet);
		btnDelete.setText(getButtonName());
		textView.setText(planet.getValue());
		return convertView;
	}

	public Object onRetainNonConfigurationInstance() {
		return items;
	}

	public abstract void operate(final IDValue planet, View v);

	public abstract void operateText(final IDValue planet, View v,
			final int position);

}
