package com.docflowdroid.comp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;

import com.common.shared.ClSelectionItem;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocumentLong;
import com.docflowdroid.ASimpleCalculator;
import com.docflowdroid.DocFlow;
import com.docflowdroid.DocFlowCommon;
import com.docflowdroid.MyDynamicForm;
import com.docflowdroid.common.FieldDefinitionItem;
import com.docflowdroid.common.comp.IFormDefinitionPanel;
import com.docflowdroid.common.comp.IFormItem;
import com.docflowdroid.comp.adapter.IDValueAdapter;
import com.docflowdroid.helper.ICameraResult;

@SuppressLint("DefaultLocale")
public class FormDefinitionPanel extends LinearLayout implements
		IFormDefinitionPanel {
	protected DocType docType;
	private HashMap<String, FieldDefinitionItem> formitemMap;
	private DocumentLong document;
	private HashMap<String, String> displayValues;
	protected Activity context;

	protected ICameraResult currentCameraResult;

	public FormDefinitionPanel(Activity context, FormDefinition definition,
			DocType docType, DocumentLong document) {
		this(context, null, definition, docType, document);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		// setGravity(Gravity.CENTER||Gravity.LEFT);
		// TODO Auto-generated constructor stub
	}

	private boolean settingMyself = false;

	private ASimpleCalculator calc = null;
	private HashMap<String, Method> methodes = new HashMap<String, Method>();

	private Method getMethode(String method_name) {
		Method m = methodes.get(method_name);
		if (m != null)
			return m;
		createCalculator();
		if (calc == null)
			return null;
		try {
			m = calc.getClass().getMethod(method_name,
					FieldDefinitionItem.class);
			methodes.put(method_name, m);
			return m;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createCalculator() {
		if (calc != null)
			return;

		try {
			Constructor<?> constr = DocFlow.calculator.getConstructor(
					formitemMap.getClass(), IFormDefinitionPanel.class);
			calc = (ASimpleCalculator) constr.newInstance(formitemMap, this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public FormDefinitionPanel(final Activity context, AttributeSet attrs,
			FormDefinition definition, DocType docType, DocumentLong document) {

		super(context, attrs);
		this.document = document;
		this.docType = docType;
		this.context = context;
		ArrayList<FormGroup> formGroups = definition.getFormGroups();
		formitemMap = new HashMap<String, FieldDefinitionItem>();
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		ArrayList<MyDynamicForm> dynFields = new ArrayList<MyDynamicForm>();
		for (FormGroup formGroup : formGroups) {
			ArrayList<FieldDefinition> fields = formGroup.getFieldDefinitions();
			MyDynamicForm dynamicForm = new MyDynamicForm(context);
			dynamicForm.setNewLine(formGroup.getNewLine() == 1);
			ArrayList<View> items = new ArrayList<View>();
			for (FieldDefinition fieldDefinition : fields) {
				FieldDefinitionItem fi = createField(context, fieldDefinition);
				if (fi != null) {
					items.add((FormItem) fi.getFormItem());
					if (fieldDefinition.isFieldReadOnly())
						fi.getFormItem().setCanEdit(false);
					formitemMap.put(fieldDefinition.getFieldName(), fi);
				}
			}

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

			if (formGroup.getGroupTitle() != null
					&& formGroup.getGroupTitle().length() > 0) {
				dynamicForm.setIsGroup(true);
				dynamicForm.setGroupTitle(DocFlow.getCaption(
						formGroup.getFieldCaptionId(),
						formGroup.getGroupTitle()));
			}
			dynamicForm.setFields(items.toArray(new FormItem[] {}));
			dynFields.add(dynamicForm);
			// this.addMember(dynamicForm);
			// listValue.setFieldDefinitionListValue(formitemMap, this);
		}

		LinearLayout container = this;

		if (definition.isHorizontal()) {
			container = createNewPanel(context);
		}

		for (MyDynamicForm dynamicForm : dynFields) {
			if (dynamicForm.isNewLine()) {
				container = createNewPanel(context);
			}
			container.addView(dynamicForm);
		}

		Set<String> keys = formitemMap.keySet();

		for (String key : keys) {

			@SuppressWarnings("unused")
			IFormItem item = formitemMap.get(key).getFormItem();

		}
		setAddressFieldsDisabled();
	}

	private int item_id = 10000;

	private FieldDefinitionItem createField(Context context,
			FieldDefinition field) {
		FormItem fi = null;
		switch (field.getFieldType()) {
		case FieldDefinition.FT_STRING:
			fi = new TextItem(context);
			break;
		case FieldDefinition.FT_INTEGER:
			fi = new IntegerItem(context);
			break;
		case FieldDefinition.FT_DOUBLE:
			fi = new FloatItem(context);
			break;
		case FieldDefinition.FT_BOOLEAN:
			fi = new CheckboxItem(context);
			break;
		case FieldDefinition.FT_TEXTAREA:
			fi = new TextAreaItem(context);
			// fi.setWidth("10000px");
			break;
		case FieldDefinition.FT_STATICTEXT:
			fi = new StaticTextItem(context);
			break;
		case FieldDefinition.FT_COMBO:
			fi = new ComboBoxItem(context);
			break;
		case FieldDefinition.FT_SELECTION:
			fi = new SelectItem(context);
			break;
		case FieldDefinition.FT_DATE:
			fi = new CurrentTimeItem(context);
			break;
		case FieldDefinition.FT_CHK_GRID:
			fi = new ChkGridItem(context);
			break;
		case FieldDefinition.FT_MAP_ITEM:
			fi = new MapButton(context);
			break;
		case FieldDefinition.FT_IMAGE_ITEM:
			fi = new ImageItem(context);
			break;
		default:
			break;
		}
		if (fi == null) {
			return null;
		}
		fi.setField(field);
		fi.setPanel(this);
		fi.setTitle(DocFlow.getCaption(field.getFieldCaptionId(),
				field.getFieldCaption()));
		if (field.getFieldWidth() != null && !field.getFieldWidth().isEmpty()) {
			fi.setWidth(field.getFieldWidth());
		}
		if (field.getFieldHeight() != null && !field.getFieldHeight().isEmpty()) {
			fi.setMinimumHeight(field.getFieldHeight());
		}
		if (field.isFieldReadOnly()) {
			if (field.getFieldType() == FieldDefinition.FT_COMBO
					|| field.getFieldType() == FieldDefinition.FT_SELECTION
					|| field.getFieldType() == FieldDefinition.FT_DATE)
				fi.setCanEdit(false);
			else
				fi.setCanEdit(false);
		}
		// field.setDefaultValue("sdfsdfsd");
		setDefaultValue(field, fi);
		if (field.isHidden())
			fi.setVisibility(View.GONE);
		if (field.isDisabled()) {
			fi.setEnabled(false);
		}

		fi.setId(item_id);
		fi.setNextFocusDownId(item_id + 1);
		item_id++;

		return new FieldDefinitionItem(field, fi);
	}

	@SuppressLint("DefaultLocale")
	private void setDefaultValue(FieldDefinition field, IFormItem fi) {
		if (field.getDefaultValue() != null
				&& field.getDefaultValue().trim().length() > 0) {
			String defValue = field.getDefaultValue().trim();
			if (defValue.toLowerCase(Locale.ENGLISH).equals(
					"$regionId".toLowerCase(Locale.ENGLISH))) {
				defValue = null;
			}
			if (defValue != null
					&& defValue.toLowerCase(Locale.ENGLISH).equals(
							"$subregionId".toLowerCase(Locale.ENGLISH))) {
				defValue = null;
			}
			fi.setValue(defValue);
		}
	}

	private LinearLayout createNewPanel(Context context) {
		LinearLayout container;
		LinearLayout hl = new LinearLayout(context);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		this.addView(hl);
		container = hl;
		return container;
	}

	public void setRegionPermitions(HashMap<String, String> values,
			long customer_id, HashMap<String, String> displayValues) {
		this.displayValues = displayValues;
		Set<String> fieldKeys = formitemMap.keySet();
		for (String key : fieldKeys) {
			FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
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

		// setRemoteSqlOld(values, customer_id, fieldKeys);

	}

	public void setRemoteSqlOld(HashMap<String, String> values,
			long customer_id, Set<String> fieldKeys) {
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
					if (value.equals("10")) {
						System.out.println(111);
					}
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
						val = Boolean.valueOf(false);
					else {
						Long lng = Long.parseLong(val.toString());
						val = Boolean.valueOf(lng == 1);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			item.getFormItem().setValue(val);
			item.getFormItem().setInitialValue(val);
		}

		if (displayValues != null && !displayValues.isEmpty()) {
			keys = displayValues.keySet();
			for (String key : keys) {
				String value = displayValues.get(key);
				if (value == null)
					continue;
				FieldDefinitionItem item = formitemMap.get(key);
				if (item != null && item.getFormItem() != null)
					item.getFormItem().setDisplayValue(value);

			}
		}

		setCalculatorProceed();

		// FieldDefinitionItem item = formitemMap.get("testtttt!");
		// item.getFormItem().setValue("675431");
		// item.getFormItem().setDisplayValue("r_343514.jpg");

	}

	public static void setSelectItems(IFormItem si,
			ArrayList<ClSelectionItem> items) {
		if (items == null) {
			items = new ArrayList<ClSelectionItem>();
		}
		/*
		 * if (si instanceof ChkGridItem) { ((ChkGridItem) si).setItems(items);
		 * return; }
		 */
		HashMap<Long, String> map = new HashMap<Long, String>();
		for (ClSelectionItem item : items) {
			map.put(item.getId(), item.getValue());
		}
		si.setValueMap(map);
	}

	private void getRemouteValues(HashMap<String, String> values,
			long customer_id, HashMap<String, String> listSqls,
			final boolean datasettings) {
		if (datasettings && listSqls.isEmpty()) {
			setData(values);
			return;
		}
		final HashMap<String, String> valuesFinal = values;
		HashMap<String, ArrayList<ClSelectionItem>> result = new HashMap<String, ArrayList<ClSelectionItem>>();
		for (String key : listSqls.keySet()) {
			result.put(key, new ArrayList<ClSelectionItem>());
		}
		try {
			if (!listSqls.isEmpty())
				result = DocFlow.docFlowService.getListTypesForDocument(
						listSqls, customer_id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		setFieldLists(result, valuesFinal, datasettings);

	}

	public void setFieldLists(
			HashMap<String, ArrayList<ClSelectionItem>> result,
			HashMap<String, String> values, boolean datasettings) {
		Set<String> fieldKeys = result.keySet();
		for (String key : fieldKeys) {
			FieldDefinitionItem item = formitemMap.get(key);
			if (item == null)
				continue;
			IFormItem formItem = item.getFormItem();
			if (formItem == null
					&& !(formItem instanceof ComboBoxItem || formItem instanceof SelectItem /*
																							 * ||
																							 * formItem
																							 * instanceof
																							 * ChkGridItem
																							 */))
				continue;

			ArrayList<ClSelectionItem> items = null;
			try {
				items = result.get(key);
			} catch (Exception e) {
				// TODO: handle exception
			}

			setSelectItems(formItem, items);
			String val = null;

			if (item.getFieldDef().getDefaultValue() != null
					&& item.getFieldDef().getDefaultValue().trim().length() > 0) {
				val = item.getFieldDef().getDefaultValue().trim();
			}
			formItem.setValue(val);
			formItem.setInitialValue(val);
			// if (item.getFieldDef().isCalculateOnSet())
			// setCalculatorCalculate(item);
		}
		if (datasettings)
			setData(values);
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
			IFormItem formItem = item.getFormItem();
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

	private void comboChanged(AdapterView<?> parent, long id) {
		if (settingMyself)
			return;
		try {
			settingMyself = true;
			IDValueAdapter adapter = null;
			try {
				adapter = (IDValueAdapter) parent.getAdapter();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (adapter == null)
				return;
			if (adapter.getField() == null)
				return;

			String depFields = adapter.getField().getDependencyFields();
			depFields = depFields == null ? "" : depFields;
			final String[] depFieldNames = depFields.split(";");
			final String parent_type = adapter.getField()
					.getFieldSelectMethode() + "";
			if (parent_type.equals("1"))
				System.out.println();
			comboValueChanged(depFieldNames, id, parent_type);
		} finally {
			settingMyself = false;
		}
	}

	public static void trimDate(Date dt) {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(dt.getTime());
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		dt.setTime(c.getTimeInMillis());

	}

	public long trimDate(long dtLong) {
		String s = dtLong + "";
		s = s.substring(0, s.length() - 4);
		s = s + "0000";
		dtLong = Long.parseLong(s);
		return dtLong;
	}

	public boolean validate() {
		return calc.validate();
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
					|| fieldDef.getFieldType() == FieldDefinition.FT_IMAGE_ITEM) {
				if (value != null) {
					if (!fieldDef.isDisplayValueIgnored())
						value = new String[] { value.toString(),
								item.getFormItem().getDisplayValue() };
				}
			}

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE) {
				if (value != null) {
					Date dt = (((CurrentTimeItem) item.getFormItem())
							.getValueAsDate());
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

	public int validateDate(IFormItem formItem) {
		Date dt = ((CurrentTimeItem) formItem).getValueAsDate();
		return validateDate(dt);
	}

	public int validateDate(Date dt) {
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

	public int getDelayInterval() {
		Set<String> fieldKeys = formitemMap.keySet();

		for (String key : fieldKeys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			final IFormItem formItem = item.getFormItem();

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE
					&& formItem instanceof CurrentTimeItem) {
				int valid = validateDate(formItem, fieldDef);
				if (valid != 0 && !fieldDef.isDonotcheckForDate())
					return valid;
			}

		}
		return 0;
	}

	public int validateDate(IFormItem formItem, FieldDefinition fieldDef) {
		if (!fieldDef.getFieldName().equals(docType.getDatefield()))
			return 0;
		return validateDate(formItem);
	}

	public HashMap<String, FieldDefinitionItem> getFormitemMap() {
		return formitemMap;
	}

	public DocumentLong getDocument() {
		return document;
	}

	public void setCalculatorProceed() {
		Set<String> fieldKeys = formitemMap.keySet();
		createCalculator();
		if (calc == null)
			return;
		for (String key : fieldKeys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			FieldDefinition fieldDef = item.getFieldDef();
			if (!fieldDef.isCalculatorProceed())
				continue;
			if (fieldDef.getCalculatorClass() != null) {
				@SuppressWarnings("unused")
				String field_name = fieldDef.getFieldName();
				final Method m = getMethode(fieldDef.getCalculatorClass());
				if (m != null) {
					try {
						m.invoke(calc, item);
					} catch (Throwable e) {
						// block
						e.printStackTrace();
					}

				}
			}
		}
		System.out.println();
	}

	public void setListeners() {
		Set<String> fieldKeys = formitemMap.keySet();
		createCalculator();
		if (calc != null) {
			for (String key : fieldKeys) {
				final FieldDefinitionItem item = formitemMap.get(key);
				FieldDefinition fieldDef = item.getFieldDef();
				if (fieldDef.getCalculatorClass() != null
						&& !fieldDef.getCalculatorClass().trim().isEmpty()) {

					final Method m = getMethode(fieldDef.getCalculatorClass());
					if (m != null)
						item.getFormItem().setOnItemSelectedListener(
								new OnItemSelectedListener() {
									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {
										try {
											m.invoke(calc, item);
										} catch (Throwable e) {
											e.printStackTrace();
										}

									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										// TODO Auto-generated method stub

									}
								});

				}
				if ((fieldDef.getFieldType() == FieldDefinition.FT_COMBO || fieldDef
						.getFieldType() == FieldDefinition.FT_SELECTION)) {
					if (fieldDef.getFieldSelectMethode() > 0) {
						String depFields = fieldDef.getDependencyFields();
						depFields = depFields == null ? "" : depFields;
						String[] depFieldNames = depFields.split(";");
						// String parent_type = fieldDef.getFieldSelectMethode()
						// + "";
						if (depFieldNames.length > 0
								&& depFieldNames[0].length() > 0) {
							item.getFormItem().setOnItemSelectedListener(
									new OnItemSelectedListener() {
										public void onItemSelected(
												AdapterView<?> parent,
												View view, int position, long id) {
											comboChanged(parent, id);
										}

										public void onNothingSelected(
												AdapterView<?> paramAdapterView) {

										}
									});

						}
					}
				}
			}
		}
	}

	public ICameraResult getCurrentCameraResult() {
		return currentCameraResult;
	}

	public void setCurrentCameraResult(ICameraResult currentCameraResult) {
		this.currentCameraResult = currentCameraResult;
	}

	public Activity getContextActivity() {
		return context;
	}

	private void disableField(String default_value, String... field_names) {

		Set<String> keys = formitemMap.keySet();
		for (String itemName : keys) {
			for (String field_name : field_names) {
				if (field_name.toLowerCase().equals(itemName.toLowerCase())) {
					FieldDefinitionItem field = formitemMap.get(itemName);
					field.getFormItem().setDisabled(true);
					break;
				}
			}
			FieldDefinitionItem field = formitemMap.get(itemName);
			if (default_value != null
					&& !default_value.trim().isEmpty()
					&& default_value
							.trim()
							.toLowerCase()
							.equals(field.getFieldDef().getDefaultValue()
									.trim().toLowerCase())) {
				field.getFormItem().setDisabled(true);

			}
		}

	}

	public void setAddressFieldsDisabled() {
		int region_id = DocFlowCommon.user_obj.getUser().getRegionid();
		int subregion_id = DocFlowCommon.user_obj.getUser().getSubregionid();
		if (region_id != -1)
			disableField("$regionId", "regionId", "regid", "ppcityid");
		if (subregion_id != -1)
			disableField("$subregionId", "subregionId", "raiid", "pcityid");
	}

}
