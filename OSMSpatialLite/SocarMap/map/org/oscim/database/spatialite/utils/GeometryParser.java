package org.oscim.database.spatialite.utils;

import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.Tile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

//import com.vividsolutions.jts.geom.Geometry;

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

	// private static final GoogleMapsProjection2 pr = new
	// GoogleMapsProjection2();
	// private static final GlobalMercator gm = new GlobalMercator();
	private final Tag[] tags;
	private int mCoordPos = 0;
	private int layer = 0;
	private int mIndexPos = 0;
	private float[] mCoords;

	private final short[] mIndex;

	private final float mScale = 1;

	private final boolean closed;

	public GeometryParser(byte[] b, Tile tile, Tag[] tags) {
		mIndexPos = 0;
		mCoordPos = 0;
		mCoords = new float[100000];
		mIndex = new short[10];
		closed = parse(b);

		int len = mIndex[0];
		float[] coords = new float[len];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = mCoords[i];
		}
		mCoords = coords;
		for (int i = 0; i < mCoords.length; i++) {
			float x = mCoords[i];
			float y = mCoords[i + 1];
			double px = MercatorProjection.longitudeToPixelX(x, tile.zoomLevel);
			double py = MercatorProjection.latitudeToPixelY(y, tile.zoomLevel);
			px -= tile.pixelX;
			py -= tile.pixelY;
			mCoords[i] = (float) px;
			mCoords[i + 1] = (float) py;
			i++;
		}
		this.tags = tags;
		// if (tags == null)
		// tags = "";
		// this.tags = new Tag[0];
		// tags = tags.trim();
		// if (tags.isEmpty())
		// return;
		//
		// try {
		// ArrayList<Tag> aTags = new ArrayList<Tag>();
		// XMLElement el = new XMLElement();
		// el.parseString(tags);
		// Vector<XMLElement> chldr = el.getChildren();
		//
		// for (XMLElement ch : chldr) {
		// String key = ch.getStringAttribute("key");
		// String val = ch.getStringAttribute("val");
		// Tag tag = new Tag(key, val);
		// aTags.add(tag);
		// }
		// this.tags = aTags.toArray(new Tag[0]);
		// } catch (Exception e) {
		//
		// }

	}

	public GeometryParser(com.vividsolutions.jts.geom.Geometry geom, Tile tile,
			Tag[] tags, int layer, int id) {
		mIndexPos = 0;
		mCoordPos = 0;

		mIndex = new short[1];

		// boolean contains = geom.contains(tileGeom);
		// boolean within=geom.within(tileGeom);
		Coordinate[] coords = geom.getCoordinates();
		int len = coords.length;

		mIndex[0] = (short) (len * 2);

		mCoords = new float[coords.length * 2];
		try {
			int index = 0;
			for (int i = 0; i < coords.length; i++) {
				float x = (float) coords[i].x;
				float y = (float) coords[i].y;
				double px = MercatorProjection.longitudeToPixelX(x,
						tile.zoomLevel);
				double py = MercatorProjection.latitudeToPixelY(y,
						tile.zoomLevel);
				px -= tile.pixelX;
				py -= tile.pixelY;

				mCoords[index++] = (float) px;
				mCoords[index++] = (float) py;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.tags = tags;
		closed = (geom instanceof MultiPolygon || geom instanceof Polygon);
		this.layer = layer;
	}

	public int getLayer() {
		return layer;
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

	public Tag[] getTags() {
		return tags;
	}

	public boolean isClosed() {
		return closed;
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
