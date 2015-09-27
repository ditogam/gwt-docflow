package com.docflow.server.corector;

import java.util.Map;
import java.util.Set;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class CorectorDMI {

	@SuppressWarnings("unchecked")
	public DSResponse update(DSRequest req) throws Exception {
		Map<String, Object> values = (Map<String, Object>) req.getValues();
		Map<String, Object> oldValus = (Map<String, Object>) req.getOldValues();
		Set<String> keys = oldValus.keySet();
		for (String key : keys) {
			if (!values.containsKey(key))
				values.put(key, oldValus.get(key));
		}
		return new DSResponse(values);
	}

}
