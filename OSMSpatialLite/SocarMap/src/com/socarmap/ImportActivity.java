package com.socarmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jsqlite.Database;
import jsqlite.Stmt;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import com.socarmap.db.DBLoader;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ActivityHelper;
import com.socarmap.helper.AssetHelper;
import com.socarmap.helper.ConnectionHelper;
import com.socarmap.helper.SelectArrayAdapter;
import com.socarmap.proxy.beans.IDValue;
import com.socarmap.proxy.beans.MakeDBProcess;
import com.socarmap.proxy.beans.MakeDBResponce;
import com.socarmap.ui.IDValueAdapter;
import com.socarmap.utils.Utils;

public class ImportActivity extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnClickListener {
	public class DownloadTask extends AsyncTask<Void, Void, Boolean> {
		private Throwable ex;
		private ProgressDialog progressDialog;

		@Override
		protected Boolean doInBackground(Void... params) {
			int MAX_EXCEPTION = 100;
			int exp_count = 0;
			while (true) {
				try {
					resp = ConnectionHelper.getConnection()
							.getMakeDBProcessStatus(process.getSessionID());
					if (resp.isCompleted())
						break;
					try {
						final int operationCompleted = resp
								.getOperationCompleted();
						final String text = process.getOperations()[operationCompleted];
						ImportActivity.this.runOnUiThread(new Runnable() {

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
						Thread.sleep(100);
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
						ConnectionHelper.getLastUrl() + "getdb.jsp?sessionid="
								+ URLEncoder.encode(process.getSessionID()),
						null, getString(R.string.l_download_file),
						resp.getFileSize(), null, progressDialog,
						ImportActivity.this, buffer);
				doProcess(buffer, progressDialog, ImportActivity.this);
			} catch (Exception e) {
				ex = e;
				return false;
			}
			// if (!process.isShouldCopyTiles() && alsoTiles) {
			if (alsoTiles) {
				try {
					File dir = new File(
							ImportActivity.this.getString(R.string.tiles_path));
					File file = new File(dir, subregion_id + ".sqlite");
					FileOutputStream fos = new FileOutputStream(file);
					Utils.downlodStream(ConnectionHelper.settings.getUrl()
							+ "downloadsqlitesubregion.jsp?subregion="
							+ subregion_id, null,
							getString(R.string.l_download_file),
							process.getFilesize(), null, progressDialog,
							ImportActivity.this, fos);
					fos.flush();
					fos.close();
					progressDialog.dismiss();
				} catch (Exception e) {
					ex = e;
					return false;
				}
				return true;
			}
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
				ActivityHelper.showAlert(ImportActivity.this, ex);
			} else {
				String title = "Because difference is big(file_size="
						+ (process.getAproximatesize() / 1024.0 / 1024.0 / 1024.0)
						+ " tile_count=" + process.getCount()
						+ "), you shoud download tileDB seperately(filesize="
						+ (process.getFilesize() / 1024.0 / 1024.0 / 1024.0)
						+ ")!!!";
				ActivityHelper
						.showAlert(
								ImportActivity.this,
								"Import finnished successfully!!!"
										+ ((!process.isShouldCopyTiles() && !alsoTiles) ? title
												: ""));
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ImportActivity.this);
			progressDialog.setMessage(process.getOperations()[0]);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(process.getOperations().length);
			// progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

	}
	public class DownloadTilesTask extends AsyncTask<Void, Void, Boolean> {
		long fileSize;
		long lasupdated;

		private ProgressDialog progressDialog;

		public DownloadTilesTask(long fileSize, long lasupdated) {
			this.fileSize = fileSize;
			this.lasupdated = lasupdated;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				StringWriter fileName = new StringWriter();
				Utils.downlodStream(ConnectionHelper.settings.getUrl()
						+ "updatemapdata.jsp?lasupdated=" + lasupdated, null,
						getString(R.string.l_update_tiles_progress),
						(int) fileSize, null, progressDialog,
						ImportActivity.this, buffer, fileName);
				File dir = new File(getString(R.string.socar_db))
						.getParentFile();
				dir = new File(dir, "tmp");
				dir.delete();
				dir.mkdir();
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd",
							Locale.ENGLISH);
					Date dt = sdf.parse(fileName.toString());
					lasupdated = dt.getTime();
				} catch (Exception e) {

				}

				fileName.append(".sqlite");
				ByteArrayInputStream bis = new ByteArrayInputStream(
						buffer.toByteArray());
				File file = new File(dir, fileName.toString().trim());
				FileOutputStream out = new FileOutputStream(file);
				Utils.streamCopy(bis, out);

				out.flush();
				out.close();
				Utils.copyZXYData(file, ImportActivity.this, progressDialog);
				file.delete();
				dir.delete();
				DBSettingsLoader.getInstance().updateLast(ImportActivity.this,
						lasupdated);
				return true;
			} catch (final Throwable e) {
				ImportActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ActivityHelper.showAlert(ImportActivity.this, e);

					}
				});

			} finally {
				progressDialog.dismiss();
			}
			return false;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ImportActivity.this);
			progressDialog
					.setMessage(getString(R.string.l_update_tiles_progress));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax((int) fileSize);
			// progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

	}
	private Spinner spRegion;
	private Spinner spSubregions;

	private CheckBox cbAllZones;

	private ListView lvZones;

	private DownloadTask mAuthTask = null;

	MakeDBProcess process;

	private int subregion_id;

	private boolean alsoTiles;
	MakeDBResponce resp;

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
		final int maxProcess = 15;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.setMax(maxProcess);

			}
		});

		process = doProcess(act, "Saving zip file", process, progressDialog);
		buffer.flush();
		File file = new File(SDCardRoot, "mydb.zip");
		FileOutputStream fileOutput = new FileOutputStream(file);
		fileOutput.write(buffer.toByteArray());
		fileOutput.flush();
		fileOutput.close();
		buffer.close();
		process = doProcess(act, "Extracting file", process, progressDialog);
		String fs = System.getProperty("file.separator");
		String dbPath = file.getParentFile().getAbsolutePath();
		if (!dbPath.endsWith(fs))
			dbPath += fs;
		dbPath += "socardb";
		Utils.unzip(file, dbPath);
		file.delete();

		String dbFile = dbPath + fs + "mydb.sqlite";
		jsqlite.Database mDatabase = new Database();
		process = doProcess(act, "Opening DB", process, progressDialog);
		mDatabase.open(dbFile, jsqlite.Constants.SQLITE_OPEN_READWRITE);
		process = doProcess(act, "Updating building geometries", process,
				progressDialog);
		executeStetement(
				mDatabase,
				"update buildings set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");

		process = doProcess(act, "Rebuild building geometries", process,
				progressDialog);
		executeStetement(mDatabase,
				"SELECT RebuildGeometryTriggers('buildings', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('buildings', 'the_geom',1)");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('buildings', 'the_geom',0)");

		process = doProcess(act, "Updating roads geometries", process,
				progressDialog);
		executeStetement(
				mDatabase,
				"update roads set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");

		process = doProcess(act, "Rebuild roads geometries", process,
				progressDialog);
		executeStetement(mDatabase,
				"SELECT RebuildGeometryTriggers('roads', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('roads', 'the_geom',1)");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('roads', 'the_geom',0)");

		process = doProcess(act, "Updating district_meters geometries",
				process, progressDialog);

		executeStetement(
				mDatabase,
				"update district_meters set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");

		process = doProcess(act, "Rebuild district_meters geometries", process,
				progressDialog);

		executeStetement(mDatabase,
				"SELECT RebuildGeometryTriggers('district_meters', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('district_meters', 'the_geom',1)");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('district_meters', 'the_geom',0)");

		process = doProcess(act, "Updating Map Info geometries", process,
				progressDialog);

		executeStetement(
				mDatabase,
				"update map_info set the_geom=GeomFromText(the_geom_text,4326), the_geom_text=null where the_geom_text is not null");

		process = doProcess(act, "Rebuild Map Info geometries", process,
				progressDialog);

		executeStetement(mDatabase,
				"SELECT RebuildGeometryTriggers('map_info', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT CreateSpatialIndex('map_info', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('map_info', 'the_geom',1)");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('map_info', 'the_geom',0)");

		process = doProcess(act, "Updating Settlements geometries", process,
				progressDialog);

		executeStetement(
				mDatabase,
				"update settlements set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");

		process = doProcess(act, "Rebuild Settlements geometries", process,
				progressDialog);

		executeStetement(mDatabase,
				"SELECT RebuildGeometryTriggers('settlements', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT CreateSpatialIndex('settlements', 'the_geom')");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('settlements', 'the_geom',1)");
		executeStetement(mDatabase,
				"SELECT RecoverSpatialIndex('settlements', 'the_geom',0)");

		process = doProcess(act, "Vacuuming", process, progressDialog);
		executeStetement(mDatabase, "vacuum");

		mDatabase.close();
		MainActivity.instance.mapView.clearMap();
		MainActivity.instance.mapView.redrawMap(true);
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

	@Override
	public void onClick(View v) {
		try {

			IDValue sr = (IDValue) spSubregions.getSelectedItem();
			if (sr == null) {
				ActivityHelper.showAlert(this, "Please select subregion!!!");
				return;
			}
			String zones = null;
			if (!cbAllZones.isChecked()) {
				ArrayList<IDValue> values = ((SelectArrayAdapter) lvZones
						.getAdapter()).getItems();
				zones = "";
				for (IDValue idValue : values) {
					if (idValue.isSelected()) {
						if (!zones.isEmpty())
							zones += ",";
						zones += idValue.getId();
					}

				}
				if (zones.isEmpty())
					zones = null;
			}
			subregion_id = (int) sr.getId();
			alsoTiles = false;
			process = ConnectionHelper.getConnection().createDBMakingProcess(
					subregion_id, zones,
					ConnectionHelper.settings.getLastdowloaded());

			mAuthTask = new DownloadTask();
			mAuthTask.execute((Void) null);
			// doDownload(sr, zones);
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Button b = (Button) findViewById(R.id.b_download);
		spRegion = (Spinner) findViewById(R.id.sp_region);
		spSubregions = (Spinner) findViewById(R.id.sp_subregion);
		cbAllZones = (CheckBox) findViewById(R.id.ckb_all_zones);
		lvZones = (ListView) findViewById(R.id.lv_zones);
		setupSpinnerFromMap(spRegion, DBLoader.getInstance().regions);
		setupSpinnerFromMap(spSubregions, null);
		setupListViewFromMap(lvZones, null);
		b.setOnClickListener(this);
		Button bupdate = (Button) findViewById(R.id.b_updatetiles);
		bupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateTiles();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_import, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		IDValue g = (IDValue) parent.getItemAtPosition(pos);
		int vId = parent.getId();
		if (vId == R.id.sp_region) {
			setupSpinnerFromMap(spSubregions,
					DBLoader.getInstance().subregions.get(g.getId()));

			setupListViewFromMap(lvZones, null);
			spSubregions.setSelection(Adapter.NO_SELECTION);

		}
		if (vId == R.id.sp_subregion) {
			setupListViewFromMap(lvZones,
					DBLoader.getInstance().zones.get(g.getId()));

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void setupListViewFromMap(ListView listView,
			HashMap<Long, String> map) {
		ArrayList<IDValue> values = new ArrayList<IDValue>();
		if (map != null)
			for (Long key : map.keySet()) {
				values.add(new IDValue(key, map.get(key)));
			}
		final SelectArrayAdapter listAdapter = new SelectArrayAdapter(this,
				values);
		lvZones.setAdapter(listAdapter);
	}

	private void setupSpinnerFromMap(Spinner spinner, HashMap<Long, String> map) {
		IDValueAdapter adapter = new IDValueAdapter(map,
				getApplicationContext());
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	protected void updateTiles() {
		String dbPath = getString(R.string.socar_db);
		jsqlite.Database mDatabase = new Database();

		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			AssetHelper.getFileData(this, "tmp_sql.sql", bos);
			String sql = new String(bos.toByteArray());

			mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);

			executeStetement(
					mDatabase,
					"create table tiles_g as SELECT *,MbrMinX(Geometry) x1, MbrMinY(Geometry)y1,MbrMaxX(Geometry) x2, MbrMaxY(Geometry) y2  FROM v_tiles");
			executeStetement(mDatabase, sql);
			executeStetement(mDatabase, "vacuum");
		} catch (Throwable e) {
			ActivityHelper.showAlert(this, e);
		} finally {
			try {
				mDatabase.close();
			} catch (Exception e) {

			}
		}
	}

}
