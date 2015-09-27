package com.rdcommon.client;

import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class CommonSavePanel extends VLayout {

	public abstract void saveData(Window win) throws Exception;

}
