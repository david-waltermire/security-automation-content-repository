package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

class KeyedDocumentInternalBuilder extends AbstractKeyedInternalBuilder<IKeyedDocumentDefinition> {

	public KeyedDocumentInternalBuilder(IKeyedDocumentDefinition entityDefintion) {
		super(entityDefintion);
	}

	@Override
	public IMutableEntity<IKeyedDocumentDefinition> build(
			ContentRetriever contentRetriever, IMutableEntity<?> parent) {
		DefaultKeyedDocument retval = new DefaultKeyedDocument(getEntityDefinition(), getKey(), contentRetriever, parent);
		return retval;
	}
	
}