/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 Paul Cichonski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package gov.nist.scap.content.semantic;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.content.semantic.entity.EntityProxy;
import gov.nist.scap.content.semantic.entity.KeyedEntityProxy;
import gov.nist.scap.content.semantic.translation.EntityMetadataMap;
import gov.nist.scap.content.semantic.translation.KeyTranslator;
import gov.nist.scap.content.semantic.translation.ToRDFEntityVisitor;
import info.aduna.iteration.Iterations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.MetadataStore;

/**
 * At this point this is just going to be a facade into the triple store REST
 * interfaces
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore,
        IPersistenceContext {
    private static final Logger log =
        Logger.getLogger(TripleStoreFacadeMetaDataManager.class);
    private static final String BASE_URI =
        "http://scap.nist.gov/resource/content/individuals#";

    private Repository repository;

    private ValueFactory factory;

    private MetaDataOntology ontology;

    private TripleStoreQueryService queryService;

    private boolean modelLoaded = false;

    private ContentRetrieverFactory contentRetrieverFactory;

    private TripleStoreFacadeMetaDataManager(
            ContentRetrieverFactory contentRetrieverFactory) {
        // NOTE: this type is non-inferencing, see
        // http://www.openrdf.org/doc/sesame2/2.3.2/users/ch08.html for more
        // detail
        try {
            repository = new SailRepository(new MemoryStore());

            // repository = new
            // HTTPRepository("http://localhost:8080/openrdf-sesame",
            // "scapCmsTest");

            repository.initialize();
            factory = repository.getValueFactory();
            ontology = new MetaDataOntology(factory);
            queryService = new TripleStoreQueryService(this);
            this.contentRetrieverFactory = contentRetrieverFactory;
        } catch (RepositoryException e) {
            log.error("Exception iniitalizing triple store", e);
        }

    }

    /**
     * get an instance of this manager
     * 
     * @param contentRetrieverFactory a factory to create content retrievers
     *            (relative to the persistence store)
     * @return this manager
     */
    public static TripleStoreFacadeMetaDataManager getInstance(
            ContentRetrieverFactory contentRetrieverFactory) {
        return new TripleStoreFacadeMetaDataManager(contentRetrieverFactory);
    }

    /**
     * must be called before using the manager
     * 
     * @param model the metadata model to load into the ontology
     * @throws RepositoryException error whiling accessing the repository
     */
    public void loadModel(IMetadataModel model) throws RepositoryException {
        // TODO consider moving this to the constructor
        if (!modelLoaded) {
            ontology.loadModel(repository.getConnection(), model);
            modelLoaded = true;
        } else {
            throw new IllegalStateException(
                "The model has already been loaded.");
        }
    }

    @Override
    public IKeyedEntity<?> getEntity(IKey key) throws ProcessingException {
        try {
            RepositoryConnection conn = repository.getConnection();
            try {
                return new KeyedEntityProxy<IKeyedEntityDefinition, IKeyedEntity<IKeyedEntityDefinition>>(
                    this,
                    key);
            } finally {
                conn.close();
            }

        } catch (RepositoryException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public IEntity<?> getEntity(String contentId) {
        try {
            RepositoryConnection conn = repository.getConnection();
            try {
                URI entityURI =
                    queryService.findEntityURIbyContentId(contentId);
                if (entityURI != null) {
                    return new EntityProxy<IKeyedEntityDefinition, IKeyedEntity<IKeyedEntityDefinition>>(
                        this,
                        entityURI);
                }
            } finally {
                conn.close();
            }

        } catch (RepositoryException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(
            IExternalIdentifier externalIdentifier,
            Collection<String> boundaryObjectIds,
            Set<? extends IEntityDefinition> entityTypes) {
        try {
            RepositoryConnection conn = repository.getConnection();
            try {
                Map<String, List<URI>> entityURIs =
                    queryService.findEntityUrisFromBoundaryObjectIds(
                        externalIdentifier,
                        boundaryObjectIds,
                        entityTypes,
                        conn);
                return findEntityKeys(entityURIs, conn);

            } catch (MalformedQueryException e) {
                log.error(e);
                throw new RuntimeException(e);
            } catch (QueryEvaluationException e2) {
                log.error(e2);
                throw new RuntimeException(e2);
            } finally {
                conn.close();
            }

        } catch (RepositoryException e) {
            log.error(e);
        }
        return null;
    }

    /*
     * TODO: NOTE: there is an issue in some cases where an Entity will be sent
     * in containing duplicate relationships. At present only the first of the
     * set of duplicates is added; from the context of the triple store this is
     * the only useful behavior. The duplicate relationships make sense within
     * the context of a larger XML document, but the abstraction of the
     * metamodel removes that context, and therefore removes the usefulness of
     * the duplicate relationships. An example of this is the
     * "urn:scap-content:relationship:org.mitre.oval:criterion" Keyed
     * relationship in an Oval definition. An oval def. may have multiple
     * criterion relationships that are all equal (i.e. reference same test)
     * except for the fact that they appear under distinct criteria operators;
     * since the metamodel does not capture this extra context the relationships
     * just appear to be exactly the same. WE NEED TO FIGURE OUT HOW TO HANDLE
     * THIS.
     */
    @Override
    public void persist(Map<String, IEntity<?>> contentIdToEntityMap) {
        try {
            RepositoryConnection conn = repository.getConnection();
            try {

                IEntityVisitor entityVisitor =
                    new ToRDFEntityVisitor(
                        factory,
                        ontology,
                        new DefaultURIToEntityMap(
                            factory,
                            BASE_URI,
                            contentIdToEntityMap), conn);
                for (Map.Entry<String, IEntity<?>> entry : contentIdToEntityMap.entrySet()) {
                    entry.getValue().accept(entityVisitor);
                }

            } finally {
                conn.close();
            }
        } catch (RepositoryException e) {
            log.error(e);
        }
    }

    private Map<String, Set<? extends IKey>> findEntityKeys(
            Map<String, List<URI>> entityURIs,
            RepositoryConnection conn)
            throws RepositoryException, QueryEvaluationException,
            MalformedQueryException {
        KeyTranslator keyTranslator = new KeyTranslator(ontology);
        Map<String, Set<? extends IKey>> boundaryIdToKeyMap =
            new HashMap<String, Set<? extends IKey>>();
        for (Map.Entry<String, List<URI>> entry : entityURIs.entrySet()) {
            Set<IKey> map = new HashSet<IKey>();
            boundaryIdToKeyMap.put(entry.getKey(), map);
            for (URI entityURI : entry.getValue()) {
                Set<Statement> entityStatements =
                    getEntityStatements(entityURI, conn);
                IKey entityKey =
                    keyTranslator.translateToJava(entityStatements);
                map.add(entityKey);
            }
        }
        return boundaryIdToKeyMap;
    }

    private Set<Statement> getEntityStatements(
            URI entityURI,
            RepositoryConnection conn)
            throws RepositoryException, QueryEvaluationException,
            MalformedQueryException {
        Resource entityContextURI = queryService.findEntityContext(entityURI);
        // no need to run inferencing here
        return Iterations.addAll(
            conn.getStatements(null, null, null, false, entityContextURI),
            new HashSet<Statement>());
    }

    @Override
    public void shutdown() {
        try {
            repository.getConnection().close();
        } catch (RepositoryException e) {
            log.error(e);
        }
    }

    private class DefaultURIToEntityMap implements EntityMetadataMap {

        private final Map<IEntity<?>, String> entityToContentIdMap =
            new HashMap<IEntity<?>, String>();
        private final String baseURI;

        public DefaultURIToEntityMap(
                ValueFactory factory,
                String baseURI,
                Map<String, IEntity<?>> contentIdToEntityMap) {
            for (Map.Entry<String, IEntity<?>> entry : contentIdToEntityMap.entrySet()) {
                entityToContentIdMap.put(entry.getValue(), entry.getKey());
            }
            this.baseURI = baseURI;
        }

        @Override
        public URI getResourceURI(IEntity<?> entity) {
            return generateResourceId(entityToContentIdMap.get(entity));
        }

        @Override
        public String getContentId(IEntity<?> entity) {
            return entityToContentIdMap.get(entity);
        }

        private URI generateResourceId(String contentId) {
            return factory.createURI(baseURI + contentId);
        }

    }

    @Override
    public ContentRetrieverFactory getContentRetrieverFactory() {
        return contentRetrieverFactory;
    }

    @Override
    public MetaDataOntology getOntology() {
        return ontology;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

}
