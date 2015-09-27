package com.docflow.client.components.usermanager;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.model.UMObject;
import com.docflow.client.DocFlow;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class PUserManager extends VLayout {

	private PUsers pUsers;
	private PPermitions pPermitions;
	private PGroups pGroups;

	public PUserManager() {
		super();
		final TabSet topTabSet = new TabSet();
		topTabSet.setTabBarPosition(Side.TOP);
		pUsers = new PUsers();
		Tab tab = new Tab("Users");
		tab.setPane(pUsers);
		topTabSet.addTab(tab);

		pPermitions = new PPermitions();
		tab = new Tab("Permitions");
		tab.setPane(pPermitions);
		topTabSet.addTab(tab);

		pGroups = new PGroups();
		tab = new Tab("Groups");
		tab.setPane(pGroups);
		topTabSet.addTab(tab);
		topTabSet.setHeight100();
		topTabSet.setWidth100();
		this.addMember(topTabSet);
		refresh();
	}

	@Override
	public void destroy() {
		pUsers.destroy();
		pPermitions.destroy();
		pGroups.destroy();

		super.destroy();
	}

	public void refresh() {
		DocFlow.docFlowService.getUserManagerObjects(0,
				new AsyncCallback<HashMap<Integer, ArrayList<UMObject>>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						System.out.println(caught.getMessage());

					}

					@Override
					public void onSuccess(
							HashMap<Integer, ArrayList<UMObject>> result) {
						pUsers.setDataUM(result.get(UMObject.USER));
						pPermitions.setDataUM(result.get(UMObject.PERMITION));
						pGroups.setDataUM(result.get(UMObject.GROUP));

					}
				});
	}

}
