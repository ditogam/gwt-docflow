package com.socarmap.helper;

import android.widget.CheckBox;
import android.widget.TextView;

public class SelectViewHolder {
	private CheckBox checkBox;
	private TextView textView;

	public SelectViewHolder() {
	}

	public SelectViewHolder(TextView textView, CheckBox checkBox) {
		this.checkBox = checkBox;
		this.textView = textView;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public TextView getTextView() {
		return textView;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}
}
