package com.socarmap.helper;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socarmap.R;
import com.socarmap.db.DBLoader;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.Meter;

@SuppressLint("ViewConstructor")
public class MeterBRowArrayAdapter extends ButtonRowArrayAdapter {
	private String buttonname;
	private int user_id;

	public MeterBRowArrayAdapter(Context context, ArrayList<IDValue> items,
			int user_id) {
		super(context, items);
		buttonname = getContext().getString(R.string.change);
		this.user_id = user_id;
	}

	@Override
	public String getButtonName() {

		return buttonname;
	}

	@Override
	public void operate(final IDValue planet, final View v) {
		Meter meter = null;

		try {
			meter = DBLoader.getInstance().getMeter(planet.getId());
			if (meter == null)
				throw new Exception("Meter is null!!!");

		} catch (Throwable e) {
			ActivityHelper.showAlert(getContext(), e);
			return;
		}
		final EditText etNewValue = new EditText(v.getContext());
		etNewValue.setText(meter.getLast_value() + "");
		etNewValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		final Meter fmeter = meter;
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					setMeternewValue(planet, v, etNewValue, fmeter);
					break;
				}
			}
		};

		LinearLayout valueLayout = new LinearLayout(v.getContext());
		valueLayout.setOrientation(LinearLayout.VERTICAL);
		TextView tvValue = new TextView(v.getContext());
		tvValue.setText(v.getContext().getString(R.string.new_value));
		valueLayout.addView(tvValue);
		valueLayout.addView(etNewValue);
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setView(valueLayout)
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
		return;

	}

	@Override
	public void operateText(IDValue planet, View v, int position) {

	}

	private void setMeternewValue(final IDValue planet, View v,
			EditText etNewValue, Meter fmeter) {
		double newValue = 0;
		try {
			newValue = Double.parseDouble(etNewValue.getText().toString()
					.trim());
			if (newValue < 0.001)
				throw new Exception("<0.001!!!");
			fmeter = DBLoader.getInstance().saveMeter(
					(long) fmeter.getMeterid(), newValue,
					(long) fmeter.getCusid(), user_id);
			planet.setValue(fmeter.toString());
			notifyDataSetChanged();
		} catch (Exception e) {
			ActivityHelper.showAlert(getContext(), e);
			etNewValue.setError(e.getMessage());
			etNewValue.requestFocus();
			return;
		}

	}

}
