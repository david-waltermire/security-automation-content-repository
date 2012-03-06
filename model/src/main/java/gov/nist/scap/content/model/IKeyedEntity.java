package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;

public interface IKeyedEntity<DEFINITION extends IKeyedEntityDefinition> extends IEntity<DEFINITION> {
	IKey getKey();
}
