package com.docflow.shared.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class FormDefinition implements IsSerializable {

	private boolean horizontal;

	private ArrayList<FormGroup> formGroups;

	@SuppressWarnings("unused")
	private void createString(StringBuilder root, Node el) {
		root.append("\n<" + el.getNodeName());

		NamedNodeMap map = el.getAttributes();
		int attrcount = 0;
		try {
			for (int i = 0; i < map.getLength(); i++) {
				attrcount++;
				root.append(" " + map.item(i).getNodeName() + "=\""
						+ map.item(i).getNodeValue() + "\"");
			}
		} catch (Exception e) {

		}
		if (el.getChildNodes().getLength() > 0) {
			root.append(">");
			for (int i = 0; i < el.getChildNodes().getLength(); i++) {
				createString(root, el.getChildNodes().item(i));
			}
			root.append("\n</" + el.getNodeName() + ">");
		} else {
			root.append(" />");
		}
	}

	public ArrayList<FormGroup> getFormGroups() {
		return formGroups;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setFormGroups(ArrayList<FormGroup> formGroups) {
		this.formGroups = formGroups;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public void setXml(Document doc) {
		Node rootElem = doc.getChildNodes().item(0);
		horizontal = XMLParceserHelper.getBoolean("horizontal", rootElem);
		NodeList nodeList = rootElem.getChildNodes();
		formGroups = new ArrayList<FormGroup>();
		this.setFormGroups(formGroups);
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			FormGroup g = new FormGroup();
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				g.setXml(n);
				formGroups.add(g);
			}
		}

	}

	public void setXml(String str) {
		Document doc = XMLParser.parse(str);
		setXml(doc);
	}

	// setXml(el);
	// }
	//
	// public void setXml(XMLElement el) {
	//
	// }

	public Document toXmlElement() {
		ArrayList<FormGroup> formGroups = getFormGroups();

		Document doc = XMLParser.createDocument();
		Element rootElem = doc.createElement("Form");
		if (horizontal)
			rootElem.setAttribute("horizontal", (horizontal ? 1 : 0) + "");
		doc.appendChild(rootElem);
		if (formGroups != null)
			for (FormGroup formGroup : formGroups) {
				rootElem.appendChild(formGroup.toXmlElement(doc));
			}

		return doc;
	}

	public String toXmlString() {
		Document d = toXmlElement();
		String str = "";
		StringBuilder sb = new StringBuilder();
		createString(sb, d.getFirstChild());
		str = sb.toString().trim();
		return str;
	}

	public static String getXml(HashMap<String, Object> data) {
		String ret = "";
		Document doc = XMLParser.createDocument();
		Element rootElem = doc.createElement("DocDef");
		doc.appendChild(rootElem);
		Set<String> keys = data.keySet();
		for (String key : keys) {
			Element val = doc.createElement("Val");
			val.setAttribute("key", key);
			Object obj = data.get(key);
			String value = "";
			String text = null;
			if (obj == null) {
				value = "";
			} else if (obj instanceof String[]) {
				String[] strings = (String[]) obj;
				if (strings.length > 0) {
					value = strings[0];
					if (strings.length > 1) {
						text = strings[1];
					}
				}
			} else {
				value = obj.toString();
			}
			val.setAttribute("value", value);
			if (text != null)
				val.setAttribute("text", text);
			rootElem.appendChild(val);
		}
		ret = doc.toString();
		return ret;
	}
}
