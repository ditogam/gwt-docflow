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
package org.oscim.database.spatialite.sqlgenerator;

import jsqlite.Stmt;

import org.oscim.core.Tile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLGenerator {
	private final Table[] tables;

	private Stmt st = null;

	private Integer lastZoom = null;

	private String lastSql = null;

	private Integer lastCount = null;

	public SQLGenerator(Table[] tables) {
		this.tables = tables;

	}

	public String getBoundSql() {
		String tblSql = "";
		for (Table table : tables) {
			if (!tblSql.isEmpty())
				tblSql += "\n union all\n";
			tblSql += table.getBoundSql();
		}
		String ret = "select min(MbrMinX),min(MbrMinY), max(MbrMaxX)  ,max(MbrMaxY)\n from (select MbrMinX(the_geom) "
				+ "MbrMinX, MbrMinY(the_geom) MbrMinY,  MbrMaxX(the_geom) MbrMaxX, MbrMaxY(the_geom) MbrMaxY from ( "
				+ "" + ") b  ) f";
		ret = "select min(MbrMinX),min(MbrMinY), max(MbrMaxX)  ,max(MbrMaxY),max(cx),max(cy) \n from (select MbrMinX(the_geom) "
				+ "MbrMinX, MbrMinY(the_geom) MbrMinY,  MbrMaxX(the_geom) MbrMaxX, MbrMaxY(the_geom) MbrMaxY, X(point) cx, y(point) cy from ( "
				+ "SELECT ROWID,  the_geom, GeomFromText (ifnull(center_text, Centroid(the_geom))) point FROM map_info"
				+ ") b  ) f";

		return ret;
	}

	public synchronized Stmt getSQL(Tile tile, jsqlite.Database mDatabase,
			StringBuilder sqlText) throws Exception {
		if (lastZoom != null && lastZoom.byteValue() == tile.zoomLevel) {
			st.clear_bindings();
			st.reset();
			sqlText.append(lastSql);
		} else {
			String tblSql = "";
			int count = 0;
			for (Table table : tables) {
				String generated = table.getSQL(tile.zoomLevel, count);
				if (generated == null)
					continue;
				if (!tblSql.isEmpty())
					tblSql += "\n union all \n";
				tblSql += generated;
				count++;
			}
			if (st != null)
				try {
					st.close();
				} catch (Exception e) {

				}
			st = mDatabase.prepare(tblSql);
			if (sqlText != null)
				sqlText.append(tblSql);
			lastZoom = Integer.valueOf(tile.zoomLevel);
			lastSql = tblSql;
			lastCount = Integer.valueOf(count);

		}

		double x1 = org.oscim.core.MercatorProjection.tileXToLongitude(
				tile.tileX, tile.zoomLevel);
		double x2 = org.oscim.core.MercatorProjection.tileXToLongitude(
				tile.tileX + 1, tile.zoomLevel);

		double y1 = org.oscim.core.MercatorProjection.tileYToLatitude(
				tile.tileY, tile.zoomLevel);
		double y2 = org.oscim.core.MercatorProjection.tileYToLatitude(
				tile.tileY + 1, tile.zoomLevel);
		double min = 20037508.342789244 / 256 / (2 ^ tile.zoomLevel);
		int index = 1;
		for (int i = 0; i < lastCount.intValue(); i++) {
			// st.bind(index++, tile.tileX);
			// st.bind(index++, tile.tileY);
			// st.bind(index++, tile.zoomLevel);
			// st.bind(index++, Math.PI);
			st.bind(index++, x1);
			st.bind(index++, y1);
			st.bind(index++, x2);
			st.bind(index++, y2);
			st.bind(index++, min);

		}

		// try {
		// synchronized (this) {
		// if (st == null) {
		// String sql =
		// "select _layer,_id,geom,_tags_name from tile_data where z=? and x=? and y=? order by _layer";
		// // sqlText.append(sql);
		// st = mDatabase.prepare(sql);
		// } else {
		// st.clear_bindings();
		// st.reset();
		// }
		// int index = 1;
		// st.bind(index++, tile.zoomLevel);
		// st.bind(index++, tile.tileX);
		// st.bind(index++, tile.tileY);
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return st;

		return st;
	}

	public synchronized Cursor getSQL(Tile tile, SQLiteDatabase mDatabase)
			throws Exception {
		String tblSql = "";
		int count = 0;
		for (Table table : tables) {
			String generated = table.getSQL(tile.zoomLevel, count);
			if (generated == null)
				continue;
			if (!tblSql.isEmpty())
				tblSql += "\n union all \n";
			tblSql += generated;
			count++;
		}

		String[] selectionArgs = new String[count * 4];
		int index = 0;
		for (int i = 0; i < count; i++) {
			selectionArgs[index++] = "" + tile.tileX;
			selectionArgs[index++] = "" + tile.tileY;
			selectionArgs[index++] = "" + tile.zoomLevel;
			selectionArgs[index++] = "" + Math.PI;
		}
		Cursor st = mDatabase.rawQuery(tblSql, selectionArgs);

		return st;
	}

	public TagsParser getTagsParcerForLayer(int index) {
		try {
			return tables[index].getTagsParser();
		} catch (Exception e) {

		}
		return null;
	}

}
