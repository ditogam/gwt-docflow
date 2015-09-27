package com.docflow.server.db;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class FakeDBOperations {
	public DSResponse update(DSRequest req) throws Exception {
		DSResponse resp = new DSResponse();
		resp.setStatus(DSResponse.STATUS_SUCCESS);
		return resp;
	}
}
