package org.danwoodward.sqlite.crud;

import java.util.List;
import java.util.stream.Collectors;

import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.danwoodward.sqlite.utils.SqlBuilder;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqliteCrudRepositoryImpl implements SqliteCrudRepository, BeanFactoryAware {
	private JdbcOperations jdbcOperations;
	private SqliteCrudManager sqliteCrudManager;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.jdbcOperations = beanFactory.getBean(JdbcTemplate.class);
		this.sqliteCrudManager = beanFactory.getBean(SqliteCrudManager.class);
	}

	@Override
	public <T> List<T> retrieveAll(Class<T> type) {
		EntityMetaData entityMetaData = sqliteCrudManager.get(type);
		SqlBuilder sql = SqlBuilder.select().from(entityMetaData.getTableName());
		return jdbcOperations.query(sql.toSql(), new SqliteRowMapper<>(entityMetaData));
	}

	@Override
	public <T> T retrieve(int id, Class<T> type) {
		EntityMetaData entityMetaData = sqliteCrudManager.get(type);
		SqlBuilder sql = SqlBuilder.select().from(entityMetaData.getTableName()).where("id").eq(id);
		return jdbcOperations.queryForObject(sql.toSql(), new SqliteRowMapper<T>(entityMetaData), sql.getArgs().toArray());
	}

	@Override
	public <T> T save(T obj) {
		EntityMetaData<T> entityMetaData = sqliteCrudManager.get((Class<T>) obj.getClass());
		BeanWrapper objWrapper = new BeanWrapperImpl(obj);

		List<String> columns = entityMetaData.getPropertyMetaDatas().stream().filter(PropertyMetaData::isNotId).map(PropertyMetaData::getColumnName).collect(Collectors.toList());
		List<Object> values = entityMetaData.getPropertyMetaDatas().stream().filter(PropertyMetaData::isNotId).map(c -> objWrapper.getPropertyValue(c.getPropertyName())).collect(Collectors.toList());

		Integer id = (Integer) objWrapper.getPropertyValue("id");
		if (id == null) {
			// INSERT INTO t (c1, c2) VALUES (?, ?)
			SqlBuilder sql = SqlBuilder.insert().into(entityMetaData.getTableName()).columns(columns).values(values);
			jdbcOperations.update(sql.toSql(), sql.getArgs().toArray());
			int newId = jdbcOperations.queryForObject("SELECT last_insert_rowid()", Integer.class);
			objWrapper.setPropertyValue("id", newId);
		} else {
			// UPDATE t c1 = ?, c2 = ?
			SqlBuilder sql = SqlBuilder.update().table(entityMetaData.getTableName()).set(columns, values).where("id").eq(id);
			jdbcOperations.update(sql.toSql(), sql.getArgs().toArray());
		}

		return obj;
	}

	@Override
	public void delete(int id, Class<?> type) {
		EntityMetaData entityMetaData = sqliteCrudManager.get(type);
		SqlBuilder sql = SqlBuilder.delete().from(entityMetaData.getTableName()).where("id").eq(id);
		jdbcOperations.update(sql.toSql(), sql.getArgs().toArray());
	}
}
