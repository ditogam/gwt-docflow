package com.docflow.client.components.common;

import com.docflow.client.DocFlow;
import com.docflow.shared.User_Data;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.HTMLPane;

public class SplashDialog extends Dialog {
	private static SplashDialog splashDialog = null;
	private static Timer t;

	public static void hideSplash() {
		if (splashDialog == null)
			return;
		User_Data ud = DocFlow.user_obj.getUser_Data();
		if (ud != null && ud.getShowtimeout() > 0) {
			if (t != null)
				t.cancel();
			t = new Timer() {

				@Override
				public void run() {
					this.cancel();
					splashDialog.hide();
				}
			};
			t.schedule(ud.getShowtimeout());
		} else
			splashDialog.hide();
	}

	public static void showSplash() {
		if (splashDialog == null)
			splashDialog = new SplashDialog();

		String width = "120";
		String height = "80";
		String html = "<b>Loading<b><img src=\"images/loading.gif\" width=\"16\" height=\"16\" style=\"margin-right:8px;float:left;vertical-align:top;\"/>";
		String border = "1px solid gray";
		System.err.println(html);
		splashDialog.setPanelData(width, height, html, border,
				splashDialog.pane);
		splashDialog.centerInPage();
		splashDialog.show();
	}

	HTMLPane pane;

	public SplashDialog() {
		this.setShowHeader(false);
		String width = "120";
		String height = "80";
		String html = "<b>Loading<b><img src=\"images/loading.gif\" width=\"16\" height=\"16\" style=\"margin-right:8px;float:left;vertical-align:top;\"/>";

		String border = "1px solid gray";
		pane = new HTMLPane();
		this.setShowEdges(false);
		setPanelData(width, height, html, border, pane);

		pane.setWidth100();
		pane.setHeight100();

		this.addItem(pane);
		this.setAutoCenter(true);
		this.setIsModal(true);

	}

	public void setPanelData(String width, String height, String html,
			String border, HTMLPane pane) {
		User_Data ud = DocFlow.user_obj.getUser_Data();
		if (ud != null) {
			if (ud.getPwith() != null && ud.getPwith().trim().length() > 0)
				width = ud.getPwith();
			if (ud.getPheight() != null && ud.getPheight().trim().length() > 0)
				height = ud.getPheight();
			if (ud.getHtml() != null && ud.getHtml().trim().length() > 0)
				html = ud.getHtml();
			if (ud.getPborder() != null && ud.getPborder().trim().length() > 0)
				border = ud.getPborder();
		}

		this.setSize(width, height);
		pane.setBorder(border);
		pane.setContents(html);
		this.centerInPage();
	}
}
