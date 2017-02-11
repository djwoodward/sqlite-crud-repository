package org.danwoodward.sqlite.test;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TestPojo {
	private Integer id;
	private int someInt;
	private Integer someInteger;
	private String someString;
	private String anotherString;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getSomeInt() {
		return someInt;
	}

	public void setSomeInt(int someInt) {
		this.someInt = someInt;
	}

	public Integer getSomeInteger() {
		return someInteger;
	}

	public void setSomeInteger(Integer someInteger) {
		this.someInteger = someInteger;
	}

	public String getSomeString() {
		return someString;
	}

	public void setSomeString(String someString) {
		this.someString = someString;
	}

	public String getAnotherString() {
		return anotherString;
	}

	public void setAnotherString(String anotherString) {
		this.anotherString = anotherString;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}