package com.docflow.client.components.usermanager;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.PSharedClassList;
import com.common.shared.SharedClass;
import com.common.shared.model.UMObject;
import com.docflow.client.DocFlow;
import com.docflow.shared.common.GroupsAndPermitions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PGroups extends PSharedClassList {

	private MenuItem miEditPermitions;
	private ToolStripButton ttbEditPermitions;
	private ToolStripButton ttbEditDocTypePermitions;

	public static final String CAPTION_USER_PERMITION = "უფლებები";
	public static final String CAPTION_DOC_TYPE_PERMITION = "უფლებები დოკუმენტებზე";
	public static final String ICON_USER_PERMITION = "demoApp/use_permitions.png";

	public PGroups() {
		super();
		miEditPermitions = new MenuItem(CAPTION_USER_PERMITION,
				ICON_USER_PERMITION);
		menu.addItem(miEditPermitions);

		miEditPermitions.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				setPermitions();

			}
		});

		ttbEditPermitions = new ToolStripButton(CAPTION_USER_PERMITION,
				ICON_USER_PERMITION);
		toolStrip.addButton(ttbEditPermitions);

		ttbEditDocTypePermitions = new ToolStripButton(
				CAPTION_DOC_TYPE_PERMITION);
		toolStrip.addButton(ttbEditDocTypePermitions);

		ttbEditPermitions
				.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						setPermitions();
					}
				});

		ttbEditDocTypePermitions
				.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						setDocTypePermitions();
					}
				});
		miEditPermitions.setEnabled(false);
		ttbEditPermitions.setDisabled(true);
		ttbEditDocTypePermitions.setDisabled(true);
	}

	protected void setDocTypePermitions() {
		Record rec = lgSharedClass.getSelectedRecord();
		if (rec == null)
			return;
		final int user_id = rec.getAttributeAsInt("id");
		new WDocTypePermitions(user_id, false);

	}

	@Override
	protected SharedClass createNewSharedClass() {
		UMObject user = new UMObject();
		user.setType(UMObject.GROUP);
		return user;
	}

	@Override
	protected void disableAllActions() {
		// TODO Auto-generated method stub
		super.disableAllActions();
		if (miEditPermitions == null)
			return;
		miEditPermitions.setEnabled(false);
		ttbEditPermitions.setDisabled(true);
		ttbEditDocTypePermitions.setDisabled(true);
	}

	@Override
	protected void doAddEditOperation(boolean add, SharedClass cl) {
		UMObject user = new UMObject();
		user.setType(UMObject.GROUP);
		user.setIdVal(cl.getIdVal());
		user.setTextVal(cl.getTextVal());
		WAddEditUMObject.showWindow(user, this);

	}

	@Override
	protected void enableAdd() {
		// TODO Auto-generated method stub
		super.enableAdd();
		if (miEditPermitions == null)
			return;

	}

	@Override
	protected void enableEnableAll() {
		// TODO Auto-generated method stub
		super.enableEnableAll();
		if (miEditPermitions == null)
			return;
		miEditPermitions.setEnabled(true);
		ttbEditPermitions.setDisabled(false);
		ttbEditDocTypePermitions.setDisabled(false);
	}

	@Override
	protected int getRefreshType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void refreshByType() {
		// TODO Auto-generated method stub

	}

	public void setDataUM(ArrayList<UMObject> data) {
		ArrayList<SharedClass> sharedClasses = new ArrayList<SharedClass>();
		for (UMObject umObject : data) {
			sharedClasses.add(umObject);
		}

		super.setData(sharedClasses);
	}

	private void setPermitions() {
		Record rec = lgSharedClass.getSelectedRecord();
		if (rec == null)
			return;
		final int user_id = rec.getAttributeAsInt("id");
		DocFlow.docFlowService.getGroupsAndPermitions(false, user_id,
				new AsyncCallback<GroupsAndPermitions>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.say(caught.getMessage());

					}

					@Override
					public void onSuccess(GroupsAndPermitions result) {
						WPermitions.showWindow(false, user_id, result);

					}
				});
	}

	@Override
	public void setResult(Object obj) {
		if (obj == null)
			return;
		if (!(obj instanceof SharedClass))
			return;
		final long id = ((SharedClass) obj).getIdVal();
		DocFlow.docFlowService.getUserManagerObjects(UMObject.GROUP,
				new AsyncCallback<HashMap<Integer, ArrayList<UMObject>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							HashMap<Integer, ArrayList<UMObject>> result) {
						setDataUM(result.get(UMObject.GROUP));
						Record rec = lgSharedClass.getRecordList().find("id",
								id);
						if (rec != null)
							lgSharedClass.selectRecord(rec);
					}
				});
	}
}
