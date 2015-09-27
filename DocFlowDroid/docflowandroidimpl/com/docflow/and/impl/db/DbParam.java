package com.docflow.and.impl.db;

public class DbParam {

	public static DbParam intParam(Integer value) {
		return new DbParam(java.sql.Types.INTEGER, value);
	}

	public static DbParam longParam(Long value) {
		return new DbParam(java.sql.Types.BIGINT, value);
	}

	public static DbParam longParam(Double value) {
		return new DbParam(java.sql.Types.DOUBLE, value);
	}

	public static DbParam stringParam(String value) {
		return new DbParam(java.sql.Types.VARCHAR, value);
	}

	public static DbParam bytesParam(byte[] value) {
		return new DbParam(java.sql.Types.VARBINARY, value);
	}

	private int type;
	private Object value;

	public DbParam(int type, Object value) {
		super();
		this.type = type;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
