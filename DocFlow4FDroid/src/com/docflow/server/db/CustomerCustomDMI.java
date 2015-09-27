package com.docflow.server.db;

import java.util.List;

import com.docflow.server.DMIUtils;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class CustomerCustomDMI {

	public DSResponse remove(DSRequest req) throws Exception {
		List<?> oldValues = req.getOldValueSets();
		DSResponse responce = new DSResponse(oldValues);
		// on a removal, just return
		return responce;
	}

	public DSResponse add(DSRequest req) throws Exception {
		DSResponse responce = new DSResponse(req.getValues());
		return responce;
	}

	public DSResponse update(DSRequest req) throws Exception {

		DSResponse responce = new DSResponse(DMIUtils.findRecordById(
				req.getDataSourceName(), null, req.getValues().get("cusid"),
				"cusid"));
		return responce;
	}
}
