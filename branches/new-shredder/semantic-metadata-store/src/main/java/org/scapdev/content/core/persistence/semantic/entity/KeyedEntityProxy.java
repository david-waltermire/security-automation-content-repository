package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;

import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.IPersistenceContext;

public class KeyedEntityProxy<T extends IKeyedEntityDefinition, ENTITY extends IKeyedEntity<T>> extends EntityProxy<T, ENTITY> implements IKeyedEntity<T>, IContainer<T> {

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
    
    @Override
    public IKey getKey(String keyId) {
        IKey retval = null;
        if (getKey().getId().equals(keyId)) {
            retval = getKey();
        } else if ( getParent() != null ) {
            retval = getParent().getKey(keyId);
        }
        return retval;
    }
    
}
