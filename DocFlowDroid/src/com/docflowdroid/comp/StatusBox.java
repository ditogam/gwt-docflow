package com.docflowdroid.comp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class StatusBox extends View {
	private int color = Color.BLACK;

	public StatusBox(Context context) {
		super(context);
	}

	public StatusBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StatusBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

	}

	public void setColor(int color) {
		this.color = color;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float w, h, cx, cy, radius;

		w = getWidth();
		h = getHeight();
		cx = w / 2;
		cy = h / 2;

		if (w > h) {
			radius = h / 4;
		} else {
			radius = w / 4;
		}

		Paint MyPaint = new Paint();
		MyPaint.setStyle(Paint.Style.FILL);
		MyPaint.setAntiAlias(true);
		Shader radialGradientShader;

		float shaderCx = 0;
		float shaderCy = 0;
		float shaderRadius = getWidth();
		int shaderColor0 = color;
		int shaderColor1 = darker(shaderColor0);

		radialGradientShader = new RadialGradient(shaderCx, shaderCy,
				shaderRadius, shaderColor0, shaderColor1,
				Shader.TileMode.MIRROR);

		MyPaint.setShader(radialGradientShader);
		MyPaint.setStrokeWidth(10);

		shaderCx = cx;
		shaderCy = cy;
		shaderRadius = radius;
		shaderColor0 = color;
		shaderColor1 = darker(darker(darker(shaderColor0)));

		radialGradientShader = new RadialGradient(shaderCx, shaderCy,
				shaderRadius, shaderColor0, shaderColor1, Shader.TileMode.CLAMP);

		MyPaint.setShader(radialGradientShader);
		canvas.drawCircle(cx, cy, radius, MyPaint);
	}

	public static int darker(int c) {
		int r = Color.red(c);
		int b = Color.blue(c);
		int g = Color.green(c);
		return Color.rgb((int) (r * .7), (int) (g * .7), (int) (b * .7));
	}
}
