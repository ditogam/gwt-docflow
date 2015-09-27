package com.docflowdroid.comp;

import android.content.Context;
import android.text.InputType;

public class FloatItem extends TextItem {
	public FloatItem(Context context) {
		super(context);
		text.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
	}
}
