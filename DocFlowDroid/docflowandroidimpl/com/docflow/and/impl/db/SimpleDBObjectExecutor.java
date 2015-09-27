package com.docflow.and.impl.db;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import com.docflow.server.db.DBMapping;

public class SimpleDBObjectExecutor<T> extends ADBResultObjectExecutor<T> {

	private Method method;
	private Class<?> dbClass;

	public SimpleDBObjectExecutor(ExecutorConstructor constructor,
			Class<?> dbClass, String methode_name) throws Exception {
		super(constructor);
		this.dbClass = dbClass;
		setMethod(methode_name);
	}

	public SimpleDBObjectExecutor(ExecutorConstructor constructor,
			String methode_name) throws Exception {
		this(constructor, DBMapping.class, methode_name);
	}

	private void setMethod(String methode_name) throws Exception {
		method = dbClass.getDeclaredMethod(methode_name, ResultSet.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getResult(ResultSet rs) throws Exception {
		T val = (T) method.invoke(null, rs);
		return val;
	}

}
