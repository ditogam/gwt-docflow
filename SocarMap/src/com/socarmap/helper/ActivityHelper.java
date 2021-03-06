package com.socarmap.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.socarmap.R;
import com.socarmap.proxy.beans.SocarException;

public class ActivityHelper {
	private static final String TAG = "ActivityHelper";

	static public void askAlert(Context ctx, final String message,
			String title, DialogInterface.OnClickListener ok) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton("YES", ok);

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	static public String getDataBase(Context ctx, String filename)
			throws FileNotFoundException {
		File db = null;

		// Check application storage first
		db = new File(getPath(ctx, false), filename);
		Log.d(TAG, "Checking: " + db.toString());
		if (db.exists()) {
			return db.toString();
		}

		// Check external storage second
		db = new File(getPath(ctx, true), filename);
		Log.d(TAG, "Checking: " + db.toString());
		if (db.exists()) {
			return db.toString();
		}

		// Database not found
		throw new FileNotFoundException(
				ctx.getString(R.string.error_locate_failed));
	}

	static public File getPath(Context ctx, boolean externalStorage) {
		if (externalStorage) {
			return ctx.getExternalFilesDir(null);
		} else {
			return ctx.getFilesDir();
		}
	}

	private static void setText(View convertView, int id, String text) {
		TextView textView = (TextView) convertView.findViewById(id);
		if (textView != null)
			textView.setText(text);
	}

	static public void showAlert(Context ctx, final String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle("Application Error");
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
		alertDialog.show();
	}

	static public void showAlert(Context ctx, final Throwable e) {

		String message = e instanceof SocarException ? ((SocarException) e)
				.getMyMessage() : e.getLocalizedMessage();
		String det = "";
		if (e instanceof SocarException)
			det = ((SocarException) e).getDetailed();
		else {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			det = sw.toString();
		}
		LayoutInflater inflater = LayoutInflater.from(ctx);
		final View convertView = inflater.inflate(R.layout.error_detail, null);
		setText(convertView, R.id.errMessage, message);
		setText(convertView, R.id.errDetail, det);
		((ToggleButton) convertView.findViewById(R.id.tbDetail))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						convertView.findViewById(R.id.errDetail).setVisibility(
								isChecked ? View.VISIBLE : View.GONE);

					}
				});
		AlertDialog.Builder screenDialog = new AlertDialog.Builder(ctx);
		screenDialog.setTitle(ctx.getString(R.string.error));
		screenDialog.setView(convertView);

		screenDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		screenDialog.show();

	}
}
