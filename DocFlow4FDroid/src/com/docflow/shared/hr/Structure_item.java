package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:57:05 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Structure_itemVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Structure_item implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "STRUCTURE_ITEM";

	/**
	 * item_id:serial(10) <Primary Key>
	 */
	private int item_id;

	/**
	 * item_parent_id:int4(10)
	 */
	private int item_parent_id;

	/**
	 * item_type_id:int4(10)
	 */
	private int item_type_id;

	/**
	 * item_class_id:int4(10)
	 */
	private int item_class_id;

	/**
	 * item_scope_id:int4(10)
	 */
	private int item_scope_id;

	/**
	 * item_name:varchar(2147483647)
	 */
	private String item_name;

	/**
	 * opened:bool(1)
	 */
	private boolean opened;

	/**
	 * object_id:int4(10)
	 */
	private int object_id;

	private long item_name_id;

	/**
	 * Constractor
	 */
	public Structure_item() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>item_id</code>
	 */
	public Structure_item(int item_id) {
		this.item_id = item_id;
	}

	public int getItem_id() {
		return this.item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public int getItem_parent_id() {
		return this.item_parent_id;
	}

	public void setItem_parent_id(int item_parent_id) {
		this.item_parent_id = item_parent_id;
	}

	public int getItem_type_id() {
		return this.item_type_id;
	}

	public void setItem_type_id(int item_type_id) {
		this.item_type_id = item_type_id;
	}

	public int getItem_class_id() {
		return this.item_class_id;
	}

	public void setItem_class_id(int item_class_id) {
		this.item_class_id = item_class_id;
	}

	public int getItem_scope_id() {
		return this.item_scope_id;
	}

	public void setItem_scope_id(int item_scope_id) {
		this.item_scope_id = item_scope_id;
	}

	public String getItem_name() {
		return this.item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public boolean isOpened() {
		return this.opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getObject_id() {
		return this.object_id;
	}

	public void setObject_id(int object_id) {
		this.object_id = object_id;
	}

	public void setItem_name_id(long item_name_id) {
		this.item_name_id = item_name_id;
	}

	public long getItem_name_id() {
		return item_name_id;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Structure_itemVo:");
		buffer.append(" item_id: ");
		buffer.append(item_id);
		buffer.append(" item_parent_id: ");
		buffer.append(item_parent_id);
		buffer.append(" item_type_id: ");
		buffer.append(item_type_id);
		buffer.append(" item_class_id: ");
		buffer.append(item_class_id);
		buffer.append(" item_scope_id: ");
		buffer.append(item_scope_id);
		buffer.append(" item_name: ");
		buffer.append(item_name);
		buffer.append(" opened: ");
		buffer.append(opened);
		buffer.append(" object_id: ");
		buffer.append(object_id);
		buffer.append("]");
		return buffer.toString();
	}

}
