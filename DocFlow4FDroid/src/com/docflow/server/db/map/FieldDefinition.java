package com.docflow.server.db.map;

import java.sql.PreparedStatement;
import java.util.Map;

import com.docflow.server.DMIUtils;

public class FieldDefinition {

	private String fromField;
	private String toField;
	private int field_type;

	public FieldDefinition(String fromField, String toField, int field_type) {
		super();
		this.fromField = fromField;
		this.toField = toField;
		this.field_type = field_type;
	}

	public FieldDefinition(String fromField, int field_type) {
		this(fromField, fromField, field_type);
	}

	public FieldDefinition(String fromField) {
		this(fromField, fromField, java.sql.Types.VARCHAR);
	}

	public FieldDefinition(String fromField, String toField) {
		this(fromField, toField, java.sql.Types.VARCHAR);
	}

	public String getFromField() {
		return fromField;
	}

	public String getToField() {
		return toField;
	}

	public void setValue(PreparedStatement stmt, int index, Map<?, ?> vals)
			throws Exception {
		Object value = vals.get(fromField);
		if (value == null)
			stmt.setNull(index, field_type);
		else {
			switch (field_type) {
			case java.sql.Types.INTEGER:
				stmt.setInt(index, DMIUtils.getRowValueLong(value).intValue());
				break;
			case java.sql.Types.TIMESTAMP:
				stmt.setLong(index, DMIUtils.getRowValueDateTime(value)
						.getTime());
				break;
			default:
				stmt.setString(index, value.toString());
				break;
			}
		}
	}

}
