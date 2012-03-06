package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IBoundaryRelationshipDefinition;

public interface IBoundaryRelationship extends IRelationship<IBoundaryRelationshipDefinition> {
	IEntity<?> getContainingObject();
}
