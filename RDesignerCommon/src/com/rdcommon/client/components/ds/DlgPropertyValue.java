package com.rdcommon.client.components.ds;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.rdcommon.client.ClientGlobalSettings;
import com.rdcommon.client.SavePanel;
import com.rdcommon.shared.props.PropertyNames;
import com.rdcommon.shared.props.PropertyTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;

public class DlgPropertyValue extends Window {

	private static final String[] customTypes = new String[] { "String",
			"Number", "Boolean", "Date" };

	private final static String ENUM_NAME = "OTHER";

	private RadioGroupItem rgPropType;
	private SelectItem siType;
	private TreeMap<String, FormItem> typeMappings;
	private TreeMap<String, FormItem> propNameMappings;
	private ListGrid listGrid;
	private int type;
	private Record record;

	public DlgPropertyValue(ListGrid listGrid, int type, Record record) {
		this.listGrid = listGrid;
		this.type = type;
		this.record = record;

		typeMappings = new TreeMap<String, FormItem>();
		propNameMappings = new TreeMap<String, FormItem>();

		DynamicForm dfPropertyType = new DynamicForm();
		dfPropertyType.setGroupTitle("Property type");
		LinkedHashMap<String, String> prType = new LinkedHashMap<String, String>();
		prType.put("1", "Standart");
		prType.put("2", "Custom");
		rgPropType = new RadioGroupItem();
		rgPropType.setDefaultValue("1");
		rgPropType.setValueMap(prType);
		rgPropType.setVertical(false);
		rgPropType.setShowTitle(false);
		dfPropertyType.setFields(rgPropType);
		dfPropertyType.setWidth100();

		SelectItem siNames = new SelectItem();
		siNames.setTitle("Standart name");
		siNames.setWidth(220);
		propNameMappings.put("1", siNames);
		siNames.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				nameValueChanged(event.getValue().toString(), null);

			}
		});

		TextItem tiName = new TextItem();
		tiName.setTitle("Custom name");
		tiName.setVisible(false);
		tiName.setWidth(220);
		propNameMappings.put("2", tiName);

		siType = new SelectItem();
		siType.setTitle("Value type");
		siType.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				typeValueChanged(event.getValue().toString());

			}
		});

		TextItem tiStringValue = new TextItem();
		tiStringValue.setTitle("String value");
		typeMappings.put("String", tiStringValue);

		FloatItem fiNumber = new FloatItem();
		fiNumber.setVisible(false);
		fiNumber.setTitle("Number value");
		typeMappings.put("Number", fiNumber);

		BooleanItem biBoolean = new BooleanItem();
		biBoolean.setVisible(false);
		biBoolean.setTitle("Boolean value");
		biBoolean.setWidth(120);
		biBoolean.setValue(false);
		typeMappings.put("Boolean", biBoolean);

		DateItem diDate = new DateItem();
		diDate.setVisible(false);
		diDate.setTitle("Date value");
		typeMappings.put("Date", diDate);

		SelectItem diEnum = new SelectItem();
		diEnum.setVisible(false);
		diEnum.setTitle("List value");
		typeMappings.put(ENUM_NAME, diEnum);

		DynamicForm dfValue = new DynamicForm();
		dfValue.setGroupTitle("Values");
		dfValue.setNumCols(2);
		dfValue.setTitleOrientation(TitleOrientation.TOP);
		dfValue.setFields(siNames, tiName, siType, tiStringValue, fiNumber,
				biBoolean, diDate, diEnum);

		rgPropType.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				setStandartProperty(event.getValue().toString());
			}
		});
		if (record == null)
			setStandartProperty("1");
		else
			setValue();

		setWidth(390);
		setHeight(170);
		this.addItem(dfPropertyType);
		this.addItem(dfValue);
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					destroy();

			}
		});
		SavePanel sp = new SavePanel(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}
		}, this);
		sp.setHeight("6%");
		this.addItem(sp);
		this.centerInPage();
		setCanDragResize(true);
		this.setIsModal(true);
	}

	protected void saveData() {
		FormItem valueItem = null;
		for (FormItem item : typeMappings.values()) {
			if (item.isVisible()) {
				valueItem = item;
				break;
			}
		}
		FormItem nameItem = null;
		for (FormItem item : propNameMappings.values()) {
			if (item.isVisible()) {
				nameItem = item;
				break;
			}
		}

		if (valueItem == null || nameItem == null)
			return;

		Object nameO = nameItem.getValue();
		if (nameO == null)
			return;
		Object valueO = valueItem.getValue();
		if (valueO == null)
			return;
		String name = nameO.toString().trim();
		if (name.isEmpty())
			return;
		if (!(nameItem instanceof SelectItem)) {
			if (ClientGlobalSettings.detectIfPropertyIsStandart(name, type) != null) {
				SC.say("Property '" + name + "' is standart, please use it!!!");
				return;
			}
		}
		String value = null;
		try {
			value = validateValue(nameItem, valueItem, valueO);
		} catch (Exception e) {
			SC.say(e.getMessage());
			return;
		}

		if (record != null) {
			String recname = record.getAttribute("propname");
			if (!recname.equalsIgnoreCase(name)) {
				Record rec = listGrid.getRecordList().find("propname", name);
				if (rec == null) {
					record.setAttribute("propname", name);
				} else {
					SC.say("Property '" + name + "' is already exists!!!");
					return;
				}
			}else{
				if (!recname.equals(name)) {
					record.setAttribute("propname", name);
				}
			}
			record.setAttribute("value", value);
			listGrid.refreshRow(listGrid.getRecordIndex(record));
			hide();
			return;
		} else {
			Record rec = listGrid.getRecordList().find("propname", name);
			if (rec != null) {
				SC.say("Property '" + name + "' is already exists!!!");
				return;
			}
			record = new Record();
			record.setAttribute("propname", name);
			record.setAttribute("value", value);
			listGrid.addData(record);
		}

		hide();
	}

	private String validateValue(FormItem nameItem, FormItem valueItem,
			Object valueO) throws Exception {

		String typeName = siType.getValueAsString();
		if (typeName == null)
			return null;
		if (typeName.equals("Boolean")) {
			return ((Boolean) valueO).toString();
		}
		if (typeName.equals("Date")) {
			return ClientGlobalSettings.dateFormatter.format(((Date) valueO));
		}

		if (typeName.equals("Number")) {
			return Double.parseDouble(valueO.toString()) + "";
		}
		if (typeName.equals("String")) {
			return valueO.toString();
		}
		return valueO.toString();
	}

	protected void typeValueChanged(String tp) {
		String strVal = "String";
		if (tp == null)
			tp = strVal;
		FormItem formItem = typeMappings.get(tp);
		if (formItem == null) {
			formItem = typeMappings.get(ENUM_NAME);
			LinkedHashMap<String, String> prNames = ClientGlobalSettings
					.getEnum(type, propNameMappings.get("1").getValue()
							.toString(), tp);
			formItem.setValueMap(prNames);
			formItem.setValue((String) null);
		}
		for (FormItem formItem1 : typeMappings.values()) {
			try {
				formItem1.hide();
			} catch (Exception e) {
				formItem1.setVisible(false);
			}
		}
		try {
			formItem.show();
		} catch (Exception e) {
			formItem.setVisible(true);
		}
	}

	private void setValue() {
		String name = record.getAttribute("propname");
		String val = record.getAttribute("value");
		PropertyNames names = ClientGlobalSettings.detectIfPropertyIsStandart(
				name, type);
		if (names != null) {
			name = names.getPropertyName();
			rgPropType.setValue("1");
			setStandartProperty("1");
			nameValueChanged(name, null);
			FormItem formItem = propNameMappings.get("1");
			formItem.setValue(name);
			String typeName = ClientGlobalSettings.getTypeName(type, name, val);
			if (typeName != null) {
				siType.setValue(typeName);
				typeValueChanged(typeName);
				typeMappings.get(ENUM_NAME).setValue(val);
			} else {
				Object[] typeNames = getTypeName(val);
				siType.setValue(typeNames[0]);
				typeValueChanged(typeNames[0].toString());
				FormItem formItem1 = typeMappings.get(typeNames[0].toString());
				formItem1.setValue(typeNames[1]);

			}
		} else {
			setStandartProperty("2");
			rgPropType.setValue("2");
			FormItem formItem = propNameMappings.get("2");
			formItem.setValue(name);
			nameValueChanged(name, null);
			Object[] typeNames = getTypeName(val);
			siType.setValue(typeNames[0]);
			typeValueChanged(typeNames[0].toString());
			formItem = typeMappings.get(typeNames[0].toString());
			formItem.setValue(typeNames[1]);
		}
	}

	private Object[] getTypeName(String value) {
		if (value == null || value.trim().isEmpty())
			return new Object[] { "String", null };

		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
			return new Object[] { "Boolean", new Boolean(value) };

		try {
			Date dt = ClientGlobalSettings.dateFormatter.parse(value);
			return new Object[] { "Date", dt };
		} catch (Exception e) {
			try {
				Double d = new Double(value);
				return new Object[] { "Number", d };
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return new Object[] { "String", value };
	}

	private String replaceStandartName(String key) {
		if (key.equalsIgnoreCase("Byte") || key.equalsIgnoreCase("Short")
				|| key.equalsIgnoreCase("Integer")
				|| key.equalsIgnoreCase("Long")
				|| key.equalsIgnoreCase("Float")
				|| key.equalsIgnoreCase("Double"))
			return "Number";
		return key;
	}

	protected void setStandartProperty(String typeVal) {
		for (FormItem formItem : propNameMappings.values()) {
			try {
				formItem.hide();
			} catch (Exception e) {
				formItem.setVisible(false);
			}
		}
		FormItem formItem = propNameMappings.get(typeVal);
		if (formItem != null) {
			try {
				formItem.show();
			} catch (Exception e) {
				formItem.setVisible(true);
			}

			if (typeVal.equals("1")) {
				TreeMap<String, PropertyNames> propNames = ClientGlobalSettings
						.getNames(type);
				LinkedHashMap<String, String> prNames = new LinkedHashMap<String, String>();
				String key = null;
				String val = null;

				for (PropertyNames propertyName : propNames.values()) {
					key = propertyName.getPropertyName();
					prNames.put(key, key);
					if (val == null)
						val = key;
				}
				formItem.setValueMap(prNames);
				formItem.setValue(val);
				nameValueChanged(val, null);
			} else {
				siType.setValueMap(customTypes);
			}
		}
	}

	private void nameValueChanged(String name, String value) {
		LinkedHashMap<String, String> typeNames = new LinkedHashMap<String, String>();
		try {
			PropertyNames names = ClientGlobalSettings
					.detectIfPropertyIsStandart(name, type);
			if (names == null)
				for (String key : customTypes) {
					typeNames.put(key, key);
				}
			else {
				for (PropertyTypes key : names.getPropertyTypes()) {
					String k = key.getType();
					k = replaceStandartName(k);
					typeNames.put(k, k);
				}
			}
		} finally {
			siType.setValueMap(typeNames);
			siType.setValue(value);
			typeValueChanged(null);
		}

	}
}
