package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

public class EntityBuilder {
	private AbstractInternalBuilder<?> internalBuilder;
	private ContentRetriever contentRetriever;

	public <T extends IEntityDefinition> void setEntityDefinition(T definition) throws ProcessingException {
		EntityDefinitionVisitor visitor = new EntityDefinitionVisitor();
		this.internalBuilder = definition.accept(visitor);
	}

	public ContentRetriever getContentRetriever() {
		return contentRetriever;
	}

	public void setContentRetriever(ContentRetriever contentRetriever) {
		if (this.contentRetriever != null) {
			throw new IllegalStateException("contentRetriever has already been set");
		}
		this.contentRetriever = contentRetriever;
	}

	public IMutableEntity<?> build() {
		// TODO: implement a lazy fetch method to get the parent
		IMutableEntity<?> parent = null;
		IMutableEntity<?> retval = internalBuilder.build(getContentRetriever(), parent);
		return retval;
	}

	private class EntityDefinitionVisitor implements IEntityDefinitionVisitor<AbstractInternalBuilder<?>> {

		public AbstractInternalBuilder<?> visit(IGeneratedDocumentDefinition definition)
				throws ProcessingException {
			return new GeneratedDocumentInternalBuilder(definition);
		}

		public AbstractInternalBuilder<?> visit(IKeyedDocumentDefinition definition)
				throws ProcessingException {
			return new KeyedDocumentInternalBuilder(definition);
		}

		public AbstractInternalBuilder<?> visit(IContentNodeDefinition definition)
				throws ProcessingException {
			return new ContentNodeInternalBuilder(definition);
		}
		
	}
}
