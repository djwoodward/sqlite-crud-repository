package org.danwoodward.sqlite.crud;

import java.util.List;

public interface SqliteCrudRepository {
	<T> List<T> retrieveAll(Class<T> type);
	<T> T retrieve(int id, Class<T> type);
	<T> T save(T obj);
	void delete(int id, Class<?> type);
}
