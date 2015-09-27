/*
 * Copyright 2012 
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
package org.oscim.renderer;

import org.oscim.utils.GlConfigChooser;
import org.oscim.view.LocationHandler;
import org.oscim.view.MapView;
import org.oscim.view.MapViewPosition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.opengl.GLSurfaceView;

import com.socarmap.R;

public class GLView extends GLSurfaceView {
	Paint paint;
	Paint paintStroke;
	MapView mMapView;
	private final GLRenderer mRenderer;

	protected final Paint mPaint = new Paint();
	protected final Paint mCirclePaint = new Paint();

	protected final Bitmap PERSON_ICON;
	protected final Bitmap DIRECTION_ARROW;

	private Location mLocation;
	private final long mLocationUpdateMinTime = 0;

	/** Coordinates the feet of the person are located. */
	protected final android.graphics.Point PERSON_HOTSPOT = new android.graphics.Point(
			24, 39);

	// Compass value
	protected final Picture mCompassFrame = new Picture();
	protected final Picture mCompassRose = new Picture();
	private final Matrix mCompassMatrix = new Matrix();

	// actual compass value. Note: this one is only changed when an actual
	// compass value
	// is being read, so a check >= 0 is valid

	private float mCompassCenterX = 35.0f;
	private float mCompassCenterY = 35.0f;
	private float mCompassRadius = 20.0f;

	private final float COMPASS_FRAME_CENTER_X;
	private final float COMPASS_FRAME_CENTER_Y;
	private final float COMPASS_ROSE_CENTER_X;
	private final float COMPASS_ROSE_CENTER_Y;

	private float mScale = 1.0f;

	public GLView(Context context, MapView mapView) {
		super(context);
		this.setWillNotDraw(false);
		mMapView = mapView;
		// Log.d(TAG, "init GLSurfaceLayer");
		setEGLConfigChooser(new GlConfigChooser());
		setEGLContextClientVersion(2);

		setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
		mRenderer = new GLRenderer(mMapView);
		setRenderer(mRenderer);

		if (!MapView.debugFrameTime)
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		paintStroke = new Paint();
		paintStroke.setColor(getResources().getColor(R.color.stroke_color));
		paintStroke.setStrokeWidth(2f);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setAntiAlias(true);

		paint = new Paint();
		paint.setStrokeWidth(10);
		paint.setColor(getResources().getColor(R.color.stroke_color));
		// paintStroke.setStrokeWidth(5.6f);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		mCirclePaint.setARGB(0, 100, 100, 255);
		mCirclePaint.setAntiAlias(true);

		PERSON_ICON = ((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.person)).getBitmap();
		DIRECTION_ARROW = ((BitmapDrawable) context.getResources().getDrawable(
				R.drawable.direction_arrow)).getBitmap();

		mScale = context.getResources().getDisplayMetrics().density;

		createCompassFramePicture();
		createCompassRosePicture();

		COMPASS_FRAME_CENTER_X = mCompassFrame.getWidth() / 2 - 0.5f;
		COMPASS_FRAME_CENTER_Y = mCompassFrame.getHeight() / 2 - 0.5f;
		COMPASS_ROSE_CENTER_X = mCompassRose.getWidth() / 2 - 0.5f;
		COMPASS_ROSE_CENTER_Y = mCompassRose.getHeight() / 2 - 0.5f;

	}

	private void createCompassRosePicture() {
		// Paint design of north triangle (it's common to paint north in red
		// color)
		final Paint northPaint = new Paint();
		northPaint.setColor(0xFFA00000);
		northPaint.setAntiAlias(true);
		northPaint.setStyle(Style.FILL);
		northPaint.setAlpha(220);

		// Paint design of south triangle (black)
		final Paint southPaint = new Paint();
		southPaint.setColor(Color.BLACK);
		southPaint.setAntiAlias(true);
		southPaint.setStyle(Style.FILL);
		southPaint.setAlpha(220);

		// Create a little white dot in the middle of the compass rose
		final Paint centerPaint = new Paint();
		centerPaint.setColor(Color.WHITE);
		centerPaint.setAntiAlias(true);
		centerPaint.setStyle(Style.FILL);
		centerPaint.setAlpha(220);

		// final int picBorderWidthAndHeight = (int) ((mCompassRadius + 5) * 2 *
		// mScale);
		final int picBorderWidthAndHeight = (int) ((mCompassRadius + 5) * 2);
		final int center = picBorderWidthAndHeight / 2;

		final Canvas canvas = mCompassRose.beginRecording(
				picBorderWidthAndHeight, picBorderWidthAndHeight);

		// Blue triangle pointing north
		final Path pathNorth = new Path();
		pathNorth.moveTo(center, center - (mCompassRadius - 3) * mScale);
		pathNorth.lineTo(center + 4 * mScale, center);
		pathNorth.lineTo(center - 4 * mScale, center);
		pathNorth.lineTo(center, center - (mCompassRadius - 3) * mScale);
		pathNorth.close();
		canvas.drawPath(pathNorth, northPaint);

		// Red triangle pointing south
		final Path pathSouth = new Path();
		pathSouth.moveTo(center, center + (mCompassRadius - 3) * mScale);
		pathSouth.lineTo(center + 4 * mScale, center);
		pathSouth.lineTo(center - 4 * mScale, center);
		pathSouth.lineTo(center, center + (mCompassRadius - 3) * mScale);
		pathSouth.close();
		canvas.drawPath(pathSouth, southPaint);

		// Draw a little white dot in the middle
		canvas.drawCircle(center, center, 2, centerPaint);

		mCompassRose.endRecording();
	}

	private Point calculatePointOnCircle(float centerX, float centerY,
			float radius, float degrees) {
		// for trigonometry, 0 is pointing east, so subtract 90
		// compass degrees are the wrong way round
		final double dblRadians = Math.toRadians(-degrees + 90);

		final int intX = (int) (radius * Math.cos(dblRadians));
		final int intY = (int) (radius * Math.sin(dblRadians));

		return new Point((int) centerX + intX, (int) centerY - intY);
	}

	private void drawTriangle(Canvas canvas, float x, float y, float radius,
			float degrees, Paint paint) {
		canvas.save();
		final Point point = this.calculatePointOnCircle(x, y, radius, degrees);
		canvas.rotate(degrees, point.x, point.y);
		final Path p = new Path();
		p.moveTo(point.x - 2 * mScale, point.y);
		p.lineTo(point.x + 2 * mScale, point.y);
		p.lineTo(point.x, point.y - 5 * mScale);
		p.close();
		canvas.drawPath(p, paint);
		canvas.restore();
	}

	private void createCompassFramePicture() {
		// The inside of the compass is white and transparent
		final Paint innerPaint = new Paint();
		innerPaint.setColor(Color.WHITE);
		innerPaint.setAntiAlias(true);
		innerPaint.setStyle(Style.FILL);
		innerPaint.setAlpha(200);

		// The outer part (circle and little triangles) is gray and transparent
		final Paint outerPaint = new Paint();
		outerPaint.setColor(Color.GRAY);
		outerPaint.setAntiAlias(true);
		outerPaint.setStyle(Style.STROKE);
		outerPaint.setStrokeWidth(2.0f);
		outerPaint.setAlpha(200);

		final int picBorderWidthAndHeight = (int) ((mCompassRadius + 5) * 2);
		final int center = picBorderWidthAndHeight / 2;

		final Canvas canvas = mCompassFrame.beginRecording(
				picBorderWidthAndHeight, picBorderWidthAndHeight);

		// draw compass inner circle and border
		canvas.drawCircle(center, center, mCompassRadius * mScale, innerPaint);
		canvas.drawCircle(center, center, mCompassRadius * mScale, outerPaint);

		// Draw little triangles north, south, west and east (don't move)
		// to make those move use "-bearing + 0" etc. (Note: that would mean to
		// draw the triangles in the onDraw() method)
		drawTriangle(canvas, center, center, mCompassRadius * mScale, 0,
				outerPaint);
		drawTriangle(canvas, center, center, mCompassRadius * mScale, 90,
				outerPaint);
		drawTriangle(canvas, center, center, mCompassRadius * mScale, 180,
				outerPaint);
		drawTriangle(canvas, center, center, mCompassRadius * mScale, 270,
				outerPaint);

		mCompassFrame.endRecording();
	}

	public boolean isMyLocationEnabled() {
		LocationHandler hnd = mMapView.getLocationHandler();
		boolean mMyLocationEnabled = hnd != null
				&& (hnd.isShowMyLocationEnabled() || hnd
						.isSnapToLocationEnabled())
				&& hnd.getAccuracy() != null;
		return mMyLocationEnabled;
	}

	public boolean isCompassEnabled() {
		return mMapView.getCompassEnabled();
	}

	public boolean isLocationFollowEnabled() {
		return isMyLocationEnabled()
				&& mMapView.getLocationHandler().isSnapToLocationEnabled();
	}

	public long getLocationUpdateMinTime() {
		return mLocationUpdateMinTime;
	}

	public void setCompassCenter(float x, float y) {
		mCompassCenterX = x;
		mCompassCenterY = y;
	}

	@Override
	protected void onDraw(Canvas c) {
		// TODO Auto-generated method stub
		super.onDraw(c);
		float cx = getWidth() / 2.0f;
		float cy = getHeight() / 2.0f;
		LocationHandler hnd = mMapView.getLocationHandler();
		if (hnd != null
				&& (hnd.isShowMyLocationEnabled() || hnd
						.isSnapToLocationEnabled())
				&& hnd.getAccuracy() != null) {
			MapViewPosition mapViewPosition = mMapView.getMapViewPosition();
			float accuracy = mapViewPosition.metersToEquatorPixels(hnd
					.getAccuracy());
			float radius = accuracy * 3;
			if (radius < 10)
				radius = 10;

			paintStroke.setStyle(Paint.Style.STROKE);
			RadialGradient gradient = new RadialGradient(cx, cy, radius,
					getResources().getColor(R.color.start_color),
					getResources().getColor(R.color.end_color),
					android.graphics.Shader.TileMode.CLAMP);
			paint.setAlpha(170);
			paint.setShader(gradient);
			c.drawCircle(cx, cy, radius, paint);
			c.drawCircle(cx, cy, radius, paintStroke);
			Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
			if (hnd.getLocation() != null) {

				mLocation = hnd.getLocation();
				c.drawText("Lat: " + mLocation.getLatitude(), cx, cy + 5, p);
				c.drawText("Lon: " + mLocation.getLongitude(), cx, cy + 20, p);
				c.drawText("Alt: " + mLocation.getAltitude(), cx, cy + 35, p);
				c.drawText("Acc: " + mLocation.getAccuracy(), cx, cy + 50, p);
			} else
				c.drawText("No Location", cx, cy + 5, p);
		}

		Float mAzimuth = mMapView.getCompassAngle();
		if (mMapView.getCompassEnabled() && mAzimuth != null
				&& mAzimuth.floatValue() >= 0.0f) {
			final float centerX = mCompassCenterX * mScale;
			final float centerY = mCompassCenterY * mScale
					+ (c.getHeight() - mMapView.getHeight());

			this.mCompassMatrix.setTranslate(-COMPASS_FRAME_CENTER_X,
					-COMPASS_FRAME_CENTER_Y);
			this.mCompassMatrix.postTranslate(centerX, centerY);

			c.save();
			c.setMatrix(mCompassMatrix);
			c.drawPicture(mCompassFrame);

			this.mCompassMatrix.setRotate(-mAzimuth, COMPASS_ROSE_CENTER_X,
					COMPASS_ROSE_CENTER_Y);
			this.mCompassMatrix.postTranslate(-COMPASS_ROSE_CENTER_X,
					-COMPASS_ROSE_CENTER_Y);
			this.mCompassMatrix.postTranslate(centerX, centerY);

			c.setMatrix(mCompassMatrix);
			c.drawPicture(mCompassRose);
			c.restore();
			Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
			c.drawText("Angle: " + mAzimuth, cx, cy + 65, p);

		}

		// if (isMyLocationEnabled() && hnd.getLocation() != null) {
		//
		// MapViewPosition mapViewPosition = mMapView.getMapViewPosition();
		// mLocation = hnd.getLocation();
		// mapViewPosition.toMapPixels(new GeoPoint(mLocation.getLatitude(),
		// mLocation.getLongitude()), mMapCoords);
		// final float radius = mapViewPosition
		// .metersToEquatorPixels(this.mLocation.getAccuracy());
		//
		// this.mCirclePaint.setAlpha(50);
		// this.mCirclePaint.setStyle(Style.FILL);
		// c.drawCircle(mMapCoords.x, mMapCoords.y, radius, this.mCirclePaint);
		//
		// this.mCirclePaint.setAlpha(150);
		// this.mCirclePaint.setStyle(Style.STROKE);
		// c.drawCircle(mMapCoords.x, mMapCoords.y, radius, this.mCirclePaint);
		//
		// float[] mtx = new float[9];
		// c.getMatrix().getValues(mtx);
		// if (true) {
		// float tx = (-mtx[Matrix.MTRANS_X] + 20) / mtx[Matrix.MSCALE_X];
		// float ty = (-mtx[Matrix.MTRANS_Y] + 90) / mtx[Matrix.MSCALE_Y];
		// c.drawText("Lat: " + mLocation.getLatitude(), tx, ty + 5,
		// this.mPaint);
		// c.drawText("Lon: " + mLocation.getLongitude(), tx, ty + 20,
		// this.mPaint);
		// c.drawText("Alt: " + mLocation.getAltitude(), tx, ty + 35,
		// this.mPaint);
		// c.drawText("Acc: " + mLocation.getAccuracy(), tx, ty + 50,
		// this.mPaint);
		// }
		// float bearing = -1.0f;
		// if (mLocation.getProvider().equals(LocationManager.GPS_PROVIDER)
		// && mAzimuth != null && mAzimuth >= 0.0f) {
		// // if GPS and compass is available, use compass value
		// bearing = mAzimuth;
		// } else if (mLocation.hasSpeed() && mLocation.getSpeed() > 1
		// && mLocation.hasBearing()) {
		// // use bearing if available and if we're actually moving
		// // XXX do we really need to test for speed > 1, or maybe better
		// // some number other than 1
		// bearing = this.mLocation.getBearing();
		// }
		//
		// if (bearing >= 0.0f) {
		// /*
		// * Rotate the direction-Arrow according to the bearing we are
		// * driving. And draw it to the canvas.
		// */
		// this.directionRotater.setRotate(bearing,
		// DIRECTION_ARROW_CENTER_X, DIRECTION_ARROW_CENTER_Y);
		// this.directionRotater.postTranslate(-DIRECTION_ARROW_CENTER_X,
		// -DIRECTION_ARROW_CENTER_Y);
		// this.directionRotater.postScale(1 / mtx[Matrix.MSCALE_X],
		// 1 / mtx[Matrix.MSCALE_Y]);
		// this.directionRotater.postTranslate(mMapCoords.x, mMapCoords.y);
		// c.drawBitmap(DIRECTION_ARROW, this.directionRotater,
		// this.mPaint);
		// } else {
		// this.directionRotater.setTranslate(-PERSON_HOTSPOT.x,
		// -PERSON_HOTSPOT.y);
		// this.directionRotater.postScale(1 / mtx[Matrix.MSCALE_X],
		// 1 / mtx[Matrix.MSCALE_Y]);
		// this.directionRotater.postTranslate(mMapCoords.x, mMapCoords.y);
		// c.drawBitmap(PERSON_ICON, this.directionRotater, this.mPaint);
		// }
		//
		// }
	}

}
