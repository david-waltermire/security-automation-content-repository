package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IContext;
import org.scapdev.content.core.query.Type;

public class RelationshipContext implements IContext<RelationshipContext> {
	public static Type<RelationshipContext> relationshipType(String type) {
		return new Type<RelationshipContext>(type);
	}

	private final IConstruct<RelationshipContext> construct;

	public RelationshipContext(IConstruct<RelationshipContext> construct) {
		this.construct = construct;
	}

	public IConstruct<RelationshipContext> getConstruct() {
		return construct;
	}

}
