package org.danwoodward.sqlite.crud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.test.SmallTestPojo;
import org.danwoodward.sqlite.test.TestPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(JMockit.class)
public class SqliteSchemaUpdaterTest  {
	@Tested private SqliteSchemaUpdater sqliteSchemaUpdater;

	@Injectable private JdbcOperations jdbcOperations;
	@Injectable private PlatformTransactionManager txManager;
	@Injectable private SqliteCrudManager sqliteCrudManager;

	@Test
	public void testUpdaterNoExistingTables() throws Exception {
		Map<Class<?>, EntityMetaData> entityMetaDatas = new HashMap<>();
		entityMetaDatas.put(TestPojo.class, EntityMetaData.fromClass(TestPojo.class));
		entityMetaDatas.put(SmallTestPojo.class, EntityMetaData.fromClass(SmallTestPojo.class));

		new Expectations() {{
			sqliteCrudManager.getEntityMetaDatas(); result = entityMetaDatas;
			sqliteCrudManager.getExistingTables(); result = new HashMap<>();
			jdbcOperations.execute("CREATE TABLE test_pojo (another_string TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, some_int INTEGER, some_integer INTEGER, some_string TEXT)");
			jdbcOperations.execute("CREATE TABLE small_test_pojo (id INTEGER PRIMARY KEY AUTOINCREMENT, value TEXT)");
		}};
		sqliteSchemaUpdater.updateSchema();
	}

	@Test
	public void testUpdaterMissingTableAndMissingColumns() throws Exception {
		Map<Class<?>, EntityMetaData> entityMetaDatas = new HashMap<>();
		entityMetaDatas.put(TestPojo.class, EntityMetaData.fromClass(TestPojo.class));
		entityMetaDatas.put(SmallTestPojo.class, EntityMetaData.fromClass(SmallTestPojo.class));
		Map<String, List<String>> existingTables = new HashMap<>();
		existingTables.put("test_pojo", Arrays.asList("id", "another_string", "some_int"));

		new Expectations() {{
			sqliteCrudManager.getEntityMetaDatas(); result = entityMetaDatas;
			sqliteCrudManager.getExistingTables(); result = existingTables;
			jdbcOperations.execute("CREATE TABLE small_test_pojo (id INTEGER PRIMARY KEY AUTOINCREMENT, value TEXT)");
			jdbcOperations.execute("ALTER TABLE test_pojo ADD COLUMN some_integer INTEGER DEFAULT 0");
			jdbcOperations.execute("ALTER TABLE test_pojo ADD COLUMN some_string TEXT DEFAULT ''");
		}};
		sqliteSchemaUpdater.updateSchema();
	}

	@Test
	public void testUpdaterNoChanges() throws Exception {
		Map<Class<?>, EntityMetaData> entityMetaDatas = new HashMap<>();
		entityMetaDatas.put(TestPojo.class, EntityMetaData.fromClass(TestPojo.class));
		entityMetaDatas.put(SmallTestPojo.class, EntityMetaData.fromClass(SmallTestPojo.class));
		Map<String, List<String>> existingTables = new HashMap<>();
		existingTables.put("test_pojo", Arrays.asList("id", "another_string", "some_int", "some_integer", "some_string"));
		existingTables.put("small_test_pojo", Arrays.asList("id", "value"));

		new Expectations() {{
			sqliteCrudManager.getEntityMetaDatas(); result = entityMetaDatas;
			sqliteCrudManager.getExistingTables(); result = existingTables;
			jdbcOperations.execute(anyString); times = 0;
		}};
		sqliteSchemaUpdater.updateSchema();
	}
}
