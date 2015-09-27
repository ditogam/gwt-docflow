package com.docflowdroid.common.ds;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CDSRequest extends CDataClass implements IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4171841592929493860L;

	private transient String dsName;
	private transient Map<String, Object> criteria;

	public CDSRequest(String dsName, Map<String, Object> criteria,
			String operationId) {
		this(operationId);
		this.dsName = dsName;
		this.criteria = criteria;
	}

	public CDSRequest() {
		super();
	}

	public CDSRequest(String operationId) {
		this();
		setOperationId(operationId);
	}

	public String getOperationId() {
		return getAttribute("operationId");
	}

	public void setOperationId(String operationId) {
		setAttribute("operationId", operationId);
	}

	public Long getStartRow() {
		return getAttributeAsLong("startRow");
	}

	public void setStartRow(Long startRow) {
		setAttribute("startRow", startRow);
	}

	public Long getEndRow() {
		return getAttributeAsLong("endRow");
	}

	public void setEndRow(Long endRow) {
		setAttribute("endRow", endRow);
	}

	public String getDsName() {
		return dsName;
	}

	public Map<String, Object> getCriteria() {
		return criteria;
	}
}
