package com.docflow.shared.common;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class FormGroup implements IsSerializable {

	/**
	 * 
	 */

	public static final int FTORIENTATION_LEFT = -1;
	public static final int FTORIENTATION_TOP = 0;
	public static final int FTORIENTATION_RIGHT = 1;

	private String groupTitle;
	private int labelOrientation;
	private int fieldCaptionId;
	private String groupWidth;
	private String groupHeight;
	private int numofColumns;
	private int newLine;

	private ArrayList<FieldDefinition> fieldDefinitions;

	public int getFieldCaptionId() {
		return fieldCaptionId;
	}

	public ArrayList<FieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public int getLabelOrientation() {
		return labelOrientation;
	}

	public void setFieldCaptionId(int fieldCaptionId) {
		this.fieldCaptionId = fieldCaptionId;
	}

	public void setFieldDefinitions(ArrayList<FieldDefinition> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public void setLabelOrientation(int labelOrientation) {
		this.labelOrientation = labelOrientation;
	}

	//
	// public void setXml(String str) {
	// XMLElement el = new XMLElement();
	// el.parseString(str);
	// setXml(el);
	// }
	//
	public void setXml(Node el) {
		groupTitle = XMLParceserHelper.getAttribute("groupTitle", el);
		groupWidth = XMLParceserHelper.getAttribute("groupWidth", el);
		groupHeight = XMLParceserHelper.getAttribute("groupHeight", el);
		labelOrientation = XMLParceserHelper
				.getIntValue("labelOrientation", el);
		fieldCaptionId = XMLParceserHelper.getIntValue("fieldCaptionId", el);
		numofColumns = XMLParceserHelper.getIntValue("numofColumns", el);
		newLine = XMLParceserHelper.getIntValue("newLine", el);
		NodeList nodeList = el.getChildNodes();
		fieldDefinitions = new ArrayList<FieldDefinition>();
		this.setFieldDefinitions(fieldDefinitions);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				FieldDefinition g = new FieldDefinition();
				g.setXml(n);
				fieldDefinitions.add(g);
			}

		}
	}

	public Element toXmlElement(Document doc) {
		Element el = doc.createElement("Group");

		if (groupTitle != null && groupTitle.trim().length() != 0)
			el.setAttribute("groupTitle", groupTitle);
		if (groupHeight != null && groupHeight.trim().length() != 0)
			el.setAttribute("groupHeight", groupHeight);
		if (groupWidth != null && groupWidth.trim().length() != 0)
			el.setAttribute("groupWidth", groupWidth);
		if (fieldCaptionId != 0)
			el.setAttribute("fieldCaptionId", fieldCaptionId + "");
		if (newLine != 0)
			el.setAttribute("newLine", newLine + "");
		if (labelOrientation != 0)
			el.setAttribute("labelOrientation", labelOrientation + "");
		if (numofColumns != 0)
			el.setAttribute("numofColumns", numofColumns + "");
		ArrayList<FieldDefinition> fieldDefinitions = getFieldDefinitions();

		if (fieldDefinitions != null)
			for (FieldDefinition fieldDefinition : fieldDefinitions) {
				el.appendChild(fieldDefinition.toXmlElement(doc));
			}

		return el;
	}

	public String toXmlString(Document doc) {
		return toXmlElement(doc).toString();
	}

	public String getGroupWidth() {
		return groupWidth;
	}

	public void setGroupWidth(String groupWidth) {
		this.groupWidth = groupWidth;
	}

	public int getNewLine() {
		return newLine;
	}

	public void setNewLine(int newLine) {
		this.newLine = newLine;
	}

	public String getGroupHeight() {
		return groupHeight;
	}

	public void setGroupHeight(String groupHeight) {
		this.groupHeight = groupHeight;
	}

	public int getNumofColumns() {
		return numofColumns;
	}

	public void setNumofColumns(int numofColumns) {
		this.numofColumns = numofColumns;
	}
}
