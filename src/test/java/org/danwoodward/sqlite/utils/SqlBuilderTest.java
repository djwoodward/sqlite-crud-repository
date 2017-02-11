package org.danwoodward.sqlite.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class SqlBuilderTest {
	@Test
	public void testInsert() {
		SqlBuilder sql = SqlBuilder.insert().into("test_table")
			.columns(Arrays.asList("c1", "c2", "c3"))
			.values(Arrays.asList(53, 13, 24))
			.where("id").eq(42);
		assertEquals("INSERT INTO test_table (c1, c2, c3) VALUES (?, ?, ?) WHERE id = ?", sql.toSql());
		assertEquals(Arrays.asList(53, 13, 24, 42), sql.getArgs());
	}

	@Test
	public void testSelect() {
		SqlBuilder sql = SqlBuilder.select().from("test_table");
		assertEquals("SELECT * FROM test_table", sql.toSql());
		assertEquals(Collections.emptyList(), sql.getArgs());

		SqlBuilder sqlWitArgs = SqlBuilder.select().from("test_table").where("id").eq(42);
		assertEquals("SELECT * FROM test_table WHERE id = ?", sqlWitArgs.toSql());
		assertEquals(Collections.singletonList(42), sqlWitArgs.getArgs());
	}

	@Test
	public void testUpdate() {
		SqlBuilder sql = SqlBuilder.update().table("test_table")
			.set(Arrays.asList("c1", "c2", "c3"), Arrays.asList(53, 13, 24))
			.where("id").eq(42);
		assertEquals("UPDATE test_table SET c1 = ?, c2 = ?, c3 = ? WHERE id = ?", sql.toSql());
		assertEquals(Arrays.asList(53, 13, 24, 42), sql.getArgs());
	}

	@Test
	public void testDelete() {
		SqlBuilder sql = SqlBuilder.delete().table("test_table").where("id").eq(42);
		assertEquals("DELETE test_table WHERE id = ?", sql.toSql());
		assertEquals(Collections.singletonList(42), sql.getArgs());
	}
}
