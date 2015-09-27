package com.rdcommon.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.rdcommon.shared.ds.DSClientFieldDef;
import com.rdcommon.shared.ds.DSComponent;
import com.rdcommon.shared.ds.DSDefinition;
import com.rdcommon.shared.ds.DSField;
import com.rdcommon.shared.ds.DSFormDefinition;
import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class ClientUtils {

	private static class GroupConfig {
		public DSGroup group;
		public Layout layout;
	}

	public static ArrayList<DynamicForm> createFormPanel(DSDefinition ds,
			String name, DSClientSearchForm layout) {
		ArrayList<DynamicForm> dynamicForms = new ArrayList<DynamicForm>();

		DSFormDefinition form = ds.getSearchForm(name);
		ArrayList<DSGroup> dsGroups = form.getDsGroups();
		ArrayList<DSField> dsFields = ds.getDsFields();
		TreeMap<String, ArrayList<DSClientFieldDef>> fieldsMap = new TreeMap<String, ArrayList<DSClientFieldDef>>();
		TreeMap<String, DSClientFieldDef> fieldDefMap = new TreeMap<String, DSClientFieldDef>();
		createFieldsMap(ds, dsFields, form, fieldsMap, fieldDefMap);
		TreeMap<String, GroupConfig> groupsMap = new TreeMap<String, GroupConfig>();
		if (dsGroups != null && !dsGroups.isEmpty()) {
			for (DSGroup dsGroup : dsGroups) {
				Layout grChLay = ClientUtils.createGroup(dsGroup, groupsMap);
				if (grChLay != null)
					layout.addMember(grChLay);
			}
		}
		String methodeName = ds.getDsName() + "_" + name + "_"
				+ System.currentTimeMillis();
		Set<String> keys = fieldsMap.keySet();
		TreeMap<String, TreeMap<String, FormItem>> dependencies = new TreeMap<String, TreeMap<String, FormItem>>();
		TreeMap<String, FormItem> allItems = new TreeMap<String, FormItem>();

		for (String key : keys) {

			GroupConfig l = groupsMap.get(key);
			if (l == null)
				continue;
			DynamicForm dm = createDynamicForm(l, ds, fieldsMap.get(key),
					methodeName, layout, dependencies, allItems);
			dynamicForms.add(dm);

		}

		try {
			registerCallBack(methodeName, layout);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		for (String key : dependencies.keySet()) {
			FormItem fitem = allItems.get(key);
			if (fitem == null)
				continue;

			Collection<FormItem> fitems = dependencies.get(key).values();
			ArrayList<FormItem> aItems = new ArrayList<FormItem>(fitems);

			ArrayList<FormItemDescr> formItems = new ArrayList<FormItemDescr>();
			for (int i = 0; i < aItems.size(); i++) {
				FormItem formItem = aItems.get(i);
				String fName = formItem.getName();
				DSClientFieldDef fd = fieldDefMap.get(fName);
				String parentName = fd.getParentFieldName();
				String parentFieldValueName = fd.getParentFieldValueName();
				if (parentName != null && !parentName.trim().isEmpty()
						&& parentFieldValueName != null
						&& !parentFieldValueName.trim().isEmpty()) {
					FormItemDescr f = new FormItemDescr(formItem,
							parentFieldValueName);
					formItems.add(f);
				}
			}
			makeDependancy(fitem, false,
					formItems.toArray(new FormItemDescr[] {}));
		}
		return dynamicForms;
	}

	public static void makeDependancy(final FormItem formItemParent,
			final boolean set, final FormItemDescr... formItemChilds) {
		formItemParent.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Object value = formItemParent.getValue();
				if (value == null)
					value = -100000;
				for (FormItemDescr formItem : formItemChilds) {
					formItem.formItem.clearValue();

					if ((formItem.formItem instanceof ComboBoxItem)
							|| (formItem.formItem instanceof SelectItem)) {
						Criteria cr = formItem.formItem.getOptionCriteria();
						if (cr == null) {
							cr = new Criteria();
							formItem.formItem.setOptionCriteria(cr);
						}
						cr.setAttribute(formItem.parentName, value);
						addEditionalCriteria(formItem.aditionalCriteria, cr);
						formItem.formItem.setOptionCriteria(cr);
					}

					if (set && formItem.valueSet != null) {
						ListGridRecord record = formItemParent
								.getSelectedRecord();
						String val = null;
						if (record != null) {
							val = record.getAttribute(formItem.valueSet);
						}
						formItem.formItem.setValue(val);
					}

					formItem.formItem.fireEvent(new ChangedEvent(
							formItem.formItem.getJsObj()));
				}

			}
		});
	}

	private static native void registerCallBack(String methodeName,
			DSClientSearchForm layout)/*-{
										$wnd.myfunctions[methodeName] = function(invokerFieldName, values) {
										layout.@com.rdcommon.client.DSClientSearchForm::doCallBack(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(invokerFieldName,values);
										}
										}-*/;

	private static native void valueChanged(String functionName,
			String methodeName, String invokerFieldName, JavaScriptObject values)/*-{
																					$wnd[functionName](methodeName, invokerFieldName, values);
																					}-*/;

	private static DynamicForm createDynamicForm(GroupConfig l,
			DSDefinition ds, ArrayList<DSClientFieldDef> arrayList,
			final String methodeName, final DSClientSearchForm layout,
			TreeMap<String, TreeMap<String, FormItem>> dependencies,
			TreeMap<String, FormItem> allItems) {
		DynamicForm dm = new DynamicForm();
		ArrayList<FormItem> fims = new ArrayList<FormItem>();

		for (final DSClientFieldDef dsClientFieldDef : arrayList) {
			ClientFieldDef ti = ClientFieldDef.createField(dsClientFieldDef);
			if (dsClientFieldDef.getChangeHandlerMethode() != null
					&& !dsClientFieldDef.getChangeHandlerMethode().trim()
							.isEmpty()) {
				ti.getFormItem().addChangedHandler(new ChangedHandler() {

					@Override
					public void onChanged(ChangedEvent event) {
						try {
							valueChanged(dsClientFieldDef
									.getChangeHandlerMethode().trim(),
									methodeName, dsClientFieldDef.getName(),
									layout.getValueMap());
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});
			}
			fims.add(ti.getFormItem());
			allItems.put(ti.getFormItem().getName(), ti.getFormItem());
			String parentName = dsClientFieldDef.getParentFieldName();
			String parentFieldValueName = dsClientFieldDef
					.getParentFieldValueName();
			if (parentName != null && !parentName.trim().isEmpty()
					&& parentFieldValueName != null
					&& !parentFieldValueName.trim().isEmpty()) {
				parentName = parentName.trim();
				TreeMap<String, FormItem> childs = dependencies.get(parentName);
				if (childs == null) {
					childs = new TreeMap<String, FormItem>();

					dependencies.put(parentName, childs);
				}
				childs.put(dsClientFieldDef.getName(), ti.getFormItem());
			}
		}
		dm.setFields(fims.toArray(new FormItem[] {}));
		dm.setHeight100();
		dm.setWidth100();
		if (l.group.getDynamicFieldProps() != null) {
			DSComponent props = l.group.getDynamicFieldProps();
			setValues(props, dm);
		}
		l.layout.addMember(dm);

		return dm;
	}

	private static void createFieldsMap(DSDefinition ds,
			ArrayList<DSField> dsFields, DSFormDefinition form,
			TreeMap<String, ArrayList<DSClientFieldDef>> fieldsMap,
			TreeMap<String, DSClientFieldDef> fieldDefMap) {
		ArrayList<DSClientFieldDef> newFields = new ArrayList<DSClientFieldDef>();
		for (DSField newF : dsFields) {
			DSClientFieldDef def = form.getDSField(newF.getfName());
			DSClientFieldDef newClF = null;
			if (def == null)
				newClF = newF.getSearchProps();
			else {
				newClF = new DSClientFieldDef();
				newClF.mergeProps(newF.getSearchProps(), def);
			}
			newFields.add(newClF);
			fieldDefMap.put(newF.getfName(), newClF);
		}
		for (DSClientFieldDef dsClientFieldDef : newFields) {
			if (dsClientFieldDef.getHidden() != null
					&& dsClientFieldDef.getHidden())
				continue;
			String groupName = dsClientFieldDef.getGroupName();
			ArrayList<DSClientFieldDef> groupItems = fieldsMap.get(groupName);
			if (groupItems == null) {
				groupItems = new ArrayList<DSClientFieldDef>();
				fieldsMap.put(groupName, groupItems);
			}
			groupItems.add(dsClientFieldDef);
		}
	}

	public static Layout createGroup(DSGroup group,
			TreeMap<String, GroupConfig> groupsMap) {
		Layout grLay = group.getVertical() == null || !group.getVertical() ? new HLayout()
				: new VLayout();

		if (groupsMap != null && group.getName() != null
				&& !group.getName().trim().isEmpty()) {
			GroupConfig conf = new GroupConfig();
			conf.group = group;
			conf.layout = grLay;
			groupsMap.put(group.getName(), conf);
		}
		setValues(group, grLay);
		ArrayList<DSGroup> dsGroups = group.getDsGroups();
		if (dsGroups != null && !dsGroups.isEmpty()) {
			for (DSGroup dsGroup : dsGroups) {
				Layout grChLay = createGroup(dsGroup, groupsMap);
				if (grChLay != null)
					grLay.addMember(grChLay);
			}
		}
		return grLay;
	}

	public static void setValues(DSComponent config, Canvas item) {
		Map<String, String> additionalProps = config.getAdditionalProps();
		if (additionalProps != null && !additionalProps.isEmpty()) {
			Set<String> keys = additionalProps.keySet();
			for (String key : keys) {
				try {
					JSOHelper.setAttribute(item.getConfig(), key,
							additionalProps.get(key));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected static native boolean isCreated(FormItem item)/*-{
															var self = item.@com.smartgwt.client.core.DataClass::getJsObj()();
															return !!self.setValue;
															}-*/;

	public static void setValues(DSComponent config, FormItem item) {
		Map<String, String> additionalProps = config.getAdditionalProps();
		if (additionalProps != null && !additionalProps.isEmpty()) {

			Set<String> keys = additionalProps.keySet();
			for (String key : keys) {
				try {
					String val = additionalProps.get(key);
					Object v = val;
					try {
						if (key.startsWith("can"))
							v = new Boolean(val);
					} catch (Exception e) {
						// TODO: handle exception
					}

					item.setAttribute(key, v);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// setValueOrPercent("width", config.getWidth(), item);
		// setValueOrPercent("height", config.getHeight(), item);

	}

	public static void makeDependancy(final FormItem formItemParent,
			String publicParentId, final FormItem... formItemChilds) {
		makeDependancy(formItemParent, publicParentId, null, formItemChilds);
	}

	public static void makeDependancy(final FormItem formItemParent,
			String publicParentId, Map<?, ?> publicAditionalCriteria,
			final FormItem... formItemChilds) {
		makeDependancy(formItemParent, null, publicParentId, null, false,
				formItemChilds);
	}

	public static void makeDependancy(final FormItem formItemParent,
			String valueSet, String publicParentId,
			Map<?, ?> publicAditionalCriteria, boolean set,
			final FormItem... formItemChilds) {
		ArrayList<FormItemDescr> newformItemChilds = new ArrayList<FormItemDescr>();
		for (FormItem formItem : formItemChilds) {
			newformItemChilds.add(new FormItemDescr(formItem, publicParentId,
					publicAditionalCriteria, valueSet));
		}
		makeDependancy(formItemParent, set,
				newformItemChilds.toArray(new FormItemDescr[] {}));
	}

	private static void setValueOrPercent(String name, Number value, Canvas item) {
		if (value == null)
			return;
		if (value.doubleValue() > 0)
			try {
				JSOHelper.setAttribute(item.getConfig(), name, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if (name.equals("width"))
			item.setWidth((value.doubleValue() * -1) + "%");
		else
			item.setHeight((value.doubleValue() * -1) + "%");
	}

	public static void setValueOrPercent(String name, Number value,
			FormItem item) {
		if (value == null)
			return;
		if (value.doubleValue() > 0)
			try {
				item.setAttribute(name, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if (name.equals("width"))
			item.setWidth((value.doubleValue() * -1) + "%");
		else
			item.setHeight((value.doubleValue() * -1) + "%");
	}

	public static void fillCombo(final FormItem formItem, String sDataSource,
			String sFetchOperation, final String valueField, String nameField,
			Map<?, ?> aditionalCriteria) {

		try {
			if (!(formItem instanceof ComboBoxItem)
					&& !(formItem instanceof SelectItem)) {
				return;
			}

			formItem.setFetchMissingValues(true);
			formItem.setFilterLocally(false);
			if (formItem instanceof ComboBoxItem) {
				ComboBoxItem cItem = (ComboBoxItem) formItem;
				cItem.setAddUnknownValues(false);
				cItem.setAutoFetchData(false);
				// cItem.setTextMatchStyle(TextMatchStyle.SUBSTRING);

			} else {
				SelectItem sItem = (SelectItem) formItem;
				sItem.setAddUnknownValues(false);
				sItem.setAutoFetchData(false);

			}

			formItem.setFilterLocally(false);
			formItem.setFetchMissingValues(true);

			DataSource comboDS = DataSource.get(sDataSource);
			formItem.setOptionOperationId(sFetchOperation);
			formItem.setOptionDataSource(comboDS);
			formItem.setValueField(valueField);
			formItem.setDisplayField(nameField);
			Criteria criteria = new Criteria();
			addEditionalCriteria(aditionalCriteria, criteria);

			formItem.setOptionCriteria(criteria);
			if (formItem instanceof SelectItem)
				formItem.addKeyDownHandler(new KeyDownHandler() {

					@Override
					public void onKeyDown(KeyDownEvent event) {
						String key = event.getKeyName();
						if ((key.equals("Escape") || key.equals("Delete"))) {
							formItem.clearValue();
						}

					}
				});
			formItem.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {

					Criteria criteria = formItem.getOptionCriteria();
					if (criteria != null) {
						Object oldAttr = criteria.getAttribute(valueField);
						if (oldAttr != null) {
							criteria.setAttribute(valueField, (String) null);
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			SC.say(e.toString());
		}
	}

	public static void setDefauldCriterias(FormItem formItem,
			Map<?, ?> aditionalCriteria) {
		Criteria criteria = new Criteria();
		addEditionalCriteria(aditionalCriteria, criteria);
		formItem.setOptionCriteria(criteria);
	}

	public static Criteria createCriteria(Map<?, ?> aditionalCriteria) {
		return addEditionalCriteria(aditionalCriteria, null);
	}

	public static Criteria addEditionalCriteria(Map<?, ?> aditionalCriteria,
			Criteria criteria) {
		if (criteria == null) {
			criteria = new Criteria();
		}
		if (aditionalCriteria != null) {
			Set<?> keys = aditionalCriteria.keySet();
			for (Object key : keys) {
				if (key != null) {
					Object value = aditionalCriteria.get(key);
					if (key != null)
						criteria.setAttribute(key.toString(), value);
				}
			}
		}
		criteria.setAttribute("_UUUUUUUIDUUU", HTMLPanel.createUniqueId());
		return criteria;
	}

	public static Map<String, Object> fillMapFromForm(Map<String, Object> mp,
			DynamicForm... dms) {
		if (mp == null)
			mp = new TreeMap<String, Object>();
		for (DynamicForm dm : dms) {
			Map<?, ?> vals = dm.getValues();
			Set<?> keys = vals.keySet();
			for (Object key : keys) {
				String sKey = key.toString();
				if (dm.getField(sKey) != null) {
					mp.put(sKey, vals.get(key));
				}
			}
		}
		mp.remove("_ref");
		return mp;
	}

	public static Record setRecordMap(Map<String, Object> mp, Record record) {
		if (record == null) {
			record = new Record(mp);
			return record;
		}
		Set<?> keys = mp.keySet();
		for (Object key : keys) {
			String sKey = key.toString();
			record.setAttribute(sKey, mp.get(key));
		}
		return record;
	}

	public static Boolean getBooleanValue(FormItem item) {
		try {
			return (Boolean) item.getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public static ToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler,
			SelectionType actionType, String toolTip, String group_id,
			ToolStrip toolStrip) {
		ToolStripButton tsbButton = new ToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setGroupTitle(group_id);
		toolStrip.addButton(tsbButton);
		return tsbButton;
	}

	public static double getNumberValue(FormItem item) {
		try {
			Object val = item.getValue();
			return new Double(val.toString().trim());
		} catch (Exception e) {
			return 0.0;
		}
	}

	public static int getIntValue(FormItem item) {
		return (int) getNumberValue(item);
	}

	public static Double getDoubleValue(FormItem item) {
		if (item.getValue() == null)
			return null;
		return getNumberValue(item);
	}

	public static Integer getIntegerValue(FormItem item) {
		if (item.getValue() == null)
			return null;
		return getIntValue(item);
	}
}
