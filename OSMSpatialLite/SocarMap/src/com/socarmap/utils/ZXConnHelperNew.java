package com.socarmap.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.socarmap.db.SQLiteConnection;

public class ZXConnHelperNew {
	public static void main(String[] args) {
		// new ZXConnHelper(new File(
		// "/Users/dito/Documents/android1/MapTilesExtractor/exported"),
		// null);
	}
	private Map<Integer, Zooms> zooms = new TreeMap<Integer, Zooms>();
	private ZoomConnection zoomConnection;
	private ZX1X2 lastZx1x2 = null;
	private Context context;

	private File parrent_path;

	private ArrayList<SqlAction> sqlActions = new ArrayList<SqlAction>();

	public ZXConnHelperNew(File parrent_path, ZoomConnection zoomConnection,
			Context context) {
		this.context = context;
		this.parrent_path = parrent_path;
		this.zoomConnection = zoomConnection;
		generateZooms(parrent_path);

		sqlActions.add(new SqlAction("Attach db", "ATTACH '%s' AS updated"));
		sqlActions.add(new SqlAction("Delete old tiles DB",
				"delete from  mapfiledatazxy\n" + "where rowid in (\n"
						+ "select s.rowid from \n"
						+ "  updated.mapfiledatazxy u,mapfiledatazxy s \n"
						+ " where u.zoom=s.zoom and u.x=s.x and u.y=s.y)"));
		sqlActions
				.add(new SqlAction(
						"Insert new tiles db",
						"insert into mapfiledatazxy(zoom, x, y, file_data, proceeded, rcn, created_on)\n"
								+ "SELECT zoom, x, y, file_data, proceeded, rcn, created_on\n"
								+ "FROM updated.mapfiledatazxy"));

		sqlActions
				.add(new SqlAction(
						"Creating temp zoom",
						"create table tmp_zooms as\n"
								+ "select zoom, min(minx) minx ,max(maxx) maxx from\n"
								+ "(select zoom,minx,maxx from zooms\n"
								+ "union all\n"
								+ "select zoom,min(x) minx,max(x) maxx from updated.mapfiledatazxy group by zoom)\n"
								+ "b group by zoom"));
		sqlActions.add(new SqlAction("dettach db", "DETACH updated"));
		sqlActions.add(new SqlAction("Drop zoom", "drop table zooms"));
		sqlActions
				.add(new SqlAction(
						"Create zooms",
						"CREATE TABLE zooms (zoom INTEGER, minx INTEGER, maxx INTEGER, PRIMARY KEY(zoom, minx, maxx))"));
		sqlActions.add(new SqlAction("Insert zooms",
				"insert into zooms  select zoom,minx,maxx from tmp_zooms"));
		sqlActions.add(new SqlAction("Drop temp zoom", "drop table tmp_zooms"));

	}

	public ArrayList<ZX1X2> findZooms(int zoom, int x, int y) {
		Zooms z = zooms.get(zoom);
		ArrayList<ZX1X2> ret = new ArrayList<ZX1X2>();
		if (z == null)
			return ret;
		ArrayList<ZX1X2> zx1x2s = z.getZx1x2s();
		if (zx1x2s == null || zx1x2s.isEmpty())
			return ret;

		for (ZX1X2 zx1x2 : zx1x2s) {
			if (zx1x2.acept(zoom, x))
				ret.add(zx1x2);
		}

		return ret;
	}

	private void generateZooms(File file) {
		File[] zooms = getZoomFiles(file);
		for (File fzoom : zooms) {
			ArrayList<ZX1X2> mzooms = SqliteZoomedConnectionNew.getminiMax(
					context, fzoom);
			for (ZX1X2 zx1x21 : mzooms) {
				Zooms z = this.zooms.get(zx1x21.getZoom());
				if (z == null) {
					z = new Zooms(zx1x21.getZoom());
					this.zooms.put(zx1x21.getZoom(), z);
				}

				z.addXs(zx1x21);
			}

		}

	}

	public byte[] getImage(int zoom, int x, int y) throws Exception {

		byte[] data = null;
		if (lastZx1x2 != null && lastZx1x2.acept(zoom, x)) {
			data = zoomConnection.getImageData(lastZx1x2, zoom, x, y, true);
		}
		if (data == null) {
			Zooms z = zooms.get(zoom);
			if (z == null)
				return null;
			ArrayList<ZX1X2> zx1x2s = z.getZx1x2s();
			if (zx1x2s == null || zx1x2s.isEmpty())
				return null;
			for (ZX1X2 zx1x2 : zx1x2s) {
				if (lastZx1x2 != null && lastZx1x2.equals(zx1x2))
					continue;
				if (zx1x2.acept(zoom, x)) {
					data = zoomConnection.getImageData(zx1x2, zoom, x, y, true);
					if (data != null) {
						lastZx1x2 = zx1x2;
						return data;
					}
				}
			}
			lastZx1x2 = null;
		}
		return data;
	}

	public int getStepCount() {
		return getZoomFiles(parrent_path).length * sqlActions.size();
	}

	public ZoomConnection getZoomConnection() {
		return zoomConnection;
	}

	private File[] getZoomFiles(File file) {
		FilenameFilter fnf = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.endsWith(".sqlite");
			}
		};
		File[] zooms = file.listFiles(fnf);
		return zooms;
	}

	public void updateAll(File file, Progress progress) throws Throwable {

		File[] zooms = getZoomFiles(parrent_path);
		for (File fzoom : zooms) {
			SQLiteConnection connectionHelper = null;
			SQLiteDatabase database = null;
			try {
				connectionHelper = new SQLiteConnection(context, fzoom);
				database = connectionHelper.getReadableDatabase();
				for (SqlAction action : sqlActions) {
					String sql = action.getSql();
					if (sql.indexOf("%s") >= 0)
						sql = String.format(sql, file.getAbsolutePath());
					if (progress != null)
						progress.updateTitle(action.getName());
					database.execSQL(sql);
					if (progress != null)
						progress.stepIt();
				}
			} catch (Throwable e) {
				throw e;
			} finally {
				try {
					database.close();
				} catch (Exception e2) {

				}
				try {
					connectionHelper.close();
				} catch (Exception e2) {

				}
			}
		}
		this.zooms.clear();
		generateZooms(parrent_path);
	}

}
