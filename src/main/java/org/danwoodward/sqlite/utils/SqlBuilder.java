package org.danwoodward.sqlite.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is NOT threadsafe
 */
public class SqlBuilder {
	private final StringBuilder sql;
	private final List<Object> args;

	private SqlBuilder(String initial) {
		this.sql = new StringBuilder(initial);
		this.args = new ArrayList<>();
	}

	public static SqlBuilder insert() {
		return new SqlBuilder("INSERT ");
	}

	public static SqlBuilder select() {
		return new SqlBuilder("SELECT * ");
	}

	public static SqlBuilder update() {
		return new SqlBuilder("UPDATE ");
	}

	public static SqlBuilder delete() {
		return new SqlBuilder("DELETE ");
	}

	public SqlBuilder table(String table) {
		sql.append(table).append(" ");
		return this;
	}

	public SqlBuilder from(String table) {
		sql.append("FROM ").append(table).append(" ");
		return this;
	}

	// SET C1 = 9, C2 = 4, C3 = 4
	public SqlBuilder set(List<String> columns, List<Object> values) {
		sql.append("SET ");
		sql.append(columns.stream().map(s -> s + " = ?").collect(Collectors.joining(", "))).append(" ");

		args.addAll(values);

		return this;
	}

	public SqlBuilder columns(List<String> columns) {
		sql.append(columns.stream().collect(Collectors.joining(", ", "(", ") ")));
		return this;
	}

	public SqlBuilder values(List<Object> objs) {
		args.addAll(objs);
		sql.append("VALUES ");
		sql.append(Stream.generate(() -> "?").limit(objs.size()).collect(Collectors.joining(", ", "(", ") ")));
		return this;
	}

	public SqlBuilder eq(Object obj) {
		sql.append("= ? ");
		args.add(obj);
		return this;
	}

	public SqlBuilder where(String column) {
		sql.append("WHERE ").append(column).append(" ");
		return this;
	}

	public SqlBuilder into(String table) {
		sql.append("INTO ").append(table).append(" ");
		return this;
	}

	public String toSql() {
		return sql.toString().trim();
	}

	public List<Object> getArgs() {
		return args;
	}

}
