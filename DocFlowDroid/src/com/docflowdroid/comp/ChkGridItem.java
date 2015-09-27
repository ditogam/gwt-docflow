package com.docflowdroid.comp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.common.shared.ClSelectionItem;
import com.docflowdroid.comp.adapter.SelectArrayAdapter;

public class ChkGridItem extends FormItem {

	private SelectArrayAdapter adapter;
	private ListView grid;
	private String value = null;

	public ChkGridItem(Context context) {
		super(context);
		adapter = new SelectArrayAdapter(context,
				new ArrayList<ClSelectionItem>());
		grid = new ListView(context);
		grid.setAdapter(adapter);
		getLayoutParams().width = 400;
		getLayoutParams().height = 300;
		grid.setLayoutParams(new android.view.ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	public View getComponent() {
		return grid;
	}

	@Override
	public void setValue(final Object value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			setValuePrivate(value);
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValuePrivate(value);
				}
			});
		}
	}

	private void setValuePrivate(Object _value) {
		_value = _value == null ? "" : _value;
		this.value = _value.toString();
		String values[] = value.split(",");
		adapter.clearSelections();
		for (String v : values) {
			try {
				Long l = Long.parseLong(v.trim());
				int pos = adapter.getPosition(l);
				adapter.getItem(pos).setParentId(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void setValueMap(HashMap<Long, String> map) {
		adapter.setValues(map);
		adapter.notifyDataSetChanged();
	}

	@Override
	public String getValue() {
		String ret = "";
		ArrayList<ClSelectionItem> items = adapter.getItems();
		for (ClSelectionItem r : items) {
			Boolean chk = r.getParentId() != 0;
			if (chk != null && chk.booleanValue()) {
				if (!ret.isEmpty())
					ret += ",";
				ret += r.getId();

			}
		}
		return ret;
	}

	@Override
	public String getDisplayValue() {
		String ret = "";
		ArrayList<ClSelectionItem> items = adapter.getItems();
		for (ClSelectionItem r : items) {
			Boolean chk = r.getParentId() != 0;
			if (chk != null && chk.booleanValue()) {
				if (!ret.isEmpty())
					ret += ",";
				ret += r.getValue();

			}
		}
		return ret;
	}

	@Override
	public void requestFocusSelf() {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			grid.requestFocus();
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					requestFocusSelf();
				}
			});
		}
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		// TODO Auto-generated method stub
		return grid.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		grid.setOnFocusChangeListener(newList);

	}
}
