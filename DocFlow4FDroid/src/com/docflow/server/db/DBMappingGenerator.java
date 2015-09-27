package com.docflow.server.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.docflow.shared.hr.Captions;

public class DBMappingGenerator {
	private String makeFirstLetterUpperCase(String value) {

		char[] fn = value.toCharArray();
		fn[0] = (fn[0] + "").toUpperCase().charAt(0);
		return new String(fn);
	}

	public DBMappingGenerator(@SuppressWarnings("rawtypes") Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder("public static "
				+ clazz.getSimpleName() + " get" + clazz.getSimpleName()
				+ "(ResultSet rs) throws Exception {\n");
		sb.append("\t\t" + clazz.getSimpleName() + " result=new "
				+ clazz.getSimpleName() + "();\n");
		for (Field field : fields) {

			if (Modifier.isStatic(field.getModifiers()))
				continue;
			String fieldName = makeFirstLetterUpperCase(field.getName());

			sb.append("\t\tresult.set" + fieldName + "(rs.get"
					+ makeFirstLetterUpperCase(field.getType().getSimpleName())
					+ "(\"" + fieldName.toLowerCase() + "\"));\n");

		}
		sb.append("\t\treturn result;\n}");
		System.out.println(sb);
	}

	public static void main(String[] args) {
		new DBMappingGenerator(Captions.class);
	}
}
