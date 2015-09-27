package com.socarmap.helper;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.socarmap.R;
import com.socarmap.proxy.beans.IDValue;

@SuppressLint("ViewConstructor")
public class DeleteRowArrayAdapter extends ButtonRowArrayAdapter {

	private String buttonname;

	public DeleteRowArrayAdapter(Context context, ArrayList<IDValue> items) {
		super(context, items);
		buttonname = getContext().getString(R.string.delete);
	}

	@Override
	public String getButtonName() {

		return buttonname;
	}

	@Override
	public void operate(final IDValue planet, View v) {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					items.remove(planet);
					notifyDataSetChanged();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setMessage(
				v.getContext().getString(R.string.do_you_want_to_delete))
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
		return;

	}

	@Override
	public void operateText(IDValue planet, View v, int position) {

	}

}
