package com.docflowdroid.common.comp;

import java.util.HashMap;

import android.content.Context;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;

public interface IFormItem {
	public void setValue(Object value);

	public String getValue();

	public String getDisplayValue();

	public void setDisplayValue(String value);

	public void setCanEdit(boolean b);

	public void setTitle(String title);

	public void setWidth(String width);

	public void setMinimumHeight(String fieldHeight);

	public void setDisabled(boolean b);

	public void setValueMap(HashMap<Long, String> map);

	public void setOnItemSelectedListener(OnItemSelectedListener listener);

	public Context getContext();

	public void requestFocusSelf();

	public ImageButton[] getIcons();

	public void setIcons(ImageButton... icons);

}
