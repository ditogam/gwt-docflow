package com.docflow.client.components.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.common.shared.ClSelectionItem;
import com.common.shared.DSFieldOptions;
import com.docflow.client.DocFlow;
import com.docflow.client.components.docflow.DocumentDetailTabPane;
import com.docflow.shared.ClSelection;
import com.docflow.shared.Meter;
import com.docflow.shared.MeterPlombs;
import com.docflow.shared.common.FieldDefinition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;

public class SimpleFieldDefinitionListValue extends FieldDefinitionListValue {
	final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	final HashMap<String, ICalculatorClass> mapCalculators = new HashMap<String, ICalculatorClass>();
	NumberFormat twoDForm = NumberFormat.getFormat("#.##");

	public SimpleFieldDefinitionListValue() {

		setFunctions(this);
		map.put("0", "--");

	}

	@Override
	public void activate() {
		setFunctions(this);
		
	}
	
	public native void setFunctions(SimpleFieldDefinitionListValue x)/*-{

		$wnd.calculatorCallBackGetValue = function(name) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::getFieldValue(Ljava/lang/String;)(name);
		};
		$wnd.calculatorCallBackGetDisplayValue = function(name) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::getFieldDisplayValue(Ljava/lang/String;)(name);
		};

		$wnd.calculatorCallBackGetField = function(name) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::getField(Ljava/lang/String;)(name);
		};

		$wnd.calculatorCallBackGetFieldProperty = function(name, propertyName) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::getFieldProperty(Ljava/lang/String;Ljava/lang/String;)(name,propertyName);
		};

		$wnd.calculatorCallBackSetFieldProperty = function(name, propertyName,
				value) {
			x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::setFieldProperty(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)(name,propertyName,value);
		};

		$wnd.calculatorCallBackSetValue = function(name, value) {
			x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::setFieldValue(Ljava/lang/String;Ljava/lang/String;)(name, value);
		};

		$wnd.calculatorCallBackExecDS = function(dsName, operationId,
				resultFunction, criteria) {
			x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::executeDSOperation(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(dsName, operationId, resultFunction,criteria);
		};

		$wnd.calculatorCallBackFieldDefRequaiered = function(name, value) {
			x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::setFieldDefRequaiered(Ljava/lang/String;Z)(name, value);
		};

		$wnd.calculatorCallBackConstants = function(keyName) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::getDocConstants(Ljava/lang/String;)(keyName);
		};

		$wnd.calculatorCallBackgenerateProportions = function(cloan,
				monthcount, type, count) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::generateProportions(DIII)(cloan, monthcount, type, count);
		};

		$wnd.calculatorCallBackSetFieldListValues = function(criteria) {
			return x.@com.docflow.client.components.common.SimpleFieldDefinitionListValue::setFieldListValues(Lcom/google/gwt/core/client/JavaScriptObject;)(criteria);
		};
	}-*/;

	@Override
	public void additionalValidator(FieldDefinitionItem item) throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	public Double[] generateProportions(double cloan, int monthcount, int type,
			int count) {
		NumberFormat df = NumberFormat.getFormat("#.##");

		Double[] proportions = new Double[count];
		if (type == 1) {
			double portion = cloan / (double) monthcount;
			int proportionInt = (int) portion;
			double first = cloan - (double) proportionInt * (monthcount - 1);

			proportions[0] = new Double(df.format(first));
			for (int i = 1; i < monthcount; i++) {
				proportions[i] = new Double(df.format(proportionInt));
			}
		}
		if (type == 2) {
			double first = (cloan / 2.0);
			double portion = first / (double) (monthcount - 1);
			int proportionInt = (int) portion;
			first = cloan
					- ((double) (monthcount - 1) * (double) proportionInt);

			proportions[0] = new Double(df.format(first));
			for (int i = 1; i < monthcount; i++) {
				proportions[i] = new Double(df.format(proportionInt));
			}
		}

		if (type == 3) {
			double S = cloan;
			double Sx = ((int) (S / 2)) * 2;
			double delta = S - Sx;
			double q = 0.5;

			double first = Sx / 2.0;
			double sum = 0.0;
			for (int i = 1; i < monthcount; i++) {
				double pow = Math.pow(q, i);
				double val = first * pow;
				// System.out.println(pow);
				sum += val;
				proportions[i] = new Double(df.format(val));
			}
			// first=cloan - sum;
			proportions[0] = new Double(first);
		}

		return proportions;
	}

	public static native void exucuteFunction(String functionName,
			String fieldName)/*-{
		$wnd[functionName](fieldName);
	}-*/;

	public static native void exucuteFunctionResult(String functionName,
			JavaScriptObject array)/*-{
		$wnd[functionName](array);
	}-*/;

	public static native String getPropertyNames(JavaScriptObject object) /*-{
		var ret = "";
		for ( var i in object) {
			if (ret == "") {
				ret = ret + i
			} else {
				ret = ret + "," + i;
			}
		}
		return ret;
	}-*/;

	public static native void setProperty(JavaScriptObject object, String name,
			String value) /*-{
		object[name] = value;
	}-*/;

	public static native String getProperty(JavaScriptObject object, String name) /*-{
		var ret = object[name];
		return (ret === undefined) ? null : ret.toString();
	}-*/;

	public static native Object getPropertyObj(JavaScriptObject object,
			String name) /*-{
		var ret = object[name];
		return (ret === undefined) ? null : ret;
	}-*/;

	public void setFieldListValues(JavaScriptObject criteria) {
		try {
			String propertyNames = getPropertyNames(criteria);
			String[] propertyNamesArr = propertyNames.split(",");
			HashMap<String, ArrayList<DSFieldOptions>> listSqls = new HashMap<String, ArrayList<DSFieldOptions>>();

			for (String prop : propertyNamesArr) {
				ArrayList<DSFieldOptions> si = new ArrayList<DSFieldOptions>();
				listSqls.put(prop, si);
				Object val = getPropertyObj(criteria, prop);
				if (val != null && val instanceof JavaScriptObject) {
					JavaScriptObject o = (JavaScriptObject) val;
					propertyNames = getPropertyNames(o);
					String attrs[] = propertyNames.split(",");
					for (String s : attrs) {
						String sql = getProperty(o, s);
						DSFieldOptions f = new DSFieldOptions();
						f.setFieldName(s);
						f.setFieldTitle(sql);
						si.add(f);
					}
				}
			}
			SplashDialog.showSplash();
			DocFlow.docFlowService
					.getListTypes(
							listSqls,
							new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

								@Override
								public void onSuccess(
										HashMap<String, ArrayList<ClSelectionItem>> result) {
									SplashDialog.hideSplash();
									try {
										Set<String> fieldName = result.keySet();
										for (String name : fieldName) {
											FieldDefinitionItem item = formitemMap
													.get(name);
											if (item == null)
												continue;
											if (item.getFormItem() == null)
												continue;
											FormDefinitionPanel.setSelectItems(
													item.getFormItem(),
													result.get(name));
										}
									} catch (Exception e) {
										// TODO: handle exception
									}

								}

								@Override
								public void onFailure(Throwable caught) {
									SplashDialog.hideSplash();

								}
							});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void executeDSOperation(String dsName, String operationId,
			final String resultFunction, JavaScriptObject criteria) {
		try {
			DataSource ds = DocFlow.getDataSource(dsName);
			DSRequest req = new DSRequest();
			if (operationId != null && !operationId.trim().isEmpty())
				req.setOperationId(operationId);
			Criteria cr = new Criteria();

			try {
				String propertyNames = getPropertyNames(criteria);
				String[] propertyNamesArr = propertyNames.split(",");
				for (String prop : propertyNamesArr) {
					Object val = getProperty(criteria, prop);
					if (val == null)
						continue;
					cr.setAttribute(prop, val);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			ds.fetchData(cr, new DSCallback() {
				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					if (response == null || response.getData() == null)
						return;
					Record[] records = response.getData();
					JavaScriptObject result = null;
					ArrayList<JavaScriptObject> array = new ArrayList<JavaScriptObject>();
					for (Record record : records) {
						Map mp = record.toMap();
						Set set = mp.keySet();
						JavaScriptObject obj = JavaScriptObject.createObject();
						for (Object key : set) {
							if (key == null)
								continue;
							Object val = mp.get(key);
							if (val == null)
								continue;

							setProperty(obj, key.toString(), val.toString());

						}
						array.add(obj);
					}
					result = JSOHelper.arrayConvert(array
							.toArray(new JavaScriptObject[] {}));
					exucuteFunctionResult(resultFunction, result);

				}
			}, req);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearOtherAdresses(String name) {
		final LinkedHashMap<String, String> _map = new LinkedHashMap<String, String>(
				map);
		final FieldDefinitionItem subItem = formitemMap.get(name);
		if (subItem != null) {
			subItem.getFormItem().setValueMap(_map);
			subItem.getFormItem().setValue((Object) null);
			return;
		}

	}

	@Override
	public void fillCombo(FieldDefinitionItem item) {
		final LinkedHashMap<String, String> _map = new LinkedHashMap<String, String>(
				map);
		_map.put("0", "--");
		if (item.getFieldDef().getFieldName().equals("field5")
				&& item.getFormItem() instanceof SelectItem) {

			for (int i = 0; i < 10; i++) {
				_map.put((i + 1) + "", (i + 1) + ".Item");
			}
			item.getFormItem().setValueMap(map);
		}
		if (item.getFieldDef().getFieldName().equals("region")
				&& item.getFormItem() instanceof SelectItem) {
			final SelectItem reg = (SelectItem) item.getFormItem();
			DocFlow.docFlowService
					.getRegions(new AsyncCallback<ArrayList<ClSelectionItem>>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(ArrayList<ClSelectionItem> result) {
							for (ClSelectionItem clSelectionItem : result) {
								_map.put(clSelectionItem.getId() + "",
										clSelectionItem.getValue());
							}
							reg.setValueMap(_map);

						}
					});
		}
		if ((item.getFieldDef().getFieldName().equals("city")
				|| item.getFieldDef().getFieldName().equals("street") || item
				.getFieldDef().getFieldName().equals("district"))
				&& item.getFormItem() instanceof SelectItem) {
			final SelectItem _addressItem = (SelectItem) item.getFormItem();
			_addressItem.setValueMap(_map);
		}

	}

	private String getDocConstants(String keyName) throws Exception {
		keyName = keyName.toLowerCase();
		// if (keyName.equals("@docnum"))
		// setFieldValue(doc.getCancelary_nom(), index, ptype, stmt, keyName);
		// if (keyName.equals("@docdate"))
		// setFieldValue(doc.getDoc_date() + "", index, ptype, stmt, keyName);
		if (keyName.equals("@user"))
			return DocFlow.user_id + "";
		if (keyName.equals("@username"))
			return DocFlow.user_name;
		if (keyName.equals("@doc_id"))
			return DocumentDetailTabPane.documentDetails.getDocId() + "";
		if (keyName.equals("@cusid"))
			return DocumentDetailTabPane.documentDetails.seletedCustomer == null ? "-1"
					: DocumentDetailTabPane.documentDetails.seletedCustomer
							.toString();
		// setFieldValue(doc.getUser_name() + "", index, ptype, stmt, keyName);
		// if (keyName.equals("@cusid"))
		// setFieldValue(doc.getCust_id() + "", index, ptype, stmt, keyName);
		// if (keyName.equals("@delay"))
		// setFieldValue(doc.getDelaystatus() + "", index, ptype, stmt,
		// keyName);
		return null;
	}

	private boolean getBoolean(FieldDefinitionItem current) {
		if (current == null)
			return false;
		boolean currentValue = false;
		try {
			Object obj = current.getFormItem().getValue();
			currentValue = (Boolean) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentValue;
	}

	private HashMap<String, FieldDefinitionItem> myformitemMap;

	@Override
	public ICalculatorClass getCalculetorClass(final String name) {
		if (name == null || name.trim().length() == 0)
			return null;
		ICalculatorClass ret = mapCalculators.get(name);
		if (ret == null)
			ret = new ICalculatorClass() {

				@Override
				public void calculate(
						HashMap<String, FieldDefinitionItem> formitemMap,
						FieldDefinitionItem current) {
					myformitemMap = formitemMap;
					String funcName = name;
					try {
						String fieldName = current.getFieldDef().getFieldName();
						exucuteFunction(funcName, fieldName);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			};
		return ret;
	}

	public JavaScriptObject getField(String name) {
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return null;
		FormItem formItem = fd.getFormItem();
		return formItem.getJsObj();
	}

	private double getDoubleValue(FieldDefinitionItem current) {
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

	private int getIntValue(FieldDefinitionItem current) {
		if (current == null)
			return 0;
		int currentValue = 0;
		try {
			currentValue = Integer.parseInt(current.getFormItem().getValue()
					.toString());
		} catch (Exception e) {
		}
		return currentValue;
	}

	public String getFieldValue(String name) {
		String s=this+"";
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return "";
		FormItem formItem = fd.getFormItem();
		Object obj = formItem.getValue();
		return obj == null ? null : obj.toString().trim();
	}

	public String getFieldDisplayValue(String name) {
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return "";
		FormItem formItem = fd.getFormItem();
		Object obj = formItem.getDisplayValue();
		return obj == null ? null : obj.toString().trim();
	}

	public String getFieldProperty(String name, String propertyName) {
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return null;
		FormItem formItem = fd.getFormItem();
		Integer obj = formItem.getAttributeAsInt(propertyName);
		return obj == null ? null : obj.toString();
	}

	public void setFieldProperty(String name, String propertyName, Object value) {
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return;
		FormItem formItem = fd.getFormItem();
		// formItem.setTitle(value.toString());
		if (value == null)
			formItem.setProperty(propertyName, (String) null);
		else if (value instanceof JavaScriptObject)
			formItem.setProperty(propertyName, ((JavaScriptObject) value));
		else
			formItem.setProperty(propertyName, value.toString());
	}

	public void setFieldValue(String name, String value) {
		FieldDefinitionItem fd = formitemMap.get(name);
		if (fd == null)
			return;
		FormItem formItem = fd.getFormItem();
		formItem.setValue(value);

	}

	public void setFieldDefRequaiered(String fieldName, boolean requered) {
		if (formitemMap == null)
			return;

		final FieldDefinitionItem fdi = formitemMap.get(fieldName);
		if (fdi == null)
			return;
		final FieldDefinition fieldDef = fdi.getFieldDef();
		if (fieldDef == null)
			return;
		fieldDef.setRequaiered(requered);
	}

	private void setEditableAndRequered(String[] fields,
			HashMap<String, FieldDefinitionItem> formitemMap, boolean enable,
			String[] notrequered) {
		for (String f : fields) {
			final FieldDefinitionItem fdi = formitemMap.get(f);
			if (fdi == null)
				continue;
			final FormItem formItem = fdi.getFormItem();
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

	private void setFieldValue(FieldDefinitionItem current, Object value) {
		if (current == null)
			return;
		current.getFormItem().setValue(value);
	}

	private void setPlombs(ArrayList<MeterPlombs> meterPlombs) {
		final FieldDefinitionItem mnewvalue = formitemMap.get("oldplombs");
		if (mnewvalue == null)
			return;
		final FormItem formItem = mnewvalue.getFormItem();
		if (formItem == null)
			return;

		ArrayList<ClSelectionItem> items = new ArrayList<ClSelectionItem>();
		ClSelectionItem newItem = new ClSelectionItem();
		newItem.setId(-1);
		newItem.setValue("---NEW----");
		items.add(newItem);

		for (MeterPlombs meterPlombs2 : meterPlombs) {
			newItem = new ClSelectionItem();
			newItem.setId(meterPlombs2.getPlombid());
			String value = meterPlombs2.getPlombname() + ":"
					+ meterPlombs2.getPlace() + ":" + meterPlombs2.getStatus();
			newItem.setValue(value);
			items.add(newItem);
		}
		FormDefinitionPanel.setSelectItems(formItem, items);
	}

	protected void showMetterM3(
			final HashMap<String, FieldDefinitionItem> formitemMap,
			final FieldDefinitionItem current) {

		try {

			FieldDefinitionItem mnewvalue = formitemMap.get("mnewvalue");
			if (mnewvalue == null)
				return;
			final FormItem formItem = mnewvalue.getFormItem();
			if (formItem == null)
				return;
			String attrName = "focusechangedlisteneradded";
			String attr = formItem.getAttribute(attrName);
			if (attr == null) {
				formItem.setAttribute(attrName, "1");
				formItem.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						double m3 = getDoubleValue(formitemMap.get("expensem3"));
						if (m3 < 0) {
							SC.say("Negative", new BooleanCallback() {

								@Override
								public void execute(Boolean value) {
									formItem.focusInItem();

								}
							});

							return;
						}

					}
				});
			}
		} finally {
			double oldValue = getDoubleValue(formitemMap.get("moldvalue"));
			double newValue = getDoubleValue(formitemMap.get("mnewvalue"));
			double m3 = newValue - oldValue;
			setFieldValue(formitemMap.get("expensem3"), twoDForm.format(m3));
		}
	}

	protected void showMetterValue(
			final HashMap<String, FieldDefinitionItem> formitemMap,
			final FieldDefinitionItem current) {
		String mserial = null;
		try {

			String metterValue = formitemMap.get("meterid").getFormItem()
					.getDisplayValue();

			mserial = metterValue.split(":")[1];

		} catch (Exception e) {

		}
		try {
			formitemMap.get("mserial").getFormItem().setValue(mserial);
		} catch (Exception e) {

		}
		final boolean withplombs = formitemMap.get("oldplombs") != null;
		DocFlow.docFlowService.getMetterValue(
				(int) getDoubleValue(formitemMap.get("meterid")), withplombs,
				new AsyncCallback<Meter>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());

					}

					@Override
					public void onSuccess(Meter result) {
						if (result != null) {
							setFieldValue(formitemMap.get("moldvalue"),
									result.getMettervalue());
							setFieldValue(formitemMap.get("mnewvalue"),
									result.getMettervalue());
							setFieldValue(formitemMap.get("emetserial"),
									result.getMetserial());
							setFieldValue(formitemMap.get("emettertype"),
									result.getMtypeid() + "");
							if (withplombs)
								setPlombs(result.getMeterPlombs());
						}
						showMetterM3(formitemMap, current);
					}
				});

	}

	protected void showNegative(
			final HashMap<String, FieldDefinitionItem> formitemMap,
			final FieldDefinitionItem current) {

		try {
			final FieldDefinitionItem mnewvalue = formitemMap.get("mnewvalue");
			if (mnewvalue == null)
				return;
			final FormItem formItem = mnewvalue.getFormItem();
			if (formItem == null)
				return;
			final FieldDefinitionItem moldvalue = formitemMap.get("moldvalue");
			if (moldvalue == null)
				return;
			final FormItem formItemmoldvalue = moldvalue.getFormItem();
			if (formItemmoldvalue == null)
				return;
			String attrName = "fclist";
			String attr = formItem.getAttribute(attrName);
			if (attr == null) {
				formItem.setAttribute(attrName, "1");
				formItem.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						double oldvalue = getDoubleValue(moldvalue);
						double newvalue = getDoubleValue(mnewvalue);
						if (newvalue < oldvalue) {
							SC.say("Negative", new BooleanCallback() {
								@Override
								public void execute(Boolean value) {
									formItem.focusInItem();

								}
							});

							return;
						}

					}
				});
			}
		} finally {

		}
	}

	@SuppressWarnings("unused")
	@Override
	public void valueChanged(ChangeEvent event, FieldDefinitionItem item) {
		String fieldName = item.getFieldDef().getFieldName();
		if ((fieldName.equals("region") || fieldName.equals("city") || fieldName
				.equals("district"))
				&& item.getFormItem() instanceof SelectItem) {
			Object obj = event.getValue();
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

			final LinkedHashMap<String, String> _map = new LinkedHashMap<String, String>(
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
			// DocFlow.gissTerrierService.getItemsForType(parent_type, id,
			// new AsyncCallback<ArrayList<ClSelectionItem>>() {
			//
			// @Override
			// public void onSuccess(ArrayList<ClSelectionItem> result) {
			// for (ClSelectionItem clSelectionItem : result) {
			// _map.put(clSelectionItem.getId() + "",
			// clSelectionItem.getValue());
			// }
			// subItem.getFormItem().setValueMap(_map);
			// }
			//
			// @Override
			// public void onFailure(Throwable caught) {
			// // TODO Auto-generated method stub
			//
			// }
			// });
		}

	}
}
