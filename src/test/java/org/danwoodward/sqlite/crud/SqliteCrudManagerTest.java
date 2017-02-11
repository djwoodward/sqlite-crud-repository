package org.danwoodward.sqlite.crud;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mockit.Delegate;
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
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(JMockit.class)
public class SqliteCrudManagerTest {

	@Tested private SqliteCrudManager sqliteCrudManager;
	@Injectable private JdbcOperations jdbcOperations;
	@Injectable private PlatformTransactionManager txManager;
	@Injectable private ResultSet resultSet1;
	@Injectable private ResultSet resultSet2;
	@Injectable private ResultSet resultSet3;
	@Injectable private ResultSet resultSet4;

	@Test
	public void testGetEntities() throws Exception {
		sqliteCrudManager.registerEntity(SmallTestPojo.class);
		sqliteCrudManager.registerEntity(TestPojo.class);

		sqliteCrudManager.afterPropertiesSet();

		assertEquals(new HashSet<>(Arrays.asList(SmallTestPojo.class, TestPojo.class)), sqliteCrudManager.getEntities());
	}

	@Test
	public void testGetEntityMetaDatas() throws Exception {
		sqliteCrudManager.registerEntity(SmallTestPojo.class);
		sqliteCrudManager.registerEntity(TestPojo.class);

		sqliteCrudManager.afterPropertiesSet();

		Map<Class<?>, EntityMetaData> entityMetaDatas = sqliteCrudManager.getEntityMetaDatas();
		assertEquals(entityMetaDatas.keySet(), sqliteCrudManager.getEntities());
	}

	@Test
	public void testGetByClass() throws Exception {
		sqliteCrudManager.registerEntity(SmallTestPojo.class);
		sqliteCrudManager.registerEntity(TestPojo.class);

		sqliteCrudManager.afterPropertiesSet();

		assertNotNull(sqliteCrudManager.get(SmallTestPojo.class));
		assertNotNull(sqliteCrudManager.get(TestPojo.class));
		try {
			sqliteCrudManager.get(Date.class);
			fail("Date.class is not a valid entity, this should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testGetExistingTables() throws Exception {
		List<String> tableNames = Arrays.asList("small_test_pojo", "test_pojo", "other_table");
		new Expectations() {{
			jdbcOperations.queryForList("SELECT tbl_name FROM sqlite_master WHERE type='table'", String.class); result = tableNames;

			jdbcOperations.query("PRAGMA table_info(small_test_pojo)", (RowCallbackHandler) any); result = new Delegate() {
				void query(String sql, RowCallbackHandler rch) throws SQLException {
					rch.processRow(resultSet1);
				}
			};
			resultSet1.getString("name"); result = "test";

			jdbcOperations.query("PRAGMA table_info(test_pojo)", (RowCallbackHandler) any); result = new Delegate() {
				void query(String sql, RowCallbackHandler rch) throws SQLException {
					rch.processRow(resultSet2);
					rch.processRow(resultSet3);
				}
			};
			resultSet2.getString("name"); result = "abc";
			resultSet3.getString("name"); result = "def";

			jdbcOperations.query("PRAGMA table_info(other_table)", (RowCallbackHandler) any); result = new Delegate() {
				void query(String sql, RowCallbackHandler rch) throws SQLException {
					rch.processRow(resultSet4);
				}
			};
			resultSet4.getString("name"); result = "someValue";
		}};

		sqliteCrudManager.registerEntity(SmallTestPojo.class);
		sqliteCrudManager.registerEntity(TestPojo.class);

		sqliteCrudManager.afterPropertiesSet();

		Map<String, List<String>> expected = new HashMap<>();
		expected.put("small_test_pojo", Arrays.asList("test"));
		expected.put("test_pojo", Arrays.asList("abc", "def"));
		expected.put("other_table", Arrays.asList("someValue"));

		Map<String, List<String>> existingTables = sqliteCrudManager.getExistingTables();
		assertEquals(expected, existingTables);
	}
}
