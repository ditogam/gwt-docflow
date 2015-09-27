package com.socarmap.helper;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.socarmap.proxy.beans.IDValue;

public class IDValueAdapter extends BaseAdapter implements SpinnerAdapter {

	ArrayList<IDValue> values = new ArrayList<IDValue>();
	private Context appContext;

	public IDValueAdapter(TreeMap<Long, String> map, Context appContext) {
		this.appContext = appContext;
		setValues(map);
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		if (position + 1 > values.size())
			return null;
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		Object val = getItem(position);
		if (val == null)
			return 0;
		return ((IDValue) val).getId();
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

	public void setValues(TreeMap<Long, String> map) {
		values.clear();
		if (map == null)
			return;
		for (Long key : map.keySet()) {
			values.add(new IDValue(key, map.get(key)));
		}

	}

}
