package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IRelationshipDefinition;

public interface IRelationship<DEFINITION extends IRelationshipDefinition> extends IContentConstruct {
	IEntity<?> getOwningEntity();
	DEFINITION getDefinition();
}
