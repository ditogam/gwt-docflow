package com.docflow.client.components.common;

import java.util.HashMap;

public interface ICalculatorClass {
	public void calculate(HashMap<String, FieldDefinitionItem> formitemMap,
			FieldDefinitionItem current);
}
