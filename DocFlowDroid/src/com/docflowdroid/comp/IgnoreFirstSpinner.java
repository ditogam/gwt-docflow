package com.docflowdroid.comp;

import android.content.Context;
import android.widget.Spinner;

public class IgnoreFirstSpinner extends Spinner {
	private OnItemSelectedListener listener;
	private boolean firstAttempt = true;

	public IgnoreFirstSpinner(Context context) {
		super(context);
	}

	@Override
	public void setSelection(int position) {
		super.setSelection(position);
		if (firstAttempt && listener != null) {
			super.setOnItemSelectedListener(listener);
			firstAttempt = false;
		}
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		this.listener = listener;
	}

}
