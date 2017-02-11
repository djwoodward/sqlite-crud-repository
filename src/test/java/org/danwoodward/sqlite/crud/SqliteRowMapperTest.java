package org.danwoodward.sqlite.crud;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.test.TestPojo;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class SqliteRowMapperTest {
	@Injectable
	private ResultSet resultSet;

	@Test
	public void test() throws SQLException {
		TestPojo expected = new TestPojo();
		expected.setId(12);
		expected.setSomeString("abc123");
		expected.setAnotherString("def456");
		expected.setSomeInteger(42);
		expected.setSomeInt(102);

		new Expectations() {{
			resultSet.getObject("id"); result = expected.getId();
			resultSet.getObject("some_string"); result = expected.getSomeString();
			resultSet.getObject("another_string"); result = expected.getAnotherString();
			resultSet.getObject("some_integer"); result = expected.getSomeInteger();
			resultSet.getObject("some_int"); result = expected.getSomeInt();
		}};
		EntityMetaData entityMetaData = EntityMetaData.fromClass(TestPojo.class);

		SqliteRowMapper<TestPojo> mapper = new SqliteRowMapper<>(entityMetaData);
		TestPojo mappedPojo = mapper.mapRow(resultSet, 1);

		assertEquals(expected, mappedPojo);
	}
}
