package org.scapdev.content.core.query;

import org.scapdev.content.core.query.entity.ContentId;
import org.scapdev.content.core.query.entity.EntityId;
import org.scapdev.content.core.query.entity.Key;
import org.scapdev.content.core.query.entity.Version;
import org.scapdev.content.core.query.relationship.Relationship;
import org.scapdev.content.core.query.relationship.To;
import org.scapdev.content.core.query.relationship.ToBoundaryIdentifier;


public interface IQueryVisitor<RESULT, CONTEXT extends IContext<CONTEXT>> {
	// General
	RESULT visit(IConditional<CONTEXT> conditional);
	RESULT visit(Type<?> type);

	// Entity
	RESULT visit(ContentId contentId);
	RESULT visit(EntityId entityId);
	RESULT visit(Key key);
	RESULT visit(Version version);
	RESULT visit(Relationship key);

	// Relationship
	RESULT visit(To to);
	RESULT visit(ToBoundaryIdentifier toBoundaryIdentifier);
}
