package org.scapdev.content.core.query.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Key implements IEntityConstruct {
	private final String type;
	private final List<Field> fields;
	

	public static Key key(String type, Field...fields) {
		return new Key(type, fields);
	}

	public static Field field(String name, String value) {
		return new Field(name, value);
	}

	public Key(String type, Field...fields) {
		this.type = type;
		this.fields = Arrays.asList(fields);
	}

	public String getType() {
		return type;
	}

	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public static class Field {
		private final String name;
		private final String value;

		public Field(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
}
