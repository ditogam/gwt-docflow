package com.socarimporter;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.TreeMap;

import jsqlite.Database;
import jsqlite.Stmt;
import android.annotation.SuppressLint;
import android.app.Activity;
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

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnItemSelectedListener,
		OnItemClickListener, OnClickListener {
	private Spinner spRegion;
	private Spinner spSubregions;
	private CheckBox cbAllZones;
	private ListView lvZones;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		loginSucceed();
	}

	private void loginSucceed() {
		setContentView(R.layout.activity_main);
		Button b = (Button) findViewById(R.id.b_download);
		spRegion = (Spinner) findViewById(R.id.sp_region);
		spSubregions = (Spinner) findViewById(R.id.sp_subregion);
		cbAllZones = (CheckBox) findViewById(R.id.ckb_all_zones);
		lvZones = (ListView) findViewById(R.id.lv_zones);
		setupSpinnerFromMap(spRegion,
				Utils.getMap("getclassifier.jsp?type=region"));
		setupSpinnerFromMap(spSubregions, null);
		setupListViewFromMap(lvZones, null);
		b.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick(View v) {
		try {
			IDValue sr = (IDValue) spSubregions.getSelectedItem();
			if (sr == null) {
				ActivityHelper.showAlert(this, "Please select subregion!!!");
				return;
			}
			String zones = "";
			if (!cbAllZones.isChecked()) {
				ArrayList<IDValue> values = ((SelectArrayAdapter) lvZones
						.getAdapter()).getItems();
				for (IDValue idValue : values) {
					if (idValue.isSelected()) {
						if (!zones.isEmpty())
							zones += ",";
						zones += idValue.getId();
					}

				}
				if (!zones.isEmpty())
					zones = "&zones=" + zones;
			}

			File SDCardRoot = Environment.getExternalStorageDirectory();
			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			File file = new File(SDCardRoot, "mydb.zip");
			Utils.downloadFile(file, "index.jsp?subregion=" + sr.getId()
					+ zones);

			String fs = System.getProperty("file.separator");
			String dbPath = file.getParentFile().getAbsolutePath();
			if (!dbPath.endsWith(fs))
				dbPath += fs;
			dbPath += "socardb";
			Utils.unzip(file, dbPath);
			file.delete();
			String dbFile = dbPath + fs + "mydb.sqlite";
			jsqlite.Database mDatabase = new Database();
			mDatabase.open(dbFile, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			executeStetement(
					mDatabase,
					"update buildings set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");
			executeStetement(mDatabase,
					"SELECT RebuildGeometryTriggers('buildings', 'the_geom')");
			executeStetement(mDatabase,
					"SELECT RecoverSpatialIndex('buildings', 'the_geom',1)");
			executeStetement(mDatabase,
					"SELECT RecoverSpatialIndex('buildings', 'the_geom',0)");

			executeStetement(
					mDatabase,
					"update district_meters set the_geom=GeomFromText(geom_text,4326), geom_text=null where geom_text is not null");
			executeStetement(mDatabase,
					"SELECT RebuildGeometryTriggers('district_meters', 'the_geom')");
			executeStetement(mDatabase,
					"SELECT RecoverSpatialIndex('district_meters', 'the_geom',1)");
			executeStetement(mDatabase,
					"SELECT RecoverSpatialIndex('district_meters', 'the_geom',0)");
			executeStetement(mDatabase, "vacuum");

			mDatabase.close();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ActivityHelper.showAlert(this, sw.toString());
		}
	}

	private void executeStetement(jsqlite.Database mDatabase, String stetement)
			throws jsqlite.Exception {
		Stmt stmt = null;
		stmt = mDatabase.prepare(stetement);
		if (stmt.step()) {
			System.out.println("");
		}
		stmt.close();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		IDValue g = (IDValue) parent.getItemAtPosition(pos);
		int vId = parent.getId();
		if (vId == R.id.sp_region) {
			setupSpinnerFromMap(
					spSubregions,
					Utils.getMap("getclassifier.jsp?type=subregion&parentid="
							+ g.getId()));
			setupListViewFromMap(lvZones, null);
			spSubregions.setSelection(Adapter.NO_SELECTION);

		}
		if (vId == R.id.sp_subregion) {
			setupListViewFromMap(
					lvZones,
					Utils.getMap("getclassifier.jsp?type=zones&parentid="
							+ g.getId()));

		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void setupSpinnerFromMap(Spinner spinner, TreeMap<Long, String> map) {
		IDValueAdapter adapter = new IDValueAdapter(map,
				getApplicationContext());
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	private void setupListViewFromMap(ListView listView,
			TreeMap<Long, String> map) {
		ArrayList<IDValue> values = new ArrayList<IDValue>();
		if (map != null)
			for (Long key : map.keySet()) {
				values.add(new IDValue(key, map.get(key)));
			}
		final SelectArrayAdapter listAdapter = new SelectArrayAdapter(this,
				values);
		lvZones.setAdapter(listAdapter);
	}
}
