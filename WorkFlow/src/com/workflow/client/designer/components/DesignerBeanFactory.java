package com.workflow.client.designer.components;

import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.bean.BeanFactory.MetaFactory;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tree.TreeGridField;

public interface DesignerBeanFactory extends MetaFactory {
	// BeanFactory<ListGrid> getListGridFactory();
	//
	// BeanFactory<DynamicForm> getDynamicFormFactory();
	//
	// BeanFactory<CanvasItem> getCanvasItemFactory();
	BeanFactory<Tab> getTabFactory();

	BeanFactory<ListGridField> getListGridFieldFactory();

	BeanFactory<TreeGridField> getTreeGridFieldFactory();

	BeanFactory<SectionStackSection> getSectionStackSectionFactory();
	
	BeanFactory<MenuItem> getMenuItemFactory();
	
//	BeanFactory<PropertyEditor> getPropertyEditorFactory();

}
