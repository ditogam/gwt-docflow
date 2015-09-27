package com.docflowdroid.common.process;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.docflowdroid.DocFlowCommon;

public class ProcessExecutor extends DocFlowCommon{

	public static void execute(IProcess process, Activity activity) {
		new Process(process, activity).execute((Void) null);
	}

	private static class Process extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog progressDialog;
		private IProcess process;
		private Activity activity;

		public Process(IProcess process, Activity activity) {
			this.activity = activity;
			this.process = process;
			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(activity
					.getText(login_progress_signing_in));
			// progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			activity.runOnUiThread(new Runnable() {

				public void run() {
					progressDialog.show();
				}
			});

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				process.execute();
				return true;
			} catch (final Exception e) {
				activity.runOnUiThread(new Runnable() {

					public void run() {
						com.docflowdroid.ActivityHelper.showAlert(activity, e);

					}
				});
			} finally {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
					}
				});
			}
			return false;
		}

	}
}
