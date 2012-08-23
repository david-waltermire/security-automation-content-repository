package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityId implements IEntityConstruct {
	public static EntityId entityId(String entityId) {
		return new EntityId(entityId);
	}

	private final String entityId;

	@JsonCreator
	public EntityId(@JsonProperty("entityId") String entityId) {
		this.entityId = entityId;
	}

	public String getEntityId() {
		return entityId;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}
}
