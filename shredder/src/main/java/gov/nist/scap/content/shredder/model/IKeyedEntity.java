package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.shredder.rules.IKeyedEntityDefinition;

public interface IKeyedEntity<DEFINITION extends IKeyedEntityDefinition> extends IEntity<DEFINITION> {
	IKey getKey();
}
