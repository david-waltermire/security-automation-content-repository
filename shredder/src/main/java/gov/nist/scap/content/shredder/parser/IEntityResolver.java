package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;

public interface IEntityResolver {
	public IKeyedEntity<?> resolveEntity(IKey key);
}
