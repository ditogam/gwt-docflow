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

import java.util.ArrayList;

import org.oscim.core.Tag;
import org.oscim.database.spatialite.sqlgenerator.TagsParser;

public class Values {

	public static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public int layer;
	public int count;
	public String data;

	private final TagsParser tagsParser;

	public Values(int layer, int count, String data, TagsParser tagsParser) {
		this.layer = layer;
		this.data = data;
		this.count = count;
		this.tagsParser = tagsParser;
	}

	public ArrayList<RowValue> analyze() {
		ArrayList<RowValue> ret = new ArrayList<RowValue>();

		String[] values = this.data.split(";");
		if (values.length == 1
				&& (values[0] == null || values[0].trim().isEmpty()))
			return ret;
		for (String value : values) {
			try {
				String rowData[] = value.split(",");
				int id = Integer.parseInt(rowData[0].trim());
				byte[] bt = hexStringToByteArray(rowData[1].trim());
				Tag[] tags = new Tag[0];
				if (tagsParser != null)
					tags = tagsParser.parse(new String(
							hexStringToByteArray(rowData[2].trim()), "UTF8"));
				ret.add(new RowValue(id, bt, tags));
			} catch (Throwable e) {

			}

		}

		// XMLElement el = new XMLElement();
		// try {
		// el.parseString(data);
		// Vector<XMLElement> chldr = el.getChildren();
		// for (XMLElement ch : chldr) {
		// int id = ch.getIntAttribute("id", -1);
		// String ewkb_name = ch.getAttribute("ewkb_name", "").toString();
		// byte[] bt = hexStringToByteArray(ewkb_name);
		// String htags = ch.getAttribute("tags", "").toString();
		// byte[] btags = hexStringToByteArray(htags);
		// String tags = new String(btags);
		// ret.add(new RowValue(id, bt, tags));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return ret;
	}
}
