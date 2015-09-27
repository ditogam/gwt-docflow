package com.docflow.shared.common;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class FieldDefinition implements IsSerializable {

	/**
	 * 
	 */

	// field types

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
	public static final int FT_CHK_GRID = 10;// java.textfield.string values
												// delimened by ","
	public static final int FT_MAP_ITEM = 11;// java.textfield.string
	public static final int FT_IMAGE_ITEM = 12;// java.textfield.string
	public static final int FT_CUSTOM = 13;// CustomItem
	// public static final int FT_GRID = 9;

	public static final double MAX_NUMBER = Double.MAX_VALUE;
	public static final double MIN_NUMBER = -10000000000.9D;

	private String fieldName; // fieldname
	private String fieldCaption; // field caption title
	private int fieldType; // field type -- zemot racaa imis mnishvneloba
	private String fieldWidth;
	private String fieldHeight;
	private int fieldSelectMethode; // Type from ClSelection
	private String fieldValidationClass; // temporary unused
	private String fieldValidationMethode; // temporary unused
	private String dependencyFields;// comma separated fieldnames
	private boolean requaiered;
	private boolean comboString;
	private boolean canBeNoll;
	private boolean fieldReadOnly;
	private int fieldCaptionId;
	private String fieldSelectionSQL;
	private String parentField;
	private String calculatorClass;
	private String defaultValue;
	private boolean hidden;
	private boolean calculatorProceed;
	private boolean canBeNegative;
	private boolean disabled;
	private boolean donotcheckForDate;
	private double maxValue;
	private double minValue;
	private String displayValue;
	private String displayTitles;
	private boolean calculateOnSet;
	private boolean displayValueIgnored;
	private boolean clearComboValue;
	private boolean noUnknownValueComboValue;

	public String getCalculatorClass() {
		return calculatorClass;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDependencyFields() {
		return dependencyFields;
	}

	public String getFieldCaption() {
		return fieldCaption;
	}

	public int getFieldCaptionId() {
		return fieldCaptionId;
	}

	public String getFieldHeight() {
		return fieldHeight;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldSelectionSQL() {
		return fieldSelectionSQL;
	}

	public int getFieldSelectMethode() {
		return fieldSelectMethode;
	}

	public int getFieldType() {
		return fieldType;
	}

	public String getFieldValidationClass() {
		return fieldValidationClass;
	}

	public String getFieldValidationMethode() {
		return fieldValidationMethode;
	}

	public String getFieldWidth() {
		return fieldWidth;
	}

	public String getParentField() {
		return parentField;
	}

	public boolean isCalculatorProceed() {
		return calculatorProceed;
	}

	public boolean isCanBeNegative() {
		return canBeNegative;
	}

	public boolean isCanBeNoll() {
		return canBeNoll;
	}

	public boolean isComboString() {
		return comboString;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean isDonotcheckForDate() {
		return donotcheckForDate;
	}

	public boolean isFieldReadOnly() {
		return fieldReadOnly;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isRequaiered() {
		return requaiered;
	}

	public void setCalculatorClass(String calculatorClass) {
		this.calculatorClass = calculatorClass;
	}

	public void setCalculatorProceed(boolean calculatorProceed) {
		this.calculatorProceed = calculatorProceed;
	}

	public void setCanBeNegative(boolean canBeNegative) {
		this.canBeNegative = canBeNegative;
	}

	public void setCanBeNoll(boolean canBeNoll) {
		this.canBeNoll = canBeNoll;
	}

	public void setComboString(boolean comboString) {
		this.comboString = comboString;
	}

	//
	// public void setXml(String str) {
	// XMLElement el = new XMLElement();
	// el.parseString(str);
	// setXml(el);
	// }

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDependencyFields(String dependencyFields) {
		this.dependencyFields = dependencyFields;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setDonotcheckForDate(boolean donotcheckForDate) {
		this.donotcheckForDate = donotcheckForDate;
	}

	public void setFieldCaption(String fieldCaption) {
		this.fieldCaption = fieldCaption;
	}

	public void setFieldCaptionId(int fieldCaptionId) {
		this.fieldCaptionId = fieldCaptionId;
	}

	public void setFieldHeight(String fieldHeight) {
		this.fieldHeight = fieldHeight;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldReadOnly(boolean fieldReadOnly) {
		this.fieldReadOnly = fieldReadOnly;
	}

	public void setFieldSelectionSQL(String fieldSelectionSQL) {
		this.fieldSelectionSQL = fieldSelectionSQL;
	}

	public void setFieldSelectMethode(int fieldSelectMethode) {
		this.fieldSelectMethode = fieldSelectMethode;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public void setFieldValidationClass(String fieldValidationClass) {
		this.fieldValidationClass = fieldValidationClass;
	}

	public void setFieldValidationMethode(String fieldValidationMethode) {
		this.fieldValidationMethode = fieldValidationMethode;
	}

	public void setFieldWidth(String fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public void setRequaiered(boolean requaiered) {
		this.requaiered = requaiered;
	}

	public void setXml(Node el) {
		fieldName = XMLParceserHelper.getAttribute("fieldName", el);
		fieldSelectionSQL = XMLParceserHelper.getAttribute("fieldSelectionSQL",
				el);
		fieldCaption = XMLParceserHelper.getAttribute("fieldCaption", el);
		fieldType = XMLParceserHelper.getIntValue("fieldType", el);
		fieldCaptionId = XMLParceserHelper.getIntValue("fieldCaptionId", el);
		fieldWidth = XMLParceserHelper.getAttribute("fieldWidth", el);
		calculatorClass = XMLParceserHelper.getAttribute("calculatorClass", el);
		fieldHeight = XMLParceserHelper.getAttribute("fieldHeight", el);
		fieldReadOnly = XMLParceserHelper.getBoolean("fieldReadOnly", el);
		fieldSelectMethode = XMLParceserHelper.getIntValue(
				"fieldSelectMethode", el);
		fieldValidationClass = XMLParceserHelper.getAttribute(
				"fieldValidationClass", el);
		fieldValidationMethode = XMLParceserHelper.getAttribute(
				"fieldValidationMethode", el);
		dependencyFields = XMLParceserHelper.getAttribute("dependencyFields",
				el);
		parentField = XMLParceserHelper.getAttribute("parentField", el);
		requaiered = XMLParceserHelper.getBoolean("requaiered", el);
		comboString = XMLParceserHelper.getBoolean("comboString", el);
		canBeNoll = XMLParceserHelper.getBoolean("canBeNoll", el);
		defaultValue = XMLParceserHelper.getAttribute("defaultValue", el);
		hidden = XMLParceserHelper.getBoolean("hidden", el);
		calculateOnSet = XMLParceserHelper.getBoolean("calculateOnSet", el);
		calculatorProceed = XMLParceserHelper.getBoolean("calculatorProceed",
				el);
		displayValueIgnored = XMLParceserHelper.getBoolean(
				"displayValueIgnored", el);
		noUnknownValueComboValue = XMLParceserHelper.getBoolean(
				"noUnknownValueComboValue", el);
		clearComboValue = XMLParceserHelper.getBoolean("clearComboValue", el);
		canBeNegative = XMLParceserHelper.getBoolean("canBeNegative", el);
		disabled = XMLParceserHelper.getBoolean("disabled", el);
		maxValue = XMLParceserHelper.getDoubleValue("maxValue", el, MAX_NUMBER);
		minValue = XMLParceserHelper.getDoubleValue("minValue", el, MIN_NUMBER);
		displayValue = XMLParceserHelper.getAttribute("displayValue", el);
		displayTitles = XMLParceserHelper.getAttribute("displayTitles", el);

		if (fieldType == FT_DATE)
			donotcheckForDate = XMLParceserHelper.getBoolean(
					"donotcheckForDate", el);
	}

	public Element toXmlElement(Document doc) {
		Element el = doc.createElement("Field");
		el.setAttribute("fieldName", fieldName);
		if (fieldCaption != null && fieldCaption.trim().length() != 0)
			el.setAttribute("fieldCaption", fieldCaption);
		if (displayValue != null && displayValue.trim().length() != 0)
			el.setAttribute("displayValue", displayValue);
		if (displayTitles != null && displayTitles.trim().length() != 0)
			el.setAttribute("displayTitles", displayTitles);
		if (fieldType != 0)
			el.setAttribute("fieldType", fieldType + "");
		if (fieldCaptionId != 0)
			el.setAttribute("fieldCaptionId", fieldCaptionId + "");
		if (fieldWidth != null && fieldWidth.trim().length() != 0)
			el.setAttribute("fieldWidth", fieldWidth);
		if (parentField != null && parentField.trim().length() != 0)
			el.setAttribute("parentField", parentField);
		if (fieldHeight != null && fieldHeight.trim().length() != 0)
			el.setAttribute("fieldHeight", fieldHeight);
		if (fieldSelectMethode != 0)
			el.setAttribute("fieldSelectMethode", fieldSelectMethode + "");
		if (fieldValidationClass != null
				&& fieldValidationClass.trim().length() != 0)
			el.setAttribute("fieldValidationClass", fieldValidationClass);
		if (fieldValidationMethode != null
				&& fieldValidationMethode.trim().length() != 0)
			el.setAttribute("fieldValidationMethode", fieldValidationMethode);
		if (dependencyFields != null && dependencyFields.trim().length() != 0)
			el.setAttribute("dependencyFields", dependencyFields);
		if (fieldReadOnly)
			el.setAttribute("fieldReadOnly", (fieldReadOnly ? 1 : 0) + "");
		if (displayValueIgnored)
			el.setAttribute("displayValueIgnored", (fieldReadOnly ? 1 : 0) + "");
		if (requaiered)
			el.setAttribute("requaiered", (requaiered ? 1 : 0) + "");

		if (noUnknownValueComboValue)
			el.setAttribute("noUnknownValueComboValue",
					(noUnknownValueComboValue ? 1 : 0) + "");
		if (clearComboValue)
			el.setAttribute("clearComboValue", (clearComboValue ? 1 : 0) + "");
		if (comboString)
			el.setAttribute("comboString", (comboString ? 1 : 0) + "");
		el.setAttribute("canBeNoll", (canBeNoll ? 1 : 0) + "");
		if (fieldSelectionSQL != null && fieldSelectionSQL.trim().length() != 0)
			el.setAttribute("fieldSelectionSQL", fieldSelectionSQL);
		if (calculatorClass != null && calculatorClass.trim().length() != 0)
			el.setAttribute("calculatorClass", calculatorClass);
		if (defaultValue != null && defaultValue.trim().length() != 0)
			el.setAttribute("defaultValue", defaultValue);
		if (hidden)
			el.setAttribute("hidden", (hidden ? 1 : 0) + "");
		if (calculateOnSet)
			el.setAttribute("calculateOnSet", (calculateOnSet ? 1 : 0) + "");
		if (maxValue != MAX_NUMBER)
			el.setAttribute("maxValue", maxValue + "");

		if (minValue != MIN_NUMBER)
			el.setAttribute("minValue", minValue + "");

		if (calculatorProceed)
			el.setAttribute("calculatorProceed", (calculatorProceed ? 1 : 0)
					+ "");
		if (canBeNegative)
			el.setAttribute("canBeNegative", (canBeNegative ? 1 : 0) + "");
		if (disabled)
			el.setAttribute("disabled", (disabled ? 1 : 0) + "");
		if (donotcheckForDate)
			el.setAttribute("donotcheckForDate", (donotcheckForDate ? 1 : 0)
					+ "");
		return el;
	}

	//
	public String toXmlString(Document doc) {
		return toXmlElement(doc).toString();
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayTitles() {
		return displayTitles;
	}

	public void setDisplayTitles(String displayTitles) {
		this.displayTitles = displayTitles;
	}

	public boolean isCalculateOnSet() {
		return calculateOnSet;
	}

	public void setCalculateOnSet(boolean calculateOnSet) {
		this.calculateOnSet = calculateOnSet;
	}

	public boolean isDisplayValueIgnored() {
		return displayValueIgnored;
	}

	public void setDisplayValueIgnored(boolean displayValueIgnored) {
		this.displayValueIgnored = displayValueIgnored;
	}

	public boolean isClearComboValue() {
		return clearComboValue;
	}

	public void setClearComboValue(boolean clearComboValue) {
		this.clearComboValue = clearComboValue;
	}

	public boolean displayValueApplied() {
		return fieldType == FT_IMAGE_ITEM || fieldType == FT_MAP_ITEM;
	}

	public boolean isNoUnknownValueComboValue() {
		return noUnknownValueComboValue;
	}

	public void setNoUnknownValueComboValue(boolean noUnknownValueComboValue) {
		this.noUnknownValueComboValue = noUnknownValueComboValue;
	}

}
