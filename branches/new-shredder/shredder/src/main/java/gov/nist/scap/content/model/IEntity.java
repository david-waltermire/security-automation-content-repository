package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IEntityDefinition;

import java.util.Collection;


public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct {
	DEFINITION getDefinition();
	IEntity<?> getParent();
	Collection<? extends IRelationship<?>> getRelationships();
	Collection<? extends IKeyedRelationship> getKeyedRelationships();
	Collection<? extends IIndirectRelationship> getIndirectRelationships();
	IContentHandle getContentHandle();
}
