package com.google.gwt.xml.client.impl;

import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

public class NamedNodeMapImpl implements NamedNodeMap {

	private org.w3c.dom.NamedNodeMap nodeMap;

	public NamedNodeMapImpl(org.w3c.dom.NamedNodeMap nodeMap) {
		this.nodeMap = nodeMap;
	}

	@Override
	public int getLength() {
		return nodeMap.getLength();
	}

	@Override
	public Node getNamedItem(String name) {
		org.w3c.dom.Node node = nodeMap.getNamedItem(name);
		return node == null ? null : NodeImpl.build(node);
	}

	@Override
	public Node item(int index) {
		org.w3c.dom.Node node = nodeMap.item(index);
		return node == null ? null : NodeImpl.build(node);
	}

}
