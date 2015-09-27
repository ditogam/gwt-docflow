package com.docflowdroid.comp;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TextItem extends FormItem {

	protected EditText text;

	// private

	public TextItem(Context context) {
		super(context);
		getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
		setLayoutParams(getLayoutParams());
		text = new EditText(context);
		text.setEms(10);
		text.setSingleLine();
		ViewGroup.LayoutParams params = text.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
		params.width = 200;
		text.setLayoutParams(params);

	}

	@Override
	public void setValue(final Object value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			text.setText(value == null ? "" : value.toString().trim());
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValue(value);

				}
			});
		}
	}

	@Override
	public String getValue() {
		return text.getText().toString();
	}

	@Override
	public View getComponent() {
		// TODO Auto-generated method stub
		return text;
	}

	@Override
	public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				listener.onItemSelected(null, text, -1, -1);

			}
		});
	}

	@Override
	public void requestFocusSelf() {
		try {
			if (Looper.getMainLooper().equals(Looper.myLooper())) {
				text.requestFocus();
			} else {
				((Activity) this.getContext()).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							requestFocusSelf();
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}

	}

	@Override
	public void setEnabled(final boolean enabled) {
		try {
			if (Looper.getMainLooper().equals(Looper.myLooper())) {
				super.setEnabled(enabled);
				text.setEnabled(enabled);
			} else {
				((Activity) this.getContext()).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							setEnabled(enabled);
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		// TODO Auto-generated method stub
		return text.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		text.setOnFocusChangeListener(newList);

	}

}
