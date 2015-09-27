package com.docflowdroid.common.listenerinvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class AListener implements InvocationHandler {
	private Object instance;
	private Class<?> clazz;
	private AListenerMethode[] methodes;
	private HashMap<String, AListenerMethode> map;

	public AListener(Object instance, AListenerMethode[] methodes)
			throws Exception {
		super();
		this.instance = instance;
		this.methodes = methodes;
		checkInstance();
	}

	public AListener(Class<?> clazz, AListenerMethode[] methodes)
			throws Exception {
		super();
		this.clazz = clazz;
		this.methodes = methodes;
		checkInstance();
	}

	private void checkInstance() throws Exception {
		if (instance != null) {
			clazz = instance.getClass();
		}
		map = new HashMap<String, AListenerMethode>();
		for (AListenerMethode m : methodes) {
			map.put(m.getOnMethodeName(), m);
			m.setup(clazz);
		}
	}

	protected void putParamValue(String methodeName, int index, Object value) {
		AListenerMethode m = map.get(methodeName);
		if (m != null)
			m.putParamValue(index, value);
	}

	protected void invokeMethode(String methodeName) {
		try {
			AListenerMethode m = map.get(methodeName);
			if (m != null)
				m.invokeMethode(instance);
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	protected void putParamsAndInvoke(String methodeName, Object[] args)
			throws Exception {
		AListenerMethode m = map.get(methodeName);
		if (m != null) {
			if (args != null)
				for (int i = 0; i < args.length; i++) {
					m.putParamValue(i, args[i]);
				}
			m.invokeMethode(instance);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T createListener(Class<T> clazz) {
		Object o = Proxy.newProxyInstance(this.clazz.getClassLoader(),
				new Class[] { clazz }, this);
		return (T) o;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		putParamsAndInvoke(method.getName(), args);
		return null;
	}

}
