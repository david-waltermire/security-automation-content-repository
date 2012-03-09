package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

public interface IContainer<DEFINITION extends IEntityDefinition> extends IEntity<DEFINITION> {
	IKey getKey(String keyId);
}
