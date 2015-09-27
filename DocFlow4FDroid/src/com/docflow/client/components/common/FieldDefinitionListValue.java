package com.docflow.client.components.common;

import java.util.HashMap;
import java.util.Set;

import com.docflow.client.components.CurrentTimeItem;
import com.docflow.shared.common.FieldDefinition;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;

public abstract class FieldDefinitionListValue {
	protected HashMap<String, FieldDefinitionItem> formitemMap;
	protected FormDefinitionPanel definitionPanel;

	public abstract void additionalValidator(FieldDefinitionItem item)
			throws Exception;

	public abstract void fillCombo(FieldDefinitionItem item);

	public abstract ICalculatorClass getCalculetorClass(String name);
	
	public abstract void activate();

	protected Object getValue(FieldDefinitionItem item) {
		Object value = item.getFormItem().getValue();
		if (value == null) {
			return value;
		}
		FieldDefinition fieldDef = item.getFieldDef();
		Number numbernumb = null;
		if ((!fieldDef.isComboString() && (fieldDef.getFieldType() == FieldDefinition.FT_COMBO || fieldDef
				.getFieldType() == FieldDefinition.FT_SELECTION))
				|| fieldDef.getFieldType() == FieldDefinition.FT_INTEGER) {
			try {
				numbernumb = Long.parseLong(value.toString());

			} catch (Exception e) {
				numbernumb = 0.0;
			}
		}
		if (fieldDef.getFieldType() == FieldDefinition.FT_DOUBLE) {
			try {
				numbernumb = Double.parseDouble(value.toString());
			} catch (Exception e) {
				numbernumb = 0.0;
			}
		}
		if (numbernumb != null && numbernumb.doubleValue() == 0.0
				&& !fieldDef.isCanBeNoll() && !fieldDef.isFieldReadOnly()) {
			return null;
		}
		if (fieldDef.getFieldType() == FieldDefinition.FT_STRING
				&& value.toString().trim().length() == 0
				&& !fieldDef.isFieldReadOnly()) {
			return null;
		}
		if (fieldDef.getFieldType() == FieldDefinition.FT_CHK_GRID
				&& value.toString().trim().length() == 0
				&& !fieldDef.isFieldReadOnly()) {
			return null;
		}
		return value;
	}

	public void setFieldDefinitionListValue(
			HashMap<String, FieldDefinitionItem> formitemMap,
			FormDefinitionPanel definitionPanel) {

		this.formitemMap = formitemMap;
		this.definitionPanel = definitionPanel;
		Set<String> keys = formitemMap.keySet();
		for (String key : keys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			final FormItem formItem = item.getFormItem();
			FieldDefinition fieldDef = item.getFieldDef();
			if (fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION) {
				fillCombo(item);
			}
			formItem.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					valueChanged(event, item);
				}
			});

		}
	}

	public boolean validate() {
		Set<String> keys = formitemMap.keySet();
		for (String key : keys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			final FormItem formItem = item.getFormItem();
			final FieldDefinition fieldDef = item.getFieldDef();
			try {
				additionalValidator(item);
			} catch (Exception e) {
				return false;
			}
			if (getValue(item) == null && fieldDef.isRequaiered()) {
				SC.say("Error!!!",
						"Please "
								+ (((fieldDef.getFieldType() == FieldDefinition.FT_COMBO || fieldDef
										.getFieldType() == FieldDefinition.FT_SELECTION)) ? " select "
										: " enter ")
								+ fieldDef.getFieldCaption(),
						new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								formItem.focusInItem();

							}
						});
				return false;
			}
			if (fieldDef.getFieldType() == FieldDefinition.FT_INTEGER
					|| fieldDef.getFieldType() == FieldDefinition.FT_DOUBLE) {
				try {
					double value = Double
							.parseDouble(getValue(item).toString());
					if (value < fieldDef.getMinValue()) {
						SC.say("Error!!!", fieldDef.getFieldCaption()
								+ " must be at least " + fieldDef.getMinValue()
								+ "!!!", new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								formItem.focusInItem();

							}
						});
						return false;
					}

					if (value > fieldDef.getMaxValue()) {
						SC.say("Error!!!", fieldDef.getFieldCaption()
								+ " must be less " + fieldDef.getMinValue()
								+ "!!!", new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								formItem.focusInItem();

							}
						});
						return false;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE
					&& formItem instanceof CurrentTimeItem) {
				String error = definitionPanel.validateDate(formItem, fieldDef)
						+ "";
				if (error.equals("-1"))
					error = "თარიღი მითითებულია მომავალში!!!";
				else if (error.equals("-2") && !fieldDef.isDonotcheckForDate())
					error = "თარიღი ძალიან ძველია!!!!";
				else
					error = "";
				if (error != null && error.length() > 0) {
					SC.say("Error!!!", error, new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							formItem.focusInItem();
						}
					});
					return false;
				}
			}

		}
		return true;
	}

	public abstract void valueChanged(ChangeEvent event,
			FieldDefinitionItem item);
}
