package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * A variant of EntityProxy that supports keys
 * @author Adam Halbardier
 *
 * @param <T> the entity defintion
 * @param <ENTITY> the keyed entity type
 */
public class KeyedEntityProxy<T extends IKeyedEntityDefinition, ENTITY extends IKeyedEntity<T>> extends EntityProxy<T, ENTITY> implements IKeyedEntity<T>, IContainer<T> {

    private IKey key;
    /**
     * constructor specifying a contentId
     * @param persistContext the persistence context
     * @param contentId the contentId of the entity
     * @throws RepositoryException error accessing repository
     */
    public KeyedEntityProxy(
            IPersistenceContext persistContext,
            String contentId) throws RepositoryException {
        super(persistContext, contentId);
    }

    /**
     * constructor specifying a resource ID
     * @param persistContext the persistence context
     * @param resourceId the resourceId of the entity
     * @throws RepositoryException error accessing repository
     */
    public KeyedEntityProxy(
            IPersistenceContext persistContext,
            URI resourceId) throws RepositoryException {
        super(persistContext, resourceId);
    }
    
    /**
     * constructor specifying a key
     * @param persistContext the persistence context
     * @param key the key of the entity
     * @throws RepositoryException error accessing repository
     */
    public KeyedEntityProxy(
            IPersistenceContext persistContext,
            IKey key) throws RepositoryException {
        super(persistContext);
        this.key = key;
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
    
    @Override
    protected void loadEntity() {
        if (entity == null) {
            if( key != null ) {
                try {
                    resourceId = queryService.findEntityURI(key);
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
            super.loadEntity();
        }

    }
    
}
