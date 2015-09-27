package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:57:34 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Item_typeVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Item_type implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "ITEM_TYPE";

	/**
	 * item_type_id:serial(10) <Primary Key>
	 */
	private int item_type_id;

	/**
	 * item_type_name:varchar(2147483647)
	 */
	private String item_type_name;

	/**
	 * Constractor
	 */
	public Item_type() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>item_type_id</code>
	 */
	public Item_type(int item_type_id) {
		this.item_type_id = item_type_id;
	}

	public int getItem_type_id() {
		return this.item_type_id;
	}

	public void setItem_type_id(int item_type_id) {
		this.item_type_id = item_type_id;
	}

	public String getItem_type_name() {
		return this.item_type_name;
	}

	public void setItem_type_name(String item_type_name) {
		this.item_type_name = item_type_name;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Item_typeVo:");
		buffer.append(" item_type_id: ");
		buffer.append(item_type_id);
		buffer.append(" item_type_name: ");
		buffer.append(item_type_name);
		buffer.append("]");
		return buffer.toString();
	}

}
