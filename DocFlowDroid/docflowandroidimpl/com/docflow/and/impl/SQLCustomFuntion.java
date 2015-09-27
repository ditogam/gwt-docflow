package com.docflow.and.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import jsqlite.Function;
import jsqlite.FunctionContext;
import bsh.Interpreter;

public class SQLCustomFuntion implements Function {
	private bsh.Interpreter bsh = null;
	private String function_name;
	private int param_count;
	private String bean_shell;
	private String replace_function;

	private ArrayList<Integer> databases;

	public SQLCustomFuntion(String function_name, int param_count,
			String bean_shell, String replace_function) {
		super();
		this.function_name = function_name;
		this.param_count = param_count;
		this.bean_shell = bean_shell;
		this.replace_function = replace_function;
		bsh = new Interpreter();
		databases = new ArrayList<Integer>();
	}

	@Override
	public void function(FunctionContext fc, String[] args) {
		try {
			bsh.set("args", args);
			String s = bean_shell;
			Object o = bsh.eval(s);
			o = bsh.eval(function_name + "()");
			if (o != null) {
				if (o instanceof Number) {
					fc.set_result(((Number) o).doubleValue());
				} else {
					if (o instanceof byte[])
						fc.set_result(((byte[]) o));
					else {
						fc.set_error(o.toString());
					}
				}
			}

		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String error = sw.toString();
			fc.set_error(error);
		}
	}

	@Override
	public void step(FunctionContext fc, String[] args) {

	}

	@Override
	public void last_step(FunctionContext fc) {

	}

	public String addDatabaseFunction(jsqlite.Database db, String sql) {
		try {
			int db_instance_id = db.hashCode();
			if (replace_function != null && sql.contains(replace_function))
				sql = sql.replaceAll(replace_function, function_name);
			if (sql.contains(function_name)) {
				if (databases.contains(db_instance_id))
					return sql;
				db.create_function(function_name, param_count, this);
				databases.add(db_instance_id);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sql;
	}

}
