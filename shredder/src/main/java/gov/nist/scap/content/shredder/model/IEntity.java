package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

import java.util.Collection;

import org.apache.xmlbeans.XmlCursor;


public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct {
	DEFINITION getDefinition();
	IEntity<?> getParentContext();
	Collection<? extends IRelationship<?>> getRelationships();
	Bookmark getBookmark();
	XmlCursor getCursor();
}
