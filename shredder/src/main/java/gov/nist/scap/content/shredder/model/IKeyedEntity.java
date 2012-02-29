package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

public interface IKeyedEntity<DEFINITION extends IEntityDefinition> extends IEntity<DEFINITION> {
	IKey getKey();
}
