package org.oscim.view;

import org.oscim.core.MapPosition;

public interface MapListener {
	/*
	 * Called when a map is scrolled.
	 */
	public boolean onStateChanged(MapPosition event);

}
