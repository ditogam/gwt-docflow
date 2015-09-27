package com.socarmap.helper;

import android.widget.Button;
import android.widget.TextView;

public class ButtonRowViewHolder {
	private Button btnDelete;
	private TextView textView;

	public ButtonRowViewHolder() {
	}

	public ButtonRowViewHolder(TextView textView, Button btnDelete) {
		this.btnDelete = btnDelete;
		this.textView = textView;
	}

	public Button getBtnDelete() {
		return btnDelete;
	}

	public TextView getTextView() {
		return textView;
	}
}
