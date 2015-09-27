package com.rdcommon.server;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class SimpleCompileTest {
	public static void main(String[] args) {
		String fileToCompile = "test" + java.io.File.separator + "MyClass.java";
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult = compiler.run(null, null, null, fileToCompile);
		if (compilationResult == 0) {
			System.out.println("Compilation is successful");
		} else {
			System.out.println("Compilation Failed");
		}
	}
}