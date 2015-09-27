package com.docflow.shared.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.common.shared.usermanager.TransfarableUser;

public class DocFormProcessor {
	protected FormDefinition def;
	private HashMap<String, String> values;
	protected HashMap<String, String> texts;
	private IPermitionChecker permitionChecker;
	private TransfarableUser user;

	private HashMap<String, FieldDefinition> fieldsMap;
	private HashMap<String, String> listSqls;

	public DocFormProcessor(FormDefinition def, HashMap<String, String> values,
			HashMap<String, String> texts, IPermitionChecker permitionChecker,
			TransfarableUser user) {
		super();
		this.def = def;
		this.values = values;
		this.texts = texts;
		this.user = user;
		this.permitionChecker = permitionChecker;

		fieldsMap = new HashMap<String, FieldDefinition>();

		ArrayList<FormGroup> formGroups = def.getFormGroups();
		for (FormGroup g : formGroups) {
			ArrayList<FieldDefinition> fieldDefinitions = g
					.getFieldDefinitions();
			for (FieldDefinition fd : fieldDefinitions) {
				fieldsMap.put(fd.getFieldName(), fd);
			}
		}
		generateCriterias();
	}

	public HashMap<String, FieldDefinition> getFieldsMap() {
		return fieldsMap;
	}

	private boolean isValRegionalField(String val) {
		return val.toLowerCase().equals("$regionId".toLowerCase())
				|| val.toLowerCase().equals("$subregionId".toLowerCase());
	}

	public HashMap<String, String> getListSqls() {
		return listSqls;
	}

	private void generateCriterias() {
		listSqls = new HashMap<String, String>();
		Set<String> fieldKeys = fieldsMap.keySet();

		for (String key : fieldKeys) {
			FieldDefinition fieldDef = fieldsMap.get(key);

			String val = fieldDef.getDefaultValue();
			if (val != null && val.trim().length() > 0) {
				val = val.trim();
				boolean regionalField = isValRegionalField(val);
				if (!regionalField && !values.containsKey(key)) {
					values.put(key, val);
					continue;
				}
				if (permitionChecker.hasPermition("CAN_VIEW_ALL_REGIONS")
						&& regionalField) {
					if (values.containsKey(key))
						continue;
				}
				if (val.toLowerCase().equals("$regionId".toLowerCase())
						&& user.getRegionid() >= 0) {
					values.put(key, "" + user.getRegionid());
				}
				if (val.toLowerCase().equals("$subregionId".toLowerCase())
						&& user.getSubregionid() >= 0) {
					values.put(key, "" + user.getSubregionid());
				}
			}
		}

		for (String key : fieldKeys) {
			FieldDefinition fieldDef = fieldsMap.get(key);
			if (fieldDef.getFieldType() == FieldDefinition.FT_COMBO
					|| fieldDef.getFieldType() == FieldDefinition.FT_SELECTION
					|| fieldDef.getFieldType() == FieldDefinition.FT_CHK_GRID) {
				String value = "";
				if (fieldDef.getFieldSelectMethode() > 0) {
					value = fieldDef.getFieldSelectMethode() + "";
					String parentField = fieldDef.getParentField();
					parentField = parentField == null ? "" : parentField;
					if (parentField.length() != 0) {
						FieldDefinition pItem = fieldsMap.get(parentField);
						String parent_id = values.get(parentField);
						if (parent_id == null || parent_id.trim().length() == 0) {
							parent_id = "-1";

						}
						parent_id = parent_id.trim();

						if (pItem != null)
							value += "_" + pItem.getFieldSelectMethode() + "_"
									+ parent_id;
					}

				} else
					value = fieldDef.getFieldSelectionSQL();
				if (value == null || value.length() < 1)
					continue;
				listSqls.put(key, value);

			}
		}

	}
}
