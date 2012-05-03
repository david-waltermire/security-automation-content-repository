package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IQueryVisitor;
import org.scapdev.content.core.query.entity.EntityContext;
import org.scapdev.content.core.query.entity.IEntityConstruct;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Relationship extends RelationshipContext implements IEntityConstruct {

	public static Relationship relationship(IConstruct<RelationshipContext> construct) {
		return new Relationship(construct);
	}

	@JsonCreator
	public Relationship(@JsonProperty("construct") IConstruct<RelationshipContext> construct) {
		super(construct);
	}

	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}
}
