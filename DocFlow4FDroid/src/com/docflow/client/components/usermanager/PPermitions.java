package com.docflow.client.components.usermanager;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.PSharedClassList;
import com.common.shared.SharedClass;
import com.common.shared.model.UMObject;
import com.docflow.client.DocFlow;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;

public class PPermitions extends PSharedClassList {

	@Override
	protected SharedClass createNewSharedClass() {
		UMObject user = new UMObject();
		user.setType(UMObject.GROUP);
		return user;
	}

	@Override
	protected void doAddEditOperation(boolean add, SharedClass cl) {
		UMObject user = new UMObject();
		user.setType(UMObject.PERMITION);
		user.setIdVal(cl.getIdVal());
		user.setTextVal(cl.getTextVal());
		WAddEditUMObject.showWindow(user, this);

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

	@Override
	public void setResult(Object obj) {
		if (obj == null)
			return;
		if (!(obj instanceof SharedClass))
			return;
		final long id = ((SharedClass) obj).getIdVal();
		DocFlow.docFlowService.getUserManagerObjects(UMObject.PERMITION,
				new AsyncCallback<HashMap<Integer, ArrayList<UMObject>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							HashMap<Integer, ArrayList<UMObject>> result) {
						setDataUM(result.get(UMObject.PERMITION));
						Record rec = lgSharedClass.getRecordList().find("id",
								id);
						if (rec != null)
							lgSharedClass.selectRecord(rec);
					}
				});
	}
}
