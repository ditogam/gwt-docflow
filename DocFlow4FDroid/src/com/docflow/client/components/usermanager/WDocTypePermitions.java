package com.docflow.client.components.usermanager;

import java.util.ArrayList;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.shared.docflow.DocType;
import com.docflow.shared.docflow.DocTypesAndPermitions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.grid.events.ChangeEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class WDocTypePermitions extends Window {
	int user_or_group_id;
	boolean user;
	private TreeGrid tgDocTypes;

	public static class TerminationTreeNode extends TreeNode {

		public TerminationTreeNode(DocType dt, boolean group) {
			setAttribute("TypeId", !group ? dt.getId() : -1 * dt.getGroup_id());
			setAttribute("GroupId", group ? 0 : -1 * dt.getGroup_id());
			setAttribute("isGroupId", group);
			setAttribute("Name",
					!group ? dt.getDoctypevalue() : dt.getDoctypegroupvalue());

		}
	}

	final Tree tree = new Tree();

	public WDocTypePermitions(int user_or_group_id, boolean user) {
		try {
			this.user_or_group_id = user_or_group_id;
			this.user = user;
			SplashDialog.showSplash();

			tree.setModelType(TreeModelType.PARENT);
			tree.setNameProperty("Name");
			tree.setIdField("TypeId");
			tree.setParentIdField("GroupId");
			tree.setShowRoot(true);

			tgDocTypes = new TreeGrid();
			tgDocTypes.setWidth100();
			tgDocTypes.setHeight("80%");
			TreeGridField descrip = new TreeGridField("Name", "DocType");
			descrip.setCanSort(false);

			tgDocTypes.setSelectionAppearance(SelectionAppearance.CHECKBOX);
			tgDocTypes.setShowSelectedStyle(false);
			tgDocTypes.setShowPartialSelection(true);
			tgDocTypes.setCascadeSelection(true);

			// TreeGridField active = new TreeGridField("active", "R");
			// active.setType(ListGridFieldType.BOOLEAN);
			// active.setCanSort(false);
			//
			// active.setCanEdit(true);
			//
			// active.addChangeHandler(new ChangeHandler() {
			//
			// @Override
			// public void onChange(ChangeEvent event) {
			// calculateDocType(event);
			// }
			// });
			tgDocTypes.setFields(descrip);

			DocFlow.docFlowService.getDocTypePermitions(user_or_group_id, user,
					DocFlow.language_id,
					new AsyncCallback<DocTypesAndPermitions>() {

						@Override
						public void onSuccess(DocTypesAndPermitions result) {
							SplashDialog.hideSplash();
							setData(result, tree);
						}

						@Override
						public void onFailure(Throwable caught) {
							SplashDialog.hideSplash();

						}
					});
		} catch (Exception e) {
			SplashDialog.showSplash();
		}

		this.addItem(tgDocTypes);
		tgDocTypes.addDrawHandler(new DrawHandler() {

			@Override
			public void onDraw(DrawEvent event) {
				tree.openAll();

			}
		});
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

		setWidth(630);
		setHeight(830);
		this.setIsModal(true);
		setCanDragResize(true);
		this.centerInPage();
		setTitle("Doc Type Permitions");

		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					WDocTypePermitions.this.destroy();

			}
		});
	}

	protected void saveData() {
		Record[] data = tgDocTypes.getSelectedRecords();
		ArrayList<Integer> restrictions = new ArrayList<Integer>();
		for (Record record : data) {
			if (!record.getAttributeAsBoolean("isGroupId")) {
				restrictions.add(record.getAttributeAsInt("TypeId"));
			}
		}
		SplashDialog.showSplash();
		DocFlow.docFlowService.saveDocTypePermitions(user_or_group_id, user,
				restrictions, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						SplashDialog.hideSplash();
						destroy();
					}

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();
						SC.warn(caught.getMessage());

					}
				});

	}

	protected void calculateDocType(ChangeEvent event) {
		Object val = event.getValue();
		Boolean isactive = (Boolean) val;
		if (!isactive)
			return;
		Record record = tgDocTypes.getSelectedRecord();
		if (record == null)
			return;
		if (!record.getAttributeAsBoolean("isGroupId"))
			return;
	}

	protected void setData(DocTypesAndPermitions result, Tree tree) {
		ArrayList<DocType> doctypes = result.getDocTypes();
		int group_id = -1;
		ArrayList<TreeNode> data = new ArrayList<TreeNode>();
		ArrayList<TreeNode> selected = new ArrayList<TreeNode>();
		for (DocType docType : doctypes) {

			if (docType.getGroup_id() != group_id) {
				group_id = docType.getGroup_id();
				data.add(new TerminationTreeNode(docType, true));

			}

			TerminationTreeNode n = new TerminationTreeNode(docType, false);
			data.add(n);
			n.setAttribute("obj_ct", docType);
			n.setAttribute("isSelected", false);
			ArrayList<Integer> restrictions = result.getRestrictions();
			if (restrictions != null) {
				for (Integer r : restrictions) {
					if (r.intValue() == docType.getId()) {
						n.setAttribute("isSelected", true);
						selected.add(n);
						break;
					}
				}
			}

		}
		tree.setData(data.toArray(new TreeNode[] {}));

		tgDocTypes.setData(tree);
		tgDocTypes.selectRecords(selected.toArray(new TreeNode[] {}));
		show();
	}
}
