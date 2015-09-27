package com.rdcommon.shared.ds;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSGrid extends DSFormDefinition implements IsSerializable {

	private Boolean editable;
	private Boolean exportable;

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public Boolean getExportable() {
		return exportable;
	}

	public void setExportable(Boolean exportable) {
		this.exportable = exportable;
	}

}
