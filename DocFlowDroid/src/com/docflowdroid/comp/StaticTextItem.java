package com.docflowdroid.comp;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StaticTextItem extends FormItem {

	protected TextView valueItem;

	public StaticTextItem(Context context) {
		super(context);
		createCompSelf(context);
	}

	private void createCompSelf(Context context) {
		valueItem = new TextView(context);
		ViewGroup.LayoutParams params = valueItem.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
		params.width = 200;
		valueItem.setLayoutParams(params);
	}

	public StaticTextItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createCompSelf(context);
	}

	public StaticTextItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		createCompSelf(context);
	}

	@Override
	public void setValue(final Object value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			valueItem.setText(value == null ? "" : Html.fromHtml(value
					.toString().trim()));
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValue(value);
				}
			});
		}

	}

	@Override
	public String getValue() {
		return valueItem.getText().toString();
	}

	@Override
	public View getComponent() {
		// TODO Auto-generated method stub
		return valueItem;
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		// TODO Auto-generated method stub
		return valueItem.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		valueItem.setOnFocusChangeListener(newList);

	}
}
