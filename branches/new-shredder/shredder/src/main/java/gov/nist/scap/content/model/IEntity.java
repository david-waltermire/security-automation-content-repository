package gov.nist.scap.content.model;

import gov.nist.scap.content.shredder.model.IContentConstruct;
import gov.nist.scap.content.shredder.model.IContentHandle;
import gov.nist.scap.content.shredder.model.IIndirectRelationship;
import gov.nist.scap.content.shredder.model.IKeyedRelationship;
import gov.nist.scap.content.shredder.model.IRelationship;
import gov.nist.scap.content.shredder.rules.IEntityDefinition;

import java.util.Collection;


public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct {
	DEFINITION getDefinition();
	IEntity<?> getParent();
	Collection<? extends IRelationship<?>> getRelationships();
	Collection<? extends IKeyedRelationship> getKeyedRelationships();
	Collection<? extends IIndirectRelationship> getIndirectRelationships();
	IContentHandle getContentHandle();
}
