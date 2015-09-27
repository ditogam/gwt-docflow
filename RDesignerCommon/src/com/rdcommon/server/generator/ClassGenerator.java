package com.rdcommon.server.generator;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javassist.ClassPool;
import javassist.CtClass;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.rdcommon.server.ClassFileManager;
import com.rdcommon.shared.ClassDefinition;

public class ClassGenerator {
	public static void generateClasses(List<ClassDefinition> classDefinitions)
			throws Exception {
		for (ClassDefinition classDefinition : classDefinitions) {
			generateClass(classDefinition);
		}
	}

	private static void generateClass(ClassDefinition classDefinition)
			throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		Iterable<? extends JavaFileObject> fileObjects;
		String className = classDefinition.getPackage_name() + '.'
				+ classDefinition.getClass_name();

		fileObjects = getJavaSourceFromString(className,
				classDefinition.getCode());

		ClassFileManager fileManager = new ClassFileManager(
				compiler.getStandardFileManager(null, null, null));

		compiler.getTask(null, fileManager, null, null, null, fileObjects)
				.call();
		byte[] bytes = fileManager.getBytes();
		ClassPool cp = ClassPool.getDefault();
		CtClass k = cp.makeClass(new ByteArrayInputStream(bytes));
		@SuppressWarnings("unused")
		Class<?> kkk = k.toClass();
		Class.forName(className);
	}

	static Iterable<JavaSourceFromString> getJavaSourceFromString(String name,
			String code) {
		final JavaSourceFromString jsfs;
		jsfs = new JavaSourceFromString(name, code);
		return new Iterable<JavaSourceFromString>() {
			public Iterator<JavaSourceFromString> iterator() {
				return new Iterator<JavaSourceFromString>() {
					boolean isNext = true;

					public boolean hasNext() {
						return isNext;
					}

					public JavaSourceFromString next() {
						if (!isNext)
							throw new NoSuchElementException();
						isNext = false;
						return jsfs;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("memory:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		System.out.println(uri);
		this.code = code;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}