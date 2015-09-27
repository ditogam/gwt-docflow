package com.docflowdroid.common.ds;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CDSResponce extends CDSRequest implements IsSerializable {
	/**
	 * 
	 */
	private List<Map<String, Object>> result;

	public CDSResponce() {
		super();
	}

	public long getTotalRows() {
		return getAttributeAsLong("totalRows");
	}

	public void setTotalRows(Long totalRows) {
		setAttribute("totalRows", totalRows);
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}

}
