package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

class GeneratedDocumentInternalBuilder extends AbstractInternalBuilder<IGeneratedDocumentDefinition> {

	public GeneratedDocumentInternalBuilder(IGeneratedDocumentDefinition entityDefintion) {
		super(entityDefintion);
	}

	@Override
	public IMutableEntity<IGeneratedDocumentDefinition> build(
			ContentRetriever contentRetriever,
			IMutableEntity<?> parent) {
		DefaultGeneratedDocument retval = new DefaultGeneratedDocument(getEntityDefinition(), contentRetriever, parent);
		return retval;
	}
}