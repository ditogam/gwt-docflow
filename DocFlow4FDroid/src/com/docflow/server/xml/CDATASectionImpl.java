package com.docflow.server.xml;

import com.google.gwt.xml.client.CDATASection;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;

public class CDATASectionImpl implements CDATASection {

	private org.w3c.dom.CDATASection node;

	public CDATASectionImpl(org.w3c.dom.CDATASection node) {
		this.node = node;
	}

	@Override
	public Text splitText(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendData(String appendedData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteData(int offset, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return node.getData();
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return node.getLength();
	}

	@Override
	public void insertData(int offset, String insertedData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replaceData(int offset, int count, String replacementData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setData(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String substringData(int offset, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node appendChild(Node newChild) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedNodeMap getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeList getChildNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getLastChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getNodeType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNodeValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getOwnerDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getParentNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void normalize() {
		// TODO Auto-generated method stub

	}

	@Override
	public Node removeChild(Node oldChild) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNodeValue(String nodeValue) {
		// TODO Auto-generated method stub

	}

}
