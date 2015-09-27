package com.docflow.server;

import com.docflow.shared.DocFlowSerializer;
import com.googlecode.xremoting.core.servlet.XRemotingServlet;
import com.googlecode.xremoting.core.spi.Serializer;

public class WDocFlowANDServer extends XRemotingServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1621477902119119076L;
	private DocFlowServiceImpl serviceImpl = null;

	@Override
	protected Object getTarget() {
		if (serviceImpl == null)
			serviceImpl = new DocFlowServiceImpl(this);

		return serviceImpl;
	}

	@Override
	protected Serializer createSerializer() {
		// TODO Auto-generated method stub
		return new DocFlowSerializer();
	}
}
