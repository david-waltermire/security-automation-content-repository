package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.KeyedRelationshipInfo;

import java.util.Collection;

public interface ContentHandler {
	void handle(IEntity<?> entity);
	void handle(KeyedRelationshipInfo info);
	Collection<? extends IEntity<?>> getEntities();
}
