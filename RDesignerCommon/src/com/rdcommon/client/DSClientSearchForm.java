package com.rdcommon.client;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.rdcommon.shared.ds.DSDefinition;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class DSClientSearchForm extends VLayout {

	private ArrayList<DynamicForm> dynamicForms;
	private IButton findButton;
	private IButton clearButton;
	@SuppressWarnings("unused")
	private DSDefinition ds;

	public DSClientSearchForm(DSDefinition ds, String name) {
		this.ds = ds;
		dynamicForms = ClientUtils.createFormPanel(ds, name, this);

		HLayout buttonLayout = new HLayout(5);
		buttonLayout.setWidth100();
		buttonLayout.setHeight(30);
		buttonLayout.setAlign(Alignment.RIGHT);

		findButton = new IButton();
		findButton.setTitle(("find"));

		findButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				search();

			}
		});

		clearButton = new IButton();
		clearButton.setTitle(("clear"));
		clearButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clearValues();

			}
		});
		buttonLayout.setBorder("5px black solid ");
		buttonLayout.setMembers(findButton, clearButton);
		this.addMember(buttonLayout);
	}

	protected void search() {
		TreeMap<String, Object> globalMap = new TreeMap<String, Object>();
		for (DynamicForm dm : dynamicForms) {
			Map<?, ?> mp = dm.getValues();
			Set<?> keys = mp.keySet();
			for (Object key : keys) {
				globalMap.put(key.toString(), mp.get(key));
			}
		}
		System.out.println(globalMap);
	}

	protected void clearValues() {
		for (DynamicForm dm : dynamicForms) {
			dm.setValues(new TreeMap<String, Object>());
		}

	}

	public void doCallBack(String invokerFieldName, JavaScriptObject values) {
		try {
			Map<?, ?> vals = JSOHelper.convertToMap(values);
			System.out.println(vals);
			for (DynamicForm dm : dynamicForms) {
				dm.setValues(vals);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public JavaScriptObject getValueMap() {
		Map<String, Object> map = new TreeMap<String, Object>();
		ClientUtils.fillMapFromForm(map,
				dynamicForms.toArray(new DynamicForm[] {}));
		JavaScriptObject ret = JSOHelper.convertMapToJavascriptObject(map);
		return ret;
	}
}
