/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.database.spatialite;

import java.io.File;
import java.util.ArrayList;

import jsqlite.Stmt;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.Tag;
import org.oscim.database.IMapDatabase;
import org.oscim.database.IMapDatabaseCallback;
import org.oscim.database.MapInfo;
import org.oscim.database.MapOptions;
import org.oscim.database.OpenResult;
import org.oscim.database.QueryResult;
import org.oscim.database.spatialite.sqlgenerator.SQLGenerator;
import org.oscim.database.spatialite.sqlgenerator.Table;
import org.oscim.database.spatialite.sqlgenerator.TagsParser;
import org.oscim.database.spatialite.utils.AssetHelper;
import org.oscim.database.spatialite.utils.GeometryParser;
import org.oscim.generator.JobTile;

import android.content.Context;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKBReader;

public class MapDatabase implements IMapDatabase {
	jsqlite.Database mDatabase = null;

	private static MapInfo mMapInfo = null;

	private boolean mOpenFile = false;

	private MapOptions options = null;

	private SQLGenerator generator = null;

	private Context context;
	private WKBReader reader = null;

	private static final int SRID = 4326;

	GeometryFactory jtsFactory = new GeometryFactory(new PrecisionModel(), SRID);

	@Override
	public void cancel() {

	}

	@Override
	public void close() {
		if (mDatabase != null) {
			try {
				mDatabase.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				mDatabase = null;
			}
		}

	}

	private boolean connect() {
		try {
			mDatabase = new jsqlite.Database();
			mDatabase.open(options.get("file"),
					jsqlite.Constants.SQLITE_OPEN_READONLY);
			reader = new WKBReader();
			Stmt st = mDatabase.prepare("PRAGMA cache_size = 1000000");
			st.step();
			st.close();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized QueryResult executeQuery(JobTile tile,
			IMapDatabaseCallback mapDatabaseCallback) {
		if (mDatabase == null) {
			if (!connect())
				return QueryResult.FAILED;
		}
		ArrayList<GeometryParser> parcer = new ArrayList<GeometryParser>();
		Stmt st = null;
		int count = 0;
		try {
			StringBuilder sb = new StringBuilder();
			st = generator.getSQL(tile, mDatabase, sb);

			while (st.step()) {

				try {
					count++;
					int layer = st.column_int(0);
					int id = count;
					id = st.column_int(1);
					byte[] bt = st.column_bytes(2);
					String tag = st.column_string(3);

					Geometry geom = null;
					try {
						geom = reader.read(bt);
						geom.setSRID(SRID);
					} catch (Exception e) {

						continue;
					}
					Tag[] tags = new Tag[0];
					TagsParser tp = generator.getTagsParcerForLayer(layer);
					if (tp != null)
						tags = tp.parse(tag);
					else
						continue;
					if (tags == null || tags.length == 0)
						continue;
					parcer.add(new GeometryParser(geom, tile, tags, layer, id));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
			if (e.getMessage().equals("stmt already closed")) {
				close();
			}
			return QueryResult.FAILED;
		} finally {

		}
		for (GeometryParser p : parcer) {

			if (!p.isPointData())
				mapDatabaseCallback.renderWay((byte) p.getLayer(), p.getTags(),
						p.getmCoords(), p.getmIndex(), p.isClosed(), 0);
			else
				mapDatabaseCallback.renderPointOfInterest((byte) p.getLayer(),
						p.getTags(), p.getLatitude(), p.getLongitude());
		}

		return QueryResult.SUCCESS;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public MapInfo getMapInfo() {

		if (mMapInfo != null)
			return mMapInfo;

		if (mDatabase == null) {
			if (!connect())
				return null;
		}

		double[] data = getProjectionData();

		mMapInfo = new MapInfo(new BoundingBox(data[1], data[0], data[3],
				data[2]), new Byte((byte) 15), new GeoPoint(data[5], data[4]),
				null, 0, 0, 0, "de", "comment", "author", new int[] { 8, 9, 10,
						11, 12, 13, 14, 15, 16, 17, 18, 19, 20 });

		return mMapInfo;
	}

	@Override
	public String getMapProjection() {

		return null;
	}

	private double[] getProjectionData() {
		double[] data = null;

		Stmt st = null;
		try {
			String sql = generator.getBoundSql();
			st = mDatabase.prepare(sql);

			if (st.step()) {
				data = new double[] { st.column_double(0), st.column_double(1),
						st.column_double(2), st.column_double(3),
						st.column_double(4), st.column_double(5) };

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (Exception e2) {
			}
		}
		return data;
	}

	@Override
	public boolean isOpen() {
		return mOpenFile;
	}

	@Override
	public OpenResult open(MapOptions options1, Context context1) {
		this.options = options1;
		this.setContext(context1);
		mOpenFile = true;
		Table[] tables = null;

		if (options.containsKey("style_text")) {
			tables = Table.getTableValues(options.get("style_text"));
		} else {
			String fileName = options.get("style_file");
			if (fileName == null)
				fileName = "spatialite_map_style.xml";
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				AssetHelper.copyAsset(context1, "spatialite_map_style.xml",
						file);
			}
			tables = Table.getTablesFromFile(fileName);
		}

		generator = new SQLGenerator(tables);
		// Cache.initCache(context,
		// "/mnt/sdcard/socardb/tiles/tilecatch.sqlite");
		return OpenResult.SUCCESS;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
