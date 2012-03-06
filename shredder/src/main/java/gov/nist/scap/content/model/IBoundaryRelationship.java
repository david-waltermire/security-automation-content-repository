package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IBoundaryRelationshipDefinition;

public interface IBoundaryRelationship extends IRelationship<IBoundaryRelationshipDefinition> {
	IEntity<?> getContainingObject();
}
