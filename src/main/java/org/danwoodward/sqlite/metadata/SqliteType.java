package org.danwoodward.sqlite.metadata;

public enum SqliteType {
	/**
	 * NULL value.
	 */
	NULL(""),
	/**
	 * signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending on the magnitude of the value.
	 */
	INTEGER("0"),
	/**
	 * floating point value, stored as an 8-byte IEEE floating point number.
	 */
	REAL("0.0"),
	/**
	 * text string, stored using the database encoding (UTF-8, UTF-16BE or UTF-16LE).
	 */
	TEXT("''"),
	/**
	 * blob of data, stored exactly as it was input.
	 */
	BLOB("");

	private String defaultValue;
	SqliteType(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public static SqliteType fromJavaType(Class<?> clazz) {
		if (clazz == Integer.class || clazz == int.class) {
			return INTEGER;
		}
		if (clazz == String.class) {
			return TEXT;
		}
		return null;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}
