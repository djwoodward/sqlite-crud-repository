package org.danwoodward.sqlite.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.danwoodward.sqlite.test.TestPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcOperations;

@RunWith(JMockit.class)
public class SqliteCrudRepositoryImplTest {
	@Tested private SqliteCrudRepositoryImpl sqliteCrudRepository;

	@Injectable private JdbcOperations jdbcOperations;
	@Injectable private SqliteCrudManager sqliteCrudManager;
	@Injectable private EntityMetaData entityMetaData;

	@Test
	public void testRetrieveAll() {
		List<TestPojo> aList = new ArrayList<>();
		new Expectations() {{
			sqliteCrudManager.get(TestPojo.class); result = entityMetaData;
			entityMetaData.getTableName(); result = "bananas";
			jdbcOperations.query("SELECT * FROM bananas", (SqliteRowMapper) any); result = aList;
		}};

		List<TestPojo> testPojos = sqliteCrudRepository.retrieveAll(TestPojo.class);

		assertSame(aList, testPojos);
	}

	@Test
	public void testRetrieve() {
		TestPojo aTestPojo = new TestPojo();
		new Expectations() {{
			sqliteCrudManager.get(TestPojo.class); result = entityMetaData;
			entityMetaData.getTableName(); result = "bananas";
			jdbcOperations.queryForObject("SELECT * FROM bananas WHERE id = ?", (SqliteRowMapper) any, 12);
				result = aTestPojo;
		}};

		TestPojo testPojo = sqliteCrudRepository.retrieve(12, TestPojo.class);

		assertSame(aTestPojo, testPojo);
	}

	@Test
	public void testDelete() {
		TestPojo aTestPojo = new TestPojo();
		new Expectations() {{
			sqliteCrudManager.get(TestPojo.class); result = entityMetaData;
			entityMetaData.getTableName(); result = "bananas";
			jdbcOperations.update("DELETE FROM bananas WHERE id = ?", 12);
		}};

		sqliteCrudRepository.delete(12, TestPojo.class);
	}

	@Test
	public void testSaveInsert() {
		List<PropertyMetaData> propertyMetaDatas = new ArrayList<>();
		propertyMetaDatas.add(new PropertyMetaData("id", "id", Integer.class));
		propertyMetaDatas.add(new PropertyMetaData("some_int", "someInt", int.class));
		propertyMetaDatas.add(new PropertyMetaData("some_integer", "someInteger", Integer.class));
		propertyMetaDatas.add(new PropertyMetaData("some_string", "someString", String.class));
		propertyMetaDatas.add(new PropertyMetaData("another_string", "anotherString", String.class));

		new Expectations() {{
			sqliteCrudManager.get(TestPojo.class); result = entityMetaData;
			entityMetaData.getTableName(); result = "bananas";
			entityMetaData.getPropertyMetaDatas(); result = propertyMetaDatas;
			jdbcOperations.update("INSERT INTO bananas (some_int, some_integer, some_string, another_string) VALUES (?, ?, ?, ?)",
					12, 42, "someStr", "someOtherStr");
			jdbcOperations.queryForObject("SELECT last_insert_rowid()", Integer.class); result = 99;
		}};

		TestPojo TestPojo = new TestPojo();
		TestPojo.setSomeInt(12);
		TestPojo.setSomeInteger(42);
		TestPojo.setSomeString("someStr");
		TestPojo.setAnotherString("someOtherStr");

		TestPojo savedPojo = sqliteCrudRepository.save(TestPojo);
		assertEquals((Integer) 99, savedPojo.getId());
	}

	@Test
	public void testSaveUpdate() {
		List<PropertyMetaData> propertyMetaDatas = new ArrayList<>();
		propertyMetaDatas.add(new PropertyMetaData("id", "id", Integer.class));
		propertyMetaDatas.add(new PropertyMetaData("some_int", "someInt", int.class));
		propertyMetaDatas.add(new PropertyMetaData("some_integer", "someInteger", Integer.class));
		propertyMetaDatas.add(new PropertyMetaData("some_string", "someString", String.class));
		propertyMetaDatas.add(new PropertyMetaData("another_string", "anotherString", String.class));

		new Expectations() {{
			sqliteCrudManager.get(TestPojo.class); result = entityMetaData;
			entityMetaData.getTableName(); result = "bananas";
			entityMetaData.getPropertyMetaDatas(); result = propertyMetaDatas;
			jdbcOperations.update("UPDATE bananas SET some_int = ?, some_integer = ?, some_string = ?, another_string = ? WHERE id = ?",
					12, 42, "someStr", "someOtherStr", 99);
		}};

		TestPojo TestPojo = new TestPojo();
		TestPojo.setId(99);
		TestPojo.setSomeInt(12);
		TestPojo.setSomeInteger(42);
		TestPojo.setSomeString("someStr");
		TestPojo.setAnotherString("someOtherStr");

		TestPojo savedPojo = sqliteCrudRepository.save(TestPojo);
		assertEquals((Integer) 99, savedPojo.getId());
	}
}
