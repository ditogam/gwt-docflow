package com.docflow.shared;

public class MapObjectTypes {
	public static final int MO_BUILDING_TYPE = 1;
	public static final int MO_DISTRICT_METER_TYPE = 2;
	public static final int[] MO_TYPES = new int[] { MO_BUILDING_TYPE,
			MO_DISTRICT_METER_TYPE };

	public static final int MOT_POINT = 1;
	public static final int MOT_LINE = 2;
	public static final int MOT_POLYGON = 3;

	public static String getMapObjectTypeTblName(int type) {
		switch (type) {
		case MO_BUILDING_TYPE:
			return "buildings";
		case MO_DISTRICT_METER_TYPE:
			return "district_meters";

		default:
			return null;
		}
	}

	public static boolean getMapObjectTypeIsPoint(int type) {
		switch (type) {
		case MO_DISTRICT_METER_TYPE:
			return true;
		default:
			return false;
		}
	}

	public static String getMapObjectTypeIcon(int type) {
		switch (type) {
		case MO_DISTRICT_METER_TYPE:
			return "img/district_meter48.png";
		default:
			return null;
		}
	}

	public static int[] getMapObjectTypeIconSize(int type) {
		switch (type) {
		case MO_DISTRICT_METER_TYPE:
			return new int[] { 25, 25 };
		default:
			return null;
		}
	}
}
