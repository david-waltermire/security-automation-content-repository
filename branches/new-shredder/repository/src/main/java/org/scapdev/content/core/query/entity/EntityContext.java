package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IContext;
import org.scapdev.content.core.query.Type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityContext implements IContext<EntityContext> {
	public static Type<EntityContext> entityType(String type) {
		return new Type<EntityContext>(type);
	}

	private IConstruct<EntityContext> construct;

	@JsonCreator
	public EntityContext(@JsonProperty("construct") IConstruct<EntityContext> construct) {
		this.construct = construct;
	}

	public IConstruct<EntityContext> getConstruct() {
		return construct;
	}
}
