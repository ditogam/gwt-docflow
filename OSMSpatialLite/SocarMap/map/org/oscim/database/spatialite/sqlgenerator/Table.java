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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

import org.oscim.database.spatialite.utils.XMLElement;

public class Table {
	@SuppressWarnings("unchecked")
	public static Table[] getTables(String filename) {
		ArrayList<Table> tables = new ArrayList<Table>();
		FileReader fr = null;
		try {
			fr = new FileReader(filename);
			XMLElement el = new XMLElement();
			el.parseFromReader(fr);
			Vector<XMLElement> chldr = el.getChildren();

			for (XMLElement ch : chldr) {
				Table tbl = new Table(ch.getStringAttribute("table_name"),
						ch.getStringAttribute("geom_name"),
						ch.getStringAttribute("id_name"),
						ch.getStringAttribute("tags_name"), ch.getIntAttribute(
								"srid", 4326),
						ch.getIntAttribute("minZoom", -1), ch.getIntAttribute(
								"maxZoom", -1));
				tables.add(tbl);
			}
		} catch (Exception e) {

		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (Exception e2) {

				}
		}
		return tables.toArray(new Table[0]);
	}
	private String table_name;
	private String geom_name;
	private String tags_name;
	private int srid = 4326;
	private String id_name;
	private int minZoom = -1;
	private int maxZoom = -1;

	private TagsParser tagsParser;

	public Table(String table_name, String geom_name, String id_name,
			String tags_name) {
		setDefaultFields(table_name, geom_name, id_name, tags_name);
	}

	public Table(String table_name, String geom_name, String id_name,
			String tags_name, int srid) {
		setDefaultFields(table_name, geom_name, id_name, tags_name);
		this.srid = srid;
	}

	public Table(String table_name, String geom_name, String id_name,
			String tags_name, int minZoom, int maxZoom) {
		setDefaultFields(table_name, geom_name, id_name, tags_name);
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public Table(String table_name, String geom_name, String id_name,
			String tags_name, int srid, int minZoom, int maxZoom) {
		setDefaultFields(table_name, geom_name, id_name, tags_name);
		this.srid = srid;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public String getBoundSql() {
		return "select transform(" + geom_name + "," + srid
				+ ") the_geom from " + table_name;
	}

	public String getSQL(int zoom, int index) {

		if (minZoom > -1 && zoom < minZoom)
			return null;
		if (maxZoom > -1 && zoom > minZoom)
			return null;

		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append("SELECT " + index + " _layer, " + id_name + " _id,"
				+ "AsBinary( " + geom_name + ") geom," + tags_name
				+ " _tags_name \n");
		sb.append("FROM " + table_name + ", \n");
		sb.append("(select transform(BuildMbr(min_x,min_y,max_x,max_y,900913),"
				+ srid + ") Geometry from (select *,\n");
		sb.append("	(x* 256 * resolution - 2 * PI * DIV / 2.0) min_x,\n");
		sb.append("	(my* 256 * resolution - 2 * PI * DIV / 2.0) min_y,\n");
		sb.append("	(x1* 256 * resolution - 2 * PI * DIV / 2.0) max_x,\n");
		sb.append("	(y1* 256 * resolution - 2 * PI * DIV / 2.0) max_y\n");
		sb.append("		from (select *,x+1 x1,my+1 y1  \n");
		sb.append("		from (\n");
		sb.append("			select *,cast(pow(2,z)-y-1 as int) as my, (2 * PI * 6378137 / 256) / pow(2, z) as resolution\n");
		sb.append("			from (select ? x,? y, ? z, ? PI, 6378137 DIV) z) f) b) s ) po\n");
		sb.append("where " + id_name + " in (\n");
		sb.append("select pkid from idx_" + table_name + "_" + geom_name
				+ " where pkid MATCH\n");
		sb.append("RtreeIntersects(MbrMinX(po.Geometry), MbrMinY(po.Geometry),MbrMaxX(po.Geometry), MbrMaxY(po.Geometry)))\n");

		sb = new StringBuilder();
		sb.append("SELECT " + index + " _layer, " + id_name + " _id,"
				+ "AsBinary( case when Within(buffered, " + geom_name
				+ ")=1 then buffered else (Intersection(buffered," + geom_name
				+ ")) end) geom," + tags_name + " _tags_name \n");
		sb.append("FROM "
				+ table_name
				+ ",(select x1, y1,x2, y2,simp, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (\n"
				+ "select *,transform(Geometry,3035) mGeometry from\n"
				+ "(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (select ? x1 ,? y1,? x2 ,? y2, 50 diff,? simp )))\n"
				+ ") po \n");
		sb.append("where " + id_name + " in (\n");
		sb.append("select pkid from idx_" + table_name + "_" + geom_name
				+ " where pkid MATCH\n");
		sb.append("RtreeIntersects(x1, y1,x2, y2))\n");

		return sb.toString();
	}

	public TagsParser getTagsParser() {
		if (tagsParser == null)
			tagsParser = new DefaultTagsParser();
		return tagsParser;
	}

	private void setDefaultFields(String table_name, String geom_name,
			String id_name, String tags_name) {
		this.table_name = table_name;
		this.geom_name = geom_name;
		this.id_name = id_name;
		this.tags_name = tags_name;
	}

	public void setTagsParser(TagsParser tagsParser) {
		this.tagsParser = tagsParser;
	}

}
