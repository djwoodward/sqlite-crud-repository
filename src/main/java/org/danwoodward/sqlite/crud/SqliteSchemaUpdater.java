package org.danwoodward.sqlite.crud;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.danwoodward.sqlite.utils.SqliteCrudUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

public class SqliteSchemaUpdater {
	private static Logger logger = LoggerFactory.getLogger(SqliteSchemaUpdater.class);

	private final JdbcOperations jdbcOperations;
	private final PlatformTransactionManager txManager;
	private final SqliteCrudManager sqliteCrudManager;

	public SqliteSchemaUpdater(JdbcOperations jdbcOperations, PlatformTransactionManager txManager,
			SqliteCrudManager sqliteCrudManager) {
		this.jdbcOperations = jdbcOperations;
		this.txManager = txManager;
		this.sqliteCrudManager = sqliteCrudManager;
	}

	public void updateSchema() throws Exception {
		TransactionTemplate tmpl = new TransactionTemplate(txManager);
		tmpl.execute(this::doInTransaction);
	}

	// Scenarios
	// 1. table doesn't exist yet
	//       CREATE
	// 2. table exists but is missing a column
	//       ALTER TABLE ADD COLUMN
	// 3. table exists but we don't have an entity for it
	//      Ignore
	// 4. table exists but column type changed
	//      Ignore.
	// 5. table exists but has extra column
	//      Ignore
	private <T> T doInTransaction(TransactionStatus transactionStatus) {
		Map<String, List<String>> existingTables = sqliteCrudManager.getExistingTables();
		Map<Class<?>, EntityMetaData> entityMetaDatas = sqliteCrudManager.getEntityMetaDatas();

		for (EntityMetaData<?> entityMetaData : entityMetaDatas.values()) {
			String tableName = entityMetaData.getTableName();

			List<String> columns = existingTables.get(tableName);
			if (columns == null) {
				StringBuilder sql = new StringBuilder();
				sql.append("CREATE TABLE ").append(entityMetaData.getTableName()).append(" (");
				sql.append(entityMetaData.getPropertyMetaDatas().stream().map(SqliteCrudUtils::getSqlDefinition).collect(Collectors.joining(", ")));
				sql.append(")");
				jdbcOperations.execute(sql.toString());
				logger.info("Created Table: " + tableName + ": " + sql);
			} else {
				logger.info("Table Found " + tableName);
				for (PropertyMetaData propertyMetaData : entityMetaData.getPropertyMetaDatas()) {
					if (!columns.contains(propertyMetaData.getColumnName())) {
						String sql = "ALTER TABLE " + entityMetaData.getTableName() + " ADD COLUMN " + propertyMetaData.getColumnName() + " " + propertyMetaData.getSqliteType() + " DEFAULT " + propertyMetaData.getSqliteType().getDefaultValue();
						jdbcOperations.execute(sql);
						logger.info("Added Column: " + entityMetaData.getTableName() + "." + propertyMetaData.getColumnName());
					}
				}
			}
		}
		return null;
	}
}
