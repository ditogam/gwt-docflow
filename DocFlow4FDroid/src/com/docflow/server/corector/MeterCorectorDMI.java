package com.docflow.server.corector;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class MeterCorectorDMI {

	public DSResponse addEditCorector(DSRequest req) throws Exception {
		DSResponse resp = null;
		DSRequest newReq = new DSRequest();
		newReq.setOldValues(req.getOldValues());
		newReq.setValues(req.getValues());
		newReq.setCriteria(req.getCriteria());
		newReq.setOperationType(req.getOperationType());
		newReq.setDataSource(req.getDataSource());
		resp = newReq.execute();
		return resp;
	}

}
