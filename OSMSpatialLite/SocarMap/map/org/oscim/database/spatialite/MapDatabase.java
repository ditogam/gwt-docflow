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

	// private static ArrayList<GeometryParser> parcer = null;

	private static final int SRID = 4326;

	GeometryFactory jtsFactory = new GeometryFactory(new PrecisionModel(), SRID);

	public MapDatabase() {
		// Table roads = new Table(
		// "roads",
		// "the_geom",
		// "ruid",
		// "'highway='||hex('road')||';name='||hex(ifnull(rname,''))",
		// 15, -1);
		// Table buildings = new Table(
		// "buildings",
		// "the_geom",
		// "buid",
		// "case when has_customer=0 then 'emptybuilding' else 'nonemptybuilding' end|| '='||hex('bld')",
		// 15, -1);

	}
	@Override
	public void cancel() {

	}

	// private static LinearRing createBox(final double x, final double dx,
	// final double y,
	// final double dy, final int npoints, final GeometryFactory gf) {
	//
	// // figure out the number of points per side
	// int ptsPerSide = npoints / 4;
	// int rPtsPerSide = npoints % 4;
	// Coordinate[] coords = new Coordinate[npoints + 1];
	// coords[0] = new Coordinate(x, y); // start
	// gf.getPrecisionModel().makePrecise(coords[0]);
	//
	// int cindex = 1;
	// for (int i = 0; i < 4; i++) { // sides
	// int npts = ptsPerSide + (rPtsPerSide-- > 0 ? 1 : 0);
	// // npts atleast 1
	//
	// if (i % 2 == 1) { // odd vert
	// double cy = dy / npts;
	// if (i > 1) // down
	// cy *= -1;
	// double tx = coords[cindex - 1].x;
	// double sy = coords[cindex - 1].y;
	//
	// for (int j = 0; j < npts; j++) {
	// coords[cindex] = new Coordinate(tx, sy + (j + 1) * cy);
	// gf.getPrecisionModel().makePrecise(coords[cindex++]);
	// }
	// } else { // even horz
	// double cx = dx / npts;
	// if (i > 1) // down
	// cx *= -1;
	// double ty = coords[cindex - 1].y;
	// double sx = coords[cindex - 1].x;
	//
	// for (int j = 0; j < npts; j++) {
	// coords[cindex] = new Coordinate(sx + (j + 1) * cx, ty);
	// gf.getPrecisionModel().makePrecise(coords[cindex++]);
	// }
	// }
	// }
	// coords[npoints] = new Coordinate(x, y); // end
	// gf.getPrecisionModel().makePrecise(coords[npoints]);
	//
	// return gf.createLinearRing(coords);
	// }

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public synchronized QueryResult executeQuery(JobTile tile,
			IMapDatabaseCallback mapDatabaseCallback) {

		// double x1 = MercatorProjection.tileXToLongitude(tile.tileX,
		// tile.zoomLevel);
		// double x2 = MercatorProjection.tileXToLongitude(tile.tileX + 1,
		// tile.zoomLevel);
		//
		// double y1 = MercatorProjection.tileYToLatitude(tile.tileY,
		// tile.zoomLevel);
		// double y2 = MercatorProjection.tileYToLatitude(tile.tileY + 1,
		// tile.zoomLevel);
		// String s = x1 + "," + y1 + "," + x2 + "," + y2;
		//
		// LinearRing lnring = jtsFactory.createLinearRing(new Coordinate[] {
		// new Coordinate(x1, y1), new Coordinate(x2, y1),
		// new Coordinate(x2, y2), new Coordinate(x1, y2),
		// new Coordinate(x1, y1) });
		//
		// Geometry tileGeom = jtsFactory.createPolygon(lnring, null);
		// tileGeom.setSRID(SRID);
		if (mDatabase == null) {
			if (!connect())
				return QueryResult.FAILED;
		}
		ArrayList<GeometryParser> parcer = new ArrayList<GeometryParser>();
		Stmt st = null;
		int count = 0;
		try {
			// Timer.start("generator.getSQL");
			StringBuilder sb = new StringBuilder();
			st = generator.getSQL(tile, mDatabase, sb);
			// Timer.step("generator.getSQL");
			// Timer.start("generator.loop");
			while (st.step()) {

				try {
					// Timer.start("values.add");
					count++;
					int layer = st.column_int(0);
					int id = count;
					id = st.column_int(1);
					byte[] bt = st.column_bytes(2);
					String tag = st.column_string(3);

					// Timer.start("reader.read");
					Geometry geom = null;
					try {
						geom = reader.read(bt);
						geom.setSRID(SRID);
					} catch (Exception e) {

						continue;
					}
					// Timer.step("reader.read");
					Tag[] tags = new Tag[0];
					TagsParser tp = generator.getTagsParcerForLayer(layer);
					// Timer.start("TagsParser.parse");
					if (tp != null)
						tags = tp.parse(tag);
					else
						continue;
					if (tags == null || tags.length == 0)
						continue;
					// Timer.step("TagsParser.parse");
					// Timer.start("GeometryParser.parse");
					parcer.add(new GeometryParser(geom, tile, tags, layer, id));
					// Timer.step("GeometryParser.parse");
					// Timer.step("values.add");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// if (count > 200)
			// System.out.println(count);
			// Timer.step("generator.loop");
			// Timer.printall();
			// Cache.writeCatch(values, tile);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().equals("stmt already closed")) {
				close();
				// return executeQuery(tile, mapDatabaseCallback);
			}
			return QueryResult.FAILED;
		} finally {

		}
		for (GeometryParser p : parcer) {
			mapDatabaseCallback.renderWay((byte) p.getLayer(), p.getTags(),
					p.getmCoords(), p.getmIndex(), p.isClosed(), 0);
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
		String fileName = options.get("style_file");
		if (fileName == null)
			fileName = "spatialite_map_style.xml";
		File file = new File(fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			AssetHelper.copyAsset(context1, "spatialite_map_style.xml", file);
		}
		Table[] tables = Table.getTables(fileName);

		generator = new SQLGenerator(tables);
		// Cache.initCache(context,
		// "/mnt/sdcard/socardb/tiles/tilecatch.sqlite");
		return OpenResult.SUCCESS;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
