package com.rdcommon.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.rdcommon.shared.props.PropertyNames;
import com.rdcommon.shared.props.PropertyTypes;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.ValueEnum;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class PropertyNameGenerator {
	private static final TreeMap<Integer, ArrayList<Class<?>>> propertyClassMappings = new TreeMap<Integer, ArrayList<Class<?>>>();

	public static TreeMap<Integer, TreeMap<String, PropertyNames>> generate() {
		propertyClassMappings.clear();
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(DataSource.class);
		propertyClassMappings.put(PropertyNames.PT_DATASOURCE, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(DataSourceField.class);
		propertyClassMappings.put(PropertyNames.PT_DSFIELD, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(ListGrid.class);
		classes.add(Canvas.class);
		propertyClassMappings.put(PropertyNames.PT_GRID, classes);

		classes = new ArrayList<Class<?>>();
		propertyClassMappings.put(PropertyNames.PT_GRID_GROUP, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(Window.class);
		classes.add(VLayout.class);
		classes.add(Layout.class);
		classes.add(Canvas.class);
		propertyClassMappings.put(PropertyNames.PT_WINDOWFORM, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(Layout.class);
		classes.add(Canvas.class);
		propertyClassMappings.put(PropertyNames.PT_PANLE_GROUP, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(DynamicForm.class);
		classes.add(Canvas.class);
		propertyClassMappings.put(PropertyNames.PT_DYNAMICFORM, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(FormItem.class);
		propertyClassMappings.put(PropertyNames.PT_FORMITEM, classes);

		classes = new ArrayList<Class<?>>();
		classes.add(ListGridField.class);
		propertyClassMappings.put(PropertyNames.PT_GRID_FIELD, classes);

		TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames = new TreeMap<Integer, TreeMap<String, PropertyNames>>();
		for (Integer type : propertyClassMappings.keySet()) {
			TreeMap<String, PropertyNames> map = new TreeMap<String, PropertyNames>();
			propertyNames.put(type, map);
			classes = propertyClassMappings.get(type);
			for (Class<?> clazz : classes) {
				getMethodeNames(map, clazz);
			}
		}
		return propertyNames;
	}

	private static void getMethodeNames(TreeMap<String, PropertyNames> map,
			Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("set"))
				if (method.getParameterTypes().length == 1) {
					Class<?> tp = method.getParameterTypes()[0];
					Object type = null;
					String methodeName = method.getName();
					TreeMap<String, String> enums = new TreeMap<String, String>();
					if (DSCPropGenerator.isPrimitive(tp))
						type = DSCPropGenerator.getXmlValue("1", tp).getClass()
								.getSimpleName();
					else if (DSCPropGenerator.isInstance(tp, ValueEnum.class)
							&& tp.isEnum()) {
						type = tp.getSimpleName();
						Object[] myenums = tp.getEnumConstants();
						for (Object object : myenums) {
							if (object instanceof ValueEnum) {
								String key = ((ValueEnum) object).getValue();
								enums.put(key, key);
							}
						}

					} else if (DSCPropGenerator.isInstance(tp, Map.class)) {
						// type = "map";
					}
					if (type != null) {
						setClassValues(map, methodeName, type.toString(), enums);
						// System.out.println(method.getName() + " = " + type);
					}
				}
		}
	}

	private static void setClassValues(TreeMap<String, PropertyNames> map,
			String methodeName, String type, TreeMap<String, String> enums) {

		String nm = methodeName.substring("set".length());
		nm = (nm.charAt(0) + "").toLowerCase() + nm.substring(1);
		methodeName = nm;

		PropertyNames d = map.get(methodeName);
		if (d == null) {
			d = new PropertyNames();
			map.put(methodeName, d);
			d.setPropertyName(methodeName);
		}
		ArrayList<PropertyTypes> types = d.getPropertyTypes();
		if (types == null) {
			types = new ArrayList<PropertyTypes>();
			d.setPropertyTypes(types);
		}
		PropertyTypes pType = null;
		for (PropertyTypes pt : types) {
			if (pt.getType().equals(type)) {
				pType = pt;
				break;
			}
		}
		if (pType == null) {
			pType = new PropertyTypes();
			pType.setType(type);
			types.add(pType);
		}

		if (pType.getEnumDef() == null)
			pType.setEnumDef(new ArrayList<String>());

		for (String enumD : enums.keySet()) {
			pType.getEnumDef().add(enumD);
		}

	}

}
