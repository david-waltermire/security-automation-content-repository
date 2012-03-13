package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;

abstract class AbstractKeyedInternalBuilder<DEFINITION extends IKeyedEntityDefinition> extends
		AbstractInternalBuilder<DEFINITION> {
	private IKey key;

	public AbstractKeyedInternalBuilder(DEFINITION entityDefintion) {
		super(entityDefintion);
	}
	
	public IKey getKey() {
		return key;
	}

	public void setKey(IKey key) {
		if (this.key != null) {
			throw new IllegalStateException("key has already been set");
		}
		this.key = key;
	}

}
