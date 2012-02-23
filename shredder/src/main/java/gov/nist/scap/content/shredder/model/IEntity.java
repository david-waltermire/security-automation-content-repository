package gov.nist.scap.content.shredder.model;

import java.util.Collection;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;


public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct {
	DEFINITION getDefinition();
	Bookmark getBookmark();
	IEntity<?> getParentContext();
	IKey getKey(String keyId);
	void accept(IContainerVisitor visitor);
	Collection<? extends IRelationship<?>> getRelationships();
}
