package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.definitions.KeyedRelationshipInfo;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IMutableEntity;

import java.util.Collection;

public interface ContentHandler {
	void handle(IMutableEntity<?> entity);
	void handle(KeyedRelationshipInfo info);
	Collection<? extends IEntity<?>> getEntities();
}
