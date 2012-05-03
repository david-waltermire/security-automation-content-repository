package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IQueryVisitor;
import org.scapdev.content.core.query.entity.EntityContext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class To extends EntityContext implements IRelationshipConstruct {

	public static To to(IConstruct<EntityContext> construct) {
		return new To(construct);
	}

	@JsonCreator
	public To(@JsonProperty("construct") IConstruct<EntityContext> construct) {
		super(construct);
	}

	public <RESULT> RESULT visit(IQueryVisitor<RESULT, RelationshipContext> visitor) {
		return visitor.visit(this);
	}
}
