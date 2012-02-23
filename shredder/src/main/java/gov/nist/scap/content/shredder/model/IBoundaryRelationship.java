package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IBoundaryRelationshipDefinition;

public interface IBoundaryRelationship extends IRelationship<IBoundaryRelationshipDefinition> {
	IEntity<?> getContainingObject();
}
