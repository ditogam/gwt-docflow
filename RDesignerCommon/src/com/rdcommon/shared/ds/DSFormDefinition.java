package com.rdcommon.shared.ds;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSFormDefinition extends DSComponent implements IsSerializable {

	public static final int FT_SEARCH = 1;
	public static final int FT_GRID = 2;
	public static final int FT_EDIT = 3;

	private ArrayList<DSClientFieldDef> overridenFields;
	private ArrayList<DSGroup> dsGroups;

	public ArrayList<DSClientFieldDef> getOverridenFields() {
		return overridenFields;
	}

	public void setOverridenFields(ArrayList<DSClientFieldDef> overridenFields) {
		this.overridenFields = overridenFields;
	}

	public ArrayList<DSGroup> getDsGroups() {
		return dsGroups;
	}

	public void setDsGroups(ArrayList<DSGroup> dsGroups) {
		this.dsGroups = dsGroups;
	}

	public DSClientFieldDef getDSField(String name) {
		if (overridenFields != null && !overridenFields.isEmpty()) {
			for (DSClientFieldDef item : overridenFields) {
				if (item.getName().equals(name))
					return item;
			}
		}
		return null;
	}

	@Override
	public void mergeProps(DSCProp p, DSCProp o) {
		super.mergeProps(p, o);
		DSFormDefinition p1 = (DSFormDefinition) p;
		DSFormDefinition o1 = (DSFormDefinition) o;

		if (o1.overridenFields != null)
			this.overridenFields = o1.overridenFields;
		else
			this.overridenFields = p1.overridenFields;

		if (o1.dsGroups != null)
			this.dsGroups = o1.dsGroups;
		else
			this.dsGroups = p1.dsGroups;

	}
}
