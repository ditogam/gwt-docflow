package com.rdcommon.shared.ds;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSComponent extends DSCProp implements IsSerializable {
	protected String name;
	protected String title;
	protected Boolean hidden;
	protected Boolean readOnly;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}


	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public void mergeProps(DSCProp p, DSCProp o) {
		super.mergeProps(p, o);
		DSComponent p1 = (DSComponent) p;
		DSComponent o1 = (DSComponent) o;


		if (o1.title != null)
			this.title = o1.title;
		else
			this.title = p1.title;

		if (o1.hidden != null)
			this.hidden = o1.hidden;
		else
			this.hidden = p1.hidden;

		if (o1.readOnly != null)
			this.readOnly = o1.readOnly;
		else
			this.readOnly = p1.readOnly;

	}
}
