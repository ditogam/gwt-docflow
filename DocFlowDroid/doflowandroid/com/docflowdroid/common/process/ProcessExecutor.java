package com.docflowdroid.common.process;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.docflowdroid.DocFlowCommon;

public class ProcessExecutor extends DocFlowCommon {

	public static void execute(IProcess process, Activity activity) {
		try {
			new Process(process, activity).execute((Void) null);
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	private static class Process extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog progressDialog;
		private IProcess process;
		private Activity activity;

		public Process(IProcess process, Activity activity) {
			try {
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
			} catch (Throwable e) {
				// TODO: handle exception
			}

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				process.execute();
				return true;
			} catch (final Throwable e) {
				try {
					activity.runOnUiThread(new Runnable() {

						public void run() {
							try {
								com.docflowdroid.ActivityHelper.showAlert(
										activity, e);
							} catch (Throwable e2) {
								// TODO: handle exception
							}

						}
					});
				} catch (Throwable e2) {
					// TODO: handle exception
				}
			} finally {
				try {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							try {
								progressDialog.dismiss();
							} catch (Throwable e2) {
								// TODO: handle exception
							}
						}
					});

				} catch (Throwable e2) {
					// TODO: handle exception
				}
			}
			return false;
		}

	}
}
