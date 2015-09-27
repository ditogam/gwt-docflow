package com.docflow.client.components.usermanager;

import com.smartgwt.client.widgets.Window;

public class WUsermanagment extends Window {

	public static void showWindow() {
		WUsermanagment w = new WUsermanagment();
		w.show();
	}

	public WUsermanagment() {
		PUserManager um = new PUserManager();
		um.refresh();
		this.addItem(um);

		this.setSize("700", "600");
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}
}
