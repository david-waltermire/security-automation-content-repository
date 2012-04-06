package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.SoftHashMap;
import gov.nist.scap.content.semantic.TripleStoreQueryService;
import gov.nist.scap.content.semantic.exceptions.NonUniqueResultException;
import gov.nist.scap.content.semantic.translation.EntityTranslator;
import gov.nist.scap.content.semantic.translation.KeyTranslator;
import info.aduna.iteration.Iterations;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class EntityProxy<T extends IEntityDefinition, ENTITY extends IEntity<T>>
        implements IEntity<T> {

    protected ENTITY entity;
    private String contentId;
    protected URI resourceId;
    protected IPersistenceContext persistContext;
    protected TripleStoreQueryService queryService;

    private EntityTranslator entityTranslator;
    
    //Set the minimum size of the cache in the constructor
    private static Map<URI, SoftReference<IEntity<?>>> inMemWeakCache = new SoftHashMap<URI, SoftReference<IEntity<?>>>(100);

    /**
     * The base constructor to be call from other constructors
     * @param persistContext the persistence context
     * @throws RepositoryException error accessing the repository
     */
    protected EntityProxy(IPersistenceContext persistContext)
            throws RepositoryException {
        queryService = new TripleStoreQueryService(persistContext);
        entityTranslator = new EntityTranslator(persistContext);
        this.persistContext = persistContext;
    }

    /**
     * use this constructor to make an entity based on the content id
     * @param persistenceContext the persistence context
     * @param contentId the content id of the entity to construct
     * @throws RepositoryException error accessing the repository
     */
    public EntityProxy(
            IPersistenceContext persistenceContext,
            String contentId) throws RepositoryException {
        this(persistenceContext);
        this.contentId = contentId;
    }

    /**
     * use this constructor to make an entity based on the resource URI
     * @param persistenceContext the persistence context
     * @param resourceId the resource URI of the entity to construct
     * @throws RepositoryException error accessing the repository
     */
    public EntityProxy(
            IPersistenceContext persistenceContext,
            URI resourceId) throws RepositoryException {
        this(persistenceContext);
        this.resourceId = resourceId;
    }

    @Override
    public T getDefinition() {
        loadEntity();
        return entity.getDefinition();
    }

    @Override
    public IContainer<?> getParent() {
        loadEntity();
        return entity.getParent();
    }

    @Override
    public Collection<? extends IRelationship<?>> getRelationships() {
        loadEntity();
        return entity.getRelationships();
    }

    @Override
    public Collection<? extends IKeyedRelationship> getKeyedRelationships() {
        loadEntity();
        return entity.getKeyedRelationships();
    }

    @Override
    public Collection<? extends IBoundaryIdentifierRelationship> getBoundaryIdentifierRelationships() {
        loadEntity();
        return entity.getBoundaryIdentifierRelationships();
    }

    @Override
    public Collection<? extends ICompositeRelationship> getCompositeRelationships() {
        loadEntity();
        return entity.getCompositeRelationships();
    }

    @Override
    public IContentHandle getContentHandle() {
        loadEntity();
        return entity.getContentHandle();
    }

    @Override
    public void accept(IEntityVisitor visitor) {
        loadEntity();
        entity.accept(visitor);
    }

    @Override
    public IVersion getVersion() {
        loadEntity();
        return entity.getVersion();
    }

    @Override
    public Map<String, ? extends Set<String>> getProperties() {
        loadEntity();
        return entity.getProperties();
    }

    @Override
    public List<String> getPropertyById(String id) {
        loadEntity();
        return entity.getPropertyById(id);
    }

    /**
     * call this method to load an entity from the triple store
     */
    protected void loadEntity() {
        if (entity == null) {

            RepositoryConnection conn;
            try {
                conn = persistContext.getRepository().getConnection();
                if( resourceId == null && contentId != null ) {
                    loadResourceIdFromContentId(contentId, conn);
                }

                // at this point, resourceId should be loaded...check if the entity already exists in cache
                SoftReference<IEntity<?>> sr = inMemWeakCache.get(resourceId);
                if( sr != null ) {
                    @SuppressWarnings("unchecked")
                    ENTITY e = (ENTITY)sr.get();
                    if( e != null ) {
                        entity = e;
                        return;
                    }
                }

                Set<Statement> entityStatements =
                    getEntityStatements(resourceId, conn);
                Map<URI, IKey> relatedEntityKeys =
                    findEntityKeys(
                        queryService.findAllRelatedEntityURIs(resourceId),
                        conn);
                entity =
                    entityTranslator.translateToJava(
                        entityStatements,
                        relatedEntityKeys,
                        persistContext.getContentRetrieverFactory());

                if (entity == null) {
                    throw new NullPointerException(
                        "EntityProxy could not load the entity " + contentId != null
                            ? contentId : resourceId.stringValue());
                }
                inMemWeakCache.put(resourceId, new SoftReference<IEntity<?>>(entity));
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void loadResourceIdFromContentId(String contentId, RepositoryConnection conn) throws RepositoryException {
            Set<Statement> result =
                    Iterations.addAll(
                        conn.getStatements(null, persistContext.getOntology().HAS_CONTENT_ID.URI, persistContext.getRepository().getValueFactory().createLiteral(contentId), false),
                        new HashSet<Statement>());
            if( result.size() == 0 ) {
                throw new NullPointerException(
                    "EntityProxy could not load the entity URI from " + contentId);
            } else if ( result.size() > 1 ) {
                throw new NonUniqueResultException();
            }
            resourceId = (URI)((Statement)result.toArray()[0]).getSubject();
        
    }

    private Set<Statement> getEntityStatements(
            URI entityURI,
            RepositoryConnection conn) throws RepositoryException {
        Resource entityContextURI = queryService.findEntityContext(entityURI);
        // no need to run inferencing here
        Set<Statement> result =
            Iterations.addAll(
                conn.getStatements(null, null, null, false, entityContextURI),
                new HashSet<Statement>());
        return result;
    }

    private Map<URI, IKey> findEntityKeys(
            List<URI> entityURIs,
            RepositoryConnection conn) throws RepositoryException {
        KeyTranslator keyTranslator =
            new KeyTranslator(
                persistContext.getOntology());
        Map<URI, IKey> entityURIToKeyMap = new HashMap<URI, IKey>();
        for (URI entityURI : entityURIs) {
            Set<Statement> entityStatements =
                getEntityStatements(entityURI, conn);
            IKey entityKey = keyTranslator.translateToJava(entityStatements);
            entityURIToKeyMap.put(entityURI, entityKey);
        }
        return entityURIToKeyMap;
    }

}
