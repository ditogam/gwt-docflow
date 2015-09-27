package com.rdcommon.server.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nanoxml.XMLElement;

import com.rdcommon.server.DMIUtils;
import com.rdcommon.server.DSCPropGenerator;
import com.rdcommon.server.PropertyNameGenerator;
import com.rdcommon.shared.ClassDefinition;
import com.rdcommon.shared.GlobalValues;
import com.rdcommon.shared.JSDefinition;
import com.rdcommon.shared.ds.DSDefinition;
import com.rdcommon.shared.props.PropertyNames;

public class DynamicGenerator {

	public static GlobalValues generate() throws Exception {
		TreeMap<Integer, ArrayList<String>> mpOperatBings = new TreeMap<Integer, ArrayList<String>>();
		List<Map<?, ?>> tmpList = DMIUtils.findRecordsByCriteria(
				"DSdefinitionsSODS", null, new TreeMap<String, Object>());
		for (Map<?, ?> map : tmpList) {
			Integer dsdefinitions_id = new Integer(map.get("dsdefinitions_id")
					.toString());
			ArrayList<String> data = mpOperatBings.get(dsdefinitions_id);
			if (data == null) {
				data = new ArrayList<String>();
				mpOperatBings.put(dsdefinitions_id, data);
			}
			data.add((String) map.get("operation_b_text"));
		}

		tmpList = DMIUtils.findRecordsByCriteria("DSdefinitionsDS", null,
				new TreeMap<String, Object>());
		ArrayList<DSDefinition> dsDefinitions = new ArrayList<DSDefinition>();

		for (Map<?, ?> map : tmpList) {
			Integer id = new Integer(map.get("id").toString());
			DSDefinition def = new DSDefinition();
			XMLElement el = new XMLElement();
			el.parseString((String) map.get("xml_text"));
			DSCPropGenerator.loadFromXml(el, def);
			def.setId(id);
			def.setOperationBindings(mpOperatBings.get(id));
			dsDefinitions.add(def);
		}

		List<ClassDefinition> classDefinitions = DMIUtils
				.findObjectsdByCriteria("ClassDefinitionsDS", null,
						new TreeMap<String, Object>(), ClassDefinition.class);

		List<JSDefinition> jsDefinitions = DMIUtils.findObjectsdByCriteria(
				"JSDefinitionsDS", null, new TreeMap<String, Object>(),
				JSDefinition.class);

		tmpList = DMIUtils.findRecordsByCriteria("PropertyDefinitionDS", null,
				new TreeMap<String, Object>());
		TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames = PropertyNameGenerator.generate();
		// new TreeMap<Integer, TreeMap<String, PropertyNames>>();
		//
		// for (Map<?, ?> map : tmpList) {
		// Integer id = new Integer(map.get("property_type").toString());
		// String property_name = (String) map.get("property_name");
		// String valuetypes = (String) map.get("valuetypes");
		// PropertyNames pn = PropertyNames.createProperty(property_name,
		// valuetypes);
		// if (pn == null)
		// continue;
		// TreeMap<String, PropertyNames> p = propertyNames.get(id);
		// if (p == null) {
		// p = new TreeMap<String, PropertyNames>();
		// propertyNames.put(id, p);
		// }
		// p.put(property_name, pn);
		//
		// }

		return new GlobalValues(dsDefinitions, classDefinitions, jsDefinitions,
				propertyNames);

	}

}
