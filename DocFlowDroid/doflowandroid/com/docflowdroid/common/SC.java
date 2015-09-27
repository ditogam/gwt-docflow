package com.docflowdroid.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SC {

	public static void say(Context cnt, String title, String text,
			final BooleanCallback booleanCallback) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(cnt);

			builder.setTitle(title);
			builder.setMessage(text);

			builder.setNegativeButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (booleanCallback != null)
								booleanCallback.execute(true);
							dialog.dismiss();
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
		} catch (Throwable e) {
		}
	}

	public static void say(Context cnt, String text,
			BooleanCallback booleanCallback) {
		say(cnt, null, text, booleanCallback);
	}

	public static void say(Context cnt, String text) {
		say(cnt, null, text, null);
	}
}
