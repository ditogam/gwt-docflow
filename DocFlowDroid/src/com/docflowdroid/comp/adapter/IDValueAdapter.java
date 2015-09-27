package com.docflowdroid.comp.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.common.FieldDefinition;
import com.docflowdroid.R;

public class IDValueAdapter extends ArrayAdapter<ClSelectionItem> implements
		SpinnerAdapter {

	ArrayList<ClSelectionItem> values = new ArrayList<ClSelectionItem>();
	private Context appContext;
	private FieldDefinition field;

	public IDValueAdapter(ArrayList<ClSelectionItem> values, Context appContext) {
		super(appContext, R.layout.list_item, R.id.cl_select_item);
		this.values = values;
		sortValues();
		this.appContext = appContext;
	}

	public IDValueAdapter(HashMap<Long, String> map, Context appContext) {
		super(appContext, R.layout.list_item, R.id.cl_select_item);
		this.appContext = appContext;
		setValues(map);
	}

	private void sortValues() {
		if (values == null)
			return;
		Collections.sort(values, new Comparator<ClSelectionItem>() {
			@Override
			public int compare(ClSelectionItem val1, ClSelectionItem val2) {
				String v1 = val1.toString();
				v1 = v1 == null ? "" : v1;
				String v2 = val2.toString();
				v2 = v2 == null ? "" : v2;
				return v1.compareTo(v2);
			}
		});
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public ClSelectionItem getItem(int position) {
		if (position + 1 > values.size())
			return null;
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		Object val = getItem(position);
		if (val == null)
			return 0;
		return ((ClSelectionItem) val).getId();
	}

	public Integer getPosition(long id) {

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).getId() == id)
				return i;
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView v = new TextView(appContext);
		v.setTextColor(Color.BLACK);
		String text = "hhhh" + position;
		Object val = getItem(position);
		if (val != null)
			text = val.toString();
		v.setText(text);
		return v;
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	public void setValues(HashMap<Long, String> map) {
		values.clear();
		if (map == null)
			return;
		for (Long key : map.keySet()) {
			ClSelectionItem val = new ClSelectionItem();
			val.setId(key);
			val.setValue(map.get(key));
			values.add(val);
		}
		sortValues();
	}

	public FieldDefinition getField() {
		return field;
	}

	public void setField(FieldDefinition field) {
		this.field = field;
	}

}
