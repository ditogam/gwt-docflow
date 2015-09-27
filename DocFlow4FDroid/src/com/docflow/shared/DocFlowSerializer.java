package com.docflow.shared;

import java.util.HashMap;

import com.common.shared.ClSelectionItem;
import com.googlecode.xremoting.core.xstream.XStreamSerializer;

public class DocFlowSerializer extends XStreamSerializer {

	public DocFlowSerializer() {
		super();
		xstream.aliasPackage("comsh", "com.common.shared");
		xstream.aliasPackage("dfsh", "com.docflow.shared");
	}

	public static void main(String[] args) {
		DocFlowSerializer des = new DocFlowSerializer();
		HashMap<Integer, ClSelectionItem[]> map = new HashMap<Integer, ClSelectionItem[]>();
		ClSelectionItem[] list = new ClSelectionItem[10];
		for (int i = 0; i < list.length; i++) {
			list[i] = new ClSelectionItem();
			list[i].setId(i + 1);
			list[i].setValue("AAA_" + list[i].getId());
		}
		map.put(1, list);

		list = new ClSelectionItem[5];
		for (int i = 0; i < list.length; i++) {
			list[i] = new ClSelectionItem();
			list[i].setId(i + 1);
			list[i].setValue("AAA_" + list[i].getId());
		}
		map.put(2, list);
		String s = des.xstream.toXML(map);
		System.out.println(s);
	}
}
