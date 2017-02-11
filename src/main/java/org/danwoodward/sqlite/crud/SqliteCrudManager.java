package org.danwoodward.sqlite.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

public class SqliteCrudManager implements InitializingBean, BeanFactoryAware {
	// dependencies
	private JdbcOperations jdbcOperations;
	private PlatformTransactionManager txManager;
	private SqliteSchemaUpdater sqliteSchemaUpdater;

	// data
	private Set<Class<?>> entities = new HashSet<>();
	private Map<Class<?>, EntityMetaData> entityMetaDatas = new HashMap<>();
	private Map<String, List<String>> existingTables = new HashMap<>();
	private boolean updateSchema = false;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.jdbcOperations = beanFactory.getBean(JdbcOperations.class);
		this.txManager = beanFactory.getBean(PlatformTransactionManager.class);
		this.sqliteSchemaUpdater = new SqliteSchemaUpdater(jdbcOperations, txManager, this);
	}

	@Override // aka @PostConstruct
	public void afterPropertiesSet() throws Exception {
		for (Class<?> clazz : entities) {
			EntityMetaData metaData = EntityMetaData.fromClass(clazz);
			validateMetaData(metaData);
			entityMetaDatas.put(clazz, metaData);
		}
		existingTables = retrieveSqliteTableData();

		if (updateSchema) {
			sqliteSchemaUpdater.updateSchema();
		}
	}

	/**
	 * If set the database schema will be updated to match the registered entities. This is non destructive and will not
	 * delete any tables, and will not delete any columns.
	 */
	public void updateSchema(boolean updateSchema) {
		this.updateSchema = updateSchema;
	}

	public void registerEntity(Class<?> clazz) {
		entities.add(clazz);
	}

	/**
	 * All entities registered to this manager.
	 */
	public Set<Class<?>> getEntities() {
		return entities;
	}

	/**
	 * EntityMetaData extracted from entities.
	 */
	public Map<Class<?>, EntityMetaData> getEntityMetaDatas() {
		return entityMetaDatas;
	}

	public <T> EntityMetaData<T> get(Class<T> type) {
		EntityMetaData entityMetaData = entityMetaDatas.get(type);
		if (entityMetaData == null) {
			throw new IllegalArgumentException(type + " cannot be used with this repository. It was not registered with SqliteCrudManager.");
		}
		return entityMetaData;
	}

	/**
	 * Table names mapped to columns. This will always return the initial state of the DB. After the schema is updated
	 * this will no longer reflect the current state of the table. The intentions of this data is to be used to update
	 * the schema based on the current registered entities.
	 */
	public Map<String, List<String>> getExistingTables() {
		return existingTables;
	}

	/**
	 * Verify this entity can be used with SqliteCrud
	 */
	private void validateMetaData(EntityMetaData entityMetaData) {
		// verify there is a field: "Integer id"
		// verify all the fields can be mapped to a sql type
		List<PropertyMetaData> propertyMetaDatas = entityMetaData.getPropertyMetaDatas();
		boolean idFound = false;
		for (PropertyMetaData propertyMetaData : propertyMetaDatas) {
			Assert.notNull(propertyMetaData.getSqliteType(), entityMetaData.getTableClass()
					+ " field " + propertyMetaData.getPropertyType() + " could not be mapped to a sql type");
			if (propertyMetaData.getColumnName().equals("id") && propertyMetaData.getPropertyType() == Integer.class) {
				idFound = true;
			}
		}
		Assert.isTrue(idFound, entityMetaData.getTableClass() + " missing 'Integer id'.");
	}

	/**
	 * Returns a table names mapped to columns
	 */
	private Map<String, List<String>> retrieveSqliteTableData() {
		Map<String, List<String>> columnDataByTable = new HashMap<>();
		List<String> tableNames = jdbcOperations.queryForList("SELECT tbl_name FROM sqlite_master WHERE type='table'", String.class);
		// get table meta data
		for (String tableName : tableNames) {
			List<String> columns = new ArrayList<>();
			jdbcOperations.query("PRAGMA table_info(" + tableName + ")",
					(RowCallbackHandler) rs -> columns.add(rs.getString("name")));

			columnDataByTable.put(tableName, columns);
		}
		return columnDataByTable;
	}
}
