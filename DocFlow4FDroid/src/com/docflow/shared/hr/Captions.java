package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 20:14:58 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * CaptionsVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Captions implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "CAPTIONS";

	/**
	 * id:bigserial(19) <Primary Key>
	 */
	private long id;

	/**
	 * language_id:int4(10) <Primary Key>
	 */
	private int language_id;

	/**
	 * cvalue:varchar(2147483647)
	 */
	private String cvalue;

	/**
	 * Constractor
	 */
	public Captions() {
	}

	public Captions(long id, int language_id) {
		this.id = id;
		this.language_id = language_id;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLanguage_id() {
		return this.language_id;
	}

	public void setLanguage_id(int language_id) {
		this.language_id = language_id;
	}

	public String getCvalue() {
		return this.cvalue;
	}

	public void setCvalue(String cvalue) {
		this.cvalue = cvalue;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[CaptionsVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" language_id: ");
		buffer.append(language_id);
		buffer.append(" cvalue: ");
		buffer.append(cvalue);
		buffer.append("]");
		return buffer.toString();
	}

}
