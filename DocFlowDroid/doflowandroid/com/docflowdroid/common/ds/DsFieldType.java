package com.docflowdroid.common.ds;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum DsFieldType implements IsSerializable {
	/**
	 * Generic text, e.g. <code>"John Doe"</code>. This is the default field
	 * type. Use <code>field.length</code> to set length.
	 */
	TEXT("text"),
	/**
	 * A boolean value, e.g. <code>true</code>
	 */
	BOOLEAN("boolean"),
	/**
	 * A whole number, e.g. <code>123</code>
	 */
	INTEGER("integer"),
	/**
	 * A floating point (decimal) number, e.g. <code>1.23</code>
	 */
	FLOAT("float"),
	/**
	 * A logical date, with no time value (such as a holiday or birthday).
	 * Represented on the client as a JavaScript <code>Date</code> object where
	 * time values are ignored. See
	 * {@link com.smartgwt.client.docs.DateFormatAndStorage} for more
	 * information on date display and serialization formats. See also
	 * {@link com.smartgwt.client.util.Date} for Smart GWT extensions to the
	 * <code>Date</code> object.
	 */
	DATE("date"),
	/**
	 * A time of day, with no date. Represented on the client as a JavaScript
	 * <code>Date</code> object in UTC/GMT by default (see also
	 * {@link com.smartgwt.client.docs.DateFormatAndStorage} and the String
	 * class).
	 */
	TIME("time"),
	/**
	 * A date and time, accurate to the second. Represented on the client as a
	 * JavaScript <code>Date</code> object. See also
	 * {@link com.smartgwt.client.docs.DateFormatAndStorage} and
	 * {@link com.smartgwt.client.util.Date} for Smart GWT extensions to the
	 * <code>Date</code> object.
	 */
	DATETIME("datetime"),
	/**
	 * A text value constrained to a set of legal values specified by the
	 * field's {@link com.smartgwt.client.data.DataSourceField#getValueMap
	 * valueMap}, as though a {@link com.smartgwt.client.types.ValidatorType} of
	 * "isOneOf" had been declared.
	 */
	ENUM("enum"),
	/**
	 * An enum whose values are numeric.
	 */
	INTENUM("intEnum"),
	/**
	 * If you are using the Smart GWT SQL datasource connector, a
	 * <code>sequence</code> is a unique, increasing whole number, incremented
	 * whenever a new record is added. Otherwise, <code>sequence</code> behaves
	 * identically to <code>integer</code>. This type is typically used with
	 * <code>field.primaryKey</code> to auto-generate unique primary keys. See
	 * also {@link com.smartgwt.client.data.DataSourceField#getSequenceName
	 * sequenceName}.
	 */
	SEQUENCE("sequence"),
	/**
	 * A string representing a well-formed URL. Some components will render this
	 * as an HTML link (using an anchor tag for example).
	 */
	LINK("link"),
	/**
	 * A string representing a well-formed URL that points to an image. Some
	 * components will render an IMG tag with the value of this field as the
	 * 'src' attribute to render the image.
	 */
	IMAGE("image"),
	/**
	 * Arbitrary binary data. When this field type is present, three additional
	 * fields are automatically generated. They are: &lt;fieldName&gt;_filename,
	 * &lt;fieldName&gt;_filesize, and &lt;fieldName&gt;_date_created where
	 * &lt;fieldName&gt; is the value of the <code>name</code> attribute of this
	 * field. These fields are marked as
	 * {@link com.smartgwt.client.data.DataSourceField#getHidden hidden}
	 * <code>:true</code> to suppress their rendering by default. You can show
	 * one or more of these fields by specifying the field with a
	 * <code>hidden:false</code> override in the fields array of the databound
	 * component. <i>Stream / view file support for custom DataSources</i>: a
	 * custom DataSource or DMI must implement the "viewFile" and "downloadFile"
	 * operationTypes and return a single Record with a byte[] as the field
	 * value for the binary field. For more detail see the overview of
	 * {@link com.smartgwt.client.docs.BinaryFields Binary Fields}.
	 */
	BINARY("binary"),
	/**
	 * Binary data comprising an image. Causes
	 * {@link com.smartgwt.client.widgets.form.fields.ViewFileItem} to be used
	 * when the field is displayed in a form, allowing the image to optionally
	 * be displayed inline.
	 */
	IMAGEFILE("imageFile"),
	/**
	 * Fields of this type can contain any data value and have no default
	 * formatting or validation behavior. This is useful as the
	 * {@link com.smartgwt.client.data.SimpleType#getInheritsFrom parent type}
	 * for SimpleTypes where you do not want any of the standard validation or
	 * formatting logic to be inherited from the standard built-in types.
	 */
	ANY("any"),
	/**
	 * Synonymous with "any".
	 */
	CUSTOM("custom"),
	/**
	 * Fields of this type are automatically populated by the Smart GWT Server
	 * with the current authenticated userId as part of "add" and "update"
	 * operations. By default, fields of this type are hidden and not editable;
	 * the server ignores any value that the client sends in a field of this
	 * type. Note that the "authenticated user" can be set explicitly on the
	 * server-side <code>RPCManager</code> using the setUserId() method, or it
	 * can come from the servlet API if you are using its built-in
	 * authentication scheme. See the server-side Javadocs for
	 * <code>RPCManager</code>.
	 */
	MODIFIER("modifier"),
	/**
	 * Fields of this type are automatically populated by the Smart GWT Server
	 * with the current date and time as part of "add" and "update" operations.
	 * By default, fields of this type are hidden and not editable; the server
	 * ignores any value that the client sends in a field of this type.
	 */
	MODIFIERTIMESTAMP("modifierTimestamp"),
	/**
	 * Fields of this type are automatically populated by the Smart GWT Server
	 * with the current authenticated userId as part of "add" operations. By
	 * default, fields of this type are hidden and not editable; the server
	 * ignores any value that the client sends in a field of this type. The
	 * notes about type "modifier" also apply to fields of this type.
	 */
	CREATOR("creator"),
	/**
	 * Fields of this type are automatically populated by the Smart GWT Server
	 * with the current date and time as part of an "add" operation (when the
	 * record is first created). By default, fields of this type are hidden and
	 * not editable; the server ignores any value that the client sends in a
	 * field of this type.
	 */
	CREATORTIMESTAMP("creatorTimestamp"),
	/**
	 * Same as "text", but causes
	 * {@link com.smartgwt.client.widgets.form.fields.PasswordItem} to be used
	 * by default for editing (hides typed-in value).
	 */
	PASSWORD("password"),
	/**
	 * A special field type specifically for use with Unicode data in
	 * conjunction with the Microsoft SQL Server database. Field type "ntext"
	 * implies the use of
	 * {@link com.smartgwt.client.data.DataSourceField#getSqlStorageStrategy
	 * sqlStorageStrategy} "ntext"; other than that, this type is identical to
	 * "text"
	 */
	NTEXT("ntext"),
	/**
	 * An integer number with locale-based formatting, e.g.
	 * <code>12,345,678</code>. See
	 * {@link com.smartgwt.client.docs.LocalizedNumberFormatting Localized
	 * Number Formatting} for more info.
	 */
	LOCALEINT("localeInt"),
	/**
	 * A float number with locale-based formatting, e.g. <code>12,345.67</code>.
	 * See {@link com.smartgwt.client.docs.LocalizedNumberFormatting Localized
	 * Number Formatting} for more info.
	 */
	LOCALEFLOAT("localeFloat"),
	/**
	 * A float number with locale-based formatting and using currency symbol,
	 * e.g. <code>$12,345.67</code>. See
	 * {@link com.smartgwt.client.docs.LocalizedNumberFormatting Localized
	 * Number Formatting} for more info.
	 */
	LOCALECURRENCY("localeCurrency");
	private String value;

	DsFieldType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
