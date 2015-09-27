package com.docflowdroid.comp.ds;

import android.content.Context;

import com.common.shared.ds.DsField;
import com.common.shared.ds.DsFieldType;

public class ListGridField {
	private String fieldName;
	private String fieldTitle;
	private Long fieldWidth;
	private String type;
	private boolean fieldVisible;
	private Boolean showFilter;
	private boolean wrapText;
	private LGFieldView fieldView = null;
	private boolean rowNum;

	public ListGridField(DsField def) {
		this(def.getName(), def.getTitle(), def.getLength());
		this.setType(def.getType());
	}

	public ListGridField(String fieldName) {
		this(fieldName, null);
	}

	public ListGridField(String fieldName, String fieldTitle) {
		this(fieldName, fieldTitle, null);
	}

	public ListGridField(String fieldName, String fieldTitle, Long fieldWidth) {
		this(fieldName, fieldTitle, fieldWidth, null);
	}

	public ListGridField(String fieldName, String fieldTitle, Long fieldWidth,
			Boolean showFilter) {
		super();
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.fieldWidth = fieldWidth;
		this.showFilter = showFilter;
		fieldVisible = true;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldTitle() {
		return fieldTitle;
	}

	public void setFieldTitle(String fieldTitle) {
		this.fieldTitle = fieldTitle;
		refreshView();
	}

	public Long getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(Long fieldWidth) {
		this.fieldWidth = fieldWidth;
		refreshView();
	}

	public Boolean getShowFilter() {
		return showFilter;
	}

	public void setShowFilter(Boolean showFilter) {
		this.showFilter = showFilter;
		refreshView();
	}

	public boolean isFieldVisible() {
		return fieldVisible;
	}

	public void setFieldVisible(boolean fieldVisible) {
		this.fieldVisible = fieldVisible;
		refreshView();
	}

	public boolean isWrapText() {
		return wrapText;
	}

	public void setWrapText(boolean wrapText) {
		this.wrapText = wrapText;
		refreshView();
	}

	public String getType() {
		if (type == null || type.trim().isEmpty())
			type = DsFieldType.TEXT.getValue();
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void creatViewTitle(Context context, boolean showFilterHeader) {
		if (fieldView == null)
			fieldView = new LGFieldView(context, this, showFilterHeader);
		fieldView.refreshView();
	}

	public void refreshView() {
		if (fieldView != null)
			fieldView.refreshView();
	}

	public LGFieldView getFieldView() {
		return fieldView;
	}

	public boolean isRowNum() {
		return rowNum;
	}

	public void setRowNum(boolean rowNum) {
		this.rowNum = rowNum;
	}
}