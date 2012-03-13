package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IEntityDefinition;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

abstract class AbstractInternalBuilder<DEFINITION extends IEntityDefinition> implements IInternalBuilder {
	private final DEFINITION entityDefinition;

	public AbstractInternalBuilder(DEFINITION entityDefintion) {
		this.entityDefinition = entityDefintion;
	}

	public abstract IMutableEntity<DEFINITION> build(
			ContentRetriever contentRetriever,
			IMutableEntity<?> parent);

	public DEFINITION getEntityDefinition() {
		return entityDefinition;
	}
}