package com.google.gwt.xml.client.impl;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class NodeListImpl implements NodeList {
	private org.w3c.dom.NodeList nodeList;

	public NodeListImpl(org.w3c.dom.NodeList nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return nodeList.getLength();
	}

	@Override
	public Node item(int index) {
		return NodeImpl.build(nodeList.item(index));
	}

}
