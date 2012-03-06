package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

public interface IRelationship<DEFINITION extends IRelationshipDefinition> extends IContentConstruct {
	IEntity<?> getOwningEntity();
	DEFINITION getDefinition();
}
