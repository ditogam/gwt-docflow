package com.docflowdroid.common.listenerinvoker;

import java.lang.reflect.Method;
import java.util.HashMap;

public class AListenerMethode {
	private AListenerParam[] params;
	private String methodeName;
	private String onMethodeName;
	private Method method;
	private HashMap<Integer, AListenerParam> args = null;

	public AListenerMethode(String methodeName, String onMethodeName,
			AListenerParam[] params) {
		this.params = params;
		this.onMethodeName = onMethodeName;
		this.methodeName = methodeName;
	}

	public AListenerMethode(String methodeName, AListenerParam[] params) {
		this.params = params;
		this.methodeName = methodeName;
	}

	void setup(Class<?> clazz) throws Exception {
		if (params == null)
			params = new AListenerParam[0];
		Class<?>[] paramsTypes = new Class<?>[params.length];
		for (int i = 0; i < paramsTypes.length; i++) {
			paramsTypes[i] = params[i].getClazz();
		}
		method = clazz.getDeclaredMethod(methodeName, paramsTypes);
		args = new HashMap<Integer, AListenerParam>();
		for (AListenerParam p : params) {
			if (!p.isMethodeArgument())
				continue;
			args.put(p.getArg_index(), p);
		}

	}

	void putParamValue(int index, Object value) {
		AListenerParam p = args.get(index);
		if (p != null)
			p.setValue(value);
	}

	void invokeMethode(Object instance) throws Exception {
		Object[] arguments = new Object[params.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = params[i].getValue();
		}
		method.invoke(instance, arguments);
	}

	public String getMethodeName() {
		return methodeName;
	}

	public String getOnMethodeName() {
		return onMethodeName;
	}

	public void setOnMethodeName(String onMethodeName) {
		this.onMethodeName = onMethodeName;
	}
}
