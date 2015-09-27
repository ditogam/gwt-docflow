package com.rdcommon.client;

import java.util.LinkedHashMap;

import com.rdcommon.shared.ds.DSClientFieldDef;
import com.rdcommon.shared.ds.DSDefinition;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class ClientFieldDef {

	private DSClientFieldDef dsClientFieldDef;
	private FormItem formItem;

	public ClientFieldDef(DSClientFieldDef dsClientFieldDef, FormItem formItem) {
		this.dsClientFieldDef = dsClientFieldDef;
		this.formItem = formItem;
	}

	public static final int FT_STRING = 1; // java.textfield.string
	public static final int FT_INTEGER = 2; // java.textfield.int or
											// java.textfield.long
	public static final int FT_DOUBLE = 3; // java.textfield.double
	public static final int FT_BOOLEAN = 4; // java.textfield.boolean
	public static final int FT_TEXTAREA = 5; // java.textarea string
	public static final int FT_STATICTEXT = 6; // java.label
	public static final int FT_COMBO = 7; // java.combobox editable-find
	public static final int FT_SELECTION = 8; // java.combobox selection
	public static final int FT_DATE = 9; // java.date
	public static final int FT_IMAGE = 10; // java.date

	private static LinkedHashMap<String, String> fieldTypes = null;
	private static LinkedHashMap<String, String> fieldActualTypes = null;

	public static LinkedHashMap<String, String> getFieldTypes() {
		if (fieldTypes == null) {
			fieldTypes = new LinkedHashMap<String, String>();
			fieldTypes.put(FT_STRING + "", "String");
			fieldTypes.put(FT_INTEGER + "", "Integer(Long)");
			fieldTypes.put(FT_DOUBLE + "", "Float(Double)");
			fieldTypes.put(FT_BOOLEAN + "", "Boolean");
			fieldTypes.put(FT_TEXTAREA + "", "Big text");
			fieldTypes.put(FT_STATICTEXT + "", "Label");
			fieldTypes.put(FT_SELECTION + "", "Drop down box");
			fieldTypes.put(FT_COMBO + "", "Combo box");
			fieldTypes.put(FT_DATE + "", "Date");
			fieldTypes.put(FT_IMAGE + "", "Image");
		}
		return fieldTypes;
	}

	public static LinkedHashMap<String, String> getFieldActualTypes() {
		if (fieldActualTypes == null) {
			LinkedHashMap<String, String> tmp = new LinkedHashMap<String, String>();
			tmp.put(1 + "", "String");
			tmp.put(2 + "", "Long");
			tmp.put(3 + "", "Integer");
			tmp.put(4 + "", "Float");
			tmp.put(5 + "", "Double");
			tmp.put(6 + "", "Boolean");
			tmp.put(7 + "", "Date");
			tmp.put(8 + "", "Timestamp");
			tmp.put(9 + "", "Image");
			ClientFieldDef.fieldActualTypes = new LinkedHashMap<String, String>();
			for (String val : tmp.values()) {
				ClientFieldDef.fieldActualTypes.put(val, val);
			}
		}
		return fieldActualTypes;
	}

	public static ClientFieldDef createField(DSClientFieldDef field) {
		FormItem fi = null;
		switch (field.getType()) {
		case FT_STRING:
			fi = new TextItem();
			break;
		case FT_INTEGER:
			fi = new IntegerItem();
			break;
		case FT_DOUBLE:
			fi = new FloatItem();
			break;
		case FT_BOOLEAN:
			fi = new CheckboxItem();
			break;
		case FT_TEXTAREA:
			fi = new TextAreaItem();
			// fi.setWidth("10000px");
			break;
		case FT_STATICTEXT:
			fi = new StaticTextItem();
			break;
		case FT_COMBO:
			fi = new ComboBoxItem();
			break;
		case FT_SELECTION:
			fi = new SelectItem();
			break;
		case FT_DATE:
			fi = new DateItem();
			break;
		default:
			break;
		}
		if (fi == null) {
			return null;
		}
		ClientUtils.setValues(field, fi);
		fi.setName(field.getName());
		fi.setTitle(field.getTitle());

		if (field.getReadOnly() != null && field.getReadOnly()) {
			fi.setCanEdit(false);
		}
		setDefaultValue(field, fi);
		field.getAdditionalProps();
		if (FT_SELECTION == field.getType() || field.getType() == FT_COMBO) {
			if (field.getDsName() != null && field.getDsIdField() != null
					&& field.getDsValueField() != null) {
				ClientUtils
						.fillCombo(
								fi,
								(field.getDsIsCustomGenerated() != null && field
										.getDsIsCustomGenerated()) ? DSDefinition.DSDefinition_EXTN
										+ field.getDsName()
										: field.getDsName(), field
										.getDsFunction(), field.getDsIdField(),
								field.getDsValueField(), null);
			}
		}

		return new ClientFieldDef(field, fi);
	}

	private static void setDefaultValue(DSClientFieldDef field, FormItem fi) {
		if (field.getDefaultValue() != null
				&& field.getDefaultValue().trim().length() > 0) {
			String defValue = field.getDefaultValue().trim();
			fi.setValue(defValue);
		}
	}

	public DSClientFieldDef getDsClientFieldDef() {
		return dsClientFieldDef;
	}

	public void setDsClientFieldDef(DSClientFieldDef dsClientFieldDef) {
		this.dsClientFieldDef = dsClientFieldDef;
	}

	public FormItem getFormItem() {
		return formItem;
	}

	public void setFormItem(FormItem formItem) {
		this.formItem = formItem;
	}
}
