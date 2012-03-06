package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IKeyedRelationshipDefinition;

public interface IKeyedRelationship extends IRelationship<IKeyedRelationshipDefinition> {
	IKeyedEntity<?> getReferencedEntity();
	IKey getKey();
}
