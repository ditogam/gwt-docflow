package com.workflow.client.designer;

import javax.servlet.http.Cookie;

import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.workflow.client.designer.components.ComponentTreeNode;
import com.workflow.client.designer.components.DesignChangedEvent;
import com.workflow.client.designer.components.DesignChangedHandler;

public class PPropertyView extends VLayout implements DesignChangedHandler {
	private PPropertyEditor current;

	public PPropertyView() {

	}

	@Override
	public void onDesignComponentChanged(DesignChangedEvent event) {
		onDesignChanged(event);
		if (event.getRecords() == null || event.getRecords().length < 1)
			return;
		ListGridRecord rec = event.getRecords()[0];
		String class_name = rec
				.getAttribute(ComponentTreeNode.CLASS_NAME_ATTRIBUTE);
		if (class_name == null)
			return;

		try {

			if (current == null) {
				current = new PPropertyEditor();
				current.setHeight100();
				current.setWidth100();
				addMember(current);
			}
			current.show();
			Object attrbts = rec
					.getAttributeAsObject(ComponentTreeNode.COMPONENT_PARAMS_ATTRIBUTE);
			Object obj = rec
					.getAttributeAsObject(ComponentTreeNode.COMPONENT_ATTRIBUTE);
			if (obj == null) {
				onDesignChanged(event);
				return;
			}
			current.setComponent_Data(obj, attrbts, rec);
		} catch (Exception e) {
			e.printStackTrace();
			onDesignChanged(event);
		}

	}

	@Override
	public void onDesignChanged(DesignChangedEvent event) {
		// if (current != null)
		// current.hide();
	}

}
