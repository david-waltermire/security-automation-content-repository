package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.Conditional;
import org.scapdev.content.core.query.IConditional;
import org.scapdev.content.core.query.entity.IEntityConstruct;

public class Relationship implements IEntityConstruct {
	public static Relationship relationship(IRelationshipConstruct construct) {
		return new Relationship(new Conditional<IRelationshipConstruct>(Conditional.Form.CONJUNCTIVE, construct));
	}

	public static Relationship relationship(IConditional<? extends IRelationshipConstruct> conditional) {
		return new Relationship(conditional);
	}

	public Relationship(IConditional<? extends IRelationshipConstruct> conditional) {
		// TODO Auto-generated constructor stub
	}
}
