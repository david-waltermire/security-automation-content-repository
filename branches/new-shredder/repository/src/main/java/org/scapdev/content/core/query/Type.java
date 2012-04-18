package org.scapdev.content.core.query;

import org.scapdev.content.core.query.entity.IEntityConstruct;
import org.scapdev.content.core.query.relationship.IRelationshipConstruct;

public class Type implements IEntityConstruct, IRelationshipConstruct {
	public static Type type(String type) {
		return new Type(type);
	}

	public Type(String type) {
		// TODO Auto-generated constructor stub
	}
}
