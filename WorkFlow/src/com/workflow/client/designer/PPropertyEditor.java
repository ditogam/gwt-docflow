package com.workflow.client.designer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.core.DataClass;
import com.smartgwt.client.widgets.BaseWidget;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.workflow.client.Utils;
import com.workflow.client.designer.components.ComponentTreeNode;
import com.workflow.client.designer.components.PropertyEditor;

public class PPropertyEditor extends VLayout {

	private IButton bMode;
	private IButton bSave;
	private boolean basic;
	private PropertyEditor propertyEditor = null;
	private KeyPressHandler keySave;

	private Object obj;
	private Object attrbts;
	private ListGridRecord rec;

	public PPropertyEditor(boolean methode_mode) {
		keySave = new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().toLowerCase()
						.equals("Enter".toLowerCase()))
					saveComponentData();
			}
		};
		propertyEditor = new PropertyEditor();
		propertyEditor.setHeight("*");
		if (methode_mode)
			propertyEditor.switch_to_MethodeMode();
		HLayout bLayout = new HLayout();
		bLayout.setAutoHeight();
		bMode = new IButton("More");
		bMode.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				propertyEditor.switchMode();
				setKeyEvents();
				basic = !basic;
				bMode.setTitle(basic ? "More" : "Less");
			}
		});
		bSave = new IButton("Apply");
		bSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveComponentData();

			}
		});
		bLayout.setMembers(bSave, bMode);
		setMembers(propertyEditor, bLayout);
	}

	protected void saveComponentData() {
		Map map = propertyEditor.getChangedValues();
		propertyEditor.rememberValues();
		if (map == null || map.isEmpty())
			return;
		Map<String, Object> objAttr = null;
		if (attrbts == null) {
			attrbts = new HashMap<String, Object>();
		}
		objAttr = Utils.jsToMap(attrbts);
		objAttr.putAll(map);
		rec.setAttribute(ComponentTreeNode.COMPONENT_PARAMS_ATTRIBUTE, objAttr);
		PComponentView.setAttributesToComp(objAttr, obj);
	}

	public PPropertyEditor() {
		this(false);
	}

	public boolean setComponent_Data(Object obj, Object attrbts,
			ListGridRecord rec) {
		this.obj = obj;
		this.attrbts = attrbts;
		this.rec = rec;
		JavaScriptObject js_obj = null;
		if (obj instanceof BaseWidget) {
			js_obj = ((BaseWidget) obj).getJsObj();
		}
		if (obj instanceof DataClass) {
			js_obj = ((DataClass) obj).getJsObj();
		}
		if (js_obj == null)
			return false;
		propertyEditor.editComponent(js_obj,
				Utils.getClassSimpleName(obj.getClass()));
		setKeyEvents();
		Map mp = Utils.jsToMap(attrbts);
		if (mp != null)
			propertyEditor.setValues(mp);
		propertyEditor.rememberValues();
		return true;
	}

	public void setKeyEvents() {
		for (FormItem formItem : propertyEditor.getFields()) {
			formItem.addKeyPressHandler(keySave);
		}
	}
}
