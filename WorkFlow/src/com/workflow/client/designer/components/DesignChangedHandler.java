package com.workflow.client.designer.components;

import com.google.gwt.event.shared.EventHandler;

public interface DesignChangedHandler extends EventHandler {
	public void onDesignChanged(DesignChangedEvent event);

	public void onDesignComponentChanged(DesignChangedEvent event);
}
