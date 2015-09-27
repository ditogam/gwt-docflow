package com.docflowdroid.comp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.common.shared.ClSelectionItem;
import com.docflowdroid.R;

public class SelectArrayAdapter extends ArrayAdapter<ClSelectionItem> {
	private ArrayList<ClSelectionItem> items;
	private ISelectButtonExecute executor;
	private String buttonTitle;

	public SelectArrayAdapter(Context context,
			ArrayList<ClSelectionItem> items, ISelectButtonExecute executor) {
		super(context, android.R.layout.simple_list_item_1, items);
		this.items = items;
		this.executor = executor;
		if (executor != null)
			this.buttonTitle = executor.getButtonTitle();
	}

	public SelectArrayAdapter(Context context, ArrayList<ClSelectionItem> items) {
		this(context, items, null);
	}

	public ArrayList<ClSelectionItem> getItems() {
		return items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// Planet to display
		final ClSelectionItem planet = this.getItem(position);

		// The child views in each row.
		CheckBox checkBox;
		TextView textView;
		Button buttonView = null;

		// Create a new row view
		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final View myview = inflater
					.inflate(R.layout.select_row_item, null);
			checkBox = (CheckBox) myview.findViewById(R.id.siCheckBox);
			textView = (TextView) myview.findViewById(R.id.siTextView);
			buttonView = (Button) myview.findViewById(R.id.siButton);
			// myview.setOrientation(LinearLayout.HORIZONTAL);
			convertView = myview;
			// textView = new TextView(getContext());
			// LinearLayout.LayoutParams lastTxtParams = new
			// LinearLayout.LayoutParams(
			// RelativeLayout.LayoutParams.MATCH_PARENT,
			// RelativeLayout.LayoutParams.WRAP_CONTENT);
			// // lastTxtParams.setMargins(10, 0, 0, 0);
			// textView.setLayoutParams(lastTxtParams);
			// // textView.setTextAppearance(getContext(),
			// // android.R.style.TextAppearance_Medium);
			// myview.addView(textView);
			//
			// LinearLayout.LayoutParams lastchbParams = new
			// LinearLayout.LayoutParams(
			// RelativeLayout.LayoutParams.WRAP_CONTENT,
			// RelativeLayout.LayoutParams.WRAP_CONTENT);
			// // lastchbParams.setMargins(10, 0, 6, 0);
			// // lastchbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// checkBox = new CheckBox(getContext());
			// checkBox.setLayoutParams(lastchbParams);
			// myview.addView(checkBox);

			if (executor != null) {
				buttonView.setVisibility(View.VISIBLE);
				buttonView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						executor.executeItem(planet, myview);

					}
				});
			}

			final View mv = convertView;
			// Find the child views.

			// Optimization: Tag the row with it's child views, so we don't
			// have to
			// call findViewById() later when we reuse the row.
			convertView.setTag(new SelectViewHolder(textView, checkBox,
					buttonView));
			// If CheckBox is toggled, update the planet it is tagged with.
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ClSelectionItem planet = getItem(position);
					planet.setParentId(planet.getParentId() == 0 ? 1 : 0);
					SelectViewHolder viewHolder = (SelectViewHolder) mv
							.getTag();
					viewHolder.getCheckBox().setChecked(
							planet.getParentId() != 0);
				}
			});
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					ClSelectionItem planet = (ClSelectionItem) cb.getTag();
					planet.setParentId(cb.isChecked() ? 1 : 0);
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
		checkBox.setChecked(planet.getParentId() != 0);
		textView.setText(planet.getValue());
		if (buttonView != null)
			buttonView.setText(buttonTitle);
		return convertView;
	}

	public Object onRetainNonConfigurationInstance() {
		return items;
	}

	public Integer getPosition(long id) {

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getId() == id)
				return i;
		}
		return null;
	}

	public void clearSelections() {

		for (int i = 0; i < items.size(); i++) {
			items.get(i).setParentId(0);
		}
	}

	public void setValues(HashMap<Long, String> map) {
		items.clear();
		if (map == null)
			return;
		for (Long key : map.keySet()) {
			ClSelectionItem val = new ClSelectionItem();
			val.setId(key);
			val.setValue(map.get(key));
			items.add(val);
		}

	}
}
