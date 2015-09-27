/*
 * Copyright 2012, 2013 Hannes Janetzek
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
package org.oscim.generator;

import static org.oscim.generator.JobTile.STATE_NONE;

import java.util.Arrays;

import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.database.IMapDatabase;
import org.oscim.database.IMapDatabaseCallback;
import org.oscim.database.QueryResult;
import org.oscim.renderer.MapTile;
import org.oscim.renderer.layer.ExtrusionLayer;
import org.oscim.renderer.layer.Layer;
import org.oscim.renderer.layer.Layers;
import org.oscim.renderer.layer.LineLayer;
import org.oscim.renderer.layer.LineTexLayer;
import org.oscim.renderer.layer.PolygonLayer;
import org.oscim.renderer.layer.TextItem;
import org.oscim.theme.IRenderCallback;
import org.oscim.theme.RenderTheme;
import org.oscim.theme.renderinstruction.Area;
import org.oscim.theme.renderinstruction.Line;
import org.oscim.theme.renderinstruction.RenderInstruction;
import org.oscim.theme.renderinstruction.Text;
import org.oscim.utils.LineClipper;
import org.oscim.view.DebugSettings;
import org.oscim.view.MapView;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;

/**
 * @author Hannes Janetzek
 * @note 1. The MapWorkers call TileGenerator.execute() to load a tile. 2. The
 *       tile data will be loaded from current MapDatabase 3. MapDatabase calls
 *       the IMapDatabaseCallback functions implemented by TileGenerator for WAY
 *       and POI items. 4. these callbacks then call RenderTheme to get the
 *       matching style. 5. RenderTheme calls IRenderCallback functions with
 *       style information 6. Styled items become added to MapTile.layers...
 *       roughly
 */
public class TileGenerator implements IRenderCallback, IMapDatabaseCallback {

	private static String TAG = TileGenerator.class.getName();

	private static final double STROKE_INCREASE = Math.sqrt(2.2);
	private static final byte LAYERS = 11;

	public static final byte STROKE_MIN_ZOOM_LEVEL = 12;
	public static final byte STROKE_MAX_ZOOM_LEVEL = 17;

	private static RenderTheme renderTheme;
	private static int renderLevels;
	private static DebugSettings debug;

	private static byte getValidLayer(byte layer) {
		if (layer < 0) {
			return 0;
		} else if (layer >= LAYERS) {
			return LAYERS - 1;
		} else {
			return layer;
		}
	}

	// current MapDatabase used by this TileGenerator
	private IMapDatabase mMapDatabase;

	// currently processed tile
	private MapTile mTile;
	// coordinates of the currently processed way
	private float[] mCoords;

	private short[] mIndices;

	// current line layer, will be added to outline layers
	private LineLayer mCurLineLayer;

	// layer data prepared for rendering
	private Layers mLayers;

	private TextItem mLabels;

	private int mDrawingLayer;

	private float mStrokeScale = 1.0f;

	// private final MapView mMapView;

	private RenderInstruction[] mRenderInstructions = null;
	private final Tag[] debugTagBox = { new Tag("debug", "box") };
	private final Tag[] debugTagWay = { new Tag("debug", "way") };
	private final Tag[] debugTagArea = { new Tag("debug", "area") };
	private final float[] debugBoxCoords = { 0, 0, 0, Tile.TILE_SIZE,
			Tile.TILE_SIZE, Tile.TILE_SIZE, Tile.TILE_SIZE, 0, 0, 0 };

	private final short[] debugBoxIndex = { 10 };

	private float mProjectionScaleFactor;
	private float mPoiX, mPoiY;

	private int mPriority;
	// replacement for variable value tags that should not be matched by
	// RenderTheme
	private final static Tag mTagEmptyName = new Tag(Tag.TAG_KEY_NAME, null,
			false);
	private final static Tag mTagEmptyHouseNr = new Tag(
			Tag.TAG_KEY_HOUSE_NUMBER, null, false);

	public static void setDebugSettings(DebugSettings debugSettings) {
		debug = debugSettings;
	}

	public static void setRenderTheme(RenderTheme theme) {
		renderTheme = theme;
		renderLevels = theme.getLevels();
	}

	private Tag mTagName;

	private Tag mTagHouseNr;

	private boolean mDebugDrawPolygons;

	boolean mDebugDrawUnmatched;

	private final LineClipper mClipper;

	private boolean mRenderBuildingModel;

	private boolean mClosed;

	/**
	 * @param mapView
	 *            the MapView
	 */
	public TileGenerator(MapView mapView) {
		// mMapView = mapView;
		mClipper = new LineClipper(0, 0, Tile.TILE_SIZE, Tile.TILE_SIZE, true);
	}

	@Override
	public boolean checkWay(Tag[] tags, boolean closed) {

		mRenderInstructions = TileGenerator.renderTheme.matchWay(this, tags,
				(byte) (mTile.zoomLevel + 0), closed, false);

		return mRenderInstructions != null;
	}

	public void cleanup() {
	}

	private void debugUnmatched(boolean closed, Tag[] tags) {

		Log.d(TAG,
				"DBG way not matched: " + closed + " "
						+ Arrays.deepToString(tags));

		mTagName = new Tag("name", tags[0].key + ":" + tags[0].value, false);

		if (closed) {
			TileGenerator.renderTheme.matchWay(this, debugTagArea, (byte) 0,
					true, true);
		} else {
			TileGenerator.renderTheme.matchWay(this, debugTagWay, (byte) 0,
					true, true);
		}
	}

	public boolean executeJob(JobTile jobTile) {
		MapTile tile;

		if (mMapDatabase == null)
			return false;

		tile = mTile = (MapTile) jobTile;

		mDebugDrawPolygons = !debug.disablePolygons;
		mDebugDrawUnmatched = debug.debugTheme;

		if (tile.layers != null) {
			// should be fixed now.
			Log.d(TAG, "BUG tile already loaded " + tile + " " + tile.state);
			return false;
		}

		setScaleStrokeWidth(tile.zoomLevel);

		// account for area changes with latitude
		mProjectionScaleFactor = 0.5f + 0.5f * ((float) Math.sin(Math
				.abs(MercatorProjection.pixelYToLatitude(tile.pixelY,
						tile.zoomLevel))
				* (Math.PI / 180)));

		mLayers = new Layers();
		if (mMapDatabase.executeQuery(tile, this) != QueryResult.SUCCESS) {
			// Log.d(TAG, "Failed loading: " + tile);
			mLayers.clear();
			mLayers = null;
			TextItem.release(mLabels);
			mLabels = null;

			// FIXME add STATE_FAILED?
			tile.state = STATE_NONE;
			return false;
		}

		if (debug.drawTileFrames) {
			mTagName = new Tag("name", tile.toString(), false);
			mPoiX = Tile.TILE_SIZE >> 1;
			mPoiY = 10;
			TileGenerator.renderTheme.matchNode(this, debugTagWay, (byte) 0);

			mIndices = debugBoxIndex;
			if (MapView.enableClosePolygons)
				mIndices[0] = 8;
			else
				mIndices[0] = 10;

			mCoords = debugBoxCoords;
			mDrawingLayer = 10 * renderLevels;
			TileGenerator.renderTheme.matchWay(this, debugTagBox, (byte) 0,
					false, true);
		}

		tile.layers = mLayers;
		tile.labels = mLabels;
		mLayers = null;
		mLabels = null;

		return true;
	}

	// Replace tags that should only be matched by key in RenderTheme
	// to avoid caching RenderInstructions for each way of the same type
	// only with different name.
	// Maybe this should be done within RenderTheme, also allowing
	// to set these replacement rules in theme file.
	private boolean filterTags(Tag[] tags) {
		mRenderBuildingModel = false;

		for (int i = 0; i < tags.length; i++) {
			String key = tags[i].key;
			if (tags[i].key == Tag.TAG_KEY_NAME) {
				if (tags[i].value != null) {
					mTagName = tags[i];
					tags[i] = mTagEmptyName;
				}
			} else if (tags[i].key == Tag.TAG_KEY_HOUSE_NUMBER) {
				if (tags[i].value != null) {
					mTagHouseNr = tags[i];
					tags[i] = mTagEmptyHouseNr;
				}
			} else if (mTile.zoomLevel >= 17 &&
			// FIXME, allow overlays to intercept
			// this, or use a theme option for this
					key == Tag.TAG_KEY_BUILDING) {
				mRenderBuildingModel = true;
			}
		}
		return true;
	}

	public IMapDatabase getMapDatabase() {
		return mMapDatabase;
	}

	@Override
	public void renderArea(Area area, int level) {
		int numLayer = mDrawingLayer + level;

		if (mRenderBuildingModel) {
			// Log.d(TAG, "add buildings: " + mTile + " " + mPriority);
			if (mLayers.extrusionLayers == null)
				mLayers.extrusionLayers = new ExtrusionLayer(0);

			((ExtrusionLayer) mLayers.extrusionLayers).addBuildings(mCoords,
					mIndices, mPriority);

			return;
		}

		if (!mDebugDrawPolygons)
			return;

		// if (!mProjected && !projectToTile())
		// return;

		PolygonLayer layer = (PolygonLayer) mLayers.getLayer(numLayer,
				Layer.POLYGON);
		if (layer == null)
			return;

		if (layer.area == null)
			layer.area = area;

		layer.addPolygon(mCoords, mIndices);
	}

	@Override
	public void renderAreaCaption(Text text) {
		// Log.d(TAG, "renderAreaCaption: " + mTagName);

		if (text.textKey == Tag.TAG_KEY_NAME) {
			if (mTagName == null)
				return;

			TextItem t = TextItem.get().set(mCoords[0], mCoords[1],
					mTagName.value, text);
			t.next = mLabels;
			mLabels = t;
		} else if (text.textKey == Tag.TAG_KEY_HOUSE_NUMBER) {
			if (mTagHouseNr == null)
				return;

			TextItem t = TextItem.get().set(mCoords[0], mCoords[1],
					mTagHouseNr.value, text);
			t.next = mLabels;
			mLabels = t;
		}
	}

	@Override
	public void renderAreaSymbol(Bitmap symbol) {
	}

	// ---------------- MapDatabaseCallback -----------------
	@Override
	public void renderPointOfInterest(byte layer, Tag[] tags, float latitude,
			float longitude) {

		// reset state
		mTagName = null;
		mTagHouseNr = null;

		mPoiX = longitude;
		mPoiY = latitude;

		// remove tags that should not be cached in Rendertheme
		filterTags(tags);

		TileGenerator.renderTheme.matchNode(this, tags, mTile.zoomLevel);
	}

	@Override
	public void renderPointOfInterestCaption(Text text) {
		// Log.d(TAG, "renderPointOfInterestCaption: " + mPoiX + " " + mPoiY +
		// " " + mTagName);

		if (mTagName == null)
			return;

		if (text.textKey == mTagEmptyName.key) {
			TextItem t = TextItem.get().set(mPoiX, mPoiY, mTagName.value, text);
			// TextItem t = new TextItem(mPoiX, mPoiY, mTagName.value, text);
			t.next = mLabels;
			mLabels = t;
		}
	}

	@Override
	public void renderPointOfInterestCircle(float radius, Paint fill, int level) {
	}

	@Override
	public void renderPointOfInterestSymbol(Bitmap bitmap) {
		// Log.d(TAG, "add symbol");

		// if (mLayers.textureLayers == null)
		// mLayers.textureLayers = new SymbolLayer();
		//
		// SymbolLayer sl = (SymbolLayer) mLayers.textureLayers;
		//
		// SymbolItem it = SymbolItem.get();
		// it.x = mPoiX;
		// it.y = mPoiY;
		// it.bitmap = bitmap;
		// it.billboard = true;
		//
		// sl.addSymbol(it);
	}

	@Override
	public void renderWaterBackground() {
	}

	@Override
	public void renderWay(byte layer, Tag[] tags, float[] coords,
			short[] indices, boolean closed, int prio) {

		// reset state
		mTagName = null;
		mTagHouseNr = null;
		mCurLineLayer = null;

		mPriority = prio;
		mClosed = closed;

		// replace tags that should not be cached in Rendertheme (e.g. name)
		if (!filterTags(tags))
			return;

		mDrawingLayer = getValidLayer(layer) * renderLevels;
		mCoords = coords;
		mIndices = indices;

		mRenderInstructions = TileGenerator.renderTheme.matchWay(this, tags,
				(byte) (mTile.zoomLevel + 0), closed, true);

		if (mRenderInstructions == null && mDebugDrawUnmatched)
			debugUnmatched(closed, tags);

		mCurLineLayer = null;
	}

	// ----------------- RenderThemeCallback -----------------
	@Override
	public void renderWay(Line line, int level) {
		// TODO projectToTile();

		int numLayer = (mDrawingLayer * 2) + level;

		if (line.stipple == 0) {
			if (line.outline && mCurLineLayer == null) {
				// FIXME in RenderTheme
				Log.e(TAG, "BUG in theme: line must come before outline!");
				return;
			}

			LineLayer lineLayer = (LineLayer) mLayers.getLayer(numLayer,
					Layer.LINE);

			if (lineLayer == null)
				return;

			if (lineLayer.line == null) {
				lineLayer.line = line;

				float w = line.width;
				if (!line.fixed) {
					w *= mStrokeScale;
					w *= mProjectionScaleFactor;
				}
				lineLayer.width = w;
			}

			if (line.outline) {
				lineLayer.addOutline(mCurLineLayer);
				return;
			}

			lineLayer.addLine(mCoords, mIndices, mClosed);
			mCurLineLayer = lineLayer;
		} else {
			LineTexLayer lineLayer = (LineTexLayer) mLayers.getLayer(numLayer,
					Layer.TEXLINE);

			if (lineLayer == null)
				return;

			if (lineLayer.line == null) {
				lineLayer.line = line;

				float w = line.width;
				if (!line.fixed) {
					w *= mStrokeScale;
					w *= mProjectionScaleFactor;
				}
				lineLayer.width = w;
			}

			lineLayer.addLine(mCoords, mIndices);
		}
	}

	@Override
	public void renderWaySymbol(Bitmap symbol, boolean alignCenter,
			boolean repeat) {

	}

	@Override
	public void renderWayText(Text text) {
		// Log.d(TAG, "renderWayText: " + mTagName);

		if (mTagName == null)
			return;

		if (text.textKey == mTagEmptyName.key && mTagName.value != null) {
			int offset = 0;
			for (int i = 0, n = mIndices.length; i < n; i++) {
				int length = mIndices[i];
				if (length < 4)
					break;
				mLabels = WayDecorator.renderText(mClipper, mCoords,
						mTagName.value, text, offset, length, mLabels);
				offset += length;
			}
		}
	}

	public void setMapDatabase(IMapDatabase mapDatabase) {
		if (mMapDatabase != null)
			mMapDatabase.close();

		mMapDatabase = mapDatabase;
		// mMapProjection = mMapDatabase.getMapProjection();
	}

	/**
	 * Sets the scale stroke factor for the given zoom level.
	 * 
	 * @param zoomLevel
	 *            the zoom level for which the scale stroke factor should be
	 *            set.
	 */
	private void setScaleStrokeWidth(byte zoomLevel) {
		mStrokeScale = (float) Math.pow(STROKE_INCREASE, zoomLevel
				- STROKE_MIN_ZOOM_LEVEL);
		if (mStrokeScale < 1)
			mStrokeScale = 1;
	}
}
