package com.docflow.client.components.common.u;

import java.util.ArrayList;

import com.common.client.SplashDialog;
import com.docflow.client.DocFlow;
import com.docflow.client.components.common.FormDefinitionPanel;
import com.docflow.shared.common.FieldDefinition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;

public class ImageItem extends StaticTextItem {
	private FormDefinitionPanel panel;
	private FieldDefinition field;
	private FormItemIcon icon;
	private String value = null;
	private static final String display_image = "display_image.png";
	private static final String folder_open_16 = "folder_open_16.png";

	public ImageItem(FormDefinitionPanel panel, FieldDefinition field) {
		super();
		this.panel = panel;
		this.field = field;

		icon = new PickerIcon(new Picker(folder_open_16),
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						if (value == null || value.trim().isEmpty()) {
							try {
								openimage(ImageItem.this.field.getFieldName(),
										ImageItem.this);
							} catch (Exception e) {
								SplashDialog.hideSplash();
							}
						} else {
							DocFlow.showFile(value.toString());
						}
					}
				});
		super.setIcons(icon);
	}

	public native void openimage(String field_name, ImageItem item)/*-{

		$wnd.image_functions = function(field, result) {
			item.@com.docflow.client.components.common.u.ImageItem::setImageResult(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(field, result);
		};
		$wnd.upload_started = function(txt) {
			item.@com.docflow.client.components.common.u.ImageItem::upload_started_self(Ljava/lang/String;)(txt);
		};
		$wnd.post_upload_file(field_name, 'image_functions');
	}-*/;

	public void upload_started_self(String txt) {
		SplashDialog.showSplash();
		Timer timer = new Timer() {

			@Override
			public void run() {
				SplashDialog.hideSplash();

			}
		};
		timer.schedule(30000);
	}

	public void setImageResult(String field_name, JavaScriptObject obj) {
		SplashDialog.hideSplash();
		obj = getFirstElement(obj);
		int id = JSOHelper.getAttributeAsInt(obj, "id");
		String filename = JSOHelper.getAttribute(obj, "file");
		setValue(id + "");
		setDisplayValue(filename);
	}

	private native JavaScriptObject getFirstElement(JavaScriptObject obj)/*-{
		return obj[0];
	}-*/;

	@Override
	public void setIcons(FormItemIcon... icons) {
		ArrayList<FormItemIcon> _icons = new ArrayList<FormItemIcon>();
		_icons.add(icon);
		if (icons != null)
			for (FormItemIcon formItemIcon : icons) {
				_icons.add(formItemIcon);
			}
		super.setIcons(_icons.toArray(new FormItemIcon[] {}));
	}

	@Override
	public void setValue(Object value) {
		setValue(value == null ? (String) null : value.toString());
	}

	@Override
	public void setValue(String value) {
		this.value = value;
		String img = display_image;
		if (value == null || value.toString().trim().isEmpty()) {
			img = folder_open_16;
			setDisplayValue(null);
		}
		icon.setSrc(img);
		setAttribute("icons", getAttributeAsJavaScriptObject("icons"));
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getDisplayValue() {
		Object dv = super.getValue();
		return dv == null ? null : dv.toString();
	}

	public void setDisplayValue(String value) {
		super.setValue(value);
	}
}
