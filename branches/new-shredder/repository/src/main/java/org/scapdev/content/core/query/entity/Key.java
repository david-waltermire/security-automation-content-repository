package org.scapdev.content.core.query.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Key implements IEntityConstruct {

	public static Key key(String type, Field...fields) {
		return new Key(type, fields);
	}

	public static Field field(String name, String value) {
		return new Field(name, value);
	}

	private final String type;
	private final List<Field> fields;

	public Key(String type, Field...fields) {
		this(type, Arrays.asList(fields));
	}

	@JsonCreator
	public Key(@JsonProperty("type") String type, @JsonProperty("fields") List<Field> fields) {
		this.type = type;
		this.fields = fields;
	}

	public String getType() {
		return type;
	}

	public List<Field> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}

	public static class Field {
		private final String name;
		private final String value;

		@JsonCreator
		public Field(@JsonProperty("name") String name, @JsonProperty("value") String value) {
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
