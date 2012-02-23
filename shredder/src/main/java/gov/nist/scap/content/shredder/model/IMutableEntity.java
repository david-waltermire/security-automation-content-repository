package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

public interface IMutableEntity<DEFINITION extends IEntityDefinition> extends IEntity<DEFINITION> {
	void appendRelationship(IRelationship<?> relationship);
	/**
	 * 
	 * @return the key for the entity if it is indexed or <code>null</code> otherwise
	 */
	IKey getKey();
}
