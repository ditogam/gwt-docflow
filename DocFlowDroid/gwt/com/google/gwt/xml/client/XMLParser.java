package com.google.gwt.xml.client;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import com.google.gwt.xml.client.impl.NodeImpl;

public class XMLParser {
	static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	public static Document parse(String xml) {

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			org.w3c.dom.Document doc = db.parse(is);
			return (Document) NodeImpl.build(doc);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public static Document createDocument() {
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document doc = db.newDocument();
			return (Document) NodeImpl.build(doc);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
