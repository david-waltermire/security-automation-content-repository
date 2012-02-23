package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

public interface IContainer<DEFINITION extends IEntityDefinition> extends IEntity<DEFINITION> {
	IKey getKey(String keyId);
	void accept(IContainerVisitor visitor);
}
