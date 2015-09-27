package com.docflow.client.components;

//import com.magticom.billing.web.client.session.ClientSession;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.BaseWidget;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class SavePanel extends VLayout {

	public IButton saveBtn;
	public IButton cancelBtn;
	public HLayout hl;

	public SavePanel(String saveTitle, ClickHandler saveClicked,
			String cancelTitle, ClickHandler cancelClicked) {
		if (saveTitle == null || saveTitle.trim().length() == 0)
			saveTitle = "OK";// ClientSession.getInstance().getCaption("save");
		if (cancelTitle == null || cancelTitle.trim().length() == 0)
			cancelTitle = "Close";// ClientSession.getInstance().getCaption("cancel");
		hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		saveBtn = new IButton(saveTitle);
		saveBtn.addClickHandler(saveClicked);
		cancelBtn = new IButton(cancelTitle);
		cancelBtn.addClickHandler(cancelClicked);
		hl.addMember(saveBtn);
		hl.addMember(cancelBtn);
		hl.setWidth100();
		hl.setMembersMargin(10);
		hl.setHeight("30");
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		hl.addMember(l);
		addMember(hl);
	}

	public void addItem(Canvas canv) {
		hl.addMember(canv);
	}

	public SavePanel(ClickHandler saveClicked, ClickHandler cancelClicked) {
		this(null, saveClicked, null, cancelClicked);
	}

	public SavePanel(String saveTitle, ClickHandler saveClicked,
			ClickHandler cancelClicked) {
		this(saveTitle, saveClicked, null, cancelClicked);
	}

	public SavePanel(ClickHandler saveClicked, final BaseWidget bw) {
		this(null, saveClicked, bw);
	}

	public SavePanel(String saveTitle, ClickHandler saveClicked,
			final BaseWidget bw) {
		this(saveTitle, saveClicked, null, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				bw.destroy();

			}
		});
	}

}
