package com.rdcommon.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.isomorphic.sql.SQLDataSource;
import com.rdcommon.client.ClientFieldDef;
import com.rdcommon.shared.ds.DSClientFieldDef;
import com.rdcommon.shared.ds.DSComponent;
import com.rdcommon.shared.ds.DSDefinition;
import com.rdcommon.shared.ds.DSField;
import com.rdcommon.shared.ds.DSFormDefinition;
import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.ValueEnum;

public class Test {
	public static final DSDefinition ds = new DSDefinition();

	static {
		// try {
		// CompileString.createClass();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (CannotCompileException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		ds.setId(1);
		ds.setDsName("DocTypeDS");
		ds.setTableName("v_doc_type_group");
		ds.setOperationBindings(new ArrayList<String>());
		ds.setServerObjectClassName("com.rdcommon.server.TestDMI");
		// ds.getOperationBindings()
		// .add("<operationBinding operationId=\"fetch\" operationType=\"fetch\"\n"
		// + "            serverMethod=\"fetch\" >\n"
		// +
		// "            <serverObject className=\"com.rdcommon.server.TestDMI\" lookupStyle=\"new\" />\n"
		// + "</operationBinding>");
		// System.out.println(ds.getOperationBindings().get(0));
		ds.setOperationBindings(new ArrayList<String>());
		DSFormDefinition form = new DSFormDefinition();
		form.setName("main");
		form.setDsGroups(new ArrayList<DSGroup>());
		DSGroup gr = new DSGroup();
		DSComponent dynamicFieldProps = new DSComponent();
		dynamicFieldProps.setAdditionalProps(new TreeMap<String, String>());
		dynamicFieldProps.getAdditionalProps().put("groupTitle",
				"5px solid green");
		dynamicFieldProps.getAdditionalProps().put("titleOrientation", "top");
		gr.setDynamicFieldProps(dynamicFieldProps);

		gr.setAdditionalProps(new TreeMap<String, String>());

		gr.getAdditionalProps().put("border", "5px solid green");
		// gr.setWidth(-100);
		// gr.setHeight(-100);
		gr.setName("main");
		form.getDsGroups().add(gr);

		ds.setSearchForms(new ArrayList<DSFormDefinition>());
		ds.getSearchForms().add(form);

		ds.setDsFields(new ArrayList<DSField>());

		gr.setAdditionalProps(new TreeMap<String, String>());

		gr.getAdditionalProps().put("border", "1px solid red");
		DSField field;
		DSClientFieldDef cl;

		field = new DSField();
		field.setfName("id");
		field.setfTitle("ID");
//		field.setDsfType("integer");
		field.setPrimaryKey(true);

		cl = new DSClientFieldDef();
		cl.setName(field.getfName());
		cl.setTitle(field.getfName());
		cl.setType(ClientFieldDef.FT_INTEGER);
		cl.setGroupName("main");
		cl.setHidden(true);

		field.setSearchProps(cl);
		ds.getDsFields().add(field);

		field = new DSField();
		field.setfName("caption_value");
		field.setfTitle("Caption");
		// field.setDsfType("text");

		cl = new DSClientFieldDef();
		cl.setName(field.getfName());
		cl.setTitle(field.getfTitle());
		cl.setGroupName("main");
		cl.setType(ClientFieldDef.FT_SELECTION);
		cl.setDsName("DocTypeDS");
		cl.setAdditionalProps(new TreeMap<String, String>());
		// cl.getAdditionalProps().put("canEdit", "dfsdfsdf");
		// cl.setReadOnly(true);
		// cl.setWidth(500);
		cl.setDsIdField("id");
		cl.setDsValueField("caption_value");
		cl.setDsIsCustomGenerated(true);
		field.setSearchProps(cl);
		ds.getDsFields().add(field);
		ArrayList<DSDefinition> def = new ArrayList<DSDefinition>();
		def.add(ds);
		// DSGenerator.getInstance().registerDSs(def);
	}

	static class PropertyDescriptionskk {
		public String name;
		public TreeMap<String, TreeMap<String, String>> types = new TreeMap<String, TreeMap<String, String>>();

		public String toString(int type) {
			String nm = name.substring("set".length());
			nm = (nm.charAt(0) + "").toLowerCase() + nm.substring(1);
			String ret = "insert into dsdefinition.property_names values ('"
					+ nm + "'," + type + ",'";
			String values = "";
			for (String key : types.keySet()) {
				if (values.length() > 0)
					values += ";";
				values += key;
				String ens = "";
				for (String eKey : types.get(key).keySet()) {
					if (ens.length() > 0)
						ens += ",";
					ens += eKey;
				}
				values += ens.length() > 0 ? ":" + ens : "";
			}
			ret += values + "');";
			return ret;
		}
	}

	private static void setClassValues(
			TreeMap<String, PropertyDescriptionskk> map, String methodeName,
			String type, TreeMap<String, String> enums) {

		PropertyDescriptionskk d = map.get(methodeName);
		if (d == null) {
			d = new PropertyDescriptionskk();
			map.put(methodeName, d);
			d.name = methodeName;
		}
		TreeMap<String, String> renums = d.types.get(type);
		if (renums == null) {
			renums = new TreeMap<String, String>();
			d.types.put(type, renums);
		}
		for (String key : enums.keySet()) {
			renums.put(key, key);
		}

	}

	public static void main(String[] args) {
		TreeMap<String, PropertyDescriptionskk> map = new TreeMap<String, Test.PropertyDescriptionskk>();
		Class<?> clazz = DataSource.class;
		getMethodeNames(map, clazz);

		// clazz = Canvas.class;
		// getMethodeNames(map, clazz);
		// clazz = BaseWidget.class;
		// getMethodeNames(map, clazz);

		for (String key : map.keySet()) {
			System.out.println(map.get(key).toString(7));
		}
	}

	private static void getMethodeNames(
			TreeMap<String, PropertyDescriptionskk> map, Class<?> clazz) {
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

}
