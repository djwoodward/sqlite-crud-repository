package org.danwoodward.sqlite.utils;

import static org.junit.Assert.assertEquals;

import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testGetSnakeCase() {
		assertEquals("some_table_name", SqliteCrudUtils.toSnakeCase("SomeTableName"));
		assertEquals("singleword", SqliteCrudUtils.toSnakeCase("Singleword"));
		assertEquals("super_weird_ca_se", SqliteCrudUtils.toSnakeCase("SuperWeirdCaSe"));
		assertEquals("allupper", SqliteCrudUtils.toSnakeCase("ALLUPPER"));
		assertEquals("alllower", SqliteCrudUtils.toSnakeCase("alllower"));
		assertEquals("", SqliteCrudUtils.toSnakeCase(""));
	}

	@Test(expected = NullPointerException.class)
	public void testSnakeCaseRejectsNull() {
		assertEquals(null, SqliteCrudUtils.toSnakeCase(null));
	}

	@Test
	public void testGetSqlDefinition() {
		assertEquals("full_name TEXT",
				SqliteCrudUtils.getSqlDefinition(new PropertyMetaData("full_name", "fullName", String.class)));

		assertEquals("user_age INTEGER",
				SqliteCrudUtils.getSqlDefinition(new PropertyMetaData("user_age", "userAge", Integer.class)));

		assertEquals("user_height INTEGER",
				SqliteCrudUtils.getSqlDefinition(new PropertyMetaData("user_height", "userHeight", int.class)));
	}
}
