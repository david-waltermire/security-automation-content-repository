package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IKeyedRelationshipDefinition;

public interface IKeyedRelationship extends IRelationship<IKeyedRelationshipDefinition> {
	IKeyedEntity<?> getReferencedEntity();
	IKey getKey();
}
