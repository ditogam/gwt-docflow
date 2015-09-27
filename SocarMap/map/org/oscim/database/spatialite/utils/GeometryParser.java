package org.oscim.database.spatialite.utils;

import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.Tile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
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

	private boolean pointData;

	private float latitude;
	private float longitude;

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public GeometryParser(com.vividsolutions.jts.geom.Geometry geom, Tile tile,
			Tag[] tags, int layer, int id) {
		mIndexPos = 0;
		mCoordPos = 0;

		mIndex = new short[1];

		Coordinate[] coords = geom.getCoordinates();
		int len = coords.length;

		mIndex[0] = (short) (len * 2);

		mCoords = new float[coords.length * 2];
		pointData = geom instanceof Point || geom instanceof MultiPoint;

		if (pointData) {
			Coordinate point = geom.getCentroid().getCoordinate();
			longitude = (float) MercatorProjection.longitudeToPixelX(point.x,
					tile.zoomLevel);
			latitude = (float) MercatorProjection.latitudeToPixelY(point.y,
					tile.zoomLevel);
			longitude -= tile.pixelX;
			latitude -= tile.pixelY;

			// longitude = (float) point.x;
			// latitude = (float) point.y;

		} else {
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

	public boolean isPointData() {
		return pointData;
	}

}
