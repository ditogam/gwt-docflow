package com.workflow.client.designer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.core.DataClass;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.BaseWidget;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.workflow.client.Utils;
import com.workflow.client.designer.components.ComponentTreeNode;
import com.workflow.client.designer.components.DesignChangedEvent;
import com.workflow.client.designer.components.DesignChangedHandler;

public class PComponentView extends VLayout implements DesignChangedHandler {

	private VLayout content = null;

	public PComponentView() {

		// IButton b = new IButton();
		// addMember(b);
		// b.addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// try {
		// if (v != null)
		// v.destroy();
		// v = new PComponentPropertyView("IntegerItem");
		// v.setHeight("*");
		// v.setWidth100();
		// addMember(v);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}

	private ArrayList<ItemConstructor> fullList;

	@Override
	public void onDesignChanged(DesignChangedEvent event) {
		if (content != null) {
			removeMember(content);
			content.destroy();
			content = null;
		}
		TreeNode node = event.getTreegrid().getTree().getRoot();
		if (fullList != null)
			fullList.clear();
		fullList = new ArrayList<ItemConstructor>();

		content = new VLayout();
		content.setWidth100();
		content.setHeight100();
		ItemConstructor last = new ItemConstructor();
		// last.item = content;
		processNode(node, event.getTreegrid().getTree(), last);
		processItem(last, content);
		for (ItemConstructor item : fullList) {
			setAttributesToComp(item.attrs, item.item);
		}
		addMember(content);
	}

	private void addDynamicFormFields(ItemConstructor last) {

		ArrayList<FormItem> formItems = new ArrayList<FormItem>();
		ArrayList<ItemConstructor> subItems = last.subItems;
		for (ItemConstructor sub : subItems) {
			if (sub.item != null && sub.item instanceof FormItem)
				formItems.add((FormItem) sub.item);
		}
		((DynamicForm) last.item).setFields(formItems
				.toArray(new FormItem[] {}));

	}

	private void addListGridField(ItemConstructor last) {
		int id = 0;
		ArrayList<ListGridField> formItems = new ArrayList<ListGridField>();
		ArrayList<ItemConstructor> subItems = last.subItems;
		for (ItemConstructor sub : subItems) {
			if (sub.item != null && sub.item instanceof ListGridField) {
				ListGridField field = (ListGridField) sub.item;

				field.setName("Field_" + id++);
				formItems.add(field);
			}
		}
		((ListGrid) last.item).setFields(formItems
				.toArray(new ListGridField[] {}));

	}

	private void processItem(ItemConstructor last, Object lastCanvas) {
		if (last.item != null) {
			Object obj = last.item;
			if (obj instanceof Canvas && !(obj instanceof Menu)) {
				Canvas newCanvas = (Canvas) obj;
				if (lastCanvas instanceof Layout)
					((Layout) lastCanvas).addMember(newCanvas);
				else if (lastCanvas instanceof Tab)
					((Tab) lastCanvas).setPane(newCanvas);
				if (lastCanvas instanceof CanvasItem)
					((CanvasItem) lastCanvas).setCanvas(newCanvas);
				if (lastCanvas instanceof SectionStackSection)
					((SectionStackSection) lastCanvas).addItem(newCanvas);
				else if (lastCanvas instanceof Canvas)
					((Canvas) lastCanvas).addChild(newCanvas);

			}
			if (obj instanceof Tab && lastCanvas instanceof TabSet) {
				((TabSet) lastCanvas).addTab((Tab) obj);
			}
			if (obj instanceof Menu && lastCanvas instanceof IMenuButton) {
				((IMenuButton) lastCanvas).setMenu((Menu) obj);
			}

			if (obj instanceof MenuItem && lastCanvas instanceof Menu) {
				((Menu) lastCanvas).addItem((MenuItem) obj);
			}

			if (obj instanceof Menu && lastCanvas instanceof MenuItem) {
				((MenuItem) lastCanvas).setSubmenu((Menu) obj);
			}

			if (lastCanvas instanceof ToolStrip) {
				if (obj instanceof ToolStripButton) {
					ToolStrip toolStrip = (ToolStrip) lastCanvas;
					((ToolStripButton) obj).setTitle("");
					toolStrip.addButton((ToolStripButton) obj);

				}
				if (obj instanceof ToolStripMenuButton)
					((ToolStrip) lastCanvas)
							.addMenuButton((ToolStripMenuButton) obj);
			}

			if (obj instanceof SectionStackSection
					&& lastCanvas instanceof SectionStack) {
				((SectionStack) lastCanvas)
						.addSection((SectionStackSection) obj);
			}

			lastCanvas = last.item;
			if (lastCanvas instanceof DynamicForm)
				addDynamicFormFields(last);
			if (lastCanvas instanceof ListGrid & !(obj instanceof Menu))
				addListGridField(last);
		}

		ArrayList<ItemConstructor> subItems = last.subItems;
		for (ItemConstructor item : subItems) {
			processItem(item, lastCanvas);
		}
	}

	private void processNode(TreeNode node, Tree tree, ItemConstructor last) {
		String class_name = node
				.getAttribute(ComponentTreeNode.CLASS_NAME_ATTRIBUTE);
		ItemConstructor constr = new ItemConstructor();
		fullList.add(constr);
		last.subItems.add(constr);
		if (class_name != null) {
			try {

				constr.item = BeanFactory.newInstance(class_name);
				node.setAttribute(ComponentTreeNode.COMPONENT_ATTRIBUTE,
						constr.item);
				constr.attrs = node
						.getAttributeAsObject(ComponentTreeNode.COMPONENT_PARAMS_ATTRIBUTE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		TreeNode[] children = tree.getChildren(node);
		if (children != null)
			for (TreeNode treeNode : children) {
				processNode(treeNode, tree, constr);
			}
	}

	public static void setAttributesToComp(Object attrs, Object item) {
		if (attrs == null)
			return;
		if (item == null)
			return;
		try {
			attrs = Utils.jsToMap(attrs);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (!(attrs instanceof Map))
			return;
		Map<String, Object> map = (Map<String, Object>) attrs;
		Set<String> keys = map.keySet();
		for (String attr_name : keys) {
			Object val = map.get(attr_name);
			if (item instanceof BaseWidget) {
				try {
					BaseWidget w = (BaseWidget) item;
					if (val instanceof JavaScriptObject)
						w.setProperty(attr_name, (JavaScriptObject) val);
					else if (val instanceof Number && val != null) {
						w.setProperty(attr_name, ((Number) val).doubleValue());
					} else if (val instanceof String)
						w.setProperty(attr_name, (String) val);
					w.draw();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (attr_name.equals("title")) {
				String sValue = val == null ? null : val.toString();
				if (item instanceof Tab)
					((Tab) item).setTitle(sValue);
				if (item instanceof MenuItem)
					((MenuItem) item).setTitle(sValue);
			}

			if (item instanceof DataClass) {
				try {
					DataClass w = (DataClass) item;
					if (val instanceof JavaScriptObject)
						w.setAttribute(attr_name, (JavaScriptObject) val);
					else if (val instanceof Number && val != null) {
						w.setAttribute(attr_name, ((Number) val).doubleValue());
					} else
						w.setAttribute(attr_name, (String) val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// if (item instanceof FormItem) {
			// try {
			// FormItem w = (FormItem) item;
			// if (val instanceof JavaScriptObject)
			// w.setProperty(attr_name, (JavaScriptObject) val);
			// else if (val instanceof Number && val != null) {
			// w.setProperty(attr_name, ((Number) val).doubleValue());
			// } else if (val instanceof String)
			// w.setProperty(attr_name, (String) val);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
		}
	}

	private int item_id;

	private class ItemConstructor {
		Object item;
		int my_id;
		Object attrs;
		ArrayList<ItemConstructor> subItems;

		public ItemConstructor() {
			my_id = item_id++;
			subItems = new ArrayList<ItemConstructor>();
		}
	}

	@Override
	public void onDesignComponentChanged(DesignChangedEvent event) {
		// TODO Auto-generated method stub

	}
}
