package com.rdcommon.client.components.ds;

import java.util.TreeMap;

import com.rdcommon.client.ClientFieldDef;
import com.rdcommon.client.ClientUtils;
import com.rdcommon.shared.ds.DSClientFieldDef;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class PDSClientField extends VLayout {
	private int type;

	private DSClientFieldDef fieldDef;

	private PDSComponent component;
	private DynamicForm dmMain;
	private DynamicForm dmSelect;

	private SelectItem tiType;
	private SelectItem tiActualType;
	private TextItem tiGroupName;
	private TextItem tiParentFieldName;
	private TextItem tiParentValueFieldName;
	private TextItem tiChangeHandlerMethode;
	private TextItem tiDefaultValue;

	private TextItem tiDsName;
	private BooleanItem biIsCustomDS;
	private TextItem tiDsFunction;
	private TextItem tiDsIdField;
	private TextItem tiDsValueField;

	public PDSClientField(DSClientFieldDef fieldDef, final int type) {
		if (fieldDef == null)
			fieldDef = new DSClientFieldDef();
		else {
			DSClientFieldDef nf = new DSClientFieldDef();
			nf.mergeProps(fieldDef, new DSClientFieldDef());
			fieldDef = nf;
		}
		this.type = type;
		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setWidth("350");
		dmMain.setHeight("100");
		this.addMember(dmMain);
		tiType = new SelectItem("type", "Type");
		tiType.setValueMap(ClientFieldDef.getFieldTypes());
		tiType.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				String val = event.getValue().toString();
				boolean iscombo = val.equals(ClientFieldDef.FT_COMBO + "")
						|| val.equals(ClientFieldDef.FT_SELECTION + "");
				dmSelect.setDisabled(!iscombo);

			}
		});

		tiActualType = new SelectItem("actualType", "Actual type");
		tiActualType.setValueMap(ClientFieldDef.getFieldActualTypes());
		tiGroupName = new TextItem("groupName", "Group name");
		tiParentFieldName = new TextItem("parentFieldName", "Parent name");
		tiParentValueFieldName = new TextItem("parentFieldValueName",
				"Parent value field name");

		tiChangeHandlerMethode = new TextItem("changeHandlerMethode",
				"Change JS method");
		tiDefaultValue = new TextItem("defaultValue", "Default value");

		dmMain.setNumCols(3);
		dmMain.setFields(tiType, tiActualType, tiGroupName, tiParentFieldName,
				tiParentValueFieldName, tiChangeHandlerMethode, tiDefaultValue);

		component = new PDSComponent(fieldDef, type, true);
		component.getDmMain().setNumCols(3);
		dmSelect = new DynamicForm();
		dmSelect.setTitleOrientation(TitleOrientation.TOP);
		dmSelect.setWidth("350");
		dmSelect.setHeight("100");
		dmSelect.setDisabled(true);
		dmSelect.setGroupTitle("For Drop down box or Combo box only");

		tiDsName = new TextItem("dsName", "Data source");
		biIsCustomDS = new BooleanItem("dsIsCustomGenerated", "Is custom DS");
		tiDsFunction = new TextItem("dsFunction", "DS function");
		tiDsIdField = new TextItem("dsIdField", "DS ID Field");
		tiDsValueField = new TextItem("dsValueField", "DS value Field");
		dmSelect.setNumCols(3);
		dmSelect.setFields(tiDsName, biIsCustomDS, tiDsFunction, tiDsIdField,
				tiDsValueField);
		component.setHeight("100");
		component.setWidth("350");
		component.setVertical(false);
		this.addMember(dmSelect);
		this.addMember(component);

		setFieldDef(fieldDef);
	}

	public void setFieldDef(DSClientFieldDef fieldDef) {
		this.fieldDef = fieldDef;
		if (fieldDef == null) {
			dmMain.setValues(new TreeMap<String, Object>());
			dmSelect.setValues(new TreeMap<String, Object>());
			tiType.setValue("1");
			fieldDef = new DSClientFieldDef();
			component.setData(fieldDef);
			dmSelect.setDisabled(true);
			return;
		}

		tiType.setValue(fieldDef.getType() + "");
		tiActualType.setValue(fieldDef.getActualType());
		tiGroupName.setValue(fieldDef.getGroupName());
		tiParentFieldName.setValue(fieldDef.getParentFieldName());
		tiParentValueFieldName.setValue(fieldDef.getParentFieldValueName());
		tiChangeHandlerMethode.setValue(fieldDef.getChangeHandlerMethode());
		tiDefaultValue.setValue(fieldDef.getDefaultValue());

		dmSelect.setDisabled(!(fieldDef.getType() == ClientFieldDef.FT_COMBO || fieldDef
				.getType() == ClientFieldDef.FT_SELECTION));

		tiDsName.setValue(fieldDef.getDsName());
		biIsCustomDS.setValue(fieldDef.getDsIsCustomGenerated());
		tiDsFunction.setValue(fieldDef.getDsFunction());
		tiDsIdField.setValue(fieldDef.getDsIdField());
		tiDsValueField.setValue(fieldDef.getDsValueField());
		component.setData(fieldDef);
	}

	public DSClientFieldDef getFieldDef() {

		fieldDef.setType(ClientUtils.getIntValue(tiType));
		fieldDef.setActualType(tiActualType.getValueAsString());
		fieldDef.setGroupName(tiGroupName.getValueAsString());
		fieldDef.setParentFieldName(tiParentFieldName.getValueAsString());
		fieldDef.setParentFieldValueName(tiParentValueFieldName
				.getValueAsString());
		fieldDef.setChangeHandlerMethode(tiChangeHandlerMethode
				.getValueAsString());
		fieldDef.setDefaultValue(tiDefaultValue.getValueAsString());
		boolean b = dmSelect.getDisabled();

		fieldDef.setDsName(b ? null : tiDsName.getValueAsString());
		fieldDef.setDsIsCustomGenerated(b ? null : ClientUtils
				.getBooleanValue(biIsCustomDS));

		fieldDef.setDsFunction(b ? null : tiDsFunction.getValueAsString());
		fieldDef.setDsIdField(b ? null : tiDsIdField.getValueAsString());
		fieldDef.setDsValueField(b ? null : tiDsValueField.getValueAsString());

		component.saveData();

		return fieldDef;
	}

	public DSClientFieldDef cloneData() {
		getFieldDef();
		DSClientFieldDef nf = new DSClientFieldDef();
		nf.mergeProps(fieldDef, new DSClientFieldDef());
		return nf;
	}
}
