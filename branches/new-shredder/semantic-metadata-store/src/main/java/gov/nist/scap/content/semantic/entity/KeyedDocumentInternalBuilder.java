package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

class KeyedDocumentInternalBuilder extends AbstractInternalBuilder<IKeyedDocumentDefinition> {

	public KeyedDocumentInternalBuilder(IKeyedDocumentDefinition entityDefintion) {
		super(entityDefintion);
	}

	@Override
	public IMutableEntity<IKeyedDocumentDefinition> build(
			IKey key,
			ContentRetriever contentRetriever,
			IContainer<?> parent) {
		DefaultKeyedDocument retval = new DefaultKeyedDocument(getEntityDefinition(), key, contentRetriever, parent);
		return retval;
	}
	
}