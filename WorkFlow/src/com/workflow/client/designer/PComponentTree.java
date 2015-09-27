package com.workflow.client.designer;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ColorItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SelectOtherItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.TimeItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripResizer;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.workflow.client.designer.components.BasicTreeNode;
import com.workflow.client.designer.components.ButtonTreeNode;
import com.workflow.client.designer.components.CanvasItemTreeNode;
import com.workflow.client.designer.components.CanvasTreeNode;
import com.workflow.client.designer.components.ComponentTreeNode;
import com.workflow.client.designer.components.DesignChangedEvent;
import com.workflow.client.designer.components.DesignChangedHandler;
import com.workflow.client.designer.components.DynamicFormTreeNode;
import com.workflow.client.designer.components.FormItemTreeNode;
import com.workflow.client.designer.components.IMenuButtonTreeNode;
import com.workflow.client.designer.components.ListGridTreeNode;
import com.workflow.client.designer.components.MenuTreeNode;
import com.workflow.client.designer.components.ReorderActions;
import com.workflow.client.designer.components.SelectionSteckTreeNode;
import com.workflow.client.designer.components.TabSetTreeNode;
import com.workflow.client.designer.components.ToolBarButtonTreeNode;
import com.workflow.client.designer.components.ToolBarItemTreeNode;
import com.workflow.client.designer.components.ToolBarMenuTreeNode;
import com.workflow.client.designer.components.ToolBarTreeNode;
import com.workflow.client.designer.components.TreeGridTreeNode;

public class PComponentTree extends TreeGrid {
	public PComponentTree(TreeNode[] nodes, boolean visible_component) {

		// setCanAcceptDrop(true);
		setShowHeader(false);
		setSelectionType(SelectionStyle.SINGLE);
		Tree componentTree = new Tree();
		componentTree.setModelType(TreeModelType.PARENT);
		componentTree.setRootValue(1);
		componentTree.setNameProperty("Id");
		componentTree.setIdField("Id");
		componentTree.setParentIdField("parentId");
		componentTree.setOpenProperty("isOpen");
		componentTree.setData(nodes == null ? createData() : nodes);

		TreeGridField[] fields = new TreeGridField[nodes != null
				&& visible_component ? 2 : 1];

		TreeGridField formattedField = new TreeGridField("Name");
		formattedField.setName("Name");
		formattedField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if (record == null)
					return (String) value;
				if (record.getAttribute("_bold") != null)
					return "<b>" + value + "</b>";
				else
					return (String) value;
			}
		});
		fields[0] = formattedField;
		if (nodes != null && visible_component) {
			setShowRecordComponents(true);
			setShowRecordComponentsByCell(true);
			TreeGridField componentField = new TreeGridField("my__comp", 50);
			fields[1] = componentField;
		}
		// setAutoFetchData(true);
		// setCanReorderRecords(true);
		setShowOpenIcons(true);
		setDropIconSuffix("into");
		setClosedIconSuffix("");
		setData(componentTree);
		setFields(fields);

		setHoverMoveWithMouse(true);
		setCanHover(true);
		setShowHover(true);
		setHoverOpacity(75);
		setHoverCustomizer(new HoverCustomizer() {
			@Override
			public String hoverHTML(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if (record instanceof TreeNode) {
					TreeNode hoveredTreeNode = (TreeNode) record;
					return "the name is: "
							+ hoveredTreeNode
									.getAttribute(ComponentTreeNode.ID_ATTRIBUTE)
							+ " parent: "
							+ hoveredTreeNode
									.getAttribute(ComponentTreeNode.PARENT_ID_ATTRIBUTE);
				} else {
					return "";// should not happen
				}
			}
		});
		addSelectionChangedHandler(new SelectionChangedHandler() {

			@Override
			public void onSelectionChanged(SelectionEvent event) {
				for (DesignChangedHandler eventHandler : changedEvents) {
					eventHandler
							.onDesignComponentChanged(new DesignChangedEvent(
									PComponentTree.this, event.getSelection()));
				}
			}
		});

	}

	private ArrayList<DesignChangedHandler> changedEvents = new ArrayList<DesignChangedHandler>();

	public void addDesignChangedHandler(DesignChangedHandler handler) {
		changedEvents.add(handler);
	}

	public void removeDesignChangedHandler(DesignChangedHandler handler) {
		changedEvents.remove(handler);
	}

	public void dataChanged(final ListGridRecord... records) {
		for (DesignChangedHandler eventHandler : changedEvents) {
			eventHandler.onDesignChanged(new DesignChangedEvent(
					PComponentTree.this, records));
		}
	}

	@Override
	protected Canvas createRecordComponent(ListGridRecord record, Integer colNum) {
		String fieldName = this.getFieldName(colNum);
		if ("my__comp".endsWith(fieldName)) {
			return new ReorderActions(getTree(), this, record);
		} else
			return super.createRecordComponent(record, colNum);
	}

	private TreeNode[] createData() {
		return new ComponentTreeNode[] {
				new CanvasTreeNode("vlayout", null, "VLayout", true,
						"VLayout.png", VLayout.class),
				new CanvasTreeNode("hlayout", null, "HLayout", true,
						"HLayout.png", HLayout.class),
				new BasicTreeNode("buttons", null, "<b>Buttons</b>", false,
						"button.gif", new CanvasTreeNode("ibutton", "buttons",
								"IButton", false, "button.gif", IButton.class),
						new CanvasTreeNode("imgbutton", "buttons", "ImgButton",
								false, "img.png", ImgButton.class),
						new IMenuButtonTreeNode("imenu", "buttons")),
				new TabSetTreeNode(),
				new MenuTreeNode(),
				new ListGridTreeNode(),
				new TreeGridTreeNode(),
				new SelectionSteckTreeNode(),
				new ToolBarTreeNode(
						"toolbar",
						"toolbars",
						"ToolBar",
						new ToolBarButtonTreeNode("toolbarbutton", "toolbar"),
						new ToolBarMenuTreeNode("toolbarmenubutton", "toolbar"),
						new ToolBarButtonTreeNode("toolbarresizer", "toolbar",
								"ToolStripResizer", "Tab.png",
								ToolStripResizer.class),
						new ToolBarButtonTreeNode("toolbarseperator",
								"toolbar", "ToolBarSeparator", "Tab.png",
								ToolStripSeparator.class)),

				new DynamicFormTreeNode("dynamicform", null, "DynamicForm",
						true, "DynamicForm.png", new BasicTreeNode("textitems",
								"dynamicform", "<b>TextItems</b>", false,
								"text.gif", new FormItemTreeNode("textitems",
										TextItem.class), new FormItemTreeNode(
										"textitems", TextAreaItem.class),
								new FormItemTreeNode("textitems",
										PasswordItem.class),
								new FormItemTreeNode("textitems",
										HiddenItem.class),
								new FormItemTreeNode("textitems",
										StaticTextItem.class)),
						new BasicTreeNode("numbertems", "dynamicform",
								"<b>NumberItems</b>", false, "float.gif",
								new FormItemTreeNode("numbertems",
										IntegerItem.class),
								new FormItemTreeNode("numbertems",
										FloatItem.class)),

						new BasicTreeNode("choosertems", "dynamicform",
								"<b>ChooserItems</b>", false, "select.gif",
								new FormItemTreeNode("choosertems",
										CheckboxItem.class),
								new FormItemTreeNode("choosertems",
										RadioGroupItem.class),
								new FormItemTreeNode("choosertems",
										SelectItem.class),
								new FormItemTreeNode("choosertems",

								SelectOtherItem.class), new FormItemTreeNode(
										"choosertems", ComboBoxItem.class),
								new FormItemTreeNode("choosertems",
										ColorItem.class), new FormItemTreeNode(
										"choosertems", DateItem.class),
								new FormItemTreeNode("choosertems",
										TimeItem.class)), new BasicTreeNode(
								"buttonItems", "dynamicform",
								"<b>ButtonItems</b>", false, "button.gif",
								new FormItemTreeNode("buttonItems",
										ButtonItem.class),
								new FormItemTreeNode("buttonItems",
										SubmitItem.class)),

						new BasicTreeNode("otherItems", "dynamicform",
								"<b>OtherItems</b>", false, "canvas.gif",
								new CanvasItemTreeNode("otherItems"),
								new ToolBarItemTreeNode("otherItems")))

		};
	}
}
