package org.danwoodward.sqlite.metadata;

public class PropertyMetaData {
	private final String columnName;
	private final String propertyName;
	private final SqliteType sqliteType;
	private final Class<?> propertyType;

	public PropertyMetaData(String columnName, String propertyName, Class<?> propertyType) {
		this.columnName = columnName;
		this.sqliteType = SqliteType.fromJavaType(propertyType);
		this.propertyName = propertyName;
		this.propertyType = propertyType;
	}

	public String getColumnName() {
		return columnName;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}


	public SqliteType getSqliteType() {
		return sqliteType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isNotId() {
		return !columnName.equals("id");
	}

	@Override
	public String toString() {
		return "PropertyMetaData{" +
				"columnName='" + columnName + '\'' +
				", propertyName='" + propertyName + '\'' +
				", sqliteType=" + sqliteType +
				", propertyType=" + propertyType +
				'}';
	}
}
