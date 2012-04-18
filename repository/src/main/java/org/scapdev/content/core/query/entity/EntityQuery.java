package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.Conditional;
import org.scapdev.content.core.query.IConditional;
import org.scapdev.content.core.query.IQuery;

public class EntityQuery extends EntityContext implements IQuery {

	public static EntityQuery selectEntitiesWith(IEntityConstruct construct) {
		return new EntityQuery(new Conditional<IEntityConstruct>(Conditional.Form.CONJUNCTIVE, construct));
	}

	public static EntityQuery selectEntitiesWith(IConditional<? extends IEntityConstruct> conditional) {
		return new EntityQuery(conditional);
	}

	public EntityQuery(IConditional<? extends IEntityConstruct> conditional) {
		super(conditional);
	}
}
