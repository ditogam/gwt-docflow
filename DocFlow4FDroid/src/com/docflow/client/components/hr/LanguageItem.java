package com.docflow.client.components.hr;

import java.util.HashMap;

import com.docflow.client.DocFlow;
import com.docflow.shared.hr.Captions;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;

public class LanguageItem extends TextItem implements LanguageValueSet {

	private Long id;

	public LanguageItem() {
		constructItem();
	}

	public LanguageItem(JavaScriptObject jsObj) {
		super(jsObj);
		constructItem();

	}

	public LanguageItem(String name) {
		super(name);
		constructItem();
	}

	public LanguageItem(String name, String title) {
		super(name, title);
		constructItem();
	}

	private void constructItem() {
		setAttribute("readOnly", true);
		PickerIcon dataPicker = new PickerIcon(PickerIcon.REFRESH,
				new FormItemClickHandler() {

					@Override
					public void onFormItemClick(FormItemIconClickEvent event) {
						showCaptionInput();
					}
				});
		setIcons(dataPicker);
	}

	public void setValue(Long id, String value) {
		setValue(value);
		setId(id);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		if (id == null)
			return -1L;
		return id;
	}

	private void showCaptionInput() {
		if (id == null) {
			showCaptionInput(new HashMap<Integer, Captions>());
			return;
		}
		DocFlow.docFlowService.getCaptions(id,
				new AsyncCallback<HashMap<Integer, Captions>>() {

					@Override
					public void onSuccess(HashMap<Integer, Captions> result) {
						showCaptionInput(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());

					}
				});
	}

	private void showCaptionInput(HashMap<Integer, Captions> result) {
		WCaptions.showForm(true, id, getTitle(), this, result);
	}

}
