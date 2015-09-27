package com.docflow.server.db.map;

import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.VARCHAR;

import com.docflow.shared.MapObjectTypes;

public class DBMapObjectTypes extends MapObjectTypes {
	public static class SQLTypes {
		public String field_name;
		public int field_sql_type;

		public SQLTypes(String field_name, int field_sql_type) {
			this.field_name = field_name;
			this.field_sql_type = field_sql_type;
		}
	}

	public static final String getSQLForUpdate(int map_object_type) {
		switch (map_object_type) {
		case MO_BUILDING_TYPE:
			return "update buildings set street=?,senobis_no=?,sartuliano=?,cusid=? where buid=?";
		case MO_DISTRICT_METER_TYPE:
			return "update district_meters set cusid=? where id=? ";
		default:
			return null;
		}
	}

	public static final SQLTypes[] getSQLTypes(int map_object_type) {
		switch (map_object_type) {
		case MO_BUILDING_TYPE:
			return new SQLTypes[] { new SQLTypes("street", VARCHAR),
					new SQLTypes("senobis_no", VARCHAR),
					new SQLTypes("sartuliano", SMALLINT),
					new SQLTypes("cusid", INTEGER),
					new SQLTypes("buid", INTEGER) };
		case MO_DISTRICT_METER_TYPE:
			return new SQLTypes[] { new SQLTypes("cusid", INTEGER),
					new SQLTypes("buid", INTEGER) };
		default:
			return null;
		}
	}
}
