package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:56:13 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * StructureVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Structure implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "STRUCTURE";

	/**
	 * id:serial(10) <Primary Key>
	 */
	private int id;

	/**
	 * structure_name:varchar(2147483647)
	 */
	private String structure_name;

	/**
	 * parent_department_id:int4(10)
	 */
	private int parent_department_id;

	/**
	 * job_description:varchar(2147483647)
	 */
	private String job_description;

	/**
	 * max_substructures_count:int4(10)
	 */
	private int max_substructures_count;

	/**
	 * max_position_count:int4(10)
	 */
	private int max_position_count;

	/**
	 * creator_id:int4(10)
	 */
	private int creator_id;

	/**
	 * creator_name:varchar(2147483647)
	 */
	private String creator_name;

	/**
	 * structure_item_id:int4(10)
	 */
	private int structure_item_id;

	private long structure_name_id;
	private long job_description_id;

	private String parent_department;

	/**
	 * Constractor
	 */
	public Structure() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public Structure(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStructure_name() {
		return this.structure_name;
	}

	public void setStructure_name(String structure_name) {
		this.structure_name = structure_name;
	}

	public int getParent_department_id() {
		return this.parent_department_id;
	}

	public void setParent_department_id(int parent_department_id) {
		this.parent_department_id = parent_department_id;
	}

	public String getJob_description() {
		return this.job_description;
	}

	public void setJob_description(String job_description) {
		this.job_description = job_description;
	}

	public int getMax_substructures_count() {
		return this.max_substructures_count;
	}

	public void setMax_substructures_count(int max_substructures_count) {
		this.max_substructures_count = max_substructures_count;
	}

	public int getMax_position_count() {
		return this.max_position_count;
	}

	public void setMax_position_count(int max_position_count) {
		this.max_position_count = max_position_count;
	}

	public int getCreator_id() {
		return this.creator_id;
	}

	public void setCreator_id(int creator_id) {
		this.creator_id = creator_id;
	}

	public String getCreator_name() {
		return this.creator_name;
	}

	public void setCreator_name(String creator_name) {
		this.creator_name = creator_name;
	}

	public int getStructure_item_id() {
		return this.structure_item_id;
	}

	public void setStructure_item_id(int structure_item_id) {
		this.structure_item_id = structure_item_id;
	}

	public void setJob_description_id(long job_description_id) {
		this.job_description_id = job_description_id;
	}

	public long getJob_description_id() {
		return job_description_id;
	}

	public long getStructure_name_id() {
		return structure_name_id;
	}

	public void setStructure_name_id(long structure_name_id) {
		this.structure_name_id = structure_name_id;
	}

	public void setParent_department(String parent_department) {
		this.parent_department = parent_department;
	}

	public String getParent_department() {
		return parent_department;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[StructureVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" structure_name: ");
		buffer.append(structure_name);
		buffer.append(" parent_department_id: ");
		buffer.append(parent_department_id);
		buffer.append(" job_description: ");
		buffer.append(job_description);
		buffer.append(" max_substructures_count: ");
		buffer.append(max_substructures_count);
		buffer.append(" max_position_count: ");
		buffer.append(max_position_count);
		buffer.append(" creator_id: ");
		buffer.append(creator_id);
		buffer.append(" creator_name: ");
		buffer.append(creator_name);
		buffer.append(" structure_item_id: ");
		buffer.append(structure_item_id);
		buffer.append("]");
		return buffer.toString();
	}
}
