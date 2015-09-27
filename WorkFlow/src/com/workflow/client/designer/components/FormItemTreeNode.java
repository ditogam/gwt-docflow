package com.workflow.client.designer.components;

import com.smartgwt.client.widgets.form.fields.FormItem;
import com.workflow.client.Utils;

public class FormItemTreeNode extends ComponentTreeNode {

	public FormItemTreeNode(String parentId, Class<? extends FormItem> formItem) {
		super(Utils.getClassSimpleName(formItem).toLowerCase(), parentId,
				Utils.getClassSimpleName(formItem), true, getIconName(formItem));
		setAttribute(CLASS_NAME_ATTRIBUTE, formItem.getName());

	}

	private static final String getIconName(Class<? extends FormItem> formItem) {
		String simpleName = Utils.getClassSimpleName(formItem);
		String name = simpleName.replaceAll("Item", "");
		String firstChar = (name.charAt(0) + "").toLowerCase();
		name = firstChar + name.substring(1) + ".gif";
		return name;
	}

}
