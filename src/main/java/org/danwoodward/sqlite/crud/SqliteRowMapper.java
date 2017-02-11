package org.danwoodward.sqlite.crud;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.danwoodward.sqlite.metadata.EntityMetaData;
import org.danwoodward.sqlite.metadata.PropertyMetaData;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.RowMapper;

public class SqliteRowMapper<T> implements RowMapper<T> {
	private final EntityMetaData<T> entityMetaData;

	public SqliteRowMapper(EntityMetaData<T> entityMetaData) {
		this.entityMetaData = entityMetaData;
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			T obj = entityMetaData.getTableClass().newInstance();
			BeanWrapper objWrapper = new BeanWrapperImpl(obj);
			for (PropertyMetaData propertyMetaData : entityMetaData.getPropertyMetaDatas()) {
				String columnName = propertyMetaData.getColumnName();
				String propertyName = propertyMetaData.getPropertyName();
				objWrapper.setPropertyValue(propertyName, rs.getObject(columnName));
			}
			return (T) objWrapper.getWrappedInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

}
