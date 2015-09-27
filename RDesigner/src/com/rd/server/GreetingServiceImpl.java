package com.rd.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.rd.client.GreetingService;
import com.rdcommon.server.DSGenerator;
import com.rdcommon.shared.GlobalValues;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	@Override
	public GlobalValues getGlobalValues() throws Exception {
		DSGenerator.getDSDefiniString();
		return DSGenerator.globalValues;
	}
}
