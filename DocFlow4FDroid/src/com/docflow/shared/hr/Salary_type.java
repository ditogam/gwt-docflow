package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:54:37 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Salary_typeVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Salary_type implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "SALARY_TYPE";

	/**
	 * id:serial(10) <Primary Key>
	 */
	private int id;

	/**
	 * description:varchar(2147483647)
	 */
	private String description;
	private long description_id;

	/**
	 * Constractor
	 */
	public Salary_type() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public Salary_type(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Salary_typeVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" description: ");
		buffer.append(description);
		buffer.append("]");
		return buffer.toString();
	}

	public void setDescription_id(long description_id) {
		this.description_id = description_id;
	}

	public long getDescription_id() {
		return description_id;
	}

}
