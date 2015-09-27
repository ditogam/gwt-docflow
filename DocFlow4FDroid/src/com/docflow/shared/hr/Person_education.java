package com.docflow.shared.hr;

/* Code Generator Information.
 * generator Version 1.0.0 release 2007/10/10
 * generated Date Tue Oct 25 18:59:28 GET 2011
 */
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Person_educationVo.
 * 
 * @author root
 * @version 1.0 history Symbol Date Person Note [1] 2011/10/25 root Generated.
 */
public class Person_education implements IsSerializable {

	/**
	 * 
	 */

	public static final String TABLE = "PERSON_EDUCATION";

	/**
	 * person_id:int4(10)
	 */
	private int person_id;

	/**
	 * education_name_and_place:varchar(2147483647)
	 */
	private String education_name_and_place;

	/**
	 * faculty:varchar(2147483647)
	 */
	private String faculty;

	/**
	 * enter_year:int4(10)
	 */
	private int enter_year;

	/**
	 * greduate_year:int4(10)
	 */
	private int greduate_year;

	/**
	 * leave_grade:int4(10)
	 */
	private int leave_grade;

	/**
	 * greduate_degree_certificate_num:varchar(2147483647)
	 */
	private String greduate_degree_certificate_num;

	/**
	 * id:serial(10) <Primary Key>
	 */
	private int id;
	private long education_name_and_place_id;

	/**
	 * Constractor
	 */
	public Person_education() {
	}

	/**
	 * Constractor
	 * 
	 * @param <code>id</code>
	 */
	public Person_education(int id) {
		this.id = id;
	}

	public int getPerson_id() {
		return this.person_id;
	}

	public void setPerson_id(int person_id) {
		this.person_id = person_id;
	}

	public String getEducation_name_and_place() {
		return this.education_name_and_place;
	}

	public void setEducation_name_and_place(String education_name_and_place) {
		this.education_name_and_place = education_name_and_place;
	}

	public String getFaculty() {
		return this.faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public int getEnter_year() {
		return this.enter_year;
	}

	public void setEnter_year(int enter_year) {
		this.enter_year = enter_year;
	}

	public int getGreduate_year() {
		return this.greduate_year;
	}

	public void setGreduate_year(int greduate_year) {
		this.greduate_year = greduate_year;
	}

	public int getLeave_grade() {
		return this.leave_grade;
	}

	public void setLeave_grade(int leave_grade) {
		this.leave_grade = leave_grade;
	}

	public String getGreduate_degree_certificate_num() {
		return this.greduate_degree_certificate_num;
	}

	public void setGreduate_degree_certificate_num(
			String greduate_degree_certificate_num) {
		this.greduate_degree_certificate_num = greduate_degree_certificate_num;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Person_educationVo:");
		buffer.append(" person_id: ");
		buffer.append(person_id);
		buffer.append(" education_name_and_place: ");
		buffer.append(education_name_and_place);
		buffer.append(" faculty: ");
		buffer.append(faculty);
		buffer.append(" enter_year: ");
		buffer.append(enter_year);
		buffer.append(" greduate_year: ");
		buffer.append(greduate_year);
		buffer.append(" leave_grade: ");
		buffer.append(leave_grade);
		buffer.append(" greduate_degree_certificate_num: ");
		buffer.append(greduate_degree_certificate_num);
		buffer.append(" id: ");
		buffer.append(id);
		buffer.append("]");
		return buffer.toString();
	}

	public void setEducation_name_and_place_id(long education_name_and_place_id) {
		this.education_name_and_place_id = education_name_and_place_id;
	}

	public long getEducation_name_and_place_id() {
		return education_name_and_place_id;
	}

}
