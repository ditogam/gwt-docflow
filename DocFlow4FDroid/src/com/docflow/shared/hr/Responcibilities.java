package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 19:00:04 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * ResponcibilitiesVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Responcibilities implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "RESPONCIBILITIES";

	/**
	 * id:serial(10) <Primary Key>
	 */
	private int id;

	/**
	 * item_id:int4(10)
	 */
	private int item_id;

	/**
	 * item_type_id:int2(5)
	 */
	private short item_type_id;

	/**
	 * resp_type_id:int4(10)
	 */
	private int resp_type_id;

	/**
	 * description:varchar(2147483647)
	 */
	private String description;

	private long description_id;

	private String item_type;

	/**
	 * Constractor
	 */
	public Responcibilities() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public Responcibilities(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItem_id() {
		return this.item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public short getItem_type_id() {
		return this.item_type_id;
	}

	public void setItem_type_id(short item_type_id) {
		this.item_type_id = item_type_id;
	}

	public int getResp_type_id() {
		return this.resp_type_id;
	}

	public void setResp_type_id(int resp_type_id) {
		this.resp_type_id = resp_type_id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDescription_id() {
		return description_id;
	}

	public void setDescription_id(long description_id) {
		this.description_id = description_id;
	}

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[ResponcibilitiesVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" item_id: ");
		buffer.append(item_id);
		buffer.append(" item_type_id: ");
		buffer.append(item_type_id);
		buffer.append(" resp_type_id: ");
		buffer.append(resp_type_id);
		buffer.append(" description: ");
		buffer.append(description);
		buffer.append("]");
		return buffer.toString();
	}

}
