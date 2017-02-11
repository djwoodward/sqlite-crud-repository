package org.danwoodward.sqlite.utils;


import org.danwoodward.sqlite.metadata.PropertyMetaData;

public class SqliteCrudUtils {
	public static String toSnakeCase(String camelCase) {
		String regex = "([a-z])([A-Z]+)";
		String replacement = "$1_$2";
		return camelCase.replaceAll(regex, replacement).toLowerCase();
	}

	public static String getSqlDefinition(PropertyMetaData column) {
		String sqlDefinition = column.getColumnName() + " " + column.getSqliteType();
		if (!column.isNotId()) {
			sqlDefinition += " PRIMARY KEY AUTOINCREMENT";
		}
		return sqlDefinition;
	}
}
