package com.docflowdroid.common.listenerinvoker;

public class AListenerParam {
	private Class<?> clazz;
	private Object value;
	private Integer arg_index;

	public AListenerParam(Class<?> clazz, int arg_index) {
		super();
		this.clazz = clazz;
		this.arg_index = arg_index;
	}

	public AListenerParam(Class<?> clazz, Object value) {
		super();
		this.clazz = clazz;
		this.value = value;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Integer getArg_index() {
		return arg_index;
	}

	public Object getValue() {
		return value;
	}

	public boolean isMethodeArgument() {
		return arg_index != null && arg_index.intValue() >= 0;
	}

	void setValue(Object value) {
		this.value = value;
	}

}
