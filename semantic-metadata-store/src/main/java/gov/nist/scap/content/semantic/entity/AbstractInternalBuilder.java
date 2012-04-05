package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

abstract class AbstractInternalBuilder<DEFINITION extends IEntityDefinition> implements IInternalBuilder {
	private final DEFINITION entityDefinition;

	public AbstractInternalBuilder(DEFINITION entityDefintion) {
		this.entityDefinition = entityDefintion;
	}

	public DEFINITION getEntityDefinition() {
		return entityDefinition;
	}
}