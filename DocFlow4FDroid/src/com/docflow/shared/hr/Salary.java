package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:55:31 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * SalaryVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */

public class Salary implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "SALARY";

	/**
	 * position_id:int4(10) <Primary Key>
	 */
	private int position_id;

	/**
	 * salary_type_id:int4(10) <Primary Key>
	 */
	private int salary_type_id;

	/**
	 * salary_value:float8(17,17)
	 */
	private double salary_value;

	/**
	 * description:varchar(2147483647)
	 */
	private String description;

	/**
	 * Constractor
	 */
	public Salary() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>position_id</code>
	 * @param <code>salary_type_id</code>
	 */
	public Salary(int position_id, int salary_type_id) {
		this.position_id = position_id;
		this.salary_type_id = salary_type_id;
	}

	public int getPosition_id() {
		return this.position_id;
	}

	public void setPosition_id(int position_id) {
		this.position_id = position_id;
	}

	public int getSalary_type_id() {
		return this.salary_type_id;
	}

	public void setSalary_type_id(int salary_type_id) {
		this.salary_type_id = salary_type_id;
	}

	public double getSalary_value() {
		return this.salary_value;
	}

	public void setSalary_value(double salary_value) {
		this.salary_value = salary_value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[SalaryVo:");
		buffer.append(" position_id: ");
		buffer.append(position_id);
		buffer.append(" salary_type_id: ");
		buffer.append(salary_type_id);
		buffer.append(" salary_value: ");
		buffer.append(salary_value);
		buffer.append(" description: ");
		buffer.append(description);
		buffer.append("]");
		return buffer.toString();
	}

}
