package com.rdcommon.client;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;

public class CommonDialog extends Window {
	public CommonDialog(final CommonSavePanel csp, boolean destroOnHide,
			int height, int width, String title) {

		if (destroOnHide)
			addVisibilityChangedHandler(new VisibilityChangedHandler() {
				@Override
				public void onVisibilityChanged(VisibilityChangedEvent event) {
					if (!event.getIsVisible())
						destroy();

				}
			});
		SavePanel sp = new SavePanel(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					csp.saveData(CommonDialog.this);
					hide();
				} catch (Exception e) {
					SC.say(e.getMessage());
				}
			}
		}, this);
		this.setHeight(height);
		this.setWidth(width);
		this.setTitle(title);
		this.addItem(csp);
		sp.setHeight("30");
		sp.setWidth100();
		this.addItem(sp);
		this.centerInPage();
		setCanDragResize(true);
		this.setIsModal(true);
	}
}
