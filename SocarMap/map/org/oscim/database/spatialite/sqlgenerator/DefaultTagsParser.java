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

import java.util.ArrayList;

import org.oscim.core.Tag;
import org.oscim.database.spatialite.Values;

public class DefaultTagsParser extends TagsParser {

	@Override
	public Tag[] parse(String sTags) {

		String[] values = sTags.split(";");
		if (values.length == 1
				&& (values[0] == null || values[0].trim().isEmpty()))
			return new Tag[0];
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (int i = 0; i < values.length; i++) {
			String sTag = values[i].trim();
			String[] sPair = sTag.split("=");

			if (sPair.length == 1
					&& (sPair[0] == null || sPair[0].trim().isEmpty()))
				continue;
			String key = sPair[0].trim();
			String value = null;
			try {
				value = sPair.length > 1 ? new String(
						Values.hexStringToByteArray(sPair[1].trim()), "UTF8")
						: null;
			} catch (Throwable e) {

			}
			tags.add(new Tag(key, value));

		}
		return tags.toArray(new Tag[0]);
	}

}
