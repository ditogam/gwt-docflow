package com.socarmap.helper;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.socarmap.R;

public class BarcodeLauncher extends Activity {

	private static final int BCL_RESULT_REQUEST_CODE = 10008;

	private static void scan(Activity act) throws Throwable {
		Intent intent = new Intent("com.google.saxing.client.android.SCAN");
		intent.setPackage("com.google.saxing.client.android");
		act.startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
	}

	public static void scanBarCode(Activity act) {
		try {
			scan(act);
		} catch (Throwable e) {
			Intent intent = new Intent(act, BarcodeLauncher.class);
			act.startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BCL_RESULT_REQUEST_CODE) {
			try {
				scan(this);
			} catch (Throwable e) {
				ActivityHelper.showAlert(this,
						"Unable to launch barcodereader!!!");
			}
		} else
			ActivityHelper.showAlert(this, "Barcodereader not installed!!!");
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = new File(getString(R.string.barcode_apk));
			AssetHelper.CopyAsset(this.getBaseContext(), file.getParentFile(),
					file.getName());

			intent.setDataAndType(
					Uri.parse("file://" + file.getAbsolutePath()),
					"application/vnd.android.package-archive");
			startActivityForResult(intent, BCL_RESULT_REQUEST_CODE);
		} catch (Exception e) {
			Intent intent = new Intent();
			if (getParent() == null) {
				setResult(Activity.RESULT_CANCELED, intent);
			} else {
				getParent().setResult(Activity.RESULT_CANCELED, intent);
			}

			finish();
		}

	}

}
