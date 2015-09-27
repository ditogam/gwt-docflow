package com.docflow.client.components.usermanager;

import com.docflow.client.DocFlow;
import com.docflow.shared.common.GroupsAndPermitions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class WPermitions extends Window {

	public static void showWindow(boolean user, int user_or_group_id,
			GroupsAndPermitions groupsAndPermitions) {
		WPermitions w = new WPermitions(user, user_or_group_id,
				groupsAndPermitions);
		w.show();
	}

	boolean user;
	int user_or_group_id;
	private PApplyPermitions pApplyPermitions;

	private PApplyGroups pApplyGroups;

	public WPermitions(boolean user, int user_or_group_id,
			GroupsAndPermitions groupsAndPermitions) {
		this.user = user;
		this.user_or_group_id = user_or_group_id;

		TabSet tabset = new TabSet();

		pApplyPermitions = new PApplyPermitions(
				groupsAndPermitions.getPermitionItems());
		Tab tab = new Tab("Permitions");
		tab.setPane(pApplyPermitions);
		tabset.addTab(tab);

		if (user) {
			pApplyGroups = new PApplyGroups(
					groupsAndPermitions.getUser_Groups());
			tab = new Tab("Groups");
			tab.setPane(pApplyGroups);
			tabset.addTab(tab);
		}

		tabset.setTabBarPosition(Side.TOP);
		tabset.setHeight("90%");
		tabset.setWidth100();
		this.addItem(tabset);

		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		});
		IButton bCancel = new IButton("Cancel");
		bCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		hl.addMember(bSave);
		hl.addMember(bCancel);
		hl.setWidth100();
		hl.setMembersMargin(10);
		hl.setHeight("30");
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		hl.addMember(l);
		this.addItem(hl);

		this.setSize("700", "600");
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}

	private void saveData() {
		String permitions = pApplyPermitions.getSelectedPermitions();
		String groups = "";
		if (user)
			groups = pApplyGroups.getSelectedGroups();

		DocFlow.docFlowService.setPermitions(user, user_or_group_id,
				permitions, groups, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());

					}

					@Override
					public void onSuccess(Void result) {
						destroy();

					}
				});
	}
}
