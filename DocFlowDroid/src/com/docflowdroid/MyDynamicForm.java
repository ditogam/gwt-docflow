package com.docflowdroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.docflowdroid.comp.FormItem;
import com.docflowdroid.comp.TitleOrientation;
import com.docflowdroid.helper.Utils;

@SuppressLint("NewApi")
public class MyDynamicForm extends LinearLayout {
	private boolean newLine;

	public MyDynamicForm(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		setWillNotDraw(false);
		setPadding(10, 5, 5, 5);
	}

	public MyDynamicForm(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isNewLine() {
		return newLine;
	}

	public void setNewLine(boolean newLine) {
		this.newLine = newLine;
	}

	private int numCols = 2;

	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	TitleOrientation orientation = TitleOrientation.TOP;

	public void setTitleOrientation(TitleOrientation orientation) {
		this.orientation = orientation;

	}

	public void setHeight(String groupHeight) {
		if (getLayoutParams() != null)
			getLayoutParams().height = getLongValue(groupHeight);
	}

	public void setWidth(String groupWidth) {
		if (getLayoutParams() != null)
			getLayoutParams().width = getLongValue(groupWidth);
	}

	public void setFields(FormItem[] array) {
		LinearLayout gridView = new LinearLayout(getContext());
		gridView.setOrientation(LinearLayout.VERTICAL);
		gridView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		boolean newRow = true;
		int cnt = 0;
		LinearLayout container = null;
		for (FormItem formItem : array) {
			if (newRow) {
				container = new LinearLayout(getContext());
				container.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				container.setOrientation(LinearLayout.HORIZONTAL);
				gridView.addView(container);
				newRow = false;
			}
			formItem.createView(orientation);
			container.addView(formItem);
			cnt++;
			if (cnt >= numCols) {
				newRow = true;
				cnt = 0;

			}
		}
		if (group) {
			// gridView.setBackground(getResources().getDrawable(R.drawable.border));
		}
		addView(gridView);
	}

	public static int getLongValue(String val) {
		val = val == null ? "" : val.trim();
		if (val.equals("100%"))
			return LinearLayout.LayoutParams.MATCH_PARENT;
		if (val.endsWith("%"))
			return LinearLayout.LayoutParams.WRAP_CONTENT;
		try {
			return Utils.getWidth(Integer.parseInt(val));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return LinearLayout.LayoutParams.WRAP_CONTENT;
	}

	boolean group = false;
	String caption;

	public void setIsGroup(boolean group) {
		this.group = group;
	}

	public void setGroupTitle(String caption) {
		this.caption = caption;
		TextView tv = new TextView(getContext());
		tv.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		String s = "<u><i><b>" + caption + "</b></i></u>";
		tv.setText(Html.fromHtml(s));

		addView(tv);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		group = false;
		if (!group)
			return;
		Paint strokePaint = new Paint();
		strokePaint.setARGB(255, 255, 0, 0);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(2);
		Rect r = canvas.getClipBounds();
		Rect outline = new Rect(1, 1, r.right - 1, r.bottom - 1);
		canvas.drawRect(outline, strokePaint);
		// canvas.c;
		// paint.setStyle(Style.FILL);
		// paint.setColor(android.graphics.Color.WHITE);
		// paint.setTextSize(15);
		// canvas.drawText(caption, 3, 15, paint);
	}

}
