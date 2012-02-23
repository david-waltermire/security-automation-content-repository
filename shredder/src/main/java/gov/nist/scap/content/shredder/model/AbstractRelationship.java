package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IRelationshipDefinition;

public class AbstractRelationship<DEFINITION extends IRelationshipDefinition> implements IRelationship<DEFINITION> {
	private final DEFINITION definition;
	private final IEntity<?> owningEntity;

	public AbstractRelationship(DEFINITION definition, IEntity<?> owningEntity) {
		this.definition = definition;
		this.owningEntity = owningEntity;
	}

	public DEFINITION getDefinition() {
		return definition;
	}

	public IEntity<?> getOwningEntity() {
		return owningEntity;
	}
}
