package com.docflowdroid.comp;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.widget.LinearLayout;

public class TextAreaItem extends TextItem {

	public TextAreaItem(Context context) {
		super(context);
		getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
		getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
		setLayoutParams(getLayoutParams());
		text.setSingleLine(false);
		text.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		text.setHorizontallyScrolling(false);
		text.setVerticalScrollBarEnabled(true);
		text.setGravity(Gravity.CENTER);
		text.setLines(10);
		text.setGravity(Gravity.TOP);
		text.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

	}
}
