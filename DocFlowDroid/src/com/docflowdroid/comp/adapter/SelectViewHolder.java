package com.docflowdroid.comp.adapter;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectViewHolder {
	private CheckBox checkBox;
	private TextView textView;
	private Button buttonView;

	public SelectViewHolder() {
	}

	public SelectViewHolder(TextView textView, CheckBox checkBox,
			Button buttonView) {
		this.checkBox = checkBox;
		this.textView = textView;
		this.buttonView = buttonView;
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

	public Button getButtonView() {
		return buttonView;
	}

	public void setButtonView(Button buttonView) {
		this.buttonView = buttonView;
	}
}
