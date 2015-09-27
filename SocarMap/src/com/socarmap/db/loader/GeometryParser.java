package com.socarmap.db.loader;

public class GeometryParser {

	private static ValueGetter valueGetterForEndian(byte[] bytes) {
		if (bytes[0] == ValueGetter.XDR.NUMBER) { // XDR
			return new ValueGetter.XDR(bytes);
		} else if (bytes[0] == ValueGetter.NDR.NUMBER) {
			return new ValueGetter.NDR(bytes);
		} else {
			throw new IllegalArgumentException("Unknown Endian type:"
					+ bytes[0]);
		}
	}

	private final float mScale = 1;

	private int mCoordPos = 0;
	private int mIndexPos = 0;
	private float[] mCoords;
	private short[] mIndex;

	private boolean succeded;

	public GeometryParser(byte[] b) {
		mIndexPos = 0;
		mCoordPos = 0;
		mCoords = new float[100000];
		mIndex = new short[100000];
		succeded = parse(b);
	}

	public int getmCoordPos() {
		return mCoordPos;
	}

	public float[] getmCoords() {
		return mCoords;
	}

	public short[] getmIndex() {
		return mIndex;
	}

	public int getmIndexPos() {
		return mIndexPos;
	}

	public float getmScale() {
		return mScale;
	}

	public boolean isSucceded() {
		return succeded;
	}

	private boolean parse(byte[] value) {
		return parseGeometry(valueGetterForEndian(value));
	}

	private void parseCollection(ValueGetter data) {
		int count = data.getInt();
		parseGeometryArray(data, count);
	}

	private boolean parseGeometry(ValueGetter data) {
		byte endian = data.getByte(); // skip and test endian flag
		if (endian != data.endian) {
			throw new IllegalArgumentException("Endian inconsistency!");
		}
		int typeword = data.getInt();

		int realtype = typeword & 0x1FFFFFFF; // cut off high flag bits

		boolean haveZ = (typeword & 0x80000000) != 0;
		boolean haveM = (typeword & 0x40000000) != 0;
		boolean haveS = (typeword & 0x20000000) != 0;

		// int srid = Geometry.UNKNOWN_SRID;
		boolean polygon = false;
		if (haveS) {
			// srid = Geometry.parseSRID(data.getInt());
			data.getInt();
		}
		switch (realtype) {
		case Geometry.POINT:
			parsePoint(data, haveZ, haveM);
			break;
		case Geometry.LINESTRING:
			parseLineString(data, haveZ, haveM);
			break;
		case Geometry.POLYGON:
			parsePolygon(data, haveZ, haveM);
			polygon = true;
			break;
		case Geometry.MULTIPOINT:
			parseMultiPoint(data);
			break;
		case Geometry.MULTILINESTRING:
			parseMultiLineString(data);
			break;
		case Geometry.MULTIPOLYGON:
			parseMultiPolygon(data);
			polygon = true;
			break;
		case Geometry.GEOMETRYCOLLECTION:
			parseCollection(data);
			break;
		default:
			throw new IllegalArgumentException("Unknown Geometry Type: "
					+ realtype);
		}
		// if (srid != Geometry.UNKNOWN_SRID) {
		// result.setSrid(srid);
		// }
		return polygon;
	}

	/**
	 * Parse an Array of "full" Geometries
	 * 
	 * @param data
	 *            ...
	 * @param count
	 *            ...
	 */
	private void parseGeometryArray(ValueGetter data, int count) {
		for (int i = 0; i < count; i++) {
			parseGeometry(data);
			mIndex[mIndexPos++] = 0;
		}
	}

	private void parseLineString(ValueGetter data, boolean haveZ, boolean haveM) {
		int count = data.getInt();
		for (int i = 0; i < count; i++) {
			mCoords[mCoordPos++] = (float) (data.getDouble()) * mScale;
			mCoords[mCoordPos++] = (float) (data.getDouble()) * mScale;
			if (haveZ)
				data.getDouble();
			if (haveM)
				data.getDouble();
		}
		mIndex[mIndexPos++] = (short) (count * 2);
	}

	private void parseMultiLineString(ValueGetter data) {
		int count = data.getInt();
		parseGeometryArray(data, count);
	}

	private void parseMultiPoint(ValueGetter data) {
		parseGeometryArray(data, data.getInt());
	}

	private void parseMultiPolygon(ValueGetter data) {
		int count = data.getInt();
		parseGeometryArray(data, count);
	}

	private void parsePoint(ValueGetter data, boolean haveZ, boolean haveM) {
		// double X = data.getDouble();
		// double Y = data.getDouble();
		mCoords[0] = (float) (data.getDouble() * mScale);
		mCoords[1] = (float) (data.getDouble() * mScale);
		mIndex[0] = 2;
		mIndexPos = 1;
		if (haveZ)
			data.getDouble();

		if (haveM)
			data.getDouble();

	}

	private void parsePolygon(ValueGetter data, boolean haveZ, boolean haveM) {
		int count = data.getInt();

		for (int i = 0; i < count; i++) {
			parseLineString(data, haveZ, haveM);
		}
	}
}
