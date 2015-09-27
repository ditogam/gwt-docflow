package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 19:00:19 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Responcibility_typesVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Responcibility_types implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "RESPONCIBILITY_TYPES";

	/**
	 * id:serial(10)
	 */
	private int id;

	/**
	 * resp_type_name:varchar(2147483647)
	 */
	private String resp_type_name;
	private long resp_type_name_id;

	/**
	 * Constractor
	 */
	public Responcibility_types() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResp_type_name() {
		return this.resp_type_name;
	}

	public void setResp_type_name(String resp_type_name) {
		this.resp_type_name = resp_type_name;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Responcibility_typesVo:");
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append(" resp_type_name: ");
		buffer.append(resp_type_name);
		buffer.append("]");
		return buffer.toString();
	}

	public void setResp_type_name_id(long resp_type_name_id) {
		this.resp_type_name_id = resp_type_name_id;
	}

	public long getResp_type_name_id() {
		return resp_type_name_id;
	}

}
