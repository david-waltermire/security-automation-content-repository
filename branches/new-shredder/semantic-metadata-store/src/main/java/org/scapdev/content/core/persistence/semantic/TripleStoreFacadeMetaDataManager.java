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
package org.scapdev.content.core.persistence.semantic;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import info.aduna.iteration.Iteration;
import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.MetadataStore;
import org.scapdev.content.core.persistence.semantic.translation.EntityMetadataMap;
import org.scapdev.content.core.persistence.semantic.translation.EntityTranslator;
import org.scapdev.content.core.persistence.semantic.translation.KeyTranslator;
import org.scapdev.content.core.persistence.semantic.translation.ToRDFEntityVisitor;

/**
 * At this point this is just going to be a facade into the triple store REST
 * interfaces
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore {
    private static final Logger log =
        Logger.getLogger(TripleStoreFacadeMetaDataManager.class);
    private static final String BASE_URI =
        "http://scap.nist.gov/resource/content/individuals#";

    private Repository repository;

    private ValueFactory factory;

    private MetaDataOntology ontology;

    private TripleStoreQueryService queryService;

    EntityTranslator entityTranslator;

    private boolean modelLoaded = false;

    private TripleStoreFacadeMetaDataManager() {
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
            queryService = new TripleStoreQueryService(repository, ontology);
            entityTranslator =
                new EntityTranslator(BASE_URI, ontology, factory);
        } catch (RepositoryException e) {
            log.error("Exception iniitalizing triple store", e);
        }

    }
    
    public static TripleStoreFacadeMetaDataManager getInstance() {
        return new TripleStoreFacadeMetaDataManager();
    }
    
    public void loadModel(IMetadataModel model) throws RepositoryException {
        if (!modelLoaded) {
            ontology.loadModel(repository.getConnection(), model);
            modelLoaded = true;
        } else {
            throw new IllegalStateException("The model has already been loaded.");
        }
    }

    @Override
    public IKeyedEntity<?> getEntity(
            IKey key,
            ContentRetrieverFactory contentRetrieverFactory) throws ProcessingException {
        try {
            RepositoryConnection conn = repository.getConnection();
            try {
                URI entityURI = queryService.findEntityURI(key, conn);
                if (entityURI != null) {
                    Set<Statement> entityStatements =
                        getEntityStatements(entityURI, conn);
                    // need to find the entityKeys from the
                    // KeyedRelationship...these are not included in
                    // owningEntityContext on persist
                    Map<URI, IKey> relatedEntityKeys =
                        findEntityKeys(queryService.findAllRelatedEntityURIs(
                            entityURI,
                            conn), conn);
                    return entityTranslator.translateToJava(
                        entityStatements,
                        relatedEntityKeys,
                        contentRetrieverFactory);
                }
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

    @Override
    public IEntity<?> getEntity(
            String contentId,
            ContentRetrieverFactory contentRetrieverFactory) {
        return null;
    }

    @Override
    public Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(IExternalIdentifier externalIdentifier, Collection<String> boundaryObjectIds, Set<? extends IEntityDefinition> entityTypes) {
//        try {
//            RepositoryConnection conn = repository.getConnection();
//            try {
//                List<URI> entityURIs =
//                    queryService.findEntityUrisFromBoundaryObjectIds(
//                        indirectType,
//                        indirectIds,
//                        entityType,
//                        conn);
//                return new HashSet<IKey>(
//                    findEntityKeys(entityURIs, conn).values());
//
//            } catch (MalformedQueryException e) {
//                log.error(e);
//                throw new RuntimeException(e);
//            } catch (QueryEvaluationException e2) {
//                log.error(e2);
//                throw new RuntimeException(e2);
//            } finally {
//                conn.close();
//            }
//
//        } catch (RepositoryException e) {
//            log.error(e);
//        }
        return null;
    }

    /**
     * <p>
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
     * </p>
     */
    @Override
    public void persist(
            Map<String, IEntity<?>> contentIdToEntityMap) {
        try {
            //TODO change this back
            RepositoryConnection conn = repository.getConnection();
            //RepositoryConnection conn = new RepositoryConnectionTest();
            try {

                IEntityVisitor entityVisitor = new ToRDFEntityVisitor(factory, ontology, new DefaultURIToEntityMap(factory, BASE_URI, contentIdToEntityMap), conn);
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
            throws RepositoryException, QueryEvaluationException,
            MalformedQueryException {
        KeyTranslator keyTranslator =
            new KeyTranslator(BASE_URI, ontology, factory);
        Map<URI, IKey> entityURIToKeyMap = new HashMap<URI, IKey>();
        for (URI entityURI : entityURIs) {
            Set<Statement> entityStatements =
                getEntityStatements(entityURI, conn);
            // TODO: may want to refactor to only pass translator key
            // statements, but this will work
            IKey entityKey = keyTranslator.translateToJava(entityStatements);
            entityURIToKeyMap.put(entityURI, entityKey);
        }
        return entityURIToKeyMap;
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
            throws RepositoryException, QueryEvaluationException,
            MalformedQueryException {
        Resource entityContextURI =
            queryService.findEntityContext(entityURI, conn);
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
        
        private final Map<IEntity<?>, String> entityToContentIdMap = new HashMap<IEntity<?>, String>();
        private final String baseURI;
        
        
        public DefaultURIToEntityMap(ValueFactory factory, String baseURI, Map<String, IEntity<?>> contentIdToEntityMap) {
             for( Map.Entry<String, IEntity<?>> entry : contentIdToEntityMap.entrySet() ) {
                 entityToContentIdMap.put(entry.getValue(),entry.getKey());
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
    
//    //TODO remove this...it's for testing only
    private class RepositoryConnectionTest implements RepositoryConnection {

        OutputStream os;
        
        public RepositoryConnectionTest() {
            try {
                os = new FileOutputStream("triples out.txt");
            } catch (FileNotFoundException e) {
                // TODO: log exception
                e.printStackTrace();
            }
            
        }
        @Override
        public Repository getRepository() {
            return null;
        }

        @Override
        public ValueFactory getValueFactory() {
            return null;
        }

        @Override
        public boolean isOpen() throws RepositoryException {
            return false;
        }

        @Override
        public void close() throws RepositoryException {
        }

        @Override
        public Query prepareQuery(QueryLanguage ql, String query)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public Query prepareQuery(QueryLanguage ql, String query, String baseURI)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public TupleQuery prepareTupleQuery(QueryLanguage ql, String query)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public TupleQuery prepareTupleQuery(
                QueryLanguage ql,
                String query,
                String baseURI)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public GraphQuery prepareGraphQuery(QueryLanguage ql, String query)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public GraphQuery prepareGraphQuery(
                QueryLanguage ql,
                String query,
                String baseURI)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public BooleanQuery prepareBooleanQuery(
                QueryLanguage ql,
                String query,
                String baseURI)
                throws RepositoryException, MalformedQueryException {
            return null;
        }

        @Override
        public RepositoryResult<Resource> getContextIDs()
                throws RepositoryException {
            return null;
        }

        @Override
        public RepositoryResult<Statement> getStatements(
                Resource subj,
                URI pred,
                Value obj,
                boolean includeInferred,
                Resource... contexts) throws RepositoryException {
            return null;
        }

        @Override
        public boolean hasStatement(
                Resource subj,
                URI pred,
                Value obj,
                boolean includeInferred,
                Resource... contexts) throws RepositoryException {
            return false;
        }

        @Override
        public boolean hasStatement(
                Statement st,
                boolean includeInferred,
                Resource... contexts) throws RepositoryException {
            return false;
        }

        @Override
        public void exportStatements(
                Resource subj,
                URI pred,
                Value obj,
                boolean includeInferred,
                RDFHandler handler,
                Resource... contexts)
                throws RepositoryException, RDFHandlerException {
        }

        @Override
        public void export(RDFHandler handler, Resource... contexts)
                throws RepositoryException, RDFHandlerException {
        }

        @Override
        public long size(Resource... contexts) throws RepositoryException {
            return 0;
        }

        @Override
        public boolean isEmpty() throws RepositoryException {
            return false;
        }

        @Override
        public void setAutoCommit(boolean autoCommit)
                throws RepositoryException {
        }

        @Override
        public boolean isAutoCommit() throws RepositoryException {
            return false;
        }

        @Override
        public void commit() throws RepositoryException {
            
        }

        @Override
        public void rollback() throws RepositoryException {
        }

        @Override
        public void add(
                InputStream in,
                String baseURI,
                RDFFormat dataFormat,
                Resource... contexts)
                throws IOException, RDFParseException, RepositoryException {
        }

        @Override
        public void add(
                Reader reader,
                String baseURI,
                RDFFormat dataFormat,
                Resource... contexts)
                throws IOException, RDFParseException, RepositoryException {
        }

        @Override
        public void add(
                URL url,
                String baseURI,
                RDFFormat dataFormat,
                Resource... contexts)
                throws IOException, RDFParseException, RepositoryException {
        }

        @Override
        public void add(
                File file,
                String baseURI,
                RDFFormat dataFormat,
                Resource... contexts)
                throws IOException, RDFParseException, RepositoryException {
        }

        @Override
        public void add(
                Resource subject,
                URI predicate,
                Value object,
                Resource... contexts) throws RepositoryException {
            try {
                os.write((subject.stringValue() + " " + predicate.stringValue() + " " + object.stringValue() + "\n").getBytes());
            } catch (IOException e) {
                // TODO: log exception
                e.printStackTrace();
            }
            
        }

        @Override
        public void add(Statement st, Resource... contexts)
                throws RepositoryException {
            try {
                os.write((st.getSubject().stringValue() + " " + st.getPredicate().stringValue() + " " + st.getObject().stringValue() + "\n").getBytes());
            } catch (IOException e) {
                // TODO: log exception
                e.printStackTrace();
            }
            
        }

        @Override
        public void add(
                Iterable<? extends Statement> statements,
                Resource... contexts) throws RepositoryException {
            for( Statement st : statements ) {
                try {
                    os.write((st.getSubject().stringValue() + " " + st.getPredicate().stringValue() + " " + st.getObject().stringValue() + "\n").getBytes());
                } catch (IOException e) {
                    // TODO: log exception
                    e.printStackTrace();
                }
            }
        }

        @Override
        public <E extends Exception> void add(
                Iteration<? extends Statement, E> statements,
                Resource... contexts) throws RepositoryException, E {
        }

        @Override
        public void remove(
                Resource subject,
                URI predicate,
                Value object,
                Resource... contexts) throws RepositoryException {
        }

        @Override
        public void remove(Statement st, Resource... contexts)
                throws RepositoryException {
        }

        @Override
        public void remove(
                Iterable<? extends Statement> statements,
                Resource... contexts) throws RepositoryException {
        }

        @Override
        public <E extends Exception> void remove(
                Iteration<? extends Statement, E> statements,
                Resource... contexts) throws RepositoryException, E {
        }

        @Override
        public void clear(Resource... contexts) throws RepositoryException {
        }

        @Override
        public RepositoryResult<Namespace> getNamespaces()
                throws RepositoryException {
            return null;
        }

        @Override
        public String getNamespace(String prefix) throws RepositoryException {
            return null;
        }

        @Override
        public void setNamespace(String prefix, String name)
                throws RepositoryException {
        }

        @Override
        public void removeNamespace(String prefix) throws RepositoryException {
        }

        @Override
        public void clearNamespaces() throws RepositoryException {
        }
        
    }

}
