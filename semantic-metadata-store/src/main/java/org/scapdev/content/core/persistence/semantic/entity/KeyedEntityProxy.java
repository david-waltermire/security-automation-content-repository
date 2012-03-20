package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;

import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.IPersistenceContext;

public class KeyedEntityProxy<T extends IKeyedEntityDefinition, ENTITY extends IKeyedEntity<T>> extends EntityProxy<T, ENTITY> implements IKeyedEntity<T> {

    public KeyedEntityProxy(
            String baseURI,
            IPersistenceContext persistContext,
            String contentId) {
        super(baseURI, persistContext, contentId);
    }

    public KeyedEntityProxy(
            String baseURI,
            IPersistenceContext persistContext,
            URI resourceId) {
        super(baseURI, persistContext, resourceId);
    }
    
    
    @Override
    public IKey getKey() {
        loadEntity();
        return entity.getKey();
    }
    
}
