package com.docflowdroid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import android.util.Base64;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.common.shared.ClSelectionItem;
import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.docflow.shared.ClSelection;
import com.docflow.shared.common.FieldDefinition;
import com.docflowdroid.common.BooleanCallback;
import com.docflowdroid.common.FieldDefinitionItem;
import com.docflowdroid.common.SC;
import com.docflowdroid.common.comp.IComboBoxItem;
import com.docflowdroid.common.comp.ICurrentTimeItem;
import com.docflowdroid.common.comp.IFormDefinitionPanel;
import com.docflowdroid.common.comp.IFormItem;
import com.docflowdroid.common.ds.DsOperationResult;
import com.docflowdroid.common.listenerinvoker.AListenerMethode;
import com.docflowdroid.common.listenerinvoker.ClassCreator;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;

public abstract class ASimpleCalculator {
	protected HashMap<String, FieldDefinitionItem> formitemMap;
	protected IFormDefinitionPanel definitionPanel;
	final LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>();

	public ASimpleCalculator(HashMap<String, FieldDefinitionItem> formitemMap,
			IFormDefinitionPanel definitionPanel) {
		this.definitionPanel = definitionPanel;
		this.formitemMap = formitemMap;
		map.put(0L, "--");
	}

	public void fillCombo(FieldDefinitionItem item) {
		final LinkedHashMap<Long, String> _map = new LinkedHashMap<Long, String>(
				map);
		_map.put(-1L, "--");

		if (item.getFieldDef().getFieldName().equals("region")
				&& item.getFormItem() instanceof IComboBoxItem) {
			final IComboBoxItem reg = (IComboBoxItem) item.getFormItem();
			ArrayList<ClSelectionItem> result = new ArrayList<ClSelectionItem>();
			try {
				result = DocFlowCommon.docFlowService.getRegions();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (ClSelectionItem clSelectionItem : result) {
				_map.put(clSelectionItem.getId(), clSelectionItem.getValue());
			}
			((IComboBoxItem) reg).setValueMap(_map);

		}
		if ((item.getFieldDef().getFieldName().equals("city")
				|| item.getFieldDef().getFieldName().equals("street") || item
				.getFieldDef().getFieldName().equals("district"))
				&& item.getFormItem() instanceof IComboBoxItem) {
			final IComboBoxItem _addressItem = (IComboBoxItem) item
					.getFormItem();
			_addressItem.setValueMap(_map);
		}

	}

	protected FieldDefinitionItem getItem(String fieldName) {
		return formitemMap.get(fieldName);
	}

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

	public void additionalValidator(FieldDefinitionItem item) throws Exception {

	}

	protected double getDoubleValue(FieldDefinitionItem current) {
		if (current == null)
			return 0.0;
		double currentValue = 0.0;
		try {
			currentValue = Double.parseDouble(current.getFormItem().getValue()
					.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return currentValue;
	}

	protected int getIntValue(FieldDefinitionItem current) {

		return (int) getDoubleValue(current);
	}

	protected boolean getBooleanValue(FieldDefinitionItem current) {
		return Double.valueOf(getIntValue(current) + "").intValue() == 1;
	}

	protected void setOnFocusChangeValue(FieldDefinitionItem item,
			OnFocusChangeListener newList) {
		if ((item != null)) {
			IFormItem fi = (IFormItem) item.getFormItem();
			OnFocusChangeListener lis = item.getFormItem()
					.getOnFocusChangeListener();
			if (lis == null)
				fi.setOnFocusChangeListener(newList);

		}
	}

	protected void setFieldValue(final String fieldName, final Object value) {
		definitionPanel.getContextActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				FieldDefinitionItem fi = getItem(fieldName);
				if (fi != null)
					fi.getFormItem().setValue(value);

			}
		});

	}

	protected static DecimalFormat number_format = new DecimalFormat("####0.##");

	public Object formatDouble(double amounts) {
		if (amounts == 0.0)
			return null;
		return number_format.format(amounts);
	}

	public Object formatDouble(String amounts) {
		try {
			return formatDouble(Double.valueOf(amounts.trim()));
		} catch (Exception e) {
			return formatDouble(Double.valueOf(amounts.trim()));
		}

	}

	protected Object getFieldValue(String fieldName) {
		FieldDefinitionItem fi = getItem(fieldName);
		if (fi != null)
			return fi.getFormItem().getValue();
		return null;
	}

	public void setEditableAndRequered(String[] fields, boolean enable,
			String[] notrequered) {
		for (String f : fields) {
			final FieldDefinitionItem fdi = formitemMap.get(f);
			if (fdi == null)
				continue;
			final IFormItem formItem = fdi.getFormItem();
			if (formItem == null)
				continue;
			final FieldDefinition fieldDef = fdi.getFieldDef();
			if (fieldDef == null)
				continue;
			formItem.setDisabled(!enable);
			fieldDef.setRequaiered(enable);
			for (String r : notrequered) {
				if (r.equals(f))
					fieldDef.setRequaiered(false);
			}

		}
	}

	public void setFieldDefinitionListValue(
			HashMap<String, FieldDefinitionItem> formitemMap,
			IFormDefinitionPanel definitionPanel) {

		this.formitemMap = formitemMap;
		this.definitionPanel = definitionPanel;
		Set<String> keys = formitemMap.keySet();
		for (String key : keys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			final IFormItem formItem = item.getFormItem();
			FieldDefinition fieldDef = item.getFieldDef();
			if (fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION) {
				fillCombo(item);
			}
			formItem.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					valueChanged(parent, id, item);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

		}
	}

	public boolean validate() {
		Set<String> keys = formitemMap.keySet();
		for (String key : keys) {
			final FieldDefinitionItem item = formitemMap.get(key);
			final IFormItem formItem = item.getFormItem();
			final FieldDefinition fieldDef = item.getFieldDef();
			try {
				additionalValidator(item);
			} catch (Exception e) {
				return false;
			}
			if (getValue(item) == null && fieldDef.isRequaiered()) {
				SC.say(formItem.getContext(),
						"Error!!!",
						"Please "
								+ (((fieldDef.getFieldType() == FieldDefinition.FT_COMBO || fieldDef
										.getFieldType() == FieldDefinition.FT_SELECTION)) ? " select "
										: " enter ")
								+ fieldDef.getFieldCaption(),
						new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								formItem.requestFocusSelf();

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
						SC.say(DocFlowCommon.activity,
								"Error!!!",
								fieldDef.getFieldCaption()
										+ " must be at least "
										+ fieldDef.getMinValue() + "!!!",
								new BooleanCallback() {
									@Override
									public void execute(Boolean value) {
										formItem.requestFocusSelf();

									}
								});
						return false;
					}

					if (value > fieldDef.getMaxValue()) {
						SC.say(DocFlowCommon.activity, "Error!!!",
								fieldDef.getFieldCaption() + " must be less "
										+ fieldDef.getMinValue() + "!!!",
								new BooleanCallback() {

									@Override
									public void execute(Boolean value) {
										formItem.requestFocusSelf();

									}
								});
						return false;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (fieldDef.getFieldType() == FieldDefinition.FT_DATE
					&& formItem instanceof ICurrentTimeItem) {
				String error = definitionPanel.validateDate(formItem, fieldDef)
						+ "";
				if (error.equals("-1"))
					error = "თარიღი მითითებულია მომავალში!!!";
				else if (error.equals("-2") && !fieldDef.isDonotcheckForDate())
					error = "თარიღი ძალიან ძველია!!!!";
				else
					error = "";
				if (error != null && error.length() > 0) {
					SC.say(DocFlowCommon.activity, "Error!!!", error,
							new BooleanCallback() {

								@Override
								public void execute(Boolean value) {
									formItem.requestFocusSelf();
								}
							});
					return false;
				}
			}

		}
		return true;
	}

	protected void clearOtherAdresses(String name) {
		final LinkedHashMap<Long, String> _map = new LinkedHashMap<Long, String>(
				map);
		final FieldDefinitionItem subItem = formitemMap.get(name);
		if (subItem != null) {
			subItem.getFormItem().setValueMap(_map);
			subItem.getFormItem().setValue((Object) null);
			return;
		}

	}

	@SuppressWarnings("unused")
	public void valueChanged(AdapterView<?> parent, long _id,
			FieldDefinitionItem item) {
		String fieldName = item.getFieldDef().getFieldName();
		if ((fieldName.equals("region") || fieldName.equals("city") || fieldName
				.equals("district"))
				&& item.getFormItem() instanceof IComboBoxItem) {
			Object obj = _id;
			int id = -1;
			String subElementName = null;
			int parent_type = -1;
			String[] clearItems = null;

			if (fieldName.equals("region")) {
				subElementName = "district";
				parent_type = ClSelection.T_REGION;
				clearItems = new String[] { "district", "city", "street" };
			}
			if (fieldName.equals("district")) {
				subElementName = "city";
				parent_type = ClSelection.T_SUBREGION;
				clearItems = new String[] { "city", "street" };
			}
			if (fieldName.equals("city")) {
				subElementName = "street";
				parent_type = ClSelection.T_CITY;
				clearItems = new String[] { "street" };
			}

			final LinkedHashMap<Long, String> _map = new LinkedHashMap<Long, String>(
					map);
			try {
				id = Integer.parseInt(obj.toString());
				if (subElementName == null) {
					Integer.parseInt("dd");
				}

			} catch (Exception e) {
				// item.getFormItem().setValueMap(_map);
				return;
			}
			final FieldDefinitionItem subItem = formitemMap.get(subElementName);
			if (subItem != null) {
				subItem.getFormItem().setValueMap(_map);
				return;
			}
			for (String string : clearItems) {
				clearOtherAdresses(string);
			}
		}

	}

	public void executeDs(final CDSRequest dsRequest,
			final DsOperationResult callback) {
		try {
			ProcessExecutor.execute(new IProcess() {

				@Override
				public void execute() throws Exception {
					try {
						CDSResponce responce = DocFlowCommon.docFlowService
								.dsFetchData(dsRequest.getDsName(),
										dsRequest.getCriteria(), dsRequest);
						if (callback != null)
							callback.operationResult(responce);
					} catch (Throwable e) {
						final Throwable ex = e;
						definitionPanel.getContextActivity().runOnUiThread(
								new Runnable() {

									@Override
									public void run() {
										ActivityHelper.showAlert(
												definitionPanel
														.getContextActivity(),
												ex);

									}
								});
					}
				}
			}, definitionPanel.getContextActivity());
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	public String convertHexToString(String hex) {

		try {
			byte[] bt = Base64.decode(hex.getBytes("UTF8"), Base64.DEFAULT);

			return new String(bt, "UTF8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return hex;
	}

	public void executeDs(CDSRequest dsRequest, AListenerMethode callback,
			Object instance) throws Exception {
		executeDs(dsRequest, ClassCreator.createClass(DsOperationResult.class,
				instance, callback));
	}

	public void executeDs(CDSRequest dsRequest, AListenerMethode callback,
			Class<?> clazz) throws Exception {
		executeDs(dsRequest, ClassCreator.createClass(DsOperationResult.class,
				clazz, callback));
	}

}
