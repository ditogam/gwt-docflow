package com.docflow.client.components.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.client.components.CurrentTimeItem;
import com.docflow.client.components.common.u.ChkGridItem;
import com.docflow.client.components.common.u.ImageItem;
import com.docflow.client.components.common.u.MapButton;
import com.docflow.client.components.docflow.WDFSearchCustomers;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon.Picker;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class FormDefinitionPanel extends VLayout {

	public static void setSelectItems(FormItem si,
			ArrayList<ClSelectionItem> items) {
		if (items == null) {
			items = new ArrayList<ClSelectionItem>();
		}
		if (si instanceof ChkGridItem) {
			((ChkGridItem) si).setItems(items);
			return;
		}
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (ClSelectionItem item : items) {
			map.put(item.getId() + "", item.getValue());
		}
		si.setValueMap(map);
	}

	@SuppressWarnings("deprecation")
	public static void trimDate(Date dt) {
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(0);

	}

	private HashMap<String, FieldDefinitionItem> formitemMap;
	private FieldDefinitionListValue listValue;

	private boolean settingMyself = false;

	protected DocType docType;
	private DocPanelSettingDataComplete dataComplete;

	public FormDefinitionPanel(FormDefinition definition,
			FieldDefinitionListValue listValue, DocType docType,
			DocPanelSettingDataComplete dataComplete) {
		super();
		this.listValue = listValue;
		this.dataComplete = dataComplete;
		this.docType = docType;
		this.setWidth100();
		this.setHeight100();
		ArrayList<FormGroup> formGroups = definition.getFormGroups();
		formitemMap = new HashMap<String, FieldDefinitionItem>();
		ArrayList<MyDynamicForm> dynFields = new ArrayList<MyDynamicForm>();
		for (FormGroup formGroup : formGroups) {
			ArrayList<FieldDefinition> fields = formGroup.getFieldDefinitions();
			MyDynamicForm dynamicForm = new MyDynamicForm();
			dynamicForm.setNewLine(formGroup.getNewLine() == 1);
			ArrayList<FormItem> items = new ArrayList<FormItem>();
			for (FieldDefinition fieldDefinition : fields) {
				FieldDefinitionItem fi = createField(fieldDefinition);
				if (fi != null) {
					items.add(fi.getFormItem());
					if (fieldDefinition.isFieldReadOnly())
						fi.getFormItem().setCanEdit(false);
					formitemMap.put(fieldDefinition.getFieldName(), fi);
				}
			}

			//
			dynamicForm.setAlign(Alignment.LEFT);
			switch (formGroup.getLabelOrientation()) {
			case FormGroup.FTORIENTATION_LEFT:
				dynamicForm.setTitleOrientation(TitleOrientation.LEFT);
				break;
			case FormGroup.FTORIENTATION_TOP:
				dynamicForm.setTitleOrientation(TitleOrientation.TOP);
				break;
			case FormGroup.FTORIENTATION_RIGHT:
				dynamicForm.setTitleOrientation(TitleOrientation.RIGHT);
				break;
			default:
				break;
			}

			int numofColumns = 0;
			if (definition.isHorizontal()) {
				numofColumns = formGroup.getNumofColumns() > 0 ? formGroup
						.getNumofColumns() : 1;
			} else {

				numofColumns = formGroup.getNumofColumns() > 0 ? formGroup
						.getNumofColumns() : items.size();
			}
			numofColumns = numofColumns
					* (formGroup.getLabelOrientation() == FormGroup.FTORIENTATION_TOP ? 1
							: 2);
			dynamicForm.setNumCols(numofColumns);

			if (formGroup.getGroupHeight() != null
					&& !formGroup.getGroupHeight().isEmpty()) {
				dynamicForm.setHeight(formGroup.getGroupHeight());
			}

			if (formGroup.getGroupWidth() != null
					&& !formGroup.getGroupWidth().isEmpty()) {
				dynamicForm.setWidth(formGroup.getGroupWidth());
			}
			dynamicForm.setFields(items.toArray(new FormItem[] {}));
			dynFields.add(dynamicForm);
			if (formGroup.getGroupTitle() != null
					&& formGroup.getGroupTitle().length() > 0) {
				dynamicForm.setIsGroup(true);
				dynamicForm.setGroupTitle(DocFlow.getCaption(
						formGroup.getFieldCaptionId(),
						formGroup.getGroupTitle()));
			}
			// this.addMember(dynamicForm);
			listValue.setFieldDefinitionListValue(formitemMap, this);

		}
		Layout container = this;
		if (definition.isHorizontal()) {
			container = createNewPanel();
		}

		for (MyDynamicForm dynamicForm : dynFields) {
			if (dynamicForm.isNewLine()) {
				container = createNewPanel();
			}
			container.addMember(dynamicForm);
		}

		Set<String> fieldKeys = formitemMap.keySet();

		for (String key : fieldKeys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			if (fieldDef.getCalculatorClass() != null) {
				final ICalculatorClass classCalculator = listValue
						.getCalculetorClass(fieldDef.getCalculatorClass());
				if (classCalculator != null) {
					item.getFormItem().addChangedHandler(new ChangedHandler() {
						@Override
						public void onChanged(ChangedEvent event) {
							classCalculator.calculate(formitemMap, item);

						}
					});
				}
			}
			if (fieldDef.isClearComboValue()) {
				PickerIcon eraserValuePicker = new PickerIcon(new Picker(
						"eraser.png"), new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						item.getFormItem().setValue((Object) null);
						fireChangedEvent(item.getFormItem());
					}
				});
				item.getFormItem().setIcons(eraserValuePicker);
			}

			if ((fieldDef.getFieldType() == FieldDefinition.FT_COMBO || fieldDef
					.getFieldType() == FieldDefinition.FT_SELECTION)) {
				if (fieldDef.getFieldSelectMethode() > 0) {
					String depFields = fieldDef.getDependencyFields();
					depFields = depFields == null ? "" : depFields;
					final String[] depFieldNames = depFields.split(";");
					final String parent_type = fieldDef.getFieldSelectMethode()
							+ "";
					if (depFieldNames.length > 0
							&& depFieldNames[0].length() > 0) {
						item.getFormItem().addChangedHandler(
								new ChangedHandler() {
									@Override
									public void onChanged(ChangedEvent event) {
										if (settingMyself)
											return;
										try {
											settingMyself = true;
											comboValueChanged(depFieldNames,
													event.getValue(),
													parent_type);
										} finally {
											settingMyself = false;
										}

									}
								});
					}
				}
			}
		}

	}

	private native void fireChangedEvent(FormItem fi) /*-{
		var obj = null;
		obj = fi.@com.smartgwt.client.core.DataClass::getJsObj()();
		var selfJ = fi;
		if (obj.getValue === undefined) {
			return;
		}
		var param = {
			"form" : obj.form,
			"item" : obj,
			"value" : obj.getValue()
		};
		var event = @com.smartgwt.client.widgets.form.fields.events.ChangedEvent::new(Lcom/google/gwt/core/client/JavaScriptObject;)(param);
		selfJ.@com.smartgwt.client.core.DataClass::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(event);
	}-*/;

	private Layout createNewPanel() {
		Layout container;
		HLayout hl = new HLayout();
		hl.setWidth100();
		hl.setAutoHeight();
		hl.setMembersMargin(5);
		this.addMember(hl);
		container = hl;
		return container;
	}

	private void comboValueChanged(String[] depFieldNames,
			Object selectedvalue, String parent_type) {
		HashMap<String, String> listSqls = new HashMap<String, String>();

		for (int i = 0; i < depFieldNames.length; i++) {
			String key = depFieldNames[i];
			FieldDefinitionItem item = formitemMap.get(key);
			if (item == null)
				continue;
			FieldDefinition fieldDef = item.getFieldDef();
			if (fieldDef == null)
				continue;
			FormItem formItem = item.getFormItem();
			if (formItem == null)
				continue;
			// result.put(key, );
			formItem.setValue((String) null);
			setSelectItems(formItem, new ArrayList<ClSelectionItem>());
			if ((fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION || fieldDef
					.getFieldType() == FieldDefinition.FT_CHK_GRID)
					&& fieldDef.getFieldSelectMethode() > 0) {

				String depFields = fieldDef.getDependencyFields();
				depFields = depFields == null ? "" : depFields;
				final String[] mydepFieldNames = depFields.split(";");
				final String myparent_type = fieldDef.getFieldSelectMethode()
						+ "";
				if (mydepFieldNames.length > 0
						&& mydepFieldNames[0].length() > 0) {
					comboValueChanged(mydepFieldNames, null, myparent_type);
				}
				if (selectedvalue == null)
					continue;
				String value = fieldDef.getFieldSelectMethode() + "_"
						+ parent_type + "_" + selectedvalue.toString();
				listSqls.put(key, value);

			}
		}
		getRemouteValues(null, -1, listSqls, false);
	}

	private FieldDefinitionItem createField(FieldDefinition field) {
		FormItem fi = null;
		switch (field.getFieldType()) {
		case FieldDefinition.FT_STRING:
			fi = new TextItem();
			break;
		case FieldDefinition.FT_INTEGER:
			fi = new IntegerItem();
			break;
		case FieldDefinition.FT_DOUBLE:
			fi = new FloatItem();
			break;
		case FieldDefinition.FT_BOOLEAN:
			fi = new CheckboxItem();
			break;
		case FieldDefinition.FT_TEXTAREA:
			fi = new TextAreaItem();
			// fi.setWidth("10000px");
			break;
		case FieldDefinition.FT_STATICTEXT:
			fi = new StaticTextItem();
			break;
		case FieldDefinition.FT_COMBO:
			fi = new ComboBoxItem();
			if (field.isNoUnknownValueComboValue())
				((ComboBoxItem) fi).setAddUnknownValues(!field
						.isNoUnknownValueComboValue());
			break;
		case FieldDefinition.FT_SELECTION:
			fi = new SelectItem();
			if (field.isNoUnknownValueComboValue())
				((SelectItem) fi).setAddUnknownValues(!field
						.isNoUnknownValueComboValue());
			break;
		case FieldDefinition.FT_DATE:
			fi = new CurrentTimeItem();
			break;
		case FieldDefinition.FT_CHK_GRID:
			fi = new ChkGridItem();
			break;
		case FieldDefinition.FT_MAP_ITEM:
			fi = new MapButton(this, field);
			break;
		case FieldDefinition.FT_IMAGE_ITEM:
			fi = new ImageItem(this, field);
			break;
		case FieldDefinition.FT_CUSTOM:
			fi = new CanvasItem();
			break;
		default:
			break;
		}
		if (fi == null) {
			return null;
		}
		fi.setTitle(DocFlow.getCaption(field.getFieldCaptionId(),
				field.getFieldCaption()));
		if (field.getFieldWidth() != null && !field.getFieldWidth().isEmpty()) {
			fi.setWidth(field.getFieldWidth());
		}
		if (field.getFieldHeight() != null && !field.getFieldHeight().isEmpty()) {
			fi.setHeight(field.getFieldHeight());
		}
		if (field.isFieldReadOnly()) {
			if (field.getFieldType() == FieldDefinition.FT_COMBO
					|| field.getFieldType() == FieldDefinition.FT_SELECTION
					|| field.getFieldType() == FieldDefinition.FT_DATE)
				fi.setCanEdit(false);
			else
				fi.setCanEdit(false);
		}
		setDefaultValue(field, fi);
		if (field.isHidden())
			fi.setVisible(false);
		if (field.isDisabled()) {
			fi.setDisabled(true);
		}
		return new FieldDefinitionItem(field, fi);
	}

	public HashMap<String, Object> getData() {
		HashMap<String, Object> values = new HashMap<String, Object>();
		Set<String> keys = formitemMap.keySet();
		for (String key : keys) {
			FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			Object value = item.getFormItem().getValue();
			if (fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION
					|| fieldDef.getFieldType() == FieldDefinition.FT_CHK_GRID
					|| fieldDef.getFieldType() == FieldDefinition.FT_MAP_ITEM
					|| fieldDef.getFieldType() == FieldDefinition.FT_IMAGE_ITEM
					|| fieldDef.getFieldType() == FieldDefinition.FT_CUSTOM) {
				if (value != null) {
					if (!fieldDef.isDisplayValueIgnored())
						value = new String[] { value.toString(),
								item.getFormItem().getDisplayValue() };
				}
			}

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE) {
				if (value != null) {
					Date dt = (Date) value;
					value = new String[] { dt.getTime() + "", dt.toString() };
				}
			}
			if (fieldDef.getFieldType() == FieldDefinition.FT_BOOLEAN) {
				if (value != null) {
					Boolean dt = false;
					try {
						if (value instanceof Boolean)
							dt = (Boolean) value;
						else
							dt = (Boolean) (Integer.parseInt(value.toString()) == 1);
					} catch (Exception e) {
						// TODO: handle exception
					}
					value = new String[] { (dt.booleanValue() ? 1 : 0) + "",
							dt.toString() };
				}
			}
			values.put(key, value);
		}
		return values;
	}

	public int getDelayInterval() {
		Set<String> fieldKeys = formitemMap.keySet();

		for (String key : fieldKeys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			final FormItem formItem = item.getFormItem();

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE
					&& formItem instanceof CurrentTimeItem) {
				int valid = validateDate(formItem, fieldDef);
				if (valid != 0 && !fieldDef.isDonotcheckForDate())
					return valid;
			}

		}
		return 0;
	}

	public HashMap<String, FieldDefinitionItem> getFormitemMap() {
		return formitemMap;
	}

	private void getRemouteValues(HashMap<String, String> values,
			long customer_id, HashMap<String, String> listSqls,
			final boolean datasettings) {
		if (datasettings && listSqls.isEmpty()) {
			setData(values);
			return;
		}
		final HashMap<String, String> valuesFinal = values;
		SplashDialog.showSplash();
		DocFlow.docFlowService
				.getListTypesForDocument(
						listSqls,
						customer_id,
						new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

							@Override
							public void onFailure(Throwable caught) {
								SplashDialog.hideSplash();

							}

							@Override
							public void onSuccess(
									HashMap<String, ArrayList<ClSelectionItem>> result) {
								setFieldLists(result, valuesFinal, datasettings);
								SplashDialog.hideSplash();

							}
						});
	}

	public void setCalculatorProceed() {
		Set<String> fieldKeys = formitemMap.keySet();

		for (String key : fieldKeys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			if (!fieldDef.isCalculatorProceed())
				continue;
			if (fieldDef.getCalculatorClass() != null) {
				final ICalculatorClass classCalculator = listValue
						.getCalculetorClass(fieldDef.getCalculatorClass());
				if (classCalculator != null) {
					classCalculator.calculate(formitemMap, item);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setData(HashMap<String, String> values) {
		Set<String> keys = formitemMap.keySet();
		if (values == null) {
			values = new HashMap<String, String>();
		}
		for (String key : keys) {
			Object obj = values == null ? null : values.get(key);
			FieldDefinitionItem item = formitemMap.get(key);
			if (item == null)
				continue;
			if (!item.getFieldDef().isFieldReadOnly())
				item.getFormItem().setCanEdit(true);// .setAttribute("readOnly",
													// false);
			if (item.getFormItem() instanceof CurrentTimeItem) {
				if (obj != null) {
					try {
						Long lng = Long.parseLong(obj.toString());
						item.getFormItem().setValue(new Date(lng.longValue()));
					} catch (Exception e) {
						try {
							Date dt = new Date(obj.toString());
							item.getFormItem().setValue(dt);
						} catch (Exception e2) {
							item.getFormItem().setValue(
									DocFlow.getCurrentDate());
						}
					}
				} else
					item.getFormItem().setValue(DocFlow.getCurrentDate());
			}
			if (item.getFormItem() instanceof CheckboxItem) {
				try {
					Integer lng = Integer.parseInt(obj.toString());
					item.getFormItem().setValue(lng.intValue() == 1);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			Object val = obj;
			if (val == null && item.getFieldDef().getDefaultValue() != null
					&& item.getFieldDef().getDefaultValue().trim().length() > 0) {
				val = item.getFieldDef().getDefaultValue().trim();
				if (val.toString().startsWith("$"))
					val = null;
			}
			if (item.getFieldDef().getFieldType() == FieldDefinition.FT_DATE
					&& item.getFormItem() instanceof CurrentTimeItem
					&& val != null) {
				try {
					Long lng = Long.parseLong(val.toString());
					val = new Date(lng);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (item.getFieldDef().getFieldType() == FieldDefinition.FT_BOOLEAN
					&& item.getFormItem() instanceof CheckboxItem) {
				try {
					if (val == null)
						val = new Boolean(false);
					else {
						Long lng = Long.parseLong(val.toString());
						val = new Boolean(lng == 1);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			item.getFormItem().setValue(val);
			// if (item.getFieldDef().isCalculateOnSet())
			// setCalculatorCalculate(item);

		}
		if (dataComplete != null && newData) {
			newData = false;
			dataComplete.settingDataComplete();
		}
	}

	private boolean newData = false;

	public void setData(HashMap<String, String> values, long customer_id,
			HashMap<String, String> displayValues) {
		if (displayValues == null)
			displayValues = new HashMap<String, String>();
		newData = true;
		Set<String> fieldKeys = formitemMap.keySet();
		for (String key : fieldKeys) {
			FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			if (fieldDef.getFieldType() == FieldDefinition.FT_IMAGE_ITEM
					&& displayValues.containsKey(key))
				((ImageItem) item.getFormItem()).setDisplayValue(displayValues
						.get(key));

			if ((fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION || fieldDef
						.getFieldType() == FieldDefinition.FT_CHK_GRID)) {
				String val = fieldDef.getDefaultValue();
				if (val != null && val.trim().length() > 0) {
					val = val.trim();
					if (DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS")
							&& (val.toLowerCase().equals(
									"$regionId".toLowerCase()) || val
									.toLowerCase().equals(
											"$subregionId".toLowerCase()))) {
						if (values.get(key) != null)
							continue;
					}
					if (val.toLowerCase().equals("$regionId".toLowerCase())
							&& DocFlow.user_obj.getUser().getRegionid() >= 0) {
						values.put(key, ""
								+ DocFlow.user_obj.getUser().getRegionid());
						if (!item.getFieldDef().isFieldReadOnly())
							item.getFormItem()
									.setDisabled(
											!DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS"));
					}
					if (val.toLowerCase().equals("$subregionId".toLowerCase())
							&& DocFlow.user_obj.getUser().getSubregionid() >= 0) {
						values.put(key, ""
								+ DocFlow.user_obj.getUser().getSubregionid());
						if (!item.getFieldDef().isFieldReadOnly())
							item.getFormItem()
									.setDisabled(
											!DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS"));
					}
				}
			}
			setDefaultValue(fieldDef, item.getFormItem());
		}

		HashMap<String, String> listSqls = new HashMap<String, String>();
		for (String key : fieldKeys) {
			FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();

			if (fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION
					|| fieldDef.getFieldType() == FieldDefinition.FT_CHK_GRID) {
				String value = "";
				if (fieldDef.getFieldSelectMethode() > 0) {
					value = fieldDef.getFieldSelectMethode() + "";
					String parentField = fieldDef.getParentField();
					parentField = parentField == null ? "" : parentField;
					if (parentField.length() != 0) {
						FieldDefinitionItem pItem = formitemMap
								.get(parentField);
						String parent_id = values.get(parentField);
						if (parent_id == null || parent_id.trim().length() == 0) {
							parent_id = "-1";

						}
						parent_id = parent_id.trim();

						if (pItem != null && pItem.getFieldDef() != null)
							value += "_"
									+ pItem.getFieldDef()
											.getFieldSelectMethode() + "_"
									+ parent_id;
					}

				} else
					value = fieldDef.getFieldSelectionSQL();
				if (value == null || value.length() < 1)
					continue;
				listSqls.put(key, value);

			}
		}

		getRemouteValues(values, customer_id, listSqls, true);

	}

	private void setDefaultValue(FieldDefinition field, FormItem fi) {
		if (field.getDefaultValue() != null
				&& field.getDefaultValue().trim().length() > 0) {
			String defValue = field.getDefaultValue().trim();
			if (defValue.toLowerCase().equals("$regionId".toLowerCase())) {
				defValue = null;
			}
			if (defValue != null
					&& defValue.toLowerCase().equals(
							"$subregionId".toLowerCase())) {
				defValue = null;
			}
			fi.setValue(defValue);
		}
	}

	// private void setCalculatorCalculate(FieldDefinitionItem item) {
	// FieldDefinition fieldDef = item.getFieldDef();
	// if (fieldDef.getCalculatorClass() != null) {
	// final ICalculatorClass classCalculator = listValue
	// .getCalculetorClass(fieldDef.getCalculatorClass());
	// if (classCalculator != null) {
	// classCalculator.calculate(formitemMap, item);
	// }
	// }
	// }

	private void setFieldLists(
			HashMap<String, ArrayList<ClSelectionItem>> result,
			HashMap<String, String> values, boolean datasettings) {
		Set<String> fieldKeys = result.keySet();
		for (String key : fieldKeys) {
			FieldDefinitionItem item = formitemMap.get(key);
			if (item == null)
				continue;
			FormItem formItem = item.getFormItem();
			if (formItem == null
					&& !(formItem instanceof ComboBoxItem
							|| formItem instanceof SelectItem || formItem instanceof ChkGridItem))
				continue;

			ArrayList<ClSelectionItem> items = result.get(key);

			setSelectItems(formItem, items);
			String val = null;

			if (item.getFieldDef().getDefaultValue() != null
					&& item.getFieldDef().getDefaultValue().trim().length() > 0) {
				val = item.getFieldDef().getDefaultValue().trim();
			}
			formItem.setValue(val);
			// if (item.getFieldDef().isCalculateOnSet())
			// setCalculatorCalculate(item);
		}
		if (datasettings)
			setData(values);

	}

	public void setFormitemMap(HashMap<String, FieldDefinitionItem> formitemMap) {
		this.formitemMap = formitemMap;
	}

	public long trimDate(long dtLong) {
		String s = dtLong + "";
		s = s.substring(0, s.length() - 4);
		s = s + "0000";
		dtLong = Long.parseLong(s);
		return dtLong;
	}

	public boolean validate() {
		return listValue.validate();
	}

	public int validateDate(FormItem formItem) {
		Date dt = ((CurrentTimeItem) formItem).getValueAsDate();
		if (dt == null)
			dt = DocFlow.getCurrentDate();
		trimDate(dt);
		Date now = DocFlow.getCurrentDate();
		trimDate(now);
		long ldt = dt.getTime();
		long lnow = now.getTime();

		ldt = trimDate(ldt);
		lnow = trimDate(lnow);
		if (ldt > lnow)
			return -1;

		String dayadds[] = docType.getDelayinterval().split(",");
		int warring = Integer.parseInt(dayadds[0]);
		int error = Integer.parseInt(dayadds[1]);
		int fatalerror = Integer.parseInt(dayadds[2]);
		long dayms = 1000 * 60 * 60 * 24;
		long errordate = lnow - (dayms * error);
		long fatalerrordate = lnow - (dayms * fatalerror);

		long warringdate = lnow - (dayms * warring);
		if (ldt <= fatalerrordate)
			return -2;
		if (ldt <= errordate)
			return 2;
		if (ldt <= warringdate)
			return 1;
		return 0;
	}

	public int validateDate(FormItem formItem, FieldDefinition fieldDef) {
		if (docType == null)
			return 0;
		if (!fieldDef.getFieldName().equals(docType.getDatefield()))
			return 0;
		return validateDate(formItem);
	}

	public void setDataComplete(DocPanelSettingDataComplete dataComplete) {
		this.dataComplete = dataComplete;
	}

	public void activate() {
		listValue.activate();
	}

}
