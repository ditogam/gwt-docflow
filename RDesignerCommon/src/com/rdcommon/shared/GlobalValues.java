package com.rdcommon.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.rdcommon.shared.ds.DSDefinition;
import com.rdcommon.shared.props.PropertyNames;

public class GlobalValues implements IsSerializable {

	private ArrayList<DSDefinition> dsDefinitions;
	private List<ClassDefinition> classDefinitions;
	private List<JSDefinition> jsDefinitions;
	private TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames;

	public GlobalValues() {
		// TODO Auto-generated constructor stub
	}

	public GlobalValues(ArrayList<DSDefinition> dsDefinitions,
			List<ClassDefinition> classDefinitions,
			List<JSDefinition> jsDefinitions,
			TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames) {
		this.dsDefinitions = dsDefinitions;
		this.classDefinitions = classDefinitions;
		this.jsDefinitions = jsDefinitions;
		this.setPropertyNames(propertyNames);
	}

	public ArrayList<DSDefinition> getDsDefinitions() {
		return dsDefinitions;
	}

	public void setDsDefinitions(ArrayList<DSDefinition> dsDefinitions) {
		this.dsDefinitions = dsDefinitions;
	}

	public List<ClassDefinition> getClassDefinitions() {
		return classDefinitions;
	}

	public void setClassDefinitions(List<ClassDefinition> classDefinitions) {
		this.classDefinitions = classDefinitions;
	}

	public List<JSDefinition> getJsDefinitions() {
		return jsDefinitions;
	}

	public void setJsDefinitions(List<JSDefinition> jsDefinitions) {
		this.jsDefinitions = jsDefinitions;
	}

	public TreeMap<Integer, TreeMap<String, PropertyNames>> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(
			TreeMap<Integer, TreeMap<String, PropertyNames>> propertyNames) {
		this.propertyNames = propertyNames;
	}
}
