package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

import java.util.Collection;


public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct {
	DEFINITION getDefinition();
	IEntity<?> getParentContext();
	Collection<? extends IRelationship<?>> getRelationships();
	Collection<? extends IKeyedRelationship> getKeyedRelationships();
	Collection<? extends IIndirectRelationship> getIndirectRelationships();
	IContentHandle getContentHandle();
}
