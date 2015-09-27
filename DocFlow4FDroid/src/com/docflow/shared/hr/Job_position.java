package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:58:44 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Job_positionVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Job_position implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "JOB_POSITION";

	/**
	 * id:int4(10) <Primary Key>
	 */
	private int id;

	/**
	 * position_name:varchar(2147483647)
	 */
	private String position_name;

	/**
	 * structure_id:int4(10)
	 */
	private int structure_id;

	/**
	 * position_description:varchar(2147483647)
	 */
	private String position_description;

	/**
	 * person_id:int4(10)
	 */
	private int person_id;

	/**
	 * Constractor
	 */

	private long position_name_id;

	private long position_description_id;

	public Job_position() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public Job_position(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPosition_name() {
		return this.position_name;
	}

	public void setPosition_name(String position_name) {
		this.position_name = position_name;
	}

	public int getStructure_id() {
		return this.structure_id;
	}

	public void setStructure_id(int structure_id) {
		this.structure_id = structure_id;
	}

	public String getPosition_description() {
		return this.position_description;
	}

	public void setPosition_description(String position_description) {
		this.position_description = position_description;
	}

	public int getPerson_id() {
		return this.person_id;
	}

	public void setPerson_id(int person_id) {
		this.person_id = person_id;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Job_positionVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" position_name: ");
		buffer.append(position_name);
		buffer.append(" structure_id: ");
		buffer.append(structure_id);
		buffer.append(" position_description: ");
		buffer.append(position_description);
		buffer.append(" person_id: ");
		buffer.append(person_id);
		buffer.append("]");
		return buffer.toString();
	}

	public void setPosition_name_id(long position_name_id) {
		this.position_name_id = position_name_id;
	}

	public long getPosition_name_id() {
		return position_name_id;
	}

	public void setPosition_description_id(long position_description_id) {
		this.position_description_id = position_description_id;
	}

	public long getPosition_description_id() {
		return position_description_id;
	}

}
