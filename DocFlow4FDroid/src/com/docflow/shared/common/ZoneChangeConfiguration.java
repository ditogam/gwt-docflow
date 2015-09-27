package com.docflow.shared.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ZoneChangeConfiguration implements IsSerializable {
	private int[] restricted_edits;
	private int charcount;
	private String[] coef_editable_columns;
	private int boper_acctype_default;

	public void setRestricted_edits_Str(String restricted_edits) {
		if (restricted_edits == null)
			restricted_edits = "";
		restricted_edits = restricted_edits.trim();
		if (restricted_edits.length() == 0) {
			this.restricted_edits = new int[0];
			return;
		}
		String splitted[] = restricted_edits.split(",");
		this.restricted_edits = new int[splitted.length];
		for (int i = 0; i < splitted.length; i++) {
			try {
				this.restricted_edits[i] = Integer.parseInt(splitted[i].trim());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public int[] getRestricted_edits() {
		return restricted_edits;
	}

	public void setRestricted_edits(int[] restricted_edits) {
		this.restricted_edits = restricted_edits;
	}

	public int getCharcount() {
		return charcount;
	}

	public void setCharcount(int charcount) {
		this.charcount = charcount;
	}

	public void setCoef_editable_columns_Str(String coef_editable_columns) {
		if (coef_editable_columns == null)
			coef_editable_columns = "";
		coef_editable_columns = coef_editable_columns.trim();
		if (coef_editable_columns.length() == 0) {
			this.coef_editable_columns = new String[] {};
		}
		this.coef_editable_columns = coef_editable_columns.split(",");
	}

	public String[] getCoef_editable_columns() {
		return coef_editable_columns;
	}

	public void setCoef_editable_columns(String[] coef_editable_columns) {
		this.coef_editable_columns = coef_editable_columns;
	}

	public int getBoper_acctype_default() {
		return boper_acctype_default;
	}

	public void setBoper_acctype_default(int boper_acctype_default) {
		this.boper_acctype_default = boper_acctype_default;
	}

}
