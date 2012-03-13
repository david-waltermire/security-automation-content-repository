package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;

public interface ICompositeRelationship extends IRelationship<ICompositeRelationshipDefinition> {
	IEntity<?> getContainingObject();
}
