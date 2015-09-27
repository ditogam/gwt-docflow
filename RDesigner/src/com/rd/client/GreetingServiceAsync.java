package com.rd.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.rdcommon.shared.GlobalValues;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {

	void getGlobalValues(AsyncCallback<GlobalValues> callback);
}
