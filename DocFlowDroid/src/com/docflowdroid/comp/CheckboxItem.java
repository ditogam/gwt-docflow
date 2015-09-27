package com.docflowdroid.comp;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckboxItem extends FormItem {

	private CheckBox checkBox;

	public CheckboxItem(Context context) {
		super(context);
		checkBox = new CheckBox(context);
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

	private void setValuePrivate(Object value) {
		value = value == null ? "" : value.toString().trim();
		boolean val = false;
		try {
			val = Boolean.valueOf(value.toString());
		} catch (Exception e) {
			val = value.equals("1")
					|| value.toString().toLowerCase().equals("true");
		}
		checkBox.setChecked(val);
	}

	@Override
	public String getValue() {
		return checkBox.isChecked() ? "1" : "0";
	}

	@Override
	public View getComponent() {
		// TODO Auto-generated method stub
		return checkBox;
	}

	@Override
	public void requestFocusSelf() {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			checkBox.requestFocus();
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
		return checkBox.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		checkBox.setOnFocusChangeListener(newList);

	}

	@Override
	public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				listener.onItemSelected(null, checkBox, -1, -1);

			}
		});
	}
}
