package org.scapdev.content.core.query.entity;

import java.net.URI;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityId implements IEntityConstruct {
	public static EntityId entityId(URI entityId) {
		return new EntityId(entityId);
	}

	private final URI entityId;

	@JsonCreator
	public EntityId(@JsonProperty("entityId") URI entityId) {
		this.entityId = entityId;
	}

	public URI getEntityId() {
		return entityId;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}
}
