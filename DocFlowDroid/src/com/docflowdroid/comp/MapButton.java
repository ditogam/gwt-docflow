package com.docflowdroid.comp;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.docflowdroid.DocFlow;
import com.docflowdroid.common.BooleanCallback;
import com.docflowdroid.common.FieldDefinitionItem;
import com.docflowdroid.common.SC;
import com.docflowdroid.common.comp.IFormItem;
import com.docflowdroid.helper.DrowableDownloader;

@SuppressLint("DefaultLocale")
public class MapButton extends FormItem {

	private ImageButton buttonItem;
	private TextView tvTitle;
	private String value;
	private Drawable drawable;

	private LinearLayout component;

	@SuppressLint("DefaultLocale")
	public MapButton(Context context) {
		super(context);
		buttonItem = new ImageButton(context);
		tvTitle = new TextView(context);
		LayoutParams paramtext = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		component = new LinearLayout(context);
		component.setOrientation(LinearLayout.HORIZONTAL);
		tvTitle.setLayoutParams(paramtext);
		component.addView(buttonItem);
		component.addView(tvTitle);

		buttonItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DocFlow.mapObjectProcessor != null
						&& DocFlow.mapObjectProcessor.mapObjectProcessor != null) {
					int _subregion_id = -1;
					HashMap<String, FieldDefinitionItem> map = panel
							.getFormitemMap();
					FieldDefinitionItem sri = null;

					for (String key : map.keySet()) {
						FieldDefinitionItem item = map.get(key);
						System.out.println(key);
						if ((item.getFieldDef().getDefaultValue() != null && item
								.getFieldDef().getDefaultValue().trim()
								.equals("$subregionId"))
								|| key.trim()
										.toLowerCase(Locale.ENGLISH)
										.equals("subregionId"
												.toLowerCase(Locale.ENGLISH))
								|| key.trim()
										.toLowerCase()
										.equals("subregion_Id"
												.toLowerCase(Locale.ENGLISH))) {
							sri = map.get(key);
							break;
						}
					}
					if (sri != null) {
						Object subregion_id = sri.getFormItem().getValue();
						if (subregion_id == null
								|| subregion_id.toString().trim().isEmpty()
								|| subregion_id.toString().trim().equals("-1")) {
							final IFormItem fi = sri.getFormItem();
							SC.say(DocFlow.activity,
									"Please select subregion!!!",
									new BooleanCallback() {
										@Override
										public void execute(Boolean value) {
											fi.requestFocusSelf();
										}
									});
							subregion_id = panel.getDocument().getSubregionid();
							// return;
						}
						_subregion_id = FormItem.getRowValueLong(subregion_id)
								.intValue();
					}
					Integer cusid = null;
					try {
						cusid = panel.getDocument().getCust_id();
						if (cusid != null && cusid.intValue() < 1)
							cusid = null;
					} catch (Throwable e) {
						//
					}
					DocFlow.mapObjectProcessor.mapObjectProcessor.execute(
							MapButton.this, cusid, _subregion_id);
				}

			}
		});

	}

	@Override
	public View getComponent() {
		return component;
	}

	@Override
	public void setValue(final Object _value) {
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			setValuePrivate(_value);
		} else {
			((Activity) this.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setValuePrivate(_value);
				}
			});
		}

	}

	private void setValuePrivate(Object _value) {
		_value = _value == null ? "" : _value;
		value = _value.toString().trim();
		if (value.isEmpty())
			tvTitle.setText("");
		else {
			String title = "მონაცემები შეყვანილია";
			try {
				if (DocFlow.mapObjectProcessor != null)
					title = getContext().getString(
							DocFlow.mapObjectProcessor.text);
			} catch (Exception e) {
			}
			tvTitle.setText(title);
		}
	}

	@Override
	public String getDisplayValue() {
		try {
			return tvTitle.getText().toString().trim();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean needTitle() {
		return false;
	}

	@Override
	public void setTitle(String title) {
		tvTitle.setText("");
		title = title == null ? "" : title.trim();
		if (title.isEmpty())
			title = "map/google_earth.png";
		String images = "images/";
		if (!title.startsWith(images)
				&& !title.toLowerCase().startsWith("http"))
			title = images + title;
		String url = title;
		if (!title.toLowerCase().startsWith("http")) {

			url = DrowableDownloader.getMainURL() + title;
		}
		try {
			drawable = DrowableDownloader.drawableFromUrl(url);
			buttonItem.setImageDrawable(drawable);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		// TODO Auto-generated method stub
		return buttonItem.getOnFocusChangeListener();
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener newList) {
		buttonItem.setOnFocusChangeListener(newList);

	}

	public Drawable getDrawable() {
		return drawable;
	}

}
