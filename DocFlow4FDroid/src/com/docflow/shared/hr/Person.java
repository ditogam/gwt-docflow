package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:59:09 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * PersonVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Person implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "PERSON";

	/**
	 * person_id:serial(10) <Primary Key>
	 */
	private int person_id;

	/**
	 * person_last_name:varchar(2147483647)
	 */
	private String person_last_name;

	/**
	 * person_first_name:varchar(2147483647)
	 */
	private String person_first_name;

	/**
	 * person_picture:bytea(2147483647)
	 */
	private byte[] person_picture;

	/**
	 * person_picture_filename:varchar(2147483647)
	 */
	private String person_picture_filename;

	/**
	 * person_picture_filesize:int4(10)
	 */
	private int person_picture_filesize;

	/**
	 * person_picture_date_created:date(13)
	 */
	private java.sql.Date person_picture_date_created;

	/**
	 * person_middle_name:varchar(2147483647)
	 */
	private String person_middle_name;

	/**
	 * person_birth_date:timestamptz(35,6)
	 */
	private java.sql.Timestamp person_birth_date;

	/**
	 * person_identity_no:varchar(2147483647)
	 */
	private String person_identity_no;

	/**
	 * person_address:varchar(2147483647)
	 */
	private String person_address;

	/**
	 * person_family:varchar(2147483647)
	 */
	private String person_family;

	/**
	 * person_nationality_other:varchar(2147483647)
	 */
	private String person_nationality_other;

	/**
	 * person_sex:int4(10)
	 */
	private int person_sex;

	/**
	 * person_nationality:int4(10)
	 */
	private int person_nationality;

	/**
	 * person_merige_statuse:int4(10)
	 */
	private int person_merige_statuse;

	private long person_last_name_id;
	private long person_first_name_id;
	private long person_address_tid;
	private long person_family_id;
	private long person_middle_name_id;
	private int user_id;

	private int picture_id;
	private int initiator_id;

	/**
	 * Constractor
	 */
	public Person() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>person_id</code>
	 */
	public Person(int person_id) {
		this.person_id = person_id;
	}

	public int getPerson_id() {
		return this.person_id;
	}

	public void setPerson_id(int person_id) {
		this.person_id = person_id;
	}

	public String getPerson_last_name() {
		return this.person_last_name;
	}

	public void setPerson_last_name(String person_last_name) {
		this.person_last_name = person_last_name;
	}

	public String getPerson_first_name() {
		return this.person_first_name;
	}

	public void setPerson_first_name(String person_first_name) {
		this.person_first_name = person_first_name;
	}

	public byte[] getPerson_picture() {
		return this.person_picture;
	}

	public void setPerson_picture(byte[] person_picture) {
		this.person_picture = person_picture;
	}

	public String getPerson_picture_filename() {
		return this.person_picture_filename;
	}

	public void setPerson_picture_filename(String person_picture_filename) {
		this.person_picture_filename = person_picture_filename;
	}

	public int getPerson_picture_filesize() {
		return this.person_picture_filesize;
	}

	public void setPerson_picture_filesize(int person_picture_filesize) {
		this.person_picture_filesize = person_picture_filesize;
	}

	public java.sql.Date getPerson_picture_date_created() {
		return this.person_picture_date_created;
	}

	public void setPerson_picture_date_created(
			java.sql.Date person_picture_date_created) {
		this.person_picture_date_created = person_picture_date_created;
	}

	public String getPerson_middle_name() {
		return this.person_middle_name;
	}

	public void setPerson_middle_name(String person_middle_name) {
		this.person_middle_name = person_middle_name;
	}

	public java.sql.Timestamp getPerson_birth_date() {
		return this.person_birth_date;
	}

	public void setPerson_birth_date(java.sql.Timestamp person_birth_date) {
		this.person_birth_date = person_birth_date;
	}

	public String getPerson_identity_no() {
		return this.person_identity_no;
	}

	public void setPerson_identity_no(String person_identity_no) {
		this.person_identity_no = person_identity_no;
	}

	public String getPerson_address() {
		return this.person_address;
	}

	public void setPerson_address(String person_address) {
		this.person_address = person_address;
	}

	public String getPerson_family() {
		return this.person_family;
	}

	public void setPerson_family(String person_family) {
		this.person_family = person_family;
	}

	public String getPerson_nationality_other() {
		return this.person_nationality_other;
	}

	public void setPerson_nationality_other(String person_nationality_other) {
		this.person_nationality_other = person_nationality_other;
	}

	public int getPerson_sex() {
		return this.person_sex;
	}

	public void setPerson_sex(int person_sex) {
		this.person_sex = person_sex;
	}

	public int getPerson_nationality() {
		return this.person_nationality;
	}

	public void setPerson_nationality(int person_nationality) {
		this.person_nationality = person_nationality;
	}

	public int getPerson_merige_statuse() {
		return this.person_merige_statuse;
	}

	public void setPerson_merige_statuse(int person_merige_statuse) {
		this.person_merige_statuse = person_merige_statuse;
	}

	public long getPerson_last_name_id() {
		return person_last_name_id;
	}

	public void setPerson_last_name_id(long person_last_name_id) {
		this.person_last_name_id = person_last_name_id;
	}

	public long getPerson_first_name_id() {
		return person_first_name_id;
	}

	public void setPerson_first_name_id(long person_first_name_id) {
		this.person_first_name_id = person_first_name_id;
	}

	public long getPerson_address_tid() {
		return person_address_tid;
	}

	public void setPerson_address_tid(long person_address_tid) {
		this.person_address_tid = person_address_tid;
	}

	public long getPerson_family_id() {
		return person_family_id;
	}

	public void setPerson_family_id(long person_family_id) {
		this.person_family_id = person_family_id;
	}

	public void setPicture_id(int picture_id) {
		this.picture_id = picture_id;
	}

	public int getPicture_id() {
		return picture_id;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[PersonVo:");
		buffer.append(" person_id: ");
		buffer.append(person_id);
		buffer.append(" person_last_name: ");
		buffer.append(person_last_name);
		buffer.append(" person_first_name: ");
		buffer.append(person_first_name);
		buffer.append(" person_picture: ");
		buffer.append(person_picture);
		buffer.append(" person_picture_filename: ");
		buffer.append(person_picture_filename);
		buffer.append(" person_picture_filesize: ");
		buffer.append(person_picture_filesize);
		buffer.append(" person_picture_date_created: ");
		buffer.append(person_picture_date_created);
		buffer.append(" person_middle_name: ");
		buffer.append(person_middle_name);
		buffer.append(" person_birth_date: ");
		buffer.append(person_birth_date);
		buffer.append(" person_identity_no: ");
		buffer.append(person_identity_no);
		buffer.append(" person_address: ");
		buffer.append(person_address);
		buffer.append(" person_family: ");
		buffer.append(person_family);
		buffer.append(" person_nationality_other: ");
		buffer.append(person_nationality_other);
		buffer.append(" person_sex: ");
		buffer.append(person_sex);
		buffer.append(" person_nationality: ");
		buffer.append(person_nationality);
		buffer.append(" person_merige_statuse: ");
		buffer.append(person_merige_statuse);
		buffer.append("]");
		return buffer.toString();
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setPerson_middle_name_id(long person_middle_name_id) {
		this.person_middle_name_id = person_middle_name_id;
	}

	public long getPerson_middle_name_id() {
		return person_middle_name_id;
	}

	public void setInitiator_id(int initiator_id) {
		this.initiator_id = initiator_id;
	}

	public int getInitiator_id() {
		return initiator_id;
	}

}
