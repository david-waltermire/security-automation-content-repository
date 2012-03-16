package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

public interface IReferencingRelationship<DEFINITION extends IRelationshipDefinition, REFERENCE extends IEntity<?>> extends IRelationship<DEFINITION> {
	REFERENCE getReferencedEntity();
}
