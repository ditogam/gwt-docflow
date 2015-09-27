/*
 * Copyright 2012, 2013 Hannes Janetzek
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static org.oscim.generator.JobTile.STATE_NEW_DATA;
import static org.oscim.generator.JobTile.STATE_READY;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.renderer.layer.Layers;
import org.oscim.renderer.overlays.RenderOverlay;
import org.oscim.theme.RenderTheme;
import org.oscim.utils.FastMath;
import org.oscim.utils.GlUtils;
import org.oscim.view.MapView;
import org.oscim.view.MapViewPosition;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author Hannes Janetzek
 */
public class GLRenderer implements GLSurfaceView.Renderer {

	public class Matrices {
		public final float[] viewproj = new float[16];
		public final float[] proj = new float[16];
		public final float[] view = new float[16];

		// for temporary use by callee
		public final float[] mvp = new float[16];
	}

	private static final String TAG = GLRenderer.class.getName();
	private static final int MB = 1024 * 1024;
	private static final int SHORT_BYTES = 2;
	private static final int CACHE_TILES_MAX = 250;

	private static final int LIMIT_BUFFERS = 16 * MB;

	public static final float COORD_SCALE = 8.0f;

	static int CACHE_TILES = CACHE_TILES_MAX;
	private static MapView mMapView;

	static int mWidth, mHeight;
	private static MapViewPosition mMapViewPosition;

	private static MapPosition mMapPosition;
	private static ShortBuffer shortBuffer;

	private static short[] mFillCoords;

	// bytes currently loaded in VBOs
	private static int mBufferMemoryUsage;
	private static float[] mTileCoords = new float[8];

	private static float[] mDebugCoords = new float[8];

	private static Matrices mMatrices;

	// private
	static float[] mClearColor = null;
	private static boolean mUpdateColor = false;

	// drawlock to synchronize Main- and GL-Thread
	// static ReentrantLock tilelock = new ReentrantLock();
	static ReentrantLock drawlock = new ReentrantLock();

	// Add additional tiles that serve as placeholer when flipping
	// over date-line.
	// I dont really like this but cannot think of a better solution:
	// the other option would be to run scanbox each time for upload,
	// drawing, proxies and text layer. needing to add placeholder only
	// happens rarely, unless you live on Fidschi

	/* package */static int mHolderCount;
	/* package */static TileSet mDrawTiles;

	// scanline fill class used to check tile visibility
	private static ScanBox mScanBox = new ScanBox() {
		@Override
		void setVisible(int y, int x1, int x2) {
			int cnt = mDrawTiles.cnt;

			MapTile[] tiles = mDrawTiles.tiles;

			for (int i = 0; i < cnt; i++) {
				MapTile t = tiles[i];
				if (t.tileY == y && t.tileX >= x1 && t.tileX < x2)
					t.isVisible = true;
			}

			int xmax = 1 << mZoom;
			if (x1 >= 0 && x2 < xmax)
				return;

			// add placeholder tiles to show both sides
			// of date line. a little too complicated...
			for (int x = x1; x < x2; x++) {
				MapTile holder = null;
				MapTile tile = null;
				boolean found = false;

				if (x >= 0 && x < xmax)
					continue;

				int xx = x;
				if (x < 0)
					xx = xmax + x;
				else
					xx = x - xmax;

				if (xx < 0 || xx >= xmax)
					continue;

				for (int i = cnt; i < cnt + mHolderCount; i++)
					if (tiles[i].tileX == x && tiles[i].tileY == y) {
						found = true;
						break;
					}

				if (found)
					continue;

				for (int i = 0; i < cnt; i++)
					if (tiles[i].tileX == xx && tiles[i].tileY == y) {
						tile = tiles[i];
						break;
					}

				if (tile == null)
					continue;

				holder = new MapTile(x, y, mZoom);
				holder.isVisible = true;
				holder.holder = tile;
				tile.isVisible = true;
				tiles[cnt + mHolderCount++] = holder;
			}
		}
	};

	private static int uploadCnt = 0;

	private static Object tilelock = new Object();

	public static final boolean debugView = false;

	private static void checkBufferUsage(boolean force) {
		// try to clear some unused vbo when exceding limit

		if (!force && mBufferMemoryUsage < LIMIT_BUFFERS) {
			if (CACHE_TILES < CACHE_TILES_MAX)
				CACHE_TILES += 50;
			return;
		}

		Log.d(TAG, "buffer object usage: " + mBufferMemoryUsage / MB + "MB");

		mBufferMemoryUsage -= BufferObject.limitUsage(2 * MB);

		Log.d(TAG, "now: " + mBufferMemoryUsage / MB + "MB");

		if (mBufferMemoryUsage > LIMIT_BUFFERS && CACHE_TILES > 100)
			CACHE_TILES -= 50;
	}

	public static int depthOffset(MapTile t) {
		return ((t.tileX % 4) + (t.tileY % 4 * 4) + 1);
	}

	private static void draw() {
		long start = 0;

		if (MapView.debugFrameTime)
			start = SystemClock.uptimeMillis();

		if (mUpdateColor) {
			float cc[] = mClearColor;
			GLES20.glClearColor(cc[0], cc[1], cc[2], cc[3]);
			// GLES20.glClearColor(0.8f, 0.8f, 0.8f, 1);
			mUpdateColor = false;
		}

		// Note: it seems faster to also clear the stencil buffer even
		// when not needed. probaly otherwise it is masked out from the
		// depth buffer as they share the same memory region afaik
		// or for a better reason see OpenGL Insights chapter 23.
		GLES20.glDepthMask(true);
		GLES20.glStencilMask(0xFF);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT
				| GLES20.GL_STENCIL_BUFFER_BIT);

		int serial = 0;
		if (mDrawTiles != null)
			serial = mDrawTiles.serial;

		// get current tiles to draw
		mDrawTiles = mMapView.getTileManager().getActiveTiles(mDrawTiles);

		// FIXME what if only drawing overlays?
		if (mDrawTiles == null || mDrawTiles.cnt == 0) {
			return;
		}

		boolean tilesChanged = false;
		// check if the tiles have changed...
		if (serial != mDrawTiles.serial) {
			mMapPosition.zoomLevel = -1;
			tilesChanged = true;
		}

		// get current MapPosition, set mTileCoords (mapping of screen to model
		// coordinates)
		MapPosition pos = mMapPosition;
		boolean positionChanged;

		synchronized (mMapViewPosition) {
			mMapViewPosition.updateAnimation();

			positionChanged = mMapViewPosition.getMapPosition(pos);

			if (positionChanged)
				mMapViewPosition.getMapViewProjection(mTileCoords);

			mMapViewPosition
					.getMatrix(mMatrices.view, null, mMatrices.viewproj);

		}

		int tileCnt = mDrawTiles.cnt;
		MapTile[] tiles = mDrawTiles.tiles;

		if (positionChanged)
			updateTileVisibility();

		tileCnt += mHolderCount;

		/* compile layer data and upload to VBOs */
		uploadCnt = 0;
		for (int i = 0; i < tileCnt; i++) {
			MapTile tile = tiles[i];

			if (!tile.isVisible)
				continue;

			if (tile.state == STATE_NEW_DATA) {
				uploadTileData(tile);
				continue;
			}

			if (tile.holder != null) {
				// load tile that is referenced by this holder
				if (tile.holder.state == STATE_NEW_DATA)
					uploadTileData(tile.holder);

				tile.state = tile.holder.state;

			} else if (tile.state != STATE_READY) {
				// check near relatives than can serve as proxy
				if ((tile.proxies & MapTile.PROXY_PARENT) != 0) {
					MapTile rel = tile.rel.parent.tile;
					if (rel.state == STATE_NEW_DATA)
						uploadTileData(rel);

					continue;
				}
				for (int c = 0; c < 4; c++) {
					if ((tile.proxies & 1 << c) == 0)
						continue;

					MapTile rel = tile.rel.child[c].tile;
					if (rel != null && rel.state == STATE_NEW_DATA)
						uploadTileData(rel);
				}
			}
		}

		if (uploadCnt > 0)
			checkBufferUsage(false);

		tilesChanged |= (uploadCnt > 0);

		/* update overlays */
		List<RenderOverlay> overlays = mMapView.getOverlayManager()
				.getRenderLayers();

		for (int i = 0, n = overlays.size(); i < n; i++)
			overlays.get(i).update(mMapPosition, positionChanged, tilesChanged);

		/* draw base layer */
		BaseMap.draw(tiles, tileCnt, pos, mMatrices);

		/* draw overlays */
		for (int i = 0, n = overlays.size(); i < n; i++) {
			RenderOverlay renderOverlay = overlays.get(i);

			if (renderOverlay.newData) {
				renderOverlay.compile();
				renderOverlay.newData = false;
			}
			if (renderOverlay.isReady)
				renderOverlay.render(mMapPosition, mMatrices);
		}

		if (MapView.debugFrameTime) {
			GLES20.glFinish();
			Log.d(TAG, "draw took " + (SystemClock.uptimeMillis() - start));
		}

		if (debugView) {
			GLState.test(false, false);

			float mm = 0.5f;
			float min = -mm;
			float max = mm;
			float ymax = mm * mHeight / mWidth;
			mDebugCoords[0] = min;
			mDebugCoords[1] = ymax;
			mDebugCoords[2] = max;
			mDebugCoords[3] = ymax;
			mDebugCoords[4] = min;
			mDebugCoords[5] = -ymax;
			mDebugCoords[6] = max;
			mDebugCoords[7] = -ymax;

			PolygonRenderer.debugDraw(mMatrices.proj, mDebugCoords, 0);

			pos.zoomLevel = -1;
			mMapViewPosition.getMapViewProjection(mDebugCoords);

			PolygonRenderer.debugDraw(mMatrices.viewproj, mDebugCoords, 1);

		}

		if (GlUtils.checkGlOutOfMemory("finish")) {
			checkBufferUsage(true);
			// TODO also throw out some textures etc
		}
	}

	// get a TileSet of currently visible tiles
	public static TileSet getVisibleTiles(TileSet td) {
		if (mDrawTiles == null)
			return td;

		// ensure tiles keep visible state
		synchronized (GLRenderer.tilelock) {
			MapTile[] newTiles = mDrawTiles.tiles;
			int cnt = mDrawTiles.cnt;

			if (td == null)
				td = new TileSet(newTiles.length);

			// unlock previous tiles
			for (int i = 0; i < td.cnt; i++)
				td.tiles[i].unlock();

			// lock tiles to not be removed from cache
			td.cnt = 0;
			for (int i = 0; i < cnt; i++) {
				MapTile t = newTiles[i];
				if (t.isVisible && t.state == STATE_READY) {
					t.lock();
					td.tiles[td.cnt++] = t;
				}
			}
		}
		return td;
	}

	public static void releaseTiles(TileSet td) {
		for (int i = 0; i < td.cnt; i++) {
			td.tiles[i].unlock();
			td.tiles[i] = null;
		}
		td.cnt = 0;
	}

	public static void setRenderTheme(RenderTheme t) {
		mClearColor = GlUtils.colorToFloat(t.getMapBackground());
		mUpdateColor = true;
	}

	private static void updateTileVisibility() {
		float[] coords = mTileCoords;
		MapPosition pos = mMapPosition;
		MapTile[] tiles = mDrawTiles.tiles;
		// lock tiles while updating isVisible state
		synchronized (GLRenderer.tilelock) {

			for (int i = 0; i < mDrawTiles.cnt; i++)
				tiles[i].isVisible = false;

			// relative zoom-level, 'tiles' could not have been updated after
			// zoom-level changed.
			byte z = tiles[0].zoomLevel;
			float div = FastMath.pow(z - pos.zoomLevel);

			// transform screen coordinates to tile coordinates
			float scale = pos.scale / div;
			float px = (float) pos.x * div;
			float py = (float) pos.y * div;

			for (int i = 0; i < 8; i += 2) {
				coords[i + 0] = (px + coords[i + 0] / scale) / Tile.TILE_SIZE;
				coords[i + 1] = (py + coords[i + 1] / scale) / Tile.TILE_SIZE;
			}

			// count placeholder tiles
			mHolderCount = 0;

			// check visibile tiles
			mScanBox.scan(coords, z);
		}
	}

	public static boolean uploadLayers(Layers layers, int newSize,
			boolean addFill) {

		GLES20.glBindBuffer(GL_ARRAY_BUFFER, layers.vbo.id);

		// add fill coordinates
		if (addFill)
			newSize += 8;

		ShortBuffer sbuf = shortBuffer;

		if (sbuf.capacity() < newSize) {
			shortBuffer = sbuf = ByteBuffer
					.allocateDirect(newSize * SHORT_BYTES)
					.order(ByteOrder.nativeOrder()).asShortBuffer();
		} else {
			sbuf.clear();
			// if (addFill)
			// sbuf.position(8);
		}

		if (addFill)
			sbuf.put(mFillCoords, 0, 8);

		layers.compile(sbuf, addFill);
		sbuf.flip();

		if (newSize != sbuf.remaining()) {
			Log.d(TAG, "wrong size: " + " new size: " + newSize
					+ " buffer pos: " + sbuf.position() + " buffer limit: "
					+ sbuf.limit() + " buffer fill: " + sbuf.remaining());
			return false;
		}
		newSize *= SHORT_BYTES;

		// reuse memory allocated for vbo when possible and allocated
		// memory is less then four times the new data
		if (layers.vbo.size > newSize && layers.vbo.size < newSize * 4
				&& mBufferMemoryUsage < LIMIT_BUFFERS) {
			GLES20.glBufferSubData(GL_ARRAY_BUFFER, 0, newSize, sbuf);
		} else {
			mBufferMemoryUsage += newSize - layers.vbo.size;
			layers.vbo.size = newSize;
			GLES20.glBufferData(GL_ARRAY_BUFFER, layers.vbo.size, sbuf,
					GL_DYNAMIC_DRAW);
		}

		return true;
	}

	private static void uploadTileData(MapTile tile) {
		tile.state = STATE_READY;

		if (tile.layers == null)
			return;

		int newSize = tile.layers.getSize();
		if (newSize > 0) {

			if (tile.layers.vbo == null)
				tile.layers.vbo = BufferObject.get(newSize);

			if (!uploadLayers(tile.layers, newSize, true)) {
				Log.d(TAG, "BUG uploadTileData " + tile + " failed!");

				BufferObject.release(tile.layers.vbo);
				tile.layers.vbo = null;
				tile.layers.clear();
				tile.layers = null;
			}
		}
	}

	private long lastDraw = 0;

	private boolean mNewSurface;

	/**
	 * @param mapView
	 *            the MapView
	 */
	public GLRenderer(MapView mapView) {

		mMapView = mapView;
		mMapViewPosition = mapView.getMapViewPosition();
		mMapPosition = new MapPosition();

		mMatrices = new Matrices();

		short min = 0;
		short max = (short) ((Tile.TILE_SIZE * COORD_SCALE));
		mFillCoords = new short[8];
		mFillCoords[0] = min;
		mFillCoords[1] = max;
		mFillCoords[2] = max;
		mFillCoords[3] = max;
		mFillCoords[4] = min;
		mFillCoords[5] = min;
		mFillCoords[6] = max;
		mFillCoords[7] = min;

		ByteBuffer bbuf = ByteBuffer.allocateDirect(MB >> 2).order(
				ByteOrder.nativeOrder());

		shortBuffer = bbuf.asShortBuffer();
		shortBuffer.put(mFillCoords, 0, 8);
	}

	void clearBuffer() {
		mNewSurface = true;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		long start = SystemClock.uptimeMillis();
		long wait = 30 - (start - lastDraw);
		if (wait > 5) {
			// Log.d(TAG, "wait " + wait);
			SystemClock.sleep(wait);
			lastDraw = start + wait;
		} else
			lastDraw = start;

		// prevent main thread recreating all tiles (updateMap)
		// while rendering is going on.
		drawlock.lock();
		try {
			draw();
		} finally {
			drawlock.unlock();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		Log.d(TAG, "SurfaceChanged:" + mNewSurface + " " + width + "x" + height);

		if (width <= 0 || height <= 0)
			return;

		mWidth = width;
		mHeight = height;

		mMapViewPosition.getMatrix(null, mMatrices.proj, null);

		if (debugView) {
			// modify this to scale only the view, to see better which tiles are
			// rendered
			Matrix.setIdentityM(mMatrices.mvp, 0);
			Matrix.scaleM(mMatrices.mvp, 0, 0.5f, 0.5f, 1);
			Matrix.multiplyMM(mMatrices.proj, 0, mMatrices.mvp, 0,
					mMatrices.proj, 0);
		}

		BaseMap.setProjection(mMatrices.proj);

		GLES20.glViewport(0, 0, width, height);
		GLES20.glScissor(0, 0, width, height);
		GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

		GLES20.glClearStencil(0x00);

		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		if (!mNewSurface) {
			mMapView.redrawMap(false);
			return;
		}

		mNewSurface = false;
		mBufferMemoryUsage = 0;
		mDrawTiles = null;

		int numTiles = (mWidth / (Tile.TILE_SIZE / 2) + 2)
				* (mHeight / (Tile.TILE_SIZE / 2) + 2);

		// Set up vertex buffer objects
		int numVBO = (CACHE_TILES + (numTiles * 2));
		BufferObject.init(numVBO);

		if (mClearColor != null)
			mUpdateColor = true;

		GLState.init();

		mMapView.redrawMap(true);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// String ext = GLES20.glGetString(GLES20.GL_EXTENSIONS);
		// Log.d(TAG, "Extensions: " + ext);

		// classes that require GL context for initialization
		LineRenderer.init();
		LineTexRenderer.init();
		PolygonRenderer.init();
		TextureRenderer.init();
		TextureObject.init(10);

		mNewSurface = true;
	}
}
