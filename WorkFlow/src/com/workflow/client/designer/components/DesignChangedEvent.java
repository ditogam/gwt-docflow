package com.workflow.client.designer.components;

import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.workflow.client.designer.PComponentTree;

public class DesignChangedEvent {
	private PComponentTree treegrid;
	private ListGridRecord[] records;
	
	
	
	public DesignChangedEvent(PComponentTree treegrid, ListGridRecord[] records) {
		super();
		this.treegrid = treegrid;
		this.records = records;
	}

	public PComponentTree getTreegrid() {
		return treegrid;
	}
	
	public ListGridRecord[] getRecords() {
		return records;
	}
	
}
