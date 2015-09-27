package com.docflowdroid.comp;

import android.content.Context;
import android.text.InputType;

public class IntegerItem extends TextItem {

	public IntegerItem(Context context) {
		super(context);
		text.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_VARIATION_NORMAL);
	}

}
