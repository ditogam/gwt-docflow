package com.docflowdroid.common.listenerinvoker;

public class ClassCreator {

	public static <T> T createClass(Class<T> clazz, Object instance,
			AListenerMethode[] methodes) throws Exception {
		return (T) new AListener(instance, methodes).createListener(clazz);
	}

	public static <T> T createClass(Class<T> clazz, Object instance,
			AListenerMethode methode) throws Exception {
		return createClass(clazz, instance, new AListenerMethode[] { methode });
	}

	public static <T> T createClass(Class<T> clazz, Class<?> exucutor_class,
			AListenerMethode[] methodes) throws Exception {
		return (T) new AListener(exucutor_class, methodes)
				.createListener(clazz);
	}

	public static <T> T createClass(Class<T> clazz, Class<?> exucutor_class,
			AListenerMethode methode) throws Exception {
		return createClass(clazz, exucutor_class,
				new AListenerMethode[] { methode });
	}

}