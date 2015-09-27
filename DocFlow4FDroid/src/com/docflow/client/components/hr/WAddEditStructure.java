package com.docflow.client.components.hr;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class WAddEditStructure extends Window {
	public static void showWindow(boolean add, int parentid, int type,
			TreeGrid tree) {
		WAddEditStructure w = new WAddEditStructure(add, parentid, type, tree);
		w.show();
		w.tiName.selectValue();
		w.tiName.focusInItem();
	}

	private TextItem tiName;

	public WAddEditStructure(final boolean add, final int parentid,
			final int type, final TreeGrid tree) {
		super();
		setMembersMargin(5);
		setTitle("ფორმა");

		setWidth(400);

		tiName = new TextItem("item_name", "სახელი");
		tiName.setWidth(250);
		if (!add)
			tiName.setValue(tree.getSelectedRecord().getAttribute("item_name"));
		tiName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// event.cancel();

			}
		});

		DynamicForm df = new DynamicForm();
		df.setAlign(Alignment.LEFT);

		df.setWidth100();

		df.setItems(tiName);
		setHeight(112);

		df.setAutoHeight();
		this.addItem(df);
		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveData(add, parentid, type, tree);
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

		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}

	private void saveData(boolean add, int parentid, int type, TreeGrid tree) {
		String otext = null;
		try {
			otext = tiName.getValue().toString();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (otext == null || otext.length() == 0) {
			SC.say("გთხოვთ შეიყვანოთ ტექსტი!!!", new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					tiName.focusInItem();
				}
			});
			return;
		}

		if (add) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("item_parent_id", parentid);
			map.put("item_type_id", type);
			map.put("item_name", otext);
			tree.startEditingNew(map);

		} else {
			tree.getSelectedRecord().setAttribute("item_name", otext);
			Tree tree1 = tree.getData();
			TreeNode node = (TreeNode) tree.getSelectedRecord();
			tree1.closeFolder(node);
			tree1.openFolder(node);
		}
		tree.saveAllEdits();
		destroy();
	}

}
