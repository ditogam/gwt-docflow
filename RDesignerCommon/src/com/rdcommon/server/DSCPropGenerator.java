package com.rdcommon.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import nanoxml.XMLElement;

import com.rdcommon.shared.ds.DSCProp;

public class DSCPropGenerator {
	private static void collectFieldsAndMethodes(ArrayList<Field> fields,
			ArrayList<Method> methods, Class<?> clazz) {
		if (clazz.equals(DSCProp.class))
			return;
		Field[] myfields = clazz.getDeclaredFields();
		Method[] mymethods = clazz.getDeclaredMethods();

		for (Method method : mymethods) {
			methods.add(method);
		}
		for (Field field : myfields) {
			if (addModifier(field.getModifiers()))
				fields.add(field);
		}
		collectFieldsAndMethodes(fields, methods, clazz.getSuperclass());
	}

	private static boolean addModifier(int modifier) {
		return !(Modifier.isStatic(modifier) || Modifier.isFinal(modifier) || Modifier
				.isTransient(modifier));
	}

	private static final Class<?>[] simpleClasses = new Class[] {
			Boolean.class, String.class, Character.class, Byte.class,
			Short.class, Integer.class, Long.class, Float.class, Double.class };

	private static boolean equalClasses(Class<?> clazz, Class<?> clazz1,
			Class<?> clazz2) {
		if (clazz.equals(clazz1))
			return true;
		if (clazz.equals(clazz2))
			return true;
		return false;
	}

	public static Object getXmlValue(String value, Class<?> clazz) {
		if (value == null)
			return null;
		String val = value.toString().trim();
		if (clazz.equals(String.class))
			return value;
		if (equalClasses(clazz, Boolean.class, Boolean.TYPE))
			return (value.equals("1") || value.equalsIgnoreCase("true")) ? new Boolean(
					true) : new Boolean(false);
		if (equalClasses(clazz, Character.class, Character.TYPE)) {
			return new Character(val.length() > 0 ? val.charAt(0) : (char) 0);
		}
		if (equalClasses(clazz, Byte.class, Byte.TYPE)) {
			return new Byte(val);
		}
		if (equalClasses(clazz, Short.class, Short.TYPE)) {
			return new Short(val);
		}
		if (equalClasses(clazz, Integer.class, Integer.TYPE)) {
			return new Integer(val);
		}
		if (equalClasses(clazz, Long.class, Long.TYPE)) {
			return new Long(val);
		}
		if (equalClasses(clazz, Float.class, Float.TYPE)) {
			return new Float(val);
		}
		if (equalClasses(clazz, Double.class, Double.TYPE)) {
			return new Double(val);
		}
		return null;
	}

	public static boolean isPrimitive(Class<?> clazz) {
		if (clazz.isPrimitive())
			return true;

		for (Class<?> class1 : simpleClasses) {
			if (class1.equals(clazz))
				return true;
		}
		return false;
	}

	private static Method getMethod(ArrayList<Method> methods, String name) {
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(name)
					&& method.getParameterTypes().length == 0)
				return method;
		}
		return null;
	}

	private static Method setMethod(ArrayList<Method> methods, String name) {
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(name)
					&& method.getParameterTypes().length == 1)
				return method;
		}
		return null;
	}

	private static Field getField(ArrayList<Field> fields, String name) {
		for (Field field : fields) {
			if (field.getName().equalsIgnoreCase(name))
				return field;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static XMLElement generateXML(XMLElement elem, DSCProp prop) {
		ArrayList<Field> fields = new ArrayList<Field>();
		ArrayList<Method> methods = new ArrayList<Method>();
		collectFieldsAndMethodes(fields, methods, prop.getClass());
		if (elem == null)
			elem = new XMLElement(new Hashtable(),false, false);
		elem.setName(prop.getClass().getSimpleName());

		if (prop.getAdditionalProps() != null
				&& !prop.getAdditionalProps().isEmpty()) {
			Set<String> keys = prop.getAdditionalProps().keySet();
			for (String key : keys) {
				elem.setAttribute(key, prop.getAdditionalProps().get(key));
			}
		}
		for (Field field : fields) {
			char[] fieldName = field.getName().toCharArray();

			Object val = null;
			fieldName[0] = ((fieldName[0] + "").toUpperCase()).charAt(0);
			String sFieldName = new String(fieldName);
			String methodName = "get" + sFieldName;
			try {
				Method m = getMethod(methods, methodName);
				val = m.invoke(prop);
			} catch (Exception e) {

				e.printStackTrace();
				continue;
			}

			if (val == null)
				continue;
			if (isPrimitive(field.getType())) {
				elem.setAttribute(sFieldName, val);
			} else {
				XMLElement subElem = new XMLElement(new Hashtable(),false, false);
				subElem.setName(sFieldName);

				if (val instanceof DSCProp) {
					elem.addChild(subElem);
					subElem.addChild(generateXML(null, ((DSCProp) val)));
				} else if (val instanceof List) {
					elem.addChild(subElem);
					List<?> list = (List<?>) val;
					for (Object obj : list) {
						if (obj instanceof DSCProp)
							subElem.addChild(generateXML(null, ((DSCProp) obj)));
						else if (obj instanceof String)
							subElem.setAttribute("value", obj);

					}
				}
			}
		}
		return elem;
	}

	private static boolean isDSCProp(Class<?> clazz) {
		if (clazz == null)
			return false;
		if (clazz.equals(Object.class))
			return false;
		if (clazz.equals(DSCProp.class))
			return true;

		return isDSCProp(clazz.getSuperclass());
	}

	public static boolean isSupercalss(Class<?> clazz, Class<?> superClass) {
		if (clazz == null)
			return false;
		if (clazz.equals(Object.class))
			return false;
		if (clazz.equals(superClass))
			return true;

		return isSupercalss(clazz.getSuperclass(), superClass);
	}

	public static boolean isInstance(Class<?> clazz, Class<?> superClass) {
		if (clazz == null)
			return false;
		if (clazz.equals(Object.class))
			return false;
		if (clazz.equals(superClass))
			return true;
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> class1 : interfaces) {
			if (class1.equals(superClass))
				return true;
			if (isInstance(class1, superClass))
				return true;
		}

		return isSupercalss(clazz.getSuperclass(), superClass);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void loadFromXml(XMLElement elem, DSCProp prop) {
		ArrayList<Field> fields = new ArrayList<Field>();
		ArrayList<Method> methods = new ArrayList<Method>();
		collectFieldsAndMethodes(fields, methods, prop.getClass());
		Enumeration<?> en = elem.enumerateAttributeNames();
		if (prop.getAdditionalProps() == null)
			prop.setAdditionalProps(new TreeMap<String, String>());
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			String value = elem.getAttribute(key).toString();
			if (value == null)
				continue;
			value = value.trim();
			if (value.isEmpty())
				continue;
			Object val = null;
			try {
				Method m = setMethod(methods, "set" + key);
				Class<?> clazz = m.getParameterTypes()[0];
				val = getXmlValue(value, clazz);
				m.invoke(prop, val);
			} catch (Exception e) {
				prop.setValue(key, value);
			}
		}

		Vector<?> vec = elem.getChildren();
		for (Object object : vec) {
			XMLElement el = (XMLElement) object;
			String elName = el.getName();
			Field field = getField(fields, elName);
			if (field == null)
				continue;
			Class<?> clazz = field.getType();
			try {
				Object val = clazz.newInstance();
				Method m = setMethod(methods, "set" + field.getName());
				m.invoke(prop, val);
				if (val instanceof DSCProp) {
					loadFromXml((XMLElement) el.getChildren().get(0),
							((DSCProp) (val)));
				} else if (val instanceof List) {
					ParameterizedType clazzType = (ParameterizedType) field
							.getGenericType();
					Class<?> clazz1 = (Class<?>) clazzType
							.getActualTypeArguments()[0];
					List list = (List) val;
					if (isDSCProp(clazz1)) {
						Vector<?> childs = el.getChildren();
						for (Object chlds : childs) {
							DSCProp obj = (DSCProp) clazz1.newInstance();
							list.add(obj);
							loadFromXml((XMLElement) chlds, obj);
						}
					} else {
						if (clazz1.equals(String.class)) {
							Vector<?> childs = el.getChildren();
							for (Object chlds : childs) {
								XMLElement c = (XMLElement) chlds;
								list.add(c.getAttribute("value"));
							}
						}
					}
				}
			} catch (Exception e) {

			}

		}
	}
}
