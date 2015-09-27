package com.docflow.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.docflow.shared.ClSelection;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

public class ClientUtils {

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

	public static void makeDependancy(final FormItem formItemParent,
			final boolean set, final FormItemDescr... formItemChilds) {
		formItemParent.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Object value = formItemParent.getValue();
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

				}

			}
		});
	}

	public static void fillSelectionCombo(final FormItem formItem, int type) {
		fillSelectionCombo(formItem, type, null);
	}

	public static void fillSelectionCombo(final FormItem formItem, int type,
			Map<String, Object> aditionalCriteria) {
		if (aditionalCriteria == null)
			aditionalCriteria = new TreeMap<String, Object>();
		fillCombo(formItem, ClSelection.getTypeNameDS(type), "fetchSelections",
				"id", "cvalue", aditionalCriteria);
	}

	public static void fillCombo(final FormItem formItem, String sDataSource,
			String sFetchOperation, String valueField, String nameField) {
		fillCombo(formItem, sDataSource, sFetchOperation, valueField,
				nameField, null);
	}

	public static void fetchData(Criteria criteria, DSCallback cb,
			String sDataSource, String sFetchOperation) {
		DSRequest req = new DSRequest();
		req.setOperationId(sFetchOperation);

		DocFlow.getDataSource(sDataSource).fetchData((criteria), cb, req);
	}

	public static void fetchData(java.util.Map<String, Object> criteria,
			DSCallback cb, String sDataSource, String sFetchOperation) {
		fetchData(createCriteria(criteria), cb, sDataSource, sFetchOperation);
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

			DataSource comboDS = DocFlow.getDataSource(sDataSource);
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

	public static ListGridField createDescrFilterField(String gridFieldName,
			String fieldTitle, int with, int descrTypeId, boolean isCombo) {
		Map<String, Integer> pCriteria = new LinkedHashMap<String, Integer>();
		pCriteria.put("description_type_id", descrTypeId);
		return createFilterField(gridFieldName, fieldTitle, with,
				"DescriptionsDS", "searchDescriptions", pCriteria,
				"description_id", "description", isCombo);
	}

	public static ListGridField createCommonCloseFilterField(
			String gridFieldName, String fieldTitle, int with, int ttype,
			boolean isCombo) {
		Map<String, Integer> pCriteria = new LinkedHashMap<String, Integer>();
		pCriteria.put("ttype", ttype);
		return createFilterField(gridFieldName, fieldTitle, with,
				"ClosedOpenedDS", "searchClosedOpened", pCriteria, "id",
				"name", isCombo);
	}

	public static ListGridField createCommonCloseFilterField(
			String gridFieldName, String fieldTitle, int with, boolean isCombo) {
		return createCommonCloseFilterField(gridFieldName, fieldTitle, with, 0,
				isCombo);
	}

	public static ListGridField createFilterField(String gridFieldName,
			String fieldTitle, int with, String dsName, String dsOpName,
			Map<?, ?> pCriteria, String idFieldName, String displayFieldName,
			boolean isCombo) {
		ListGridField field = new ListGridField(gridFieldName, fieldTitle, with);
		field.setOptionDataSource(DocFlow.getDataSource(dsName));
		field.setOptionOperationId(dsOpName);
		field.setOptionCriteria(addEditionalCriteria(pCriteria, null));
		field.setValueField(idFieldName);
		field.setDisplayField(displayFieldName);
		field.setEditorType(isCombo ? new ComboBoxItem() : new SelectItem());
		field.setFilterEditorType(isCombo ? new ComboBoxItem()
				: new SelectItem());
		field.setAutoFetchDisplayMap(true);
		field.setCanFilter(true);
		return field;
	}

	public static boolean containsOneOfString(String source, String... compare) {
		if (source == null || compare == null || source.trim().length() == 0
				|| compare.length == 0)
			return false;
		for (String string : compare) {
			if (string == null || string.trim().length() == 0)
				continue;
			if (source.trim().equals(string.trim()))
				return true;
		}
		return false;
	}

	public static boolean contansStringOneOf(String source, String redex,
			String... compare) {
		for (String string : compare) {
			if (contansString(source, string, redex))
				return true;
		}
		return false;
	}

	public static boolean contansString(String source, String compare,
			String redex) {
		if (compare == null || compare.trim().length() == 0)
			return false;
		ArrayList<String> contains = concatSplitString(compare, redex);
		for (String str : contains) {
			if (!source.contains(str.trim()))
				return false;
		}
		return true;
	}

	public static ArrayList<String> concatSplitString(String source,
			String redex) {
		return concatSplitString(source, null, redex);
	}

	public static ArrayList<String> concatSplitString(String source,
			ArrayList<String> contains, String redex) {
		if (contains == null)
			contains = new ArrayList<String>();
		if (source == null || source.trim().length() == 0)
			return contains;
		source = source.trim();
		String[] splited = source.split(redex);
		for (String string : splited) {
			if (string == null || string.length() == 0)
				continue;
			if (!contains.contains(string))
				contains.add(string);
		}

		return contains;
	}

	public static void getDownloadFile(String url) {
		try {
			Window window = new Window();
			window.setWidth(350);
			window.setHeight(70);
			window.setTitle("ფაილის გადმოწერა");
			window.setIsModal(true);
			window.setShowCloseButton(true);
			window.setCanDrag(false);
			window.setCanDragReposition(false);
			window.setCanDragResize(false);
			window.setCanDragScroll(false);
			window.centerInPage();

			HLayout hLayout = new HLayout();
			hLayout.setWidth100();
			hLayout.setHeight100();
			hLayout.setPadding(10);

			DynamicForm dynamicForm = new DynamicForm();
			dynamicForm.setWidth100();
			dynamicForm.setHeight100();
			dynamicForm.setTitleWidth(150);

			LinkItem linkItem = new LinkItem();
			linkItem.setTitle("ფაილის მისამართი");
			linkItem.setLinkTitle("გადმოწერეთ ფაილი");
			linkItem.setValue(url);

			dynamicForm.setFields(linkItem);

			hLayout.addMember(dynamicForm);

			window.addItem(hLayout);
			window.show();
		} catch (Exception e) {
			SC.say(e.toString());
		}
	}

	public static void locateRecord(String dataSource, String operationId,
			String fieldName, Object fieldValue) {
		DataSource recordLocatorDS = DocFlow.getDataSource("RecordLocatorDS");
		DSRequest req = new DSRequest();
		req.setOperationId("locateRecord");
		Criteria cr = new Criteria();
		cr.setAttribute("DataSourceName", dataSource);
		cr.setAttribute("OperationId", operationId);
		cr.setAttribute("FieldName", fieldName);
		cr.setAttribute("FieldValue", fieldValue);
		recordLocatorDS.fetchData(cr, new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				// TODO Auto-generated method stub

			}
		}, req);
	}
}
