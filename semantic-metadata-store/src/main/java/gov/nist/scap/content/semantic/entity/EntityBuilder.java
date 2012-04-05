package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.DefaultVersion;
import gov.nist.scap.content.model.IContainer;
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
	private ContentRetriever contentRetriever;
	private IKey key;
	private String versionValue;
	private IContainer<?> parent;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();
	private final Map<IPropertyDefinition, List<String>> properties = new HashMap<IPropertyDefinition, List<String>>();
	private IEntityDefinition definition;

	public void setEntityDefinition(IEntityDefinition definition) throws ProcessingException {
		this.definition = definition;
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
	
	public void setVersionValue(String version) {
	    this.versionValue = version;
	}

	public IEntity<?> getParent() {
		return parent;
	}

	public void setParent(IContainer<?> parent) {
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

	public IEntity<?> build() {
	    try {
            return this.definition.accept(new EntityDefinitionVisitor());
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
	}

	private class EntityDefinitionVisitor implements IEntityDefinitionVisitor<IEntity<?>> {

		public IEntity<?> visit(IGeneratedDocumentDefinition definition) {
		    DefaultGeneratedDocument dgd = new DefaultGeneratedDocument(definition, contentRetriever, parent);
		    setCommonInfo(dgd);
		    return dgd;
		}

		public IEntity<?> visit(IKeyedDocumentDefinition definition) {
		    DefaultKeyedDocument dkd = new DefaultKeyedDocument(definition, key, contentRetriever, parent);
		    setCommonInfo(dkd);
		    return dkd;
		}

		public IEntity<?> visit(IContentNodeDefinition definition) {
			DefaultContentNode dcn = new DefaultContentNode(definition, key, contentRetriever, parent);
			setCommonInfo(dcn);
			return dcn;
		}
		
		private void setCommonInfo(IMutableEntity<?> ime) {
	        for (IRelationship<?> relationship : relationships) {
	            ime.addRelationship(relationship);
	        }
	        for (Map.Entry<IPropertyDefinition, List<String>> entry : properties.entrySet()) {
	            ime.addProperty(entry.getKey().getId(), entry.getValue());
	        }
	        if( definition.getVersionDefinition() != null ) {
	            ime.setVersion(new DefaultVersion(definition.getVersionDefinition(), versionValue));
	        }
		}
	}
}
