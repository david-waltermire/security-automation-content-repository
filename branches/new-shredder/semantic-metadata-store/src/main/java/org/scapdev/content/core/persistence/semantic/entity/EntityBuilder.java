package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IPropertyDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

public class EntityBuilder {
	private static final EntityDefinitionVisitor visitor = new EntityDefinitionVisitor();
	private IInternalBuilder internalBuilder;
	private ContentRetriever contentRetriever;
	private IKey key;
	private IMutableEntity<?> parent;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();
	private final Map<IPropertyDefinition, List<String>> properties = new HashMap<IPropertyDefinition, List<String>>();

	public <T extends IEntityDefinition> void setEntityDefinition(T definition) throws ProcessingException {
		internalBuilder = definition.accept(visitor);
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

	public IKey getKey() {
		return key;
	}

	public void setKey(IKey key) {
		if (this.key != null) {
			throw new IllegalStateException("key has already been set");
		}
		this.key = key;
	}

	public IEntity<?> getParent() {
		return parent;
	}

	public void setParent(IMutableEntity<?> parent) {
		if (this.parent != null) {
			throw new IllegalStateException("parent has already been set");
		}
		this.parent = parent;
	}

	public void addRelationship(IRelationship<?> relationship) {
		this.relationships.add(relationship);
	}

	public void addProperty(IPropertyDefinition definition, String value) {
		if (definition == null) {
			throw new NullPointerException("definition");
		}
		if (value == null) {
			throw new NullPointerException("value");
		}
		List<String> result = properties.get(definition);
		if (result == null) {
			result = new LinkedList<String>();
			properties.put(definition, result);
		}
		result.add(value);
	}

	public IMutableEntity<?> build() {
		IMutableEntity<?> retval = internalBuilder.build(key, getContentRetriever(), parent);
		for (IRelationship<?> relationship : relationships) {
			retval.addRelationship(relationship);
		}
		for (Map.Entry<IPropertyDefinition, List<String>> entry : properties.entrySet()) {
			retval.addProperty(entry.getKey().getId(), entry.getValue());
		}
		return retval;
	}

	private static class EntityDefinitionVisitor implements IEntityDefinitionVisitor<AbstractInternalBuilder<?>> {

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
