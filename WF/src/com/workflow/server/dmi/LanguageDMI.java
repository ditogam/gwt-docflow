package com.workflow.server.dmi;

import javax.servlet.http.HttpServletRequest;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class LanguageDMI {
	private static final String language_id = "language_id";

	public static DSResponse updatelanguageid(DSRequest dsRequest,
			HttpServletRequest servletRequest) throws Exception {
		servletRequest.getSession().setAttribute(language_id,
				dsRequest.getValues().get(language_id));
		return new DSResponse(dsRequest.getValues());
	}
}
