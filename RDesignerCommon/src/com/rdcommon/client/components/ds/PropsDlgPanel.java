package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSCProp;
import com.smartgwt.client.widgets.Window;

public class PropsDlgPanel extends CommonSavePanel {

	private GRProperty propP;

	public PropsDlgPanel(DSCProp prop, int type) {
		propP = new GRProperty(prop, type);
		propP.setWidth100();
		propP.setHeight100();
		this.addMember(propP);
	}

	@Override
	public void saveData(Window win) throws Exception {
		propP.saveData();
	}

}
