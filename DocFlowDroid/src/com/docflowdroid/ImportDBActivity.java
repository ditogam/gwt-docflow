package com.docflowdroid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.ArrayList;

import jsqlite.Database;
import jsqlite.Stmt;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.docflow.shared.DBData;
import com.docflow.shared.DbExpoResult;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;
import com.docflowdroid.helper.DrowableDownloader;
import com.docflowdroid.helper.Utils;

public class ImportDBActivity extends Activity {
	private DbExpoResult resp;
	private ArrayList<DBData> dbDatas;

	private String session_id;
	private int operationCompleted = 0;

	private String getCurrentTableCaption(String tblName) {
		for (DBData tbl : dbDatas) {
			if (tbl.getTbl_name().equals(tblName))
				return tbl.getTbl_caption();
		}
		return "Unknown";
	}

	private void getCurrentTableIndex(String tblName) {
		operationCompleted = 0;
		for (DBData tbl : dbDatas) {
			operationCompleted++;
			if (tbl.getTbl_name().equals(tblName))
				return;
		}
		operationCompleted = 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_db);

		((Button) findViewById(R.id.btn_import))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						beginDownload();

					}
				});

	}

	protected void beginDownload() {
		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				try {

					DbExpoResult myresp = DocFlow.docFlowService
							.createExportSession(24);
					if (myresp.getException() != null)
						throw myresp.getException();
					resp = myresp;
					session_id = resp.getSession_id();
					dbDatas = resp.getDbDatas();
					resp.setTableName(dbDatas.get(0).getTbl_name());
					ImportDBActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							operationCompleted = 0;
							DownloadTask mDownloadTask = new DownloadTask();
							mDownloadTask.execute((Void) null);
						}
					});

				} catch (final Throwable e) {
					ImportDBActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ActivityHelper.showAlert(ImportDBActivity.this, e);
						}
					});
				}

			}
		}, this);

	}

	public class DownloadTask extends AsyncTask<Void, Void, Boolean> {
		private Throwable ex;
		private ProgressDialog progressDialog;

		@Override
		protected Boolean doInBackground(Void... params) {
			int MAX_EXCEPTION = 100;
			int exp_count = 0;
			while (true) {
				try {
					DbExpoResult myresp = DocFlow.docFlowService
							.getExportStatus(session_id);
					resp = myresp;

					if (resp.getException() != null) {
						exp_count = MAX_EXCEPTION + 100;
						throw resp.getException();
					}
					if (resp.getDone() != null && resp.getDone().booleanValue()) {
						System.out.println("");
						break;
					}
					try {

						final String text = getCurrentTableCaption(resp
								.getTableName());
						getCurrentTableIndex(resp.getTableName());
						ImportDBActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progressDialog.setMessage(text);
								progressDialog.setProgress(operationCompleted);

							}
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (Exception ignored) {
						ignored.printStackTrace();
					}
				} catch (Throwable e) {
					exp_count++;
					if (exp_count > MAX_EXCEPTION) {
						ex = e;
						return false;
					}
				}

			}
			try {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				Utils.downlodStream(
						DrowableDownloader.getMainURL()
								+ "exportdb.jsp?sessionid="
								+ URLEncoder.encode(session_id, "UTF8"), null,
						getString(R.string.l_download_file),
						resp.getFileSize(), null, progressDialog,
						ImportDBActivity.this, buffer);
				doProcess(buffer, progressDialog, ImportDBActivity.this);
			} catch (Throwable e) {
				ex = e;
				return false;
			}
			// if (!process.isShouldCopyTiles() && alsoTiles) {

			return true;

		}

		@Override
		protected void onCancelled() {
			progressDialog.dismiss();
		}

		@Override
		protected void onPostExecute(Boolean success) {
			progressDialog.dismiss();
			success = success == null ? false : success;
			if (!success && ex != null) {
				progressDialog.dismiss();
				ActivityHelper.showAlert(ImportDBActivity.this, ex);
			} else {

				ActivityHelper.showAlert(ImportDBActivity.this,
						"Import finnished successfully!!!");
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ImportDBActivity.this);
			progressDialog.setMessage(dbDatas.get(0).getTbl_caption());
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(dbDatas.size());
			// progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

	}

	private void setMaxProcess(final ProgressDialog progressDialog,
			Activity act, final int maxProcess) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.setMax(maxProcess);

			}
		});
	}

	private int doProcess(Activity act, final String text, final int process,
			final ProgressDialog progressDialog) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				progressDialog.setMessage(text);
				progressDialog.setProgress(process);

			}
		});
		return process + 1;
	}

	private void doProcess(ByteArrayOutputStream buffer,
			final ProgressDialog progressDialog, Activity act) throws Exception {
		File SDCardRoot = Environment.getExternalStorageDirectory();
		// create a new file, specifying the path, and the filename
		// which we want to save the file as.
		int process = 0;

		int maxProcess = 7;
		setMaxProcess(progressDialog, act, maxProcess);

		process = doProcess(act, "Saving zip file", process, progressDialog);
		buffer.flush();

		SDCardRoot = new File(SDCardRoot, "extracted");
		SDCardRoot.delete();
		SDCardRoot.mkdirs();
		File file = new File(SDCardRoot, "mydb.zip");
		FileOutputStream fileOutput = new FileOutputStream(file);
		fileOutput.write(buffer.toByteArray());
		fileOutput.flush();
		fileOutput.close();
		buffer.close();
		File dir = SDCardRoot;
		process = doProcess(act, "Extracting file", process, progressDialog);
		Utils.unzip(file, dir.getAbsolutePath());
		// file.delete();
		file = new File(SDCardRoot, "exported.sqlite");

		dir = new File(getString(R.string.docflow_db_path));
		dir.mkdirs();
		File newFile = new File(dir, file.getName());
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(newFile);
		Utils.copyLarge(fis, fos);
		fis.close();
		file.delete();
		fos.flush();
		fos.close();
		String fileName = newFile.getAbsolutePath();
		Database mDatabase = new Database();

		process = doProcess(act, "Opening DB", process, progressDialog);
		mDatabase.open(fileName, jsqlite.Constants.SQLITE_OPEN_READWRITE);

		// process = doProcess(act, "Creating tables", process, progressDialog);
		// for (DBData td : dbDatas) {
		//
		// executeStetement(mDatabase, "create table tmp_" + td.getTbl_name()
		// + " (" + td.getField_names() + ")");
		// executeStetement(
		// mDatabase,
		// "create table " + td.getTbl_name() + " ("
		// + td.getField_names() + ",PRIMARY KEY ( "
		// + td.getPrimary_keys() + " ))");
		//
		// }

		// for (DBData td : dbDatas) {
		// process = doProcess(act, "Inserting " + td.getTbl_caption(),
		// process, progressDialog);
		// File df = new File(SDCardRoot, td.getTbl_name());
		// if (!df.exists())
		// continue;
		// FileReader fis_r = new FileReader(df);
		// insertToSpatLite(mDatabase, td.getTbl_name(), fis_r);
		// fis.close();
		// df.delete();
		//
		// executeStetement(mDatabase, "insert into " + td.getTbl_name()
		// + " select * from  tmp_" + td.getTbl_name());
		// executeStetement(mDatabase, "drop table tmp_" + td.getTbl_name());
		//
		// }
		File df = new File(SDCardRoot, "update.sql");
		FileReader fis_r = new FileReader(df);
		String[] runafter = insertToSpatLite(fis_r);
		fis.close();

		df.delete();
		process = doProcess(act, "Creating other operations ", process,
				progressDialog);
		for (String stmt : runafter) {
			if (stmt == null || stmt.trim().isEmpty())
				continue;
			executeStetement(mDatabase, stmt);
		}

		process = doProcess(act, "Vacuuming", process, progressDialog);
		executeStetement(mDatabase, "vacuum");
		mDatabase.close();

	}

	private String[] insertToSpatLite(Reader r) throws Exception {
		BufferedReader br = new BufferedReader(r);
		String line;

		String res = "";
		while ((line = br.readLine()) != null) {
			res += line + "\n";
		}
		res = res.trim();
		return res.split(";");
	}

	private void executeStetement(jsqlite.Database mDatabase, String stetement)
			throws jsqlite.Exception {
		Stmt stmt = null;
		stmt = mDatabase.prepare(stetement);
		if (stmt.step()) {
			// System.out.println("");
		}
		stmt.close();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_import_db,
					container, false);
			return rootView;
		}
	}

}
