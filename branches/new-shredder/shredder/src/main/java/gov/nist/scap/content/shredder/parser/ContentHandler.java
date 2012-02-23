package gov.nist.scap.content.shredder.parser;

import java.util.Collection;

import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.rules.KeyedRelationshipInfo;

public interface ContentHandler {
	void handle(IMutableEntity<?> entity);
	void handle(KeyedRelationshipInfo info);
	Collection<? extends IEntity<?>> getEntities();
}
