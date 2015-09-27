package com.docflowdroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jsqlite.Database;
import jsqlite.Stmt;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.database.MapDatabases;
import org.oscim.database.MapOptions;
import org.oscim.overlay.OverlayItem;
import org.oscim.view.MapView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.docflow.shared.map.MakeDBProcess;
import com.docflow.shared.map.MakeDBResponce;
import com.docflowdroid.comp.MapButton;
import com.docflowdroid.comp.adapter.MarkersOverlay;
import com.docflowdroid.helper.DrowableDownloader;
import com.docflowdroid.helper.Utils;
import com.docflowdroid.map.TableDefination;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

public class MapActivity extends org.oscim.view.MapActivity {
	private MapView mapView;
	MakeDBResponce resp;
	public static Drawable drawable;
	public static MapButton mapButton;
	public static final String MAP_COORDS = "MAP_COORDS";
	public static final String MAP_SUBREGION_ID = "MAP_SUBREGION_ID";

	public class DownloadTask extends AsyncTask<Void, Void, Boolean> {
		private Throwable ex;
		private ProgressDialog progressDialog;

		@Override
		protected Boolean doInBackground(Void... params) {
			int MAX_EXCEPTION = 100;
			int exp_count = 0;
			while (true) {
				try {
					resp = DocFlow.docFlowService
							.getMakeDBProcessStatus(process.getSessionID());
					if (resp.isCompleted())
						break;
					try {
						final int operationCompleted = resp
								.getOperationCompleted();
						final String text = process.getOperations()[operationCompleted];
						MapActivity.this.runOnUiThread(new Runnable() {

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
						DrowableDownloader.getMainURL()
								+ "getdb.jsp?sessionid="
								+ URLEncoder.encode(process.getSessionID(),
										"UTF8"), null,
						getString(R.string.l_download_file),
						resp.getFileSize(), null, progressDialog,
						MapActivity.this, buffer);
				doProcess(buffer, progressDialog, MapActivity.this);
			} catch (Exception e) {
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
				ActivityHelper.showAlert(MapActivity.this, ex);
			} else {

				ActivityHelper.showAlert(MapActivity.this,
						"Import finnished successfully!!!");
			}

		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MapActivity.this);
			progressDialog.setMessage(process.getOperations()[0]);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(process.getOperations().length);
			// progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

	}

	public double[] MetersToLatLon(double mx, double my) {
		double originShift = 2 * Math.PI * 6378137 / 2.0;
		double lon = (mx / originShift) * 180.0;
		double lat = (my / originShift) * 180.0;

		lat = 180
				/ Math.PI
				* (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
		return new double[] { lat, lon };
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		DocFlow.setActivityLandscape(this);

		checkDB();
	}

	private String dbPath = "";
	private Date lastUpdate = null;
	private MakeDBProcess process;
	private DownloadTask mDownloadTask = null;
	private HashMap<String, Integer> updates;
	private int subregion_id;

	private void checkDB() {
		Bundle bundle = this.getIntent().getExtras();
		if (!bundle.containsKey(MAP_SUBREGION_ID)) {
			finish();
			return;
		}
		try {
			subregion_id = bundle.getInt(MAP_SUBREGION_ID);
			dbPath = getString(R.string.docflow_db_path);
			File dir = new File(dbPath);
			if (!dir.exists())
				dir.mkdirs();
			File _dbPath = new File(dir, subregion_id + ".sqlite");
			dbPath = _dbPath.getAbsolutePath();
			if (_dbPath.exists()) {
				jsqlite.Database mDatabase = new jsqlite.Database();
				mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READONLY);

				jsqlite.Stmt st = mDatabase
						.prepare("select last_updated from mapinfo where id="
								+ subregion_id);
				if (!st.step())
					throw new Exception("Cannot find row");

				long time = st.column_long(0);
				lastUpdate = new Date(time);
				mDatabase.close();
			}

			if (lastUpdate != null) {
				updates = DocFlow.docFlowService.checkForUpdates(subregion_id,
						lastUpdate);
				if (updates == null || updates.isEmpty()) {
					createMap();
					return;
				}
			}
			process = DocFlow.docFlowService.createDBMakingProcess(
					subregion_id, lastUpdate);
			mDownloadTask = new DownloadTask();
			mDownloadTask.execute((Void) null);
		} catch (Throwable e) {
			finish();
			return;
		}

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

	private void executeStetement(jsqlite.Database mDatabase, String stetement)
			throws jsqlite.Exception {
		Stmt stmt = null;
		stmt = mDatabase.prepare(stetement);
		if (stmt.step()) {
			// System.out.println("");
		}
		stmt.close();
	}

	private void doProcess(ByteArrayOutputStream buffer,
			final ProgressDialog progressDialog, Activity act) throws Exception {
		File SDCardRoot = Environment.getExternalStorageDirectory();
		// create a new file, specifying the path, and the filename
		// which we want to save the file as.
		int process = 0;

		TableDefination[] definitions = new TableDefination[] {
				new TableDefination("mapinfo", "id", new String[] {
						"last_updated", "geom_text", "lcentroid_text",
						"distr_geo", "id", "region_id"

				}, new String[][] { { "geom_text", "geom" } })

				,

				new TableDefination("buildings", "buid", new String[] {
						"geom_text", "senobis_no", "lcentroid_text", "buid",
						"has_customers" }, new String[][] {
						{ "geom_text", "geom" },
						{ "lcentroid_text", "lcentroid" } })

				,

				new TableDefination("roads", "ruid", new String[] {
						"geom_text", "rname", "ruid" }, new String[][] { {
						"geom_text", "geom" } })

				,

				new TableDefination("settlements", "id", new String[] {
						"geom_text", "id" }, new String[][] { { "geom_text",
						"geom" } })

				,

				new TableDefination("district_meters", "cusid", new String[] {
						"geom_text", "cusid" }, new String[][] { { "geom_text",
						"geom" } })

		};

		int maxProcess = 4 + (lastUpdate == null ? 1 : 3) * definitions.length;
		setMaxProcess(progressDialog, act, maxProcess);

		process = doProcess(act, "Saving zip file", process, progressDialog);
		buffer.flush();
		File file = new File(SDCardRoot, "mydb.zip");
		FileOutputStream fileOutput = new FileOutputStream(file);
		fileOutput.write(buffer.toByteArray());
		fileOutput.flush();
		fileOutput.close();
		buffer.close();
		File dir = new File(getString(R.string.docflow_db_path));
		process = doProcess(act, "Extracting file", process, progressDialog);
		Utils.unzip(file, dir.getAbsolutePath());
		file.delete();
		file = new File(dir, "mydb.sqlite");
		String fileName = file.getAbsolutePath();
		if (lastUpdate == null) {
			file.renameTo(new File(dbPath));
		}

		Database mDatabase = new Database();

		process = doProcess(act, "Opening DB", process, progressDialog);
		mDatabase.open(dbPath, jsqlite.Constants.SQLITE_OPEN_READWRITE);
		String alise = "TEST";

		if (lastUpdate != null) {
			executeStetement(mDatabase, "ATTACH DATABASE '" + fileName
					+ "' as '" + alise + "'");
			for (TableDefination td : definitions) {
				if (updates == null
						|| (updates.containsKey(td.getTableName()) && updates
								.get(td.getTableName()).intValue() > 0)) {
					String sql = td.createDeleteStatement(alise);
					try {
						doProcess(
								act,
								"Deleteing old values from "
										+ td.getTableName(), process,
								progressDialog);
						executeStetement(mDatabase, sql);

						doProcess(act,
								"Insert new values from " + td.getTableName(),
								process, progressDialog);
						sql = td.createInsertStatement(alise);
						executeStetement(mDatabase, sql);
					} catch (Exception e) {
						throw new Exception(sql + "\n" + e.getMessage());
					}
				}
			}
			executeStetement(mDatabase, "DETACH DATABASE '" + alise + "'");
		}

		for (TableDefination td : definitions) {
			if (updates == null
					|| (updates.containsKey(td.getTableName()) && updates.get(
							td.getTableName()).intValue() > 0)) {
				doProcess(act, "Rebuild " + td.getTableName() + " geometries ",
						process, progressDialog);
				ArrayList<String> list = td.createUpdateStatement();
				for (String sql : list) {
					try {
						executeStetement(mDatabase, sql);
					} catch (Exception e) {
						throw new Exception(sql + "\n" + e.getMessage());
					}
				}
			}

		}
		executeStetement(mDatabase, "update mapinfo set  last_updated ="
				+ System.currentTimeMillis() + "  where id=" + subregion_id);
		// process = doProcess(act, "Vacuuming", process, progressDialog);
		// executeStetement(mDatabase, "vacuum");
		mDatabase.close();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				createMap();

			}
		});

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

	private void createMap() {
		try {

			WKTReader kml = new WKTReader();

			mapView = (MapView) findViewById(R.id.map);
			mapView.setColors(R.color.stroke_color, R.color.start_color,
					R.color.end_color);
			mapView.setClickable(true);
			mapView.setFocusable(true);

			MapOptions options = new MapOptions(MapDatabases.SPATIALITE_READER);
			options.put("file", dbPath);
			options.put("style_text",
					DocFlow.user_obj.getAndroid_map_renderer());
			mapView.setMapDatabase(options);

			Bundle bundle = this.getIntent().getExtras();
			if (!bundle.containsKey(MAP_COORDS)) {
				finish();
				return;
			}
			String coords = bundle.getString(MAP_COORDS);
			if (coords == null) {
				finish();
				return;
			}

			Point poly = kml.read(coords).getCentroid();

			byte zoom = 18;

			double[] dcoords = MetersToLatLon(poly.getX(), poly.getY());
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.detectAll().penaltyLog().build());
			GeoPoint gCoords = new GeoPoint(dcoords[0], dcoords[1]);
			MapPosition mp = new MapPosition(gCoords, zoom, 1);
			List<OverlayItem> aList = new ArrayList<OverlayItem>();
			aList.add(new OverlayItem("", "", gCoords));
			MarkersOverlay<OverlayItem> marker = new MarkersOverlay<OverlayItem>(
					drawable, mapView, this, aList);
			mapView.getOverlays().add(marker);
			mapView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			mapView.setMapCenter(mp);

			mapView.redrawMap(true);
		} catch (Throwable e) {
			e.printStackTrace();
			finish();
			return;

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
