package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IQuery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityQuery extends EntityContext implements IQuery {

	public static EntityQuery selectEntitiesWith(IConstruct<EntityContext> construct) {
		return new EntityQuery(construct);
	}

	@JsonCreator
	public EntityQuery(@JsonProperty("construct") IConstruct<EntityContext> construct) {
		super(construct);
	}
}
