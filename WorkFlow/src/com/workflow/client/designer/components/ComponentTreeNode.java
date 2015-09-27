package com.workflow.client.designer.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderDropEvent;
import com.workflow.client.Utils;

public class ComponentTreeNode extends TreeNode {

	private static HashMap<String, Integer> idOrder = new HashMap<String, Integer>();

	public static final String CLASS_NAME_ATTRIBUTE = "CLASS_NAME_ATTRIBUTE";
	public static final String TYPE_NAME_ATTRIBUTE = "TYPE_NAME_ATTRIBUTE";
	public static final String ALREADY_LOADED_ATTRIBUTE = "ALREADY_LOADED_ATTRIBUTE";
	public static final String UNMOVABLE_ATTRIBUTE = "UNMOVABLE_ATTRIBUTE";
	public static final String COMPONENT_ATTRIBUTE = "COMPONENT_ATTRIBUTE";
	public static final String COMPONENT_PARAMS_ATTRIBUTE = "COMPONENT_PARAMS_ATTRIBUTE";
	public static final String PARENT_ID_ATTRIBUTE = "parentId";
	public static final String ID_ATTRIBUTE = "Id";

	public ComponentTreeNode(String Id, String parentId, String name,
			boolean isOpen, String icon, ComponentTreeNode... children) {
		setAttribute(ID_ATTRIBUTE, Id);
		setAttribute(Id, parentId);
		setAttribute("Name", name);
		setAttribute("isOpen", isOpen);
		setIcon("designer/" + icon);
		setAttribute("children", children);
		setAttribute(TYPE_NAME_ATTRIBUTE,
				Utils.getClassSimpleName(this.getClass()));
	}

	protected ComponentTreeNode(Map<String, String> attributes) {
		Set<String> keys = attributes.keySet();
		for (String key : keys) {
			try {
				setAttribute(key, attributes.get(key));
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		setCanAcceptDrop(true);
		setCanDrag(true);
		setCanExpand(true);
	}

	public static String[] getCloneAttributes() {
		return new String[] { "Id", "parentId", "Name", "icon",
				CLASS_NAME_ATTRIBUTE, TYPE_NAME_ATTRIBUTE };
	}

	public static HashMap<String, String> cloneAttributes(ListGridRecord node) {
		return cloneAttributes(node, true);
	}

	public static HashMap<String, String> cloneAttributes(ListGridRecord node,
			boolean newId) {
		String[] attrNames = getCloneAttributes();
		HashMap<String, String> attributes = new HashMap<String, String>();
		for (String attr : attrNames) {
			try {
				String value = node.getAttribute(attr);
				if (value == null)
					continue;
				attributes.put(attr, value);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (newId) {
			String name = node.getAttribute("Name");

			Integer id_order = idOrder.get(name);
			if (id_order == null)
				id_order = 0;
			id_order++;
			attributes.put("Id", name + id_order.intValue());
			idOrder.put(name, id_order);
		}
		return attributes;
	}

	private static ArrayList<String> CanvasTypes = new ArrayList<String>();

	protected static void addCanvasType(Class<?> clazz) {
		String name = Utils.getClassSimpleName(clazz);

		if (!CanvasTypes.contains(name))
			CanvasTypes.add(name);
	}

	public static boolean isCanvas(ListGridRecord src) {
		return isCanvas(src.getAttribute(TYPE_NAME_ATTRIBUTE));
	}

	public static boolean isCanvas(String typeName) {
		if (typeName == null)
			return false;
		for (String class_name : CanvasTypes)
			if (typeName.equals(class_name))
				return true;

		return false;
	}

	public static boolean isDynamicForm(ListGridRecord src) {
		return isDynamicForm(src.getAttribute(TYPE_NAME_ATTRIBUTE));
	}

	public static boolean isDynamicForm(String typeName) {
		if (typeName == null)
			return false;
		return typeName.equals(Utils
				.getClassSimpleName(DynamicFormTreeNode.class));

	}

	public static boolean isUnmovable(ListGridRecord src) {
		return isUnmovable(src.getAttribute(UNMOVABLE_ATTRIBUTE));
	}

	public static boolean isUnmovable(String typeName) {
		if (typeName == null)
			return false;
		return typeName.equals(UNMOVABLE_ATTRIBUTE);

	}

	public static boolean isFormItem(ListGridRecord src) {
		return isFormItem(src.getAttribute(TYPE_NAME_ATTRIBUTE));
	}

	public static boolean isFormItem(String typeName) {
		if (typeName == null)
			return false;
		return typeName
				.equals(Utils.getClassSimpleName(FormItemTreeNode.class))
				|| typeName.equals(Utils
						.getClassSimpleName(CanvasItemTreeNode.class));

	}

	public static boolean isCanvasItem(ListGridRecord src) {
		return isCanvasItem(src.getAttribute(TYPE_NAME_ATTRIBUTE));
	}

	public static boolean isCanvasItem(String typeName) {
		if (typeName == null)
			return false;
		return typeName.equals(Utils
				.getClassSimpleName(CanvasItemTreeNode.class));

	}

	public static boolean isAlreadyLoaded(ListGridRecord src) {
		if (src == null)
			return false;
		return src.getAttribute(ALREADY_LOADED_ATTRIBUTE) != null;

	}

	public static void setAlreadyLoaded(ListGridRecord src) {
		if (src == null)
			return;
		src.setAttribute(ALREADY_LOADED_ATTRIBUTE, ALREADY_LOADED_ATTRIBUTE);

	}

	public static void acceptMove(FolderDropEvent event, TreeGrid source) {

		TreeNode srcNode = event.getNodes()[0];
		if (srcNode == null) {
			event.cancel();
			return;
		}
		TreeNode destNode = (TreeNode) source.getRecord(event.getIndex());
		if (destNode == null) {
			event.cancel();
			return;
		}
		if (!canAcceptDropType(destNode, srcNode))
			event.cancel();
		System.out.println(event.isCancelled() + " src name="
				+ srcNode.getAttribute(ID_ATTRIBUTE) + ":"
				+ srcNode.getAttribute(TYPE_NAME_ATTRIBUTE) + "-getCanDrag="
				+ destNode.getCanDrag() + " target name="
				+ destNode.getAttribute(ID_ATTRIBUTE) + ":"
				+ destNode.getAttribute(TYPE_NAME_ATTRIBUTE)
				+ "-getCanAcceptDrop=" + destNode.getCanAcceptDrop());
	}

	public static boolean canAcceptType(ListGridRecord dest,
			ListGridRecord source) {
		if (dest == null || source == null)
			return false;
		return canAcceptType(dest.getAttribute(TYPE_NAME_ATTRIBUTE),
				source.getAttribute(TYPE_NAME_ATTRIBUTE));

	}

	public static boolean canAccept(
			Map<String, ArrayList<String>> allowedTypes, String dest_type,
			String source_type) {
		if (dest_type == null || source_type == null)
			return false;
		if (!allowedTypes.containsKey(dest_type))
			return false;
		return allowedTypes.get(dest_type).contains(source_type);
	}

	public static boolean canAcceptType(String dest_type, String source_type) {
		return canAccept(allowedTypes, dest_type, source_type);
	}

	public static boolean canAcceptDropType(ListGridRecord dest,
			ListGridRecord source) {
		if (dest == null || source == null)
			return false;
		return canAcceptDropType(dest.getAttribute(TYPE_NAME_ATTRIBUTE),
				source.getAttribute(TYPE_NAME_ATTRIBUTE));

	}

	public static Boolean canAcceptDropType(String dest_type, String source_type) {
		if (canAcceptType(dest_type, source_type)
				|| canAccept(allowedParentTypes, dest_type, source_type))
			return true;

		return false;
	}

	private static Map<String, ArrayList<String>> allowedTypes = new HashMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>> allowedParentTypes = new HashMap<String, ArrayList<String>>();

	private static void addAccept(Map<String, ArrayList<String>> allowedTypes,
			Class<?> type, Class<?>... types) {
		ArrayList<String> list = new ArrayList<String>();
		for (Class<?> cls : types) {
			list.add(Utils.getClassSimpleName(cls));
		}
		allowedTypes.put(Utils.getClassSimpleName(type), list);
	}

	private static void addAccept(Class<?> type, Class<?>... types) {
		addAccept(allowedTypes, type, types);
	}

	private static void addParentAccept(Class<?> type, Class<?>... types) {
		addAccept(allowedParentTypes, type, types);
	}

	private static void extractSelfInCanvas(Class<?> type) {
		String name = Utils.getClassSimpleName(type);
		allowedTypes.get(name);
		ArrayList<String> list = allowedTypes.get(name);
		if (list == null)
			return;
		list.remove(name);
	}

	private static void extractTypesInCanvas(Class<?> type, Class<?>... types) {
		String name = Utils.getClassSimpleName(type);

		allowedTypes.get(name);
		ArrayList<String> list = allowedTypes.get(name);
		if (list == null)
			return;
		for (Class<?> tp : types) {
			name = Utils.getClassSimpleName(tp);
			list.remove(name);
		}

	}

	private static void generateAccept() {

		addAccept(DynamicFormTreeNode.class, CanvasItemTreeNode.class,
				FormItemTreeNode.class, ToolBarItemTreeNode.class);

		addAccept(TabSetTreeNode.class, TabSetTreeNode.TabTreeNode.class);

		addAccept(ListGridTreeNode.class,
				ListGridTreeNode.ListGridFieldTreeNode.class);

		addAccept(TreeGridTreeNode.class,
				TreeGridTreeNode.TreeGridFieldTreeNode.class);

		addAccept(SelectionSteckTreeNode.class,
				SelectionSteckTreeNode.SectionStackSectionTreeNode.class);

		addAccept(ToolBarTreeNode.class, ToolBarButtonTreeNode.class,
				ToolBarMenuTreeNode.class, FormItemTreeNode.class);

		addAccept(MenuTreeNode.class, MenuTreeNode.MenuItemTreeNode.class);
		addAccept(MenuTreeNode.MenuItemTreeNode.class, MenuTreeNode.class);

		addAccept(ToolBarMenuTreeNode.class, MenuTreeNode.class);
		addAccept(IMenuButtonTreeNode.class, MenuTreeNode.class);

		addAccept(ToolBarMenuTreeNode.class, MenuTreeNode.class);
		addAccept(ToolBarItemTreeNode.class, ToolBarButtonTreeNode.class,
				ToolBarMenuTreeNode.class, FormItemTreeNode.class);

		for (String canvas_type : CanvasTypes) {
			if (ignerodContainerCanvas.contains(canvas_type))
				continue;
			ArrayList<String> list = allowedTypes.get(canvas_type);
			if (list == null)
				list = new ArrayList<String>();

			for (String canvas_child : CanvasTypes) {
				if (!list.contains(canvas_child))
					list.add(canvas_child);
			}
			allowedTypes.put(canvas_type, list);
		}
		extractSelfInCanvas(TabSetTreeNode.TabTreeNode.class);
		extractSelfInCanvas(ToolBarItemTreeNode.class);
		extractSelfInCanvas(CanvasItemTreeNode.class);
		extractSelfInCanvas(SelectionSteckTreeNode.SectionStackSectionTreeNode.class);

		extractTypesInCanvas(CanvasItemTreeNode.class, FormItemTreeNode.class,
				ToolBarItemTreeNode.class);
		extractTypesInCanvas(ToolBarItemTreeNode.class, FormItemTreeNode.class,
				CanvasItemTreeNode.class);

		addParentAccept(CanvasItemTreeNode.class, FormItemTreeNode.class);
		addParentAccept(FormItemTreeNode.class, CanvasItemTreeNode.class);
	}

	private static ArrayList<String> ignerodContainerCanvas = new ArrayList<String>();

	protected static void addIgnoreCanvasType(Class<?> clazz) {
		String name = Utils.getClassSimpleName(clazz);

		if (!ignerodContainerCanvas.contains(name))
			ignerodContainerCanvas.add(name);
	}

	static {
		addCanvasType(CanvasTreeNode.class);
		addCanvasType(DynamicFormTreeNode.class);
		addCanvasType(CanvasItemTreeNode.class);
		addCanvasType(ToolBarTreeNode.class);
		addCanvasType(ToolBarItemTreeNode.class);
		addCanvasType(TabSetTreeNode.class);
		addCanvasType(SelectionSteckTreeNode.class);
		addCanvasType(ListGridTreeNode.class);
		addCanvasType(TreeGridTreeNode.class);
		addCanvasType(TabSetTreeNode.TabTreeNode.class);
		addCanvasType(SelectionSteckTreeNode.SectionStackSectionTreeNode.class);
		addCanvasType(IMenuButtonTreeNode.class);

		addIgnoreCanvasType(DynamicFormTreeNode.class);
		addIgnoreCanvasType(TabSetTreeNode.class);
		addIgnoreCanvasType(SelectionSteckTreeNode.class);
		addIgnoreCanvasType(ListGridTreeNode.class);
		addIgnoreCanvasType(TreeGridTreeNode.class);
		addIgnoreCanvasType(IMenuButtonTreeNode.class);

		generateAccept();

		addCanvasType(MenuTreeNode.class);

		addCanvasType(ToolBarMenuTreeNode.class);
		addCanvasType(MenuTreeNode.MenuItemTreeNode.class);
	}

}
