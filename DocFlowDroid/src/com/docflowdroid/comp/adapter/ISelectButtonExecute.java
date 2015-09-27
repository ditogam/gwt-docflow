package com.docflowdroid.comp.adapter;

import android.view.View;

public interface ISelectButtonExecute {
	public String getButtonTitle();

	public void executeItem(Object item, View view);
}
