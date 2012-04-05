package org.scapdev.content.core.persistence.semantic.entity;

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
import gov.nist.scap.content.model.definitions.ProcessingException;
import info.aduna.iteration.Iterations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.scapdev.content.core.persistence.semantic.IPersistenceContext;
import org.scapdev.content.core.persistence.semantic.TripleStoreQueryService;
import org.scapdev.content.core.persistence.semantic.translation.EntityTranslator;
import org.scapdev.content.core.persistence.semantic.translation.KeyTranslator;

public class EntityProxy<T extends IEntityDefinition, ENTITY extends IEntity<T>> implements IEntity<T> {

    protected ENTITY entity;
    private String contentId;
    private URI resourceId;
    private String baseURI;
    private IPersistenceContext persistContext;
    private TripleStoreQueryService queryService;

    private EntityTranslator entityTranslator;

    private EntityProxy(
            String baseURI,
            IPersistenceContext persistContext) throws RepositoryException {
        queryService = new TripleStoreQueryService(persistContext);
        entityTranslator =
            new EntityTranslator(baseURI, persistContext);
        this.baseURI = baseURI;
        this.persistContext = persistContext;
    }

    public EntityProxy(
            String baseURI,
            IPersistenceContext persistenceContext,
            String contentId) throws RepositoryException {
        this(baseURI, persistenceContext);
        this.contentId = contentId;
    }

    public EntityProxy(
            String baseURI,
            IPersistenceContext persistenceContext,
            URI resourceId) throws RepositoryException {
        this(baseURI, persistenceContext);
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

    protected void loadEntity() {
        if (entity == null) {

            if (contentId != null) {

            }

            RepositoryConnection conn;
            try {
                conn = persistContext.getRepository().getConnection();
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
                if( entity == null ) {
                    throw new NullPointerException("EntityProxy could not load the entity " + resourceId != null ? resourceId.toString() : contentId);
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            } catch (ProcessingException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Uses context to get all triples associated with entityURI
     * 
     * @param entityUri
     * @return
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    private Set<Statement> getEntityStatements(
            URI entityURI,
            RepositoryConnection conn)
            throws RepositoryException {
        Resource entityContextURI =
            queryService.findEntityContext(entityURI);
        // no need to run inferencing here
        Set<Statement> result = Iterations.addAll(
            conn.getStatements(null, null, null, false, entityContextURI),
            new HashSet<Statement>()); 
        return result;
    }

    /**
     * Helper method to generate keys for all entities
     * 
     * @param entityURIs
     * @param conn
     * @return
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     * @throws RepositoryException
     */
    private Map<URI, IKey> findEntityKeys(
            List<URI> entityURIs,
            RepositoryConnection conn)
            throws RepositoryException {
        KeyTranslator keyTranslator =
            new KeyTranslator(baseURI, persistContext.getOntology(), persistContext.getRepository().getValueFactory());
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
