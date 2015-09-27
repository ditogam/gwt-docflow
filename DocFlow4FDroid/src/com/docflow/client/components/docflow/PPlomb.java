package com.docflow.client.components.docflow;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PPlomb extends VLayout {
	private ListGrid lgPlombs;
	public Integer user_id;

	public PPlomb() {
		if (DocFlow.user_obj.getPlombPermition() == null)
			return;

		lgPlombs = new ListGrid();
		lgPlombs.setDataSource(DocFlow.getDataSource("UserPlombsDS"));
		lgPlombs.setAutoFetchData(false);

		ToolStrip ts = new ToolStrip();
		addCombo(ts);
		final ToolStripButton tsbAdd = new ToolStripButton("",
				"[SKIN]/actions/add.png");
		ts.addButton(tsbAdd);
		final ToolStripButton tsbEdit = new ToolStripButton("",
				"[SKIN]/actions/approve.png");
		ts.addButton(tsbEdit);
		final ToolStripButton tsbRemove = new ToolStripButton("",
				"[SKIN]/actions/remove.png");
		ts.addButton(tsbRemove);
		ClickHandler ch = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getSource().equals(tsbAdd))
					addData();
				if (event.getSource().equals(tsbEdit))
					editData();
				if (event.getSource().equals(tsbRemove))
					removeData();

			}
		};
		tsbAdd.addClickHandler(ch);
		tsbEdit.addClickHandler(ch);
		tsbRemove.addClickHandler(ch);
		this.addMember(ts);

		lgPlombs.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				editData();

			}
		});

		lgPlombs.setHeight100();
		this.addMember(lgPlombs);
	}

	protected void removeData() {
		if (lgPlombs.getSelectedRecord() == null) {
			SC.say("Please select record!!!");
			return;
		}

		if (lgPlombs.getSelectedRecords().length > 1) {
			SC.say("Please select single record!!!");
			return;
		}
		SC.ask("Do you want to delete data?", new BooleanCallback() {

			@Override
			public void execute(Boolean value) {
				if (!value)
					return;
				lgPlombs.removeData(lgPlombs.getSelectedRecord());

			}
		});
	}

	private void fetchData(int user_id) {
		this.user_id = user_id;
		Criteria criteria = new Criteria();
		criteria.addCriteria("userid", user_id);
		lgPlombs.fetchData(criteria);
	}

	private void addCombo(ToolStrip ts) {
		if (!DocFlow.user_obj.getPlombPermition()) {
			user_id = DocFlow.user_obj.getUser().getUser_id();
			fetchData(user_id);
			return;
		}

		SelectItem siUsers = new SelectItem("siUsers", "Users");
		ClientUtils.fillCombo(siUsers, "PlombUsersDS", null, "userid",
				"username", new java.util.TreeMap<String, Integer>());
		siUsers.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				Object user_id = event.getValue();
				fetchData(user_id == null ? -1 : Integer.parseInt(user_id
						.toString()));
			}
		});
		ts.addFormItem(siUsers);
	}

	protected void editData() {
		if (lgPlombs.getSelectedRecord() == null)
			return;
		new WPlomb(lgPlombs.getSelectedRecord(), lgPlombs, user_id).show();

	}

	protected void addData() {
		if (user_id == null)
			return;
		new WPlomb(null, lgPlombs, user_id).show();

	}

	protected void getDataValues() {
		// TODO Auto-generated method stub

	}
}
