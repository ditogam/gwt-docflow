package com.docflowdroid.common.ds;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CDataClass implements IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8830546768450305733L;
	private Map<String, Object> attributes;

	public CDataClass() {
		setAttributes(new HashMap<String, Object>());
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setAttribute(String property, Object value) {
		if (value == null) {
			attributes.remove(property);
			return;
		}
		attributes.put(property, value);
	}

	public void setAttribute(String property, String value) {
		setAttribute(property, (Object) value);
	}

	public void setAttribute(String property, int value) {
		setAttribute(property, new Integer(value));
	}

	public void setAttribute(String property, double value) {
		setAttribute(property, new Double(value));
	}

	public void setAttribute(String property, long value) {
		setAttribute(property, new Long(value));
	}

	public void setAttribute(String property, boolean value) {
		setAttribute(property, new Boolean(value));
	}

	public void setAttribute(String property, Date value) {
		setAttribute(property, (Object) value);
	}

	public void setAttribute(String property, Integer value) {
		setAttribute(property, (Object) value);
	}

	public void setAttribute(String property, Double value) {
		setAttribute(property, (Object) value);
	}

	public void setAttribute(String property, Long value) {
		setAttribute(property, (Object) value);
	}

	public void setAttribute(String property, Boolean value) {
		setAttribute(property, (Object) value);
	}

	public String getAttribute(String property) {
		Object obj = attributes.get(property);
		return obj == null ? null : obj.toString();
	}

	public String getAttributeAsString(String property) {
		return getAttribute(property);
	}

	public Integer getAttributeAsInt(String property) {
		Object obj = attributes.get(property);
		return obj == null ? null : new Integer(obj.toString());
	}

	public Boolean getAttributeAsBoolean(String property) {
		Object obj = attributes.get(property);
		return obj == null ? null : new Boolean(obj.toString());
	}

	public Double getAttributeAsDouble(String property) {
		Object obj = attributes.get(property);
		return obj == null ? null : new Double(obj.toString());
	}

	public Long getAttributeAsLong(String property) {
		Double dVal = this.getAttributeAsDouble(property);
		return dVal == null ? null : dVal.longValue();
	}

	public Float getAttributeAsFloat(String property) {
		Double dVal = this.getAttributeAsDouble(property);
		return dVal == null ? null : dVal.floatValue();
	}

	public Date getAttributeAsDate(String property) {
		Object obj = attributes.get(property);
		return obj == null ? null : (Date) obj;
	}

	public Object getAttributeAsObject(String property) {
		return attributes.get(property);
	}
}
