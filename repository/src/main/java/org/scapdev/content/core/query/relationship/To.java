package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.Conditional;
import org.scapdev.content.core.query.IConditional;
import org.scapdev.content.core.query.entity.EntityContext;
import org.scapdev.content.core.query.entity.IEntityConstruct;

public class To extends EntityContext implements IRelationshipConstruct {

	public To(IConditional<? extends IEntityConstruct> conditional) {
		super(conditional);
	}

	public static To to(IEntityConstruct construct) {
		return new To(new Conditional<IEntityConstruct>(Conditional.Form.CONJUNCTIVE, construct));
	}

	public static To to(IConditional<? extends IEntityConstruct> conditional) {
		return new To(conditional);
	}
}
