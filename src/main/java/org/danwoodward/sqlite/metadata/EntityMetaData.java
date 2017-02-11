package org.danwoodward.sqlite.metadata;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.danwoodward.sqlite.utils.SqliteCrudUtils;
import org.springframework.beans.BeanUtils;

public class EntityMetaData<T> {
	private final String tableName;
	private final Class<T> entityClass;
	private final List<PropertyMetaData> propertyMetaDatas = new ArrayList<>();

	private EntityMetaData(String tableName, Class<T> entityClass) {
		this.tableName = tableName;
		this.entityClass = entityClass;
	}

	public Class<T> getTableClass() {
		return entityClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void addColumnMetaData(PropertyMetaData columnMetaData) {
		propertyMetaDatas.add(columnMetaData);
	}

	public List<PropertyMetaData> getPropertyMetaDatas() {
		return propertyMetaDatas;
	}

	public static EntityMetaData fromClass(Class<?> clazz) {
		String tableName = SqliteCrudUtils.toSnakeCase(clazz.getSimpleName());
		EntityMetaData entityMetaData = new EntityMetaData(tableName, clazz);

		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getPropertyType() == Class.class) {
				continue;
			}
			String columnName = SqliteCrudUtils.toSnakeCase(propertyDescriptor.getName());
			String propertyName = propertyDescriptor.getName();

			Class<?> propertyType = propertyDescriptor.getPropertyType();
			entityMetaData.addColumnMetaData(new PropertyMetaData(columnName, propertyName, propertyType));
		}
		return entityMetaData;
	}

	@Override
	public String toString() {
		return "EntityMetaData{" +
				"tableName='" + tableName + '\'' +
				", propertyMetaDatas=" + propertyMetaDatas +
				'}';
	}
}
