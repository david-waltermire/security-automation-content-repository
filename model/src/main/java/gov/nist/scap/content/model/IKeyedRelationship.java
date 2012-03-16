package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;

public interface IKeyedRelationship extends IReferencingRelationship<IKeyedRelationshipDefinition, IKeyedEntity<?>> {
	IKey getKey();
}
