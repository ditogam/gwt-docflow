package com.rdcommon.client.components.ds;

import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DSGroupNode extends TreeNode {
	public DSGroupNode(int id, int parent_id, DSGroup dsGroup) {
		 setAttribute("id", id);  
         setAttribute("parent_id", parent_id);  
         setAttribute("dsGroup", dsGroup);  
         setAttribute("name", dsGroup==null?"Groups":dsGroup.getName());  
         setAttribute("isOpen", true);
         
	}
}
