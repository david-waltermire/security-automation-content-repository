package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.AbstractEntity;
import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.DefaultVersion;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
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

/**
 * A builder to construct and entity
 * 
 * @author Adam Halbardier
 */
public class EntityBuilder {
    private ContentRetriever contentRetriever;
    private IKey key;
    private String versionValue;
    private IContainer<?> parent;
    private final List<IRelationship<?>> relationships =
        new LinkedList<IRelationship<?>>();
    private final Map<IPropertyDefinition, List<String>> properties =
        new HashMap<IPropertyDefinition, List<String>>();
    private IEntityDefinition definition;

    /**
     * set the entity definition
     * 
     * @param definition the entity definition
     */
    public void setEntityDefinition(IEntityDefinition definition) {
        this.definition = definition;
    }

    /**
     * set the content retriever
     * 
     * @param contentRetriever the content retriever
     */
    public void setContentRetriever(ContentRetriever contentRetriever) {
        if (this.contentRetriever != null) {
            throw new IllegalStateException(
                "contentRetriever has already been set");
        }
        this.contentRetriever = contentRetriever;
    }

    /**
     * set the key
     * 
     * @param key the key
     */
    public void setKey(IKey key) {
        if (this.key != null) {
            throw new IllegalStateException("key has already been set");
        }
        this.key = key;
    }

    /**
     * set the version value
     * 
     * @param version the version value
     */
    public void setVersionValue(String version) {
        if (this.versionValue != null) {
            throw new IllegalStateException(
                "version value has already been set");
        }
        this.versionValue = version;
    }

    /**
     * set the parent entity
     * 
     * @param parent the parent entity
     */
    public void setParent(IContainer<?> parent) {
        if (this.parent != null) {
            throw new IllegalStateException("parent has already been set");
        }
        this.parent = parent;
    }

    /**
     * add relationship to entity
     * 
     * @param relationship relationship to add
     */
    public void addRelationship(IRelationship<?> relationship) {
        this.relationships.add(relationship);
    }

    /**
     * add property to entity
     * 
     * @param definition definition of property
     * @param value value of property
     */
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

    /**
     * build the entity
     * 
     * @param <T> the result type
     * @return the built entity
     */
    public <T extends IEntity<?>> T build() {
        try {
            return this.definition.accept(new EntityDefinitionVisitor<T>());
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private class EntityDefinitionVisitor<T extends IEntity<?>> implements
            IEntityDefinitionVisitor<T> {

        public T visit(IGeneratedDocumentDefinition definition) {
            DefaultGeneratedDocument dgd =
                new DefaultGeneratedDocument(
                    definition,
                    contentRetriever,
                    parent);
            setCommonInfo(dgd);
            @SuppressWarnings("unchecked")
            T retVal = (T)dgd;
            return retVal;
        }

        public T visit(IKeyedDocumentDefinition definition) {
            DefaultKeyedDocument dkd =
                new DefaultKeyedDocument(
                    definition,
                    key,
                    contentRetriever,
                    parent);
            setCommonInfo(dkd);
            @SuppressWarnings("unchecked")
            T retVal = (T)dkd;
            return retVal;
        }

        public T visit(IContentNodeDefinition definition) {
            DefaultContentNode dcn =
                new DefaultContentNode(
                    definition,
                    key,
                    contentRetriever,
                    parent);
            setCommonInfo(dcn);
            @SuppressWarnings("unchecked")
            T retVal = (T)dcn;
            return retVal;
        }

        private void setCommonInfo(AbstractEntity<?> ime) {
            for (IRelationship<?> relationship : relationships) {
                ime.addRelationship(relationship);
            }
            for (Map.Entry<IPropertyDefinition, List<String>> entry : properties.entrySet()) {
                ime.addProperty(entry.getKey().getId(), entry.getValue());
            }
            if (definition.getVersionDefinition() != null) {
                ime.setVersion(new DefaultVersion(
                    definition.getVersionDefinition(),
                    versionValue));
            }
        }
    }
}
