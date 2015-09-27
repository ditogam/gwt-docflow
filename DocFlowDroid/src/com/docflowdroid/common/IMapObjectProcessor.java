package com.docflowdroid.common;

import com.docflowdroid.comp.MapButton;

public interface IMapObjectProcessor {
	public static final int GOOGLE_SRID = 900913;
	public static final int SYSTEM_SRID = 32638;

	public void execute(MapButton mapButton, Integer cusid, int subregion_id);
}
