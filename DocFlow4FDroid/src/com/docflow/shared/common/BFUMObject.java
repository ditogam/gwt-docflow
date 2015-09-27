package com.docflow.shared.common;

import com.common.shared.SharedClass;
import com.google.gwt.user.client.rpc.IsSerializable;

public class BFUMObject extends SharedClass implements IsSerializable {

	/**
	 * 
	 */

	private long idVal;
	private String textVal;
	private int type;
	private boolean pwdApplyed;
	private String pwd;
	private long caption_id;

	public long getCaption_id() {
		return caption_id;
	}

	@Override
	public long getIdVal() {
		// TODO Auto-generated method stub
		return idVal;
	}

	public String getPwd() {
		if (pwd == null)
			return "";
		return pwd;
	}

	@Override
	public String getTextVal() {
		// TODO Auto-generated method stub
		return textVal;
	}

	public int getType() {
		return type;
	}

	public boolean isPwdApplyed() {
		return pwdApplyed;
	}

	public void setCaption_id(long caption_id) {
		this.caption_id = caption_id;
	}

	public void setIdVal(long idVal) {
		this.idVal = idVal;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setPwdApplyed(boolean pwdApplyed) {
		this.pwdApplyed = pwdApplyed;
	}

	public void setTextVal(String textVal) {
		this.textVal = textVal;
	}

	public void setType(int type) {
		this.type = type;
	}

}
