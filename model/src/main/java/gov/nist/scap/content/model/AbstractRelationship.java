package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

public abstract class AbstractRelationship<DEFINITION extends IRelationshipDefinition> implements IRelationship<DEFINITION> {
	private final DEFINITION definition;

	public AbstractRelationship(DEFINITION definition) {
		this.definition = definition;
	}

	public DEFINITION getDefinition() {
		return definition;
	}
}
