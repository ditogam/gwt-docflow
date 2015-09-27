package com.socarmap.ui.process;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.socarmap.R;
import com.socarmap.helper.ActivityHelper;

public class ProcessExecutor {

	private static class Process extends AsyncTask<Void, Void, Throwable> {
		private ProgressDialog progressDialog;
		private IProcess process;
		private Activity aActivity;

		public Process(IProcess process, Activity activity) {
			this.aActivity = activity;
			this.process = process;

		}

		@Override
		protected Throwable doInBackground(Void... params) {

			try {
				process.execute();
				return null;
			} catch (Throwable e) {
				return e;
			} finally {
			}

		}

		@Override
		protected void onPostExecute(final Throwable result) {

			super.onPostExecute(result);
			aActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					progressDialog.dismiss();
				}
			});
			if (result != null)
				aActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ActivityHelper.showAlert(aActivity, result);

					}
				});

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(aActivity);
			progressDialog.setMessage(aActivity.getText(R.string.loading));
			progressDialog.setCancelable(false);
			aActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.show();
				}
			});
		}

	}

	public static void execute(IProcess process, Activity activity) {
		new Process(process, activity).execute((Void) null);
	}
}
